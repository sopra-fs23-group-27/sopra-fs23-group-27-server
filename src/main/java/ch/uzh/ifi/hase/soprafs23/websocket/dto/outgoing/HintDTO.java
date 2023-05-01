package ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing;

public class HintDTO {
    private String hint;

    public HintDTO(String hint) {
        this.hint = hint;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }
}
