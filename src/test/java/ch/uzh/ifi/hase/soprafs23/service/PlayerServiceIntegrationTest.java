package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerPutDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the PlayerResource REST resource.
 *
 * @see PlayerService
 */
@WebAppConfiguration
@SpringBootTest
public class PlayerServiceIntegrationTest {

    private Player testPlayer;

    @Qualifier("playerRepository")
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerService playerService;

    @BeforeEach
    public void setup() {
        playerRepository.deleteAll();

        // given
        assertNull(playerRepository.findByPlayerName("testUsername"));

        testPlayer = new Player();
        testPlayer.setPassword("password");
        testPlayer.setPlayerName("testPlayerName");
    }

    @AfterEach
    void afterEach() {
        playerRepository.deleteAll();
    }

    @Test
    public void createPlayer_validInputs_success() {
        // given
        assertNull(playerRepository.findByPlayerName("testPlayerName"));

        // when
        Player createdPlayer = playerService.createPlayer(testPlayer);

        // then
        assertEquals(testPlayer.getId(), createdPlayer.getId());
        assertEquals(testPlayer.getPassword(), createdPlayer.getPassword());
        assertEquals(testPlayer.getPlayerName(), createdPlayer.getPlayerName());
        assertNotNull(createdPlayer.getToken());
        assertEquals(0, createdPlayer.getTotalCorrectGuesses());
        assertEquals(0, createdPlayer.getTimeUntilCorrectGuess());
        assertEquals(0, createdPlayer.getNumWrongGuesses());
        assertEquals(0, createdPlayer.getnRoundsPlayed());
        assertFalse(createdPlayer.isPermanent());
    }

    @Test
    public void createPlayer_duplicatePlayerName_throwsException() {
        assertNull(playerRepository.findByPlayerName("testPlayerName"));

        // when
        Player createdPlayer = playerService.createPlayer(testPlayer);

        // attempt to create second player with same playerName
        Player testPlayer2 = new Player();

        // change the name but forget about the playerName
        testPlayer2.setPassword("password");
        testPlayer2.setPlayerName("testPlayerName");

        // check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> playerService.createPlayer(testPlayer2));
    }

    @Test
    public void registerPlayer_validInputs_success() {
        // given
        assertNull(playerRepository.findByPlayerName("testPlayerName"));

        // when
        Player createdPlayer = playerService.registerPlayer(testPlayer);

        // then
        assertEquals(testPlayer.getId(), createdPlayer.getId());
        assertEquals(testPlayer.getPassword(), createdPlayer.getPassword());
        assertEquals(testPlayer.getPlayerName(), createdPlayer.getPlayerName());
        assertEquals(0, createdPlayer.getTotalCorrectGuesses());
        assertEquals(0, createdPlayer.getTimeUntilCorrectGuess());
        assertEquals(0, createdPlayer.getNumWrongGuesses());
        assertEquals(0, createdPlayer.getnRoundsPlayed());
        assertNotNull(createdPlayer.getToken());
        assertTrue(createdPlayer.isPermanent());
    }

    @Test
    public void registerPlayer_duplicatePlayerName_throwsException() {
        assertNull(playerRepository.findByPlayerName("testPlayerName"));

        Player createdPlayer = playerService.registerPlayer(testPlayer);

        // attempt to create second player with same playerName
        Player testPlayer2 = new Player();

        // change the name but forget about the playerName
        testPlayer2.setPassword("password");
        testPlayer2.setPlayerName("testPlayerName");

        // check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> playerService.registerPlayer(testPlayer2));
    }

    @Test
    public void registerPlayerAndUpdatePlayer_updatePlayerNameAndPassword_validInputs_success() {
        // create player
        Player registeredPlayer = playerService.registerPlayer(testPlayer);

        // ensure created player is permanent
        assertTrue(registeredPlayer.isPermanent());

        Long playerId = registeredPlayer.getId();
        String token = registeredPlayer.getToken();

        // create playerPutDTO to update player
        PlayerPutDTO playerPutDTO = new PlayerPutDTO();
        playerPutDTO.setPlayerName("testPlayerNameUpdated");
        playerPutDTO.setPassword("passwordUpdated");

        // update player
        Player updatedPlayer = playerService.updatePlayer(playerId, playerPutDTO, token);

        // then
        assertEquals(registeredPlayer.getId(), updatedPlayer.getId());
        assertEquals("passwordUpdated", updatedPlayer.getPassword());
        assertEquals("testPlayerNameUpdated", updatedPlayer.getPlayerName());
        assertNotEquals(registeredPlayer.getToken(), updatedPlayer.getToken());
        assertNotNull(updatedPlayer.getToken());
        assertTrue(updatedPlayer.isPermanent());
    }

