package ch.uzh.ifi.hase.soprafs23.entity;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;

import ch.uzh.ifi.hase.soprafs23.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs23.service.WebSocketService;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.incoming.GuessDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing.CorrectGuessDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import ch.uzh.ifi.hase.soprafs23.repository.CountryRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;

import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;

public class GameTest {

    @Mock
    private CountryHandler countryHandler;

    @Mock
    private WebSocketService webSocketService;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private LobbyRepository lobbyRepository;

    @Mock
    private BasicLobby basicLobby;

    @Mock
    private AdvancedLobby advancedLobby;

    @Mock
    private Lobby playAgainLobby;

    @Mock
    private ScoreBoard scoreBoard;

    @Mock
    private HintHandler hintHandler;

    @InjectMocks
    private Game basicGame;

    @InjectMocks
    private Game advancedGame;

    private ArrayList<String> continents;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        basicGame = Mockito.spy(new Game(countryHandler, webSocketService, countryRepository, playerRepository, lobbyRepository, basicLobby));
        advancedGame = Mockito.spy(new Game(countryHandler, webSocketService, countryRepository, playerRepository, lobbyRepository, advancedLobby));

        continents = new ArrayList<String>(Arrays.asList("Americas", "Europe"));
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
    public void testEndBasicGame_EmptyPlayAgainLobby() {
        // Override game attributes
        ReflectionTestUtils.setField(basicGame, "playAgainTimeWindow", 1);

        // mock lobbyRepository method
        when(lobbyRepository.findByLobbyId(anyLong())).thenReturn(playAgainLobby);

        ArrayList<String> playersInRematch = new ArrayList();
        doReturn(playersInRematch).when(playAgainLobby).getJoinedPlayerNames();

        // call the method to be tested
        basicGame.endGame();

        // check that the lobby was notified about end game
        verify(webSocketService, times(1)).sendToLobby(anyLong(), eq("/game-end"), anyString());
        verify(lobbyRepository, times(1)).delete(playAgainLobby);
    }

    @Test
    public void testEndBasicGame_NonEmptyPlayAgainLobby() {
        // Override game attributes
        ReflectionTestUtils.setField(basicGame, "playAgainTimeWindow", 1);

        // mock lobbyRepository method
        when(lobbyRepository.findByLobbyId(anyLong())).thenReturn(playAgainLobby);

        ArrayList<String> playersInRematch = new ArrayList();
        playersInRematch.add("player");
        doReturn(playersInRematch).when(playAgainLobby).getJoinedPlayerNames();

        // call the method to be tested
        basicGame.endGame();

        // check that the lobby was notified about end game
        verify(webSocketService, times(1)).sendToLobby(anyLong(), eq("/game-end"), anyString());
        verify(lobbyRepository, times(0)).delete(playAgainLobby);
        verify(playAgainLobby, times(1)).setCollectingPlayAgains(false);
    }

    @Test
    public void testEndAdvancedGame() {
        // Override game attributes
        ReflectionTestUtils.setField(advancedGame, "playAgainTimeWindow", 1);

        // mock lobbyRepository method
        when(lobbyRepository.findByLobbyId(anyLong())).thenReturn(playAgainLobby);

        // call the method to be tested
        advancedGame.endGame();

        // check that the lobby was notified about end game
        verify(webSocketService, times(1)).sendToLobby(anyLong(), eq("/game-end"), anyString());
    }

    @Test
    public void testStartRoundBasicMode_firstRound() {
        // given
        ArrayList<String> allCountryCodes = new ArrayList();
        allCountryCodes.add("CH");
        allCountryCodes.add("US");
        allCountryCodes.add("DE");
        allCountryCodes.add("FR");
        allCountryCodes.add("IT");

        // Override game attributes
        ReflectionTestUtils.setField(basicGame, "allCountryCodes", allCountryCodes);
        ReflectionTestUtils.setField(basicGame, "correctGuess", "switzerland");
        ReflectionTestUtils.setField(basicGame, "hintHandler", hintHandler);
        ReflectionTestUtils.setField(basicGame, "numRounds", 4);
        ReflectionTestUtils.setField(basicGame, "scoreBoard", scoreBoard);

        // Mock the country class
        Country country = mock(Country.class);

        // Mock some repository methods and service methods
        when(countryHandler.sourceCountryInfo(5, continents)).thenReturn(allCountryCodes);
        doNothing().when(webSocketService).sendToLobby(anyLong(), anyString(), any());
        when(countryRepository.findByCountryCode(anyString())).thenReturn(country);
        doNothing().when(basicGame).updateCorrectGuess(anyString());

        // Mock the behavior of the HintHandler methods
        doNothing().when(hintHandler).setHints();
        doNothing().when(hintHandler).sendRequiredDetailsViaWebSocket();

        // call the method to be tested
        basicGame.startRound();

        // check that this is the first round
        assertEquals(0, ReflectionTestUtils.getField(basicGame, "round"));
        // check that correct guess has been updated
        assertEquals("switzerland", ReflectionTestUtils.getField(basicGame, "correctGuess"));

        // check the following methods were called
        verify(scoreBoard, times(1)).resetAllCurrentScores();
        verify(webSocketService, times(1)).sendToLobby(anyLong(), eq("/round-start"), eq("{}"));
        verify(basicGame, times(1)).updateCorrectGuess(anyString());
        verify(hintHandler, times(1)).setHints();
        verify(hintHandler, times(1)).sendRequiredDetailsViaWebSocket();

        // check that the following methods were not called
        verify(basicGame, times(0)).endGame();
    }

