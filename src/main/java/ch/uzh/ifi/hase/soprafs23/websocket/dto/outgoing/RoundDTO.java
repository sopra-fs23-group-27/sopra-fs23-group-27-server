package ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing;

public class RoundDTO {

    private Integer round;

    public RoundDTO(Integer round) {
        this.round = round;
    }

    public Integer getRound() {
        return round;
    }

    public void setRound(Integer round) {
        this.round = round;
    }
    
}