    @Test
    public void registerPlayerAndUpdatePlayer_updateOnlyPlayerName_validInputs_success() {
        // create player
        Player registeredPlayer = playerService.registerPlayer(testPlayer);

        // ensure created player is permanent
        assertTrue(registeredPlayer.isPermanent());

        Long playerId = registeredPlayer.getId();
        String token = registeredPlayer.getToken();

        // create playerPutDTO to update player
        PlayerPutDTO playerPutDTO = new PlayerPutDTO();
        playerPutDTO.setPlayerName("testPlayerNameUpdated");

        // update player
        Player updatedPlayer = playerService.updatePlayer(playerId, playerPutDTO, token);

        // then
        assertEquals(registeredPlayer.getId(), updatedPlayer.getId());
        assertEquals(registeredPlayer.getPassword(), updatedPlayer.getPassword());
        assertEquals("testPlayerNameUpdated", updatedPlayer.getPlayerName());
        assertNotEquals(registeredPlayer.getToken(), updatedPlayer.getToken());
        assertNotNull(updatedPlayer.getToken());
        assertTrue(updatedPlayer.isPermanent());
    }

    @Test
    public void registerPlayerAndUpdatePlayer_updateOnlyPassword_validInputs_success() {
        // create player
        Player registeredPlayer = playerService.registerPlayer(testPlayer);

        // ensure created player is permanent
        assertTrue(registeredPlayer.isPermanent());

        Long playerId = registeredPlayer.getId();
        String token = registeredPlayer.getToken();

        // create playerPutDTO to update player
        PlayerPutDTO playerPutDTO = new PlayerPutDTO();
        playerPutDTO.setPassword("passwordUpdated");

        // update player
        Player updatedPlayer = playerService.updatePlayer(playerId, playerPutDTO, token);

        // then
        assertEquals(registeredPlayer.getId(), updatedPlayer.getId());
        assertEquals("passwordUpdated", updatedPlayer.getPassword());
        assertEquals(registeredPlayer.getPlayerName(), updatedPlayer.getPlayerName());
        assertNotEquals(registeredPlayer.getToken(), updatedPlayer.getToken());
        assertNotNull(updatedPlayer.getToken());
        assertTrue(updatedPlayer.isPermanent());
    }

    @Test
    public void registerPlayerAndUpdatePlayer_updateEmptyDTO_validInputs_success() {
        // create player
        Player registeredPlayer = playerService.registerPlayer(testPlayer);

        // ensure created player is permanent
        assertTrue(registeredPlayer.isPermanent());

        Long playerId = registeredPlayer.getId();
        String token = registeredPlayer.getToken();

        // create playerPutDTO to update player
        PlayerPutDTO playerPutDTO = new PlayerPutDTO();

        // update player
        Player updatedPlayer = playerService.updatePlayer(playerId, playerPutDTO, token);

        // then
        assertEquals(registeredPlayer.getId(), updatedPlayer.getId());
        assertEquals(registeredPlayer.getPassword(), updatedPlayer.getPassword());
        assertEquals(registeredPlayer.getPlayerName(), updatedPlayer.getPlayerName());
        assertEquals(registeredPlayer.getToken(), updatedPlayer.getToken());
        assertNotNull(updatedPlayer.getToken());
        assertTrue(updatedPlayer.isPermanent());
    }

