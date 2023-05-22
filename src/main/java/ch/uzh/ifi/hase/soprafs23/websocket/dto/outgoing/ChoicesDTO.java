package ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing;

import java.util.List;

public class ChoicesDTO {
    private List<String> choices;

    public ChoicesDTO(List<String> choices) {
        this.choices = choices;
    }

    public List<String> getChoices() {
        return choices;
    }

    public void setChoices(List<String> choices) {
        this.choices = choices;
    }

}
