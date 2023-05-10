package ch.uzh.ifi.hase.soprafs23.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PlayerTest {

    @Test
    public void createPlayer() {
        // Create a new Player
        Player player = new Player();
        player.setId(1L);
        player.setPlayerName("TestPlayer");
        player.setPassword("password");
        player.setToken("test-token");
        player.setCreator(true);
        player.setLobbyId(1L);
        player.setWsConnectionId("test-connection");

        // Assert that the Player was created correctly
        assertNotNull(player);
        assertNotNull(player.getId());
        assertEquals("TestPlayer", player.getPlayerName());
        assertEquals("password", player.getPassword());
        assertEquals("test-token", player.getToken());
        assertEquals(true, player.isCreator());
        assertEquals(1L, player.getLobbyId());
        assertEquals("test-connection", player.getWsConnectionId());
    }

    @Test
    public void updatePlayer() {
        // Create a new Player
        Player player = new Player();
        player.setPlayerName("TestPlayer");
        player.setPassword("password");
        player.setToken("test-token");
        player.setCreator(true);

        // Update the Player
        player.setPlayerName("UpdatedPlayer");
        player.setPassword("new-password");
        player.setToken("new-token");
        player.setCreator(false);
        player.setLobbyId(1L);
        player.setWsConnectionId("connection-id");

        // Assert that the Player was updated correctly
        assertEquals("UpdatedPlayer", player.getPlayerName());
        assertEquals("new-password", player.getPassword());
        assertEquals("new-token", player.getToken());
        assertEquals(false, player.isCreator());
        assertEquals(1L, player.getLobbyId());
        assertEquals("connection-id", player.getWsConnectionId());
    }
}
