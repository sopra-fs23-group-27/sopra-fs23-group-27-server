package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.service.AuthenticationService;
import ch.uzh.ifi.hase.soprafs23.service.LobbyService;
import ch.uzh.ifi.hase.soprafs23.service.PlayerService;
import ch.uzh.ifi.hase.soprafs23.service.WebSocketService;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.incoming.AuthenticateDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

/**
 * Provides general WebSocket endpoints
 */
@Controller
public class WebSocketController {

    Logger log = LoggerFactory.getLogger(WebSocketController.class);

    private final AuthenticationService authenticationService;
    private final WebSocketService webSocketService;
    private final PlayerService playerService;

    public WebSocketController(AuthenticationService authenticationService,
                               WebSocketService webSocketService,
                               PlayerService playerService) {
        this.authenticationService = authenticationService;
        this.webSocketService = webSocketService;
        this.playerService = playerService;
    }

    @MessageMapping("/authentication")
    public synchronized void authenticatePlayer(SimpMessageHeaderAccessor smha, AuthenticateDTO dto) {
        Player player = playerService.getPlayerByToken(dto.getPlayerToken());
        if (player.getLobbyId() != null && this.webSocketService.isPlayerReconnecting(dto.getPlayerToken())) {
            log.info("Authentication Event: Previous Player (token: " + dto.getPlayerToken() + ") is reconnecting with" +
                    " new websocketId: " + webSocketService.getIdentity(smha));
            this.webSocketService.initReconnectionProcedure(webSocketService.getIdentity(smha), dto.getPlayerToken());
        }
        else {
            log.info("Authentication Event: New Player with websocketId: " + webSocketService.getIdentity(smha) + " connected");
            this.authenticationService.authenticateAndJoinLobby(webSocketService.getIdentity(smha), dto.getPlayerToken());
        }

    }


    @EventListener
    public void playerDisconnectEvent(SessionDisconnectEvent disconnectEvent) {
        Principal disconnectedPlayer = disconnectEvent.getUser();
        if (disconnectedPlayer != null) {
            String wsConnectionId = disconnectedPlayer.getName();
            log.info("Disconnect Event: Player with websocketId " + wsConnectionId + " disconnected." +
                    " Initiating disconnection procedure.");

            this.webSocketService.initDisconnectionProcedureByWsId(wsConnectionId);
        }
    }

}