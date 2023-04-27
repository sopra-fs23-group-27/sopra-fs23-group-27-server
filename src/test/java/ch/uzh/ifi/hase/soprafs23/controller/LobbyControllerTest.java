package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.AdvancedLobby;
import ch.uzh.ifi.hase.soprafs23.entity.BasicLobby;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.rest.dto.AdvancedLobbyCreateDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.BasicLobbyCreateDTO;
import ch.uzh.ifi.hase.soprafs23.service.AuthenticationService;
import ch.uzh.ifi.hase.soprafs23.service.LobbyService;
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

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Lobby Controller
 * This class is responsible for handling all REST request that are related to
 * the lobby.
 * The controller will receive the request and delegate the execution to the
 * LobbyService and finally return the result.
 */
@WebMvcTest(LobbyController.class)
class LobbyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LobbyService lobbyService;

    @MockBean
    private AuthenticationService authenticationService;

    @Test
    public void testCreatePublicBasicLobby_validInput_lobbyCreated() throws Exception {
        // given
        String playerToken = "test-token";

        Player player = new Player();
        player.setId(1L);
        player.setPassword("password");
        player.setPlayerName("testPlayerName");
        player.setToken(playerToken);

        BasicLobby basicLobby = new BasicLobby();
        basicLobby.setLobbyId(0L);
        basicLobby.setLobbyName("testLobby");
        basicLobby.setNumOptions(4);
        basicLobby.setNumSeconds(30);
        basicLobby.setIsPublic(true);
        basicLobby.setLobbyCreatorPlayerToken(player.getToken());
        basicLobby.addPlayerToLobby(player.getPlayerName());

        BasicLobbyCreateDTO basicLobbyCreateDTO = new BasicLobbyCreateDTO();
        basicLobbyCreateDTO.setLobbyName("testLobby");
        basicLobbyCreateDTO.setNumOptions(4);
        basicLobbyCreateDTO.setNumSeconds(30);
        basicLobbyCreateDTO.setIsPublic(true);

        given(lobbyService.createBasicLobby(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(basicLobby);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/lobbies/basic")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(basicLobbyCreateDTO))
                .header("Authorization", playerToken);

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.lobbyId", is(basicLobby.getLobbyId().intValue())))
                .andExpect(jsonPath("$.lobbyName", is(basicLobby.getLobbyName())))
                .andExpect(jsonPath("$.numOptions", is(basicLobby.getNumOptions())))
                .andExpect(jsonPath("$.numSeconds", is(basicLobby.getNumSeconds())))
                .andExpect(jsonPath("$.isPublic", is(basicLobby.getIsPublic())));
    }

    @Test
    public void testCreatePublicBasicLobby_missingLobbyName_400Exception() throws Exception {
        // given
        BasicLobbyCreateDTO basicLobbyCreateDTO = new BasicLobbyCreateDTO();
        basicLobbyCreateDTO.setNumOptions(4);
        basicLobbyCreateDTO.setNumSeconds(30);
        basicLobbyCreateDTO.setIsPublic(true);

        String playerToken = "test-token";

        String errorMessage = "Lobby's name is missing.";

        ResponseStatusException badRequestException = new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);

        // when the mock object (lobbyService) is called throw exception
        doThrow(badRequestException).when(lobbyService).createBasicLobby(Mockito.any(), Mockito.any(), Mockito.any());

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/lobbies/basic")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(basicLobbyCreateDTO))
                .header("Authorization", playerToken);

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(is(errorMessage)));
    }

    @Test
    public void testCreatePublicBasicLobby_lobbyNameIsTaken_400Exception() throws Exception {
        // given
        BasicLobbyCreateDTO basicLobbyCreateDTO = new BasicLobbyCreateDTO();
        basicLobbyCreateDTO.setLobbyName("testLobby");
        basicLobbyCreateDTO.setNumOptions(4);
        basicLobbyCreateDTO.setNumSeconds(30);
        basicLobbyCreateDTO.setIsPublic(true);

        String playerToken = "test-token";

        String errorMessage = "Lobby's name is already taken.";

        ResponseStatusException badRequestException = new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);

        // when the mock object (lobbyService) is called throw exception
        doThrow(badRequestException).when(lobbyService).createBasicLobby(Mockito.any(), Mockito.any(), Mockito.any());

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/lobbies/basic")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(basicLobbyCreateDTO))
                .header("Authorization", playerToken);

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(is(errorMessage)));
    }

    @Test
    public void testCreatePublicAdvancedLobby_validInput_lobbyCreated() throws Exception {
        // given
        String playerToken = "test-token";

        Player player = new Player();
        player.setId(1L);
        player.setPassword("password");
        player.setPlayerName("testPlayerName");
        player.setToken(playerToken);

        AdvancedLobby advancedLobby = new AdvancedLobby();
        advancedLobby.setLobbyId(0L);
        advancedLobby.setLobbyName("testLobby");
        advancedLobby.setNumSeconds(30);
        advancedLobby.setIsPublic(true);
        advancedLobby.setNumSecondsUntilHint(10);
        advancedLobby.setHintInterval(5);
        advancedLobby.setMaxNumGuesses(3);
        advancedLobby.setLobbyCreatorPlayerToken(player.getToken());
        advancedLobby.addPlayerToLobby(player.getPlayerName());

        AdvancedLobbyCreateDTO advancedLobbyCreateDTO = new AdvancedLobbyCreateDTO();
        advancedLobbyCreateDTO.setLobbyName("testLobby");
        advancedLobbyCreateDTO.setNumSeconds(30);
        advancedLobbyCreateDTO.setNumSecondsUntilHint(10);
        advancedLobbyCreateDTO.setHintInterval(5);
        advancedLobbyCreateDTO.setMaxNumGuesses(3);
        advancedLobbyCreateDTO.setIsPublic(true);

        given(lobbyService.createAdvancedLobby(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(advancedLobby);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/lobbies/advanced")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(advancedLobbyCreateDTO))
                .header("Authorization", playerToken);

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.lobbyId", is(advancedLobby.getLobbyId().intValue())))
                .andExpect(jsonPath("$.lobbyName", is(advancedLobby.getLobbyName())))
                .andExpect(jsonPath("$.numSeconds", is(advancedLobby.getNumSeconds())))
                .andExpect(jsonPath("$.numSecondsUntilHint", is(advancedLobby.getNumSecondsUntilHint())))
                .andExpect(jsonPath("$.hintInterval", is(advancedLobby.getHintInterval())))
                .andExpect(jsonPath("$.maxNumGuesses", is(advancedLobby.getMaxNumGuesses())))
                .andExpect(jsonPath("$.isPublic", is(advancedLobby.getIsPublic())));
    }

    @Test
    public void testCreatePublicAdvancedLobby_missingLobbyName_400Exception() throws Exception {
        // given
        AdvancedLobbyCreateDTO advancedLobbyCreateDTO = new AdvancedLobbyCreateDTO();
        advancedLobbyCreateDTO.setNumSeconds(30);
        advancedLobbyCreateDTO.setNumSecondsUntilHint(10);
        advancedLobbyCreateDTO.setHintInterval(5);
        advancedLobbyCreateDTO.setMaxNumGuesses(3);
        advancedLobbyCreateDTO.setIsPublic(true);

        String playerToken = "test-token";

        String errorMessage = "Lobby's name is missing.";

        ResponseStatusException badRequestException = new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);

        // when the mock object (lobbyService) is called throw exception
        doThrow(badRequestException).when(lobbyService).createBasicLobby(Mockito.any(), Mockito.any(), Mockito.any());

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/lobbies/basic")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(advancedLobbyCreateDTO))
                .header("Authorization", playerToken);

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(is(errorMessage)));
    }

    @Test
    public void testCreatePublicAdvancedLobby_lobbyNameIsTaken_400Exception() throws Exception {
        // given
        AdvancedLobbyCreateDTO advancedLobbyCreateDTO = new AdvancedLobbyCreateDTO();
        advancedLobbyCreateDTO.setLobbyName("testLobby");
        advancedLobbyCreateDTO.setNumSeconds(30);
        advancedLobbyCreateDTO.setNumSecondsUntilHint(10);
        advancedLobbyCreateDTO.setHintInterval(5);
        advancedLobbyCreateDTO.setMaxNumGuesses(3);
        advancedLobbyCreateDTO.setIsPublic(true);

        String playerToken = "test-token";

        String errorMessage = "Lobby's name is already taken.";

        ResponseStatusException badRequestException = new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);

        // when the mock object (lobbyService) is called throw exception
        doThrow(badRequestException).when(lobbyService).createBasicLobby(Mockito.any(), Mockito.any(), Mockito.any());

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/lobbies/basic")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(advancedLobbyCreateDTO))
                .header("Authorization", playerToken);

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(is(errorMessage)));
    }

    @Test
    public void testJoinAdvancedLobby() throws Exception {
        // given
        String playerToken = "test-token";
        String testLobbyId = "0";



        // when the mock object (lobbyService) is called do nothing
        doNothing().when(lobbyService).checkIfLobbyIsJoinable(Mockito.any(), Mockito.any());
        doNothing().when(authenticationService).addToAuthenticatedJoins(Mockito.any(), Mockito.any());

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/lobbies/{lobbyId}/join", testLobbyId)
                .header("Authorization", playerToken);


        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void testJoinAdvancedLobby_invalidLobbyId_404Exception() throws Exception {
        // given
        String playerToken = "test-token";
        String testLobbyId = "0";
        String errorMessage = "Lobby not found.";

        ResponseStatusException notFoundException = new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);


        // when the mock object (lobbyService) is called throw exception
        doThrow(notFoundException).when(lobbyService).checkIfLobbyIsJoinable(Mockito.any(), Mockito.any());
        // when the mock object (lobbyService) is called do nothing
        doNothing().when(authenticationService).addToAuthenticatedJoins(Mockito.any(), Mockito.any());

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/lobbies/{lobbyId}/join", testLobbyId)
                .header("Authorization", playerToken);

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isNotFound())
                .andExpect(status().reason(is(errorMessage)));
    }

    @Test
    public void testJoinAdvancedLobby_lobbyIsNotJoinable_403Exception() throws Exception {
        // given
        String playerToken = "test-token";
        String testLobbyId = "0";
        String errorMessage = "You are trying to join a lobby that has already started a game.";

        ResponseStatusException forbiddenException = new ResponseStatusException(HttpStatus.FORBIDDEN, errorMessage);


        // when the mock object (lobbyService) is called throw exception
        doThrow(forbiddenException).when(lobbyService).checkIfLobbyIsJoinable(Mockito.any(), Mockito.any());
        // when the mock object (lobbyService) is called do nothing
        doNothing().when(authenticationService).addToAuthenticatedJoins(Mockito.any(), Mockito.any());

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/lobbies/{lobbyId}/join", testLobbyId)
                .header("Authorization", playerToken);

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isForbidden())
                .andExpect(status().reason(is(errorMessage)));
    }

    @Test
    public void testJoinAdvancedLobby_privateLobbyWithoutKey_403Exception() throws Exception {
        // given
        String playerToken = "test-token";
        String testLobbyId = "0";
        String errorMessage = "You are trying to join a private lobby. However, the provided lobby key is incorrect.";

        ResponseStatusException forbiddenException = new ResponseStatusException(HttpStatus.FORBIDDEN, errorMessage);


        // when the mock object (lobbyService) is called throw exception
        doThrow(forbiddenException).when(lobbyService).checkIfLobbyIsJoinable(Mockito.any(), Mockito.any());
        // when the mock object (lobbyService) is called do nothing
        doNothing().when(authenticationService).addToAuthenticatedJoins(Mockito.any(), Mockito.any());

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/lobbies/{lobbyId}/join", testLobbyId)
                .header("Authorization", playerToken);

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isForbidden())
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