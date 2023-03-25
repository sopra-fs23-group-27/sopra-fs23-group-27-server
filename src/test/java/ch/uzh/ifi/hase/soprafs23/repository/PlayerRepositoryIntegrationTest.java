package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.Player;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class PlayerRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PlayerRepository playerRepository;

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
}
