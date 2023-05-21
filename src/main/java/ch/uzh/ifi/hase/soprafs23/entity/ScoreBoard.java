package ch.uzh.ifi.hase.soprafs23.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

import javax.print.attribute.standard.MediaSize.NA;

public class ScoreBoard {

    private ArrayList<String> playerNames;

    private final Logger log = LoggerFactory.getLogger(ScoreBoard.class);

    private HashMap<String, Boolean> currentCorrectGuess;
    private HashMap<String, Integer> totalCorrectGuesses;
    private HashMap<String, Integer> totalCorrectGuessesInARow;

    private HashMap<String, Integer> currentTimeUntilCorrectGuess;
    private HashMap<String, Integer> totalTimeUntilCorrectGuess;

    private HashMap<String, Integer> currentNumberOfWrongGuesses;
    private HashMap<String, Integer> totalNumberOfWrongGuesses;

    private HashMap<String, Integer> currentTotalScore;
    private HashMap<String, Integer> leaderBoardTotalScore;

    public ScoreBoard(ArrayList<String> playerNames) {

        this.playerNames = playerNames;

        this.currentCorrectGuess = new HashMap<String, Boolean>();
        this.totalCorrectGuesses = new HashMap<String, Integer>();
        this.totalCorrectGuessesInARow = new HashMap<String, Integer>();

        this.currentTimeUntilCorrectGuess = new HashMap<String, Integer>();
        this.totalTimeUntilCorrectGuess = new HashMap<String, Integer>();

        this.currentNumberOfWrongGuesses = new HashMap<String, Integer>();
        this.totalNumberOfWrongGuesses = new HashMap<String, Integer>();

        this.leaderBoardTotalScore = new HashMap<String, Integer>();

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
        // RETURNS: true if the player has guessed correctly
        // RETURNS: false if the player has guessed incorrectly
        // RETURNS: false if the player has not guessed yet AND IS THEREFORE NOT IN THE 
        // currentCorrectGuess HashMap
        return this.currentCorrectGuess.getOrDefault(playerName, false);
    }

    public Integer getCurrentTimeUntilCorrectGuessPerPlayer(String playerName) {
        return this.currentTimeUntilCorrectGuess.getOrDefault(playerName, 0);
    }

    public Integer getCurrentNumberOfWrongGuessesPerPlayer(String playerName) {
        return this.currentNumberOfWrongGuesses.getOrDefault(playerName, 0);
    }

    public Integer getCurrentScorePerPlayer(String playerName) {
        return this.currentTotalScore.getOrDefault(playerName, 0);
    }

    public Integer getTotalCorrectGuessesPerPlayer(String playerName) {
        return this.totalCorrectGuesses.getOrDefault(playerName, 0);
    }

    public Integer getTotalCorrectGuessesInARowPerPlayer(String playerName) {
        return this.totalCorrectGuessesInARow.getOrDefault(playerName, 0);
    }

    public Integer getTotalTimeUntilCorrectGuessPerPlayer(String playerName) {
        return this.totalTimeUntilCorrectGuess.getOrDefault(playerName, 0);
    }

    public Integer getTotalNumberOfWrongGuessesPerPlayer(String playerName) {
        return this.totalNumberOfWrongGuesses.getOrDefault(playerName, 0);
    }

    public Integer getLeaderBoardTotalScorePerPlayer(String playerName) {
        return this.leaderBoardTotalScore.getOrDefault(playerName, 0);
    }

    public String getLeaderBoardTotalScore() {
        return this.leaderBoardTotalScore.toString();
    }

    // ---------------------------------------------------------------------------------

    public void removePlayer(String playerName) {
        this.playerNames.remove(playerName);

        this.currentCorrectGuess.remove(playerName);
        this.totalCorrectGuesses.remove(playerName);
        this.totalCorrectGuessesInARow.remove(playerName);

        this.currentTimeUntilCorrectGuess.remove(playerName);
        this.totalTimeUntilCorrectGuess.remove(playerName);

        this.currentNumberOfWrongGuesses.remove(playerName);
        this.totalNumberOfWrongGuesses.remove(playerName);

        this.leaderBoardTotalScore.remove(playerName);
    }

