package ch.uzh.ifi.hase.soprafs23.service;

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
        assertNull(playerRepository.findByPlayerName("testPlayerName"));

        Player testPlayer = new Player();
        testPlayer.setPassword("password");
        testPlayer.setPlayerName("testPlayerName");

        // when
        Player createdPlayer = playerService.createPlayer(testPlayer);

        // then
        assertEquals(testPlayer.getId(), createdPlayer.getId());
        assertEquals(testPlayer.getPassword(), createdPlayer.getPassword());
        assertEquals(testPlayer.getPlayerName(), createdPlayer.getPlayerName());
        assertNotNull(createdPlayer.getToken());
    }

    @Test
    public void createPlayer_duplicatePlayerName_throwsException() {
        assertNull(playerRepository.findByPlayerName("testPlayerName"));

        Player testPlayer = new Player();
        testPlayer.setPassword("password");
        testPlayer.setPlayerName("testPlayerName");
        Player createdPlayer = playerService.createPlayer(testPlayer);

        // attempt to create second player with same playerName
        Player testPlayer2 = new Player();

        // change the name but forget about the playerName
        testPlayer2.setPassword("password");
        testPlayer2.setPlayerName("testPlayerName");

        // check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> playerService.createPlayer(testPlayer2));
    }
}
