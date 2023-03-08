package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.exceptions.SopraServiceException;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.AuthenticationPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class UserController {

  private final UserService userService;

  private final UserRepository userRepository;

  UserController(UserService userService, UserRepository userRepository) {
    this.userRepository = userRepository;
    this.userService = userService;
  }

  @GetMapping("/users")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<UserGetDTO> getAllUsers() {
    // fetch all users in the internal representation
    List<User> users = userService.getUsers();
    List<UserGetDTO> userGetDTOs = new ArrayList<>();

    // convert each user to the API representation
    for (User user : users) {
      userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
    }
    return userGetDTOs;
  }

  // create a GET mapping to fetch data from a specific user
  @GetMapping("/users/{id}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO getUser(@PathVariable Long id) {

    // fetch specific user in the internal representation
    UserGetDTO userGetDTO = new UserGetDTO();
    User user = userService.getUser(id);

    // check if user with requested user id exists
    if (user == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("No user with this id exists!"));
    }

    // convert user to the API representation
    userGetDTO = (DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));

    // return user
    return userGetDTO;
  }

  // create a POST mapping to set the user status to OFFLINE upon logout
  @PostMapping("/users/{id}/logout")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO logUserOut(@PathVariable(value = "id") Long id) {
    User toBeloggedOutUser = userRepository.findById(id).get();

    // throw exception if user does not exist
    if (toBeloggedOutUser == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("User does not exist"));
    }

    // logout currently logged in user
    User loggedOutUser = userService.logoutUser(toBeloggedOutUser);

    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(loggedOutUser);
  }

  // create a PUT mapping to change data from a specific user
  @PutMapping("/users/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void modifyUser(@PathVariable Long id, @RequestBody UserPutDTO userPutDTO) {
    // convert API user to internal representation
    User userInput = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);

    // throw exception if username is already taken
    if (userRepository.findByUsername(userInput.getUsername()) != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("Username already taken!"));
    }

    // throw exception if user does not exist
    User user = userRepository.findById(id).get();
    if (user == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("No user with this id exists!"));
    }

    // throw exception if UserPutDTO provides no info
    if (userInput == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,
          String.format("Please provide valid information to edit the user!"));
    }

    // modify user
    userService.modifyUser(id, userInput);
  }

  @PostMapping("/users")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public UserGetDTO createUser(@RequestBody UserPostDTO userPostDTO) {
    // convert API user to internal representation
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // create user
    User createdUser = userService.createUser(userInput);
    // convert internal representation of user back to API
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
  }

  // create a POST mapping to check if the login data of the current user
  // matches the data of a user in the database
  @PostMapping("/login")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public AuthenticationPostDTO loginUser(@RequestBody UserPostDTO userPostDTO) throws SopraServiceException {
    // fetch user to be logged in from the internal representation
    User toBeloggedInUser = userRepository.findByUsername(userPostDTO.getUsername());

    // throw exception if user does not exist
    if (toBeloggedInUser == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("User does not exist"));
    }

    // get token from user login
    String token = userService.loginUser(userPostDTO.getUsername(), userPostDTO.getPassword());
    AuthenticationPostDTO authenticationPostDTO = new AuthenticationPostDTO();
    authenticationPostDTO.setId(toBeloggedInUser.getId());
    authenticationPostDTO.setToken(token);
    authenticationPostDTO.setUsername(userPostDTO.getUsername());

    // return token
    return authenticationPostDTO;
  }
}