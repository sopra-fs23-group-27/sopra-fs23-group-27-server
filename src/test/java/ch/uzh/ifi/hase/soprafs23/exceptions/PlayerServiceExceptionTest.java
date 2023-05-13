package ch.uzh.ifi.hase.soprafs23.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerServiceExceptionTest {

    @Test
    public void testConstructor() {
        String expectedMessage = "test exception";
        PlayerServiceException exception = new PlayerServiceException(expectedMessage);
        assertEquals(expectedMessage, exception.getMessage());
    }
}
