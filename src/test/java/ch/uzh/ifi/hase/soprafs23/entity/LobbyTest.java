package ch.uzh.ifi.hase.soprafs23.entity;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class LobbyTest {

    @Test
    void testGetSetLobbyId() {
        Lobby lobby = new BasicLobby();
        lobby.setLobbyId(123L);
        assertEquals(123L, lobby.getLobbyId());
    }

    @Test
    void testGetSetLobbyName() {
        Lobby lobby = new BasicLobby();
        lobby.setLobbyName("Test Lobby");
        assertEquals("Test Lobby", lobby.getLobbyName());
    }

    @Test
    void testGetSetIsPublic() {
        Lobby lobby = new BasicLobby();
        lobby.setIsPublic(true);
        assertTrue(lobby.getIsPublic());
    }

    @Test
    void testGetSetNumSeconds() {
        Lobby lobby = new BasicLobby();
        lobby.setNumSeconds(60);
        assertEquals(60, lobby.getNumSeconds());
    }

    @Test
    void testGetSetJoinedPlayerNames() {
        Lobby lobby = new BasicLobby();
        lobby.setJoinedPlayerNames(Arrays.asList("Player1", "Player2"));
        assertEquals(Arrays.asList("Player1", "Player2"), lobby.getJoinedPlayerNames());
    }

    @Test
    void testGetSetLobbyCreatorPlayerToken() {
        Lobby lobby = new BasicLobby();
        lobby.setLobbyCreatorPlayerToken("token123");
        assertEquals("token123", lobby.getLobbyCreatorPlayerToken());
    }

    @Test
    void testGetSetJoinable() {
        Lobby lobby = new BasicLobby();
        lobby.setJoinable(false);
        assertFalse(lobby.isJoinable());
    }

    @Test
    void testGetSetCurrentGameId() {
        Lobby lobby = new BasicLobby();
        lobby.setCurrentGameId(456L);
        assertEquals(456L, lobby.getCurrentGameId());
    }

    @Test
    void testGetSetPrivateLobbyKey() {
        Lobby lobby = new BasicLobby();
        lobby.setPrivateLobbyKey("key123");
        assertEquals("key123", lobby.getPrivateLobbyKey());
    }

    @Test
    void testGetMode() {
        BasicLobby basicLobby = new BasicLobby();
        assertEquals("BASIC", basicLobby.getMode());

        AdvancedLobby advancedLobby = new AdvancedLobby();
        assertEquals("ADVANCED", advancedLobby.getMode());
    }
}