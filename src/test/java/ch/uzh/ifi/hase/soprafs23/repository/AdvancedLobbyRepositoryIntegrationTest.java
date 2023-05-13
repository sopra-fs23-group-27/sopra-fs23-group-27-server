package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.AdvancedLobby;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class AdvancedLobbyRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AdvancedLobbyRepository advancedLobbyRepository;

    @Test
    public void whenFindAll_thenReturnAllLobbies() {
        // given
        AdvancedLobby lobby1 = new AdvancedLobby();
        lobby1.setLobbyName("Lobby 1");
        entityManager.persist(lobby1);

        AdvancedLobby lobby2 = new AdvancedLobby();
        lobby2.setLobbyName("Lobby 2");
        entityManager.persist(lobby2);

        AdvancedLobby lobby3 = new AdvancedLobby();
        lobby3.setLobbyName("Lobby 3");
        entityManager.persist(lobby3);

        entityManager.flush();

        // when
        List<AdvancedLobby> lobbies = advancedLobbyRepository.findAll();

        // then
        assertThat(lobbies).hasSize(3);
        assertThat(lobbies).extracting(AdvancedLobby::getLobbyName)
                .containsExactly(lobby1.getLobbyName(), lobby2.getLobbyName(), lobby3.getLobbyName());
    }
}
