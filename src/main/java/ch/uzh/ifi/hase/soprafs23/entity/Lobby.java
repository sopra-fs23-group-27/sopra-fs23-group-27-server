package ch.uzh.ifi.hase.soprafs23.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Inheritance
@DiscriminatorColumn(name = "LOBBY_TYPE")
@Table(name = "LOBBY")
public class Lobby implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lobbyId;

    private String lobbyName;
    private boolean isPublic;
    private int numSeconds;

    @ElementCollection
    private List<String> joinedPlayerNames;

    private Long lobbyCreatorId;
    private boolean isJoinable;
    private Long currentGameId;
    private String privateLobbyKey;


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

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
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

    public Long getLobbyCreatorId() {
        return lobbyCreatorId;
    }

    public void setLobbyCreatorId(Long lobbyCreatorId) {
        this.lobbyCreatorId = lobbyCreatorId;
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