    @Test
    public void testStartRoundBasicMode_lastRound() {
        // given
        ArrayList<String> allCountryCodes = new ArrayList();
        allCountryCodes.add("CH");
        allCountryCodes.add("US");
        allCountryCodes.add("DE");
        allCountryCodes.add("FR");
        allCountryCodes.add("IT");

        // Override game attributes
        ReflectionTestUtils.setField(basicGame, "allCountryCodes", allCountryCodes);
        ReflectionTestUtils.setField(basicGame, "correctGuess", "italy");
        ReflectionTestUtils.setField(basicGame, "hintHandler", hintHandler);
        ReflectionTestUtils.setField(basicGame, "numRounds", 4);
        ReflectionTestUtils.setField(basicGame, "round", 3);
        ReflectionTestUtils.setField(basicGame, "scoreBoard", scoreBoard);

        // Mock the country class
        Country country = mock(Country.class);

        // Mock some repository methods and service methods
        when(countryHandler.sourceCountryInfo(5, continents)).thenReturn(allCountryCodes);
        doNothing().when(webSocketService).sendToLobby(anyLong(), anyString(), any());
        when(countryRepository.findByCountryCode(anyString())).thenReturn(country);
        doNothing().when(basicGame).updateCorrectGuess(anyString());

        // Mock the behavior of the HintHandler methods
        doNothing().when(hintHandler).setHints();
        doNothing().when(hintHandler).sendRequiredDetailsViaWebSocket();

        // call the method to be tested
        basicGame.startRound();

        // check that this is the last round
        assertEquals(3, ReflectionTestUtils.getField(basicGame, "round"));
        // check that correct guess has been updated
        assertEquals("italy", ReflectionTestUtils.getField(basicGame, "correctGuess"));

        // check the following methods were called
        verify(basicGame, times(0)).endGame();
        verify(scoreBoard, times(1)).resetAllCurrentScores();
        verify(webSocketService, times(1)).sendToLobby(anyLong(), eq("/round-start"), eq("{}"));
        verify(basicGame, times(1)).updateCorrectGuess(anyString());
        verify(hintHandler, times(1)).setHints();
        verify(hintHandler, times(1)).sendRequiredDetailsViaWebSocket();
    }

    @Test
    public void testStartRoundBasicMode_afterLastRound() {
        // given
        ArrayList<String> allCountryCodes = new ArrayList();
        allCountryCodes.add("CH");
        allCountryCodes.add("US");
        allCountryCodes.add("DE");
        allCountryCodes.add("FR");
        allCountryCodes.add("IT");

        // Override game attributes
        ReflectionTestUtils.setField(basicGame, "allCountryCodes", allCountryCodes);
        ReflectionTestUtils.setField(basicGame, "correctGuess", "italy");
        ReflectionTestUtils.setField(basicGame, "hintHandler", hintHandler);
        ReflectionTestUtils.setField(basicGame, "numRounds", 4);
        ReflectionTestUtils.setField(basicGame, "round", 4);
        ReflectionTestUtils.setField(basicGame, "playAgainTimeWindow", 1);


        // Mock the country class
        Country country = mock(Country.class);

        // Mock some repository methods and service methods
        when(countryHandler.sourceCountryInfo(5, continents)).thenReturn(allCountryCodes);
        doNothing().when(webSocketService).sendToLobby(anyLong(), anyString(), any());
        when(countryRepository.findByCountryCode(anyString())).thenReturn(country);
        doNothing().when(basicGame).updateCorrectGuess(anyString());
        when(lobbyRepository.findByLobbyId(anyLong())).thenReturn(playAgainLobby);

        // Mock the behavior of the HintHandler methods
        doNothing().when(hintHandler).setHints();
        doNothing().when(hintHandler).sendRequiredDetailsViaWebSocket();

        // call the method to be tested
        basicGame.startRound();

        // check the following methods were called
        verify(basicGame, times(1)).endGame();
        verify(webSocketService, times(1)).sendToLobby(anyLong(), eq("/game-end"), eq("{}"));
        // check that the following methods were not called
        verify(webSocketService, times(0)).sendToLobby(anyLong(), eq("/round-start"), eq("{}"));
        verify(basicGame, times(0)).updateCorrectGuess(anyString());
        verify(hintHandler, times(0)).setHints();
        verify(hintHandler, times(0)).sendRequiredDetailsViaWebSocket();
    }

