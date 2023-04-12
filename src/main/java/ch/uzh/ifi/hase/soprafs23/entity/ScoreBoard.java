package ch.uzh.ifi.hase.soprafs23.entity;

import java.util.ArrayList;
import java.util.HashMap;

public class ScoreBoard {

    private ArrayList<String> playerNames;

    private HashMap<String, Boolean> currentCorrectGuess;
    private HashMap<String, Integer> totalCorrectGuesses;
    private HashMap<String, Integer> totalCorrectGuessesInARow;

    private HashMap<String, Integer> currentTimeUntilCorrectGuess;
    private HashMap<String, Integer> totalTimeUntilCorrectGuess;

    private HashMap<String, Integer> currentNumberOfWrongGuesses;
    private HashMap<String, Integer> totalNumberOfWrongGuesses;

    public ScoreBoard(ArrayList<String> playerNames) {

        this.playerNames = playerNames;

        this.currentCorrectGuess = new HashMap<String, Boolean>();
        this.totalCorrectGuesses = new HashMap<String, Integer>();
        this.totalCorrectGuessesInARow = new HashMap<String, Integer>();

        this.currentTimeUntilCorrectGuess = new HashMap<String, Integer>();
        this.totalTimeUntilCorrectGuess = new HashMap<String, Integer>();

        this.currentNumberOfWrongGuesses = new HashMap<String, Integer>();
        this.totalNumberOfWrongGuesses = new HashMap<String, Integer>();

        initTotalScores();
    }

    // -------------------------- SETTERS ------------------------- //

    // IMPORTANT: these setters must be used for ALL the players of a game
    // If there are 4 players in the game, all 3 setters must be called 4 times

    public void setCurrentCorrectGuessPerPlayer(String playerName, Boolean correctGuess) {
        this.currentCorrectGuess.put(playerName, correctGuess);
    }

    public void setCurrentTimeUntilCorrectGuessPerPlayer(String playerName, Integer timeUntilCorrectGuess) {
        this.currentTimeUntilCorrectGuess.put(playerName, timeUntilCorrectGuess);
    }

    public void setCurrentNumberOfWrongGuessesPerPlayer(String playerName, Integer numberOfWrongGuesses) {
        this.currentNumberOfWrongGuesses.put(playerName, numberOfWrongGuesses);
    }

    // -------------------------- GETTERS -------------------------- //

    // these getters are used to find the players that are not registered in the
    // HashMaps

    public Boolean getCurrentCorrectGuessPerPlayer(String playerName) {
        return this.currentCorrectGuess.getOrDefault(playerName, null);
    }

    public Integer getCurrentTimeUntilCorrectGuessPerPlayer(String playerName) {
        return this.currentTimeUntilCorrectGuess.getOrDefault(playerName, null);
    }

    public Integer getCurrentNumberOfWrongGuessesPerPlayer(String playerName) {
        return this.currentNumberOfWrongGuesses.getOrDefault(playerName, null);
    }

    public Integer getTotalCorrectGuessesPerPlayer(String playerName) {
        return this.totalCorrectGuesses.getOrDefault(playerName, null);
    }

    public Integer getTotalCorrectGuessesInARowPerPlayer(String playerName) {
        return this.totalCorrectGuessesInARow.getOrDefault(playerName, null);
    }

    public Integer getTotalTimeUntilCorrectGuessPerPlayer(String playerName) {
        return this.totalTimeUntilCorrectGuess.getOrDefault(playerName, null);
    }

    public Integer getTotalNumberOfWrongGuessesPerPlayer(String playerName) {
        return this.totalNumberOfWrongGuesses.getOrDefault(playerName, null);
    }

    // ---------------------------------------------------------------------------------

    public void resetAllTotalScores() {
        // This function resets all HashMaps that store the total scores.
        // This can be used if the game is restarted with the same settings
        // That is, if the admin chooses to play the same game again

        this.totalCorrectGuesses = new HashMap<String, Integer>();
        this.totalCorrectGuessesInARow = new HashMap<String, Integer>();
        this.totalTimeUntilCorrectGuess = new HashMap<String, Integer>();
        this.totalNumberOfWrongGuesses = new HashMap<String, Integer>();
    }

    public void updateTotalScores() {

        // UPDATE: totalCorrectGuesses
        for (String playerName : this.currentCorrectGuess.keySet()) {
            if (this.currentCorrectGuess.get(playerName)) {
                // if the boolean for the player is true, the player has made a correct guess
                // therefore increment totalCorrectGuesses and currentCorrectGuessesInARow by 1
                this.totalCorrectGuesses.put(playerName, this.totalCorrectGuesses.get(playerName) + 1);
                this.totalCorrectGuessesInARow.put(playerName, this.totalCorrectGuessesInARow.get(playerName) + 1);
            } else {
                // if the boolean for the player is false, the player has made a wrong guess
                // therefore reset currentCorrectGuessesInARow to 0
                this.totalCorrectGuessesInARow.put(playerName, 0);
            }
        }

        // UPDATE:totalTimeUntilCorrectGuess
        for (String playerName : this.currentTimeUntilCorrectGuess.keySet()) {
            this.totalTimeUntilCorrectGuess.put(playerName, this.totalTimeUntilCorrectGuess.get(playerName)
                    + this.currentTimeUntilCorrectGuess.get(playerName));
        }

        // UPDATE: totalNumberOfWrongGuesses
        for (String playerName : this.currentNumberOfWrongGuesses.keySet()) {
            this.totalNumberOfWrongGuesses.put(playerName,
                    this.totalNumberOfWrongGuesses.get(playerName) + this.currentNumberOfWrongGuesses.get(playerName));
        }
    }

    public void initTotalScores() {
        // This function initializes all HashMaps that store the total scores.
        // This can be used if the game is restarted with the same settings
        // That is, if the admin chooses to play the same game again

        for (String playerName : this.playerNames) {
            this.totalCorrectGuesses.put(playerName, 0);
            this.totalCorrectGuessesInARow.put(playerName, 0);
            this.totalTimeUntilCorrectGuess.put(playerName, 0);
            this.totalNumberOfWrongGuesses.put(playerName, 0);
        }

    }

    private void resetAllCurrentScores() {
        // This function resets all HashMaps that store the current scores.
        // To be used after each round.

        this.currentCorrectGuess = new HashMap<String, Boolean>();
        this.currentTimeUntilCorrectGuess = new HashMap<String, Integer>();
        this.currentNumberOfWrongGuesses = new HashMap<String, Integer>();
    }

}
