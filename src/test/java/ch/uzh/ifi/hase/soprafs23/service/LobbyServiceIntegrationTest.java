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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@WebAppConfiguration
@SpringBootTest
public class LobbyServiceIntegrationTest {

    @Autowired
    private LobbyRepository lobbyRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Mock
    private WebSocketService webSocketService;

    @Autowired
    private LobbyService lobbyService;

    @Autowired
    private GameService gameService;

    private Player testPlayer1;
    private Player testPlayer2;
    private Player testPlayer3;
    private BasicLobbyCreateDTO basicLobbyCreateDTO;
    private AdvancedLobbyCreateDTO advancedLobbyCreateDTO;
    private Lobby basicLobbyInput;
    private Lobby advancedLobbyInput;

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

        advancedLobbyCreateDTO = new AdvancedLobbyCreateDTO();
        advancedLobbyCreateDTO.setLobbyName("testAdvancedLobby");
        advancedLobbyCreateDTO.setIsPublic(true);
        advancedLobbyCreateDTO.setNumSeconds(100);
        advancedLobbyCreateDTO.setNumSecondsUntilHint(5);
        advancedLobbyCreateDTO.setHintInterval(5);
        advancedLobbyCreateDTO.setMaxNumGuesses(10);

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

}
