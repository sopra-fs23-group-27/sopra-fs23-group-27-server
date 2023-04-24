package ch.uzh.ifi.hase.soprafs23.entity;

import java.util.ArrayList;

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
    private final Logger log = LoggerFactory.getLogger(CountryHandlerService.class);

    private final CountryHandlerService countryHandlerService;
    private final CountryRepository countryRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private ScoreBoard scoreBoard;
    private HintHandler hintHandler;

    private ArrayList<String> allCountryCodes;
    private Country currentCountry;
    private String correctGuess;
    private Integer round;
    private Integer gameId;
    private ArrayList<String> playerNames;

    public Game(CountryHandlerService countryHandlerService,
                CountryRepository countryRepository,
                SimpMessagingTemplate messagingTemplate) {

        this.countryHandlerService = countryHandlerService;
        this.countryRepository = countryRepository;
        this.allCountryCodes = this.countryHandlerService.sourceCountryInfo(5);
        this.messagingTemplate = messagingTemplate;

        // TODO: get gameId from game lobby
        // this.gameId = gameLobby.getLobbyId();
        this.gameId = 1;

        // set the round to 0, this is to get the first of the sourced countries
        // after each round, this Integer is incremented by 1
        this.round = 0;

        // TESTING
        ArrayList<String> playerNames = new ArrayList<String>();
        playerNames.add("Player1");
        playerNames.add("Player2");
        playerNames.add("Player3");
        playerNames.add("Player4");
        this.playerNames = playerNames;

        // initialize ScoreBoard (UNCOMMENT THIS LINE AS SOON AS THE LOBBY PROVIDES A
        // LIST OF PLAYER NAMES FOR THE GAME)
        this.scoreBoard = new ScoreBoard(this.playerNames);

        // startRound();
        // log.info(allCountryCodes.toString());
        // log.info(this.correctGuess);
        // log.info("test1");
        // log.info(this.round.toString());

        // log.info(this.scoreBoard.getCurrentCorrectGuessPerPlayer("Player1").toString());

        // endRound();
        // log.info("test2");
        // log.info(this.round.toString());
        // log.info(this.scoreBoard.getCurrentCorrectGuessPerPlayer("Player1").toString());
        // log.info(this.scoreBoard.getTotalCorrectGuessesPerPlayer("Player2").toString());

    }

    public void startRound() {
        String currentCountryCode = this.allCountryCodes.get(this.round);
        log.info("test0");
        log.info(currentCountryCode);
        updateCorrectGuess(currentCountryCode);
        // init procedure for a new round

        // init hints for new round given country code
        hintHandler = new HintHandler(
                currentCountryCode, 3, gameId,
                countryRepository, messagingTemplate
        );
        hintHandler.setHints();
        hintHandler.sendHintViaWebSocket();


        // start the timer
        // call endRound if the timer runs out

    }

    public void endRound() {
        // end procedure for a round

        // stop the timer

        // for each player that has NOT guessed the country correctly,
        // set the current guess to false
        // for each player that has not given a single guess, set the number of wrong
        // guesses to 0
        // REPLACE WITH: for (String playerName : this.Lobby.getPlayers()) {
        for (String playerName : this.playerNames) {
            if (this.scoreBoard.getCurrentCorrectGuessPerPlayer(playerName)== false) {
                // This is a very ugly solution, but it works for now
                // the function getCurrentCorrectGuessPerPlayer cannot return null, as null is 
                // not a valid Boolean value. Therefore, if the player has not yet guessed,
                // even though the player has no entry in the underlying HashMap "currentCorrectGuess",
                // the function returns false making use of getOrDefault(). This is not a problem, as 
                // the player has not yet guessed and therefore the current guess is false anyway. However,
                // it is a very ugly solution because we set the current guess to false after checking if
                // getCurrentCorrectGuessPerPlayer returns false, what effectively means "null"
                this.scoreBoard.setCurrentCorrectGuessPerPlayer(playerName, false);
                this.scoreBoard.setCurrentTimeUntilCorrectGuessPerPlayer(playerName, 100); // replace with maximum time
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

        // this.scoreBoard.updateTotalScores();
        resetCorrectGuess();

        // RESET all the current scores in the ScoreBoard
        this.scoreBoard.resetAllCurrentScores();

        // reset the timer

        // prepare the counter for the next round
        this.round++;
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

    public Boolean validateGuess(String PlayerName, String guess) {
        // prepare the guess
        // remove all whitespaces and make it lowercase
        guess = guess.toLowerCase();
        guess = guess.replaceAll("\\s+", "");

        if (guess.equals(this.correctGuess)) {
            // TODO: Enter here the correct guess time
            // this.scoreBoard.setCurrentTimeUntilCorrectGuessPerPlayer(PlayerName, 100);
            this.scoreBoard.setCurrentCorrectGuessPerPlayer(PlayerName, true);
            return true;
        } else {
            //if guess is wrong, send GuessDTO to client
            messagingTemplate.convertAndSend(String.format("/topic/queue/%d/guesses", gameId), guess);

            // increment the number of wrong guesses by 1
            this.scoreBoard.setCurrentNumberOfWrongGuessesPerPlayer(
                    PlayerName,
                    this.scoreBoard.getCurrentNumberOfWrongGuessesPerPlayer(PlayerName) + 1);
            return false;
        }
    }

    private void resetCorrectGuess() {
        this.correctGuess = null;
        this.currentCountry = null;
    }

}
