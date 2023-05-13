package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.service.AuthenticationService;
import ch.uzh.ifi.hase.soprafs23.service.WebSocketService;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.incoming.AuthenticateDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WebSocketControllerTest {

    @Mock
    AuthenticationService authenticationService;

    @Mock
    WebSocketService webSocketService;


    @Mock
    SimpMessageHeaderAccessor smha;

    @Mock
    AuthenticateDTO dto;

    @Mock
    SessionDisconnectEvent disconnectEvent;

    @Mock
    Principal disconnectedPlayer;

    @Mock
    Logger log;

    @InjectMocks
    WebSocketController webSocketController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        webSocketController = new WebSocketController(authenticationService, webSocketService);
    }

    @Test
    void playerDisconnectEvent_shouldDisconnectPlayerFromLobby() {
        when(disconnectEvent.getUser()).thenReturn(disconnectedPlayer);
        when(disconnectedPlayer.getName()).thenReturn("testWebSocketId");

        webSocketController.playerDisconnectEvent(disconnectEvent);

        verify(webSocketService).initDisconnectionProcedureByWsId("testWebSocketId");
    }
}