package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerPutDTO;
import ch.uzh.ifi.hase.soprafs23.service.PlayerService;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * PlayerControllerTest
 * This is a WebMvcTest which allows to test the PlayerController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the PlayerController works.
 */
@WebMvcTest(PlayerController.class)
public class PlayerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlayerService playerService;

    @Test
    public void givenPlayer_whenGetPlayer_thenReturnJsonArray() throws Exception {
        // given
        Player player = new Player();
        player.setPassword("password");
        player.setPlayerName("firstname@lastname");

        // this mocks the PlayerService -> we define above what the playerService should
        // return when getPlayerById() is called
        given(playerService.getPlayerById(anyLong(), anyString())).willReturn(player);

        // when
        MockHttpServletRequestBuilder getRequest = get("/players/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "someToken");

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.playerName", is(player.getPlayerName())));
    }

    ////////// MAPPING 1 //////////
    @Test
    public void createPlayer_validInput_playerCreated() throws Exception {
        // given
        Player player = new Player();
        player.setId(1L);
        player.setPassword("password");
        player.setPlayerName("testPlayerName");
        player.setToken("1");


        // valid token
        String validToken = player.getToken();

        PlayerPostDTO playerPostDTO = new PlayerPostDTO();
        playerPostDTO.setPassword("password");
        playerPostDTO.setPlayerName("testPlayerName");

        given(playerService.createPlayer(Mockito.any())).willReturn(player);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/players")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(header().string("Authorization", validToken))
                .andExpect(jsonPath("$.id", is(player.getId().intValue())))
                .andExpect(jsonPath("$.playerName", is(player.getPlayerName())));
    }


    ////////// MAPPING 2 //////////
    @Test
    public void createPlayer_invalidInput_409thrown() throws Exception {
        // given
        String errorMessage = "Error: The playerName provided is already taken and cannot be used. " +
                "Please select another playerName!";

        ResponseStatusException conflictException = new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);

        PlayerPostDTO playerPostDTO = new PlayerPostDTO();
        playerPostDTO.setPassword("password");
        playerPostDTO.setPlayerName("testPlayerName");

        // when the mock object (playerService) is called for createPlayer() method with any parameters,
        // then it will return the object "conflictException"
        given(playerService.createPlayer(Mockito.any())).willThrow(conflictException);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/players")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isConflict())
                .andExpect(status().reason(is(errorMessage)));
    }


    ////////// MAPPING 3 //////////
    @Test
    public void getPlayerProfile_validInput_playerRetrieved() throws Exception {
        // given
        Player player = new Player();
        player.setId(1L);
        player.setPassword("password");
        player.setPlayerName("testPlayerName");
        player.setToken("1");

        // valid playerId
        Long validPlayerId = player.getId();

        // valid token
        String validToken = player.getToken();

        // when the mock object (playerService) is called for getPlayerById() method with any parameters,
        // then it will return the object "player"
        given(playerService.getPlayerById(validPlayerId, validToken)).willReturn(player);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/players/{playerId}", validPlayerId)
                .header("Authorization", validToken);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(player.getId().intValue())))
                .andExpect(jsonPath("$.playerName", is(player.getPlayerName())));
    }


    ////////// MAPPING 4 //////////
    @Test
    public void getPlayerProfile_invalidPlayerId_404thrown() throws Exception {
        // given
        long invalidPlayerId = 0; //some random invalid playerId
        String errorMessage = "Error: The player with playerId " + invalidPlayerId + " does not exist.";

        ResponseStatusException notFoundException = new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);

        // when the mock object (playerService) is called for getPlayerById() method with an invalid playerId,
        // then it will return the object "notFoundException"
        given(playerService.getPlayerById(invalidPlayerId, "someValidToken")).willThrow(notFoundException);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/players/{playerId}", invalidPlayerId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "someValidToken");

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isNotFound())
                .andExpect(status().reason(is(errorMessage)));
    }


    ////////// MAPPING 5 //////////
    @Test
    public void updatePlayerProfile_validPlayerId_playerUpdated() throws Exception {
        // given
        Player player = new Player();
        player.setId(1L);
        player.setPassword("password");
        player.setPlayerName("testPlayerName");
        player.setToken("someToken");

        // some random playerName update
        PlayerPutDTO playerPutDTO = new PlayerPutDTO();
        playerPutDTO.setPlayerName("someNewPlayerName");

        // valid playerId
        long validPlayerId = player.getId();
        String newToken = "newTokenWithUpdatedPlayerName";
        player.setToken(newToken);


        // when the mock object (playerService) is called for updatePlayer() method,
        // then it will return the object "player"
        given(playerService.updatePlayer(Mockito.anyLong(), Mockito.any(), Mockito.anyString())).willReturn(player);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/players/{playerId}", validPlayerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerPutDTO))
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
    public void updatePlayerProfile_invalidPlayerId_404thrown() throws Exception {
        // given
        // some random playerName update
        PlayerPutDTO playerPutDTO = new PlayerPutDTO();
        playerPutDTO.setPlayerName("someNewPlayerName");

        // some random invalid playerId
        long invalidPlayerId = 99;
        String errorMessage = "The player with id " + invalidPlayerId + " not found";

        ResponseStatusException notFoundException = new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);

        // when the mock object (playerService) is called for checkIfPlayerIdExists() method with an invalid playerId,
        // then it will return the object "notFoundException"
        doThrow(notFoundException).when(playerService).checkIfPlayerIdExists(invalidPlayerId);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/players/{playerId}", invalidPlayerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerPutDTO))
                .header("Authorization", "someToken");
        ;

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isNotFound())
                .andExpect(status().reason(is(errorMessage)));
    }


    ////////// MAPPING 6b //////////
    @Test
    public void updatePlayerProfile_invalidToken_401thrown() throws Exception {
        // given
        Player player = new Player();
        player.setId(1L);
        player.setPassword("password");
        player.setPlayerName("testPlayerName");
        player.setToken("1");

        // some random playerName update
        PlayerPutDTO playerPutDTO = new PlayerPutDTO();
        playerPutDTO.setPlayerName("someNewPlayerName");

        // valid playerId
        long validPlayerId = player.getId();


        String errorMessage = "You are unauthorized to perform this action since your provided token is not valid.";
        ResponseStatusException unauthorizedException = new ResponseStatusException(HttpStatus.UNAUTHORIZED, errorMessage);
        doThrow(unauthorizedException).when(playerService).updatePlayer(Mockito.anyLong(), Mockito.any(), Mockito.anyString());

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/players/{playerId}", validPlayerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerPutDTO))
                .header("Authorization", "someToken");

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isUnauthorized())
                .andExpect(status().reason(is(errorMessage)));
    }

    /**
     * Helper Method to convert playerPostDTO into a JSON string such that the input
     * can be processed
     * Input will look like this: {"password": "password", "playerName": "testPlayerName"}
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