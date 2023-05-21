package ch.uzh.ifi.hase.soprafs23.rest.dto;

import java.util.ArrayList;
import java.util.Arrays;

public class AdvancedLobbyCreateDTO {

    private boolean isPublic = true;
    private ArrayList<String> continent = new ArrayList<String>(
            Arrays.asList("Africa", "Americas", "Asia", "Europe", "Oceania"));
    private int numRounds = 4;
    private int numSeconds = 30;
    private int numSecondsUntilHint = 10;
    private int hintInterval = 5;
    private int maxNumGuesses = 10;
    private String lobbyName;

    public boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public int getNumRounds() {
        return numRounds;
    }

    public void setNumRounds(int numRounds) {
        this.numRounds = numRounds;
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

    public ArrayList<String> getContinent() {
        return continent;
    }

    public void setContinent(ArrayList continent) {
        this.continent = continent;
    }
}
