package ch.uzh.ifi.hase.soprafs23.rest.dto;

import java.time.LocalDate;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;

public class UserGetDTO {

  private Long id;
  private LocalDate timestamp;
  private String username;
  private UserStatus status;
  private LocalDate birthDate;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public LocalDate getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDate timestamp) {
    this.timestamp = timestamp;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public UserStatus getStatus() {
    return status;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }

  public LocalDate getBirthDate() {
        return birthDate;
    }

  public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
}
