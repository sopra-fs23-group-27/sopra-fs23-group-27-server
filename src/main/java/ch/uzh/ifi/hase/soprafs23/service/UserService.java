package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;

import org.apache.catalina.connector.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<User> getUsers() {
    return this.userRepository.findAll();
  }

  public User getUser(Long id) {
    return this.userRepository.findById(id).get();
  }

   // helper function to modify a user based on provided username and birthDate
   public User modifyUser(Long id, User user) {
    User modifiedUser = userRepository.findById(id).get();

    if (modifiedUser == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("A user with this userId was not found!"));
    }

    // modify data if entered as argument (i.e. not null)
    if (user.getUsername() != null) {
      modifiedUser.setUsername(user.getUsername());
    }
    if (user.getBirthDate() != null) {
      modifiedUser.setBirthDate(user.getBirthDate());
    }
    // throw exception if no data is provided
    if (user.getUsername() == null && user.getBirthDate() == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Please provide a new username and/or a new password!"));
    }
    return modifiedUser;
  }

  public User createUser(User newUser) {
    newUser.setToken(UUID.randomUUID().toString());
    newUser.setStatus(UserStatus.ONLINE);
    checkIfUserExists(newUser);
    // saves the given entity but data is only persisted in the database once
    // flush() is called
    newUser = userRepository.save(newUser);
    userRepository.flush();

    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }

  /**
   * This is a helper method that will check the uniqueness criteria of the
   * username and the name
   * defined in the User entity. The method will do nothing if the input is unique
   * and throw an error otherwise.
   *
   * @param userToBeCreated
   * @throws org.springframework.web.server.ResponseStatusException
   * @see User
   */
  private void checkIfUserExists(User userToBeCreated) {
    User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

    String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
    if (userByUsername != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT,
          String.format(baseErrorMessage, "username", "is"));
    }
  }


  // helper function to login a user based on provided username and password
  public String loginUser(String username, String password) {
    User user = userRepository.findByUsername(username);
    String loginToken;

    // check if user exists in DB and matches provided login data
    if (user == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("User does not exist! Please make sure to register first."));
    } else if (!(user.getPassword().equals(password))) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Invalid password!"));
    } else {
      user.setStatus(UserStatus.ONLINE);
      loginToken = UUID.randomUUID().toString();
      user.setToken(loginToken);
      userRepository.save(user);
      userRepository.flush();
    }

    return loginToken;
}

// helper function to logout a user
public User logoutUser (User toBeLoggedOutUser) {

  User loggedOutUser = toBeLoggedOutUser;
  loggedOutUser.setStatus(UserStatus.OFFLINE);
  // saves the given entity but data is only persisted in the database once
  // flush() is called
  loggedOutUser = userRepository.save(loggedOutUser);
  userRepository.flush();
  return loggedOutUser;
}
}
