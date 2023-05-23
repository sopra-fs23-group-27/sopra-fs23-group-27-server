package ch.uzh.ifi.hase.soprafs23.websocket;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@SpringBootTest
@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true"})
public class WebSocketConfigurationTest {

    @Autowired
    private WebSocketConfiguration webSocketConfiguration;

    @Test
    public void configureMessageBroker_ShouldEnableSimpleBrokerWithCorrectPrefixes() {
        // Arrange
        MessageBrokerRegistry registry = mock(MessageBrokerRegistry.class);
        String[] expectedPrefixes = {"/topic", "/queue"};

        // Act
        webSocketConfiguration.configureMessageBroker(registry);

        // Assert
        verify(registry).enableSimpleBroker(expectedPrefixes);
    }

    @Test
    public void configureMessageBroker_ShouldSetApplicationDestinationPrefixes() {
        // Arrange
        MessageBrokerRegistry registry = mock(MessageBrokerRegistry.class);
        String[] expectedPrefixes = {"/app"};

        // Act
        webSocketConfiguration.configureMessageBroker(registry);

        // Assert
        verify(registry).setApplicationDestinationPrefixes(expectedPrefixes);
    }
}