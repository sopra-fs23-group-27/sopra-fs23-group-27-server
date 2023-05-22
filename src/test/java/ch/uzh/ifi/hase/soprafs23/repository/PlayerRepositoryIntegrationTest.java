package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class PlayerRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PlayerRepository playerRepository;

    @AfterEach
    void tearDown() {
        playerRepository.deleteAll();
    }

    @Test
    public void findByPlayerName_success() {
        // given
        Player player = new Player();
        player.setPassword("password");
        player.setPlayerName("firstname@lastname");
        player.setToken("1");

        entityManager.persist(player);
        entityManager.flush();

        // when
        Player found = playerRepository.findByPlayerName(player.getPlayerName());

        // then
        assertNotNull(found.getId());
        assertEquals(found.getPassword(), player.getPassword());
        assertEquals(found.getPlayerName(), player.getPlayerName());
        assertEquals(found.getToken(), player.getToken());
    }

    @Test
    public void findByToken_success() {
        // given
        Player player = new Player();
        player.setPassword("password");
        player.setPlayerName("firstname@lastname");
        player.setToken("1");

        entityManager.persist(player);
        entityManager.flush();

        // when
        Player found = playerRepository.findByToken(player.getToken());

        // then
        assertNotNull(found.getId());
        assertEquals(found.getPassword(), player.getPassword());
        assertEquals(found.getPlayerName(), player.getPlayerName());
        assertEquals(found.getToken(), player.getToken());
    }

    @Test
    public void findByWsConnectionId_success() {
        // given
        Player player = new Player();
        player.setPassword("password");
        player.setPlayerName("firstname@lastname");
        player.setToken("1");
        player.setWsConnectionId("1");

        entityManager.persist(player);
        entityManager.flush();

        // when
        Player found = playerRepository.findByToken(player.getWsConnectionId());

        // then
        assertNotNull(found.getId());
        assertEquals(found.getPassword(), player.getPassword());
        assertEquals(found.getPlayerName(), player.getPlayerName());
        assertEquals(found.getToken(), player.getToken());
        assertEquals(found.getWsConnectionId(), player.getWsConnectionId());
    }

    @Test
    public void findByLobbyId_success() {
        // given
        Player player = new Player();
        player.setPassword("password");
        player.setPlayerName("player1");
        player.setToken("1");
        player.setLobbyId(1L);

        entityManager.persist(player);
        entityManager.flush();

        Player player2 = new Player();
        player2.setPassword("password");
        player2.setPlayerName("player2");
        player2.setToken("2");
        player2.setLobbyId(2L);

        entityManager.persist(player2);
        entityManager.flush();

        // when
        List<Player> found = playerRepository.findByLobbyId(1L);

        // then
        assertEquals(1, found.size());
        assertEquals(found.get(0).getLobbyId(), player.getLobbyId());
        assertEquals(found.get(0).getPlayerName(), player.getPlayerName());
    }

}
