package ch.uzh.ifi.hase.soprafs23.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerStatsTest {

    @Test
    public void testSetAndGetTotalCorrectGuesses() {
        PlayerStats playerStats = new PlayerStats();
        playerStats.setTotalCorrectGuesses(5);
        assertEquals(5, playerStats.getTotalCorrectGuesses());
    }

    @Test
    public void testSetAndGetNRoundsPlayed() {
        PlayerStats playerStats = new PlayerStats();
        playerStats.setnRoundsPlayed(10);
        assertEquals(10, playerStats.getnRoundsPlayed());
    }

    @Test
    public void testSetAndGetAvgTimeUntilCorrectGuess() {
        PlayerStats playerStats = new PlayerStats();
        playerStats.setAvgTimeUntilCorrectGuess(30);
        assertEquals(30, playerStats.getAvgTimeUntilCorrectGuess());
    }

    @Test
    public void testSetAndGetNumWrongGuesses() {
        PlayerStats playerStats = new PlayerStats();
        playerStats.setNumWrongGuesses(2);
        assertEquals(2, playerStats.getNumWrongGuesses());
    }
}