    @Test
    public void testStartRoundAdvancedMode_firstRound() {
        // given
        ArrayList<String> allCountryCodes = new ArrayList();
        allCountryCodes.add("CH");
        allCountryCodes.add("US");
        allCountryCodes.add("DE");
        allCountryCodes.add("FR");
        allCountryCodes.add("IT");

        // Override game attributes
        ReflectionTestUtils.setField(advancedGame, "allCountryCodes", allCountryCodes);
        ReflectionTestUtils.setField(advancedGame, "correctGuess", "switzerland");
        ReflectionTestUtils.setField(advancedGame, "hintHandler", hintHandler);
        ReflectionTestUtils.setField(advancedGame, "numRounds", 4);
        ReflectionTestUtils.setField(advancedGame, "scoreBoard", scoreBoard);

        // Mock the country class
        Country country = mock(Country.class);

        // Mock some repository methods and service methods
        when(countryHandler.sourceCountryInfo(5, continents)).thenReturn(allCountryCodes);
        doNothing().when(webSocketService).sendToLobby(anyLong(), anyString(), any());
        when(countryRepository.findByCountryCode(anyString())).thenReturn(country);
        doNothing().when(advancedGame).updateCorrectGuess(anyString());

        // Mock the behavior of the HintHandler methods
        doNothing().when(hintHandler).setHints();
        doNothing().when(hintHandler).sendRequiredDetailsViaWebSocket();

        // call the method to be tested
        advancedGame.startRound();

        // check that this is the first round
        assertEquals(0, ReflectionTestUtils.getField(advancedGame, "round"));
        // check that correct guess has been updated
        assertEquals("switzerland", ReflectionTestUtils.getField(advancedGame, "correctGuess"));

        // check the following methods were called
        verify(scoreBoard, times(1)).resetAllCurrentScores();
        verify(webSocketService, times(1)).sendToLobby(anyLong(), eq("/round-start"), eq("{}"));
        verify(advancedGame, times(1)).updateCorrectGuess(anyString());
        verify(hintHandler, times(1)).setHints();
        verify(hintHandler, times(1)).sendRequiredDetailsViaWebSocket();

        // check that the following methods were not called
        verify(advancedGame, times(0)).endGame();
    }

    @Test
    public void testStartRoundAdvancedMode_lastRound() {
        // given
        ArrayList<String> allCountryCodes = new ArrayList();
        allCountryCodes.add("CH");
        allCountryCodes.add("US");
        allCountryCodes.add("DE");
        allCountryCodes.add("FR");
        allCountryCodes.add("IT");

        // Override game attributes
        ReflectionTestUtils.setField(advancedGame, "allCountryCodes", allCountryCodes);
        ReflectionTestUtils.setField(advancedGame, "correctGuess", "italy");
        ReflectionTestUtils.setField(advancedGame, "hintHandler", hintHandler);
        ReflectionTestUtils.setField(advancedGame, "numRounds", 4);
        ReflectionTestUtils.setField(advancedGame, "round", 3);
        ReflectionTestUtils.setField(advancedGame, "scoreBoard", scoreBoard);

        // Mock the country class
        Country country = mock(Country.class);

        // Mock some repository methods and service methods
        when(countryHandler.sourceCountryInfo(5, continents)).thenReturn(allCountryCodes);
        doNothing().when(webSocketService).sendToLobby(anyLong(), anyString(), any());
        when(countryRepository.findByCountryCode(anyString())).thenReturn(country);
        doNothing().when(advancedGame).updateCorrectGuess(anyString());

        // Mock the behavior of the HintHandler methods
        doNothing().when(hintHandler).setHints();
        doNothing().when(hintHandler).sendRequiredDetailsViaWebSocket();

        // call the method to be tested
        advancedGame.startRound();

        // check that this is the last round
        assertEquals(3, ReflectionTestUtils.getField(advancedGame, "round"));
        // check that correct guess has been updated
        assertEquals("italy", ReflectionTestUtils.getField(advancedGame, "correctGuess"));

        // check the following methods were called
        verify(advancedGame, times(0)).endGame();
        verify(scoreBoard, times(1)).resetAllCurrentScores();
        verify(webSocketService, times(1)).sendToLobby(anyLong(), eq("/round-start"), eq("{}"));
        verify(advancedGame, times(1)).updateCorrectGuess(anyString());
        verify(hintHandler, times(1)).setHints();
        verify(hintHandler, times(1)).sendRequiredDetailsViaWebSocket();
    }

