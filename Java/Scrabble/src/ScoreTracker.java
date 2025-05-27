package src;

import java.util.ArrayList;

public class ScoreTracker {
    private final ArrayList<Integer> moveScores;
    private int highestScore;
    private String bestMove;

    public ScoreTracker() {
        this.moveScores = new ArrayList<>();
        this.highestScore = 0;
        this.bestMove = "";
    }

    /**
     * Adds a score for a move and updates the highest score if applicable.
     * @param move The word played
     * @param score The score of the move
     */
    public void addScore(String move, int score) {
        moveScores.add(score);
        if (score > highestScore) {
            highestScore = score;
            bestMove = move;
        }
    }

    /**
     * Returns the highest score achieved so far.
     */
    public int getHighestScore() {
        return highestScore;
    }

    /**
     * Returns the best move (the one with the highest score).
     */
    public String getBestMove() {
        return bestMove;
    }

    /**
     * Returns all recorded scores for moves.
     */
    public ArrayList<Integer> getAllScores() {
        return new ArrayList<>(moveScores);
    }

    /**
     * Clears all stored scores.
     */
    public void reset() {
        moveScores.clear();
        highestScore = 0;
        bestMove = "";
    }

    /**
     * Test the functionality of ScoreTracker
     */
    public static void main(String[] args) {
        // Create an instance of ScoreTracker
        ScoreTracker scoreTracker = new ScoreTracker();

        // Test adding some scores
        scoreTracker.addScore("Move1", 10);
        scoreTracker.addScore("Move2", 15);
        scoreTracker.addScore("Move3", 8);

        // Output the highest score and best move
        System.out.println("Highest Score: " + scoreTracker.getHighestScore());  // Expected: 15
        System.out.println("Best Move: " + scoreTracker.getBestMove());  // Expected: Move2

        // Output all recorded scores
        System.out.println("All Scores: " + scoreTracker.getAllScores());  // Expected: [10, 15, 8]

        // Reset the tracker and check if it works
        scoreTracker.reset();
        System.out.println("After Reset:");
        System.out.println("Highest Score: " + scoreTracker.getHighestScore());  // Expected: 0
        System.out.println("Best Move: " + scoreTracker.getBestMove());  // Expected: ""
    }
}
