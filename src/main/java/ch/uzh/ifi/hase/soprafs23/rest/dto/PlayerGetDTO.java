package ch.uzh.ifi.hase.soprafs23.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PlayerGetDTO {

    private Long id;
    private String playerName;
    private Integer totalCorrectGuesses = 0;
    private Integer nRoundsPlayed = 0;
    private Integer timeUntilCorrectGuess = 0;
    private Integer numWrongGuesses = 0;
    private boolean isPermanent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

//    @JsonProperty("creation_date")
//    public LocalDateTime getCreationDate() {
//        return creationDate;
//    }


    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Integer getTotalCorrectGuesses() {
        return totalCorrectGuesses;
    }

    public void setTotalCorrectGuesses(Integer totalCorrectGuesses) {
        this.totalCorrectGuesses = totalCorrectGuesses;
    }

    // playerstats
    public Integer getnRoundsPlayed() {
        return nRoundsPlayed;
    }

    public void setnRoundsPlayed(Integer nRoundsPlayed) {
        this.nRoundsPlayed = nRoundsPlayed;
    }

    public Integer getTimeUntilCorrectGuess() {
        return timeUntilCorrectGuess;
    }

    public void setTimeUntilCorrectGuess(Integer timeUntilCorrectGuess) {
        this.timeUntilCorrectGuess = timeUntilCorrectGuess;
    }

    public Integer getNumWrongGuesses() {
        return numWrongGuesses;
    }

    public void setNumWrongGuesses(Integer numWrongGuesses) {
        this.numWrongGuesses = numWrongGuesses;
    }

    public boolean isPermanent() {
        return isPermanent;
    }

    public void setPermanent(boolean isPermanent) {
        this.isPermanent = isPermanent;
    }


}