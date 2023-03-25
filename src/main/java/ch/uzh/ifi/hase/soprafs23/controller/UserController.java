package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserGetDTO> getAllUsers(@RequestHeader("Authorization") String token) {
        // fetch all users in the internal representation
        List<User> users = userService.getUsers(token);
        List<UserGetDTO> userGetDTOs = new ArrayList<>();

        // convert each user to the API representation
        for (User user : users) {
            userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
        }
        return userGetDTOs;
    }

    @GetMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO getUser(@PathVariable Long userId,
                              @RequestHeader("Authorization") String token) {
        // fetch user in the internal representation
        User user = userService.getUserById(userId, token);
        // convert user to the API representation
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
    }

    @PostMapping("/users")
    @CrossOrigin(exposedHeaders = "*")
    public ResponseEntity createUser(@RequestBody UserPostDTO userPostDTO) {
        // convert API user to internal representation
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        // create user
        User createdUser = userService.createUser(userInput);

        // convert internal representation of user back to API
        UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Authorization", createdUser.getToken())
                .body(userGetDTO);
    }

    @PostMapping("/login")
    @CrossOrigin(exposedHeaders = "*")
    public ResponseEntity loginUser(@RequestBody UserPostDTO userPostDTO) {
        // convert API user to internal representation
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        // login user
        User loggedInUser = userService.loginUser(userInput);


        // convert internal representation of user back to API
        UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(loggedInUser);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Authorization", loggedInUser.getToken())
                .body(userGetDTO);
    }

    @PostMapping("/users/{userId}/logout")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO logoutUser(@PathVariable Long userId,
                                 @RequestHeader("Authorization") String token) {

        // logout user
        User loggedOutUser = userService.logoutUser(userId, token);

        // convert internal representation of user back to API
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(loggedOutUser);
    }


    @PutMapping("/users/{userId}")
    @CrossOrigin(exposedHeaders = "*")
    public ResponseEntity updateUser(@PathVariable Long userId,
                                     @RequestBody UserPutDTO userPutDTO,
                                     @RequestHeader("Authorization") String token) {

        // check if user exists
        userService.checkIfUserIdExists(userId);

        // update user
        User updatedUser = userService.updateUser(userId, userPutDTO, token);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .header("Authorization", updatedUser.getToken())
                .build();
    }
}
