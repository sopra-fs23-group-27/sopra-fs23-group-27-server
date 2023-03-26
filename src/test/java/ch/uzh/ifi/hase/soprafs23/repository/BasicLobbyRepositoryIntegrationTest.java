package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.BasicLobby;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class BasicLobbyRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BasicLobbyRepository basicLobbyRepository;

    @Test
    public void findByLobbyName_success() {
        // given
        BasicLobby basicLobby = new BasicLobby();

        String lobbyName = "testLobbyName";
        int numOptions = 4;
        boolean isPublic = true;
        List<String> joinedPlayerNames = List.of("testPlayerName1", "testPlayerName2", "testPlayerName3");

        basicLobby.setLobbyName(lobbyName);
        basicLobby.setNumOptions(numOptions);
        basicLobby.setIsPublic(isPublic);
        basicLobby.setJoinedPlayerNames(joinedPlayerNames);

        entityManager.persist(basicLobby);
        entityManager.flush();

        Long lobbyId = basicLobby.getLobbyId();
        System.out.println("The ID of the created lobby: " + lobbyId);


        // when
        BasicLobby found = basicLobbyRepository.findBasicLobbyByLobbyName("testLobbyName");

        // then
        assertNotNull(found.getLobbyId());
        assertEquals(found.getLobbyId(), lobbyId);
        assertEquals(found.getLobbyName(), basicLobby.getLobbyName());
        assertEquals(found.getNumOptions(), basicLobby.getNumOptions());
        assertEquals(found.getIsPublic(), basicLobby.getIsPublic());
        assertEquals(found.getJoinedPlayerNames(), basicLobby.getJoinedPlayerNames());

    }

    @Test
    public void findByLobbyId_success() {
        // given
        BasicLobby basicLobby = new BasicLobby();

        String lobbyName = "testLobbyName";
        int numOptions = 4;
        boolean isPublic = true;
        List<String> joinedPlayerNames = List.of("testPlayerName1", "testPlayerName2", "testPlayerName3");

        basicLobby.setLobbyName(lobbyName);
        basicLobby.setNumOptions(numOptions);
        basicLobby.setIsPublic(isPublic);
        basicLobby.setJoinedPlayerNames(joinedPlayerNames);

        entityManager.persist(basicLobby);
        entityManager.flush();

        Long lobbyId = basicLobby.getLobbyId();
        System.out.println("The ID of the created lobby: " + lobbyId);


        // when
        BasicLobby found = basicLobbyRepository.findBasicLobbyById(lobbyId);

        // then
        assertNotNull(found.getLobbyId());
        assertEquals(found.getLobbyId(), lobbyId);
        assertEquals(found.getLobbyName(), basicLobby.getLobbyName());
        assertEquals(found.getNumOptions(), basicLobby.getNumOptions());
        assertEquals(found.getIsPublic(), basicLobby.getIsPublic());
        assertEquals(found.getJoinedPlayerNames(), basicLobby.getJoinedPlayerNames());

    }
}
