package ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing;

public class CorrectGuessDTO {

    private String correctGuess;

    public CorrectGuessDTO(String correctGuess) {
        this.correctGuess = correctGuess;
    }

    public String getCorrectGuess() {
        return correctGuess;
    }

    public void setCorrectGuess(String correctGuess) {
        this.correctGuess = correctGuess;
    }
    
}
