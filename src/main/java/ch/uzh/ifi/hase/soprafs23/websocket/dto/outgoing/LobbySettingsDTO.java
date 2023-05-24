package ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LobbySettingsDTO {

    private Long lobbyId;
    private String lobbyName;
    private String mode;
    private boolean isPublic;
    private ArrayList<String> continent;
    private int numOptions;
    private int numRounds;
    private int numSeconds;
    private int numSecondsUntilHint;
    private int hintInterval;
    private int maxNumGuesses;
    private List<String> joinedPlayerNames;
    private String lobbyCreatorPlayerToken;
    private String privateLobbyKey;

    private HashMap<String, Boolean> playerRoleMap = new HashMap<String, Boolean>();

    public HashMap<String, Boolean> getPlayerRoleMap() {
        return playerRoleMap;
    }

    public void setPlayerRoleMap(HashMap<String, Boolean> playerRoleMap) {
        this.playerRoleMap = playerRoleMap;
    }

    public Long getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(Long lobbyId) {
        this.lobbyId = lobbyId;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public void setLobbyName(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }


    public boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public int getNumOptions() {
        return numOptions;
    }

    public void setNumOptions(int numOptions) {
        this.numOptions = numOptions;
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

    public List<String> getJoinedPlayerNames() {
        return joinedPlayerNames;
    }

    public void setJoinedPlayerNames(List<String> joinedPlayerNames) {
        this.joinedPlayerNames = joinedPlayerNames;
    }

    public String getLobbyCreatorPlayerToken() {
        return lobbyCreatorPlayerToken;
    }

    public void setLobbyCreatorPlayerToken(String lobbyCreatorPlayerToken) {
        this.lobbyCreatorPlayerToken = lobbyCreatorPlayerToken;
    }

    public String getPrivateLobbyKey() {
        return privateLobbyKey;
    }

    public void setPrivateLobbyKey(String privateLobbyKey) {
        this.privateLobbyKey = privateLobbyKey;
    }

    public int getNumRounds() {
        return numRounds;
    }

    public void setNumRounds(int numRounds) {
        this.numRounds = numRounds;
    }

    public ArrayList<String> getContinent() {
        return continent;
    }

    public void setContinent(ArrayList<String> continent) {
        this.continent = continent;
    }
}
