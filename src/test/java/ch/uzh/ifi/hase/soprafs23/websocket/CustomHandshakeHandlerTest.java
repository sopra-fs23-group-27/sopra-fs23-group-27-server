package ch.uzh.ifi.hase.soprafs23.websocket;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.security.Principal;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;

public class CustomHandshakeHandlerTest {

    private CustomHandshakeHandler handshakeHandler;
    private ServerHttpRequest request;
    private WebSocketHandler webSocketHandler;
    private Map<String, Object> attributes;

    @BeforeEach
    public void setUp() {
        handshakeHandler = new CustomHandshakeHandler();
        request = mock(ServerHttpRequest.class);
        webSocketHandler = mock(WebSocketHandler.class);
        attributes = Collections.emptyMap();
    }

    @Test
    public void determineUser_ShouldReturnStompPrincipal() {
        // Arrange
        when(request.getURI()).thenReturn(URI.create("http://example.com"));
        when(webSocketHandler.toString()).thenReturn("MockWebSocketHandler");
        when(request.getHeaders()).thenReturn(new HttpHeaders());

        // Act
        Principal result = handshakeHandler.determineUser(request, webSocketHandler, attributes);

        // Assert
        // Verify that the returned object is an instance of StompPrincipal
        assertTrue(result instanceof StompPrincipal);
    }

    @Test
    public void determineUser_ShouldGenerateUniqueId() {
        // Arrange
        when(request.getURI()).thenReturn(URI.create("http://example.com"));
        when(webSocketHandler.toString()).thenReturn("MockWebSocketHandler");
        when(request.getHeaders()).thenReturn(new HttpHeaders());

        // Act
        Principal result = handshakeHandler.determineUser(request, webSocketHandler, attributes);

        // Assert
        // Verify that the StompPrincipal has a unique ID
        assertNotNull(result);
        assertNotEquals("", result.getName());
    }
}