    @Test
    public void testStartRoundAdvancedMode_afterLastRound() {
        // given
        ArrayList<String> allCountryCodes = new ArrayList();
        allCountryCodes.add("CH");
        allCountryCodes.add("US");
        allCountryCodes.add("DE");
        allCountryCodes.add("FR");
        allCountryCodes.add("IT");

        // Override game attributes
        ReflectionTestUtils.setField(advancedGame, "allCountryCodes", allCountryCodes);
        ReflectionTestUtils.setField(advancedGame, "correctGuess", "italy");
        ReflectionTestUtils.setField(advancedGame, "hintHandler", hintHandler);
        ReflectionTestUtils.setField(advancedGame, "numRounds", 4);
        ReflectionTestUtils.setField(advancedGame, "round", 4);
        ReflectionTestUtils.setField(advancedGame, "playAgainTimeWindow", 1);


        // Mock the country class
        Country country = mock(Country.class);

        // Mock some repository methods and service methods
        when(countryHandler.sourceCountryInfo(5, continents)).thenReturn(allCountryCodes);
        doNothing().when(webSocketService).sendToLobby(anyLong(), anyString(), any());
        when(countryRepository.findByCountryCode(anyString())).thenReturn(country);
        doNothing().when(advancedGame).updateCorrectGuess(anyString());
        when(lobbyRepository.findByLobbyId(anyLong())).thenReturn(playAgainLobby);

        // Mock the behavior of the HintHandler methods
        doNothing().when(hintHandler).setHints();
        doNothing().when(hintHandler).sendRequiredDetailsViaWebSocket();

        // call the method to be tested
        advancedGame.startRound();


        // check the following methods were called
        verify(advancedGame, times(1)).endGame();
        verify(webSocketService, times(1)).sendToLobby(anyLong(), eq("/game-end"), eq("{}"));
        // check that the following methods were not called
        verify(webSocketService, times(0)).sendToLobby(anyLong(), eq("/round-start"), eq("{}"));
        verify(advancedGame, times(0)).updateCorrectGuess(anyString());
        verify(hintHandler, times(0)).setHints();
        verify(hintHandler, times(0)).sendRequiredDetailsViaWebSocket();
    }

    @Test
    public void testValidateGuessBasicMode_firstWrongGuess() {
        // given
        String playerName = "player1";
        String guess = "Wrong Guess";
        String wsConnectionId = "connection1";

        // override the attribute scoreBoard
        ReflectionTestUtils.setField(basicGame, "scoreBoard", scoreBoard);
        ReflectionTestUtils.setField(basicGame, "isAcceptingGuesses", true);

        doNothing().when(scoreBoard).setCurrentCorrectGuessPerPlayer(anyString(), anyBoolean());
        when(scoreBoard.getCurrentNumberOfWrongGuessesPerPlayer(Mockito.anyString())).thenReturn(0);
        when(scoreBoard.getCurrentCorrectGuessPerPlayer(Mockito.anyString())).thenReturn(false);

        ReflectionTestUtils.setField(basicGame, "startTime", 0L);

        // call the method to be tested
        basicGame.validateGuess(playerName, guess, wsConnectionId);

        // check that the methods setCurrentNumberOfWrongGuessesPerPlayer was called
        verify(scoreBoard).setCurrentNumberOfWrongGuessesPerPlayer(playerName, 1);
        // check that the methods setCurrentTimeUntilCorrectGuessPerPlayer and setCurrentCorrectGuessPerPlayer was not called
        verify(scoreBoard, times(1)).setCurrentTimeUntilCorrectGuessPerPlayer(eq(playerName), anyInt());
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
        ReflectionTestUtils.setField(basicGame, "isAcceptingGuesses", true);


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
        ReflectionTestUtils.setField(basicGame, "isAcceptingGuesses", true);


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
        ReflectionTestUtils.setField(basicGame, "isAcceptingGuesses", true);


        doNothing().when(scoreBoard).setCurrentCorrectGuessPerPlayer(anyString(), anyBoolean());
        when(scoreBoard.getCurrentNumberOfWrongGuessesPerPlayer(Mockito.anyString())).thenReturn(1);
        when(scoreBoard.getCurrentCorrectGuessPerPlayer(Mockito.anyString())).thenReturn(false);

        // call the method to be tested
        basicGame.validateGuess(playerName, guess, wsConnectionId);

        // Assert
        verify(webSocketService, times(1)).sendToPlayerInLobby(eq(wsConnectionId), eq("/errors"), anyString(), anyString());
    }

