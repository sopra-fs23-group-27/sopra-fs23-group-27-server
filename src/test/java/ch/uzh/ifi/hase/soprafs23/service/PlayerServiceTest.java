package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

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
        testPlayer.setPlayername("testPlayername");
        testPlayer.setCreationDate(LocalDateTime.of(2023, 3, 5, 15, 0, 0));

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
        assertEquals(testPlayer.getPlayername(), createdPlayer.getPlayername());
        assertNotNull(createdPlayer.getToken());
        assertEquals(PlayerStatus.ONLINE, createdPlayer.getStatus());
    }

    @Test
    public void createPlayer_duplicateName_throwsException() {
        // given -> a first player has already been created
        playerService.createPlayer(testPlayer);

        // when -> setup additional mocks for PlayerRepository
        Mockito.when(playerRepository.findByPlayername(Mockito.any())).thenReturn(testPlayer);

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
        Mockito.when(playerRepository.findByPlayername(Mockito.any())).thenReturn(testPlayer);

        // then -> attempt to create second player with same player -> check that an error
        // is thrown
        assertThrows(ResponseStatusException.class, () -> playerService.createPlayer(testPlayer));
    }

}
