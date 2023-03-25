package ch.uzh.ifi.hase.soprafs23.rest.dto;

public class PlayerPostDTO {

    private String password;

    private String playername;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPlayername() {
        return playername;
    }

    public void setPlayername(String playername) {
        this.playername = playername;
    }
}
