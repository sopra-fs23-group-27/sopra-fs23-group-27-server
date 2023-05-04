package ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing;

public class FlagDTO {
    private String url;

    public FlagDTO(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
