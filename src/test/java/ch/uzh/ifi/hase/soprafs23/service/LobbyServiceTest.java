package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.AdvancedLobbyCreateDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.BasicLobbyCreateDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.incoming.RemoveDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class LobbyServiceTest {

    @Mock
    private LobbyRepository lobbyRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private PlayerService playerService;

    @Mock
    private WebSocketService webSocketService;

    @Mock
    private GameService gameService;

    @InjectMocks
    private LobbyService lobbyService;

    private Player testPlayer1;
    private BasicLobbyCreateDTO basicLobbyCreateDTO;
    private Lobby basicLobby;
    private AdvancedLobbyCreateDTO advancedLobbyCreateDTO;
    private Lobby advancedLobby;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // given
        // create testPlayer1
        testPlayer1 = new Player();
        testPlayer1.setPlayerName("testPlayer1");
        testPlayer1.setToken("testToken1");
        testPlayer1.setWsConnectionId("testWsConnectionId1");

        // create basicLobbyCreateDTO
        basicLobbyCreateDTO = new BasicLobbyCreateDTO();
        basicLobbyCreateDTO.setLobbyName("testBasicLobby");
        basicLobbyCreateDTO.setIsPublic(true);
        basicLobbyCreateDTO.setNumSeconds(10);
        basicLobbyCreateDTO.setNumRounds(4);
        basicLobbyCreateDTO.setNumOptions(4);
        basicLobbyCreateDTO.setContinent(new ArrayList<String>(
                Arrays.asList("Africa", "Americas", "Asia", "Europe", "Oceania")));

        // create basicLobby
        basicLobby = new BasicLobby();
        basicLobby.setLobbyName("testBasicLobby");
        basicLobby.setIsPublic(true);
        basicLobby.setNumSeconds(10);
        basicLobby.setNumRounds(4);
        ((BasicLobby) basicLobby).setNumOptions(4);
        basicLobby.setLobbyCreatorPlayerToken(testPlayer1.getToken());
        basicLobby.addPlayerToLobby(testPlayer1.getPlayerName());
        basicLobby.setContinent(new ArrayList<String>(
                Arrays.asList("Africa", "Americas", "Asia", "Europe", "Oceania")));

        // create advancedLobbyCreateDTO
        advancedLobbyCreateDTO = new AdvancedLobbyCreateDTO();
        advancedLobbyCreateDTO.setLobbyName("testAdvancedLobby");
        advancedLobbyCreateDTO.setIsPublic(true);
        advancedLobbyCreateDTO.setNumSeconds(50);
        advancedLobbyCreateDTO.setNumRounds(4);
        advancedLobbyCreateDTO.setNumSecondsUntilHint(10);
        advancedLobbyCreateDTO.setHintInterval(5);
        advancedLobbyCreateDTO.setMaxNumGuesses(10);
        advancedLobbyCreateDTO.setContinent(new ArrayList<String>(
                Arrays.asList("Africa", "Americas", "Asia", "Europe", "Oceania")));

        // create advancedLobby
        advancedLobby = new AdvancedLobby();
        advancedLobby.setLobbyName("testAdvancedLobby");
        advancedLobby.setIsPublic(true);
        advancedLobby.setNumSeconds(50);
        advancedLobby.setNumRounds(4);
        ((AdvancedLobby) advancedLobby).setNumSecondsUntilHint(10);
        ((AdvancedLobby) advancedLobby).setHintInterval(5);
        ((AdvancedLobby) advancedLobby).setMaxNumGuesses(10);
        advancedLobby.setLobbyCreatorPlayerToken(testPlayer1.getToken());
        advancedLobby.addPlayerToLobby(testPlayer1.getPlayerName());
        advancedLobby.setContinent(new ArrayList<String>(
                Arrays.asList("Africa", "Americas", "Asia", "Europe", "Oceania")));

        // when -> any object is being saved in the userRepository -> return the dummy testUser
        Mockito.doNothing().when(webSocketService).sendToLobby(Mockito.any(), Mockito.any(), Mockito.any());
        when(playerService.getPlayerByToken(anyString())).thenReturn(testPlayer1);
        when(playerRepository.save(Mockito.any())).thenReturn(testPlayer1);
        when(playerRepository.findByToken(Mockito.any())).thenReturn(testPlayer1);
        when(playerRepository.findByLobbyId(Mockito.any())).thenReturn(Collections.singletonList(testPlayer1));
    }

    @Test
    void testCreatePublicBasicLobby() {
        // return basicLobby when lobbyRepository.save() or lobbyRepository.findByLobbyId() is called
        when(lobbyRepository.findByLobbyId(Mockito.any())).thenReturn(basicLobby);
        when(lobbyRepository.save(Mockito.any())).thenReturn(basicLobby);

        // create basic lobby
        Lobby basicLobbyInput = DTOMapper.INSTANCE.convertBasicLobbyCreateDTOtoEntity(basicLobbyCreateDTO);
        BasicLobby testBasicLobbyCreated = lobbyService.createBasicLobby(basicLobbyInput, testPlayer1.getToken(), basicLobbyInput.getIsPublic());

        // verify that lobby was saved into repository
        Mockito.verify(lobbyRepository, Mockito.times(1)).save(Mockito.any());
        // verify that player was updated
        Mockito.verify(playerRepository, Mockito.times(1)).save(Mockito.any());

        // check that lobby was created correctly
        assertNotNull(testBasicLobbyCreated);
        assertEquals(testBasicLobbyCreated.getLobbyName(), basicLobbyCreateDTO.getLobbyName());
        assertEquals(testBasicLobbyCreated.getIsPublic(), basicLobbyCreateDTO.getIsPublic());
        assertEquals(testBasicLobbyCreated.getNumSeconds(), basicLobbyCreateDTO.getNumSeconds());
        assertEquals(testBasicLobbyCreated.getNumRounds(), basicLobbyCreateDTO.getNumRounds());
        assertEquals(testBasicLobbyCreated.getNumOptions(), basicLobbyCreateDTO.getNumOptions());
        assertEquals(testBasicLobbyCreated.getLobbyCreatorPlayerToken(), testPlayer1.getToken());
        assertEquals(testBasicLobbyCreated.getJoinedPlayerNames().get(0), testPlayer1.getPlayerName());
        assertTrue(testBasicLobbyCreated.isJoinable());
        assertEquals(5, testBasicLobbyCreated.getContinent().size());
    }

    @Test
    void testCreatePrivateBasicLobby() {
        basicLobbyCreateDTO.setIsPublic(false);
        basicLobby.setLobbyId(0L);
        basicLobby.setIsPublic(false);
        basicLobby.setPrivateLobbyKey("testPrivateKey");

        // return basicLobby when lobbyRepository.save() or lobbyRepository.findByLobbyId() is called
        when(lobbyRepository.findByLobbyId(Mockito.any())).thenReturn(basicLobby);
        when(lobbyRepository.save(Mockito.any())).thenReturn(basicLobby);

        // create basic lobby
        Lobby basicLobbyInput = DTOMapper.INSTANCE.convertBasicLobbyCreateDTOtoEntity(basicLobbyCreateDTO);
        BasicLobby testBasicLobbyCreated = lobbyService.createBasicLobby(basicLobbyInput, testPlayer1.getToken(), basicLobbyInput.getIsPublic());

        // verify that lobby was saved into repository
        Mockito.verify(lobbyRepository, Mockito.times(2)).save(Mockito.any());
        // verify that player was updated
        Mockito.verify(playerRepository, Mockito.times(1)).save(Mockito.any());

        // check that lobby was created correctly
        assertNotNull(testBasicLobbyCreated);
        assertEquals(testBasicLobbyCreated.getLobbyName(), basicLobbyCreateDTO.getLobbyName());
        assertEquals(testBasicLobbyCreated.getIsPublic(), basicLobbyCreateDTO.getIsPublic());
        assertFalse(testBasicLobbyCreated.getIsPublic());
        assertEquals(testBasicLobbyCreated.getNumSeconds(), basicLobbyCreateDTO.getNumSeconds());
        assertEquals(testBasicLobbyCreated.getNumRounds(), basicLobbyCreateDTO.getNumRounds());
        assertEquals(testBasicLobbyCreated.getNumOptions(), basicLobbyCreateDTO.getNumOptions());
        assertEquals(testBasicLobbyCreated.getLobbyCreatorPlayerToken(), testPlayer1.getToken());
        assertEquals(testBasicLobbyCreated.getJoinedPlayerNames().get(0), testPlayer1.getPlayerName());
        assertTrue(testBasicLobbyCreated.isJoinable());
        assertNotNull(testBasicLobbyCreated.getPrivateLobbyKey());
        assertEquals(5, testBasicLobbyCreated.getContinent().size());
    }

    @Test
    void testCreatePublicAdvancedLobby() {
        // return advancedLobby when lobbyRepository.save() or lobbyRepository.findByLobbyId() is called
        when(lobbyRepository.findByLobbyId(Mockito.any())).thenReturn(advancedLobby);
        when(lobbyRepository.save(Mockito.any())).thenReturn(advancedLobby);

        // create advanced lobby
        Lobby advancedLobbyInput = DTOMapper.INSTANCE.convertAdvancedLobbyCreateDTOtoEntity(advancedLobbyCreateDTO);
        AdvancedLobby testAdvancedLobbyCreated = lobbyService.createAdvancedLobby(advancedLobbyInput, testPlayer1.getToken(), advancedLobbyInput.getIsPublic());

        // verify that lobby was saved into repository
        Mockito.verify(lobbyRepository, Mockito.times(1)).save(Mockito.any());
        // verify that player was updated
        Mockito.verify(playerRepository, Mockito.times(1)).save(Mockito.any());

        // check that lobby was created correctly
        assertNotNull(testAdvancedLobbyCreated);
        assertEquals(testAdvancedLobbyCreated.getLobbyName(), advancedLobbyCreateDTO.getLobbyName());
        assertEquals(testAdvancedLobbyCreated.getIsPublic(), advancedLobbyCreateDTO.getIsPublic());
        assertEquals(testAdvancedLobbyCreated.getNumSeconds(), advancedLobbyCreateDTO.getNumSeconds());
        assertEquals(testAdvancedLobbyCreated.getNumRounds(), advancedLobbyCreateDTO.getNumRounds());
        assertEquals(testAdvancedLobbyCreated.getNumSecondsUntilHint(), advancedLobbyCreateDTO.getNumSecondsUntilHint());
        assertEquals(testAdvancedLobbyCreated.getHintInterval(), advancedLobbyCreateDTO.getHintInterval());
        assertEquals(testAdvancedLobbyCreated.getMaxNumGuesses(), advancedLobbyCreateDTO.getMaxNumGuesses());
        assertEquals(testAdvancedLobbyCreated.getLobbyCreatorPlayerToken(), testPlayer1.getToken());
        assertEquals(testAdvancedLobbyCreated.getJoinedPlayerNames().get(0), testPlayer1.getPlayerName());
        assertTrue(testAdvancedLobbyCreated.isJoinable());
        assertEquals(5, testAdvancedLobbyCreated.getContinent().size());
    }

    @Test
    void testCreatePrivateAdvancedLobby() {
        advancedLobbyCreateDTO.setIsPublic(false);
        advancedLobby.setLobbyId(0L);
        advancedLobby.setIsPublic(false);
        advancedLobby.setPrivateLobbyKey("testPrivateKey");

        // return advancedLobby when lobbyRepository.save() or lobbyRepository.findByLobbyId() is called
        when(lobbyRepository.findByLobbyId(Mockito.any())).thenReturn(advancedLobby);
        when(lobbyRepository.save(Mockito.any())).thenReturn(advancedLobby);

        // create advanced lobby
        Lobby advancedLobbyInput = DTOMapper.INSTANCE.convertAdvancedLobbyCreateDTOtoEntity(advancedLobbyCreateDTO);
        AdvancedLobby testAdvancedLobbyCreated = lobbyService.createAdvancedLobby(advancedLobbyInput, testPlayer1.getToken(), advancedLobbyInput.getIsPublic());

        // verify that lobby was saved into repository
        Mockito.verify(lobbyRepository, Mockito.times(2)).save(Mockito.any());
        // verify that player was updated
        Mockito.verify(playerRepository, Mockito.times(1)).save(Mockito.any());

        // check that lobby was created correctly
        assertNotNull(testAdvancedLobbyCreated);
        assertEquals(testAdvancedLobbyCreated.getLobbyName(), advancedLobbyCreateDTO.getLobbyName());
        assertEquals(testAdvancedLobbyCreated.getIsPublic(), advancedLobbyCreateDTO.getIsPublic());
        assertFalse(testAdvancedLobbyCreated.getIsPublic());
        assertEquals(testAdvancedLobbyCreated.getNumSeconds(), advancedLobbyCreateDTO.getNumSeconds());
        assertEquals(testAdvancedLobbyCreated.getNumRounds(), advancedLobbyCreateDTO.getNumRounds());
        assertEquals(testAdvancedLobbyCreated.getNumSecondsUntilHint(), advancedLobbyCreateDTO.getNumSecondsUntilHint());
        assertEquals(testAdvancedLobbyCreated.getHintInterval(), advancedLobbyCreateDTO.getHintInterval());
        assertEquals(testAdvancedLobbyCreated.getMaxNumGuesses(), advancedLobbyCreateDTO.getMaxNumGuesses());
        assertEquals(testAdvancedLobbyCreated.getLobbyCreatorPlayerToken(), testPlayer1.getToken());
        assertEquals(testAdvancedLobbyCreated.getJoinedPlayerNames().get(0), testPlayer1.getPlayerName());
        assertTrue(testAdvancedLobbyCreated.isJoinable());
        assertNotNull(testAdvancedLobbyCreated.getPrivateLobbyKey());
        assertEquals(5, testAdvancedLobbyCreated.getContinent().size());
    }

    @Test
    void createLobby_validInputs_success() {
        // return basicLobby  when lobbyRepository.save() or lobbyRepository.findByLobbyId() is called
        // for the first time. Then return advancedLobby when lobbyRepository.save() or lobbyRepository.findByLobbyId()
        // is called
        when(lobbyRepository.findByLobbyId(Mockito.any())).thenReturn(basicLobby, advancedLobby);
        when(lobbyRepository.save(Mockito.any())).thenReturn(basicLobby, advancedLobby);

        // create basic lobby
        Lobby basicLobbyInput = DTOMapper.INSTANCE.convertBasicLobbyCreateDTOtoEntity(basicLobbyCreateDTO);
        BasicLobby testBasicLobbyCreated = lobbyService.createBasicLobby(basicLobbyInput, testPlayer1.getToken(), true);

        // create advanced lobby
        Lobby advancedLobbyInput = DTOMapper.INSTANCE.convertAdvancedLobbyCreateDTOtoEntity(advancedLobbyCreateDTO);
        AdvancedLobby testAdvancedLobbyCreated = lobbyService.createAdvancedLobby(advancedLobbyInput, testPlayer1.getToken(), true);

        // Mock the lobbyRepository
        List<Lobby> testLobbies = new ArrayList<>();
        testLobbies.add(testBasicLobbyCreated);
        testLobbies.add(testAdvancedLobbyCreated);
        when(lobbyRepository.findAllByIsPublicAndIsJoinable(true, true)).thenReturn(testLobbies);

        // call method to test
        List<Lobby> allFoundPublicAndJoinableLobbies = lobbyService.getAllPublicAndJoinableLobbies();

        // then
        assertEquals(allFoundPublicAndJoinableLobbies.size(), 2);
    }

    @Test
    void testStartGameValidInput() {
        basicLobby.setLobbyId(0L);
        basicLobby.addPlayerToLobby("testPlayer2");

        Mockito.when(lobbyRepository.findByLobbyId(Mockito.any())).thenReturn(basicLobby);

        lobbyService.startGame(basicLobby.getLobbyId(), basicLobby.getLobbyCreatorPlayerToken());

        assertFalse(basicLobby.isJoinable());
        Mockito.verify(gameService, Mockito.times(1)).startGame(Mockito.any());
    }

    @Test
    void testStartGameNotEnoughPlayersInLobby() {
        basicLobby.setLobbyId(0L);

        Mockito.when(lobbyRepository.findByLobbyId(Mockito.any())).thenReturn(basicLobby);

        assertThrows(ResponseStatusException.class, () -> lobbyService.startGame(basicLobby.getLobbyId(), basicLobby.getLobbyCreatorPlayerToken()));
        assertTrue(basicLobby.isJoinable());

    }

    @Test
    void testStartGameNotInitiatedByLobbyOwner() {
        basicLobby.setLobbyId(0L);
        basicLobby.addPlayerToLobby("testPlayer2");

        Mockito.when(lobbyRepository.findByLobbyId(Mockito.any())).thenReturn(basicLobby);

        assertThrows(ResponseStatusException.class, () -> lobbyService.startGame(basicLobby.getLobbyId(), "SomeWrongPlayerToken"));
        assertTrue(basicLobby.isJoinable());

    }

    @Test
    void testStartGameIsCollectingPlayAgains() {
        basicLobby.setLobbyId(0L);
        basicLobby.addPlayerToLobby("testPlayer2");
        basicLobby.setCollectingPlayAgains(true);

        Mockito.when(lobbyRepository.findByLobbyId(Mockito.any())).thenReturn(basicLobby);

        assertThrows(ResponseStatusException.class, () -> lobbyService.startGame(basicLobby.getLobbyId(), basicLobby.getLobbyCreatorPlayerToken()));
        assertTrue(basicLobby.isJoinable());

    }

    @Test
    void testKickPlayerFromLobby_KickNotRequestedByLobbyOwner() {
        // create player to be kicked
        Player testPlayer2 = new Player();
        testPlayer2.setPlayerName("testPlayer2");
        testPlayer2.setToken("testToken2");
        testPlayer2.setWsConnectionId("testWsConnectionId2");

        RemoveDTO removeDTO = new RemoveDTO();
        removeDTO.setPlayerName(testPlayer2.getPlayerName());

        // create 3rd player which tries to request the kick (but is not admin)
        Player testPlayer3 = new Player();
        testPlayer3.setPlayerName("testPlayer3");
        testPlayer3.setToken("testToken3");
        testPlayer3.setWsConnectionId("testWsConnectionId3");

        basicLobby.setLobbyId(0L);
        basicLobby.addPlayerToLobby(testPlayer2.getPlayerName());

        Mockito.when(lobbyRepository.findByLobbyId(Mockito.any())).thenReturn(basicLobby);
        Mockito.when(playerService.getPlayerByWsConnectionId(Mockito.any())).thenReturn(testPlayer3);
        Mockito.when(playerRepository.findByPlayerName(Mockito.any())).thenReturn(testPlayer2);


        assertThrows(ResponseStatusException.class, () -> lobbyService.kickPlayerFromLobby(basicLobby.getLobbyId().intValue(), removeDTO, testPlayer3.getWsConnectionId()));

    }

    @Test
    void testKickPlayerFromLobby_Success() {
        // create player to be kicked
        Player testPlayer2 = new Player();
        testPlayer2.setPlayerName("testPlayer2");
        testPlayer2.setToken("testToken2");
        testPlayer2.setWsConnectionId("testWsConnectionId2");
        testPlayer2.setLobbyId(0L);

        RemoveDTO removeDTO = new RemoveDTO();
        removeDTO.setPlayerName(testPlayer2.getPlayerName());

        lobbyService = Mockito.spy(new LobbyService(lobbyRepository, playerRepository, playerService, gameService));


        basicLobby.setLobbyId(0L);
        basicLobby.addPlayerToLobby(testPlayer2.getPlayerName());

        Mockito.when(lobbyRepository.findByLobbyId(Mockito.any())).thenReturn(basicLobby);
        Mockito.when(playerService.getPlayerByWsConnectionId(Mockito.any())).thenReturn(testPlayer1);
        Mockito.when(playerRepository.findByPlayerName(Mockito.any())).thenReturn(testPlayer2);
        Mockito.when(playerService.getPlayerByToken(testPlayer2.getToken())).thenReturn(testPlayer2);
        Mockito.doNothing().when(gameService).sendLobbySettings(Mockito.any());
        Mockito.when(lobbyRepository.save(Mockito.any())).thenReturn(basicLobby);
        Mockito.doNothing().when(playerService).clearLobbyConfigFromPlayer(Mockito.any());
        Mockito.doReturn(basicLobby).when(lobbyService).leaveLobby(basicLobby, testPlayer2.getToken());

        // call method to test
        String actualReturnedValue = lobbyService.kickPlayerFromLobby(basicLobby.getLobbyId().intValue(), removeDTO, testPlayer1.getWsConnectionId());

        // then
        assertEquals(actualReturnedValue, testPlayer2.getWsConnectionId());
    }

    @Test
    void resendLobbySettings_success(){
        doNothing().when(gameService).sendLobbySettings(anyInt());

        lobbyService.resendLobbySettings(1);

        verify(gameService, times(1)).sendLobbySettings(eq(1));
    }

    @Test
    void clearPlayerAfterGameEnd_registeredPlayer() {
        when(playerService.getPlayerByToken(anyString())).thenReturn(testPlayer1);

        lobbyService.clearPlayerAfterGameEnd(testPlayer1.getToken());

        verify(playerService, times(1)).clearLobbyConfigFromPlayer(eq(testPlayer1.getToken()));
    }

    @Test
    void clearPlayerAfterGameEnd_unregisteredPlayer() {
        when(playerService.getPlayerByToken(anyString())).thenReturn(null);

        lobbyService.clearPlayerAfterGameEnd("someToken");

        verify(playerService, times(0)).clearLobbyConfigFromPlayer(any());
    }

    @Test
    void playAgain_firstPlayer(){
        // given
        testPlayer1.setLobbyId(1L);
        advancedLobby.setLobbyId(1L);
        advancedLobby.setJoinable(true);
        advancedLobby.removePlayerFromLobby(testPlayer1.getPlayerName());

        AdvancedLobby advancedLobbyPlayAgain = new AdvancedLobby();
        advancedLobbyPlayAgain.setLobbyId(1L);
        advancedLobbyPlayAgain.setJoinable(true);
        advancedLobbyPlayAgain.addPlayerToLobby(testPlayer1.getPlayerName());
        advancedLobbyPlayAgain.setLobbyCreatorPlayerToken(testPlayer1.getToken());
        advancedLobbyPlayAgain.setIsPublic(true);
        advancedLobbyPlayAgain.setNumSeconds(50);
        advancedLobbyPlayAgain.setNumRounds(4);
        ((AdvancedLobby) advancedLobbyPlayAgain).setNumSecondsUntilHint(10);
        ((AdvancedLobby) advancedLobbyPlayAgain).setHintInterval(5);
        ((AdvancedLobby) advancedLobbyPlayAgain).setMaxNumGuesses(10);
        advancedLobbyPlayAgain.setLobbyCreatorPlayerToken(testPlayer1.getToken());
        advancedLobbyPlayAgain.addPlayerToLobby(testPlayer1.getPlayerName());
        advancedLobbyPlayAgain.setContinent(new ArrayList<String>(
                Arrays.asList("Africa", "Americas", "Asia", "Europe", "Oceania")));

        // mock playerService and lobbyRepository
        when(playerService.getPlayerByWsConnectionId(anyString())).thenReturn(testPlayer1);
        when(lobbyRepository.findByLobbyId(anyLong())).thenReturn(advancedLobby);
        when(lobbyRepository.save(any())).thenReturn(advancedLobbyPlayAgain);

        // call method to test
        lobbyService.playAgain(advancedLobby.getLobbyId().intValue(), testPlayer1.getWsConnectionId());

        // verify that lobby was updated
        verify(lobbyRepository, times(1)).save(any(AdvancedLobby.class));
        // test if both lobbies have the same attributes
        assertEquals(advancedLobbyPlayAgain.getLobbyId(), advancedLobby.getLobbyId());
        assertEquals(advancedLobbyPlayAgain.getIsPublic(), advancedLobby.getIsPublic());
        assertEquals(advancedLobbyPlayAgain.getNumSeconds(), advancedLobby.getNumSeconds());
        assertEquals(advancedLobbyPlayAgain.getNumRounds(), advancedLobby.getNumRounds());
        assertEquals(((AdvancedLobby) advancedLobbyPlayAgain).getNumSecondsUntilHint(), ((AdvancedLobby) advancedLobby).getNumSecondsUntilHint());
        assertEquals(((AdvancedLobby) advancedLobbyPlayAgain).getHintInterval(), ((AdvancedLobby) advancedLobby).getHintInterval());
        assertEquals(((AdvancedLobby) advancedLobbyPlayAgain).getMaxNumGuesses(), ((AdvancedLobby) advancedLobby).getMaxNumGuesses());
        assertEquals(advancedLobbyPlayAgain.getContinent(), advancedLobby.getContinent());
        assertEquals(advancedLobbyPlayAgain.getLobbyCreatorPlayerToken(), testPlayer1.getToken());
        assertEquals(advancedLobbyPlayAgain.getJoinedPlayerNames().get(0), testPlayer1.getPlayerName());
    }
}
