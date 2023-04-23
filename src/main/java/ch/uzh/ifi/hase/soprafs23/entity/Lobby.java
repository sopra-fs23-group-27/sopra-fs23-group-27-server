package ch.uzh.ifi.hase.soprafs23.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance
@DiscriminatorColumn(name = "LOBBY_TYPE")
@Table(name = "LOBBY")
public abstract class Lobby implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lobbyId;

    private String lobbyName;
    private boolean isPublic;
    private int numSeconds;

    @ElementCollection
    private List<String> joinedPlayerNames;

    private String lobbyCreatorPlayerToken;
    private boolean isJoinable;
    private Long currentGameId;
    private String privateLobbyKey;

    public Lobby() {
        this.isJoinable = true;
        this.joinedPlayerNames = new ArrayList<>();
    }


    @Transient
    public String getMode() {
        return this.getClass().getAnnotation(DiscriminatorValue.class).value();
    }

    public void addPlayerToLobby(String playerName) {
        this.joinedPlayerNames.add(playerName);
    }

    public Long getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(Long id) {
        this.lobbyId = id;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public void setLobbyName(String lobbyName) {
        this.lobbyName = lobbyName;
    }

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

    public boolean isJoinable() {
        return isJoinable;
    }

    public void setJoinable(boolean joinable) {
        isJoinable = joinable;
    }

    public Long getCurrentGameId() {
        return currentGameId;
    }

    public void setCurrentGameId(Long currentGameId) {
        this.currentGameId = currentGameId;
    }

    public String getPrivateLobbyKey() {
        return privateLobbyKey;
    }

    public void setPrivateLobbyKey(String privateLobbyKey) {
        this.privateLobbyKey = privateLobbyKey;
    }
}
