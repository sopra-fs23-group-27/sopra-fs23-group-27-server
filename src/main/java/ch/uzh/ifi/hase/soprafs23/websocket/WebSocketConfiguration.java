package ch.uzh.ifi.hase.soprafs23.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        // Set prefix for the endpoint that the client listens. Note by convention we distinguish between /topic/ and
        // /queue/. /topic/.. implies publish-subscribe (one-to-many) and /queue/ implies point-to-point (one-to-one)
        // message exchanges (Hence, we use queues to send messages to specific users)
        registry.enableSimpleBroker("/topic", "/queue");

        // STOMP messages whose destination header begins with /app are routed to @MessageMapping methods in
        // @Controller classes. (/app is the prefix for the endpoint to which the client will send messages)
        registry.setApplicationDestinationPrefixes("/app");

    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        // /ws is the HTTP URL for the endpoint to which a WebSocket (or SockJS) client needs to connect for the
        // WebSocket handshake.
        registry.addEndpoint("/ws")
                // Allow all origins to send messages to us.
                .setAllowedOriginPatterns("*")
                // for every new handshake we will create a new uuid for the UI
                .setHandshakeHandler(new CustomHandshakeHandler())
                // Enable SockJS fallback options
                .withSockJS();

    }
}