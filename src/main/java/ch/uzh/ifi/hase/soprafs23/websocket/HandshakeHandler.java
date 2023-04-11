package ch.uzh.ifi.hase.soprafs23.websocket;

import com.sun.security.auth.UserPrincipal;
import java.security.Principal;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

public class HandshakeHandler extends DefaultHandshakeHandler {

    private final Logger logger = LoggerFactory.getLogger(HandshakeHandler.class);

    @Override
    protected Principal determineUser(ServerHttpRequest req, WebSocketHandler weHandler, Map<String, Object> attributes) {
        final String randId = UUID.randomUUID().toString();
        logger.info("{}",attributes.get("name"));
        logger.info("Player opened client unique ID {}",randId);
        return new UserPrincipal(randId);
    }

}