    @Test
    public void testValidateGuessAdvancedMode_correctGuess() {
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
        ReflectionTestUtils.setField(advancedGame, "isAcceptingGuesses", true);


        // call the method to be tested
        advancedGame.validateGuess(playerName, guess, wsConnectionId);

        // check that the following methods were called
        verify(webSocketService, times(1)).sendToPlayerInLobby(any(), any(), any(), any());
        verify(scoreBoard, times(1)).setCurrentTimeUntilCorrectGuessPerPlayer(eq(playerName), anyInt());
        verify(scoreBoard, times(1)).setCurrentCorrectGuessPerPlayer(eq(playerName), eq(true));
        verify(advancedGame, times(1)).endRound();
        // check that the method setCurrentNumberOfWrongGuessesPerPlayer was not called
        verify(scoreBoard, times(0)).setCurrentNumberOfWrongGuessesPerPlayer(eq(playerName), anyInt());
    }

    @Test
    public void testValidateGuessAdvancedMode_correctGuessWithMinorTypo() {
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
        ReflectionTestUtils.setField(advancedGame, "isAcceptingGuesses", true);

        // call the method to be tested
        advancedGame.validateGuess(playerName, guess, wsConnectionId);

        // check that the following methods were called
        verify(webSocketService, times(1)).sendToPlayerInLobby(any(), any(), any(), any());
        verify(scoreBoard, times(1)).setCurrentTimeUntilCorrectGuessPerPlayer(eq(playerName), anyInt());
        verify(scoreBoard, times(1)).setCurrentCorrectGuessPerPlayer(eq(playerName), eq(true));
        verify(advancedGame, times(1)).endRound();
        // check that the method setCurrentNumberOfWrongGuessesPerPlayer was not called
        verify(scoreBoard, times(0)).setCurrentNumberOfWrongGuessesPerPlayer(eq(playerName), anyInt());
    }

