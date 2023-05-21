package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.AdvancedLobby;
import ch.uzh.ifi.hase.soprafs23.entity.BasicLobby;
import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@SpringBootTest
public class LobbyServiceIntegrationTest {

    @Autowired
    private LobbyRepository lobbyRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private LobbyService lobbyService;

    @Autowired
    private GameService gameService;

    @Autowired
    PlayerService playerService;

    @Mock
    private WebSocketService webSocketService;

    private Player testPlayer1;
    private Player testPlayer2;
    private Player testPlayer3;
    private BasicLobbyCreateDTO basicLobbyCreateDTO;
    private AdvancedLobbyCreateDTO advancedLobbyCreateDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Mockito.doNothing().when(webSocketService).sendToLobby(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.doNothing().when(webSocketService).sendToPlayerInLobby(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

        testPlayer1 = new Player();
        testPlayer1.setPlayerName("testPlayer1");
        testPlayer1.setToken("testToken1");
        testPlayer1.setWsConnectionId("testWsConnectionId1");
        playerRepository.save(testPlayer1);
        playerRepository.flush();

        testPlayer2 = new Player();
        testPlayer2.setPlayerName("testPlayer2");
        testPlayer2.setToken("testToken2");
        testPlayer1.setWsConnectionId("testWsConnectionId1");
        playerRepository.save(testPlayer2);
        playerRepository.flush();

        testPlayer3 = new Player();
        testPlayer3.setPlayerName("testPlayer3");
        testPlayer3.setToken("testToken3");
        testPlayer1.setWsConnectionId("testWsConnectionId1");
        playerRepository.save(testPlayer3);
        playerRepository.flush();

        basicLobbyCreateDTO = new BasicLobbyCreateDTO();
        basicLobbyCreateDTO.setLobbyName("testBasicLobby");
        basicLobbyCreateDTO.setIsPublic(true);
        basicLobbyCreateDTO.setNumSeconds(10);
        basicLobbyCreateDTO.setNumOptions(4);
        basicLobbyCreateDTO.setContinent(new ArrayList<String>(
                Arrays.asList("Africa", "Americas", "Asia", "Europe", "Oceania")));

        advancedLobbyCreateDTO = new AdvancedLobbyCreateDTO();
        advancedLobbyCreateDTO.setLobbyName("testAdvancedLobby");
        advancedLobbyCreateDTO.setIsPublic(true);
        advancedLobbyCreateDTO.setNumSeconds(100);
        advancedLobbyCreateDTO.setNumSecondsUntilHint(5);
        advancedLobbyCreateDTO.setHintInterval(5);
        advancedLobbyCreateDTO.setMaxNumGuesses(10);
        advancedLobbyCreateDTO.setContinent(new ArrayList<String>(
                Arrays.asList("Africa", "Americas", "Asia", "Europe", "Oceania")));
    }

    @AfterEach
    void tearDown() {
        lobbyRepository.deleteAll();
        playerRepository.deleteAll();
    }

    @Test
    @Transactional
    void testCreatePublicBasicLobby() {
        // create lobby
        Lobby basicLobbyInput = DTOMapper.INSTANCE.convertBasicLobbyCreateDTOtoEntity(basicLobbyCreateDTO);
        BasicLobby testBasicLobbyCreated = lobbyService.createBasicLobby(basicLobbyInput, testPlayer1.getToken(), true);
        Long lobbyId = testBasicLobbyCreated.getLobbyId();

        // find lobby with lobbyId
        Lobby foundLobby = lobbyRepository.findByLobbyId(lobbyId);
        BasicLobby foundBasicLobby = (BasicLobby) foundLobby;

        // check if lobbies are the same
        assertEquals(foundBasicLobby.getLobbyId(), testBasicLobbyCreated.getLobbyId());
        assertEquals(foundBasicLobby.getLobbyName(), testBasicLobbyCreated.getLobbyName());
        assertEquals(foundBasicLobby.getNumSeconds(), testBasicLobbyCreated.getNumSeconds());
        assertEquals(foundBasicLobby.getNumRounds(), testBasicLobbyCreated.getNumRounds());
        assertEquals(foundBasicLobby.getIsPublic(), testBasicLobbyCreated.getIsPublic());
        assertEquals(foundBasicLobby.getNumOptions(), testBasicLobbyCreated.getNumOptions());
        assertEquals(foundBasicLobby.getLobbyCreatorPlayerToken(), testBasicLobbyCreated.getLobbyCreatorPlayerToken());
        assertEquals(foundBasicLobby.getJoinedPlayerNames().size(), testBasicLobbyCreated.getJoinedPlayerNames().size());
        assertEquals(foundBasicLobby.getContinent().size(), testBasicLobbyCreated.getContinent().size());
        assertEquals(5, foundBasicLobby.getContinent().size());

        // check whether player is creator of lobby
        Player foundTestPlayer = playerRepository.findByToken(foundBasicLobby.getLobbyCreatorPlayerToken());
        assertEquals(foundTestPlayer.getPlayerName(), testPlayer1.getPlayerName());
        assertTrue(testBasicLobbyCreated.getJoinedPlayerNames().contains(testPlayer1.getPlayerName()));
        assertTrue(foundBasicLobby.getJoinedPlayerNames().contains(testPlayer1.getPlayerName()));
    }

    @Test
    @Transactional
    void testCreatePrivateBasicLobby() {
        basicLobbyCreateDTO.setIsPublic(false);
        // create lobby
        Lobby basicLobbyInput = DTOMapper.INSTANCE.convertBasicLobbyCreateDTOtoEntity(basicLobbyCreateDTO);
        BasicLobby testBasicLobbyCreated = lobbyService.createBasicLobby(basicLobbyInput, testPlayer1.getToken(), false);
        Long lobbyId = testBasicLobbyCreated.getLobbyId();

        // find lobby with lobbyId
        Lobby foundLobby = lobbyRepository.findByLobbyId(lobbyId);
        BasicLobby foundBasicLobby = (BasicLobby) foundLobby;

        // check if lobbies are the same
        assertEquals(foundBasicLobby.getLobbyId(), testBasicLobbyCreated.getLobbyId());
        assertEquals(foundBasicLobby.getLobbyName(), testBasicLobbyCreated.getLobbyName());
        assertEquals(foundBasicLobby.getNumSeconds(), testBasicLobbyCreated.getNumSeconds());
        assertEquals(foundBasicLobby.getNumRounds(), testBasicLobbyCreated.getNumRounds());
        assertEquals(foundBasicLobby.getIsPublic(), testBasicLobbyCreated.getIsPublic());
        assertEquals(foundBasicLobby.getNumOptions(), testBasicLobbyCreated.getNumOptions());
        assertEquals(foundBasicLobby.getLobbyCreatorPlayerToken(), testBasicLobbyCreated.getLobbyCreatorPlayerToken());
        assertEquals(foundBasicLobby.getJoinedPlayerNames().size(), testBasicLobbyCreated.getJoinedPlayerNames().size());
        assertEquals(foundBasicLobby.getContinent().size(), testBasicLobbyCreated.getContinent().size());
        assertEquals(5, foundBasicLobby.getContinent().size());

        // check if lobby is private
        assertFalse(foundBasicLobby.getIsPublic());
        assertNotNull(foundBasicLobby.getPrivateLobbyKey());

        // check whether player is creator of lobby
        Player foundTestPlayer = playerRepository.findByToken(foundBasicLobby.getLobbyCreatorPlayerToken());
        assertEquals(foundTestPlayer.getPlayerName(), testPlayer1.getPlayerName());
        assertTrue(testBasicLobbyCreated.getJoinedPlayerNames().contains(testPlayer1.getPlayerName()));
        assertTrue(foundBasicLobby.getJoinedPlayerNames().contains(testPlayer1.getPlayerName()));
    }

    @Test
    @Transactional
    void testCreatePublicBasicLobby_onlyInvalidContinents_overwrite() {
        basicLobbyCreateDTO.setContinent(new ArrayList<String>(
                Arrays.asList("World")));
        // create lobby
        Lobby basicLobbyInput = DTOMapper.INSTANCE.convertBasicLobbyCreateDTOtoEntity(basicLobbyCreateDTO);
        BasicLobby testBasicLobbyCreated = lobbyService.createBasicLobby(basicLobbyInput, testPlayer1.getToken(), true);
        Long lobbyId = testBasicLobbyCreated.getLobbyId();

        // find lobby with lobbyId
        Lobby foundLobby = lobbyRepository.findByLobbyId(lobbyId);
        BasicLobby foundBasicLobby = (BasicLobby) foundLobby;

        // check if lobbies are the same
        assertEquals(foundBasicLobby.getLobbyId(), testBasicLobbyCreated.getLobbyId());
        assertEquals(foundBasicLobby.getLobbyName(), testBasicLobbyCreated.getLobbyName());
        assertEquals(foundBasicLobby.getNumSeconds(), testBasicLobbyCreated.getNumSeconds());
        assertEquals(foundBasicLobby.getNumRounds(), testBasicLobbyCreated.getNumRounds());
        assertEquals(foundBasicLobby.getIsPublic(), testBasicLobbyCreated.getIsPublic());
        assertEquals(foundBasicLobby.getNumOptions(), testBasicLobbyCreated.getNumOptions());
        assertEquals(foundBasicLobby.getLobbyCreatorPlayerToken(), testBasicLobbyCreated.getLobbyCreatorPlayerToken());
        assertEquals(foundBasicLobby.getJoinedPlayerNames().size(), testBasicLobbyCreated.getJoinedPlayerNames().size());
        assertEquals(foundBasicLobby.getContinent().size(), testBasicLobbyCreated.getContinent().size());
        assertEquals(5, foundBasicLobby.getContinent().size());

        // check whether player is creator of lobby
        Player foundTestPlayer = playerRepository.findByToken(foundBasicLobby.getLobbyCreatorPlayerToken());
        assertEquals(foundTestPlayer.getPlayerName(), testPlayer1.getPlayerName());
        assertTrue(testBasicLobbyCreated.getJoinedPlayerNames().contains(testPlayer1.getPlayerName()));
        assertTrue(foundBasicLobby.getJoinedPlayerNames().contains(testPlayer1.getPlayerName()));
    }

    @Test
    @Transactional
    void testCreatePublicBasicLobby_someInvalidContinents_overwrite() {
        basicLobbyCreateDTO.setContinent(new ArrayList<String>(
                Arrays.asList("World", "Antartica", "Europe", "Americas")));
        // create lobby
        Lobby basicLobbyInput = DTOMapper.INSTANCE.convertBasicLobbyCreateDTOtoEntity(basicLobbyCreateDTO);
        BasicLobby testBasicLobbyCreated = lobbyService.createBasicLobby(basicLobbyInput, testPlayer1.getToken(), true);
        Long lobbyId = testBasicLobbyCreated.getLobbyId();

        // find lobby with lobbyId
        Lobby foundLobby = lobbyRepository.findByLobbyId(lobbyId);
        BasicLobby foundBasicLobby = (BasicLobby) foundLobby;

        // check if lobbies are the same
        assertEquals(foundBasicLobby.getLobbyId(), testBasicLobbyCreated.getLobbyId());
        assertEquals(foundBasicLobby.getLobbyName(), testBasicLobbyCreated.getLobbyName());
        assertEquals(foundBasicLobby.getNumSeconds(), testBasicLobbyCreated.getNumSeconds());
        assertEquals(foundBasicLobby.getNumRounds(), testBasicLobbyCreated.getNumRounds());
        assertEquals(foundBasicLobby.getIsPublic(), testBasicLobbyCreated.getIsPublic());
        assertEquals(foundBasicLobby.getNumOptions(), testBasicLobbyCreated.getNumOptions());
        assertEquals(foundBasicLobby.getLobbyCreatorPlayerToken(), testBasicLobbyCreated.getLobbyCreatorPlayerToken());
        assertEquals(foundBasicLobby.getJoinedPlayerNames().size(), testBasicLobbyCreated.getJoinedPlayerNames().size());
        assertEquals(foundBasicLobby.getContinent().size(), testBasicLobbyCreated.getContinent().size());
        assertEquals(2, foundBasicLobby.getContinent().size());

        // check whether player is creator of lobby
        Player foundTestPlayer = playerRepository.findByToken(foundBasicLobby.getLobbyCreatorPlayerToken());
        assertEquals(foundTestPlayer.getPlayerName(), testPlayer1.getPlayerName());
        assertTrue(testBasicLobbyCreated.getJoinedPlayerNames().contains(testPlayer1.getPlayerName()));
        assertTrue(foundBasicLobby.getJoinedPlayerNames().contains(testPlayer1.getPlayerName()));
    }

    @Test
    @Transactional
    void testCreatePublicAdvancedLobby() {
        // create lobby
        Lobby advancedLobbyInput = DTOMapper.INSTANCE.convertAdvancedLobbyCreateDTOtoEntity(advancedLobbyCreateDTO);
        AdvancedLobby testAdvancedLobbyCreated = lobbyService.createAdvancedLobby(advancedLobbyInput, testPlayer1.getToken(), true);
        Long lobbyId = testAdvancedLobbyCreated.getLobbyId();

        // find lobby with lobbyId
        Lobby foundLobby = lobbyRepository.findByLobbyId(lobbyId);
        AdvancedLobby foundAdvancedLobby = (AdvancedLobby) foundLobby;

        // check if lobbies are the same
        assertEquals(foundAdvancedLobby.getLobbyId(), testAdvancedLobbyCreated.getLobbyId());
        assertEquals(foundAdvancedLobby.getLobbyName(), testAdvancedLobbyCreated.getLobbyName());
        assertEquals(foundAdvancedLobby.getNumSeconds(), testAdvancedLobbyCreated.getNumSeconds());
        assertEquals(foundAdvancedLobby.getNumRounds(), testAdvancedLobbyCreated.getNumRounds());
        assertEquals(foundAdvancedLobby.getNumSecondsUntilHint(), testAdvancedLobbyCreated.getNumSecondsUntilHint());
        assertEquals(foundAdvancedLobby.getHintInterval(), testAdvancedLobbyCreated.getHintInterval());
        assertEquals(foundAdvancedLobby.getMaxNumGuesses(), testAdvancedLobbyCreated.getMaxNumGuesses());
        assertEquals(foundAdvancedLobby.getIsPublic(), testAdvancedLobbyCreated.getIsPublic());
        assertEquals(foundAdvancedLobby.getLobbyCreatorPlayerToken(), testAdvancedLobbyCreated.getLobbyCreatorPlayerToken());
        assertEquals(foundAdvancedLobby.getJoinedPlayerNames().size(), testAdvancedLobbyCreated.getJoinedPlayerNames().size());
        assertEquals(foundAdvancedLobby.getContinent().size(), testAdvancedLobbyCreated.getContinent().size());
        assertEquals(5, foundAdvancedLobby.getContinent().size());

        // check whether player is creator of lobby
        Player foundTestPlayer = playerRepository.findByToken(foundAdvancedLobby.getLobbyCreatorPlayerToken());
        assertEquals(foundTestPlayer.getPlayerName(), testPlayer1.getPlayerName());
        assertTrue(testAdvancedLobbyCreated.getJoinedPlayerNames().contains(testPlayer1.getPlayerName()));
        assertTrue(foundAdvancedLobby.getJoinedPlayerNames().contains(testPlayer1.getPlayerName()));
    }

    @Test
    @Transactional
    void testCreatePrivateAdvancedLobby() {
        advancedLobbyCreateDTO.setIsPublic(false);
        // create lobby
        Lobby advancedLobbyInput = DTOMapper.INSTANCE.convertAdvancedLobbyCreateDTOtoEntity(advancedLobbyCreateDTO);
        AdvancedLobby testAdvancedLobbyCreated = lobbyService.createAdvancedLobby(advancedLobbyInput, testPlayer1.getToken(), false);
        Long lobbyId = testAdvancedLobbyCreated.getLobbyId();

        // find lobby with lobbyId
        Lobby foundLobby = lobbyRepository.findByLobbyId(lobbyId);
        AdvancedLobby foundAdvancedLobby = (AdvancedLobby) foundLobby;

        // check if lobbies are the same
        assertEquals(foundAdvancedLobby.getLobbyId(), testAdvancedLobbyCreated.getLobbyId());
        assertEquals(foundAdvancedLobby.getLobbyName(), testAdvancedLobbyCreated.getLobbyName());
        assertEquals(foundAdvancedLobby.getNumSeconds(), testAdvancedLobbyCreated.getNumSeconds());
        assertEquals(foundAdvancedLobby.getNumRounds(), testAdvancedLobbyCreated.getNumRounds());
        assertEquals(foundAdvancedLobby.getNumSecondsUntilHint(), testAdvancedLobbyCreated.getNumSecondsUntilHint());
        assertEquals(foundAdvancedLobby.getHintInterval(), testAdvancedLobbyCreated.getHintInterval());
        assertEquals(foundAdvancedLobby.getMaxNumGuesses(), testAdvancedLobbyCreated.getMaxNumGuesses());
        assertEquals(foundAdvancedLobby.getIsPublic(), testAdvancedLobbyCreated.getIsPublic());
        assertEquals(foundAdvancedLobby.getLobbyCreatorPlayerToken(), testAdvancedLobbyCreated.getLobbyCreatorPlayerToken());
        assertEquals(foundAdvancedLobby.getJoinedPlayerNames().size(), testAdvancedLobbyCreated.getJoinedPlayerNames().size());
        assertEquals(foundAdvancedLobby.getContinent().size(), testAdvancedLobbyCreated.getContinent().size());
        assertEquals(5, foundAdvancedLobby.getContinent().size());

        // check if lobby is private
        assertFalse(foundAdvancedLobby.getIsPublic());
        assertNotNull(foundAdvancedLobby.getPrivateLobbyKey());

        // check whether player is creator of lobby
        Player foundTestPlayer = playerRepository.findByToken(foundAdvancedLobby.getLobbyCreatorPlayerToken());
        assertEquals(foundTestPlayer.getPlayerName(), testPlayer1.getPlayerName());
        assertTrue(testAdvancedLobbyCreated.getJoinedPlayerNames().contains(testPlayer1.getPlayerName()));
        assertTrue(foundAdvancedLobby.getJoinedPlayerNames().contains(testPlayer1.getPlayerName()));
    }

    @Test
    @Transactional
    void testCreatePublicAdvancedLobby_onlyInvalidContinents_overwrite() {
        advancedLobbyCreateDTO.setContinent(new ArrayList<String>(
                Arrays.asList("World")));
        // create lobby
        Lobby advancedLobbyInput = DTOMapper.INSTANCE.convertAdvancedLobbyCreateDTOtoEntity(advancedLobbyCreateDTO);
        AdvancedLobby testAdvancedLobbyCreated = lobbyService.createAdvancedLobby(advancedLobbyInput, testPlayer1.getToken(), true);
        Long lobbyId = testAdvancedLobbyCreated.getLobbyId();

        // find lobby with lobbyId
        Lobby foundLobby = lobbyRepository.findByLobbyId(lobbyId);
        AdvancedLobby foundAdvancedLobby = (AdvancedLobby) foundLobby;

        // check if lobbies are the same
        assertEquals(foundAdvancedLobby.getLobbyId(), testAdvancedLobbyCreated.getLobbyId());
        assertEquals(foundAdvancedLobby.getLobbyName(), testAdvancedLobbyCreated.getLobbyName());
        assertEquals(foundAdvancedLobby.getNumSeconds(), testAdvancedLobbyCreated.getNumSeconds());
        assertEquals(foundAdvancedLobby.getNumRounds(), testAdvancedLobbyCreated.getNumRounds());
        assertEquals(foundAdvancedLobby.getNumSecondsUntilHint(), testAdvancedLobbyCreated.getNumSecondsUntilHint());
        assertEquals(foundAdvancedLobby.getHintInterval(), testAdvancedLobbyCreated.getHintInterval());
        assertEquals(foundAdvancedLobby.getMaxNumGuesses(), testAdvancedLobbyCreated.getMaxNumGuesses());
        assertEquals(foundAdvancedLobby.getIsPublic(), testAdvancedLobbyCreated.getIsPublic());
        assertEquals(foundAdvancedLobby.getLobbyCreatorPlayerToken(), testAdvancedLobbyCreated.getLobbyCreatorPlayerToken());
        assertEquals(foundAdvancedLobby.getJoinedPlayerNames().size(), testAdvancedLobbyCreated.getJoinedPlayerNames().size());
        assertEquals(foundAdvancedLobby.getContinent().size(), testAdvancedLobbyCreated.getContinent().size());
        assertEquals(5, foundAdvancedLobby.getContinent().size());

        // check whether player is creator of lobby
        Player foundTestPlayer = playerRepository.findByToken(foundAdvancedLobby.getLobbyCreatorPlayerToken());
        assertEquals(foundTestPlayer.getPlayerName(), testPlayer1.getPlayerName());
        assertTrue(testAdvancedLobbyCreated.getJoinedPlayerNames().contains(testPlayer1.getPlayerName()));
        assertTrue(foundAdvancedLobby.getJoinedPlayerNames().contains(testPlayer1.getPlayerName()));
    }

    @Test
    @Transactional
    void testCreatePublicAdvancedLobby_someInvalidContinents_overwrite() {
        advancedLobbyCreateDTO.setContinent(new ArrayList<String>(
                Arrays.asList("World", "Antartica", "Europe", "Americas")));
        // create lobby
        Lobby advancedLobbyInput = DTOMapper.INSTANCE.convertAdvancedLobbyCreateDTOtoEntity(advancedLobbyCreateDTO);
        AdvancedLobby testAdvancedLobbyCreated = lobbyService.createAdvancedLobby(advancedLobbyInput, testPlayer1.getToken(), true);
        Long lobbyId = testAdvancedLobbyCreated.getLobbyId();

        // find lobby with lobbyId
        Lobby foundLobby = lobbyRepository.findByLobbyId(lobbyId);
        AdvancedLobby foundAdvancedLobby = (AdvancedLobby) foundLobby;

        // check if lobbies are the same
        assertEquals(foundAdvancedLobby.getLobbyId(), testAdvancedLobbyCreated.getLobbyId());
        assertEquals(foundAdvancedLobby.getLobbyName(), testAdvancedLobbyCreated.getLobbyName());
        assertEquals(foundAdvancedLobby.getNumSeconds(), testAdvancedLobbyCreated.getNumSeconds());
        assertEquals(foundAdvancedLobby.getNumRounds(), testAdvancedLobbyCreated.getNumRounds());
        assertEquals(foundAdvancedLobby.getNumSecondsUntilHint(), testAdvancedLobbyCreated.getNumSecondsUntilHint());
        assertEquals(foundAdvancedLobby.getHintInterval(), testAdvancedLobbyCreated.getHintInterval());
        assertEquals(foundAdvancedLobby.getMaxNumGuesses(), testAdvancedLobbyCreated.getMaxNumGuesses());
        assertEquals(foundAdvancedLobby.getIsPublic(), testAdvancedLobbyCreated.getIsPublic());
        assertEquals(foundAdvancedLobby.getLobbyCreatorPlayerToken(), testAdvancedLobbyCreated.getLobbyCreatorPlayerToken());
        assertEquals(foundAdvancedLobby.getJoinedPlayerNames().size(), testAdvancedLobbyCreated.getJoinedPlayerNames().size());
        assertEquals(foundAdvancedLobby.getContinent().size(), testAdvancedLobbyCreated.getContinent().size());
        assertEquals(2, foundAdvancedLobby.getContinent().size());

        // check whether player is creator of lobby
        Player foundTestPlayer = playerRepository.findByToken(foundAdvancedLobby.getLobbyCreatorPlayerToken());
        assertEquals(foundTestPlayer.getPlayerName(), testPlayer1.getPlayerName());
        assertTrue(testAdvancedLobbyCreated.getJoinedPlayerNames().contains(testPlayer1.getPlayerName()));
        assertTrue(foundAdvancedLobby.getJoinedPlayerNames().contains(testPlayer1.getPlayerName()));
    }

    @Test
    @Transactional
    void testJoinPublicBasicLobby() {
        // create lobby
        Lobby basicLobbyInput = DTOMapper.INSTANCE.convertBasicLobbyCreateDTOtoEntity(basicLobbyCreateDTO);
        BasicLobby testBasicLobbyCreated = lobbyService.createBasicLobby(basicLobbyInput, testPlayer1.getToken(), true);
        Long lobbyId = testBasicLobbyCreated.getLobbyId();

        // find lobby with lobbyId
        Lobby foundLobby = lobbyRepository.findByLobbyId(lobbyId);

        // let testPlayer2 join lobby
        lobbyService.joinLobby(foundLobby, testPlayer2.getToken(), testPlayer2.getWsConnectionId());
        lobbyService.joinLobby(foundLobby, testPlayer3.getToken(), testPlayer3.getWsConnectionId());
        foundLobby = lobbyRepository.findByLobbyId(lobbyId);

        // check if player is in lobby
        assertEquals("testBasicLobby", foundLobby.getLobbyName());
        assertEquals(3, foundLobby.getJoinedPlayerNames().size());
        assertTrue(foundLobby.getJoinedPlayerNames().contains(testPlayer1.getPlayerName()));
        assertTrue(foundLobby.getJoinedPlayerNames().contains(testPlayer2.getPlayerName()));
        assertEquals(testPlayer2.getLobbyId(), foundLobby.getLobbyId());
    }

    @Test
    @Transactional
    void testJoinPrivateBasicLobby() {
        basicLobbyCreateDTO.setIsPublic(false);
        // create lobby
        Lobby basicLobbyInput = DTOMapper.INSTANCE.convertBasicLobbyCreateDTOtoEntity(basicLobbyCreateDTO);
        BasicLobby testBasicLobbyCreated = lobbyService.createBasicLobby(basicLobbyInput, testPlayer1.getToken(), false);
        Long lobbyId = testBasicLobbyCreated.getLobbyId();

        // find lobby with lobbyId
        Lobby foundLobby = lobbyRepository.findByLobbyId(lobbyId);

        // check if key is correct
        lobbyService.checkIfLobbyIsJoinable(foundLobby.getLobbyId(), foundLobby.getPrivateLobbyKey());

        // then let testPlayers join lobby
        lobbyService.joinLobby(foundLobby, testPlayer2.getToken(), testPlayer2.getWsConnectionId());
        lobbyService.joinLobby(foundLobby, testPlayer3.getToken(), testPlayer3.getWsConnectionId());
        foundLobby = lobbyRepository.findByLobbyId(lobbyId);

        // check if player is in lobby
        assertEquals("testBasicLobby", foundLobby.getLobbyName());
        assertEquals(3, foundLobby.getJoinedPlayerNames().size());
        assertTrue(foundLobby.getJoinedPlayerNames().contains(testPlayer1.getPlayerName()));
        assertTrue(foundLobby.getJoinedPlayerNames().contains(testPlayer2.getPlayerName()));
        assertEquals(testPlayer2.getLobbyId(), foundLobby.getLobbyId());
    }

    @Test
    @Transactional
    void testJoinPublicAdvancedLobby() {
        // create lobby
        Lobby advancedLobbyInput = DTOMapper.INSTANCE.convertAdvancedLobbyCreateDTOtoEntity(advancedLobbyCreateDTO);
        AdvancedLobby testAdvancedLobbyCreated = lobbyService.createAdvancedLobby(advancedLobbyInput, testPlayer1.getToken(), true);
        Long lobbyId = testAdvancedLobbyCreated.getLobbyId();

        // find lobby with lobbyId
        Lobby foundLobby = lobbyRepository.findByLobbyId(lobbyId);

        // let testPlayer2 join lobby
        lobbyService.joinLobby(foundLobby, testPlayer2.getToken(), testPlayer2.getWsConnectionId());
        lobbyService.joinLobby(foundLobby, testPlayer3.getToken(), testPlayer3.getWsConnectionId());
        foundLobby = lobbyRepository.findByLobbyId(lobbyId);

        // check if player is in lobby
        assertEquals("testAdvancedLobby", foundLobby.getLobbyName());
        assertEquals(3, foundLobby.getJoinedPlayerNames().size());
        assertTrue(foundLobby.getJoinedPlayerNames().contains(testPlayer1.getPlayerName()));
        assertTrue(foundLobby.getJoinedPlayerNames().contains(testPlayer2.getPlayerName()));
        assertEquals(testPlayer2.getLobbyId(), foundLobby.getLobbyId());
    }

    @Test
    @Transactional
    void testJoinPrivateBasicLobby_invalidKey_exceptionThrown(){
        basicLobbyCreateDTO.setIsPublic(false);
        // create lobby
        Lobby basicLobbyInput = DTOMapper.INSTANCE.convertBasicLobbyCreateDTOtoEntity(basicLobbyCreateDTO);
        BasicLobby testBasicLobbyCreated = lobbyService.createBasicLobby(basicLobbyInput, testPlayer1.getToken(), false);
        Long lobbyId = testBasicLobbyCreated.getLobbyId();

        // find lobby with lobbyId
        Lobby foundLobby = lobbyRepository.findByLobbyId(lobbyId);

        // try to join non-existent lobby
        assertThrows(ResponseStatusException.class, () -> lobbyService.checkIfLobbyIsJoinable(foundLobby.getLobbyId(), "invalidKey"));
    }

    @Test
    @Transactional
    void testJoinPrivateAdvancedLobby_invalidKey_exceptionThrown(){
        advancedLobbyCreateDTO.setIsPublic(false);
        // create lobby
        Lobby advancedLobbyInput = DTOMapper.INSTANCE.convertAdvancedLobbyCreateDTOtoEntity(advancedLobbyCreateDTO);
        AdvancedLobby testAdvancedLobbyCreated = lobbyService.createAdvancedLobby(advancedLobbyInput, testPlayer1.getToken(), false);
        Long lobbyId = testAdvancedLobbyCreated.getLobbyId();

        // find lobby with lobbyId
        Lobby foundLobby = lobbyRepository.findByLobbyId(lobbyId);

        // try to join non-existent lobby
        assertThrows(ResponseStatusException.class, () -> lobbyService.checkIfLobbyIsJoinable(foundLobby.getLobbyId(), "invalidKey"));
    }

    @Test
    @Transactional
    void testJoinNonExistentLobby_exceptionThrown(){
        // create player
        Player testPlayer4 = new Player();
        testPlayer4.setPlayerName("testPlayer4");
        testPlayer4.setPassword("testPassword4");
        Player createdPlayer = playerService.createPlayer(testPlayer4);
        createdPlayer.setWsConnectionId("testWsConnectionId4");

        // try to join non-existent lobby
        assertThrows(ResponseStatusException.class, () -> lobbyService.checkIfLobbyIsJoinable(0L, ""));
    }

    @Test
    @Transactional
    void testCreatePlayerCreatePublicBasicLobbyAndLeaveLobby() {
        // create player
        Player testPlayer4 = new Player();
        testPlayer4.setPlayerName("testPlayer4");
        testPlayer4.setPassword("testPassword4");
        Player createdPlayer = playerService.createPlayer(testPlayer4);
        createdPlayer.setWsConnectionId("testWsConnectionId4");

        // create lobby
        Lobby basicLobbyInput = DTOMapper.INSTANCE.convertBasicLobbyCreateDTOtoEntity(basicLobbyCreateDTO);
        BasicLobby testBasicLobbyCreated = lobbyService.createBasicLobby(basicLobbyInput, createdPlayer.getToken(), true);
        Long lobbyId = testBasicLobbyCreated.getLobbyId();

        // find lobby with lobbyId
        Lobby foundLobby = lobbyRepository.findByLobbyId(lobbyId);

        // check if players are in lobby
        assertEquals("testBasicLobby", foundLobby.getLobbyName());
        assertEquals(createdPlayer.getLobbyId(), foundLobby.getLobbyId());
        assertEquals(1, foundLobby.getJoinedPlayerNames().size());
        assertTrue(foundLobby.getJoinedPlayerNames().contains(createdPlayer.getPlayerName()));

        // let createdPlayer leave lobby
        lobbyService.leaveLobby(foundLobby, createdPlayer.getToken());

        // check if createdPlayer has left the lobby
        assertNull(createdPlayer.getLobbyId());
        assertFalse(createdPlayer.isCreator());

        // check if lobby was deleted (if all players have left the lobby, the lobby is deleted)
        assertNull(lobbyRepository.findByLobbyId(lobbyId));
        // check if player still exists
        assertNotNull(playerService.getPlayerById(createdPlayer.getId(), createdPlayer.getToken()));
    }

    @Test
    @Transactional
    void testCreatePlayerCreatePublicAdvancedLobbyAndLeaveLobby() {
        // create player
        Player testPlayer4 = new Player();
        testPlayer4.setPlayerName("testPlayer4");
        testPlayer4.setPassword("testPassword4");
        Player createdPlayer = playerService.createPlayer(testPlayer4);
        createdPlayer.setWsConnectionId("testWsConnectionId4");

        // create lobby
        Lobby advancedLobbyInput = DTOMapper.INSTANCE.convertAdvancedLobbyCreateDTOtoEntity(advancedLobbyCreateDTO);
        AdvancedLobby testAdvancedLobbyCreated = lobbyService.createAdvancedLobby(advancedLobbyInput, createdPlayer.getToken(), true);
        Long lobbyId = testAdvancedLobbyCreated.getLobbyId();

        // find lobby with lobbyId
        Lobby foundLobby = lobbyRepository.findByLobbyId(lobbyId);

        // check if players are in lobby
        assertEquals("testAdvancedLobby", foundLobby.getLobbyName());
        assertEquals(createdPlayer.getLobbyId(), foundLobby.getLobbyId());
        assertEquals(1, foundLobby.getJoinedPlayerNames().size());
        assertTrue(foundLobby.getJoinedPlayerNames().contains(createdPlayer.getPlayerName()));

        // let createdPlayer leave lobby
        lobbyService.leaveLobby(foundLobby, createdPlayer.getToken());

        // check if createdPlayer has left the lobby
        assertNull(createdPlayer.getLobbyId());
        assertFalse(createdPlayer.isCreator());

        // check if lobby was deleted (if all players have left the lobby, the lobby is deleted)
        assertNull(lobbyRepository.findByLobbyId(lobbyId));
        // check if player still exists
        assertNotNull(playerService.getPlayerById(createdPlayer.getId(), createdPlayer.getToken()));
    }

    @Test
    @Transactional
    void testCreateMultiplePlayersCreatePublicBasicLobbyLetMultiplePlayersJoinAndLetLobbyCreatorLeave() {
        // create player
        Player testPlayer4 = new Player();
        testPlayer4.setPlayerName("testPlayer4");
        testPlayer4.setPassword("testPassword4");
        Player lobbyCreator = playerService.createPlayer(testPlayer4);
        lobbyCreator.setWsConnectionId("testWsConnectionId4");

        // create player
        Player testPlayer5 = new Player();
        testPlayer5.setPlayerName("testPlayer5");
        testPlayer5.setPassword("testPassword5");
        Player lobbyAdminAfterLobbyCreatorLeft = playerService.createPlayer(testPlayer5);
        lobbyAdminAfterLobbyCreatorLeft.setWsConnectionId("testWsConnectionId5");

        // create player
        Player testPlayer6 = new Player();
        testPlayer6.setPlayerName("testPlayer6");
        testPlayer6.setPassword("testPassword6");
        Player coPlayer = playerService.createPlayer(testPlayer6);
        coPlayer.setWsConnectionId("testWsConnectionId6");

        // create lobby
        Lobby basicLobbyInput = DTOMapper.INSTANCE.convertBasicLobbyCreateDTOtoEntity(basicLobbyCreateDTO);
        BasicLobby testBasicLobbyCreated = lobbyService.createBasicLobby(basicLobbyInput, lobbyCreator.getToken(), true);
        Long lobbyId = testBasicLobbyCreated.getLobbyId();

        // find lobby with lobbyId
        Lobby foundLobby = lobbyRepository.findByLobbyId(lobbyId);

        // let other players join lobby
        lobbyService.joinLobby(foundLobby, lobbyAdminAfterLobbyCreatorLeft.getToken(), lobbyAdminAfterLobbyCreatorLeft.getWsConnectionId());
        lobbyService.joinLobby(foundLobby, coPlayer.getToken(), coPlayer.getWsConnectionId());
        foundLobby = lobbyRepository.findByLobbyId(lobbyId);

        // check if players are in lobby
        assertEquals("testBasicLobby", foundLobby.getLobbyName());
        assertEquals(lobbyCreator.getLobbyId(), foundLobby.getLobbyId());
        assertEquals(3, foundLobby.getJoinedPlayerNames().size());
        assertTrue(foundLobby.getJoinedPlayerNames().contains(lobbyCreator.getPlayerName()));
        assertTrue(foundLobby.getJoinedPlayerNames().contains(lobbyAdminAfterLobbyCreatorLeft.getPlayerName()));
        assertTrue(foundLobby.getJoinedPlayerNames().contains(coPlayer.getPlayerName()));

        // let createdPlayer leave lobby
        lobbyService.leaveLobby(foundLobby, lobbyCreator.getToken());

        foundLobby = lobbyRepository.findByLobbyId(lobbyId);

        // check if createdPlayer has left the lobby
        assertNull(lobbyCreator.getLobbyId());
        // check that lobby was not deleted
        assertNotNull(foundLobby);
        assertEquals(2, foundLobby.getJoinedPlayerNames().size());
        assertTrue(foundLobby.getJoinedPlayerNames().contains(lobbyAdminAfterLobbyCreatorLeft.getPlayerName()));
        assertTrue(foundLobby.getJoinedPlayerNames().contains(coPlayer.getPlayerName()));
        // check if lobbyAdminAfterLobbyCreatorLeft is now the lobbyCreator
        assertEquals(lobbyAdminAfterLobbyCreatorLeft.getToken(), foundLobby.getLobbyCreatorPlayerToken());

        // check if player still exists
        assertNotNull(playerService.getPlayerById(lobbyCreator.getId(), lobbyCreator.getToken()));
    }

    @Test
    @Transactional
    void testCreateMultiplePlayersCreatePublicAdvancedLobbyLetMultiplePlayersJoinAndLetLobbyCreatorLeave() {
        // create player
        Player testPlayer4 = new Player();
        testPlayer4.setPlayerName("testPlayer4");
        testPlayer4.setPassword("testPassword4");
        Player lobbyCreator = playerService.createPlayer(testPlayer4);
        lobbyCreator.setWsConnectionId("testWsConnectionId4");

        // create player
        Player testPlayer5 = new Player();
        testPlayer5.setPlayerName("testPlayer5");
        testPlayer5.setPassword("testPassword5");
        Player lobbyAdminAfterLobbyCreatorLeft = playerService.createPlayer(testPlayer5);
        lobbyAdminAfterLobbyCreatorLeft.setWsConnectionId("testWsConnectionId5");

        // create player
        Player testPlayer6 = new Player();
        testPlayer6.setPlayerName("testPlayer6");
        testPlayer6.setPassword("testPassword6");
        Player coPlayer = playerService.createPlayer(testPlayer6);
        coPlayer.setWsConnectionId("testWsConnectionId6");

        // create lobby
        Lobby advancedLobbyInput = DTOMapper.INSTANCE.convertAdvancedLobbyCreateDTOtoEntity(advancedLobbyCreateDTO);
        AdvancedLobby testAdvancedLobbyCreated = lobbyService.createAdvancedLobby(advancedLobbyInput, lobbyCreator.getToken(), true);
        Long lobbyId = testAdvancedLobbyCreated.getLobbyId();

        // find lobby with lobbyId
        Lobby foundLobby = lobbyRepository.findByLobbyId(lobbyId);

        // let other players join lobby
        lobbyService.joinLobby(foundLobby, lobbyAdminAfterLobbyCreatorLeft.getToken(), lobbyAdminAfterLobbyCreatorLeft.getWsConnectionId());
        lobbyService.joinLobby(foundLobby, coPlayer.getToken(), coPlayer.getWsConnectionId());
        foundLobby = lobbyRepository.findByLobbyId(lobbyId);

        // check if players are in lobby
        assertEquals("testAdvancedLobby", foundLobby.getLobbyName());
        assertEquals(lobbyCreator.getLobbyId(), foundLobby.getLobbyId());
        assertEquals(3, foundLobby.getJoinedPlayerNames().size());
        assertTrue(foundLobby.getJoinedPlayerNames().contains(lobbyCreator.getPlayerName()));
        assertTrue(foundLobby.getJoinedPlayerNames().contains(lobbyAdminAfterLobbyCreatorLeft.getPlayerName()));
        assertTrue(foundLobby.getJoinedPlayerNames().contains(coPlayer.getPlayerName()));

        // let createdPlayer leave lobby
        lobbyService.leaveLobby(foundLobby, lobbyCreator.getToken());

        foundLobby = lobbyRepository.findByLobbyId(lobbyId);

        // check if createdPlayer has left the lobby
        assertNull(lobbyCreator.getLobbyId());
        // check that lobby was not deleted
        assertNotNull(foundLobby);
        assertEquals(2, foundLobby.getJoinedPlayerNames().size());
        assertTrue(foundLobby.getJoinedPlayerNames().contains(lobbyAdminAfterLobbyCreatorLeft.getPlayerName()));
        assertTrue(foundLobby.getJoinedPlayerNames().contains(coPlayer.getPlayerName()));
        // check if lobbyAdminAfterLobbyCreatorLeft is now the lobbyCreator
        assertEquals(lobbyAdminAfterLobbyCreatorLeft.getToken(), foundLobby.getLobbyCreatorPlayerToken());

        // check if player still exists
        assertNotNull(playerService.getPlayerById(lobbyCreator.getId(), lobbyCreator.getToken()));
    }

    @Test
    @Transactional
    void testCreatePlayerCreatePublicAdvancedLobbyAndLogout() {
        // create player
        Player testPlayer4 = new Player();
        testPlayer4.setPlayerName("testPlayer4");
        testPlayer4.setPassword("testPassword4");
        Player createdPlayer = playerService.createPlayer(testPlayer4);
        createdPlayer.setWsConnectionId("testWsConnectionId4");

        // create lobby
        Lobby advancedLobbyInput = DTOMapper.INSTANCE.convertAdvancedLobbyCreateDTOtoEntity(advancedLobbyCreateDTO);
        AdvancedLobby testAdvancedLobbyCreated = lobbyService.createAdvancedLobby(advancedLobbyInput, createdPlayer.getToken(), true);
        Long lobbyId = testAdvancedLobbyCreated.getLobbyId();

        // find lobby with lobbyId
        Lobby foundLobby = lobbyRepository.findByLobbyId(lobbyId);

        // check if players are in lobby
        assertEquals("testAdvancedLobby", foundLobby.getLobbyName());
        assertEquals(createdPlayer.getLobbyId(), foundLobby.getLobbyId());
        assertEquals(1, foundLobby.getJoinedPlayerNames().size());
        assertTrue(foundLobby.getJoinedPlayerNames().contains(createdPlayer.getPlayerName()));

        // let createdPlayer logout
        playerService.prepareLogoutPlayer(createdPlayer.getId(), createdPlayer.getToken());
        if (createdPlayer.getLobbyId() != null) {
            lobbyService.disconnectPlayer(createdPlayer.getToken());
        }
        else if (!createdPlayer.isPermanent()) {
            playerService.deletePlayer(createdPlayer.getId(), createdPlayer.getToken());
        }

        // check if createdPlayer has been deleted
        assertNull(playerRepository.findByToken(createdPlayer.getToken()));
        // check if lobby was deleted (if all players have left the lobby, the lobby is deleted)
        assertNull(lobbyRepository.findByLobbyId(lobbyId));
    }

    @Test
    @Transactional
    void testCreatePlayerCreatePublicBasicLobbyAndLogout() {
        // create player
        Player testPlayer4 = new Player();
        testPlayer4.setPlayerName("testPlayer4");
        testPlayer4.setPassword("testPassword4");
        Player createdPlayer = playerService.createPlayer(testPlayer4);
        createdPlayer.setWsConnectionId("testWsConnectionId4");

        // create lobby
        Lobby basicLobbyInput = DTOMapper.INSTANCE.convertBasicLobbyCreateDTOtoEntity(basicLobbyCreateDTO);
        BasicLobby testBasicLobbyCreated = lobbyService.createBasicLobby(basicLobbyInput, createdPlayer.getToken(), true);
        Long lobbyId = testBasicLobbyCreated.getLobbyId();

        // find lobby with lobbyId
        Lobby foundLobby = lobbyRepository.findByLobbyId(lobbyId);

        // check if players are in lobby
        assertEquals("testBasicLobby", foundLobby.getLobbyName());
        assertEquals(createdPlayer.getLobbyId(), foundLobby.getLobbyId());
        assertEquals(1, foundLobby.getJoinedPlayerNames().size());
        assertTrue(foundLobby.getJoinedPlayerNames().contains(createdPlayer.getPlayerName()));

        // let createdPlayer logout
        playerService.prepareLogoutPlayer(createdPlayer.getId(), createdPlayer.getToken());
        if (createdPlayer.getLobbyId() != null) {
            lobbyService.disconnectPlayer(createdPlayer.getToken());
        }
        else if (!createdPlayer.isPermanent()) {
            playerService.deletePlayer(createdPlayer.getId(), createdPlayer.getToken());
        }

        // check if createdPlayer has been deleted
        assertNull(playerRepository.findByToken(createdPlayer.getToken()));
        // check if lobby was deleted (if all players have left the lobby, the lobby is deleted)
        assertNull(lobbyRepository.findByLobbyId(lobbyId));
    }

    @Test
    @Transactional
    void testCreatePlayerCreatePublicBasicLobbyLeaveAndCreateNewAdvancedLobby() {
        // create player
        Player testPlayer4 = new Player();
        testPlayer4.setPlayerName("testPlayer4");
        testPlayer4.setPassword("testPassword4");
        Player createdPlayer = playerService.createPlayer(testPlayer4);
        createdPlayer.setWsConnectionId("testWsConnectionId4");

        // create basic lobby
        Lobby basicLobbyInput = DTOMapper.INSTANCE.convertBasicLobbyCreateDTOtoEntity(basicLobbyCreateDTO);
        BasicLobby testBasicLobbyCreated = lobbyService.createBasicLobby(basicLobbyInput, createdPlayer.getToken(), true);
        Long lobbyIdBasic = testBasicLobbyCreated.getLobbyId();

        // find lobby with lobbyId
        Lobby foundBasicLobby = lobbyRepository.findByLobbyId(lobbyIdBasic);

        // check if players are in lobby
        assertEquals("testBasicLobby", foundBasicLobby.getLobbyName());
        assertEquals(createdPlayer.getLobbyId(), foundBasicLobby.getLobbyId());
        assertEquals(1, foundBasicLobby.getJoinedPlayerNames().size());
        assertTrue(foundBasicLobby.getJoinedPlayerNames().contains(createdPlayer.getPlayerName()));

        // let createdPlayer leave lobby
        lobbyService.leaveLobby(foundBasicLobby, createdPlayer.getToken());

        // check if createdPlayer has left the lobby
        assertNull(createdPlayer.getLobbyId());
        assertFalse(createdPlayer.isCreator());

        // check if lobby was deleted (if all players have left the lobby, the lobby is deleted)
        assertNull(lobbyRepository.findByLobbyId(lobbyIdBasic));
        // check if player still exists
        assertNotNull(playerService.getPlayerById(createdPlayer.getId(), createdPlayer.getToken()));

        // create advanced lobby now
        Lobby advancedLobbyInput = DTOMapper.INSTANCE.convertAdvancedLobbyCreateDTOtoEntity(advancedLobbyCreateDTO);
        AdvancedLobby testAdvancedLobbyCreated = lobbyService.createAdvancedLobby(advancedLobbyInput, createdPlayer.getToken(), true);
        Long lobbyIdAdvanced = testAdvancedLobbyCreated.getLobbyId();

        // find lobby with lobbyId
        Lobby foundAdvancedLobby = lobbyRepository.findByLobbyId(lobbyIdAdvanced);

        // check if players are in lobby
        assertEquals("testAdvancedLobby", foundAdvancedLobby.getLobbyName());
        assertEquals(createdPlayer.getLobbyId(), foundAdvancedLobby.getLobbyId());
        assertEquals(1, foundAdvancedLobby.getJoinedPlayerNames().size());
        assertTrue(foundAdvancedLobby.getJoinedPlayerNames().contains(createdPlayer.getPlayerName()));

    }
}
