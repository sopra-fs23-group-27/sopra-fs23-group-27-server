package ch.uzh.ifi.hase.soprafs23.websocket;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class StompPrincipalTest {

    @Test
    public void getName_ShouldReturnProvidedName() {
        // Arrange
        String name = "Alice";
        StompPrincipal principal = new StompPrincipal(name);
        
        // Act
        String result = principal.getName();
        
        // Assert
        assertEquals(name, result);
    }
    
    @Test
    public void getName_ShouldReturnEmptyStringIfNameIsNull() {
        // Arrange
        StompPrincipal principal = new StompPrincipal(null);
        
        // Act
        String result = principal.getName();
        
        // Assert
        assertEquals(null, result);
    }
}
