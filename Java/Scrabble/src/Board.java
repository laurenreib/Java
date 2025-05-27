package src;
import java.util.Scanner;

/**
 * Board object used to store information about a move and the boardState
 * board is the board state
 * tray is the letters that were removed from the tray
 * bestMove is the word created by the best move
 */
public class Board{
    private BoardSquare[][] board;
    private String tray;
    private String bestMove;
    private int score;
    protected int boardSize;
    Board(Scanner scan) {
        board = initBoard(scan);
        boardSize = board.length;
    }

    protected BoardSquare[][] getBoard(){
        return board;
    }
    protected String getTray(){
        return tray;
    }
    protected void setTray(String tray){
        this.tray = tray;
    }

    protected String getBestMove(){
        return bestMove;
    }
    protected void setBestMove(String bestMove){
        this.bestMove = bestMove;
    }

    /**
     * Initializes a boardState using the scanner
     * @param scan scanner
     * @return the board state
     */
    private BoardSquare[][] initBoard(Scanner scan){
        int boardSize = Integer.parseInt(scan.next());
        BoardSquare[][] board = new BoardSquare[boardSize][boardSize];
        scan.useDelimiter("[\n][ ]*|[ ]+");
        int i = 0;
        int j = 0;
        while(scan.hasNext()){
            String squareStr = scan.next();
            BoardSquare bs;
            //square with multipliers
            if(squareStr.length() == 2){
                bs = new BoardSquare(squareStr.charAt(0),squareStr.charAt(1));
                board[i][j] = bs;
                j++;
            }
            //squares with a letter
            else if(squareStr.length() == 1) {
                bs = new BoardSquare(squareStr.charAt(0));
                board[i][j] = bs;
                j++;
            }
            else if(squareStr.length() > 2){
                tray = squareStr;
                return board;
            }
            if(j == boardSize){
                j = 0;
                i++;
            }
        }
        return board;
    }

    /**
     * Takes all the temporary placements and resets them to the true board state
     */
    protected void resetBoard(){
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                board[i][j].setTempLetter(board[i][j].getLetter());
                board[i][j].isNew  = false;
            }
        }
    }
    protected void setScore(int score){
        this.score = score;
    }

    protected int getScore() {
        return score;
    }
}
