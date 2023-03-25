package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the PlayerResource REST resource.
 *
 * @see PlayerService
 */
@WebAppConfiguration
@SpringBootTest
public class PlayerServiceIntegrationTest {

    @Qualifier("playerRepository")
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerService playerService;

    @BeforeEach
    public void setup() {
        playerRepository.deleteAll();
    }

    @Test
    public void createPlayer_validInputs_success() {
        // given
        assertNull(playerRepository.findByPlayername("testPlayername"));

        Player testPlayer = new Player();
        testPlayer.setPassword("password");
        testPlayer.setPlayername("testPlayername");
        testPlayer.setCreationDate(LocalDateTime.of(2023, 3, 5, 15, 0, 0));

        // when
        Player createdPlayer = playerService.createPlayer(testPlayer);

        // then
        assertEquals(testPlayer.getId(), createdPlayer.getId());
        assertEquals(testPlayer.getPassword(), createdPlayer.getPassword());
        assertEquals(testPlayer.getPlayername(), createdPlayer.getPlayername());
        assertNotNull(createdPlayer.getToken());
        assertEquals(PlayerStatus.ONLINE, createdPlayer.getStatus());
    }

    @Test
    public void createPlayer_duplicatePlayername_throwsException() {
        assertNull(playerRepository.findByPlayername("testPlayername"));

        Player testPlayer = new Player();
        testPlayer.setPassword("password");
        testPlayer.setPlayername("testPlayername");
        testPlayer.setCreationDate(LocalDateTime.of(2023, 3, 5, 15, 0, 0));
        Player createdPlayer = playerService.createPlayer(testPlayer);

        // attempt to create second player with same playername
        Player testPlayer2 = new Player();

        // change the name but forget about the playername
        testPlayer2.setPassword("password");
        testPlayer2.setPlayername("testPlayername");

        // check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> playerService.createPlayer(testPlayer2));
    }
}
