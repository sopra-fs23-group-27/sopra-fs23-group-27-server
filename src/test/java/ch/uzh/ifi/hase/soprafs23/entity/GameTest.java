package ch.uzh.ifi.hase.soprafs23.entity;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;


import ch.uzh.ifi.hase.soprafs23.repository.CountryRepository;
import ch.uzh.ifi.hase.soprafs23.service.CountryHandlerService;
import ch.uzh.ifi.hase.soprafs23.entity.Lobby;


import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

    CountryHandlerService countryHandlerService;
    CountryRepository countryRepository;
    SimpMessagingTemplate messagingTemplate;
    ScoreBoard scoreBoard;
    Lobby lobby;


    @BeforeEach
    public void setUp() {
        // mock countryHandlerService
        this.countryHandlerService = mock(CountryHandlerService.class);

        // mock countryRepository
        this.countryRepository = mock(CountryRepository.class);

        // mock messagingTemplate
        this.messagingTemplate = mock(SimpMessagingTemplate.class);

        // mock lobby
        this.lobby = mock(Lobby.class);
        when(this.lobby.getLobbyId()).thenReturn(1L);
        
        // mock scoreBoard
        this.scoreBoard = mock(ScoreBoard.class);
        doNothing().when(this.scoreBoard).setCurrentCorrectGuessPerPlayer(anyString(), anyBoolean());
    }

    @Test
    public void testCorrectValidateGuess(){
        
        // load the game class
        Game game = new Game(this.countryHandlerService, this.countryRepository, this.messagingTemplate, this.lobby);

        // override the attribute scoreBoard
        ReflectionTestUtils.setField(game, "scoreBoard", scoreBoard);

        // set attribute correctGuess (note: correctGuess is passed through lower 
        // and a regex that removes all whitespaces)
        ReflectionTestUtils.setField(game, "correctGuess", "ch");

        // mock this function to return void this.scoreBoard.setCurrentCorrectGuessPerPlayer(PlayerName, true)
        assertTrue(game.validateGuess("Player1", "CH")); 

    }
    
    @Test
    public void testCorrectValidateGuessWithSpace(){
            
        // load the game class
        Game game = new Game(this.countryHandlerService, this.countryRepository, this.messagingTemplate, this.lobby);
    
        // override the attribute scoreBoard
        ReflectionTestUtils.setField(game, "scoreBoard", scoreBoard);
    
        // set attribute correctGuess
        ReflectionTestUtils.setField(game, "correctGuess", "ch");
    
        // mock this function to return void this.scoreBoard.setCurrentCorrectGuessPerPlayer(PlayerName, false)
        assertTrue(game.validateGuess("Player1", "CH "));
    }

    @Test
    public void testLowerspacedInputValidateGuess(){
                
        // load the game class
        Game game = new Game(this.countryHandlerService, this.countryRepository, this.messagingTemplate, this.lobby);
        
        // override the attribute scoreBoard
        ReflectionTestUtils.setField(game, "scoreBoard", scoreBoard);
        
        // set attribute correctGuess
        ReflectionTestUtils.setField(game, "correctGuess", "ch");
        
        // mock this function to return void this.scoreBoard.setCurrentCorrectGuessPerPlayer(PlayerName, false)
        assertTrue(game.validateGuess("Player1", "ch"));
    }

    @Test
    public void testWrongValidateGuess(){
            
        // load the game class
        Game game = new Game(this.countryHandlerService, this.countryRepository, this.messagingTemplate, this.lobby);
    
        // override the attribute scoreBoard
        ReflectionTestUtils.setField(game, "scoreBoard", scoreBoard);
    
        // set attribute correctGuess
        ReflectionTestUtils.setField(game, "correctGuess", "ch");
    
        // mock this function to return void this.scoreBoard.setCurrentCorrectGuessPerPlayer(PlayerName, false)
        assertFalse(game.validateGuess("Player1", "DE"));
    }

    @Test
    public void testUpdateCorrectGuess(){
        
        // mock the countryRepository
        Country testCountry = new Country();
        testCountry.setCountryCode("US");
        testCountry.setName("United States");
        when(this.countryRepository.findByCountryCode(anyString())).thenReturn(testCountry);
        
        // load the game class
        Game game = new Game(this.countryHandlerService, this.countryRepository, this.messagingTemplate, this.lobby);

        // override attribute correctGuess
        ReflectionTestUtils.setField(game, "correctGuess", "ch");

        game.updateCorrectGuess("US");

        assertEquals("unitedstates", ReflectionTestUtils.getField(game, "correctGuess"));
    }
    
}
