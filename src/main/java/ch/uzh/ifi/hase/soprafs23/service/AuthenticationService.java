package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.exceptions.PlayerServiceException;
import ch.uzh.ifi.hase.soprafs23.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.LobbySettingsDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.WSConnectedDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handles authentication / registration dance
 */
@Service
@Transactional
public class AuthenticationService {

    Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    private final WebSocketService webSocketService;
    private final PlayerService playerService;
    private final LobbyService lobbyService;

    private final Map<String, Long> authenticatedJoins = new HashMap<>();

    public AuthenticationService(WebSocketService webSocketService, PlayerService playerService, LobbyService lobbyService) {
        this.webSocketService = webSocketService;
        this.playerService = playerService;
        this.lobbyService = lobbyService;
    }

    public void addToAuthenticatedJoins(String playerToken, Long lobbyId) {
        this.authenticatedJoins.put(playerToken, lobbyId);
    }

    public Long getLobbyIdFromAuthToken(String playerToken) {
        if (this.authenticatedJoins.containsKey(playerToken)) {
            return this.authenticatedJoins.get(playerToken);
        }
        return null;
    }

    public void removeFromAuthenticatedJoins(String playerToken) {
        this.authenticatedJoins.remove(playerToken);
    }

    public synchronized void authenticateAndJoinLobby(String wsConnectionId, String playerToken) {
        Player player = playerService.getPlayerByToken(playerToken);
        if (player == null) {
            throw new PlayerServiceException(
                    "Player was not authenticated. Please perform authentication before establishing a websocket connection.");
        }
        
        Long lobbyId = getLobbyIdFromAuthToken(playerToken);
        Lobby lobby = lobbyService.getLobbyById(lobbyId);

        LobbyGetDTO lobbyGetDTO = lobbyService.joinLobby(lobby, playerToken, wsConnectionId);

        WSConnectedDTO wsConnectedDTO = new WSConnectedDTO(player.getPlayerName(), lobbyId);
        this.webSocketService.sendToPlayerInLobby(wsConnectionId, "/queue/register", lobbyId.toString(), wsConnectedDTO);
        // wait for player to subscribe to channels
        webSocketService.wait(500);
        // send initial lobby-state packet
        this.webSocketService.sendToLobby(lobbyId.toString(), "/lobby-settings", lobbyGetDTO);

        removeFromAuthenticatedJoins(playerToken);
        log.info("Lobby " + lobbyId + ": Player " + wsConnectionId + " joined");
    }
}