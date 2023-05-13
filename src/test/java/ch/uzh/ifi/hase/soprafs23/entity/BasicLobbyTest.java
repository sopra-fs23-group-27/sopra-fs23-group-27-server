package ch.uzh.ifi.hase.soprafs23.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.persistence.DiscriminatorValue;

public class BasicLobbyTest {

    @Test
    void testGetSetNumOptions() {
        BasicLobby lobby = new BasicLobby();
        lobby.setNumOptions(4);
        assertEquals(4, lobby.getNumOptions());
    }

    @Test
    void testDiscriminatorValue() {
        assertEquals("BASIC", BasicLobby.class.getAnnotation(DiscriminatorValue.class).value());
    }
}
