package ch.uzh.ifi.hase.soprafs23.entity;

import java.util.ArrayList;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ScoreBoard {

    // This class is not tested, modify if needed

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

//    @ElementCollection
//    private ArrayList<String> lastRound;
//
//    @ElementCollection
//    private ArrayList<String> consecutiveCorrectGuesses;

//    @ElementCollection
//    private ArrayList<String> allRounds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

//    public ArrayList getLastRound() {
//        return lastRound;
//    }
//
//    public void setLastRound(ArrayList lastRound) {
//        this.lastRound = lastRound;
//    }
//
//    public ArrayList getConsecutiveCorrectGuesses() {
//        return consecutiveCorrectGuesses;
//    }
//
//    public void setConsecutiveCorrectGuesses(ArrayList consecutiveCorrectGuesses) {
//        this.consecutiveCorrectGuesses = consecutiveCorrectGuesses;
//    }

//    public ArrayList getAllRounds() {
//        return allRounds;
//    }
//
//    public void setAllRounds(ArrayList allRounds) {
//        this.allRounds = allRounds;
//    }

}
