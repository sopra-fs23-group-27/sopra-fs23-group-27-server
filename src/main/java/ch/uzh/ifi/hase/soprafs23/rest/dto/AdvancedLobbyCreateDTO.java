package ch.uzh.ifi.hase.soprafs23.rest.dto;

public class AdvancedLobbyCreateDTO {

    private boolean isPublic;
    private int numSeconds;
    private int numSecondsUntilHint;
    private int hintInterval;
    private int maxNumGuesses;
    private String lobbyName;

    public boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public int getNumSeconds() {
        return numSeconds;
    }

    public void setNumSeconds(int numSeconds) {
        this.numSeconds = numSeconds;
    }

    public int getNumSecondsUntilHint() {
        return numSecondsUntilHint;
    }

    public void setNumSecondsUntilHint(int numSecondsUntilHint) {
        this.numSecondsUntilHint = numSecondsUntilHint;
    }

    public int getHintInterval() {
        return hintInterval;
    }

    public void setHintInterval(int hintInterval) {
        this.hintInterval = hintInterval;
    }

    public int getMaxNumGuesses() {
        return maxNumGuesses;
    }

    public void setMaxNumGuesses(int maxNumGuesses) {
        this.maxNumGuesses = maxNumGuesses;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public void setLobbyName(String lobbyName) {
        this.lobbyName = lobbyName;
    }
}
