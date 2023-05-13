package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.AdvancedLobby;
import ch.uzh.ifi.hase.soprafs23.entity.BasicLobby;
import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        basicLobby.setIsPublic(isPublic);
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
        assertEquals(found.getIsPublic(), basicLobby.getIsPublic());
        assertEquals(found.getJoinedPlayerNames(), basicLobby.getJoinedPlayerNames());
        assertTrue(found.isJoinable());

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
        Lobby found = lobbyRepository.findByLobbyId(lobbyId);

        // then
        assertNotNull(found.getLobbyId());
        assertEquals(found.getLobbyId(), lobbyId);
        assertEquals(found.getLobbyName(), basicLobby.getLobbyName());
        assertEquals(found.getIsPublic(), basicLobby.getIsPublic());
        assertEquals(found.getJoinedPlayerNames(), basicLobby.getJoinedPlayerNames());
        assertTrue(found.isJoinable());

    }

    @Test
    public void findAllBasicAndAdvancedLobbies_success() {
        // given
        BasicLobby basicLobby = new BasicLobby();

        String lobbyName = "testLobbyName";
        boolean isPublic = true;
        List<String> joinedPlayerNames = List.of("testPlayerName1", "testPlayerName2", "testPlayerName3");

        basicLobby.setLobbyName(lobbyName);
        basicLobby.setIsPublic(isPublic);
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
        advancedLobby.setIsPublic(isPublic2);
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

        assertTrue(allFound.get(0).isJoinable());
        assertTrue(allFound.get(1).isJoinable());


        assertNotNull(allFound.get(0).getLobbyId());
        assertEquals(allFound.get(0).getLobbyId(), lobbyId);
        assertEquals(allFound.get(0).getLobbyName(), basicLobby.getLobbyName());
        assertEquals(allFound.get(0).getIsPublic(), basicLobby.getIsPublic());
        assertEquals(allFound.get(0).getJoinedPlayerNames(), basicLobby.getJoinedPlayerNames());

        assertNotNull(allFound.get(1).getLobbyId());
        assertEquals(allFound.get(1).getLobbyId(), lobbyId2);
        assertEquals(allFound.get(1).getLobbyName(), advancedLobby.getLobbyName());
        assertEquals(allFound.get(1).getIsPublic(), advancedLobby.getIsPublic());
        assertEquals(allFound.get(1).getJoinedPlayerNames(), advancedLobby.getJoinedPlayerNames());

        assertEquals(allBasicFound.get(0).getLobbyId(), lobbyId);
        assertEquals(allBasicFound.get(0).getLobbyName(), basicLobby.getLobbyName());
        assertEquals(allBasicFound.get(0).getIsPublic(), basicLobby.getIsPublic());
        assertEquals(allBasicFound.get(0).getJoinedPlayerNames(), basicLobby.getJoinedPlayerNames());

        assertEquals(allAdvancedFound.get(0).getLobbyId(), lobbyId2);
        assertEquals(allAdvancedFound.get(0).getLobbyName(), advancedLobby.getLobbyName());
        assertEquals(allAdvancedFound.get(0).getIsPublic(), advancedLobby.getIsPublic());
        assertEquals(allAdvancedFound.get(0).getJoinedPlayerNames(), advancedLobby.getJoinedPlayerNames());
    }

    @Test
    public void findAllPublicAndJoinableBasicAndAdvancedLobbies_success() {
        // create basic lobby that is public and joinable
        BasicLobby testBasicLobbyPublicAndJoinable = new BasicLobby();

        List<String> joinedPlayerNamesBasicLobbyPublicAndJoinable = List.of(
                "testPlayer1_BasicLobbyPublicAndJoinable",
                "testPlayer2_BasicLobbyPublicAndJoinable",
                "testPlayer3_BasicLobbyPublicAndJoinable");

        testBasicLobbyPublicAndJoinable.setLobbyName("testBasicLobbyPublicAndJoinable");
        testBasicLobbyPublicAndJoinable.setIsPublic(true);
        testBasicLobbyPublicAndJoinable.setJoinedPlayerNames(joinedPlayerNamesBasicLobbyPublicAndJoinable);
        testBasicLobbyPublicAndJoinable.setJoinable(true);

        entityManager.persist(testBasicLobbyPublicAndJoinable);
        entityManager.flush();

        // create basic lobby that is private and joinable
        BasicLobby testBasicLobbyPrivateAndJoinable = new BasicLobby();

        List<String> joinedPlayerNamesBasicLobbyPrivateAndJoinable = List.of(
                "testPlayer1_BasicLobbyPrivateAndJoinable",
                "testPlayer2_BasicLobbyPrivateAndJoinable",
                "testPlayer3_BasicLobbyPrivateAndJoinable");

        testBasicLobbyPrivateAndJoinable.setLobbyName("testBasicLobbyPrivateAndJoinable");
        testBasicLobbyPrivateAndJoinable.setIsPublic(false);
        testBasicLobbyPrivateAndJoinable.setJoinedPlayerNames(joinedPlayerNamesBasicLobbyPrivateAndJoinable);
        testBasicLobbyPrivateAndJoinable.setJoinable(true);

        entityManager.persist(testBasicLobbyPrivateAndJoinable);
        entityManager.flush();

        // create basic lobby that is public and but not joinable
        BasicLobby testBasicLobbyPublicAndNonJoinable = new BasicLobby();

        List<String> joinedPlayerNamesBasicLobbyPublicAndNonJoinable = List.of(
                "testPlayer1_BasicLobbyPublicAndNonJoinable",
                "testPlayer2_BasicLobbyPublicAndNonJoinable",
                "testPlayer3_BasicLobbyPublicAndNonJoinable");

        testBasicLobbyPublicAndNonJoinable.setLobbyName("testBasicLobbyPublicAndNonJoinable");
        testBasicLobbyPublicAndNonJoinable.setIsPublic(true);
        testBasicLobbyPublicAndNonJoinable.setJoinedPlayerNames(joinedPlayerNamesBasicLobbyPublicAndNonJoinable);
        testBasicLobbyPublicAndNonJoinable.setJoinable(false);

        entityManager.persist(testBasicLobbyPublicAndNonJoinable);
        entityManager.flush();

        // create advanced lobby that is public and joinable
        AdvancedLobby testAdvancedLobbyPublicAndJoinable = new AdvancedLobby();

        List<String> joinedPlayerNamesAdvancedLobbyPublicAndJoinable = List.of(
                "testPlayer1_AdvancedLobbyPublicAndJoinable",
                "testPlayer2_AdvancedLobbyPublicAndJoinable",
                "testPlayer3_AdvancedLobbyPublicAndJoinable");

        testAdvancedLobbyPublicAndJoinable.setLobbyName("testAdvancedLobbyPublicAndJoinable");
        testAdvancedLobbyPublicAndJoinable.setIsPublic(true);
        testAdvancedLobbyPublicAndJoinable.setJoinedPlayerNames(joinedPlayerNamesAdvancedLobbyPublicAndJoinable);
        testAdvancedLobbyPublicAndJoinable.setJoinable(true);

        entityManager.persist(testAdvancedLobbyPublicAndJoinable);
        entityManager.flush();

        // create advanced lobby that is public but not joinable
        AdvancedLobby testAdvancedLobbyPublicAndNonJoinable = new AdvancedLobby();

        List<String> joinedPlayerNamesAdvancedLobbyPublicAndNonJoinable = List.of(
                "testPlayer1_AdvancedLobbyPublicAndNonJoinable",
                "testPlayer2_AdvancedLobbyPublicAndNonJoinable",
                "testPlayer3_AdvancedLobbyPublicAndNonJoinable");

        testAdvancedLobbyPublicAndNonJoinable.setLobbyName("testAdvancedLobbyPublicAndNonJoinable");
        testAdvancedLobbyPublicAndNonJoinable.setIsPublic(true);
        testAdvancedLobbyPublicAndNonJoinable.setJoinedPlayerNames(joinedPlayerNamesAdvancedLobbyPublicAndNonJoinable);
        testAdvancedLobbyPublicAndNonJoinable.setJoinable(false);

        entityManager.persist(testAdvancedLobbyPublicAndNonJoinable);
        entityManager.flush();


        // find all basic and advanced lobbies that are public and joinable
        List<Lobby> lobbiesFound = lobbyRepository.findAllByIsPublicAndIsJoinable(true, true);

        // test if all lobbies found are public and joinable
        assertEquals(2, lobbiesFound.size());

        assertTrue(lobbiesFound.get(0).isJoinable());
        assertTrue(lobbiesFound.get(1).isJoinable());
    }
}
