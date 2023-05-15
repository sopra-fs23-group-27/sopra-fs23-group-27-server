package ch.uzh.ifi.hase.soprafs23.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PlayerGetDTO {

    private Long id;
    private String playerName;
    private Integer nRoundsPlayed;
    private Integer overallTotalNumberOfCorrectGuesses;
    private Integer overallTotalNumberOfWrongGuesses;
    private Integer overallTotalTimeUntilCorrectGuess;


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


    // playerstats
    public Integer getnRoundsPlayed() {
        return nRoundsPlayed;
    }

    public void setnRoundsPlayed(Integer nRoundsPlayed) {
        this.nRoundsPlayed = nRoundsPlayed;
    }

    public Integer getOverallTotalNumberOfCorrectGuesses() {
        return overallTotalNumberOfCorrectGuesses;
    }

    public void setOverallTotalNumberOfCorrectGuesses(Integer overallTotalNumberOfCorrectGuesses) {
        this.overallTotalNumberOfCorrectGuesses = overallTotalNumberOfCorrectGuesses;
    }

    public Integer getOverallTotalNumberOfWrongGuesses() {
        return overallTotalNumberOfWrongGuesses;
    }

    public void setOverallTotalNumberOfWrongGuesses(Integer overallTotalNumberOfWrongGuesses) {
        this.overallTotalNumberOfWrongGuesses = overallTotalNumberOfWrongGuesses;
    }

    public Integer getOverallTotalTimeUntilCorrectGuess() {
        return overallTotalTimeUntilCorrectGuess;
    }

    public void setOverallTotalTimeUntilCorrectGuess(Integer overallTotalTimeUntilCorrectGuess) {
        this.overallTotalTimeUntilCorrectGuess = overallTotalTimeUntilCorrectGuess;
    }

}