    @Test
    public void registerPlayerAndUpdatePlayer_updatePlayerNameAndPassword_PlayerNameTaken_throwsException() {
        // create first player
        Player firstTestPlayer = new Player();
        firstTestPlayer.setPassword("password");
        firstTestPlayer.setPlayerName("firstTestPlayerName");
        Player registeredPlayer = playerService.registerPlayer(firstTestPlayer);

        // ensure created player is permanent
        assertTrue(registeredPlayer.isPermanent());

        // create second player
        Player secondTestPlayer = new Player();
        secondTestPlayer.setPassword("password");
        secondTestPlayer.setPlayerName("secondTestPlayerName");
        Player createdPlayer = playerService.createPlayer(secondTestPlayer);

        // ensure created player is not registered / not permanent
        assertFalse(createdPlayer.isPermanent());

        Long playerId = registeredPlayer.getId();
        String token = registeredPlayer.getToken();

        // create playerPutDTO to update player
        PlayerPutDTO playerPutDTO = new PlayerPutDTO();
        playerPutDTO.setPlayerName("secondTestPlayerName");
        playerPutDTO.setPassword("passwordUpdated");

        // attempt to update first player
        assertThrows(ResponseStatusException.class, () -> playerService.updatePlayer(playerId, playerPutDTO, token));
    }

    @Test
    public void registerPlayerAndUpdatePlayer_invalidToken_throwsException() {
        // create player
        Player registeredPlayer = playerService.registerPlayer(testPlayer);

        // ensure created player is permanent
        assertTrue(registeredPlayer.isPermanent());

        Long playerId = registeredPlayer.getId();
        String token = "invalidToken";

        // create playerPutDTO to update player
        PlayerPutDTO playerPutDTO = new PlayerPutDTO();
        playerPutDTO.setPlayerName("updatedPlayerName");
        playerPutDTO.setPassword("passwordUpdated");

        // attempt to update first player
        assertThrows(ResponseStatusException.class, () -> playerService.updatePlayer(playerId, playerPutDTO, token));
    }

    @Test
    public void registerPlayerAndLogoutPlayer_validInputs_success() {
        // create player
        Player createdPlayer = playerService.registerPlayer(testPlayer);

        // ensure created player is not registered / not permanent
        assertTrue(createdPlayer.isPermanent());

        Long playerId = createdPlayer.getId();
        String token = createdPlayer.getToken();

        // proceed steps to logout player
        playerService.prepareLogoutPlayer(playerId, token);
        if (!createdPlayer.isPermanent()){
            playerService.deletePlayer(playerId, token);
        }

        // check that player was not deleted
        assertNotNull(playerRepository.findByPlayerName(createdPlayer.getPlayerName()));
        assertNotNull(playerRepository.findByToken(createdPlayer.getToken()));
        assertNotNull(playerRepository.findById(createdPlayer.getId()));
    }

    @Test
    public void registerPlayerAndLogoutPlayer_invalidPlayerId_throwsException() {
        // create player
        Player registeredPlayer = playerService.registerPlayer(testPlayer);

        // ensure created player is permanent
        assertTrue(registeredPlayer.isPermanent());

        Long playerId = 0L;
        String token = registeredPlayer.getToken();

        // attempt logout player
        assertThrows(ResponseStatusException.class, () -> playerService.prepareLogoutPlayer(playerId, token));
    }

    @Test
    public void registerPlayerAndLogoutPlayer_invalidToken_throwsException() {
        // create player
        Player registeredPlayer = playerService.registerPlayer(testPlayer);

        // ensure created player is permanent
        assertTrue(registeredPlayer.isPermanent());

        Long playerId = registeredPlayer.getId();
        String token = "invalidToken";

        // attempt to logout player
        assertThrows(ResponseStatusException.class, () -> playerService.prepareLogoutPlayer(playerId, token));
    }

    @Test
    public void createPlayerAndUpdatePlayer_updatePlayerNameAndPassword_validInputs_success() {
        // create player
        Player createdPlayer = playerService.createPlayer(testPlayer);

        // ensure created player is not registered / not permanent
        assertFalse(createdPlayer.isPermanent());

        Long playerId = createdPlayer.getId();
        String token = createdPlayer.getToken();

        // create playerPutDTO to update player
        PlayerPutDTO playerPutDTO = new PlayerPutDTO();
        playerPutDTO.setPlayerName("testPlayerNameUpdated");
        playerPutDTO.setPassword("passwordUpdated");

        // update player
        Player updatedPlayer = playerService.updatePlayer(playerId, playerPutDTO, token);

        // then
        assertEquals(createdPlayer.getId(), updatedPlayer.getId());
        assertEquals("passwordUpdated", updatedPlayer.getPassword());
        assertEquals("testPlayerNameUpdated", updatedPlayer.getPlayerName());
        assertNotEquals(createdPlayer.getToken(), updatedPlayer.getToken());
        assertNotNull(updatedPlayer.getToken());
        assertTrue(updatedPlayer.isPermanent());
    }

