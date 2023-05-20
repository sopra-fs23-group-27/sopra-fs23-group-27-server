package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.AdvancedLobby;
import ch.uzh.ifi.hase.soprafs23.entity.BasicLobby;
import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.rest.dto.AdvancedLobbyCreateDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.BasicLobbyCreateDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs23.service.AuthenticationService;
import ch.uzh.ifi.hase.soprafs23.service.LobbyService;
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

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @MockBean
    private PlayerService playerService;

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
                .andExpect(jsonPath("$.numRounds", is(basicLobby.getNumRounds())))
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
                .andExpect(jsonPath("$.numRounds", is(advancedLobby.getNumRounds())))
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

        Player testPlayer = new Player();
        testPlayer.setPlayerName("testPlayerName");
        testPlayer.setToken(playerToken);
        testPlayer.setPassword("testPassword");


        // when the mock object (lobbyService) is called do nothing
        doNothing().when(lobbyService).checkIfLobbyIsJoinable(Mockito.any(), Mockito.any());
        doNothing().when(authenticationService).addToAuthenticatedJoins(Mockito.any(), Mockito.any());
        doNothing().when(playerService).checkIfPlayerIsAlreadyInLobby(Mockito.anyString());

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
    public void testJoinAdvancedLobby_playerIsAlreadyInLobby_409Exception() throws Exception {
        // given
        String playerToken = "test-token";
        String testLobbyId = "0";
        String errorMessage ="You are already in a lobby. Please leave the lobby to join another one. If the error persists, please close your browser.";

        ResponseStatusException conflictException = new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);


        // when the mock object (lobbyService) is called do nothing
        doNothing().when(lobbyService).checkIfLobbyIsJoinable(Mockito.any(), Mockito.any());
        doNothing().when(authenticationService).addToAuthenticatedJoins(Mockito.any(), Mockito.any());
        doThrow(conflictException).when(playerService).checkIfPlayerIsAlreadyInLobby(Mockito.anyString());

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/lobbies/{lobbyId}/join", testLobbyId)
                .header("Authorization", playerToken);

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isConflict())
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

    @Test
    public void testGetAllPublicLobbies() throws Exception {
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

        AdvancedLobby advancedLobby = new AdvancedLobby();
        advancedLobby.setLobbyId(0L);
        advancedLobby.setLobbyName("testLobbyAdvanced");
        advancedLobby.setHintInterval(4);
        advancedLobby.setNumSecondsUntilHint(10);
        advancedLobby.setNumSeconds(30);
        advancedLobby.setIsPublic(true);
        advancedLobby.setLobbyCreatorPlayerToken(player.getToken());
        advancedLobby.addPlayerToLobby(player.getPlayerName());

        List<Lobby> lobbies = new ArrayList<>();
        lobbies.add(basicLobby);
        lobbies.add(advancedLobby);

        LobbyGetDTO lobbyGetDTO = new LobbyGetDTO();
        lobbyGetDTO.setLobbyName("testLobby");
        lobbyGetDTO.setNumOptions(4);
        lobbyGetDTO.setNumSeconds(30);
        lobbyGetDTO.setIsPublic(true);
        lobbyGetDTO.setLobbyCreatorPlayerToken(player.getToken());

        LobbyGetDTO advancedLobbyGetDTO = new LobbyGetDTO();
        advancedLobbyGetDTO.setLobbyName("testLobbyAdvanced");
        advancedLobbyGetDTO.setHintInterval(4);
        advancedLobbyGetDTO.setNumSecondsUntilHint(10);
        advancedLobbyGetDTO.setNumSeconds(30);
        advancedLobbyGetDTO.setIsPublic(true);
        advancedLobbyGetDTO.setLobbyCreatorPlayerToken(player.getToken());

        List<LobbyGetDTO> lobbyGetDTOs = new ArrayList<>();
        lobbyGetDTOs.add(lobbyGetDTO);
        lobbyGetDTOs.add(advancedLobbyGetDTO);

        given(lobbyService.getAllPublicAndJoinableLobbies()).willReturn(lobbies);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/lobbies")
                .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lobbyId", is(basicLobby.getLobbyId().intValue())))
                .andExpect(jsonPath("$[0].lobbyName", is(basicLobby.getLobbyName())))
                .andExpect(jsonPath("$[0].numOptions", is(basicLobby.getNumOptions())))
                .andExpect(jsonPath("$[0].numSeconds", is(basicLobby.getNumSeconds())))
                .andExpect(jsonPath("$[0].numRounds", is(basicLobby.getNumRounds())))
                .andExpect(jsonPath("$[0].isPublic", is(basicLobby.getIsPublic())))
                .andExpect(jsonPath("$[1].lobbyId", is(advancedLobby.getLobbyId().intValue())))
                .andExpect(jsonPath("$[1].lobbyName", is(advancedLobby.getLobbyName())))
                .andExpect(jsonPath("$[1].hintInterval", is(advancedLobby.getHintInterval())))
                .andExpect(jsonPath("$[1].numSecondsUntilHint", is(advancedLobby.getNumSecondsUntilHint())))
                .andExpect(jsonPath("$[1].numSeconds", is(advancedLobby.getNumSeconds())))
                .andExpect(jsonPath("$[1].numRounds", is(advancedLobby.getNumRounds())))
                .andExpect(jsonPath("$[1].isPublic", is(advancedLobby.getIsPublic())));
    }

    @Test
    public void givenBasicLobby_whenGetLobby_returnJSONArray() throws Exception {
        // given
        String playerToken = "test-token";

        Player player = new Player();
        player.setId(1L);
        player.setPassword("password");
        player.setPlayerName("testPlayerName");
        player.setToken(playerToken);

        BasicLobby lobby = new BasicLobby();
        lobby.setLobbyId(0L);
        lobby.setLobbyName("testLobby");
        lobby.setNumOptions(4);
        lobby.setNumSeconds(30);
        lobby.setIsPublic(true);
        lobby.setLobbyCreatorPlayerToken(player.getToken());
        lobby.addPlayerToLobby(player.getPlayerName());


        given(lobbyService.getLobbyById(lobby.getLobbyId())).willReturn(lobby);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/lobbies/0")
                .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lobbyId", is(lobby.getLobbyId().intValue())))
                .andExpect(jsonPath("$.lobbyName", is(lobby.getLobbyName())))
                .andExpect(jsonPath("$.numOptions", is(lobby.getNumOptions())))
                .andExpect(jsonPath("$.numSeconds", is(lobby.getNumSeconds())))
                .andExpect(jsonPath("$.numRounds", is(lobby.getNumRounds())))
                .andExpect(jsonPath("$.isPublic", is(lobby.getIsPublic())));
    }

    @Test
    public void givenAdvancedLobby_whenGetLobby_returnJSONArray() throws Exception {
        // given
        String playerToken = "test-token";

        Player player = new Player();
        player.setId(1L);
        player.setPassword("password");
        player.setPlayerName("testPlayerName");
        player.setToken(playerToken);

        AdvancedLobby lobby = new AdvancedLobby();
        lobby.setLobbyId(0L);
        lobby.setLobbyName("testLobby");
        lobby.setHintInterval(4);
        lobby.setNumSecondsUntilHint(10);
        lobby.setNumSeconds(30);
        lobby.setIsPublic(true);
        lobby.setLobbyCreatorPlayerToken(player.getToken());
        lobby.addPlayerToLobby(player.getPlayerName());


        given(lobbyService.getLobbyById(lobby.getLobbyId())).willReturn(lobby);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/lobbies/{lobbyId}", lobby.getLobbyId())
                .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lobbyId", is(lobby.getLobbyId().intValue())))
                .andExpect(jsonPath("$.lobbyName", is(lobby.getLobbyName())))
                .andExpect(jsonPath("$.hintInterval", is(lobby.getHintInterval())))
                .andExpect(jsonPath("$.numSecondsUntilHint", is(lobby.getNumSecondsUntilHint())))
                .andExpect(jsonPath("$.numSeconds", is(lobby.getNumSeconds())))
                .andExpect(jsonPath("$.numRounds", is(lobby.getNumRounds())))
                .andExpect(jsonPath("$.isPublic", is(lobby.getIsPublic())));
    }


    @Test
    public void testLeaveAdvancedLobby() throws Exception {
        // given
        String playerToken = "test-token";
        String playerToken2 = "test-token2";
        String playerToken3 = "test-token3";

        Player player1 = new Player();
        player1.setId(1L);
        player1.setPassword("password");
        player1.setPlayerName("testPlayerName1");
        player1.setToken(playerToken);

        Player player2 = new Player();
        player2.setId(2L);
        player2.setPassword("password");
        player2.setPlayerName("testPlayerName2");
        player2.setToken(playerToken2);

        Player player3 = new Player();
        player3.setId(3L);
        player3.setPassword("password");
        player3.setPlayerName("testPlayerName3");
        player3.setToken(playerToken3);

        AdvancedLobby lobby = new AdvancedLobby();
        lobby.setLobbyId(0L);
        lobby.setLobbyName("testLobby");
        lobby.setHintInterval(4);
        lobby.setNumSecondsUntilHint(10);
        lobby.setNumSeconds(30);
        lobby.setIsPublic(true);
        lobby.setLobbyCreatorPlayerToken(player1.getToken());
        lobby.addPlayerToLobby(player1.getPlayerName());
        lobby.addPlayerToLobby(player2.getPlayerName());
        lobby.addPlayerToLobby(player3.getPlayerName());


        // when
        given(lobbyService.getLobbyById(lobby.getLobbyId())).willReturn(lobby);

        lobby.removePlayerFromLobby(player3.getPlayerName());

        given(lobbyService.leaveLobby(lobby, playerToken3)).willReturn(lobby);


        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/lobbies/{lobbyId}/leave", lobby.getLobbyId().toString())
                .header("Authorization", playerToken3);


        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void testStartGameInLobby() throws Exception {
        // given
        String playerToken = "test-token";
        Long testLobbyId = 0L;


        // when the mock object (lobbyService) is called do nothing
        doNothing().when(lobbyService).startGame(testLobbyId, playerToken);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/lobbies/{lobbyId}/start", testLobbyId.toString())
                .header("Authorization", playerToken);


        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
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