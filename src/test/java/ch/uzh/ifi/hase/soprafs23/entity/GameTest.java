package ch.uzh.ifi.hase.soprafs23.entity;

import static org.mockito.Mockito.*;

import java.util.ArrayList;

import ch.uzh.ifi.hase.soprafs23.service.WebSocketService;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.GameStatsDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.GuessDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.incoming.AuthenticateDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing.CorrectGuessDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing.GuessEvalDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing.RoundDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing.WSConnectedDTO;

import info.debatty.java.stringsimilarity.JaroWinkler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;


import ch.uzh.ifi.hase.soprafs23.repository.CountryRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.service.CountryHandlerService;


import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

    @Mock
    private CountryHandlerService countryHandlerService;

    @Mock
    private WebSocketService webSocketService;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private BasicLobby basicLobby;

    @Mock
    private AdvancedLobby advancedLobby;

    @Mock
    private ScoreBoard scoreBoard;

    @InjectMocks
    private Game basicGame;

    @InjectMocks
    private Game advancedGame;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        basicGame = Mockito.spy(new Game(countryHandlerService, webSocketService, countryRepository, playerRepository, basicLobby));
        advancedGame = Mockito.spy(new Game(countryHandlerService, webSocketService, countryRepository, playerRepository, advancedLobby));
    }

    @Test
    public void testStartBasicGame() {
        // mock endRound method
        doNothing().when(basicGame).startRound();
        // call the method to be tested
        basicGame.startGame();

        // check that the method startRound() was called
        verify(basicGame, times(1)).startRound();
    }

    @Test
    public void testStartAdvancedGame() {
        // mock endRound method
        doNothing().when(advancedGame).startRound();
        // call the method to be tested
        advancedGame.startGame();

        // check that the method startRound() was called
        verify(advancedGame, times(1)).startRound();
    }

    @Test
    public void testEndBasicGame() {
        // call the method to be tested
        basicGame.endGame();

        // check that the lobby was notified about end game
        verify(webSocketService, times(1)).sendToLobby(anyLong(), eq("/game-end"), anyString());
    }

    @Test
    public void testEndAdvancedGame() {
        // call the method to be tested
        advancedGame.endGame();

        // check that the lobby was notified about end game
        verify(webSocketService, times(1)).sendToLobby(anyLong(), eq("/game-end"), anyString());
    }

    @Test
    public void testValidateGuessBasicMode_firstWrongGuess() {
        // given
        String playerName = "player1";
        String guess = "Wrong Guess";
        String wsConnectionId = "connection1";

        // override the attribute scoreBoard
        ReflectionTestUtils.setField(basicGame, "scoreBoard", scoreBoard);

        doNothing().when(scoreBoard).setCurrentCorrectGuessPerPlayer(anyString(), anyBoolean());
        when(scoreBoard.getCurrentNumberOfWrongGuessesPerPlayer(Mockito.anyString())).thenReturn(0);
        when(scoreBoard.getCurrentCorrectGuessPerPlayer(Mockito.anyString())).thenReturn(false);

        // call the method to be tested
        basicGame.validateGuess(playerName, guess, wsConnectionId);

        // check that the methods setCurrentNumberOfWrongGuessesPerPlayer was called
        verify(scoreBoard).setCurrentNumberOfWrongGuessesPerPlayer(playerName, 1);
        // check that the methods setCurrentTimeUntilCorrectGuessPerPlayer and setCurrentCorrectGuessPerPlayer was not called
        verify(scoreBoard, times(0)).setCurrentTimeUntilCorrectGuessPerPlayer(eq(playerName), anyInt());
        verify(scoreBoard, times(0)).setCurrentCorrectGuessPerPlayer(eq(playerName), anyBoolean());
    }

    @Test
    public void testValidateGuessBasicMode_firstCorrectGuess() {
        // given
        String playerName = "player1";
        String guess = "Correct Guess";
        String wsConnectionId = "connection1";

        // override the attribute scoreBoard
        ReflectionTestUtils.setField(basicGame, "scoreBoard", scoreBoard);
        ReflectionTestUtils.setField(basicGame, "correctGuess", "correctguess");
        ReflectionTestUtils.setField(basicGame, "startTime", 1000L);


        doNothing().when(scoreBoard).setCurrentCorrectGuessPerPlayer(anyString(), anyBoolean());
        when(scoreBoard.getCurrentNumberOfWrongGuessesPerPlayer(Mockito.anyString())).thenReturn(0);
        when(scoreBoard.getCurrentCorrectGuessPerPlayer(Mockito.anyString())).thenReturn(false);

        // call the method to be tested
        basicGame.validateGuess(playerName, guess, wsConnectionId);

        // check that the methods setCurrentTimeUntilCorrectGuessPerPlayer and setCurrentCorrectGuessPerPlayer was called
        verify(scoreBoard, times(1)).setCurrentTimeUntilCorrectGuessPerPlayer(eq(playerName), anyInt());
        verify(scoreBoard, times(1)).setCurrentCorrectGuessPerPlayer(eq(playerName), anyBoolean());
        // check that the method setCurrentNumberOfWrongGuessesPerPlayer was not called
        verify(scoreBoard, times(0)).setCurrentNumberOfWrongGuessesPerPlayer(eq(playerName), anyInt());
    }

    @Test
    public void testValidateGuessBasicMode_alreadySubmittedCorrectGuess() {
        // given
        String playerName = "player1";
        String guess = "Any Guess";
        String wsConnectionId = "connection1";

        // override the attribute scoreBoard
        ReflectionTestUtils.setField(basicGame, "scoreBoard", scoreBoard);

        doNothing().when(scoreBoard).setCurrentCorrectGuessPerPlayer(anyString(), anyBoolean());
        when(scoreBoard.getCurrentNumberOfWrongGuessesPerPlayer(Mockito.anyString())).thenReturn(0);
        when(scoreBoard.getCurrentCorrectGuessPerPlayer(Mockito.anyString())).thenReturn(true);

        // call the method to be tested
        basicGame.validateGuess(playerName, guess, wsConnectionId);

        // Assert
        verify(webSocketService, times(1)).sendToPlayerInLobby(eq(wsConnectionId), eq("/errors"), anyString(), anyString());
    }

    @Test
    public void testValidateGuessBasicMode_alreadySubmittedWrongGuess() {
        // given
        String playerName = "player1";
        String guess = "Any Guess";
        String wsConnectionId = "connection1";

        // override the attribute scoreBoard
        ReflectionTestUtils.setField(basicGame, "scoreBoard", scoreBoard);

        doNothing().when(scoreBoard).setCurrentCorrectGuessPerPlayer(anyString(), anyBoolean());
        when(scoreBoard.getCurrentNumberOfWrongGuessesPerPlayer(Mockito.anyString())).thenReturn(1);
        when(scoreBoard.getCurrentCorrectGuessPerPlayer(Mockito.anyString())).thenReturn(false);

        // call the method to be tested
        basicGame.validateGuess(playerName, guess, wsConnectionId);

        // Assert
        verify(webSocketService, times(1)).sendToPlayerInLobby(eq(wsConnectionId), eq("/errors"), anyString(), anyString());
    }

    @Test
    public void testValidateGuesAdvancedMode_correctGuess() {
        // given
        String playerName = "player1";
        String guess = "Correct Guess";
        String wsConnectionId = "connection1";
        String cleanedGuess = guess.toLowerCase().replaceAll("\\s+", "");

        // mock endRound method
        doNothing().when(advancedGame).endRound();

        // override the attribute scoreBoard
        ReflectionTestUtils.setField(advancedGame, "scoreBoard", scoreBoard);
        ReflectionTestUtils.setField(advancedGame, "correctGuess", "correctguess");
        ReflectionTestUtils.setField(advancedGame, "startTime", 1000L);

        // call the method to be tested
        advancedGame.validateGuess(playerName, guess, wsConnectionId);

        // check that the following methods were called
        verify(webSocketService, times(1)).sendToPlayerInLobby(any(), any(), any(), any());
        verify(scoreBoard, times(1)).setCurrentTimeUntilCorrectGuessPerPlayer(eq(playerName), anyInt());
        verify(scoreBoard, times(1)).setCurrentCorrectGuessPerPlayer(eq(playerName), eq(true));
        verify(advancedGame,times(1)).endRound();
        // check that the method setCurrentNumberOfWrongGuessesPerPlayer was not called
        verify(scoreBoard, times(0)).setCurrentNumberOfWrongGuessesPerPlayer(eq(playerName), anyInt());
    }

    @Test
    public void testValidateGuesAdvancedMode_correctGuessWithMinorTypo() {
        // given
        String playerName = "player1";
        String guess = "Correct Guesss";
        String wsConnectionId = "connection1";

        // mock endRound method
        doNothing().when(advancedGame).endRound();

        // override the attribute scoreBoard
        ReflectionTestUtils.setField(advancedGame, "scoreBoard", scoreBoard);
        ReflectionTestUtils.setField(advancedGame, "correctGuess", "correctguess");
        ReflectionTestUtils.setField(advancedGame, "startTime", 1000L);

        // call the method to be tested
        advancedGame.validateGuess(playerName, guess, wsConnectionId);

        // check that the following methods were called
        verify(webSocketService, times(1)).sendToPlayerInLobby(any(), any(), any(), any());
        verify(scoreBoard, times(1)).setCurrentTimeUntilCorrectGuessPerPlayer(eq(playerName), anyInt());
        verify(scoreBoard, times(1)).setCurrentCorrectGuessPerPlayer(eq(playerName), eq(true));
        verify(advancedGame,times(1)).endRound();
        // check that the method setCurrentNumberOfWrongGuessesPerPlayer was not called
        verify(scoreBoard, times(0)).setCurrentNumberOfWrongGuessesPerPlayer(eq(playerName), anyInt());
    }

    @Test
    public void testValidateGuesAdvancedMode_wrongGuess() {
        // given
        String playerName = "player1";
        String guess = "Wrong Guess";
        String wsConnectionId = "connection1";
        String cleanedGuess = guess.toLowerCase().replaceAll("\\s+", "");

        // mock endRound method
        doNothing().when(advancedGame).endRound();

        // override the attribute scoreBoard
        ReflectionTestUtils.setField(advancedGame, "scoreBoard", scoreBoard);
        ReflectionTestUtils.setField(advancedGame, "correctGuess", "Correct Guess");
        ReflectionTestUtils.setField(advancedGame, "startTime", 1000L);


        // call the method to be tested
        advancedGame.validateGuess(playerName, guess, wsConnectionId);

        // check that the following methods were called
        verify(webSocketService, times(1)).sendToPlayerInLobby(any(), any(), any(), any());
        verify(webSocketService, times(1)).sendToLobby(anyLong(), eq("/guesses"),  any(GuessDTO.class));
        verify(scoreBoard, times(1)).setCurrentNumberOfWrongGuessesPerPlayer(eq(playerName), anyInt());
        // check that the methods setCurrentTimeUntilCorrectGuessPerPlayer, setCurrentCorrectGuessPerPlayer
        // and endRound() were not called
        verify(scoreBoard, times(0)).setCurrentTimeUntilCorrectGuessPerPlayer(eq(playerName), anyInt());
        verify(scoreBoard, times(0)).setCurrentCorrectGuessPerPlayer(eq(playerName), eq(true));
        verify(advancedGame,times(0)).endRound();
    }

    @Test
    public void testUpdateCorrectGuessBasicMode() {

        // mock the countryRepository
        Country testCountry = new Country();
        testCountry.setCountryCode("US");
        testCountry.setName("United States");
        when(this.countryRepository.findByCountryCode(anyString())).thenReturn(testCountry);

        // override attribute correctGuess
        ReflectionTestUtils.setField(basicGame, "correctGuess", "ch");

        basicGame.updateCorrectGuess("US");

        assertEquals("unitedstates", ReflectionTestUtils.getField(basicGame, "correctGuess"));
    }

    @Test
    public void testUpdateCorrectGuessAdvancedMode() {

        // mock the countryRepository
        Country testCountry = new Country();
        testCountry.setCountryCode("US");
        testCountry.setName("United States");
        when(this.countryRepository.findByCountryCode(anyString())).thenReturn(testCountry);

        // override attribute correctGuess
        ReflectionTestUtils.setField(basicGame, "correctGuess", "ch");

        basicGame.updateCorrectGuess("US");

        assertEquals("unitedstates", ReflectionTestUtils.getField(basicGame, "correctGuess"));
    }
}
