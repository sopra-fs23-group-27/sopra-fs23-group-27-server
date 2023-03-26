package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.AdvancedLobby;
import ch.uzh.ifi.hase.soprafs23.entity.BasicLobby;
import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class LobbyRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LobbyRepository lobbyRepository;

    @Test
    public void findBasicLobbyByLobbyName_success() {
        // given
        BasicLobby basicLobby = new BasicLobby();

        String lobbyName = "testLobbyName";
        int numOptions = 4;
        boolean isPublic = true;
        List<String> joinedPlayerNames = List.of("testPlayerName1", "testPlayerName2", "testPlayerName3");

        basicLobby.setLobbyName(lobbyName);
        basicLobby.setNumOptions(numOptions);
        basicLobby.setPublic(isPublic);
        basicLobby.setJoinedPlayerNames(joinedPlayerNames);

        entityManager.persist(basicLobby);
        entityManager.flush();

        Long lobbyId = basicLobby.getLobbyId();
        System.out.println("The ID of the created lobby: " + lobbyId);


        // when
        Lobby found = lobbyRepository.findByLobbyName("testLobbyName");

        // then
        assertNotNull(found.getLobbyId());
        assertEquals(found.getLobbyId(), lobbyId);
        assertEquals(found.getLobbyName(), basicLobby.getLobbyName());
        assertEquals(found.isPublic(), basicLobby.isPublic());
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
        basicLobby.setPublic(isPublic);
        basicLobby.setJoinedPlayerNames(joinedPlayerNames);

        entityManager.persist(basicLobby);
        entityManager.flush();

        Long lobbyId = basicLobby.getLobbyId();
        System.out.println("The ID of the created lobby: " + lobbyId);


        // when
        Lobby found = lobbyRepository.findByLobbyId(lobbyId);

        // then
        assertNotNull(found.getLobbyId());
        assertEquals(found.getLobbyId(), lobbyId);
        assertEquals(found.getLobbyName(), basicLobby.getLobbyName());
        assertEquals(found.isPublic(), basicLobby.isPublic());
        assertEquals(found.getJoinedPlayerNames(), basicLobby.getJoinedPlayerNames());

    }

    @Test
    public void findAllBasicAndAdvancedLobbies_success() {
        // given
        BasicLobby basicLobby = new BasicLobby();

        String lobbyName = "testLobbyName";
        boolean isPublic = true;
        List<String> joinedPlayerNames = List.of("testPlayerName1", "testPlayerName2", "testPlayerName3");

        basicLobby.setLobbyName(lobbyName);
        basicLobby.setPublic(isPublic);
        basicLobby.setJoinedPlayerNames(joinedPlayerNames);

        entityManager.persist(basicLobby);
        entityManager.flush();

        Long lobbyId = basicLobby.getLobbyId();
        System.out.println("The ID of the created BASIC lobby: " + lobbyId);

        AdvancedLobby advancedLobby = new AdvancedLobby();

        String lobbyName2 = "testLobbyName2";
        boolean isPublic2 = true;
        List<String> joinedPlayerNames2 = List.of("testPlayerName1", "testPlayerName2", "testPlayerName3");

        advancedLobby.setLobbyName(lobbyName2);
        advancedLobby.setPublic(isPublic2);
        advancedLobby.setJoinedPlayerNames(joinedPlayerNames2);

        entityManager.persist(advancedLobby);
        entityManager.flush();

        Long lobbyId2 = advancedLobby.getLobbyId();
        System.out.println("The ID of the created ADVANCED lobby: " + lobbyId2);


        // when
        List<Lobby> allFound = lobbyRepository.findAll();
        List<BasicLobby> allBasicFound = lobbyRepository.findAllBasicLobbies();
        List<AdvancedLobby> allAdvancedFound = lobbyRepository.findAllAdvancedLobbies();

        // then
        assertEquals(allFound.size(), 2);
        assertEquals(allBasicFound.size(), 1);
        assertEquals(allAdvancedFound.size(), 1);


        assertNotNull(allFound.get(0).getLobbyId());
        assertEquals(allFound.get(0).getLobbyId(), lobbyId);
        assertEquals(allFound.get(0).getLobbyName(), basicLobby.getLobbyName());
        assertEquals(allFound.get(0).isPublic(), basicLobby.isPublic());
        assertEquals(allFound.get(0).getJoinedPlayerNames(), basicLobby.getJoinedPlayerNames());

        assertNotNull(allFound.get(1).getLobbyId());
        assertEquals(allFound.get(1).getLobbyId(), lobbyId2);
        assertEquals(allFound.get(1).getLobbyName(), advancedLobby.getLobbyName());
        assertEquals(allFound.get(1).isPublic(), advancedLobby.isPublic());
        assertEquals(allFound.get(1).getJoinedPlayerNames(), advancedLobby.getJoinedPlayerNames());

        assertEquals(allBasicFound.get(0).getLobbyId(), lobbyId);
        assertEquals(allBasicFound.get(0).getLobbyName(), basicLobby.getLobbyName());
        assertEquals(allBasicFound.get(0).isPublic(), basicLobby.isPublic());
        assertEquals(allBasicFound.get(0).getJoinedPlayerNames(), basicLobby.getJoinedPlayerNames());

        assertEquals(allAdvancedFound.get(0).getLobbyId(), lobbyId2);
        assertEquals(allAdvancedFound.get(0).getLobbyName(), advancedLobby.getLobbyName());
        assertEquals(allAdvancedFound.get(0).isPublic(), advancedLobby.isPublic());
        assertEquals(allAdvancedFound.get(0).getJoinedPlayerNames(), advancedLobby.getJoinedPlayerNames());


    }
}
