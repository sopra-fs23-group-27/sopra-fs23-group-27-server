package ch.uzh.ifi.hase.soprafs23.entity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.WebSocketService;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.GameStatsDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.GuessDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing.GuessEvalDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing.RoundDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing.TimerDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.uzh.ifi.hase.soprafs23.repository.CountryRepository;
import ch.uzh.ifi.hase.soprafs23.service.CountryHandlerService;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class Game {

    // This class is the Game object as in the UML diagram
    // This object is not connected to the database
    // here, the game logic is implemented

    // add Logger
    private final Logger log = LoggerFactory.getLogger(Game.class);

    private final CountryHandlerService countryHandlerService;
    private final WebSocketService webSocketService;
    private final CountryRepository countryRepository;

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
    private int numSecondsUntilHint;
    private int hintInterval;
    private int maxNumGuesses;
    private int numOptions;

    public Game(CountryHandlerService countryHandlerService,
                WebSocketService webSocketService, CountryRepository countryRepository,
                Lobby lobby) {

        this.countryHandlerService = countryHandlerService;
        this.webSocketService = webSocketService;
        this.countryRepository = countryRepository;
        this.allCountryCodes = this.countryHandlerService.sourceCountryInfo(5);
        this.lobby = lobby;
        this.numSeconds = lobby.getNumSeconds();
        this.numRounds = 4;

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

        // set the round to 0, this is to get the first of the sourced countries
        // after each round, this Integer is incremented by 1
        this.round = 0;

        this.scoreBoard = new ScoreBoard(this.playerNames);

    }

    public void startGame() {
        log.info("Game loop started for lobbyId: " + this.gameId);
        startRound();
    }

    public void endGame() {
        // Inform all players in the lobby that the game has ended
        log.info("Game loop ended for lobbyId: " + this.gameId);
        this.webSocketService.sendToLobby(this.gameId, "/game-end", "{}");
    }

    public void startRound() {
        startTimer(this.numSeconds, this);
        webSocketService.sendToLobby(this.gameId, "/round-start", "{}");
        log.info("Round " + (this.round + 1) + " started for lobbyId: " + this.gameId);

        String currentCountryCode = this.allCountryCodes.get(this.round);
        updateCorrectGuess(currentCountryCode);
        log.info("Correct guess for round " + (this.round + 1) + " is: " + this.correctGuess);
        // init procedure for a new round

        // init hints for new round given country code
        hintHandler = new HintHandler(
                currentCountryCode, lobby, countryRepository, webSocketService
        );
        hintHandler.setHints();

        hintHandler.sendRequiredDetailsViaWebSocket();


        // start the timer

        this.startTime = System.currentTimeMillis();
        // call endRound if the timer runs out

    }

    public void endRound() {
        // Stop the timer
        hintHandler.stopSendingHints();
        this.stopTimer();

        // end procedure for a round
        log.info("Round " + (this.round + 1) + " ended for lobbyId: " + this.gameId);

        // inform players in lobby that game round has ended
        webSocketService.sendToLobby(this.gameId, "/round-end", "{}");

        // compute the time passed since the start of the round in seconds
        Integer passedTime = this.computePassedTime();

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
                this.scoreBoard.setCurrentTimeUntilCorrectGuessPerPlayer(playerName, passedTime); // replace with maximum time
            }
            if (this.scoreBoard.getCurrentNumberOfWrongGuessesPerPlayer(playerName) == null) {
                this.scoreBoard.setCurrentNumberOfWrongGuessesPerPlayer(playerName, 0);
            }
        }

        // update the current and total scores 
        // MUST BE CALLED AFTER FOR LOOP
        this.scoreBoard.updateTotalScores();

        // computes the LeaderBoardScore for each player for the current round and total rounds
        // NOTE: ALL GETTERS FOR THE CURRENT AND TOTAL LEADERBOARD CAN ONLY BE USED FROM NOW ON
        this.scoreBoard.computeLeaderBoardScore();
        log.info("Current LeaderBoard: ");
        log.info(this.scoreBoard.getLeaderBoardTotalScore());

        // send the total LeaderBoard to the lobby
        this.sendStatsToLobby();

        // send round to lobby
        this.sendRoundToLobby();

        // this.scoreBoard.updateTotalScores();
        this.resetCorrectGuess();

        // RESET all the current scores in the ScoreBoard
        this.scoreBoard.resetAllCurrentScores();

        // reset the timer
        this.resetStartTime();

        // prepare the counter for the next round
        this.round++;

        // start the next round
        if (this.round < this.numRounds) {
            // give the players 5sec to read the stats
            try {
                Thread.sleep(5000);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

            // start new Round
            this.startRound();
        }
        // end the game if the last round has been played
        else {
            this.endGame();
        }
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

    public Boolean validateGuess(String playerName, String guess, String wsConnectionId) {
        // prepare the guess
        // remove all whitespaces and make it lowercase
        guess = guess.toLowerCase();
        guess = guess.replaceAll("\\s+", "");
        log.info("cleaned guess is: " + guess);
        log.info("correct guess is: " + this.correctGuess);

        GuessEvalDTO guessEvalDTO = new GuessEvalDTO(guess, false);

        if (guess.equals(this.correctGuess)) {

            // compute the time until the correct guess
            Integer passedTime = this.computePassedTime();

            // If guess is correct, change guessEvalDTO to true
            guessEvalDTO.setIsCorrect(true);

            // If game is in advanced mode: send guessEvalDTO to client
            if (this.lobby instanceof AdvancedLobby) {
                this.webSocketService.sendToPlayerInLobby(wsConnectionId,
                        "/guess-evaluation",
                        this.gameId.toString(),
                        guessEvalDTO);
            }


            // write time of player to scoreBoard
            this.scoreBoard.setCurrentTimeUntilCorrectGuessPerPlayer(playerName, passedTime);
            log.info(this.scoreBoard.getCurrentTimeUntilCorrectGuessPerPlayer(playerName).toString());

            // write correct guess to scoreBoard
            this.scoreBoard.setCurrentCorrectGuessPerPlayer(playerName, true);
            log.info(this.scoreBoard.getCurrentCorrectGuessPerPlayer(playerName).toString());

            // end the round since one player has submitted the correct guess
            this.endRound();


/*            // check if all players have submitted the correct guess and the round is over
            for (String playerNameList : this.playerNames) {
                if (!this.scoreBoard.getCurrentCorrectGuessPerPlayer(playerNameList)) {
                    break;
                }
                else {
                    // if all players have submitted the correct guess, end the round
                    log.info("all players have submitted the correct guess; end of round");
                    this.endRound();
                }
            }*/

            return true;
        }
        else {

            // If game is in advanced mode:
            // - send guessEvalDTO to client
            // - send GuessDTO to client
            if (this.lobby instanceof AdvancedLobby) {

                // send guessEvalDTO to client
                this.webSocketService.sendToPlayerInLobby(wsConnectionId,
                        "/guess-evaluation",
                        this.gameId.toString(),
                        guessEvalDTO);

                // send GuessDTO to client
                GuessDTO guessDTO = new GuessDTO(playerName, guess);
                webSocketService.sendToLobby(this.gameId, "/guesses", guessDTO);
            }


            // increment the number of wrong guesses by 1
            this.scoreBoard.setCurrentNumberOfWrongGuessesPerPlayer(
                    playerName,
                    this.scoreBoard.getCurrentNumberOfWrongGuessesPerPlayer(playerName) + 1);
            return false;
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

/*    public void startTimer(int seconds, Game game) {
        this.timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                game.endRound();
            }
        };
        this.timer.schedule(timerTask, seconds * 1000L);
    }*/

    public void startTimer(int seconds, Game game) {
        this.timer = new Timer();
        final int remainingTime = seconds;

        // send initial remaining time
        TimerDTO timerDTO = new TimerDTO(remainingTime);
        try {
            webSocketService.sendToLobby(game.getGameId(), "/timer", timerDTO);
            log.info("TimerDTO sent to lobby. Remaining time: " + remainingTime + " seconds.");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        TimerTask timerTask = new TimerTask() {
            int currentRemainingTime = remainingTime;

            @Override
            public void run() {
                currentRemainingTime--;
                if (currentRemainingTime == 0) {
                    game.endRound();
                }
                else {
                    TimerDTO timerDTO = new TimerDTO(currentRemainingTime);
                    try {
                        webSocketService.sendToLobby(game.getGameId(), "/timer", timerDTO);
                        log.info("TimerDTO sent to lobby. Remaining time: " + currentRemainingTime + " seconds.");
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        this.timer.scheduleAtFixedRate(timerTask, 1000L, 1000L);
    }


    public void stopTimer() {
        this.timer.cancel();
    }

    public void sendStatsToLobby() {

        // Init Arrays for the mapping into a JSON object
        ArrayList<Integer> TotalGameScores = new ArrayList<Integer>();
        ArrayList<Integer> TotalCorrectGuesses = new ArrayList<Integer>();
        ArrayList<Integer> TotalTimeUntilCorrectGuess = new ArrayList<Integer>();
        ArrayList<Integer> TotalWrongGuesses = new ArrayList<Integer>();

        // Fill the Arrays with the data from the ScoreBoard
        this.playerNames.forEach(playerName -> {
            TotalGameScores.add(this.scoreBoard.getLeaderBoardTotalScorePerPlayer(playerName));
            TotalCorrectGuesses.add(this.scoreBoard.getTotalCorrectGuessesPerPlayer(playerName));
            TotalTimeUntilCorrectGuess.add(this.scoreBoard.getTotalTimeUntilCorrectGuessPerPlayer(playerName));
            TotalWrongGuesses.add(this.scoreBoard.getTotalNumberOfWrongGuessesPerPlayer(playerName));
        });

        // Create a new GameStatsDTO object with the data from the ScoreBoard
        GameStatsDTO gameStatsDTO = new GameStatsDTO(
                this.playerNames,
                TotalGameScores,
                TotalCorrectGuesses,
                TotalTimeUntilCorrectGuess,
                TotalWrongGuesses
        );

        // send the game stats to the players
        webSocketService.sendToLobby(this.gameId, "/score-board", gameStatsDTO);
    }

    public void sendRoundToLobby() {

        // create a DTO for the current round and pass it the current round
        RoundDTO roundDTO = new RoundDTO(this.round);
        
        // send the round to the frontent on endpoint /round 
        this.webSocketService.sendToLobby(this.gameId, "/round", roundDTO);
    }
}
