package ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing;

import java.util.ArrayList;

import antlr.collections.List;

public class GameStatsDTO {
    private ArrayList<String> PlayerNames;
    private ArrayList<Integer> playerHasGuessed;
    private ArrayList<Integer> TotalGameScores;
    private ArrayList<Integer> TotalCorrectGuesses;
    private ArrayList<Integer> TotalTimeUntilCorrectGuess;
    private ArrayList<Integer> TotalWrongGuesses;
    private ArrayList<Integer> TotalCorrectGuessesInARow;
    private Boolean isCreator;

    public GameStatsDTO(
            ArrayList<String> PlayerNames,
            ArrayList<Integer> playerHasGuessed,
            ArrayList<Integer> TotalGameScores,
            ArrayList<Integer> TotalCorrectGuesses,
            ArrayList<Integer> TotalTimeUntilCorrectGuess,
            ArrayList<Integer> TotalWrongGuesses,
            ArrayList<Integer> TotalCorrectGuessesInARow


    ) {
        this.PlayerNames = PlayerNames;
        this.playerHasGuessed = playerHasGuessed;
        this.TotalGameScores = TotalGameScores;
        this.TotalCorrectGuesses = TotalCorrectGuesses;
        this.TotalTimeUntilCorrectGuess = TotalTimeUntilCorrectGuess;
        this.TotalWrongGuesses = TotalWrongGuesses;
        this.TotalCorrectGuessesInARow = TotalCorrectGuessesInARow;
        this.isCreator = false;
    }

    public ArrayList<String> getPlayerNames() {
        return PlayerNames;
    }

    public void setPlayerNames(ArrayList<String> playerNames) {
        PlayerNames = playerNames;
    }

    public ArrayList<Integer> getPlayerHasGuessed() {
        return playerHasGuessed;
    }

    public void setPlayerHasGuessed(ArrayList<Integer> playerHasGuessed) {
        this.playerHasGuessed = playerHasGuessed;
    }

    public ArrayList<Integer> getTotalGameScores() {
        return TotalGameScores;
    }

    public void setTotalGameScores(ArrayList<Integer> totalGameScores) {
        TotalGameScores = totalGameScores;
    }

    public ArrayList<Integer> getTotalCorrectGuesses() {
        return TotalCorrectGuesses;
    }

    public void setTotalCorrectGuesses(ArrayList<Integer> totalCorrectGuesses) {
        TotalCorrectGuesses = totalCorrectGuesses;
    }

    public ArrayList<Integer> getTotalTimeUntilCorrectGuess() {
        return TotalTimeUntilCorrectGuess;
    }

    public void setTotalTimeUntilCorrectGuess(ArrayList<Integer> totalTimeUntilCorrectGuess) {
        TotalTimeUntilCorrectGuess = totalTimeUntilCorrectGuess;
    }

    public ArrayList<Integer> getTotalWrongGuesses() {
        return TotalWrongGuesses;
    }

    public void setTotalWrongGuesses(ArrayList<Integer> totalWrongGuesses) {
        TotalWrongGuesses = totalWrongGuesses;
    }

    public ArrayList<Integer> getTotalCorrectGuessesInARow() {
        return TotalCorrectGuessesInARow;
    }

    public void setTotalCorrectGuessesInARow(ArrayList<Integer> totalCorrectGuessesInARow) {
        TotalCorrectGuessesInARow = totalCorrectGuessesInARow;
    }

    public void setIsCreator(Boolean isCreator) {
        this.isCreator = isCreator;
    }

    public Boolean getIsCreator() {
        return isCreator;
    }

}
