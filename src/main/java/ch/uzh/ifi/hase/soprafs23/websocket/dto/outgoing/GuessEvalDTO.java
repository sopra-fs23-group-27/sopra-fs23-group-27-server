package ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing;

public class GuessEvalDTO {
    private String guess;
    private boolean isCorrect;

    public GuessEvalDTO(String guess, boolean isCorrect) {
        this.guess = guess;
        this.isCorrect = isCorrect;
    }

    public String getGuess() {
        return guess;
    }

    public void setGuess(String guess) {
        this.guess = guess;
    }

    public boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }
}
