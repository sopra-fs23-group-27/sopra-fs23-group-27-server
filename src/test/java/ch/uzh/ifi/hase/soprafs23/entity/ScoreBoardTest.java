package ch.uzh.ifi.hase.soprafs23.entity;

import static org.mockito.Mockito.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class ScoreBoardTest {

    ScoreBoard scoreBoard;

    @BeforeEach
    public void setUp() {
        
        // load scoreboard with players
        ArrayList<String> playerNames = new ArrayList<String>();
        playerNames.add("Player1");
        playerNames.add("Player2");
        playerNames.add("Player3");
        playerNames.add("Player4");

        this.scoreBoard = new ScoreBoard(playerNames);
    }

    @Test
    public void testCorrectSetCurrentCorrectGuessPerPlayer(){
            
            // set current correct guess for player 1
            this.scoreBoard.setCurrentCorrectGuessPerPlayer("Player1", true);
    
            // check if current correct guess for player 1 is true
            assertTrue(this.scoreBoard.getCurrentCorrectGuessPerPlayer("Player1"));

    }

    @Test
    public void testWrongSetCurrentGuessPerPlayer(){
                
            // why does this return false? --> because we use getOrDefault and the default value is false
            assertFalse(this.scoreBoard.getCurrentCorrectGuessPerPlayer("Player1"));
            
    }

    @Test
    public void testCorrectSetCurrentTimeUntilCorrectGuessPerPlayer(){

        Integer passedTime = this.timer(); 

        System.out.println(passedTime);
        
        // set current time until correct guess for player 1
        this.scoreBoard.setCurrentTimeUntilCorrectGuessPerPlayer("Player1", passedTime);
    
        // check if current time until correct guess for player 1 is 10
        assertEquals(passedTime, this.scoreBoard.getCurrentTimeUntilCorrectGuessPerPlayer("Player1"));

    }

    @Test
    public void testWrongSetCurrentTimeUntilCorrectGuessPerPlayer(){

        // why does this return null? --> because we use getOrDefault and the default value is null
        assertEquals(0,this.scoreBoard.getCurrentTimeUntilCorrectGuessPerPlayer("Player1"));
    }

    @Test
    public void testCorrectSetCurrentNumberOfWrongGuessesPerPlayer(){
                    
        // set current number of wrong guesses for player 1
        this.scoreBoard.setCurrentNumberOfWrongGuessesPerPlayer("Player1", 10);
        
        // check if current number of wrong guesses for player 1 is 10
        assertEquals(10, this.scoreBoard.getCurrentNumberOfWrongGuessesPerPlayer("Player1"));
    
    }

    @Test
    public void testWrongSetCurrentNumberOfWrongGuessesPerPlayer(){
            
            // why does this return null? --> because we use getOrDefault and the default value is null
            assertEquals(0, this.scoreBoard.getCurrentNumberOfWrongGuessesPerPlayer("Player1"));
    }

    @Test
    public void testCorrectGetTotalCorrectGuessesPerPlayer(){

        // set current correct guess for player 1
        this.scoreBoard.setCurrentCorrectGuessPerPlayer("Player1", true);

        // we must first update the total scores
        this.scoreBoard.updateTotalScores();
        
        // then we can check if the total correct guesses for player 1 is 1
        assertEquals(1, this.scoreBoard.getTotalCorrectGuessesPerPlayer("Player1"));
    }

    @Test
    public void testMultipleGetTotalCorrectGuessesPerPlayer(){
            
        // set current correct guess for player 1
        this.scoreBoard.setCurrentCorrectGuessPerPlayer("Player1", true);

        // we must first update the total scores
        this.scoreBoard.updateTotalScores();

        // set current correct guess for player 1 again
        this.scoreBoard.setCurrentCorrectGuessPerPlayer("Player1", true);
    
        // update total scores again
        this.scoreBoard.updateTotalScores();   
            
        assertEquals(2, this.scoreBoard.getTotalCorrectGuessesPerPlayer("Player1"));

    }

    @Test
    public void testWrongTotalAttributesAreInitialized(){
            
        // the Total attributes are initialized with 0 for all players in the playerNames list.
        assertEquals(0, this.scoreBoard.getTotalCorrectGuessesPerPlayer("Player1"));
        assertEquals(0, this.scoreBoard.getTotalCorrectGuessesInARowPerPlayer("Player1"));
        assertEquals(0, this.scoreBoard.getTotalNumberOfWrongGuessesPerPlayer("Player1"));
        assertEquals(0, this.scoreBoard.getTotalTimeUntilCorrectGuessPerPlayer("Player1"));
        assertEquals(0, this.scoreBoard.getLeaderBoardTotalScorePerPlayer("Player1"));
    }

    @Test
    public void testCorrectTotalCorrectGuessesInARowPerPlayer(){

        // check if the mechanism for the updating of the total correct guesses in a row works for player 1 
        // and for one correct guess
        this.scoreBoard.setCurrentCorrectGuessPerPlayer("Player1", true);

        this.scoreBoard.updateTotalScores();

        assertEquals(1, this.scoreBoard.getTotalCorrectGuessesInARowPerPlayer("Player1"));
        
        // check if the the correct guesses in a row also works for two consecutive correct guesses in a row.
        this.scoreBoard.setCurrentCorrectGuessPerPlayer("Player1", true);

        this.scoreBoard.updateTotalScores();

        assertEquals(2, this.scoreBoard.getTotalCorrectGuessesInARowPerPlayer("Player1"));
    }

    @Test
    public void testTotalCorrectGuessesInARowPerPlayerFallBackToZero(){

        // check if the mechanism for the updating of the total correct guesses in a row works for player 1 
        // and for one correct guess
        this.scoreBoard.setCurrentCorrectGuessPerPlayer("Player1", true);

        this.scoreBoard.updateTotalScores();

        assertEquals(1, this.scoreBoard.getTotalCorrectGuessesInARowPerPlayer("Player1"));

        this.scoreBoard.resetAllCurrentScores();
        
        // check if the the correct guesses in a row also works for two consecutive correct guesses in a row.
        this.scoreBoard.setCurrentCorrectGuessPerPlayer("Player1", false);

        this.scoreBoard.updateTotalScores();

        assertEquals(0, this.scoreBoard.getTotalCorrectGuessesInARowPerPlayer("Player1"));

    }

    @Test
    public void testGetTotalTimeUntilCorrectGuessPerPlayer(){

        Integer a = this.timer();
        Integer b = this.timer();

        // set current time until correct guess for player 1
        this.scoreBoard.setCurrentTimeUntilCorrectGuessPerPlayer("Player1", a);

        this.scoreBoard.updateTotalScores();

        assertEquals(a, this.scoreBoard.getTotalTimeUntilCorrectGuessPerPlayer("Player1"));

        this.scoreBoard.setCurrentTimeUntilCorrectGuessPerPlayer("Player1", b);

        this.scoreBoard.updateTotalScores();

        assertEquals(a+b, this.scoreBoard.getTotalTimeUntilCorrectGuessPerPlayer("Player1"));
    }

    @Test
    public void testCorrectGetTotalNumberOfWrongGuessesPerPlayer(){

        int firstRound = 10;
        int secondRound = 11;

        // In round 1 the player guessed 10 times the wrong country, this is written into the attribute currentNumberOfWrongGuesses
        this.scoreBoard.setCurrentNumberOfWrongGuessesPerPlayer("Player1", firstRound);

        this.scoreBoard.updateTotalScores();

        assertEquals(10, this.scoreBoard.getTotalNumberOfWrongGuessesPerPlayer("Player1"));

        // In round 2 the player guessed 11 times the wrong country, this is written into the attribute currentNumberOfWrongGuesses again
        this.scoreBoard.setCurrentNumberOfWrongGuessesPerPlayer("Player1", secondRound);

        this.scoreBoard.updateTotalScores();

        // notice, only the current number of wrong guesses is added is modified with a concrete setter
        // e.g. a player guesses wrong --> setCurrentNumberOfWrongGuessesPerPlayer("Player1", 1)
        // the player guesses wrong again --> setCurrentNumberOfWrongGuessesPerPlayer("Player1", 2)
        // but this logic doesn't hold for the total number of wrong guesses
        // here the current scores get added to the total scores e.g. 
        assertEquals(firstRound + secondRound, this.scoreBoard.getTotalNumberOfWrongGuessesPerPlayer("Player1"));
    }

    @Test
    public void testResetAllCurrentScores(){

        Integer passedTime = this.timer();

        // set the current attributes for Player1
        this.scoreBoard.setCurrentCorrectGuessPerPlayer("Player1", null);

        this.scoreBoard.setCurrentTimeUntilCorrectGuessPerPlayer("Player1", passedTime);

        this.scoreBoard.setCurrentNumberOfWrongGuessesPerPlayer("Player1", 10);

        // reset the scores
        this.scoreBoard.resetAllCurrentScores();

        // check if the current attributes are null again for Player1
        assertFalse(this.scoreBoard.getCurrentCorrectGuessPerPlayer("Player1"));
        assertEquals(0, this.scoreBoard.getCurrentNumberOfWrongGuessesPerPlayer("Player1"));
        assertEquals(0, this.scoreBoard.getCurrentTimeUntilCorrectGuessPerPlayer("Player1"));
    }

    @Test
    public void testResetAllTotalScores(){

        // set the current attributes for Player1
        this.scoreBoard.setCurrentCorrectGuessPerPlayer("Player1", true);

        this.scoreBoard.setCurrentTimeUntilCorrectGuessPerPlayer("Player1", 10);

        this.scoreBoard.setCurrentNumberOfWrongGuessesPerPlayer("Player1", 10);

        // set total attributes for Player1
        this.scoreBoard.updateTotalScores();

        assertEquals(1, this.scoreBoard.getTotalCorrectGuessesPerPlayer("Player1"));
        assertEquals(10, this.scoreBoard.getTotalTimeUntilCorrectGuessPerPlayer("Player1"));
        assertEquals(10, this.scoreBoard.getTotalNumberOfWrongGuessesPerPlayer("Player1"));

        this.scoreBoard.resetAllTotalScores();

        assertEquals(0, this.scoreBoard.getTotalCorrectGuessesPerPlayer("Player1"));
        assertEquals(0, this.scoreBoard.getTotalTimeUntilCorrectGuessPerPlayer("Player1"));
        assertEquals(0, this.scoreBoard.getTotalNumberOfWrongGuessesPerPlayer("Player1"));

    }

    @Test
    public void testComputeLeaderBoardScore(){

        // FIRST ROUND:

        // Note that we have to set the current scores for ALL players, before executing the
        // computeLeaderBoardScore() method. Otherwise a null pointer exception will be thrown.
        this.scoreBoard.setCurrentCorrectGuessPerPlayer("Player1", true);
        this.scoreBoard.setCurrentCorrectGuessPerPlayer("Player2", false);
        this.scoreBoard.setCurrentCorrectGuessPerPlayer("Player3", false);
        this.scoreBoard.setCurrentCorrectGuessPerPlayer("Player4", false);

        this.scoreBoard.setCurrentTimeUntilCorrectGuessPerPlayer("Player1", 10);
        this.scoreBoard.setCurrentTimeUntilCorrectGuessPerPlayer("Player2", 30);

        this.scoreBoard.setCurrentNumberOfWrongGuessesPerPlayer("Player1", 10);
        this.scoreBoard.setCurrentNumberOfWrongGuessesPerPlayer("Player2", 20);

        // update the total scores
        this.scoreBoard.updateTotalScores();

        // update the leader board scores (current and total)
        this.scoreBoard.computeLeaderBoardScore();

        // after the first round, the scores for current and total are equivalent
        assertEquals(18, this.scoreBoard.getLeaderBoardTotalScorePerPlayer("Player1"));
        assertEquals(0, this.scoreBoard.getLeaderBoardTotalScorePerPlayer("Player2"));
        assertEquals(18, this.scoreBoard.getCurrentScorePerPlayer("Player1"));
        assertEquals(0, this.scoreBoard.getCurrentScorePerPlayer("Player2"));

        // SECOND ROUND:

        // reset the current scores
        this.scoreBoard.resetAllCurrentScores();

        // set the current scores for the second round
        this.scoreBoard.setCurrentCorrectGuessPerPlayer("Player1", true);
        this.scoreBoard.setCurrentCorrectGuessPerPlayer("Player2", false);
        this.scoreBoard.setCurrentCorrectGuessPerPlayer("Player3", false);
        this.scoreBoard.setCurrentCorrectGuessPerPlayer("Player4", false);

        this.scoreBoard.setCurrentTimeUntilCorrectGuessPerPlayer("Player1", 10);
        this.scoreBoard.setCurrentTimeUntilCorrectGuessPerPlayer("Player2", 30);

        this.scoreBoard.setCurrentNumberOfWrongGuessesPerPlayer("Player1", 10);
        this.scoreBoard.setCurrentNumberOfWrongGuessesPerPlayer("Player2", 20);

        this.scoreBoard.updateTotalScores();

        this.scoreBoard.computeLeaderBoardScore();

        // now the total score must be equal to the previous total score + the current score
        // notice that the current score for the player is more then the previous current score
        // this is because the player has had two correct guesses in a row
        assertEquals(46, this.scoreBoard.getLeaderBoardTotalScorePerPlayer("Player1"));
        assertEquals(0, this.scoreBoard.getLeaderBoardTotalScorePerPlayer("Player2"));
        assertEquals(28, this.scoreBoard.getCurrentScorePerPlayer("Player1"));
        assertEquals(0, this.scoreBoard.getCurrentScorePerPlayer("Player2"));
    }

    @Test
    public void testComputeLeaderBoardScore_negativeScore(){
        // Note that we have to set the current scores for ALL players, before executing the
        // computeLeaderBoardScore() method. Otherwise a null pointer exception will be thrown.
        this.scoreBoard.setCurrentCorrectGuessPerPlayer("Player1", true);
        this.scoreBoard.setCurrentCorrectGuessPerPlayer("Player2", false);
        this.scoreBoard.setCurrentCorrectGuessPerPlayer("Player3", false);
        this.scoreBoard.setCurrentCorrectGuessPerPlayer("Player4", false);

        this.scoreBoard.setCurrentTimeUntilCorrectGuessPerPlayer("Player1", 200);

        this.scoreBoard.setCurrentNumberOfWrongGuessesPerPlayer("Player1", 50);

        // update the total scores
        this.scoreBoard.updateTotalScores();

        // update the leader board scores (current and total)
        this.scoreBoard.computeLeaderBoardScore();

        // after the first round, the scores for current and total are equivalent
        assertEquals(0, this.scoreBoard.getLeaderBoardTotalScorePerPlayer("Player1"));
        assertEquals(0, this.scoreBoard.getCurrentScorePerPlayer("Player1"));
    }

    @Test
    public void testComputeLeaderBoardScore_catchArithmeticException(){
        // FIRST ROUND:

        // Note that we have to set the current scores for ALL players, before executing the
        // computeLeaderBoardScore() method. Otherwise a null pointer exception will be thrown.
        this.scoreBoard.setCurrentCorrectGuessPerPlayer("Player1", true);
        this.scoreBoard.setCurrentCorrectGuessPerPlayer("Player2", true);
        this.scoreBoard.setCurrentCorrectGuessPerPlayer("Player3", false);
        this.scoreBoard.setCurrentCorrectGuessPerPlayer("Player4", false);

        this.scoreBoard.setCurrentTimeUntilCorrectGuessPerPlayer("Player1", 0);
        this.scoreBoard.setCurrentTimeUntilCorrectGuessPerPlayer("Player2", 2);

        this.scoreBoard.setCurrentNumberOfWrongGuessesPerPlayer("Player1", 0);
        this.scoreBoard.setCurrentNumberOfWrongGuessesPerPlayer("Player2", 0);

        // update the total scores
        this.scoreBoard.updateTotalScores();

        // update the leader board scores (current and total)
        this.scoreBoard.computeLeaderBoardScore();

        // after the first round, the scores for current and total are equivalent
        assertEquals(110, this.scoreBoard.getLeaderBoardTotalScorePerPlayer("Player1"));
        assertEquals(60, this.scoreBoard.getLeaderBoardTotalScorePerPlayer("Player2"));
        assertEquals(110, this.scoreBoard.getCurrentScorePerPlayer("Player1"));
        assertEquals(60, this.scoreBoard.getCurrentScorePerPlayer("Player2"));

        // SECOND ROUND:

        // reset the current scores
        this.scoreBoard.resetAllCurrentScores();

        // set the current scores for the second round
        this.scoreBoard.setCurrentCorrectGuessPerPlayer("Player1", true);
        this.scoreBoard.setCurrentCorrectGuessPerPlayer("Player2", true);
        this.scoreBoard.setCurrentCorrectGuessPerPlayer("Player3", false);
        this.scoreBoard.setCurrentCorrectGuessPerPlayer("Player4", false);

        this.scoreBoard.setCurrentTimeUntilCorrectGuessPerPlayer("Player1", 2);
        this.scoreBoard.setCurrentTimeUntilCorrectGuessPerPlayer("Player2", 0);

        this.scoreBoard.setCurrentNumberOfWrongGuessesPerPlayer("Player1", 0);
        this.scoreBoard.setCurrentNumberOfWrongGuessesPerPlayer("Player2", 0);

        this.scoreBoard.updateTotalScores();

        this.scoreBoard.computeLeaderBoardScore();

        // now the total score must be equal to the previous total score + the current score
        // notice that the current score for the player is more then the previous current score
        // this is because the player has had two correct guesses in a row
        assertEquals(180, this.scoreBoard.getLeaderBoardTotalScorePerPlayer("Player1"));
        assertEquals(180, this.scoreBoard.getLeaderBoardTotalScorePerPlayer("Player2"));
        assertEquals(70, this.scoreBoard.getCurrentScorePerPlayer("Player1"));
        assertEquals(120, this.scoreBoard.getCurrentScorePerPlayer("Player2"));
    }

    private Integer timer(){

        Long startTime = System.currentTimeMillis();

        // system timeout for 1 second
        while(System.currentTimeMillis() - startTime < 1000);

        Long endTime = System.currentTimeMillis();

        Long passedTimeLong = endTime - startTime;

        Integer passedTime = passedTimeLong.intValue(); 

        passedTime = passedTime / 1000;

        return passedTime;
    }
    
}
