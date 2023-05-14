package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.LobbyService;
import ch.uzh.ifi.hase.soprafs23.service.PlayerService;
import ch.uzh.ifi.hase.soprafs23.service.WebSocketService;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.GuessDTO;

import ch.uzh.ifi.hase.soprafs23.websocket.dto.incoming.RemoveDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;

import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class GameController {

    private final Logger log = LoggerFactory.getLogger(GameController.class);

    private final GameService gameService;
    private final WebSocketService webSocketService;
    private final LobbyService lobbyService;

    GameController(GameService gameService, WebSocketService webSocketService, LobbyService lobbyService) {
        this.gameService = gameService;
        this.webSocketService = webSocketService;
        this.lobbyService = lobbyService;
    }

    @MessageMapping("/games/{lobbyId}/guess")
    public void validateGuess(@DestinationVariable Integer lobbyId,
                              SimpMessageHeaderAccessor smha,
                              @Payload GuessDTO guessDTO) {
        gameService.validateGuess(lobbyId, guessDTO, smha);
    }

    @MessageMapping("/games/{lobbyId}/send-lobby-settings")
    public void sendLobbySettings(@DestinationVariable Integer lobbyId,
                                  SimpMessageHeaderAccessor smha) {
        gameService.sendLobbySettings(lobbyId, smha);
    }

    @MessageMapping("/games/{lobbyId}/game-ready")
    public void startNewGameRound(@DestinationVariable Integer lobbyId,
                                  SimpMessageHeaderAccessor smha) {
        gameService.startNewGameRound(lobbyId, smha);

    }

    @MessageMapping("/games/{lobbyId}/remove")
    public void removePlayerFromLobby(@DestinationVariable Integer lobbyId,
                                      SimpMessageHeaderAccessor smha,
                                      @Payload RemoveDTO removeDTO) {
        String wsConnectionId = WebSocketService.getIdentity(smha);
        lobbyService.kickPlayerFromLobby(lobbyId, removeDTO, wsConnectionId);
    }

}