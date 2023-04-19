package ch.uzh.ifi.hase.soprafs23.entity;

import javax.persistence.*;

@Entity
@DiscriminatorValue("ADVANCED")
public class AdvancedLobby extends Lobby {


    private int numSecondsUntilHint;
    private int hintInterval;
    private int maxNumGuesses;

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

    public int getMaxNumGuesses() {
        return maxNumGuesses;
    }

    public void setMaxNumGuesses(int maxNumGuesses) {
        this.maxNumGuesses = maxNumGuesses;
    }
}