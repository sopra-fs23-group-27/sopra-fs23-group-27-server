package ch.uzh.ifi.hase.soprafs23.entity;

import static org.mockito.Mockito.*;

import java.util.ArrayList;

import ch.uzh.ifi.hase.soprafs23.service.WebSocketService;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.GameStatsDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.incoming.AuthenticateDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing.CorrectGuessDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing.RoundDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing.WSConnectedDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;


import ch.uzh.ifi.hase.soprafs23.repository.CountryRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.service.CountryHandlerService;


import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

    CountryHandlerService countryHandlerService;
    WebSocketService webSocketService;
    CountryRepository countryRepository;
    PlayerRepository playerRepository;
    SimpMessagingTemplate messagingTemplate;
    ScoreBoard scoreBoard;
    Lobby lobby;


    @BeforeEach
    public void setUp() {
        // mock countryHandlerService
        this.countryHandlerService = mock(CountryHandlerService.class);

        // mock webSocketService
        this.webSocketService = mock(WebSocketService.class);

        // mock countryRepository
        this.countryRepository = mock(CountryRepository.class);

        // mock playerRepository
        this.playerRepository = mock(PlayerRepository.class);

        // mock messagingTemplate
        this.messagingTemplate = mock(SimpMessagingTemplate.class);

        // Mock the WebSocketService
        webSocketService = mock(WebSocketService.class);

        // mock lobby
        this.lobby = mock(Lobby.class);
        when(this.lobby.getLobbyId()).thenReturn(1L);

        // mock scoreBoard
        this.scoreBoard = mock(ScoreBoard.class);
        doNothing().when(this.scoreBoard).setCurrentCorrectGuessPerPlayer(anyString(), anyBoolean());
    }

    @Test
    public void testCorrectValidateGuess() {

        // load the game class
        Game game = new Game(this.countryHandlerService, this.webSocketService, this.countryRepository, this.playerRepository,  this.lobby);
        Game spyGame = spy(game);

        // override the attribute scoreBoard
        ReflectionTestUtils.setField(spyGame, "scoreBoard", scoreBoard);

        // manually set start time 
        // (this must be done because startRound() was not called and therefore the attibute startTime is not set)
        ReflectionTestUtils.setField(spyGame, "startTime", 1000L);

        // set attribute correctGuess (note: correctGuess is passed through lower 
        // and a regex that removes all whitespaces)
        ReflectionTestUtils.setField(spyGame, "correctGuess", "ch");

        // mock this function to return void this.scoreBoard.setCurrentCorrectGuessPerPlayer(PlayerName, true)
        doNothing().when(spyGame).endRound();
        assertTrue(spyGame.validateGuess("Player1", "CH", "wsConnectionId"));

    }

    @Test
    public void testCorrectValidateGuessWithSpace() {

        // load the game class
        Game game = new Game(this.countryHandlerService, this.webSocketService, this.countryRepository, this.playerRepository,  this.lobby);
        Game spyGame = spy(game);

        // override the attribute scoreBoard
        ReflectionTestUtils.setField(spyGame, "scoreBoard", scoreBoard);

        // set attribute correctGuess
        ReflectionTestUtils.setField(spyGame, "correctGuess", "ch");

        // manually set start time 
        // (this must be done because startRound() was not called and therefore the attibute startTime is not set)
        ReflectionTestUtils.setField(spyGame, "startTime", 1000L);

        // mock this function to return void this.scoreBoard.setCurrentCorrectGuessPerPlayer(PlayerName, false)
        doNothing().when(spyGame).endRound();
        assertTrue(spyGame.validateGuess("Player1", "CH ", "wsConnectionId"));
    }

    @Test
    public void testLowerspacedInputValidateGuess() {

        // load the game class
        Game game = new Game(
            this.countryHandlerService, 
            this.webSocketService, 
            this.countryRepository, 
            this.playerRepository,  
            this.lobby);
        Game spyGame = spy(game);

        // override the attribute scoreBoard
        ReflectionTestUtils.setField(spyGame, "scoreBoard", scoreBoard);

        // set attribute correctGuess
        ReflectionTestUtils.setField(spyGame, "correctGuess", "ch");

        // manually set start time 
        // (this must be done because startRound() was not called and therefore the attibute startTime is not set)
        // Note that validate guess makes use uf the private computePassedTime function
        ReflectionTestUtils.setField(spyGame, "startTime", 1000L);

        // mock this function to return void this.scoreBoard.setCurrentCorrectGuessPerPlayer(PlayerName, false)
        doNothing().when(spyGame).endRound();
        assertTrue(spyGame.validateGuess("Player1", "ch", "wsConnectionId"));
    }

    @Test
    public void testWrongValidateGuess() {

        // load the game class
        Game game = new Game(
            this.countryHandlerService, 
            this.webSocketService, 
            this.countryRepository, 
            this.playerRepository,  
            this.lobby);

        // override the attribute scoreBoard
        ReflectionTestUtils.setField(game, "scoreBoard", scoreBoard);

        // set attribute correctGuess
        ReflectionTestUtils.setField(game, "correctGuess", "ch");

        // mock this function to return void this.scoreBoard.setCurrentCorrectGuessPerPlayer(PlayerName, false)
        assertFalse(game.validateGuess("Player1", "DE", "wsConnectionId"));
    }

    @Test
    public void testUpdateCorrectGuess() {

        // mock the countryRepository
        Country testCountry = new Country();
        testCountry.setCountryCode("US");
        testCountry.setName("United States");
        when(this.countryRepository.findByCountryCode(anyString())).thenReturn(testCountry);
        
        // load the game class
        Game game = new Game(
            this.countryHandlerService, 
            this.webSocketService, 
            this.countryRepository, 
            this.playerRepository,  
            this.lobby);

        // override attribute correctGuess
        ReflectionTestUtils.setField(game, "correctGuess", "ch");

        game.updateCorrectGuess("US");

        assertEquals("unitedstates", ReflectionTestUtils.getField(game, "correctGuess"));
    }

    @Test
    public void testSendsGameStatsDTO() {
        // load the game class
        Game game = new Game(
            this.countryHandlerService, 
            this.webSocketService, 
            this.countryRepository, 
            this.playerRepository,  
            this.lobby);

        Player testPlayer = new Player();
        testPlayer.setPlayerName("testPlayer");
        testPlayer.setCreator(false);
        testPlayer.setWsConnectionId("test-websocket-key");

        // make a list with the testplayer in it
        ArrayList<Player> testPlayerList = new ArrayList<Player>();

        // mock the playerRepository
        when(this.playerRepository.findByLobbyId(anyLong())).thenReturn(testPlayerList);

        // make ArrayList with the testPlayer in it
        ArrayList<String> testArrayList = new ArrayList<String>();
        testArrayList.add("testPlayer");
        
        // mock the scoreboard with player1
        ScoreBoard scoreBoard = new ScoreBoard(testArrayList);
        scoreBoard.setCurrentCorrectGuessPerPlayer("testPlayer", true);
        scoreBoard.setCurrentNumberOfWrongGuessesPerPlayer("testPlayer", 0);
        scoreBoard.setCurrentTimeUntilCorrectGuessPerPlayer("testPlayer", 0);
        scoreBoard.updateTotalScores();

        // override the attribute scoreBoard
        ReflectionTestUtils.setField(game, "scoreBoard", scoreBoard);


        game.sendStatsToLobby();

        Long gameId = 0L;
        // verify that the WebSocketService.sendToLobby() method was called with the first hint immediately
        verify(webSocketService).sendToPlayerInLobby(
            eq("test-websocket-key"),
            eq("/score-board"),
            eq(gameId.toString()),
            any(GameStatsDTO.class));    
    }

    @Test
    public void testSendsGameRoundDTO() {
        // load the game class
        Game game = new Game(
            this.countryHandlerService, 
            this.webSocketService, 
            this.countryRepository, 
            this.playerRepository,  
            this.lobby);

        game.sendRoundToLobby();

        verify(webSocketService).sendToLobby(eq(1L), eq("/round"), any(RoundDTO.class));
    }

    @Test
    public void testSendCorrectGuessToLobby() {
        
        // load the game class
        Game game = new Game(
            this.countryHandlerService, 
            this.webSocketService, 
            this.countryRepository, 
            this.playerRepository,  
            this.lobby);

        game.sendCorrectGuessToLobby();

        verify(webSocketService).sendToLobby(eq(1L), eq("/correct-country"), any(CorrectGuessDTO.class));
    }

}
