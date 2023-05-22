package ch.uzh.ifi.hase.soprafs23.entity;


import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import javax.persistence.*;
import java.io.Serializable;
import java.security.Principal;
import java.time.Instant;

/**
 * Internal Player Representation
 * This class composes the internal representation of the player and defines how
 * the player is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */
@Entity
@Table(name = "PLAYER")
public class Player implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    // automatically add the current timestamp to DB
    @CreationTimestamp
    private Instant createdOn;
    
    @UpdateTimestamp
    private Instant lastUpdatedOn;

    @Column(nullable = false, unique = true)
    private String playerName;

    @Column
    private boolean isCreator;

    @Column
    private String password;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(unique = true)
    private String wsConnectionId;

    @Column
    private Long lobbyId;

    // new fields for player stats
    @Column
    private Integer totalCorrectGuesses = 0;

    @Column
    private Integer nRoundsPlayed = 0;

    @Column
    private Integer timeUntilCorrectGuess = 0;

    @Column
    private Integer numWrongGuesses = 0;

    @Column
    private Boolean isPermanent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isCreator() {
        return isCreator;
    }

    public void setCreator(boolean creator) {
        isCreator = creator;
    }

    public Long getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(Long lobbyId) {
        this.lobbyId = lobbyId;
    }

    public String getWsConnectionId() {
        return wsConnectionId;
    }

    public void setWsConnectionId(String wsConnectionId) {
        this.wsConnectionId = wsConnectionId;
    }

    public int getTotalCorrectGuesses() {
        return totalCorrectGuesses;
    }

    public void setTotalCorrectGuesses(int totalCorrectGuesses) {
        this.totalCorrectGuesses = totalCorrectGuesses;
    }

    public int getnRoundsPlayed() {
        return nRoundsPlayed;
    }

    public void setnRoundsPlayed(int nRoundsPlayed) {
        this.nRoundsPlayed = nRoundsPlayed;
    }

    public int getTimeUntilCorrectGuess() {
        return timeUntilCorrectGuess;
    }

    public void setTimeUntilCorrectGuess(int avgTimeUntilCorrectGuess) {
        this.timeUntilCorrectGuess = avgTimeUntilCorrectGuess;
    }

    public int getNumWrongGuesses() {
        return numWrongGuesses;
    }

    public void setNumWrongGuesses(int numWrongGuesses) {
        this.numWrongGuesses = numWrongGuesses;
    }

    public void setPermanent(Boolean permanent) {
        isPermanent = permanent;
    }

    public Boolean isPermanent() {
        return isPermanent;
    }

}
