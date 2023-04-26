package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

@Service
@Transactional
public class WebSocketService {

    private PlayerRepository playerRepository;

    @Autowired
    protected SimpMessagingTemplate simpMessagingTemplate;

    public WebSocketService(@Qualifier("playerRepository") PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public void sendToPlayerInLobby(String wsConnectionId, String path, String lobbyId, Object dto) {

        this.simpMessagingTemplate.convertAndSendToUser(wsConnectionId, "/queue/lobby/" + lobbyId + path, dto);
    }

    public void sendToLobby(Long lobbyId, String path, Object dto) {
        List<Player> lobby = this.playerRepository.findByLobbyId(lobbyId);
        for (Player player : lobby) {
            sendToPlayerInLobby(player.getWsConnectionId(), path, lobbyId.toString(), dto);
        }
    }

    public static String getIdentity(SimpMessageHeaderAccessor sha) {
        Principal principal = sha.getUser();
        if (principal != null) {
            return principal.getName();
        }
        else {
            return null;
        }
    }

    public static void wait(int ms) {
        try {
            Thread.sleep(ms);
        }
        catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
    
}
