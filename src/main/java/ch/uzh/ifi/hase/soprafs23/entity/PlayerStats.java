package ch.uzh.ifi.hase.soprafs23.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class PlayerStats {

    // Modify if needed. Likely there is also the PlayerId needed to be stored here

    @Column
    private int totalCorrectGuesses;

    @Column
    private int nRoundsPlayed;

    @Column
    private int avgTimeUntilCorrectGuess;

    @Column
    private int numWrongGuesses;

    public int getTotalCorrectGuesses() {
        return totalCorrectGuesses;
    }

    public void setTotalCorrectGuesses(int totalCorrectGuesses) {
        this.totalCorrectGuesses = totalCorrectGuesses;
    }

    public int getnRoundsPlayed() {
        return nRoundsPlayed;
    }

    public void setnRoundsPlayed(int nRoundsPlayed) {
        this.nRoundsPlayed = nRoundsPlayed;
    }

    public int getAvgTimeUntilCorrectGuess() {
        return avgTimeUntilCorrectGuess;
    }

    public void setAvgTimeUntilCorrectGuess(int avgTimeUntilCorrectGuess) {
        this.avgTimeUntilCorrectGuess = avgTimeUntilCorrectGuess;
    }

    public int getNumWrongGuesses() {
        return numWrongGuesses;
    }

    public void setNumWrongGuesses(int numWrongGuesses) {
        this.numWrongGuesses = numWrongGuesses;
    }
}