    @Test
    public void testValidateGuessAdvancedMode_wrongGuess() {
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
        ReflectionTestUtils.setField(advancedGame, "isAcceptingGuesses", true);


        // call the method to be tested
        advancedGame.validateGuess(playerName, guess, wsConnectionId);

        // check that the following methods were called
        verify(webSocketService, times(1)).sendToPlayerInLobby(any(), any(), any(), any());
        verify(webSocketService, times(1)).sendToLobby(anyLong(), eq("/guesses"), any(GuessDTO.class));
        verify(scoreBoard, times(1)).setCurrentNumberOfWrongGuessesPerPlayer(eq(playerName), anyInt());
        // check that the methods setCurrentTimeUntilCorrectGuessPerPlayer, setCurrentCorrectGuessPerPlayer
        // and endRound() were not called
        verify(scoreBoard, times(1)).setCurrentTimeUntilCorrectGuessPerPlayer(eq(playerName), anyInt());
        verify(scoreBoard, times(0)).setCurrentCorrectGuessPerPlayer(eq(playerName), eq(true));
        verify(advancedGame, times(0)).endRound();
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

    @Test
    public void testEndRoundBasicMode_firstRound() {
        // given
        ArrayList<String> playerNames = new ArrayList();
        playerNames.add("player1");
        playerNames.add("player2");
        playerNames.add("player3");
        playerNames.add("player4");
        playerNames.add("player5");

        Country testCountry = new Country();
        testCountry.setName("testCountry");

        // Override game attributes
        ReflectionTestUtils.setField(basicGame, "scoreBoard", scoreBoard);
        ReflectionTestUtils.setField(basicGame, "playerNames", playerNames);
        ReflectionTestUtils.setField(basicGame, "hintHandler", hintHandler);
        ReflectionTestUtils.setField(basicGame, "timer", new Timer());
        ReflectionTestUtils.setField(basicGame, "currentCountry", testCountry);

        // Mock some repository methods and service methods
        doNothing().when(webSocketService).sendToLobby(anyLong(), anyString(), any());
        doNothing().when(webSocketService).sendToPlayerInLobby(anyString(), anyString(), anyString(), any());
        doNothing().when(scoreBoard).updateTotalScores();
        doNothing().when(scoreBoard).computeLeaderBoardScore();

        basicGame.endRound();

        // check whether the followin methods were called
        verify(webSocketService, times(1)).sendToLobby(anyLong(), eq("/round-end"), eq("{}"));
        verify(scoreBoard, times(5)).getCurrentCorrectGuessPerPlayer(anyString());
        verify(scoreBoard, times(5)).setCurrentCorrectGuessPerPlayer(anyString(), eq(false));
        verify(scoreBoard, times(5)).setCurrentTimeUntilCorrectGuessPerPlayer(anyString(), anyInt());
        verify(scoreBoard, times(5)).getCurrentNumberOfWrongGuessesPerPlayer(anyString());
        // is zero because no guesses have been submitted yet
        verify(scoreBoard, times(0)).setCurrentNumberOfWrongGuessesPerPlayer(anyString(), eq(0));
        verify(scoreBoard, times(1)).updateTotalScores();
        verify(scoreBoard, times(1)).computeLeaderBoardScore();
        verify(webSocketService, times(1)).sendToLobby(anyLong(), eq("/correct-country"), any(CorrectGuessDTO.class));
        verify(basicGame, times(0)).endGame();

        // check whether the attribute round was incremented
        assertEquals(1, ReflectionTestUtils.getField(basicGame, "round"));
        assertEquals(null, ReflectionTestUtils.getField(basicGame, "correctGuess"));
        assertEquals(null, ReflectionTestUtils.getField(basicGame, "currentCountry"));
        assertEquals(null, ReflectionTestUtils.getField(basicGame, "startTime"));
        assertEquals(null, ReflectionTestUtils.getField(basicGame, "hintHandler"));
    }

    @Test
    public void testEndRoundBasicMode_lastRound() {
        // given
        ArrayList<String> playerNames = new ArrayList();
        playerNames.add("player1");
        playerNames.add("player2");
        playerNames.add("player3");
        playerNames.add("player4");
        playerNames.add("player5");

        Country testCountry = new Country();
        testCountry.setName("testCountry");

        // Override game attributes
        ReflectionTestUtils.setField(basicGame, "scoreBoard", scoreBoard);
        ReflectionTestUtils.setField(basicGame, "playerNames", playerNames);
        ReflectionTestUtils.setField(basicGame, "hintHandler", hintHandler);
        ReflectionTestUtils.setField(basicGame, "timer", new Timer());
        ReflectionTestUtils.setField(basicGame, "currentCountry", testCountry);

        // Mock some repository methods and service methods
        doNothing().when(webSocketService).sendToLobby(anyLong(), anyString(), any());
        doNothing().when(webSocketService).sendToPlayerInLobby(anyString(), anyString(), anyString(), any());
        doNothing().when(scoreBoard).updateTotalScores();
        doNothing().when(scoreBoard).computeLeaderBoardScore();

        basicGame.endRound();

        // check whether the followin methods were called
        verify(webSocketService, times(1)).sendToLobby(anyLong(), eq("/round-end"), eq("{}"));
        verify(scoreBoard, times(5)).getCurrentCorrectGuessPerPlayer(anyString());
        verify(scoreBoard, times(5)).setCurrentCorrectGuessPerPlayer(anyString(), eq(false));
        verify(scoreBoard, times(5)).setCurrentTimeUntilCorrectGuessPerPlayer(anyString(), anyInt());
        verify(scoreBoard, times(5)).getCurrentNumberOfWrongGuessesPerPlayer(anyString());
        // is zero because no guesses have been submitted yet
        verify(scoreBoard, times(0)).setCurrentNumberOfWrongGuessesPerPlayer(anyString(), eq(0));
        verify(scoreBoard, times(1)).updateTotalScores();
        verify(scoreBoard, times(1)).computeLeaderBoardScore();
        verify(webSocketService, times(1)).sendToLobby(anyLong(), eq("/correct-country"), any(CorrectGuessDTO.class));
        verify(basicGame, times(0)).endGame();

        // check whether the attribute round was incremented
        assertEquals(1, ReflectionTestUtils.getField(basicGame, "round"));
        assertEquals(null, ReflectionTestUtils.getField(basicGame, "correctGuess"));
        assertEquals(null, ReflectionTestUtils.getField(basicGame, "currentCountry"));
        assertEquals(null, ReflectionTestUtils.getField(basicGame, "startTime"));
        assertEquals(null, ReflectionTestUtils.getField(basicGame, "hintHandler"));
    }

    @Test
    public void testEndRoundAdvancedMode_firstRound() {
        // given
        ArrayList<String> playerNames = new ArrayList();
        playerNames.add("player1");
        playerNames.add("player2");
        playerNames.add("player3");
        playerNames.add("player4");
        playerNames.add("player5");

        Country testCountry = new Country();
        testCountry.setName("testCountry");

        // Override game attributes
        ReflectionTestUtils.setField(advancedGame, "scoreBoard", scoreBoard);
        ReflectionTestUtils.setField(advancedGame, "playerNames", playerNames);
        ReflectionTestUtils.setField(advancedGame, "hintHandler", hintHandler);
        ReflectionTestUtils.setField(advancedGame, "timer", new Timer());
        ReflectionTestUtils.setField(advancedGame, "numRounds", 4);
        ReflectionTestUtils.setField(advancedGame, "round", 3);
        ReflectionTestUtils.setField(advancedGame, "playAgainTimeWindow", 1);
        ReflectionTestUtils.setField(advancedGame, "currentCountry", testCountry);

        // Mock some repository methods and service methods
        doNothing().when(hintHandler).stopSendingHints();
        doNothing().when(webSocketService).sendToLobby(anyLong(), anyString(), any());
        doNothing().when(webSocketService).sendToPlayerInLobby(anyString(), anyString(), anyString(), any());
        doNothing().when(scoreBoard).updateTotalScores();
        doNothing().when(scoreBoard).computeLeaderBoardScore();
        when(lobbyRepository.findByLobbyId(anyLong())).thenReturn(playAgainLobby);

        advancedGame.endRound();

        // check whether the followin methods were called
        verify(hintHandler, times(1)).stopSendingHints();
        verify(webSocketService, times(1)).sendToLobby(anyLong(), eq("/round-end"), eq("{}"));
        verify(scoreBoard, times(5)).getCurrentCorrectGuessPerPlayer(anyString());
        verify(scoreBoard, times(5)).setCurrentCorrectGuessPerPlayer(anyString(), eq(false));
        verify(scoreBoard, times(5)).setCurrentTimeUntilCorrectGuessPerPlayer(anyString(), anyInt());
        verify(scoreBoard, times(5)).getCurrentNumberOfWrongGuessesPerPlayer(anyString());
        // is zero because no guesses have been submitted yet
        verify(scoreBoard, times(0)).setCurrentNumberOfWrongGuessesPerPlayer(anyString(), eq(0));
        verify(scoreBoard, times(1)).updateTotalScores();
        verify(scoreBoard, times(1)).computeLeaderBoardScore();
        verify(webSocketService, times(1)).sendToLobby(anyLong(), eq("/correct-country"), any(CorrectGuessDTO.class));
        // has to be called once as game should end
        verify(advancedGame, times(1)).endGame();

        // check whether the attribute round was incremented
        assertEquals(4, ReflectionTestUtils.getField(advancedGame, "round"));
        assertEquals(null, ReflectionTestUtils.getField(advancedGame, "correctGuess"));
        assertEquals(null, ReflectionTestUtils.getField(advancedGame, "currentCountry"));
        assertEquals(null, ReflectionTestUtils.getField(advancedGame, "startTime"));
        assertEquals(null, ReflectionTestUtils.getField(advancedGame, "hintHandler"));
    }

    @Test
    public void testEndRoundAdvancedMode_lastRound() {
        // given
        ArrayList<String> playerNames = new ArrayList();
        playerNames.add("player1");
        playerNames.add("player2");
        playerNames.add("player3");
        playerNames.add("player4");
        playerNames.add("player5");

        Country testCountry = new Country();
        testCountry.setName("testCountry");

        // Override game attributes
        ReflectionTestUtils.setField(advancedGame, "scoreBoard", scoreBoard);
        ReflectionTestUtils.setField(advancedGame, "playerNames", playerNames);
        ReflectionTestUtils.setField(advancedGame, "hintHandler", hintHandler);
        ReflectionTestUtils.setField(advancedGame, "timer", new Timer());
        ReflectionTestUtils.setField(advancedGame, "numRounds", 4);
        ReflectionTestUtils.setField(advancedGame, "round", 3);
        ReflectionTestUtils.setField(advancedGame, "playAgainTimeWindow", 1);
        ReflectionTestUtils.setField(advancedGame, "currentCountry", testCountry);

        // Mock some repository methods and service methods
        doNothing().when(hintHandler).stopSendingHints();
        doNothing().when(webSocketService).sendToLobby(anyLong(), anyString(), any());
        doNothing().when(webSocketService).sendToPlayerInLobby(anyString(), anyString(), anyString(), any());
        doNothing().when(scoreBoard).updateTotalScores();
        doNothing().when(scoreBoard).computeLeaderBoardScore();
        when(lobbyRepository.findByLobbyId(anyLong())).thenReturn(playAgainLobby);

        advancedGame.endRound();

        // check whether the followin methods were called
        verify(hintHandler, times(1)).stopSendingHints();
        verify(webSocketService, times(1)).sendToLobby(anyLong(), eq("/round-end"), eq("{}"));
        verify(scoreBoard, times(5)).getCurrentCorrectGuessPerPlayer(anyString());
        verify(scoreBoard, times(5)).setCurrentCorrectGuessPerPlayer(anyString(), eq(false));
        verify(scoreBoard, times(5)).setCurrentTimeUntilCorrectGuessPerPlayer(anyString(), anyInt());
        verify(scoreBoard, times(5)).getCurrentNumberOfWrongGuessesPerPlayer(anyString());
        // is zero because no guesses have been submitted yet
        verify(scoreBoard, times(0)).setCurrentNumberOfWrongGuessesPerPlayer(anyString(), eq(0));
        verify(scoreBoard, times(1)).updateTotalScores();
        verify(scoreBoard, times(1)).computeLeaderBoardScore();
        verify(webSocketService, times(1)).sendToLobby(anyLong(), eq("/correct-country"), any(CorrectGuessDTO.class));
        // has to be called once as game should end
        verify(advancedGame, times(1)).endGame();

        // check whether the attribute round was incremented
        assertEquals(4, ReflectionTestUtils.getField(advancedGame, "round"));
        assertEquals(null, ReflectionTestUtils.getField(advancedGame, "correctGuess"));
        assertEquals(null, ReflectionTestUtils.getField(advancedGame, "currentCountry"));
        assertEquals(null, ReflectionTestUtils.getField(advancedGame, "startTime"));
        assertEquals(null, ReflectionTestUtils.getField(advancedGame, "hintHandler"));
    }

    @Test
    public void testRemovePlayersFromBasicMode() {
        ArrayList<String> playerNames = new ArrayList();
        playerNames.add("player1");
        playerNames.add("player2");
        playerNames.add("player3");
        playerNames.add("player4");
        playerNames.add("player5");

        // Override game attributes
        ReflectionTestUtils.setField(basicGame, "playerNames", playerNames);

        // call method to be tested
        basicGame.removePlayer("player2");

        // check whether the player was removed
        ArrayList<String> actualPlayers = (ArrayList<String>) ReflectionTestUtils.getField(basicGame, "playerNames");
        assertEquals(4, actualPlayers.size());
        assertEquals("player1", actualPlayers.get(0));
        assertEquals("player3", actualPlayers.get(1));
        assertEquals("player4", actualPlayers.get(2));
        assertEquals("player5", actualPlayers.get(3));
    }

    @Test
    public void testRemovePlayersFromAdvancedMode() {
        ArrayList<String> playerNames = new ArrayList();
        playerNames.add("player1");
        playerNames.add("player2");
        playerNames.add("player3");
        playerNames.add("player4");
        playerNames.add("player5");

        // Override game attributes
        ReflectionTestUtils.setField(advancedGame, "playerNames", playerNames);

        // call method to be tested
        advancedGame.removePlayer("player2");

        // check whether the player was removed
        ArrayList<String> actualPlayers = (ArrayList<String>) ReflectionTestUtils.getField(advancedGame, "playerNames");
        assertEquals(4, actualPlayers.size());
        assertEquals("player1", actualPlayers.get(0));
        assertEquals("player3", actualPlayers.get(1));
        assertEquals("player4", actualPlayers.get(2));
        assertEquals("player5", actualPlayers.get(3));
    }
}
