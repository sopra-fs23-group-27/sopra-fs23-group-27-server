package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPutDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import java.util.List;

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

    public List<User> getUsers(String token) {
        checkIfTokenExists(token);
        return this.userRepository.findAll();
    }

    public User getUserById(long userId, String token) {
        checkIfTokenExists(token);
        checkIfUserIdExists(userId);
        return this.userRepository.findById(userId);
    }

    public User createUser(User newUser) {

        // create basic authentication token
        String username = newUser.getUsername();
        String password = newUser.getPassword();
        String encodeBytes = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());

        newUser.setToken("Basic " + encodeBytes);

        newUser.setStatus(UserStatus.ONLINE);

        // set the current datetime as the creation date
        newUser.setCreationDate(LocalDateTime.now());
        checkIfUsernameExists(newUser.getUsername());
        // saves the given entity but data is only persisted in the database once
        // flush() is called
        newUser = userRepository.save(newUser);
        userRepository.flush();

        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    public User loginUser(User existingUser) {
        User userByUsername = userRepository.findByUsername(existingUser.getUsername());

        if (userByUsername == null) { //check if a user with the provided username exists
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The username provided does not exist. Please register first.");
        }
        else if (!existingUser.getPassword().equals(userByUsername.getPassword())) { //given a user with the provided username exists, check if the provided password is correct
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "The password provided is not correct. Please try again.");
        }

        // create basic authentication token
        String username = userByUsername.getUsername();
        String password = userByUsername.getPassword();
        String encodeBytes = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        userByUsername.setToken("Basic " + encodeBytes);

        userByUsername.setStatus(UserStatus.ONLINE);
        userByUsername = userRepository.save(userByUsername);
        userRepository.flush();

        return userByUsername;
    }

    public User logoutUser(long userId, String token) {

        checkIfUserIdExists(userId);
        User existingUser = userRepository.findById(userId);

        // check if token is valid
        checkIfUserTokenIsValid(token, existingUser);

        // check if user is already logged out
        if (existingUser.getStatus() == UserStatus.OFFLINE) {
            String baseErrorMessage = "The user with id %d is already logged out";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(baseErrorMessage, userId));
        }

        // logout user
        existingUser.setStatus(UserStatus.OFFLINE);
        existingUser = userRepository.save(existingUser);
        userRepository.flush();

        return existingUser;
    }


    public User updateUser(long userId, UserPutDTO userUpdateRequest, String token) {
        //checkIfUserIdExists(userId);
        User userToBeUpdated = userRepository.findById(userId);

        // check if token is valid
        checkIfUserTokenIsValid(token, userToBeUpdated);

        // update password if provided password is not null
        if (userUpdateRequest.getPassword() != null) {
            userToBeUpdated.setPassword(userUpdateRequest.getPassword());
        }

        // update birthday if provided birthday is not null
        if (userUpdateRequest.getBirthday() != null && !userUpdateRequest.getBirthday().isBlank()) {
            try {
                LocalDate birthdayParsed = LocalDate.parse(userUpdateRequest.getBirthday());
                userToBeUpdated.setBirthday(birthdayParsed);
            }
            catch (DateTimeParseException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Error: The birthday provided is not in the correct format. Please use the format yyyy-mm-dd.");
            }

        }

        // update username if provided username is not null
        if (userUpdateRequest.getUsername() != null) {
            checkIfUsernameExists(userUpdateRequest.getUsername());
            userToBeUpdated.setUsername(userUpdateRequest.getUsername());
        }

        String username = userToBeUpdated.getUsername();
        String password = userToBeUpdated.getPassword();
        String encodeBytes = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());

        userToBeUpdated.setToken("Basic " + encodeBytes);

        userToBeUpdated = userRepository.save(userToBeUpdated);
        userRepository.flush();
        return userToBeUpdated;
    }

    /**
     * This is a helper method that will check the uniqueness criteria of the
     * username
     * defined in the User entity. The method will do nothing if the input is unique
     * and throw an error otherwise.
     *
     * @param newUsername
     * @throws org.springframework.web.server.ResponseStatusException
     * @see User
     */
    private void checkIfUsernameExists(String newUsername) {
        User userByUsername = userRepository.findByUsername(newUsername);

        String baseErrorMessage = "Error: The %s provided %s already taken and cannot be used. " +
                "Please select another username!";
        if (userByUsername != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format(baseErrorMessage, "username", "is"));
        }
    }

    public void checkIfUserIdExists(long userId) {
        User userById = userRepository.findById(userId);
        if (userById == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error: The user with userId " + userId + " does not exist.");
        }
    }


    private void checkIfUserTokenIsValid(String token, User existingUser) {
        if (!token.equals(existingUser.getToken())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "You are unauthorized to perform this action since your provided token is not valid.");
        }
    }

    private void checkIfTokenExists(String token) {
        boolean tokenExists = userRepository.existsByToken(token);
        if (!tokenExists) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Error: You are unauthorized to perform this action.");
        }
    }
}
