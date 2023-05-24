package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.repository.CountryRepository;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing.LobbySettingsDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class GameServiceTest {
    @Mock
    private LobbyRepository lobbyRepository;

    @Mock
    private LobbyService lobbyService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private WebSocketService webSocketService;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private PlayerService playerService;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private CountryService countryService;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameService gameService;

    private Lobby basicLobby;
    private Lobby advancedLobby;
    private Game advancedGame;
    private Game basicGame;
    private SimpMessageHeaderAccessor smha;
    private Player testPlayer1;
    private Player testPlayer2;
    private ArrayList allCountries;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // create testPlayer1
        testPlayer1 = new Player();
        testPlayer1.setPlayerName("testPlayer1");
        testPlayer1.setToken("testToken1");
        testPlayer1.setWsConnectionId("testWsConnectionId1");
        testPlayer1.setLobbyId(1L);

        // create testPlayer2
        testPlayer2 = new Player();
        testPlayer2.setPlayerName("testPlayer2");
        testPlayer2.setToken("testToken1");
        testPlayer2.setWsConnectionId("testWsConnectionId2");
        testPlayer2.setLobbyId(1L);

        List<Player> allPlayers = new ArrayList<Player>(Arrays.asList(testPlayer1, testPlayer2));

        // create basicLobby
        basicLobby = new BasicLobby();
        basicLobby.setLobbyName("testBasicLobby");
        basicLobby.setLobbyId(1L);
        basicLobby.setIsPublic(true);
        basicLobby.setNumRounds(2);
        basicLobby.setNumSeconds(3);
        ((BasicLobby) basicLobby).setNumOptions(2);
        basicLobby.setLobbyCreatorPlayerToken(testPlayer1.getToken());
        basicLobby.addPlayerToLobby(testPlayer1.getPlayerName());
        basicLobby.addPlayerToLobby(testPlayer2.getPlayerName());
        basicLobby.setJoinedPlayerNames(new ArrayList<String>(
                Arrays.asList(testPlayer1.getPlayerName(), testPlayer2.getPlayerName())));
        basicLobby.setContinent(new ArrayList<String>(
                Arrays.asList("Africa", "Americas", "Asia", "Europe", "Oceania")));

        // create advancedLobby
        advancedLobby = new AdvancedLobby();
        advancedLobby.setLobbyName("testAdvancedLobby");
        advancedLobby.setLobbyId(2L);
        advancedLobby.setIsPublic(true);
        advancedLobby.setNumRounds(2);
        advancedLobby.setNumSeconds(5);
        ((AdvancedLobby) advancedLobby).setNumSecondsUntilHint(1);
        ((AdvancedLobby) advancedLobby).setHintInterval(1);
        ((AdvancedLobby) advancedLobby).setMaxNumGuesses(10);
        advancedLobby.setLobbyCreatorPlayerToken(testPlayer1.getToken());
        advancedLobby.addPlayerToLobby(testPlayer1.getPlayerName());
        advancedLobby.addPlayerToLobby(testPlayer2.getPlayerName());
        advancedLobby.setJoinedPlayerNames(new ArrayList<String>(
                Arrays.asList(testPlayer1.getPlayerName(), testPlayer2.getPlayerName())));
        advancedLobby.setContinent(new ArrayList<String>(
                Arrays.asList("Africa", "Americas", "Asia", "Europe", "Oceania")));

        // create two countries for testing
        // country 1
        Country switzerland = new Country();
        switzerland.setCountryCode("CH");
        switzerland.setName("Switzerland");
        switzerland.setPopulation("8655" + "K");
        switzerland.setCapital("Bern");
        switzerland.setCurrency("Swiss Franc");
        switzerland.setFlag("https://flagcdn.com/h240/ch.png");
        switzerland.setContinent("Europe");
        switzerland.setGdp("-9999");
        switzerland.setSurfaceArea("-9999");
        switzerland.setLifeExpectancyMale("-9999");
        switzerland.setLifeExpectancyFemale("-9999");
        switzerland.setUnemploymentRate("-9999");
        switzerland.setImports("-9999");
        switzerland.setExports("-9999");
        switzerland.setHomicideRate("-9999");
        switzerland.setPopulationGrowth("-9999");
        switzerland.setSecondarySchoolEnrollmentFemale("-9999");
        switzerland.setSecondarySchoolEnrollmentMale("-9999");
        switzerland.setCo2Emissions("-9999");
        switzerland.setForestedArea("-9999");
        switzerland.setInfantMortality("not available");
        switzerland.setPopulationDensity("not available");
        switzerland.setInternetUsers("not available");

        // country 2
        Country india = new Country();
        india.setCountryCode("IN");
        india.setName("India");
        india.setPopulation("1380004" + "K");
        india.setCapital("New Delhi");
        india.setCurrency("Indian Rupee");
        india.setFlag("https://flagcdn.com/h240/in.png");
        india.setContinent("Asia");
        india.setGdp("-9999");
        india.setSurfaceArea("-9999");
        india.setLifeExpectancyMale("-9999");
        india.setLifeExpectancyFemale("-9999");
        india.setUnemploymentRate("-9999");
        india.setImports("-9999");
        india.setExports("-9999");
        india.setHomicideRate("-9999");
        india.setPopulationGrowth("-9999");
        india.setSecondarySchoolEnrollmentFemale("-9999");
        india.setSecondarySchoolEnrollmentMale("-9999");
        india.setCo2Emissions("-9999");
        india.setForestedArea("-9999");
        india.setInfantMortality("not available");
        india.setPopulationDensity("not available");
        india.setInternetUsers("not available");

        allCountries = new ArrayList();
        allCountries.add(switzerland);
        allCountries.add(india);

        // mock countryService and countryRepository
        when(countryService.getAllCountriesInContinents(any())).thenReturn(allCountries);
        when(countryRepository.findCountryCodesByContinentIn(any())).thenReturn(allCountries);
        when(countryRepository.findByCountryCode(any())).thenReturn(switzerland);
        when(countryRepository.getAllCountryNamesInContinents(any())).thenReturn(allCountries);
        when(playerRepository.findByLobbyId(anyLong())).thenReturn(allPlayers);
    }

    @Test
    void testStartBasicGame() throws InterruptedException {

        // mock playerService and lobbyRepository
        when(playerService.getPlayerByToken(Mockito.anyString())).thenReturn(testPlayer1, testPlayer1);
        when(lobbyRepository.findByLobbyId(anyLong())).thenReturn(basicLobby);

        // Call the method to be tested
        gameService.startGame(basicLobby);

        // wait for 4 seconds until first round is finished. This allows to correctly verify the number
        // of times the timer sent a message to the lobby
        Thread.sleep(4000);

        // check if game is started by checking if the following methods are called
        // methods calls within gameService class
        verify(webSocketService, times(1)).sendToLobby(eq(1L), eq("/game-start"), Mockito.any());
        verify(lobbyRepository, times(1)).save(any(Lobby.class));
        verify(webSocketService, times(1)).sendToLobby(eq(1L), eq("/lobby-settings"), any(LobbySettingsDTO.class));
        // methods calls within game.startGame or Hinthandler
        verify(webSocketService, times(1)).sendToLobby(eq(1L), eq("/round-start"), any());
        verify(webSocketService, times(1)).sendToLobby(eq(1L), eq("/flag-in-round"), any(FlagDTO.class));
        verify(webSocketService, times(1)).sendToLobby(eq(1L), eq("/choices-in-round"), any(ChoicesDTO.class));
        verify(webSocketService, times(0)).sendToLobby(eq(1L), eq("/hints-in-round"), any(HintDTO.class));
        verify(webSocketService, times(4)).sendToLobby(eq(1L), eq("/timer"), any(TimerDTO.class));
    }

    @Test
    void testStartBasicGameAndWaitUntilFirstRoundIsFinished() throws InterruptedException {
        // mock playerService and lobbyRepository
        when(playerService.getPlayerByToken(Mockito.anyString())).thenReturn(testPlayer1, testPlayer1);
        when(lobbyRepository.findByLobbyId(anyLong())).thenReturn(basicLobby);

        // Call the method to be tested
        gameService.startGame(basicLobby);

        // wait for 4 seconds until first round is finished. This allows to correctly verify the number
        // of times the timer sent a message to the lobby
        Thread.sleep(5000);

        // check if game is started by checking if the following methods are called
        // methods calls within gameService class
        verify(webSocketService, times(1)).sendToLobby(eq(1L), eq("/game-start"), Mockito.any());
        verify(lobbyRepository, times(1)).save(any(Lobby.class));
        verify(webSocketService, times(1)).sendToLobby(eq(1L), eq("/lobby-settings"), any(LobbySettingsDTO.class));
        // methods calls within game.startGame, game.startRound or Hinthandler
        verify(webSocketService, times(1)).sendToLobby(eq(1L), eq("/round-start"), any());
        verify(webSocketService, times(1)).sendToLobby(eq(1L), eq("/flag-in-round"), any(FlagDTO.class));
        verify(webSocketService, times(1)).sendToLobby(eq(1L), eq("/choices-in-round"), any(ChoicesDTO.class));
        verify(webSocketService, times(0)).sendToLobby(eq(1L), eq("/hints-in-round"), any(HintDTO.class));
        verify(webSocketService, times(4)).sendToLobby(eq(1L), eq("/timer"), any(TimerDTO.class));

        // wait for 6 seconds until everything in endround was executed
        Thread.sleep(6100);

        // methods calls within game.endRound
        verify(webSocketService, times(1)).sendToLobby(eq(1L), eq("/correct-country"), any(CorrectGuessDTO.class));
        verify(webSocketService, times(1)).sendToLobby(eq(1L), eq("/round-end"), any());
        // verfiy that the following method is called twice, because the there are two players in the lobby
        verify(webSocketService, times(2)).sendToPlayerInLobby(anyString(), eq("/score-board"), any(), any());
        verify(webSocketService, times(1)).sendToLobby(eq(1L), eq("/round"), any(RoundDTO.class));
    }

    @Test
    void testStartBasicGame_numberOfRoundsLargerThanNumberOfCountriesInDB_throwsIllegalArgumentException() {
        // given (number of rounds is larger than number of countries in DB)
        basicLobby.setNumRounds(300);

        // call method to be tested and check if IllegalArgumentException is thrown
        assertThrows(IllegalArgumentException.class, () -> {
            gameService.startGame(basicLobby);
        });
    }

    @Test
    void testStartAdvancedGame() throws InterruptedException {
        // given
        testPlayer1.setLobbyId(1L);
        testPlayer2.setLobbyId(1L);

        // mock playerService and lobbyRepository
        when(playerService.getPlayerByToken(Mockito.anyString())).thenReturn(testPlayer1, testPlayer1);
        when(lobbyRepository.findByLobbyId(anyLong())).thenReturn(advancedLobby);

        // Call the method to be tested
        gameService.startGame(advancedLobby);

        // wait for 6 seconds until first round is finished. This allows to correctly verify the number
        // of times the timer sent a message to the lobby
        Thread.sleep(6000);

        // check if game is started by checking if the following methods are called
        // methods calls within gameService class
        verify(webSocketService, times(1)).sendToLobby(eq(2L), eq("/game-start"), Mockito.any());
        verify(lobbyRepository, times(1)).save(any(Lobby.class));
        verify(webSocketService, times(1)).sendToLobby(eq(2L), eq("/lobby-settings"), any(LobbySettingsDTO.class));
        // methods calls within game.startGame or Hinthandler
        verify(webSocketService, times(1)).sendToLobby(eq(2L), eq("/round-start"), any());
        verify(webSocketService, times(1)).sendToLobby(eq(2L), eq("/flag-in-round"), any(FlagDTO.class));
        verify(webSocketService, times(3)).sendToLobby(eq(2L), eq("/hints-in-round"), any(HintDTO.class));
        verify(webSocketService, times(0)).sendToLobby(eq(2L), eq("/choices-in-round"), any(ChoicesDTO.class));
        verify(webSocketService, times(6)).sendToLobby(eq(2L), eq("/timer"), any(TimerDTO.class));
    }

    @Test
    void testStartAdvancedGameAndWaitUntilFirstRoundIsFinished() throws InterruptedException {
        // given
        testPlayer1.setLobbyId(1L);
        testPlayer2.setLobbyId(1L);

        // mock playerService and lobbyRepository
        when(playerService.getPlayerByToken(Mockito.anyString())).thenReturn(testPlayer1, testPlayer1);
        when(lobbyRepository.findByLobbyId(anyLong())).thenReturn(advancedLobby);

        // Call the method to be tested
        gameService.startGame(advancedLobby);

        // wait for 6 seconds until first round is finished. This allows to correctly verify the number
        // of times the timer sent a message to the lobby
        Thread.sleep(6000);

        // check if game is started by checking if the following methods are called
        // methods calls within gameService class
        verify(webSocketService, times(1)).sendToLobby(eq(2L), eq("/game-start"), Mockito.any());
        verify(lobbyRepository, times(1)).save(any(Lobby.class));
        verify(webSocketService, times(1)).sendToLobby(eq(2L), eq("/lobby-settings"), any(LobbySettingsDTO.class));
        // methods calls within game.startGame, game.startRound or Hinthandler
        verify(webSocketService, times(1)).sendToLobby(eq(2L), eq("/round-start"), any());
        verify(webSocketService, times(1)).sendToLobby(eq(2L), eq("/flag-in-round"), any(FlagDTO.class));
        verify(webSocketService, times(3)).sendToLobby(eq(2L), eq("/hints-in-round"), any(HintDTO.class));
        verify(webSocketService, times(0)).sendToLobby(eq(2L), eq("/choices-in-round"), any(ChoicesDTO.class));
        verify(webSocketService, times(6)).sendToLobby(eq(2L), eq("/timer"), any(TimerDTO.class));

        // wait for 6 seconds until everything in endround was executed
        Thread.sleep(6100);

        // methods calls within game.endRound
        verify(webSocketService, times(1)).sendToLobby(eq(2L), eq("/correct-country"), any(CorrectGuessDTO.class));
        verify(webSocketService, times(1)).sendToLobby(eq(2L), eq("/round-end"), any());
        // verfiy that the following methods are called twice, because the there are two players in the lobby
//        verify(webSocketService, times(1)).sendToPlayerInLobby(anyString(), eq("/score-board"), any(), any());
        verify(webSocketService, times(1)).sendToLobby(eq(2L), eq("/round"), any(RoundDTO.class));
    }

    @Test
    void testStartAdvancedGame_numberOfRoundsLargerThanNumberOfCountriesInDB_throwsIllegalArgumentException() {
        // given (number of rounds is larger than number of countries in DB)
        advancedLobby.setNumRounds(300);

        // call method to be tested and check if IllegalArgumentException is thrown
        assertThrows(IllegalArgumentException.class, () -> {
            gameService.startGame(advancedLobby);
        });
    }

    @Test
    void testSendLobbySettingsBasicGame() {
        // mock playerService and lobbyRepository
        when(lobbyRepository.findByLobbyId(anyLong())).thenReturn(basicLobby);
        when(playerService.getPlayerByToken(anyString())).thenReturn(testPlayer1);

        // Call the method to be tested
        gameService.sendLobbySettings(basicLobby.getLobbyId().intValue());

        // check if game is started by checking if the following methods are called
        // methods calls within gameService class
        verify(webSocketService, times(1)).sendToLobby(eq(1L), eq("/lobby-settings"), any(LobbySettingsDTO.class));
    }

    @Test
    void testSendLobbySettingsAdvancedGame() {
        // mock playerService and lobbyRepository
        when(lobbyRepository.findByLobbyId(anyLong())).thenReturn(basicLobby);
        when(playerService.getPlayerByToken(anyString())).thenReturn(testPlayer1);

        // Call the method to be tested
        gameService.sendLobbySettings(advancedLobby.getLobbyId().intValue());

        // check if game is started by checking if the following methods are called
        // methods calls within gameService class
        verify(webSocketService, times(1)).sendToLobby(eq(2L), eq("/lobby-settings"), any(LobbySettingsDTO.class));
    }

    @Test
    void testSendLobbySettings_invalidLobbyId_throwsResponseStatusException() {
        // mock playerService and lobbyRepository
        when(playerService.getPlayerByToken(anyString())).thenReturn(testPlayer1);

        // Call the method to be tested
        assertThrows(ResponseStatusException.class, () -> {
            gameService.sendLobbySettings(0);
        });
    }
}