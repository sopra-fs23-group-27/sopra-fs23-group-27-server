package ch.uzh.ifi.hase.soprafs23.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PlayerGetDTO {

    private Long id;
    private String playerName;

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

}