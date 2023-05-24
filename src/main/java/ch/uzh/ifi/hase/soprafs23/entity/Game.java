package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.repository.CountryRepository;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.service.WebSocketService;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing.GameStatsDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.incoming.GuessDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing.CorrectGuessDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing.GuessEvalDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing.RoundDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing.TimerDTO;
import info.debatty.java.stringsimilarity.JaroWinkler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public class Game {
    // This class is the Game object as in the UML diagram
    // This object is not connected to the database
    // here, the game logic is implemented

    // add Logger
    private final Logger log = LoggerFactory.getLogger(Game.class);

    private final CountryHandler countryHandler;
    private final WebSocketService webSocketService;
    private final CountryRepository countryRepository;
    private final PlayerRepository playerRepository;
    private final LobbyRepository lobbyRepository;
    private final Integer playAgainTimeWindow;

    private ScoreBoard scoreBoard;
    private HintHandler hintHandler;

    private ArrayList<String> allCountryCodes;
    private Country currentCountry;
    private String correctGuess;
    private Integer round;
    private Integer numRounds;
    private Long gameId;
    private String gameMode;
    private ArrayList<String> playerNames;
    private Lobby lobby;

    private Long startTime;
    private int numSeconds;
    private Timer timer;
    private boolean isAcceptingGuesses;
    private Map<String, Integer> playerHasGuessed = new HashMap<String, Integer>();
    private Timer playAgainTimer;
    private int numSecondsUntilHint;
    private int hintInterval;
    private int maxNumGuesses;
    private int numOptions;
    private ArrayList<String> continent;

    public Game(CountryHandler countryHandler,
                WebSocketService webSocketService,
                CountryRepository countryRepository,
                PlayerRepository playerRepository,
                LobbyRepository lobbyRepository,
                Lobby lobby) {

        this.countryHandler = countryHandler;
        this.webSocketService = webSocketService;
        this.countryRepository = countryRepository;
        this.playerRepository = playerRepository;
        this.lobbyRepository = lobbyRepository;
        this.continent = lobby.getContinent();
        this.numRounds = lobby.getNumRounds();
        this.lobby = lobby;
        this.numSeconds = lobby.getNumSeconds();

        // in case an exception is thorwn, try to call method again
        try {
            this.allCountryCodes = this.countryHandler.sourceCountryInfo(this.numRounds, this.continent);
        }
        catch (IllegalArgumentException e) {
            this.numRounds = 5;
            this.allCountryCodes = this.countryHandler.sourceCountryInfo(this.numRounds, this.continent);
        }
        catch (Exception e) {
            this.allCountryCodes = this.countryHandler.sourceCountryInfo(this.numRounds, this.continent);
        }

        // set variables depending on lobby type
        if (lobby instanceof BasicLobby) {
            this.numOptions = ((BasicLobby) lobby).getNumOptions();
        }
        else if (lobby instanceof AdvancedLobby) {
            this.numSecondsUntilHint = ((AdvancedLobby) lobby).getNumSecondsUntilHint();
            this.hintInterval = ((AdvancedLobby) lobby).getHintInterval();
            this.maxNumGuesses = ((AdvancedLobby) lobby).getMaxNumGuesses();
        }


        this.gameId = lobby.getLobbyId();
        this.gameMode = lobby.getMode();

        List playerNames = lobby.getJoinedPlayerNames();

        // convert to ArrayList<String>
        ArrayList<String> playerNamesArrayList = new ArrayList<String>();
        for (Object playerName : playerNames) {
            playerNamesArrayList.add((String) playerName);
        }
        this.playerNames = playerNamesArrayList;
        log.info("New game created with playerNames: " + playerNamesArrayList);
        log.info("New game created with following game settings:");
        log.info("- numSeconds: " + this.numSeconds);
        log.info("- numRounds: " + this.numRounds);
        log.info("- numOptions: " + this.numOptions);
        log.info("- numSecondsUntilHint: " + this.numSecondsUntilHint);
        log.info("- hintInterval: " + this.hintInterval);
        log.info("- maxNumGuesses: " + this.maxNumGuesses);
        log.info("- continent: " + this.continent.toString());

        // set the round to 0, this is to get the first of the sourced countries
        // after each round, this Integer is incremented by 1
        this.round = 0;

        this.scoreBoard = new ScoreBoard(this.playerNames);

        playAgainTimeWindow = 20;
    }

    public void removePlayer(String playerName) {
        this.playerNames.remove(playerName);
        this.scoreBoard.removePlayer(playerName);
    }

    public void startGame() {
        log.info("Game loop started for lobbyId: " + this.gameId);
        startRound();
    }

    public void endGame() {
        // Inform all players in the lobby that the game has ended
        log.info("Game loop ended for lobbyId: " + this.gameId);
        this.webSocketService.sendToLobby(this.gameId, "/game-end", "{}");

        // set lobby to joinable again and clear players
        this.lobby.setJoinable(true);
        this.lobby.setCollectingPlayAgains(true);
        this.lobby.setCurrentGameId(null);
        this.lobby.clearPlayers();
        this.lobbyRepository.save(this.lobby);
        this.lobbyRepository.flush();

        // initiate play again procedure
        startPlayAgainTimer(playAgainTimeWindow, this);
        for (String playerName : this.playerNames) {
            this.webSocketService.initPlayAgainProcedureByPlayerName(playerName, playAgainTimeWindow.longValue() * 1000);
        }
        log.info("Play again window opened for lobbyId: " + this.gameId);
        webSocketService.sendToLobby(this.gameId, "/play-again-opened", "{}");

        // clear game
        GameRepository.removeGame(this.gameId);

        // cleanups after play again timer is over
        this.webSocketService.wait(playAgainTimeWindow * 1000);

        Lobby playAgainLobby = this.lobbyRepository.findByLobbyId(this.gameId);
        if (playAgainLobby.getJoinedPlayerNames().size() == 0) {
            log.info("No players left in lobby after re-collecting time. Lobby will be deleted.");
            this.lobbyRepository.delete(playAgainLobby);
            this.lobbyRepository.flush();
        }
        else {
            log.info("The lobby contains some players after the re-collecting time is over.");
            playAgainLobby.setCollectingPlayAgains(false);
            this.lobbyRepository.save(playAgainLobby);
            this.lobbyRepository.flush();
        }

    }

    public void clearGame() {
        this.stopTimer();

    }

    public void startRound() {
        // RESET all the current scores in the ScoreBoard
        this.scoreBoard.resetAllCurrentScores();

        // guesses are accepted again for new round
        this.isAcceptingGuesses = true;

        if (!(this.round < this.numRounds)) {
            log.info("No new round can be started since numRounds is reached." +
                    " Initiate Game loop end for lobbyId: " + this.gameId);
            this.endGame();
            return;
        }
        // set playerHasGuessed to false for all players
        for (String playerName : this.playerNames) {
            this.playerHasGuessed.put(playerName, 0);
        }
        startTimer(this.numSeconds, this);
        webSocketService.sendToLobby(this.gameId, "/round-start", "{}");
        log.info("Round " + (this.round + 1) + " started for lobbyId: " + this.gameId);

        String currentCountryCode = this.allCountryCodes.get(this.round);
        updateCorrectGuess(currentCountryCode);
        log.info("Correct guess for round " + (this.round + 1) + " is: " + this.correctGuess);

        // init hints for new round given country code
        if (hintHandler == null) {
            hintHandler = new HintHandler(
                    currentCountryCode, lobby, countryRepository, webSocketService
            );
        }
        hintHandler.setHints();

        hintHandler.sendRequiredDetailsViaWebSocket();

        // start the timer
        this.startTime = System.currentTimeMillis();
    }

    public void endRound() {
        // no more guesses are accepted for this round
        this.isAcceptingGuesses = false;

        // Stop the timer
        if (lobby instanceof AdvancedLobby) {
            hintHandler.stopSendingHints();
        }
        this.stopTimer();

        // end procedure for a round
        log.info("Round " + (this.round + 1) + " ended for lobbyId: " + this.gameId);


        // number of second for all players that have not correctly guessed the country
        // if the first player guessed the country correctly, the round is over. 
        // therefore all players, who have not guessed the time correctly would get
        // the same time as the player who guessed the country correctly
        // to avoid this. Each player that has not guessed correctly gets the full time
        Integer passedTime = this.lobby.getNumSeconds();

        // for each player that has NOT guessed the country correctly,
        // set the current guess to false
        // for each player that has not given a single guess, set the number of wrong
        // guesses to 0
        // REPLACE WITH: for (String playerName : this.Lobby.getPlayers()) {
        for (String playerName : this.playerNames) {
            if (!this.scoreBoard.getCurrentCorrectGuessPerPlayer(playerName)) {
                // This is a very ugly solution, but it works for now
                // the function getCurrentCorrectGuessPerPlayer cannot return null, as null is 
                // not a valid Boolean value. Therefore, if the player has not yet guessed,
                // even though the player has no entry in the underlying HashMap "currentCorrectGuess",
                // the function returns false making use of getOrDefault(). This is not a problem, as 
                // the player has not yet guessed and therefore the current guess is false anyway. However,
                // it is a very ugly solution because we set the current guess to false after checking if
                // getCurrentCorrectGuessPerPlayer returns false, what effectively means "null"
                this.scoreBoard.setCurrentCorrectGuessPerPlayer(playerName, false);

                if (this.scoreBoard.getCurrentTimeUntilCorrectGuessPerPlayer(playerName) == 0) {
                    // if a player has not entered a guess before, we must set the time until correct guess
                    // to the full time. Else, if the time until the correct wrong guess is being taken.
                    this.scoreBoard.setCurrentTimeUntilCorrectGuessPerPlayer(playerName, passedTime);
                }
            }
            if (this.scoreBoard.getCurrentNumberOfWrongGuessesPerPlayer(playerName) == null) {
                this.scoreBoard.setCurrentNumberOfWrongGuessesPerPlayer(playerName, 0);
            }
        }

        // write all stats for each player to DB.
        this.addStatsToDB();

        // update the current and total scores 
        // MUST BE CALLED AFTER FOR LOOP
        this.scoreBoard.updateTotalScores();

        // computes the LeaderBoardScore for each player for the current round and total rounds
        // NOTE: ALL GETTERS FOR THE CURRENT AND TOTAL LEADERBOARD CAN ONLY BE USED FROM NOW ON
        this.scoreBoard.computeLeaderBoardScore();
        log.info("Current LeaderBoard: ");
        log.info(this.scoreBoard.getLeaderBoardTotalScore());

        // send the correct Guess of the previous round to Lobby
        this.sendCorrectGuessToLobby();

        // inform players in lobby that game round has ended
        webSocketService.sendToLobby(this.gameId, "/round-end", "{}");

        // sleep for 1 second to make sure that the LeaderBoard is sent after the round-end message
        this.webSocketService.wait(1000);

        // send the total LeaderBoard to the lobby
        this.sendStatsToLobby();

        // send round to lobby
        this.sendRoundToLobby();

        // this.scoreBoard.updateTotalScores();
        this.resetCorrectGuess();


        // reset the timer
        this.resetStartTime();

        // prepare the counter for the next round
        this.round++;

        // end the game if the last round has been played
        if (this.round.equals(this.numRounds)) {
            this.endGame();
        }
        // set hintHandler again to null
        this.hintHandler = null;
    }

    public void updateCorrectGuess(String countryCode) {
        // this function is called after each round
        // it updates the correct guess for the next round
        // INPUT: countryCode (String) e.g. "CH"
        // OUTPUT: void

        // get the country from the database and set it to object variable
        // e.g. transform "CH" to "Switzerland"
        Country country = countryRepository.findByCountryCode(countryCode);
        this.currentCountry = country;

        // get the name corresponding to the country code
        this.correctGuess = country.getName();

        // remove all whitespaces and make it lowercase
        this.correctGuess = this.correctGuess.toLowerCase();
        this.correctGuess = this.correctGuess.replaceAll("\\s+", "");

    }

    public synchronized void validateGuess(String playerName, String guess, String wsConnectionId) {
        // check if guesses are accepted
        if (!this.isAcceptingGuesses) {
            log.info("Guess from player " + playerName + " not accepted, because no round is running at the moment");
            return;
        }

        // log that the player has submitted a guess in the round
        this.playerHasGuessed.put(playerName, 1);

        // clean the guess: remove all whitespaces and make it lowercase
        String cleanedGuess = guess.toLowerCase();
        cleanedGuess = cleanedGuess.replaceAll("\\s+", "");

        if (lobby instanceof BasicLobby) {
            validateGuessBasicMode(playerName, guess, wsConnectionId, cleanedGuess);
        }
        else {
            validateGuessAdvancedMode(playerName, guess, wsConnectionId, cleanedGuess);
        }
    }

    private void validateGuessAdvancedMode(String playerName, String guess, String wsConnectionId, String cleanedGuess) {
        // use JaroWinkler algorithm to compute the similarity between the guess and the correct guess
        JaroWinkler jw = new JaroWinkler();
        Double similarity = jw.similarity(cleanedGuess, this.correctGuess);
        log.info("guess from player " + playerName + " is: " + guess);
        log.info("correct guess is: " + this.correctGuess);
        log.info("similarity is: " + similarity);

        GuessEvalDTO guessEvalDTO = new GuessEvalDTO(guess, false);

        // if the similarity is greater than 0.93, the guess is correct
        // NOTE: this is a very high threshold, but it is necessary to avoid false positives
        if (similarity > 0.93) {

            if (this.scoreBoard.getCurrentTimeUntilCorrectGuessPerPlayer(playerName) == 0) {
                // compute the time until the correct guess
                Integer passedTime = this.computePassedTime();

                // write time of player to scoreBoard
                this.scoreBoard.setCurrentTimeUntilCorrectGuessPerPlayer(playerName, passedTime);
            }

            // If guess is correct, change guessEvalDTO to true
            guessEvalDTO.setIsCorrect(true);

            // If game is in advanced mode: send guessEvalDTO to client
            this.webSocketService.sendToPlayerInLobby(wsConnectionId,
                    "/guess-evaluation",
                    this.gameId.toString(),
                    guessEvalDTO);

            log.info(this.scoreBoard.getCurrentTimeUntilCorrectGuessPerPlayer(playerName).toString());

            // write correct guess to scoreBoard
            this.scoreBoard.setCurrentCorrectGuessPerPlayer(playerName, true);
            log.info(this.scoreBoard.getCurrentCorrectGuessPerPlayer(playerName).toString());

            // end the round since one player has submitted the correct guess
            this.endRound();
        }
        else {
            // send guessEvalDTO to client
            webSocketService.sendToPlayerInLobby(wsConnectionId,
                    "/guess-evaluation",
                    this.gameId.toString(),
                    guessEvalDTO);

            // send GuessDTO to client
            GuessDTO guessDTO = new GuessDTO(playerName, guess);
            webSocketService.sendToLobby(this.gameId, "/guesses", guessDTO);


            // increment the number of wrong guesses by 1
            this.scoreBoard.setCurrentNumberOfWrongGuessesPerPlayer(
                    playerName,
                    this.scoreBoard.getCurrentNumberOfWrongGuessesPerPlayer(playerName) + 1);

            if (this.scoreBoard.getCurrentTimeUntilCorrectGuessPerPlayer(playerName) == 0) {
                Integer passedTime = this.computePassedTime();
                this.scoreBoard.setCurrentTimeUntilCorrectGuessPerPlayer(playerName, passedTime);
            }
        }
    }

    private void validateGuessBasicMode(String playerName, String guess, String wsConnectionId, String cleanedGuess) {
        // check if player has already submitted a guess
        if (scoreBoard.getCurrentNumberOfWrongGuessesPerPlayer(playerName) > 0 || scoreBoard.getCurrentCorrectGuessPerPlayer(playerName)) {
            // Player has already made a guess, send an error message back to the player
            String errorMessage = String.format("You have already submitted a guess. Your guess was %s", guess);
            webSocketService.sendToPlayerInLobby(wsConnectionId, "/errors", this.gameId.toString(), errorMessage);
        }
        // if this is the first guess and the guess is correct, write the time until the correct guess to the scoreBoard
        else if (cleanedGuess.equals(this.correctGuess)) {


            // write time of player to scoreBoard

            if (this.scoreBoard.getCurrentTimeUntilCorrectGuessPerPlayer(playerName) == 0) {
                // compute the time until the correct guess
                Integer passedTime = this.computePassedTime();

                // write time of player to scoreBoard
                this.scoreBoard.setCurrentTimeUntilCorrectGuessPerPlayer(playerName, passedTime);
            }

            log.info("Correct guess from player " + playerName + "received. Passed time: " + this.scoreBoard.getCurrentTimeUntilCorrectGuessPerPlayer(playerName).toString());

            // write correct guess to scoreBoard
            this.scoreBoard.setCurrentCorrectGuessPerPlayer(playerName, true);
            //log.info(this.scoreBoard.getCurrentCorrectGuessPerPlayer(playerName).toString());
        }
        else {
            // increment the number of wrong guesses by 1
            this.scoreBoard.setCurrentNumberOfWrongGuessesPerPlayer(
                    playerName,
                    this.scoreBoard.getCurrentNumberOfWrongGuessesPerPlayer(playerName) + 1);


            if (this.scoreBoard.getCurrentTimeUntilCorrectGuessPerPlayer(playerName) == 0) {
                Integer passedTime = this.computePassedTime();
                this.scoreBoard.setCurrentTimeUntilCorrectGuessPerPlayer(playerName, passedTime);
            }
        }

        //if all players have given a guess --> end round
        Boolean allPlayersHaveGuessed = false;
        for (String player : this.playerNames) {
            if (scoreBoard.getCurrentNumberOfWrongGuessesPerPlayer(player) > 0 || scoreBoard.getCurrentCorrectGuessPerPlayer(player)) {
                allPlayersHaveGuessed = true;
            }
            else {
                allPlayersHaveGuessed = false;
                break;
            }
        }
        if (allPlayersHaveGuessed) {
            this.endRound();
        }
    }

    private void resetCorrectGuess() {
        this.correctGuess = null;
        this.currentCountry = null;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    private Integer computePassedTime() {
        // stop the timer
        Long endTime = System.currentTimeMillis();

        // compute the time until the correct guess for each player
        Long passedTimeLong = endTime - this.startTime;

        // convert long to integer
        Integer passedTime = passedTimeLong.intValue();

        passedTime = passedTime / 1000;

        return passedTime;
    }

    private void resetStartTime() {
        this.startTime = null;
    }

    private void startTimer(int seconds, Game game) {
        this.timer = new Timer();
        final int remainingTime = seconds;

        // send initial remaining time
        TimerDTO timerDTO = new TimerDTO(remainingTime);
        try {
            webSocketService.sendToLobby(game.getGameId(), "/timer", timerDTO);
            //log.info("TimerDTO sent to lobby. Remaining time: " + remainingTime + " seconds.");
        }
        catch (Exception e) {
            log.info("Exception thrown while sending timerDTO to client: ", e);
        }

        TimerTask timerTask = new TimerTask() {
            int currentRemainingTime = remainingTime;

            @Override
            public void run() {
                currentRemainingTime--;
                if (currentRemainingTime < 0) {
                    game.endRound();
                }
                else {
                    TimerDTO timerDTO = new TimerDTO(currentRemainingTime);
                    try {
                        webSocketService.sendToLobby(game.getGameId(), "/timer", timerDTO);
                        //log.info("TimerDTO sent to lobby. Remaining time: " + currentRemainingTime + " seconds.");
                    }
                    catch (Exception e) {
                        log.info("Exception thrown while sending timerDTO to client: ", e);
                    }
                }
            }
        };
        this.timer.scheduleAtFixedRate(timerTask, 1000L, 1000L);
    }

    private void startPlayAgainTimer(int seconds, Game game) {
        // fixe hier
        this.playAgainTimer = new Timer();
        final int remainingTime = seconds;

        // send initial remaining time
        TimerDTO timerDTO = new TimerDTO(remainingTime);
        try {
            webSocketService.sendToLobby(game.getGameId(), "/timer-play-again", timerDTO);
        }
        catch (Exception e) {
            log.info("Exception thrown while sending timerDTO to client: ", e);
        }

        TimerTask timerTask = new TimerTask() {
            int currentRemainingTime = remainingTime;

            @Override
            public void run() {
                currentRemainingTime--;
                if (currentRemainingTime < 0) { // changed from <= to < testing
                    stopPlayAgainTimer();
                    webSocketService.sendToLobby(game.getGameId(), "/play-again-closed", "{}");
                    log.info("Play again window closed for lobbyId: " + game.getGameId());
                }
                else {
                    TimerDTO timerDTO = new TimerDTO(currentRemainingTime);
                    try {
                        webSocketService.sendToLobby(game.getGameId(), "/timer-play-again", timerDTO);
                    }
                    catch (Exception e) {
                        log.info("Exception thrown while sending timerDTO to client: ", e);
                    }
                }
            }
        };
        this.playAgainTimer.scheduleAtFixedRate(timerTask, 1000L, 1000L);
    }

    private void stopTimer() {
        if (this.timer != null) {
            this.timer.cancel();
        }
    }

    private void stopPlayAgainTimer() {
        if (this.playAgainTimer != null) {
            this.playAgainTimer.cancel();
        }
    }

    private void sendStatsToLobby() {

        // Init Arrays for the mapping into a JSON object
        ArrayList<Integer> playerHasGuessed = new ArrayList<Integer>();
        ArrayList<Integer> TotalGameScores = new ArrayList<Integer>();
        ArrayList<Integer> TotalCorrectGuesses = new ArrayList<Integer>();
        ArrayList<Integer> CurrentTimeUntilCorrectGuess = new ArrayList<Integer>();
        ArrayList<Integer> TotalWrongGuesses = new ArrayList<Integer>();
        ArrayList<Integer> TotalCorrectGuessesInARow = new ArrayList<Integer>();

        // Fill the Arrays with the data from the ScoreBoard
        this.playerNames.forEach(playerName -> {
            playerHasGuessed.add(this.playerHasGuessed.get(playerName));
            TotalGameScores.add(this.scoreBoard.getLeaderBoardTotalScorePerPlayer(playerName));
            TotalCorrectGuesses.add(this.scoreBoard.getTotalCorrectGuessesPerPlayer(playerName));
            CurrentTimeUntilCorrectGuess.add(this.scoreBoard.getCurrentTimeUntilCorrectGuessPerPlayer(playerName));
            TotalWrongGuesses.add(this.scoreBoard.getTotalNumberOfWrongGuessesPerPlayer(playerName));
            TotalCorrectGuessesInARow.add(this.scoreBoard.getTotalCorrectGuessesInARowPerPlayer(playerName));
        });

        // Create a new GameStatsDTO object with the data from the ScoreBoard
        GameStatsDTO gameStatsDTO = new GameStatsDTO(
                this.playerNames,
                playerHasGuessed,
                TotalGameScores,
                TotalCorrectGuesses,
                CurrentTimeUntilCorrectGuess,
                TotalWrongGuesses,
                TotalCorrectGuessesInARow
        );

        // send the game stats to the players
        log.info("Sending game stats to lobby:");
        log.info("- Player names: " + gameStatsDTO.getPlayerNames());
        log.info("- Player has guessed: " + gameStatsDTO.getPlayerHasGuessed());
        log.info("- Total game scores: " + gameStatsDTO.getTotalGameScores());
        log.info("- Total correct guesses: " + gameStatsDTO.getTotalCorrectGuesses());
        log.info("- Total time until correct guess: " + gameStatsDTO.getTotalTimeUntilCorrectGuess());
        log.info("- Total wrong guesses: " + gameStatsDTO.getTotalWrongGuesses());

        List<Player> lobby = this.playerRepository.findByLobbyId(this.gameId);
        for (Player player : lobby) {
            gameStatsDTO.setIsCreator(player.isCreator());
            webSocketService.sendToPlayerInLobby(player.getWsConnectionId(), "/score-board", this.gameId.toString(), gameStatsDTO);
        }

    }

    public void resendStatsToLobby() {
        this.sendStatsToLobby();
    }

    private void sendRoundToLobby() {

        // create a DTO for the current round and pass it the current round
        RoundDTO roundDTO = new RoundDTO(this.round);


        // send the round to the frontend on endpoint /round
        log.info("Sending round info to lobby: " + roundDTO.getRound());
        this.webSocketService.sendToLobby(this.gameId, "/round", roundDTO);
    }

    private void sendCorrectGuessToLobby() {

        // create a DTO for the current round and pass it the current round
        CorrectGuessDTO correctGuessDTO = new CorrectGuessDTO(this.currentCountry.getName());

        log.info("Sending correct guess to lobby: " + this.currentCountry.getName());

        this.webSocketService.sendToLobby(this.gameId, "/correct-country", correctGuessDTO);

        webSocketService.wait(5000);
    }

    private void addStatsToDB() {

        // load all the players to a list
        List<Player> lobby = this.playerRepository.findByLobbyId(this.gameId);

        for (Player player : lobby) {

            // update the total correct guesses. This is done after each round
            try {
                // the player.getTotalCorrectGuesses() + 1 might lead to a
                // NullPointerException, if the player has not yet played a round
                // as then the value is by default null
                // this is why we use a try-catch block

                if (this.scoreBoard.getCurrentCorrectGuessPerPlayer(player.getPlayerName())) {
                    // if the player has guessed correctly in the last round,
                    // we increment the player's total correct guesses by 1
                    player.setTotalCorrectGuesses(
                            player.getTotalCorrectGuesses() + 1
                    );
                }
                else {
                    // if the player has not guessed correctly in the last round,
                    // we do not increment the player's total correct guesses
                    player.setTotalCorrectGuesses(
                            player.getTotalCorrectGuesses()
                    );
                }
            }
            catch (NullPointerException e) {
                // if the player has not yet played a round, the player.getTotalCorrectGuesses()
                // is by default null. Therefore, we set the player's total correct guesses

                if (this.scoreBoard.getCurrentCorrectGuessPerPlayer(player.getPlayerName())) {
                    player.setTotalCorrectGuesses(1);
                }
                else {
                    player.setTotalCorrectGuesses(0);
                }
            }

            // update the total wrong guesses. This is done after each round
            try {
                player.setNumWrongGuesses(
                        player.getNumWrongGuesses() +
                                this.scoreBoard.getCurrentNumberOfWrongGuessesPerPlayer(player.getPlayerName())
                );
            }
            catch (NullPointerException e) {
                player.setNumWrongGuesses(
                        this.scoreBoard.getCurrentNumberOfWrongGuessesPerPlayer(player.getPlayerName())
                );
            }


            // update the total time until correct guess. This is done after each round
            try {
                player.setTimeUntilCorrectGuess(
                        player.getTimeUntilCorrectGuess() +
                                this.scoreBoard.getCurrentTimeUntilCorrectGuessPerPlayer(player.getPlayerName())
                );
            }
            catch (NullPointerException e) {
                player.setTimeUntilCorrectGuess(
                        this.scoreBoard.getCurrentTimeUntilCorrectGuessPerPlayer(player.getPlayerName())
                );
            }

            // update the number of Games played. This is done only once after the game!
            try {
                player.setnRoundsPlayed(
                        player.getnRoundsPlayed() + 1
                );
            }
            catch (NullPointerException e) {
                player.setnRoundsPlayed(1);
            }

            playerRepository.saveAndFlush(player);

        }
    }
}
