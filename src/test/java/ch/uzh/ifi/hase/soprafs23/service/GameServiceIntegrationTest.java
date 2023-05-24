package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.repository.CountryRepository;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.AdvancedLobbyCreateDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.BasicLobbyCreateDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@SpringBootTest
public class GameServiceIntegrationTest {

    @Autowired
    private LobbyRepository lobbyRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private CountryService countryService;
    @Autowired
    private LobbyService lobbyService;
    @Autowired
    private GameService gameService;

    @Mock
    private WebSocketService webSocketService;

    private Player testPlayer1;
    private Player testPlayer2;
    private Player testPlayer3;
    private BasicLobbyCreateDTO basicLobbyCreateDTO;
    private AdvancedLobbyCreateDTO advancedLobbyCreateDTO;
    private GameRepository gameRepository;

    @BeforeEach
    void setUp() {
        // given
        testPlayer1 = new Player();
        testPlayer1.setId(1L);
        testPlayer1.setPlayerName("testPlayer1");
        testPlayer1.setToken("testToken1");
        testPlayer1.setWsConnectionId("testWsConnectionId1");
        playerRepository.save(testPlayer1);
        playerRepository.flush();

        testPlayer2 = new Player();
        testPlayer2.setId(2L);
        testPlayer2.setPlayerName("testPlayer2");
        testPlayer2.setToken("testToken2");
        testPlayer2.setWsConnectionId("testWsConnectionId2");
        playerRepository.save(testPlayer2);
        playerRepository.flush();

        testPlayer3 = new Player();
        testPlayer3.setId(3L);
        testPlayer3.setPlayerName("testPlayer3");
        testPlayer3.setToken("testToken3");
        testPlayer3.setWsConnectionId("testWsConnectionId3");
        playerRepository.save(testPlayer3);
        playerRepository.flush();

        basicLobbyCreateDTO = new BasicLobbyCreateDTO();
        basicLobbyCreateDTO.setLobbyName("testBasicLobby");
        basicLobbyCreateDTO.setIsPublic(true);
        basicLobbyCreateDTO.setNumSeconds(5);
        basicLobbyCreateDTO.setNumOptions(4);
        basicLobbyCreateDTO.setNumRounds(2);
        basicLobbyCreateDTO.setContinent(new ArrayList<String>(
                Arrays.asList("Africa", "Americas", "Asia", "Europe", "Oceania")));

        advancedLobbyCreateDTO = new AdvancedLobbyCreateDTO();
        advancedLobbyCreateDTO.setLobbyName("testAdvancedLobby");
        advancedLobbyCreateDTO.setIsPublic(true);
        advancedLobbyCreateDTO.setNumSeconds(5);
        advancedLobbyCreateDTO.setNumSecondsUntilHint(5);
        advancedLobbyCreateDTO.setHintInterval(5);
        advancedLobbyCreateDTO.setMaxNumGuesses(10);
        advancedLobbyCreateDTO.setNumRounds(2);
        advancedLobbyCreateDTO.setContinent(new ArrayList<String>(
                Arrays.asList("Africa", "Americas", "Asia", "Europe", "Oceania")));

        // mock webSocketService
        MockitoAnnotations.openMocks(this);

        Mockito.doNothing().when(webSocketService).sendToLobby(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.doNothing().when(webSocketService).sendToPlayerInLobby(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.doNothing().when(webSocketService).sendToPlayer(Mockito.any(), Mockito.any(), Mockito.any());
//        Mockito.when(WebSocketService.getIdentity(Mockito.any())).thenReturn("smha");
//        Mockito.when(SimpMessageHeaderAccessor.getUser()).thenReturn(Principal.class)
    }

    @AfterEach
    void tearDown() {
        lobbyRepository.deleteAll();
        playerRepository.deleteAll();
    }

    @Test
    @Transactional
    void testCreateJoinAndStartPublicBasicGame() throws InterruptedException {
        // create lobby
        Lobby basicLobbyInput = DTOMapper.INSTANCE.convertBasicLobbyCreateDTOtoEntity(basicLobbyCreateDTO);
        BasicLobby testBasicLobbyCreated = lobbyService.createBasicLobby(basicLobbyInput, testPlayer1.getToken(), true);
        Long lobbyId = testBasicLobbyCreated.getLobbyId();

        // find lobby with lobbyId
        Lobby foundLobby = lobbyRepository.findByLobbyId(lobbyId);
        BasicLobby foundBasicLobby = (BasicLobby) foundLobby;

        // let testPlayer2 join lobby
        lobbyService.joinLobby(foundBasicLobby, testPlayer2.getToken(), testPlayer2.getWsConnectionId());
        lobbyService.joinLobby(foundBasicLobby, testPlayer3.getToken(), testPlayer3.getWsConnectionId());
        foundLobby = lobbyRepository.findByLobbyId(lobbyId);

        // check if player is in lobby
        assertEquals("testBasicLobby", foundBasicLobby.getLobbyName());
        assertEquals(3, foundBasicLobby.getJoinedPlayerNames().size());
        assertTrue(foundBasicLobby.getJoinedPlayerNames().contains(testPlayer1.getPlayerName()));
        assertTrue(foundBasicLobby.getJoinedPlayerNames().contains(testPlayer2.getPlayerName()));
        assertTrue(foundBasicLobby.getJoinedPlayerNames().contains(testPlayer3.getPlayerName()));

        // start game
        // Call the method to be tested
        gameService.startGame(foundBasicLobby);

        // check if game is started
        Game foundGame = gameRepository.findByLobbyId(lobbyId);
        assertEquals(lobbyId, ReflectionTestUtils.getField(foundGame, "gameId"));
        assertEquals("BASIC", ReflectionTestUtils.getField(foundGame, "gameMode"));
        assertEquals(foundBasicLobby.getContinent(), ReflectionTestUtils.getField(foundGame, "continent"));
        assertEquals(foundBasicLobby.getNumRounds(), ReflectionTestUtils.getField(foundGame, "numRounds"));
        assertEquals(foundBasicLobby.getNumSeconds(), ReflectionTestUtils.getField(foundGame, "numSeconds"));
        assertEquals(foundBasicLobby.getNumOptions(), ReflectionTestUtils.getField(foundGame, "numOptions"));

        // check round number of game
        assertEquals(0, ReflectionTestUtils.getField(foundGame, "round"));

        // check if game has correct number players
        ArrayList<String> allPlayersInGame = (ArrayList<String>) ReflectionTestUtils.getField(foundGame, "playerNames");
        assertTrue(allPlayersInGame.contains(testPlayer1.getPlayerName()));
        assertTrue(allPlayersInGame.contains(testPlayer2.getPlayerName()));
        assertTrue(allPlayersInGame.contains(testPlayer3.getPlayerName()));

        // check if game has correct number of countries
        ArrayList<String> allCountriesInGame = (ArrayList<String>) ReflectionTestUtils.getField(foundGame, "allCountryCodes");
        assertEquals(foundBasicLobby.getNumRounds(), allCountriesInGame.size());

        // wait until round is finished
        webSocketService.wait(12000);

        // check some metrics of scoreboard for players
        ScoreBoard scoreBoard = (ScoreBoard) ReflectionTestUtils.getField(foundGame, "scoreBoard");
        assertEquals(0, scoreBoard.getCurrentScorePerPlayer(testPlayer1.getPlayerName()));
        assertEquals(0, scoreBoard.getCurrentScorePerPlayer(testPlayer2.getPlayerName()));
        assertEquals(0, scoreBoard.getCurrentScorePerPlayer(testPlayer3.getPlayerName()));

        assertEquals(0, scoreBoard.getCurrentNumberOfWrongGuessesPerPlayer(testPlayer1.getPlayerName()));
        assertEquals(0, scoreBoard.getCurrentNumberOfWrongGuessesPerPlayer(testPlayer2.getPlayerName()));
        assertEquals(0, scoreBoard.getCurrentNumberOfWrongGuessesPerPlayer(testPlayer3.getPlayerName()));

        // check round number of game
        assertEquals(1, ReflectionTestUtils.getField(foundGame, "round"));

        // delete game
        gameRepository.removeGame(lobbyId);
    }

    @Test
    @Transactional
    void testCreateJoinStartPublicBasicGameAndRemovePlayer() throws InterruptedException {
        // create lobby
        Lobby basicLobbyInput = DTOMapper.INSTANCE.convertBasicLobbyCreateDTOtoEntity(basicLobbyCreateDTO);
        BasicLobby testBasicLobbyCreated = lobbyService.createBasicLobby(basicLobbyInput, testPlayer1.getToken(), true);
        Long lobbyId = testBasicLobbyCreated.getLobbyId();

        // find lobby with lobbyId
        Lobby foundLobby = lobbyRepository.findByLobbyId(lobbyId);
        BasicLobby foundBasicLobby = (BasicLobby) foundLobby;

        // let testPlayer2 join lobby
        lobbyService.joinLobby(foundBasicLobby, testPlayer2.getToken(), testPlayer2.getWsConnectionId());
        lobbyService.joinLobby(foundBasicLobby, testPlayer3.getToken(), testPlayer3.getWsConnectionId());
        foundLobby = lobbyRepository.findByLobbyId(lobbyId);

        // check if player is in lobby
        assertEquals("testBasicLobby", foundBasicLobby.getLobbyName());
        assertEquals(3, foundBasicLobby.getJoinedPlayerNames().size());
        assertTrue(foundBasicLobby.getJoinedPlayerNames().contains(testPlayer1.getPlayerName()));
        assertTrue(foundBasicLobby.getJoinedPlayerNames().contains(testPlayer2.getPlayerName()));
        assertTrue(foundBasicLobby.getJoinedPlayerNames().contains(testPlayer3.getPlayerName()));

        // start game
        // Call the method to be tested
        gameService.startGame(foundBasicLobby);

        // check if game is started
        Game foundGame = gameRepository.findByLobbyId(lobbyId);
        assertEquals(lobbyId, ReflectionTestUtils.getField(foundGame, "gameId"));
        assertEquals("BASIC", ReflectionTestUtils.getField(foundGame, "gameMode"));
        assertEquals(foundBasicLobby.getContinent(), ReflectionTestUtils.getField(foundGame, "continent"));
        assertEquals(foundBasicLobby.getNumRounds(), ReflectionTestUtils.getField(foundGame, "numRounds"));
        assertEquals(foundBasicLobby.getNumSeconds(), ReflectionTestUtils.getField(foundGame, "numSeconds"));
        assertEquals(foundBasicLobby.getNumOptions(), ReflectionTestUtils.getField(foundGame, "numOptions"));

        // check round number of game
        assertEquals(0, ReflectionTestUtils.getField(foundGame, "round"));

        // check if game has correct number players
        ArrayList<String> allPlayersInGame = (ArrayList<String>) ReflectionTestUtils.getField(foundGame, "playerNames");
        assertTrue(allPlayersInGame.contains(testPlayer1.getPlayerName()));
        assertTrue(allPlayersInGame.contains(testPlayer2.getPlayerName()));
        assertTrue(allPlayersInGame.contains(testPlayer3.getPlayerName()));

        // check if game has correct number of countries
        ArrayList<String> allCountriesInGame = (ArrayList<String>) ReflectionTestUtils.getField(foundGame, "allCountryCodes");
        assertEquals(foundBasicLobby.getNumRounds(), allCountriesInGame.size());

        // wait until round is finished
        webSocketService.wait(13000);

        // check some metrics of scoreboard for players
        ScoreBoard scoreBoard = (ScoreBoard) ReflectionTestUtils.getField(foundGame, "scoreBoard");
        assertEquals(0, scoreBoard.getCurrentScorePerPlayer(testPlayer1.getPlayerName()));
        assertEquals(0, scoreBoard.getCurrentScorePerPlayer(testPlayer2.getPlayerName()));
        assertEquals(0, scoreBoard.getCurrentScorePerPlayer(testPlayer3.getPlayerName()));

        assertEquals(0, scoreBoard.getCurrentNumberOfWrongGuessesPerPlayer(testPlayer1.getPlayerName()));
        assertEquals(0, scoreBoard.getCurrentNumberOfWrongGuessesPerPlayer(testPlayer2.getPlayerName()));
        assertEquals(0, scoreBoard.getCurrentNumberOfWrongGuessesPerPlayer(testPlayer3.getPlayerName()));

        // check round number of game
        assertEquals(1, ReflectionTestUtils.getField(foundGame, "round"));

        // Call the method to be tested
        gameService.removePlayerFromGame(lobbyId, testPlayer3.getPlayerName());

        Game foundGameAfterRemoval = gameRepository.findByLobbyId(lobbyId);

        // check if game has correct number players
        ArrayList<String> allPlayersInGameAfterRemoval = (ArrayList<String>) ReflectionTestUtils.getField(foundGameAfterRemoval, "playerNames");
        assertTrue(allPlayersInGameAfterRemoval.contains(testPlayer1.getPlayerName()));
        assertTrue(allPlayersInGameAfterRemoval.contains(testPlayer2.getPlayerName()));
        assertFalse(allPlayersInGameAfterRemoval.contains(testPlayer3.getPlayerName()));

        // delete game
        gameRepository.removeGame(lobbyId);
    }

    @Test
    @Transactional
    void testCreateJoinAndStartPublicAdvancedGame() throws InterruptedException {
        // create lobby
        Lobby advancedLobbyInput = DTOMapper.INSTANCE.convertAdvancedLobbyCreateDTOtoEntity(advancedLobbyCreateDTO);
        AdvancedLobby testAdvancedLobbyCreated = lobbyService.createAdvancedLobby(advancedLobbyInput, testPlayer1.getToken(), true);
        Long lobbyId = testAdvancedLobbyCreated.getLobbyId();

        // find lobby with lobbyId
        Lobby foundLobby = lobbyRepository.findByLobbyId(lobbyId);
        AdvancedLobby foundAdvancedLobby = (AdvancedLobby) foundLobby;

        // let testPlayer2 join lobby
        lobbyService.joinLobby(foundAdvancedLobby, testPlayer2.getToken(), testPlayer2.getWsConnectionId());
        lobbyService.joinLobby(foundAdvancedLobby, testPlayer3.getToken(), testPlayer3.getWsConnectionId());
        foundLobby = lobbyRepository.findByLobbyId(lobbyId);

        // check if player is in lobby
        assertEquals("testAdvancedLobby", foundAdvancedLobby.getLobbyName());
        assertEquals(3, foundAdvancedLobby.getJoinedPlayerNames().size());
        assertTrue(foundAdvancedLobby.getJoinedPlayerNames().contains(testPlayer1.getPlayerName()));
        assertTrue(foundAdvancedLobby.getJoinedPlayerNames().contains(testPlayer2.getPlayerName()));
        assertTrue(foundAdvancedLobby.getJoinedPlayerNames().contains(testPlayer3.getPlayerName()));

        // start game
        // Call the method to be tested
        gameService.startGame(foundAdvancedLobby);

        // check if game is started
        Game foundGame = gameRepository.findByLobbyId(lobbyId);
        assertEquals(lobbyId, ReflectionTestUtils.getField(foundGame, "gameId"));
        assertEquals("ADVANCED", ReflectionTestUtils.getField(foundGame, "gameMode"));
        assertEquals(foundAdvancedLobby.getContinent(), ReflectionTestUtils.getField(foundGame, "continent"));
        assertEquals(foundAdvancedLobby.getNumRounds(), ReflectionTestUtils.getField(foundGame, "numRounds"));
        assertEquals(foundAdvancedLobby.getNumSeconds(), ReflectionTestUtils.getField(foundGame, "numSeconds"));
        assertEquals(foundAdvancedLobby.getNumSecondsUntilHint(), ReflectionTestUtils.getField(foundGame, "numSecondsUntilHint"));
        assertEquals(foundAdvancedLobby.getHintInterval(), ReflectionTestUtils.getField(foundGame, "hintInterval"));
        assertEquals(foundAdvancedLobby.getMaxNumGuesses(), ReflectionTestUtils.getField(foundGame, "maxNumGuesses"));

        // check round number of game
        assertEquals(0, ReflectionTestUtils.getField(foundGame, "round"));

        // check if game has correct number players
        ArrayList<String> allPlayersInGame = (ArrayList<String>) ReflectionTestUtils.getField(foundGame, "playerNames");
        assertTrue(allPlayersInGame.contains(testPlayer1.getPlayerName()));
        assertTrue(allPlayersInGame.contains(testPlayer2.getPlayerName()));
        assertTrue(allPlayersInGame.contains(testPlayer3.getPlayerName()));

        // check if game has correct number of countries
        ArrayList<String> allCountriesInGame = (ArrayList<String>) ReflectionTestUtils.getField(foundGame, "allCountryCodes");
        assertEquals(foundAdvancedLobby.getNumRounds(), allCountriesInGame.size());

        // wait until round is finished
        webSocketService.wait(12000);

        // check some metrics of scoreboard for players
        ScoreBoard scoreBoard = (ScoreBoard) ReflectionTestUtils.getField(foundGame, "scoreBoard");
        assertEquals(0, scoreBoard.getCurrentScorePerPlayer(testPlayer1.getPlayerName()));
        assertEquals(0, scoreBoard.getCurrentScorePerPlayer(testPlayer2.getPlayerName()));
        assertEquals(0, scoreBoard.getCurrentScorePerPlayer(testPlayer3.getPlayerName()));

        assertEquals(0, scoreBoard.getCurrentNumberOfWrongGuessesPerPlayer(testPlayer1.getPlayerName()));
        assertEquals(0, scoreBoard.getCurrentNumberOfWrongGuessesPerPlayer(testPlayer2.getPlayerName()));
        assertEquals(0, scoreBoard.getCurrentNumberOfWrongGuessesPerPlayer(testPlayer3.getPlayerName()));

        // check round number of game
        assertEquals(1, ReflectionTestUtils.getField(foundGame, "round"));

        // delete game
        gameRepository.removeGame(lobbyId);
    }

    @Test
    @Transactional
    void testCreateJoinStartPublicAdvancedGameAndRemovePlayer() throws InterruptedException {
        // create lobby
        Lobby advancedLobbyInput = DTOMapper.INSTANCE.convertAdvancedLobbyCreateDTOtoEntity(advancedLobbyCreateDTO);
        AdvancedLobby testAdvancedLobbyCreated = lobbyService.createAdvancedLobby(advancedLobbyInput, testPlayer1.getToken(), true);
        Long lobbyId = testAdvancedLobbyCreated.getLobbyId();

        // find lobby with lobbyId
        Lobby foundLobby = lobbyRepository.findByLobbyId(lobbyId);
        AdvancedLobby foundAdvancedLobby = (AdvancedLobby) foundLobby;

        // let testPlayer2 join lobby
        lobbyService.joinLobby(foundAdvancedLobby, testPlayer2.getToken(), testPlayer2.getWsConnectionId());
        lobbyService.joinLobby(foundAdvancedLobby, testPlayer3.getToken(), testPlayer3.getWsConnectionId());
        foundLobby = lobbyRepository.findByLobbyId(lobbyId);

        // check if player is in lobby
        assertEquals("testAdvancedLobby", foundAdvancedLobby.getLobbyName());
        assertEquals(3, foundAdvancedLobby.getJoinedPlayerNames().size());
        assertTrue(foundAdvancedLobby.getJoinedPlayerNames().contains(testPlayer1.getPlayerName()));
        assertTrue(foundAdvancedLobby.getJoinedPlayerNames().contains(testPlayer2.getPlayerName()));
        assertTrue(foundAdvancedLobby.getJoinedPlayerNames().contains(testPlayer3.getPlayerName()));

        // Call the method to be tested
        gameService.startGame(foundAdvancedLobby);

        // check if game is started
        Game foundGame = gameRepository.findByLobbyId(lobbyId);
        assertEquals(lobbyId, ReflectionTestUtils.getField(foundGame, "gameId"));
        assertEquals("ADVANCED", ReflectionTestUtils.getField(foundGame, "gameMode"));
        assertEquals(foundAdvancedLobby.getContinent(), ReflectionTestUtils.getField(foundGame, "continent"));
        assertEquals(foundAdvancedLobby.getNumRounds(), ReflectionTestUtils.getField(foundGame, "numRounds"));
        assertEquals(foundAdvancedLobby.getNumSeconds(), ReflectionTestUtils.getField(foundGame, "numSeconds"));
        assertEquals(foundAdvancedLobby.getNumSecondsUntilHint(), ReflectionTestUtils.getField(foundGame, "numSecondsUntilHint"));
        assertEquals(foundAdvancedLobby.getHintInterval(), ReflectionTestUtils.getField(foundGame, "hintInterval"));
        assertEquals(foundAdvancedLobby.getMaxNumGuesses(), ReflectionTestUtils.getField(foundGame, "maxNumGuesses"));

        // check round number of game
        assertEquals(0, ReflectionTestUtils.getField(foundGame, "round"));

        // check if game has correct number players
        ArrayList<String> allPlayersInGame = (ArrayList<String>) ReflectionTestUtils.getField(foundGame, "playerNames");
        assertTrue(allPlayersInGame.contains(testPlayer1.getPlayerName()));
        assertTrue(allPlayersInGame.contains(testPlayer2.getPlayerName()));
        assertTrue(allPlayersInGame.contains(testPlayer3.getPlayerName()));

        // check if game has correct number of countries
        ArrayList<String> allCountriesInGame = (ArrayList<String>) ReflectionTestUtils.getField(foundGame, "allCountryCodes");
        assertEquals(foundAdvancedLobby.getNumRounds(), allCountriesInGame.size());

        // wait until round is finished
        webSocketService.wait(12000);

        // check some metrics of scoreboard for players
        ScoreBoard scoreBoard = (ScoreBoard) ReflectionTestUtils.getField(foundGame, "scoreBoard");
        assertEquals(0, scoreBoard.getCurrentScorePerPlayer(testPlayer1.getPlayerName()));
        assertEquals(0, scoreBoard.getCurrentScorePerPlayer(testPlayer2.getPlayerName()));
        assertEquals(0, scoreBoard.getCurrentScorePerPlayer(testPlayer3.getPlayerName()));

        assertEquals(0, scoreBoard.getCurrentNumberOfWrongGuessesPerPlayer(testPlayer1.getPlayerName()));
        assertEquals(0, scoreBoard.getCurrentNumberOfWrongGuessesPerPlayer(testPlayer2.getPlayerName()));
        assertEquals(0, scoreBoard.getCurrentNumberOfWrongGuessesPerPlayer(testPlayer3.getPlayerName()));

        // check round number of game
        assertEquals(1, ReflectionTestUtils.getField(foundGame, "round"));

        // Call the method to be tested
        gameService.removePlayerFromGame(lobbyId, testPlayer3.getPlayerName());

        Game foundGameAfterRemoval = gameRepository.findByLobbyId(lobbyId);

        // check if game has correct number players
        ArrayList<String> allPlayersInGameAfterRemoval = (ArrayList<String>) ReflectionTestUtils.getField(foundGameAfterRemoval, "playerNames");
        assertTrue(allPlayersInGameAfterRemoval.contains(testPlayer1.getPlayerName()));
        assertTrue(allPlayersInGameAfterRemoval.contains(testPlayer2.getPlayerName()));
        assertFalse(allPlayersInGameAfterRemoval.contains(testPlayer3.getPlayerName()));

        // delete game
        gameRepository.removeGame(lobbyId);
    }


}
