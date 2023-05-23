package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.exceptions.PlayerServiceException;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.incoming.AuthenticateDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing.WSConnectedDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;

class AuthenticationServiceTest {

    @Spy
    @InjectMocks
    private AuthenticationService authenticationService;
    @Mock
    private LobbyService lobbyService;
    @Mock
    private WebSocketService webSocketService;
    @Mock
    private PlayerService playerService;
    @Mock
    private GameService gameService;

    @InjectMocks
    private Player testPlayer;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddToAuthenticatedJoins() {
        String playerToken = "test-token";
        Long lobbyId = 0L;
        authenticationService.addToAuthenticatedJoins(playerToken, lobbyId);

        Map<String, Long> expectedAuthenticatedJoins = new HashMap<>();

        expectedAuthenticatedJoins.put(playerToken, lobbyId);
        assertEquals(expectedAuthenticatedJoins, authenticationService.getAuthenticatedJoins());
    }

    @Test
    void testGetLobbyIdFromAuthToken() {
        String playerToken = "test-player-token";
        Long lobbyId = 0L;
        authenticationService.addToAuthenticatedJoins(playerToken, lobbyId);

        Long result = authenticationService.getLobbyIdFromAuthToken(playerToken);
        assertEquals(lobbyId, result);
    }

    @Test
    void testGetLobbyIdFromAuthTokenTokenNotIncluded() {
        String playerToken = "test-player-token";
        Long lobbyId = 0L;
        authenticationService.addToAuthenticatedJoins(playerToken, lobbyId);

        Long result = authenticationService.getLobbyIdFromAuthToken("false-test-token");
        assertNull(result);
    }

    @Test
    void testRemoveFromAuthenticatedJoins() {
        String playerToken = "test-player-token";
        Long lobbyId = 0L;
        authenticationService.addToAuthenticatedJoins(playerToken, lobbyId);

        // test if the entry is in the map
        Map<String, Long> expectedAuthenticatedJoins = new HashMap<>();
        expectedAuthenticatedJoins.put(playerToken, lobbyId);
        assertEquals(expectedAuthenticatedJoins, authenticationService.getAuthenticatedJoins());

        // test if the entry is removed from the map
        authenticationService.removeFromAuthenticatedJoins(playerToken);
        assertTrue(authenticationService.getAuthenticatedJoins().isEmpty());
    }

    @Test
    public void testAuthenticateAndJoinLobby_throwsException() {
        // return null when called with the argument "false-player-token"
        Mockito.when(playerService.getPlayerByToken("false-player-token")).thenReturn(null);

        // check if authenticationService.authenticateAndJoinLobby throws a PlayerServiceException
        assertThrows(PlayerServiceException.class, () -> authenticationService.authenticateAndJoinLobby("test-websocket-key", "false-player-token"));
    }

    @Test
    void testAuthenticateAndJoinLobby() throws InterruptedException {
        // set up
        Long lobbyId = 0L;
        WSConnectedDTO wsConnectedDTO = new WSConnectedDTO("testPlayer", lobbyId);

        testPlayer = new Player();
        testPlayer.setPlayerName("testPlayer");
        testPlayer.setCreator(false);
        testPlayer.setWsConnectionId("test-websocket-key");

        AuthenticateDTO authenticateDTO = new AuthenticateDTO();
        authenticateDTO.setPlayerToken("test-player-token");

        // mock the methods that are called in authenticateAndJoinLobby()
        Mockito.when(playerService.getPlayerByToken(Mockito.anyString())).thenReturn(testPlayer);
        Mockito.when(authenticationService.getLobbyIdFromAuthToken(Mockito.anyString())).thenReturn(lobbyId);
        Mockito.when(lobbyService.getLobbyById(Mockito.anyLong())).thenReturn(null);
        Mockito.when(lobbyService.joinLobby(Mockito.any(), Mockito.anyString(), Mockito.anyString())).thenReturn(null);

        authenticationService.authenticateAndJoinLobby("test-websocket-key", authenticateDTO.getPlayerToken());

        // check if the methods are called with the correct arguments
        Mockito.verify(webSocketService).sendToPlayerInLobby(
                eq("test-websocket-key"),
                eq("/authentication"),
                eq(lobbyId.toString()),
                Mockito.any(WSConnectedDTO.class));
        Mockito.verify(gameService).sendLobbySettings(
                eq(lobbyId.intValue()));
    }
}
