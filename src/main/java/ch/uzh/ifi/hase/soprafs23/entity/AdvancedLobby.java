package ch.uzh.ifi.hase.soprafs23.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@DiscriminatorValue("ADVANCED")
public class AdvancedLobby extends Lobby {


    private int numSecondsUntilHint;
    private int hintInterval;
    private int maxNGuesses;

    public int getNumSecondsUntilHint() {
        return numSecondsUntilHint;
    }

    public void setNumSecondsUntilHint(int numSecondsUntilHint) {
        this.numSecondsUntilHint = numSecondsUntilHint;
    }

    public int getHintInterval() {
        return hintInterval;
    }

    public void setHintInterval(int hintInterval) {
        this.hintInterval = hintInterval;
    }

    public int getMaxNGuesses() {
        return maxNGuesses;
    }

    public void setMaxNGuesses(int maxNGuesses) {
        this.maxNGuesses = maxNGuesses;
    }
}