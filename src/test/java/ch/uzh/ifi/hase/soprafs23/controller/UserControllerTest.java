package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void givenUser_whenGetUser_thenReturnJsonArray() throws Exception {
        // given
        User user = new User();
        user.setPassword("password");
        user.setUsername("firstname@lastname");
        user.setStatus(UserStatus.OFFLINE);
        user.setCreationDate(LocalDateTime.of(2023, 3, 5, 15, 59, 59));

        // this mocks the UserService -> we define above what the userService should
        // return when getUserById() is called
        given(userService.getUserById(anyLong(), anyString())).willReturn(user);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "someToken");

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.creation_date", is(user.getCreationDate().toString())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
    }

    ////////// MAPPING 1 //////////
    @Test
    public void createUser_validInput_userCreated() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setPassword("password");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);
        user.setCreationDate(LocalDateTime.of(2023, 3, 5, 15, 59, 59));


        // valid token
        String validToken = user.getToken();

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setPassword("password");
        userPostDTO.setUsername("testUsername");

        given(userService.createUser(Mockito.any())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(header().string("Authorization", validToken))
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.creation_date", is(user.getCreationDate().toString())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
    }


    ////////// MAPPING 2 //////////
    @Test
    public void createUser_invalidInput_409thrown() throws Exception {
        // given
        String errorMessage = "Error: The username provided is already taken and cannot be used. " +
                "Please select another username!";

        ResponseStatusException conflictException = new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setPassword("password");
        userPostDTO.setUsername("testUsername");

        // when the mock object (userService) is called for createUser() method with any parameters,
        // then it will return the object "conflictException"
        given(userService.createUser(Mockito.any())).willThrow(conflictException);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isConflict())
                .andExpect(status().reason(is(errorMessage)));
    }


    ////////// MAPPING 3 //////////
    @Test
    public void getUserProfile_validInput_userRetrieved() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setPassword("password");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);
        user.setCreationDate(LocalDateTime.of(2023, 3, 5, 15, 59, 59));
        user.setBirthday(LocalDate.of(1999, 12, 31));

        // valid userId
        Long validUserId = user.getId();

        // valid token
        String validToken = user.getToken();

        // when the mock object (userService) is called for getUserById() method with any parameters,
        // then it will return the object "user"
        given(userService.getUserById(validUserId, validToken)).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/users/{userId}", validUserId)
                .header("Authorization", validToken);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.status", is(user.getStatus().toString())))
                .andExpect(jsonPath("$.creation_date", is(user.getCreationDate().toString())))
                .andExpect(jsonPath("$.birthday", is(user.getBirthday().toString())));
    }


    ////////// MAPPING 4 //////////
    @Test
    public void getUserProfile_invalidUserId_404thrown() throws Exception {
        // given
        long invalidUserId = 99; //some random invalid userId
        String errorMessage = "Error: The user with userId " + invalidUserId + " does not exist.";

        ResponseStatusException notFoundException = new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);

        // when the mock object (userService) is called for getUserById() method with an invalid userId,
        // then it will return the object "notFoundException"
        given(userService.getUserById(invalidUserId, "someValidToken")).willThrow(notFoundException);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/users/{userId}", invalidUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "someValidToken");

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isNotFound())
                .andExpect(status().reason(is(errorMessage)));
    }


    ////////// MAPPING 5 //////////
    @Test
    public void updateUserProfile_validUserId_userUpdated() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setPassword("password");
        user.setUsername("testUsername");
        user.setToken("someToken");
        user.setStatus(UserStatus.ONLINE);
        user.setCreationDate(LocalDateTime.of(2023, 3, 5, 15, 59, 59));
        user.setBirthday(LocalDate.of(1999, 12, 31));

        // some random username update
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("someNewUsername");

        // valid userId
        long validUserId = user.getId();
        String newToken = "newTokenWithUpdatedUsername";
        user.setToken(newToken);


        // when the mock object (userService) is called for updateUser() method,
        // then it will return the object "user"
        given(userService.updateUser(Mockito.anyLong(), Mockito.any(), Mockito.anyString())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/users/{userId}", validUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO))
                .header("Authorization", "someToken");
        ;

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent())
                .andExpect(header().string("Authorization", newToken))
                .andExpect(jsonPath("$").doesNotExist());
    }


    ////////// MAPPING 6a //////////
    @Test
    public void updateUserProfile_invalidUserId_404thrown() throws Exception {
        // given
        // some random username update
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("someNewUsername");

        // some random invalid userId
        long invalidUserId = 99;
        String errorMessage = "The user with id " + invalidUserId + " not found";

        ResponseStatusException notFoundException = new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);

        // when the mock object (userService) is called for checkIfUserIdExists() method with an invalid userId,
        // then it will return the object "notFoundException"
        doThrow(notFoundException).when(userService).checkIfUserIdExists(invalidUserId);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/users/{userId}", invalidUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO))
                .header("Authorization", "someToken");
        ;

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isNotFound())
                .andExpect(status().reason(is(errorMessage)));
    }


    ////////// MAPPING 6b //////////
    @Test
    public void updateUserProfile_invalidToken_401thrown() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setPassword("password");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);
        user.setCreationDate(LocalDateTime.of(2023, 3, 5, 15, 59, 59));
        user.setBirthday(LocalDate.of(1999, 12, 31));

        // some random username update
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("someNewUsername");

        // valid userId
        long validUserId = user.getId();


        String errorMessage = "You are unauthorized to perform this action since your provided token is not valid.";
        ResponseStatusException unauthorizedException = new ResponseStatusException(HttpStatus.UNAUTHORIZED, errorMessage);
        doThrow(unauthorizedException).when(userService).updateUser(Mockito.anyLong(), Mockito.any(), Mockito.anyString());

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/users/{userId}", validUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO))
                .header("Authorization", "someToken");

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isUnauthorized())
                .andExpect(status().reason(is(errorMessage)));
    }

    /**
     * Helper Method to convert userPostDTO into a JSON string such that the input
     * can be processed
     * Input will look like this: {"password": "password", "username": "testUsername"}
     *
     * @param object
     * @return string
     */
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e.toString()));
        }
    }
}