    public void resetAllTotalScores() {
        // This function resets all HashMaps that store the total scores.
        // This can be used if the game is restarted with the same settings
        // That is, if the admin chooses to play the same game again

        this.totalCorrectGuesses = new HashMap<String, Integer>();
        this.totalCorrectGuessesInARow = new HashMap<String, Integer>();
        this.totalTimeUntilCorrectGuess = new HashMap<String, Integer>();
        this.totalNumberOfWrongGuesses = new HashMap<String, Integer>();

        initTotalScores();
    }

    public void updateTotalScores() {


        // UPDATE: totalCorrectGuesses
        for (String playerName : this.currentCorrectGuess.keySet()) {
            if (this.currentCorrectGuess.get(playerName)) {
                // if the boolean for the player is true, the player has made a correct guess
                // therefore increment totalCorrectGuesses and currentCorrectGuessesInARow by 1
                this.totalCorrectGuesses.put(playerName, this.totalCorrectGuesses.get(playerName) + 1);
                this.totalCorrectGuessesInARow.put(playerName, this.totalCorrectGuessesInARow.get(playerName) + 1);
            }
            else {
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

    private void initTotalScores() {
        // This function initializes all HashMaps that store the total scores.
        // This can be used if the game is restarted with the same settings
        // That is, if the admin chooses to play the same game again

        for (String playerName : this.playerNames) {
            this.totalCorrectGuesses.put(playerName, 0);
            this.totalCorrectGuessesInARow.put(playerName, 0);
            this.totalTimeUntilCorrectGuess.put(playerName, 0);
            this.totalNumberOfWrongGuesses.put(playerName, 0);
            this.leaderBoardTotalScore.put(playerName, 0);
            this.currentCorrectGuess.put(playerName, false);
            this.currentTimeUntilCorrectGuess.put(playerName, 0);
            this.currentNumberOfWrongGuesses.put(playerName, 0);
        }

    }

    public void computeLeaderBoardScore() {
        // This function computes the score for each player in the game
        // NOTE: This function can only be called once per round, otherwise the 
        // total-score will be incorrect


        // initialize the HashMap that will store the score for each player
        this.currentTotalScore = new HashMap<String, Integer>();

        // compute the score for each player
        for (String playerName : this.playerNames) {
            if (this.currentCorrectGuess.getOrDefault(playerName, true) == null) {
                // this is for error handling (there shouldn't be a case where the currentCorrectGuess is null)

                this.currentTotalScore.put(playerName, 0);
            }
            else if (!this.currentCorrectGuess.get(playerName)) {
                // if the player has not guessed correctly, the score is 0
                this.currentTotalScore.put(playerName, 0);
            }

            else {
                // the score is computed as follows:
                // 3 * the number of total correct guesses in a row
                // + 100 / the time until the correct guess (inverse since the faster the better)
                // - the number of wrong guesses / 5 (to make the score less sensitive to the number
                //  of wrong guesses, the number of wrong guesses is divided by 5)

                Integer score;
                try {
                    score = (10 * this.totalCorrectGuessesInARow.get(playerName))
                            + (100 / this.currentTimeUntilCorrectGuess.get(playerName))
                            - (this.currentNumberOfWrongGuesses.get(playerName) / 5);
                }
                catch (ArithmeticException e) {
                    score = (10 * this.totalCorrectGuessesInARow.get(playerName))
                            + (100 / 1)
                            - (this.currentNumberOfWrongGuesses.get(playerName) / 5);
                }
                if (score < 0) {
                    score = 0;
                }

                this.currentTotalScore.put(playerName, score);
            }

        }

        // update leaderBoardTotalScore with the new score
        for (String playerName : this.playerNames) {
            this.leaderBoardTotalScore.put(playerName, this.leaderBoardTotalScore.get(playerName) + this.currentTotalScore.get(playerName));
        }
    }

    public void resetAllCurrentScores() {
        // This function resets all HashMaps that store the current scores.
        // To be used after each round.

        this.currentCorrectGuess = new HashMap<String, Boolean>();
        this.currentTimeUntilCorrectGuess = new HashMap<String, Integer>();
        this.currentNumberOfWrongGuesses = new HashMap<String, Integer>();

        for (String playerName : this.playerNames) {
            this.currentCorrectGuess.put(playerName, false);
            this.currentTimeUntilCorrectGuess.put(playerName, 0);
            this.currentNumberOfWrongGuesses.put(playerName, 0);
        }
    }

}
