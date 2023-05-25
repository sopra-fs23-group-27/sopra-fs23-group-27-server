package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerPutDTO;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
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

    @MockBean
    private LobbyService lobbyService;

    @Test
    public void givenPlayer_whenGetPlayer_thenReturnJsonArray() throws Exception {
        // given
        Player player = new Player();
        player.setPassword("password");
        player.setPlayerName("firstname@lastname");
        player.setToken("validToken");

        // this mocks the PlayerService -> we define above what the playerService should
        // return when getPlayerById() is called
        given(playerService.getPlayerById(anyLong(), anyString())).willReturn(player);

        // when
        MockHttpServletRequestBuilder getRequest = get("/players/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "validToken");

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.playerName", is(player.getPlayerName())));
    }

    @Test
    public void givenPlayers_whenGetPlayers_thenReturnJsonArray() throws Exception {
        // given
        Player player = new Player();
        player.setPassword("password");
        player.setPlayerName("firstname@lastname");
        player.setToken("validToken");

        // given
        Player player2 = new Player();
        player2.setPassword("password2");
        player2.setPlayerName("firstname@lastname2");
        player2.setToken("validToken2");

        List<Player> allPlayers = new ArrayList<Player>();
        allPlayers.add(player);
        allPlayers.add(player2);

        // this mocks the PlayerService -> we define above what the playerService should
        // return when getPlayerById() is called
        given(playerService.getPlayers(anyString())).willReturn(allPlayers);

        // when
        MockHttpServletRequestBuilder getRequest = get("/players")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "validToken");

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].playerName", is(player.getPlayerName())))
                .andExpect(jsonPath("$[1].playerName", is(player2.getPlayerName())));
    }

    @Test
    public void getPlayer_401thrown() throws Exception {
        // given
        Player player = new Player();
        player.setPassword("password");
        player.setPlayerName("firstname@lastname");
        player.setToken("validToken");

        // mock playerservice
        String errorMessage = "You are unauthorized to perform this action since your provided token is not valid.";
        ResponseStatusException unauthorizedException = new ResponseStatusException(HttpStatus.UNAUTHORIZED, errorMessage);
        doThrow(unauthorizedException).when(playerService).getPlayerById(anyLong(), anyString());

        // when
        MockHttpServletRequestBuilder getRequest = get("/players/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "invalidToken");

        // then
        mockMvc.perform(getRequest).andExpect(status().isUnauthorized());
    }

    @Test
    public void getPlayer_404thrown() throws Exception {
        // given
        Player player = new Player();
        player.setPassword("password");
        player.setPlayerName("firstname@lastname");
        player.setToken("validToken");

        // mock playerservice
        String errorMessage = "The player with playerId 0 does not exist.";
        ResponseStatusException notFoundException = new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);
        doThrow(notFoundException).when(playerService).getPlayerById(anyLong(), anyString());

        // when
        MockHttpServletRequestBuilder getRequest = get("/players/0")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "validToken");

        // then
        mockMvc.perform(getRequest).andExpect(status().isNotFound());
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
        String errorMessage = "The playerName provided is already taken and cannot be used. " +
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
        String errorMessage = "The player with playerId " + invalidPlayerId + " does not exist.";

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
    public void updatePlayerProfile_validPlayerId_playerUpdatedNewPlayerName() throws Exception {
        // given
        Player player = new Player();
        player.setId(1L);
        player.setPassword("password");
        player.setPlayerName("testPlayerName");
        player.setToken("someToken");

        // some random playerName update
        PlayerPutDTO playerPutDTO = new PlayerPutDTO();
        playerPutDTO.setPlayerName("someNewPlayerName");

        // updated player
        Player updatedPlayer = new Player();
        updatedPlayer.setId(1L);
        updatedPlayer.setPassword("password");
        updatedPlayer.setPlayerName("someNewPlayerName");
        updatedPlayer.setToken("newTokenWithUpdatedPlayerName");

        // valid playerId
        long validPlayerId = player.getId();
        String newToken = updatedPlayer.getToken();

        // when the mock object (playerService) is called for updatePlayer() method,
        // then it will return the object "player"
        given(playerService.updatePlayer(Mockito.anyLong(), Mockito.any(), Mockito.anyString())).willReturn(updatedPlayer);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/players/{playerId}", validPlayerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerPutDTO))
                .header("Authorization", player.getToken());

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", newToken))
                .andExpect(jsonPath("$.id", is(player.getId().intValue())))
                .andExpect(jsonPath("$.playerName", is(updatedPlayer.getPlayerName())));
    }

    @Test
    public void updatePlayerProfile_validPlayerId_playerUpdatedNewPassword() throws Exception {
        // given
        // given
        Player player = new Player();
        player.setId(1L);
        player.setPassword("password");
        player.setPlayerName("testPlayerName");
        player.setToken("someToken");

        // some random playerName update
        PlayerPutDTO playerPutDTO = new PlayerPutDTO();
        playerPutDTO.setPassword("someNewPassword");

        // updated player
        Player updatedPlayer = new Player();
        updatedPlayer.setId(1L);
        updatedPlayer.setPassword("someNewPassword");
        updatedPlayer.setPlayerName("testPlayerName");
        updatedPlayer.setToken("newTokenWithUpdatedPassword");

        // valid playerId
        long validPlayerId = player.getId();
        String newToken = updatedPlayer.getToken();

        // when the mock object (playerService) is called for updatePlayer() method,
        // then it will return the object "player"
        given(playerService.updatePlayer(Mockito.anyLong(), Mockito.any(), Mockito.anyString())).willReturn(updatedPlayer);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/players/{playerId}", validPlayerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerPutDTO))
                .header("Authorization", player.getToken());

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", newToken))
                .andExpect(jsonPath("$.id", is(player.getId().intValue())))
                .andExpect(jsonPath("$.playerName", is(updatedPlayer.getPlayerName())));
    }

    @Test
    public void updatePlayerProfile_validPlayerId_playerUpdatedNewPlayerNameAndPassword() throws Exception {
        // given
        // given
        Player player = new Player();
        player.setId(1L);
        player.setPassword("password");
        player.setPlayerName("testPlayerName");
        player.setToken("someToken");

        // some random playerName update
        PlayerPutDTO playerPutDTO = new PlayerPutDTO();
        playerPutDTO.setPlayerName("someNewPlayerName");
        playerPutDTO.setPassword("someNewPassword");

        // updated player
        Player updatedPlayer = new Player();
        updatedPlayer.setId(1L);
        updatedPlayer.setPassword("someNewPassword");
        updatedPlayer.setPlayerName("someNewPlayerName");
        updatedPlayer.setToken("newTokenWithUpdatedPlayerNameAndPassword");

        // valid playerId
        long validPlayerId = player.getId();
        String newToken = updatedPlayer.getToken();

        // when the mock object (playerService) is called for updatePlayer() method,
        // then it will return the object "player"
        given(playerService.updatePlayer(Mockito.anyLong(), Mockito.any(), Mockito.anyString())).willReturn(updatedPlayer);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/players/{playerId}", validPlayerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerPutDTO))
                .header("Authorization", player.getToken());

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", newToken))
                .andExpect(jsonPath("$.id", is(player.getId().intValue())))
                .andExpect(jsonPath("$.playerName", is(updatedPlayer.getPlayerName())));
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

    @Test
    public void updatePlayerProfile_takenPlayerName_409thrown() throws Exception {
        // given
        // some random valid playerId
        long invalidPlayerId = 1;

        // some random playerName that is already taken
        PlayerPutDTO playerPutDTO = new PlayerPutDTO();
        playerPutDTO.setPlayerName("someTakenPlayerName");

        String errorMessage = "The playerName provided is already taken and cannot be used. " +
                "Please select another playerName!";

        ResponseStatusException conflictException = new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);

        // when the mock object (playerService) is called for checkIfPlayerIdExists() method with an invalid playerId,
        // then it will return the object "notFoundException"
        doThrow(conflictException).when(playerService).checkIfPlayerIdExists(invalidPlayerId);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/players/{playerId}", invalidPlayerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerPutDTO))
                .header("Authorization", "someToken");

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isConflict())
                .andExpect(status().reason(is(errorMessage)));
    }

    @Test
    public void registerPlayer_validInput_playerRegistred() throws Exception {
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

        given(playerService.registerPlayer(Mockito.any())).willReturn(player);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(header().string("Authorization", validToken))
                .andExpect(jsonPath("$.id", is(player.getId().intValue())))
                .andExpect(jsonPath("$.playerName", is(player.getPlayerName())));
    }

    @Test
    public void registerPlayer_invalidInput_409thrown() throws Exception {
        // given
        String errorMessage = "The playerName provided is already taken and cannot be used. " +
                "Please select another playerName!";

        ResponseStatusException conflictException = new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);

        PlayerPostDTO playerPostDTO = new PlayerPostDTO();
        playerPostDTO.setPassword("password");
        playerPostDTO.setPlayerName("testPlayerName");

        // when the mock object (playerService) is called for createPlayer() method with any parameters,
        // then it will return the object "conflictException"
        given(playerService.registerPlayer(Mockito.any())).willThrow(conflictException);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isConflict())
                .andExpect(status().reason(is(errorMessage)));
    }

    @Test
    public void loginPlayer_validInput() throws Exception {
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

        given(playerService.loginPlayer(Mockito.any())).willReturn(player);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", validToken))
                .andExpect(jsonPath("$.id", is(player.getId().intValue())))
                .andExpect(jsonPath("$.playerName", is(player.getPlayerName())));
    }

    @Test
    public void loginPlayer_invalidInput_401thrown() throws Exception {
        // given
        String errorMessage = "The password provided is not correct. Please try again.";

        ResponseStatusException unauthorizedException = new ResponseStatusException(HttpStatus.UNAUTHORIZED, errorMessage);

        PlayerPostDTO playerPostDTO = new PlayerPostDTO();
        playerPostDTO.setPassword("wrongPassword");
        playerPostDTO.setPlayerName("testPlayerName");

        given(playerService.loginPlayer(Mockito.any())).willThrow(unauthorizedException);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isUnauthorized())
                .andExpect(status().reason(is(errorMessage)));
    }

    @Test
    public void loginPlayer_invalidInput_404thrown() throws Exception {
        // given
        String errorMessage = "The playerName provided does not exist. Please register first.";

        ResponseStatusException notFoundException = new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);

        PlayerPostDTO playerPostDTO = new PlayerPostDTO();
        playerPostDTO.setPassword("Password");
        playerPostDTO.setPlayerName("testPlayerName");

        given(playerService.loginPlayer(Mockito.any())).willThrow(notFoundException);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isNotFound())
                .andExpect(status().reason(is(errorMessage)));
    }

    @Test
    public void logoutPlayer_validInput_notInLobby() throws Exception {
        // given
        Player player = new Player();
        player.setId(1L);
        player.setPassword("password");
        player.setPlayerName("testPlayerName");
        player.setToken("1");
        player.setPermanent(false);

        // valid token
        Long validPlayerId = player.getId();
        String validToken = player.getToken();

        // when
        when(playerService.getPlayerById(validPlayerId, validToken)).thenReturn(player);
        doNothing().when(playerService).deletePlayer(validPlayerId, validToken);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/players/{playerId}/logout", validPlayerId)
                .header("Authorization", validToken);

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isNoContent());
    }

    @Test
    public void logoutPlayer_validInput_inLobby() throws Exception {
        // given
        Player player = new Player();
        player.setId(1L);
        player.setPassword("password");
        player.setPlayerName("testPlayerName");
        player.setToken("1");
        player.setLobbyId(1L);

        // valid token
        Long validPlayerId = player.getId();
        String validToken = player.getToken();

        // when
        when(playerService.getPlayerById(validPlayerId, validToken)).thenReturn(player);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/players/{playerId}/logout", validPlayerId)
                .header("Authorization", validToken);

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isNoContent());
    }

    @Test
    public void logoutPlayer_invalidInput_401thrown() throws Exception {
        // given
        Player player = new Player();
        player.setId(1L);
        player.setPassword("password");
        player.setPlayerName("testPlayerName");
        player.setToken("1");

        // valid token
        Long validPlayerId = 1L;
        String invalidToken = "invalidToken";

        // given
        String errorMessage = "You are unauthorized to perform this action since your provided token is not valid.";

        ResponseStatusException unauthorizedException = new ResponseStatusException(HttpStatus.UNAUTHORIZED, errorMessage);

        Mockito.doThrow(unauthorizedException).when(playerService).prepareLogoutPlayer(Mockito.anyLong(), Mockito.any());

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/players/{playerId}/logout", validPlayerId)
                .header("Authorization", invalidToken);

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isUnauthorized())
                .andExpect(status().reason(is(errorMessage)));
    }

    @Test
    public void logoutPlayer_invalidInput_404thrown() throws Exception {
        // given
        Player player = new Player();
        player.setId(1L);
        player.setPassword("password");
        player.setPlayerName("testPlayerName");
        player.setToken("1");

        // valid token
        Long invalidPlayerId = 0L;
        String validToken = player.getToken();

        // given
        String errorMessage = "The player with playerId " + invalidPlayerId + " does not exist.";

        ResponseStatusException notFoundException = new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);

        Mockito.doThrow(notFoundException).when(playerService).prepareLogoutPlayer(Mockito.anyLong(), Mockito.any());

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/players/{playerId}/logout", invalidPlayerId)
                .header("Authorization", validToken);

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isNotFound())
                .andExpect(status().reason(is(errorMessage)));
    }

    @Test
    public void deletePlayer_validInput() throws Exception {
        // given
        Player player = new Player();
        player.setId(1L);
        player.setPassword("password");
        player.setPlayerName("testPlayerName");
        player.setToken("1");

        // valid token
        Long validPlayerId = player.getId();
        String validToken = player.getToken();

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = delete("/players/{playerId}", validPlayerId)
                .header("Authorization", validToken);

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isNoContent());
    }

    @Test
    public void deletePlayer_invalidInput_401thrown() throws Exception {
        // given
        Player player = new Player();
        player.setId(1L);
        player.setPassword("password");
        player.setPlayerName("testPlayerName");
        player.setToken("1");

        // valid token
        Long validPlayerId = 1L;
        String invalidToken = "invalidToken";

        // given
        String errorMessage = "You are unauthorized to perform this action since your provided token is not valid.";

        ResponseStatusException unauthorizedException = new ResponseStatusException(HttpStatus.UNAUTHORIZED, errorMessage);

        Mockito.doThrow(unauthorizedException).when(playerService).deletePlayer(Mockito.anyLong(), Mockito.anyString());

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = delete("/players/{playerId}", validPlayerId)
                .header("Authorization", invalidToken);

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isUnauthorized())
                .andExpect(status().reason(is(errorMessage)));
    }

    @Test
    public void deletePlayer_invalidInput_404thrown() throws Exception {
        // given
        Player player = new Player();
        player.setId(1L);
        player.setPassword("password");
        player.setPlayerName("testPlayerName");
        player.setToken("1");

        // valid token
        Long invalidPlayerId = 0L;
        String validToken = player.getToken();

        // given
        String errorMessage = "The player with playerId " + invalidPlayerId + " does not exist.";

        ResponseStatusException notFoundException = new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);

        Mockito.doThrow(notFoundException).when(playerService).deletePlayer(Mockito.anyLong(), Mockito.anyString());

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = delete("/players/{playerId}", invalidPlayerId)
                .header("Authorization", validToken);

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isNotFound())
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