package ch.uzh.ifi.hase.soprafs23.service;

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
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.HashMap;
import java.util.Map;

@WebAppConfiguration
@SpringBootTest
public class AuthenticationServiceIntegrationTest {
    @Autowired
    private LobbyRepository lobbyRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Mock
    private WebSocketService webSocketService;

    @Autowired
    private LobbyService lobbyService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private GameService gameService;

    private final Map<String, Long> authenticatedJoins = new HashMap<>();
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

//    @Test
//    void testJoiningProcess(){
//        // create basic lobby
//        Lobby basicLobbyInput = DTOMapper.INSTANCE.convertBasicLobbyCreateDTOtoEntity(basicLobbyCreateDTO);
//        BasicLobby testBasicLobbyCreated = lobbyService.createBasicLobby(basicLobbyInput, testPlayer1.getToken(), true);
//        Long lobbyId = testBasicLobbyCreated.getLobbyId();
//
//        // test joining process
//        // test joining basic lobby
//        lobbyService.joinLobby(basicLobbyInput.getLobbyId(), testPlayer1.getToken());
//        lobbyService.joinLobby(basicLobbyInput.getLobbyId(), testPlayer2.getToken());
//        lobbyService.joinLobby(basicLobbyInput.getLobbyId(), testPlayer3.getToken());
//
//
//    }

}
