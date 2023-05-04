package ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing;

public class WSConnectedDTO {
    private String playerName;

    private Long lobbyId;

    public WSConnectedDTO(String username, Long lobbyId) {
        this.playerName = username;
        this.lobbyId = lobbyId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Long getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(Long lobbyId) {
        this.lobbyId = lobbyId;
    }
}
