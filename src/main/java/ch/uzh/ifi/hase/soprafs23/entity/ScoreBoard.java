package ch.uzh.ifi.hase.soprafs23.entity;

import java.util.ArrayList;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;

@Entity
public class ScoreBoard {

    // This class is not tested, modify if needed

    @ElementCollection
    private ArrayList lastRound;

    @ElementCollection
    private ArrayList consecutiveCorrectGuesses;

    @ElementCollection
    private ArrayList allRounds;

    public ArrayList getLastRound() {
        return lastRound;
    }

    public void setLastRound(ArrayList lastRound) {
        this.lastRound = lastRound;
    }

    public ArrayList getConsecutiveCorrectGuesses() {
        return consecutiveCorrectGuesses;
    }

    public void setConsecutiveCorrectGuesses(ArrayList consecutiveCorrectGuesses) {
        this.consecutiveCorrectGuesses = consecutiveCorrectGuesses;
    }

    public ArrayList getAllRounds() {
        return allRounds;
    }

    public void setAllRounds(ArrayList allRounds) {
        this.allRounds = allRounds;
    }

}
