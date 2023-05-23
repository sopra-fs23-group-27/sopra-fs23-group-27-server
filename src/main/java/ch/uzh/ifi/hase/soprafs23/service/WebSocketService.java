package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing.TimerDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.*;

@Service
@Transactional
public class WebSocketService {

    Logger log = LoggerFactory.getLogger(WebSocketService.class);
    private PlayerRepository playerRepository;
    private final LobbyService lobbyService;

    private final Map<String, Timer> playersToBeDisconnected = new HashMap<>();

    private final ArrayList<String> reconnectionList = new ArrayList<>();

    @Autowired
    protected SimpMessagingTemplate simpMessagingTemplate;

    public WebSocketService(@Qualifier("playerRepository") PlayerRepository playerRepository,
                            @Lazy LobbyService lobbyService) {
        this.playerRepository = playerRepository;
        this.lobbyService = lobbyService;
    }

    public void sendToPlayerInLobby(String wsConnectionId, String path, String lobbyId, Object dto) {
        this.simpMessagingTemplate.convertAndSendToUser(wsConnectionId, "/queue/lobbies/" + lobbyId + path, dto);
    }

    public void sendToPlayer(String wsConnectionId, String path, Object dto) {
        this.simpMessagingTemplate.convertAndSendToUser(wsConnectionId, "/queue" + path, dto);
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


    public void initPlayAgainProcedureByPlayerName(String playerName, Long decisionTime) {
        Player player = this.playerRepository.findByPlayerName(playerName);
        String playerToken = player.getToken();

        this.playersToBeDisconnected.put(playerToken, new Timer());
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                log.info("Lobby " + player.getLobbyId() + ": Time to play again in same lobby is over for Player with playername" + player.getPlayerName());
                lobbyService.clearPlayerAfterGameEnd(playerToken);
                playersToBeDisconnected.remove(playerToken);
            }
        };
        this.playersToBeDisconnected.get(playerToken).schedule(timerTask, decisionTime);
    }

    public void initDisconnectionProcedureByWsId(String wsConnectionId) {
        Player player = this.playerRepository.findByWsConnectionId(wsConnectionId);
        if (player != null) {
            String playerToken = player.getToken();

            this.playersToBeDisconnected.put(playerToken, new Timer());
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    log.info("Lobby " + player.getLobbyId() + ": Reconnection time of Player with WsId" + player.getWsConnectionId() + " is over.");
                    lobbyService.disconnectPlayer(playerToken);
                    playersToBeDisconnected.remove(playerToken);
                }
            };
            this.playersToBeDisconnected.get(playerToken).schedule(timerTask, 50);
        }
    }

    public Boolean isPlayerReconnecting(String playerToken) {
        return playersToBeDisconnected.containsKey(playerToken);
    }

    public void initReconnectionProcedure(String newWsConnectionId, String playerToken) {

        if (this.playersToBeDisconnected.containsKey(playerToken)) {
            Player player = this.playerRepository.findByToken(playerToken);
            if (player != null) {
                this.playersToBeDisconnected.get(playerToken).cancel();
                this.playersToBeDisconnected.remove(playerToken);
                player.setWsConnectionId(newWsConnectionId);
                this.playerRepository.save(player);
                this.playerRepository.flush();
                this.lobbyService.resendLobbySettings(player.getLobbyId().intValue());
                log.info("Lobby " + player.getLobbyId() + ": Player " + player.getPlayerName() + " is reconnected. The new" +
                        "websocketId is: " + newWsConnectionId);

            }

        }
    }
}
