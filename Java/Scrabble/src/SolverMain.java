package src;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class SolverMain {
    public static void main(String[] args) throws IOException {
        Tree tree = new Tree(new File(args[0]));
        Scanner scan = new Scanner(System.in);
        while(scan.hasNext()) {
            Board board = new Board(scan);
            String tray = board.getTray();

            System.out.print("Input Board:\n");
            writeBoard(board);
            System.out.print("Tray: " + tray + "\n");

            Solver.solveNew(tray,board,tree);

            System.out.print("Solution " + board.getBestMove() + " has " + board.getScore() + " points\n");
            System.out.print("Solution Board:\n");
            writeBoard(board);
            System.out.print("\n");
        }
    }

    /**
     * Writes the board to the output file
     * @param board the board
     */
    private static void writeBoard(Board board){
        for (int i = 0; i < board.boardSize; i++) {
            for (int j = 0; j < board.boardSize; j++) {
                if(board.getBoard()[i][j].getLetter() == '#'){
                    if(board.getBoard()[i][j].getLettMult() != 1){
                        System.out.print("." + board.getBoard()[i][j].getLettMult());
                    }
                    else if(board.getBoard()[i][j].getWordMult() == 1){
                        System.out.print("..");
                    }
                    if(board.getBoard()[i][j].getWordMult() != 1){
                        System.out.print(board.getBoard()[i][j].getWordMult() + ".");
                    }
                    else  if(board.getBoard()[i][j].getLettMult() == 0){
                        System.out.print(".. ");
                    }
                } else {
                    System.out.print(" " + board.getBoard()[i][j].getLetter());
                }
                if(j< board.boardSize-1) System.out.print(" ");
            }
            System.out.print("\n");
        }
    }
}