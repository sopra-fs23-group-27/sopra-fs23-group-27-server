package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.websocket.dto.GuessDTO;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;

import org.springframework.stereotype.Controller;

@Controller
public class GameController {

    // Handles messages from /app/chat. (Note the Spring adds the /app prefix for
    // us).
    @MessageMapping("/pret")

    // Sends the return value of this method to /topic/messages
    // METHOD: subscribe
    // PAYLOAD: playername<string> guess<string>
    // DESCRIPTION: informs all players about the latest wrong guesses in the game
    // round
    @SendTo("/topic/games/{gameId}/guesses-in-round")
    public GuessDTO getGuess(GuessDTO dto) {
        return dto;
    }

}