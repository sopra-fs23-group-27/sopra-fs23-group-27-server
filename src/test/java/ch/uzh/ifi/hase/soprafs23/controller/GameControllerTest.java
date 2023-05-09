package ch.uzh.ifi.hase.soprafs23.controller;

import org.mockito.Mockito;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import static org.mockito.Mockito.verify;


import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.GuessDTO;

/**
 * Game Controller
 * This class is responsible for handling all REST request that are related to
 * the game.
 * The controller will receive the request and delegate the execution to the
 * GameService and finally return the result.
 */
public class GameControllerTest {

    @Mock
    private GameService gameService;

    @Mock
    private SimpMessageHeaderAccessor smha;

    private GameController gameController;

    public GameControllerTest() {
        MockitoAnnotations.openMocks(this);
        this.gameController = new GameController(gameService);
    }

    @Test
    public void testValidateGuessTest() {
        int lobbyId = 1;
        GuessDTO guessDTO = new GuessDTO("playerName", "guess");
        gameController.validateGuess(lobbyId, smha, guessDTO);
        verify(gameService).validateGuess(lobbyId, guessDTO, smha);
    }

    @Test
    public void testSendLobbySettingsTest() {
        int lobbyId = 1;
        gameController.sendLobbySettings(lobbyId, smha);
        verify(gameService).sendLobbySettings(lobbyId, smha);
    }

    @Test
    public void testStartNewGameRoundTest() {
        int lobbyId = 1;
        gameController.startNewGameRound(lobbyId, smha);
        verify(gameService).startNewGameRound(lobbyId, smha);
    }

}
