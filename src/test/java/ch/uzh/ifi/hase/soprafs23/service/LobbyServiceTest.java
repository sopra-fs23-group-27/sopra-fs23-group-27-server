package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.BasicLobby;
import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.BasicLobbyCreateDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    @InjectMocks
    private Player testPlayer1;
    @InjectMocks
    private BasicLobbyCreateDTO basicLobbyCreateDTO;
    @InjectMocks
    private Lobby basicLobby;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // given
        testPlayer1 = new Player();
        testPlayer1.setPlayerName("testPlayer1");
        testPlayer1.setToken("testToken1");
        testPlayer1.setWsConnectionId("testWsConnectionId1");
        playerRepository.save(testPlayer1);
        playerRepository.flush();

        basicLobbyCreateDTO = new BasicLobbyCreateDTO();
        basicLobbyCreateDTO.setLobbyName("testBasicLobby");
        basicLobbyCreateDTO.setIsPublic(true);
        basicLobbyCreateDTO.setNumSeconds(10);
        basicLobbyCreateDTO.setNumOptions(4);

        basicLobby = new BasicLobby();
        basicLobby.setLobbyName("testBasicLobby");
        basicLobby.setIsPublic(true);
        basicLobby.setNumSeconds(10);
        basicLobby.setLobbyCreatorPlayerToken(testPlayer1.getToken());
        basicLobby.addPlayerToLobby(testPlayer1.getPlayerName());

        // when -> any object is being save in the userRepository -> return the dummy testUser
        Mockito.doNothing().when(webSocketService).sendToLobby(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.when(playerService.getPlayerByToken(Mockito.anyString())).thenReturn(testPlayer1);
        Mockito.when(playerRepository.save(Mockito.any())).thenReturn(testPlayer1);
        Mockito.when(playerRepository.findByToken(Mockito.any())).thenReturn(testPlayer1);
        Mockito.when(playerRepository.findByLobbyId(Mockito.any())).thenReturn(Collections.singletonList(testPlayer1));

    }

//    @Test
//    void createLobby_returnLobbyId() {
//        Lobby basicLobbyInput = DTOMapper.INSTANCE.convertBasicLobbyCreateDTOtoEntity(basicLobbyCreateDTO);
//        BasicLobby testBasicLobbyCreated = lobbyService.createBasicLobby(basicLobbyInput, testPlayer1.getToken(), true);
//
//        // return testBasicLobbyCreated when lobbyRepository.save() or lobbyRepository.findByLobbyId() is called
//        Mockito.when(lobbyRepository.save(Mockito.any())).thenReturn(basicLobby);
//        Mockito.when(lobbyRepository.findByLobbyId(Mockito.any())).thenReturn(testBasicLobbyCreated);
//
//        // verify that lobby was saved into repository
//        Mockito.verify(lobbyRepository, Mockito.times(1)).save(Mockito.any());
//        // verify that player was updated
//        Mockito.verify(playerRepository, Mockito.times(1)).save(Mockito.any());
//
//        assertNotNull(testBasicLobbyCreated);
//    }
}
