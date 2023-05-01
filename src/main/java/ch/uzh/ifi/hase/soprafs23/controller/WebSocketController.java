package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.service.AuthenticationService;
import ch.uzh.ifi.hase.soprafs23.service.WebSocketService;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.incoming.AuthenticateDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

/**
 * Provides general WebSocket endpoints
 */
@Controller
public class WebSocketController {

    Logger log = LoggerFactory.getLogger(WebSocketController.class);

    private final AuthenticationService authenticationService;
    private final WebSocketService webSocketService;

    public WebSocketController(AuthenticationService authenticationService, WebSocketService webSocketService) {
        this.authenticationService = authenticationService;
        this.webSocketService = webSocketService;
    }

    @MessageMapping("/authentication")
    public synchronized void authenticatePlayer(SimpMessageHeaderAccessor smha, AuthenticateDTO dto) {
        log.info("Player " + webSocketService.getIdentity(smha) + ": Connection established");

        authenticationService.authenticateAndJoinLobby(webSocketService.getIdentity(smha), dto.getPlayerToken());
    }

}