    @Test
    public void createPlayerAndUpdatePlayer_updatePlayerNameAndPassword_PlayerNameTaken_throwsException() {
        // create first player
        Player firstTestPlayer = new Player();
        firstTestPlayer.setPassword("password");
        firstTestPlayer.setPlayerName("firstTestPlayerName");
        Player registeredPlayer = playerService.registerPlayer(firstTestPlayer);

        // ensure created player is permanent
        assertTrue(registeredPlayer.isPermanent());

        // create second player
        Player secondTestPlayer = new Player();
        secondTestPlayer.setPassword("password");
        secondTestPlayer.setPlayerName("secondTestPlayerName");
        Player createdPlayer = playerService.createPlayer(secondTestPlayer);

        // ensure created player is not registered / not permanent
        assertFalse(createdPlayer.isPermanent());

        Long playerId = createdPlayer.getId();
        String token = createdPlayer.getToken();

        // create playerPutDTO to update player
        PlayerPutDTO playerPutDTO = new PlayerPutDTO();
        playerPutDTO.setPlayerName("firstTestPlayerName");
        playerPutDTO.setPassword("passwordUpdated");

        // attempt to update second player
        assertThrows(ResponseStatusException.class, () -> playerService.updatePlayer(playerId, playerPutDTO, token));
    }

    @Test
    public void createPlayerAndUpdatePlayer_invalidToken_throwsException() {
        // create player
        Player createdPlayer = playerService.createPlayer(testPlayer);

        // ensure created player is not registered / not permanent
        assertFalse(createdPlayer.isPermanent());

        Long playerId = createdPlayer.getId();
        String token = "invalidToken";

        // create playerPutDTO to update player
        PlayerPutDTO playerPutDTO = new PlayerPutDTO();
        playerPutDTO.setPlayerName("updatedPlayerName");
        playerPutDTO.setPassword("passwordUpdated");

        // attempt to update second player
        assertThrows(ResponseStatusException.class, () -> playerService.updatePlayer(playerId, playerPutDTO, token));
    }

    @Test
    public void createPlayerAndLogoutPlayer_validInputs_success() {
        // create player
        Player createdPlayer = playerService.createPlayer(testPlayer);

        // ensure created player is not registered / not permanent
        assertFalse(createdPlayer.isPermanent());

        Long playerId = createdPlayer.getId();
        String token = createdPlayer.getToken();

        // proceed steps to logout player
        playerService.prepareLogoutPlayer(playerId, token);
        if (!createdPlayer.isPermanent()){
            playerService.deletePlayer(playerId, token);
        }

        // check that player was deleted
        assertNull(playerRepository.findByPlayerName(createdPlayer.getPlayerName()));
        assertNull(playerRepository.findByToken(createdPlayer.getToken()));
    }

    @Test
    public void createPlayerAndLogoutPlayer_invalidPlayerId_throwsException() {
        // create player
        Player registeredPlayer = playerService.createPlayer(testPlayer);

        // ensure created player is not permanent
        assertFalse(registeredPlayer.isPermanent());

        Long playerId = 0L;
        String token = registeredPlayer.getToken();

        // attempt to logout player
        assertThrows(ResponseStatusException.class, () -> playerService.prepareLogoutPlayer(playerId, token));
    }

    @Test
    public void createPlayerAndLogoutPlayer_invalidToken_throwsException() {
        // create player
        Player registeredPlayer = playerService.createPlayer(testPlayer);

        // ensure created player is not permanent
        assertFalse(registeredPlayer.isPermanent());

        Long playerId = registeredPlayer.getId();
        String token = "invalidToken";

        // attempt to logout player
        assertThrows(ResponseStatusException.class, () -> playerService.prepareLogoutPlayer(playerId, token));
    }
}
