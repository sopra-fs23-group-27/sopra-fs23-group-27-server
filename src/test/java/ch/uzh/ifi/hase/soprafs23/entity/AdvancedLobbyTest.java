package ch.uzh.ifi.hase.soprafs23.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.persistence.DiscriminatorValue;

public class AdvancedLobbyTest {

    @Test
    void testGetSetNumSecondsUntilHint() {
        AdvancedLobby lobby = new AdvancedLobby();
        lobby.setNumSecondsUntilHint(10);
        assertEquals(10, lobby.getNumSecondsUntilHint());
    }

    @Test
    void testGetSetHintInterval() {
        AdvancedLobby lobby = new AdvancedLobby();
        lobby.setHintInterval(5);
        assertEquals(5, lobby.getHintInterval());
    }

    @Test
    void testGetSetMaxNumGuesses() {
        AdvancedLobby lobby = new AdvancedLobby();
        lobby.setMaxNumGuesses(3);
        assertEquals(3, lobby.getMaxNumGuesses());
    }

    @Test
    void testDiscriminatorValue() {
        assertEquals("ADVANCED", AdvancedLobby.class.getAnnotation(DiscriminatorValue.class).value());
    }
}
