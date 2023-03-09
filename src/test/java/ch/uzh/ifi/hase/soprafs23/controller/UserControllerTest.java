package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @MockBean
    private UserRepository userRepository;

    //////////////// REST INTERFACE ///////////////////

    //////////////// MAPPING 1 - POST (no error) ////////////////////////
    @Test
    public void createUser_validInput_userCreated() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setPassword("1234");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);
        user.setTimestamp(LocalDate.parse("2022-02-03"));
        user.setBirthDate(LocalDate.parse("1997-02-03"));

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setPassword("1234");
        userPostDTO.setUsername("testUsername");

        given(userService.createUser(Mockito.any())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.status", is(user.getStatus().toString())))
                .andExpect(jsonPath("$.timestamp", is(user.getTimestamp().toString())))
                .andExpect(jsonPath("$.birthDate", is(user.getBirthDate().toString())));
    }

    //////////////// MAPPING 2 - POST (error) ////////////////////////
    @Test
    public void createUser_invalidInput_userCreated() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setPassword("1234");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setPassword("1234");
        userPostDTO.setUsername("testUsername");

        String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
        given(userService.createUser(Mockito.any())).willThrow(
                new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage, "username", "is")));

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        mockMvc.perform(postRequest)
                .andExpect(status().isConflict());
    }

    //////////////// MAPPING 3 - GET (no error) ////////////////////////
    @Test
    public void givenUser_whenGetUser_thenReturnJsonArray() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setPassword("1234");
        user.setUsername("SomeCoolUsername");
        user.setStatus(UserStatus.OFFLINE);

        // this mocks the UserService -> we define above what the userService should
        // return when getUser() is called
        given(userService.getUser(Mockito.any())).willReturn(user);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users/{id}", user.getId())
                .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
    }

    //////////////// MAPPING 4 - GET (error) ////////////////////////
    @Test
    public void noUser_whenGetUser_thenReturnJsonArray() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setPassword("1234");
        user.setUsername("SomeCoolUsername");
        user.setStatus(UserStatus.OFFLINE);

        // this mocks the UserService -> we define above what the userService should
        // return when getUser() is called
        given(userService.getUser(Mockito.any())).willThrow(
                new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("No user with this id exists!")));

        // when
        MockHttpServletRequestBuilder getRequest = get("/users/{id}", 2).contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest).andExpect(status().isNotFound());
    }

    //////////////// MAPPING 5 - PUT (no error) ////////////////////////
    @Test
    public void updateUser_validInput_userUpdated() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setPassword("1234");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);
        user.setTimestamp(LocalDate.parse("2022-02-03"));
        user.setBirthDate(LocalDate.parse("2022-02-03"));

        // modified
        User modifiedUser = new User();
        modifiedUser.setId(1L);
        modifiedUser.setPassword("1234");
        modifiedUser.setUsername("otherUsername");
        modifiedUser.setToken("1");
        modifiedUser.setStatus(UserStatus.ONLINE);
        modifiedUser.setTimestamp(LocalDate.parse("2022-02-04"));
        modifiedUser.setBirthDate(LocalDate.parse("2022-02-04"));

        // UserPutDTO
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setBirthDate(LocalDate.parse("2022-02-04"));
        userPutDTO.setUsername("otherUsername");

        // mock calls to userRepository
        given(userRepository.findById(Mockito.any())).willReturn(Optional.of(user));
        given(userService.modifyUser(Mockito.any(), Mockito.any())).willReturn(modifiedUser);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/users/{id}", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());
    }

    //////////////// MAPPING 6 - PUT (error) ////////////////////////
    @Test
    public void updateUser_invalidInput_userUpdated() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setPassword("1234");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);
        user.setTimestamp(LocalDate.parse("2022-02-03"));
        user.setBirthDate(LocalDate.parse("2022-02-03"));

        // modified
        User modifiedUser = new User();
        modifiedUser.setId(1L);
        modifiedUser.setPassword("1234");
        modifiedUser.setUsername("testUsername");
        modifiedUser.setToken("1");
        modifiedUser.setStatus(UserStatus.ONLINE);
        modifiedUser.setTimestamp(LocalDate.parse("2022-02-04"));
        modifiedUser.setBirthDate(LocalDate.parse("2022-02-04"));

        // UserPutDTO
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setBirthDate(LocalDate.parse("2022-02-04"));
        userPutDTO.setUsername("otherUsername");

        // mock calls to userRepository
        given(userRepository.findById(Mockito.any())).willReturn(Optional.of(user));
        given(userService.modifyUser(Mockito.any(), Mockito.any()))
                .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("User does not exist")));

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/users/{id}", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isNotFound());
    }

    ////////// OTHER TESTS ////////////////////////////////////////////////////

    @Test
    public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
        // given
        User user = new User();
        user.setPassword("1234");
        user.setUsername("SomeCoolUsername");
        user.setStatus(UserStatus.OFFLINE);

        List<User> allUsers = Collections.singletonList(user);

        // this mocks the UserService -> we define above what the userService should
        // return when getUsers() is called
        given(userService.getUsers()).willReturn(allUsers);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is(user.getUsername())))
                .andExpect(jsonPath("$[0].status", is(user.getStatus().toString())));
    }

    /**
     * Helper Method to convert userPostDTO into a JSON string such that the input
     * can be processed
     * Input will look like this: {"name": "Test User", "username": "testUsername"}
     *
     * @param object
     * @return string
     */
    private String asJsonString(final Object object) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e.toString()));
        }
    }
}