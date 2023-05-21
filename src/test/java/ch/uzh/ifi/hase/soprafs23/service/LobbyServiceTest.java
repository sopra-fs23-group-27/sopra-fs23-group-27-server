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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        Mockito.when(playerService.getPlayerByToken(Mockito.anyString())).thenReturn(testPlayer1);
        Mockito.when(playerRepository.save(Mockito.any())).thenReturn(testPlayer1);
        Mockito.when(playerRepository.findByToken(Mockito.any())).thenReturn(testPlayer1);
        Mockito.when(playerRepository.findByLobbyId(Mockito.any())).thenReturn(Collections.singletonList(testPlayer1));
    }

    @Test
    void testCreatePublicBasicLobby() {
        // return basicLobby when lobbyRepository.save() or lobbyRepository.findByLobbyId() is called
        Mockito.when(lobbyRepository.findByLobbyId(Mockito.any())).thenReturn(basicLobby);
        Mockito.when(lobbyRepository.save(Mockito.any())).thenReturn(basicLobby);

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
        Mockito.when(lobbyRepository.findByLobbyId(Mockito.any())).thenReturn(basicLobby);
        Mockito.when(lobbyRepository.save(Mockito.any())).thenReturn(basicLobby);

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
        Mockito.when(lobbyRepository.findByLobbyId(Mockito.any())).thenReturn(advancedLobby);
        Mockito.when(lobbyRepository.save(Mockito.any())).thenReturn(advancedLobby);

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
        Mockito.when(lobbyRepository.findByLobbyId(Mockito.any())).thenReturn(advancedLobby);
        Mockito.when(lobbyRepository.save(Mockito.any())).thenReturn(advancedLobby);

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
    public void createLobby_validInputs_success() {
        // return basicLobby  when lobbyRepository.save() or lobbyRepository.findByLobbyId() is called
        // for the first time. Then return advancedLobby when lobbyRepository.save() or lobbyRepository.findByLobbyId()
        // is called
        Mockito.when(lobbyRepository.findByLobbyId(Mockito.any())).thenReturn(basicLobby, advancedLobby);
        Mockito.when(lobbyRepository.save(Mockito.any())).thenReturn(basicLobby, advancedLobby);

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
        Mockito.when(lobbyRepository.findAllByIsPublicAndIsJoinable(true, true)).thenReturn(testLobbies);

        // call method to test
        List<Lobby> allFoundPublicAndJoinableLobbies = lobbyService.getAllPublicAndJoinableLobbies();

        // then
        assertEquals(allFoundPublicAndJoinableLobbies.size(), 2);
    }
}
