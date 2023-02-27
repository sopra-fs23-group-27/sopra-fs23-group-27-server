package ch.uzh.ifi.hase.soprafs23.rest.dto;

import java.time.LocalDate;

public class UserPutDTO {
    private String username;
    private LocalDate birthDate;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
}
