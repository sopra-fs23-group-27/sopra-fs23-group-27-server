package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerPutDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
<<<<<<< HEAD
import org.mockito.Spy;
=======
>>>>>>> main
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
<<<<<<< HEAD
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
=======
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
>>>>>>> main

public class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private LobbyRepository lobbyRepository;

    @InjectMocks
    private PlayerService playerService;

    private Player testPlayer;
    private Player updatedTestPlayer;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // given
        testPlayer = new Player();
        testPlayer.setId(1L);
        testPlayer.setPassword("password");
        testPlayer.setPlayerName("testPlayerName");

    }

    @Test
    public void getPlayerByToken_success(){
        // given
        testPlayer.setToken("testToken");

        // mock playerRepository
        when(playerRepository.findByToken(Mockito.anyString())).thenReturn(testPlayer);

        // call method to be tested
        Player foundPlayer = playerService.getPlayerByToken("testToken");

        // then testPlayer and foundPlayer should be the same
        assertEquals(foundPlayer.getId(), testPlayer.getId());
        assertEquals(foundPlayer.getPlayerName(), testPlayer.getPlayerName());
        assertEquals(foundPlayer.getPassword(), testPlayer.getPassword());
        assertEquals(foundPlayer.getToken(), testPlayer.getToken());
    }

    @Test
    public void getPlayerByWsConnectionId_success(){
        // given
        testPlayer.setWsConnectionId("testWsConnectionId");

        // mock playerRepository
        when(playerRepository.findByWsConnectionId(Mockito.anyString())).thenReturn(testPlayer);

        // call method to be tested
        Player foundPlayer = playerService.getPlayerByWsConnectionId("testWsConnectionId");

        // then testPlayer and foundPlayer should be the same
        assertEquals(foundPlayer.getId(), testPlayer.getId());
        assertEquals(foundPlayer.getPlayerName(), testPlayer.getPlayerName());
        assertEquals(foundPlayer.getPassword(), testPlayer.getPassword());
        assertEquals(foundPlayer.getWsConnectionId(), testPlayer.getWsConnectionId());
    }

    @Test
    public void registerPlayer_validInputs_success() {
        // when -> any object is being save in the playerRepository -> return the dummy
        // testPlayer
        when(playerRepository.save(Mockito.any())).thenReturn(testPlayer);

        // when -> any object is being save in the playerRepository -> return the dummy
        // testPlayer
        Player createdPlayer = playerService.registerPlayer(testPlayer);

        // mock player
        Mockito.verify(playerRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(testPlayer.getId(), createdPlayer.getId());
        assertEquals(testPlayer.getPassword(), createdPlayer.getPassword());
        assertEquals(testPlayer.getPlayerName(), createdPlayer.getPlayerName());
        assertNotNull(createdPlayer.getToken());
    }

    @Test
    public void registerPlayer_409thrown() {
        // given -> a first player has already been created
        playerService.registerPlayer(testPlayer);

        // when -> setup additional mocks for PlayerRepository
        when(playerRepository.findByPlayerName(Mockito.any())).thenReturn(testPlayer);

        // then -> attempt to create second player with same player -> check that an error
        // is thrown
        assertThrows(ResponseStatusException.class, () -> playerService.registerPlayer(testPlayer));
    }

    @Test
    public void createPlayer_validInputs_success() {
        // when -> any object is being save in the playerRepository -> return the dummy
        // testPlayer
        when(playerRepository.save(Mockito.any())).thenReturn(testPlayer);

        // when -> any object is being save in the playerRepository -> return the dummy
        // testPlayer
        Player createdPlayer = playerService.createPlayer(testPlayer);

        // then
        Mockito.verify(playerRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(testPlayer.getId(), createdPlayer.getId());
        assertEquals(testPlayer.getPassword(), createdPlayer.getPassword());
        assertEquals(testPlayer.getPlayerName(), createdPlayer.getPlayerName());
        assertNotNull(createdPlayer.getToken());
    }

    @Test
    public void createPlayer_duplicateName_throwsException() {
        // given -> a first player has already been created
        playerService.createPlayer(testPlayer);

        // when -> setup additional mocks for PlayerRepository
        when(playerRepository.findByPlayerName(Mockito.any())).thenReturn(testPlayer);

        // then -> attempt to create second player with same player -> check that an error
        // is thrown
        assertThrows(ResponseStatusException.class, () -> playerService.createPlayer(testPlayer));
    }

    @Test
    public void createPlayer_duplicateInputs_throwsException() {
        // given -> a first player has already been created
        playerService.createPlayer(testPlayer);

        // when -> setup additional mocks for PlayerRepository
        //Mockito.when(playerRepository.findByName(Mockito.any())).thenReturn(testPlayer);
        when(playerRepository.findByPlayerName(Mockito.any())).thenReturn(testPlayer);

        // then -> attempt to create second player with same player -> check that an error
        // is thrown
        assertThrows(ResponseStatusException.class, () -> playerService.createPlayer(testPlayer));
    }

<<<<<<< HEAD
    

=======
    @Test void updatePlayer_updateUsernameAndPassword_validInputs_success() {
        // given
        testPlayer.setPermanent(false);
        testPlayer.setToken("validToken");
        String validToken = testPlayer.getToken();

        updatedTestPlayer = new Player();
        updatedTestPlayer.setId(1L);
        updatedTestPlayer.setPassword("newPassword");
        updatedTestPlayer.setPlayerName("newPlayerName");
        updatedTestPlayer.setPermanent(true);
        updatedTestPlayer.setToken("newValidToken");

        PlayerPutDTO playerUpdateRequest = new PlayerPutDTO();
        playerUpdateRequest.setPlayerName("newPlayerName");
        playerUpdateRequest.setPassword("newPassword");

        // mock playerRepository
        when(playerRepository.findById(Mockito.anyLong())).thenReturn(testPlayer);
        when(playerRepository.save(Mockito.any())).thenReturn(updatedTestPlayer);

        // call method to be tested
        Player updatedPlayer = playerService.updatePlayer(testPlayer.getId(), playerUpdateRequest, validToken);

        // then
        Mockito.verify(playerRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(playerUpdateRequest.getPlayerName(), updatedPlayer.getPlayerName());
        assertEquals(playerUpdateRequest.getPassword(), updatedPlayer.getPassword());
        assertEquals(testPlayer.getId(), updatedPlayer.getId());
        assertTrue(updatedPlayer.isPermanent());
        assertNotNull(updatedPlayer.getToken());
    }

    @Test void updatePlayer_updateOnlyPlayername_validInputs_success() {
        // given
        testPlayer.setPermanent(false);
        testPlayer.setToken("validToken");
        String validToken = testPlayer.getToken();

        updatedTestPlayer = new Player();
        updatedTestPlayer.setId(1L);
        updatedTestPlayer.setPassword("password");
        updatedTestPlayer.setPlayerName("newPlayerName");
        updatedTestPlayer.setPermanent(true);
        updatedTestPlayer.setToken("newValidToken");

        PlayerPutDTO playerUpdateRequest = new PlayerPutDTO();
        playerUpdateRequest.setPlayerName("newPlayerName");

        // mock playerRepository
        when(playerRepository.findById(Mockito.anyLong())).thenReturn(testPlayer);
        when(playerRepository.save(Mockito.any())).thenReturn(updatedTestPlayer);

        // call method to be tested
        Player updatedPlayer = playerService.updatePlayer(testPlayer.getId(), playerUpdateRequest, validToken);

        // then
        Mockito.verify(playerRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(playerUpdateRequest.getPlayerName(), updatedPlayer.getPlayerName());
        assertEquals(testPlayer.getPassword(), updatedPlayer.getPassword());
        assertEquals(testPlayer.getId(), updatedPlayer.getId());
        assertTrue(updatedPlayer.isPermanent());
        assertNotNull(updatedPlayer.getToken());
    }

    @Test void updatePlayer_updateOnlyPassword_validInputs_success() {
        // given
        testPlayer.setPermanent(false);
        testPlayer.setToken("validToken");
        String validToken = testPlayer.getToken();

        updatedTestPlayer = new Player();
        updatedTestPlayer.setId(1L);
        updatedTestPlayer.setPassword("newPassword");
        updatedTestPlayer.setPlayerName("testPlayerName");
        updatedTestPlayer.setPermanent(true);
        updatedTestPlayer.setToken("newValidToken");

        PlayerPutDTO playerUpdateRequest = new PlayerPutDTO();
        playerUpdateRequest.setPassword("newPassword");

        // mock playerRepository
        when(playerRepository.findById(Mockito.anyLong())).thenReturn(testPlayer);
        when(playerRepository.save(Mockito.any())).thenReturn(updatedTestPlayer);

        // call method to be tested
        Player updatedPlayer = playerService.updatePlayer(testPlayer.getId(), playerUpdateRequest, validToken);

        // then
        Mockito.verify(playerRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(playerUpdateRequest.getPassword(), updatedPlayer.getPassword());
        assertEquals(testPlayer.getPlayerName(), updatedPlayer.getPlayerName());
        assertEquals(testPlayer.getId(), updatedPlayer.getId());
        assertTrue(updatedPlayer.isPermanent());
        assertNotNull(updatedPlayer.getToken());
    }

    @Test void updatePlayer_updateUsernameAndPassword_401thrown() {
        // given
        testPlayer.setPermanent(false);
        testPlayer.setToken("validToken");
        String invalidToken = "invalidToken";

        updatedTestPlayer = new Player();
        updatedTestPlayer.setId(1L);
        updatedTestPlayer.setPassword("newPassword");
        updatedTestPlayer.setPlayerName("newPlayerName");
        updatedTestPlayer.setPermanent(true);
        updatedTestPlayer.setToken("newValidToken");

        PlayerPutDTO playerUpdateRequest = new PlayerPutDTO();
        playerUpdateRequest.setPlayerName("newPlayerName");
        playerUpdateRequest.setPassword("newPassword");

        // mock playerRepository
        when(playerRepository.findById(Mockito.anyLong())).thenReturn(testPlayer);

        assertThrows(ResponseStatusException.class, () -> playerService.updatePlayer(testPlayer.getId(), playerUpdateRequest, invalidToken));

    }

    @Test
    public void loginPlayer_validInputs_success() {
        // mock playerRepository
        when(playerRepository.findByPlayerName(Mockito.any())).thenReturn(testPlayer);
        when(playerRepository.save(Mockito.any())).thenReturn(testPlayer);

        // call method to be tested
        Player loggedInPlayer = playerService.loginPlayer(testPlayer);

        // then
        assertEquals(testPlayer.getPlayerName(), loggedInPlayer.getPlayerName());
        assertEquals(testPlayer.getPassword(), loggedInPlayer.getPassword());
        assertEquals(testPlayer.getId(), loggedInPlayer.getId());
        assertNotNull(loggedInPlayer.getToken());
    }

    @Test
    public void loginPlayer_404thrown() {
        // mock playerRepository
        when(playerRepository.findByPlayerName(Mockito.any())).thenReturn(null);

        // then
        assertThrows(ResponseStatusException.class, () -> playerService.loginPlayer(testPlayer));
    }

    @Test
    public void loginPlayer_403thrown() {
        // given
        Player playerToLogIn = new Player();
        playerToLogIn.setId(1L);
        playerToLogIn.setPassword("wrongPassword");
        playerToLogIn.setPlayerName("testPlayerName");

        // mock playerRepository
        when(playerRepository.findByPlayerName(Mockito.any())).thenReturn(testPlayer);

        assertThrows(ResponseStatusException.class, () -> playerService.loginPlayer(playerToLogIn));
    }

    @Test
    public void checkIfPlayerIdExists_success() {
        // when
        when(playerRepository.findById(Mockito.anyLong())).thenReturn(testPlayer);

        // then
        assertDoesNotThrow(() -> playerService.checkIfPlayerIdExists(testPlayer.getId()));
    }

    @Test
    public void checkIfPlayerIdExists_404thrown() {
        // when -> setup additional mocks for PlayerRepository
        when(playerRepository.findById(Mockito.any())).thenReturn(null);

        // then -> attempt to find non-existent player -> check that an error is thrown
        assertThrows(ResponseStatusException.class, ()  -> playerService.checkIfPlayerIdExists(testPlayer.getId()));
    }
>>>>>>> main
}
