package ch.uzh.ifi.hase.soprafs23.rest.dto;

public class PlayerPostDTO {

    private String password;

    private String playerName;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
