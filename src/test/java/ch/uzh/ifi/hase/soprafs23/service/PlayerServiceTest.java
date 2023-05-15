package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private LobbyRepository lobbyRepository;

    @InjectMocks
    private PlayerService playerService;

    private Player testPlayer;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // given
        testPlayer = new Player();
        testPlayer.setId(1L);
        testPlayer.setPassword("password");
        testPlayer.setPlayerName("testPlayerName");

        // when -> any object is being save in the playerRepository -> return the dummy
        // testPlayer
        Mockito.when(playerRepository.save(Mockito.any())).thenReturn(testPlayer);
    }

    @Test
    public void createPlayer_validInputs_success() {
        // when -> any object is being save in the playerRepository -> return the dummy
        // testPlayer
        Player createdPlayer = playerService.createPlayer(testPlayer);

        // then
        Mockito.verify(playerRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(testPlayer.getId(), createdPlayer.getId());
        assertEquals(testPlayer.getPassword(), createdPlayer.getPassword());
        assertEquals(testPlayer.getPlayerName(), createdPlayer.getPlayerName());
        assertNotNull(createdPlayer.getToken());
    }

    @Test
    public void createPlayer_duplicateName_throwsException() {
        // given -> a first player has already been created
        playerService.createPlayer(testPlayer);

        // when -> setup additional mocks for PlayerRepository
        Mockito.when(playerRepository.findByPlayerName(Mockito.any())).thenReturn(testPlayer);

        // then -> attempt to create second player with same player -> check that an error
        // is thrown
        assertThrows(ResponseStatusException.class, () -> playerService.createPlayer(testPlayer));
    }

    @Test
    public void createPlayer_duplicateInputs_throwsException() {
        // given -> a first player has already been created
        playerService.createPlayer(testPlayer);

        // when -> setup additional mocks for PlayerRepository
        //Mockito.when(playerRepository.findByName(Mockito.any())).thenReturn(testPlayer);
        Mockito.when(playerRepository.findByPlayerName(Mockito.any())).thenReturn(testPlayer);

        // then -> attempt to create second player with same player -> check that an error
        // is thrown
        assertThrows(ResponseStatusException.class, () -> playerService.createPlayer(testPlayer));
    }

    

}
