package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.PlayerService;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.GuessDTO;

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

    GameController(GameService gameService) {
        this.gameService = gameService;
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

}