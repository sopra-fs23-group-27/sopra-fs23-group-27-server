package ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing;

public class GuessEvalDTO {
    private String guess;
    private boolean isCorrect;

    public GuessEvalDTO(String guess, boolean isCorrect) {
        this.guess = guess;
        this.isCorrect = isCorrect;
    }

    public void setIsCorrect(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }
}
