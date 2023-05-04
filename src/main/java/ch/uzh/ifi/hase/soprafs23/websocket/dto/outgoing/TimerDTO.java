package ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing;

public class TimerDTO {
    private int time;

    public TimerDTO(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
