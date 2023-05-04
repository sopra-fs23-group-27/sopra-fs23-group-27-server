package ch.uzh.ifi.hase.soprafs23.websocket.dto.incoming;

public class AuthenticateDTO {

    private String playerToken;

    public String getPlayerToken() {
        return playerToken;
    }

    public void setPlayerToken(String playerToken) {
        this.playerToken = playerToken;
    }
}