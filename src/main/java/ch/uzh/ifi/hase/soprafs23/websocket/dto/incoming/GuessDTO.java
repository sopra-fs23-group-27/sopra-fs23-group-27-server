package ch.uzh.ifi.hase.soprafs23.websocket.dto.incoming;

public class GuessDTO {

    private String playerName;
    private String guess;

    public GuessDTO(String playerName, String guess) {
        this.playerName = playerName;
        this.guess = guess;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getGuess() {
        return guess;
    }

    public void setGuess(String guess) {
        this.guess = guess;
    }

}