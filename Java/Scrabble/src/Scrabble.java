/**
 * @author Lauren Bustamante
 * CS351
 * Scrabble
  */


package src;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Scrabble extends Application {
    public static void main(String[] args) {
        launch();
    }
    private Tile selected;
    private Board board;
    private int compScore;
    private int playerScore;
    private boolean firstMove;
    private String compTray = "";
    @Override
    public void start(Stage primaryStage) throws Exception {
        int rows = 15;
        int cols = 15;
        int size = 40;
        playerScore = 0;
        compScore = 0;
        firstMove = true;

        Tile[][] tileArr = new Tile[rows][cols];

        Button checkMove = new Button("Submit Move");
        Button shuffleTray = new Button("Switch Out Tiles");
        Label compScoreLbl = new Label("Computer Score: 0");
        Label playerScoreLbl = new Label("Player Score: 0");
        TextField blankFld = new TextField("Enter Blank's Letter");
        blankFld.setOpacity(0);

        Stack<BoardSquare> bag = setBag();
        Tray playerTray = new Tray(fillTray(bag,size,0));

        GridPane gp = new GridPane();
        GridPane.setRowSpan(gp, rows);
        GridPane.setColumnSpan(gp, cols);

        HBox toolBar = new HBox();
        toolBar.getChildren().addAll(checkMove,playerTray,shuffleTray);
        toolBar.setAlignment(Pos.CENTER);
        toolBar.setSpacing(20);
        HBox scoreBar = new HBox();
        scoreBar.getChildren().addAll(compScoreLbl,blankFld, playerScoreLbl);
        scoreBar.setAlignment(Pos.CENTER);
        scoreBar.setSpacing(35);

        VBox mainVbox = new VBox();
        StackPane stp = new StackPane(mainVbox);
        mainVbox.getChildren().addAll(gp,scoreBar,toolBar);
        mainVbox.setSpacing(10);

        Tree tree = new Tree(new File("resources/sowpods.txt"));
        File file = new File("resources/scrabble_board.txt");
        board = new Board(new Scanner(file));

        for (int i = 0; i < 7; i++){
            compTray += bag.pop().getLetter();
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Tile t = new Tile(size,board.getBoard()[i][j],false);
                tileArr[i][j] = t;
                gp.add(t, i, j);
            }
        }
        Scene scene = new Scene(stp);

        /**
         * Logic for click and drop to put a tile on the board or back into the tray
         */
        scene.setOnMouseClicked(event -> {
            //pick up a tile
            if (selected == null) {
                for (Tile t : playerTray.tiles) {
                    if (t.move) {
                        selected = t;
                        if(t.boardSq.getLetter() == '*' || Character.isUpperCase(t.boardSq.getLetter())){
                            blankFld.setOpacity(1);
                            blankFld.requestFocus();
                        }
                        stp.getChildren().add(selected);
                        selected.setTranslateX(event.getX()-size/2);
                        selected.setTranslateY(event.getY()-size/2);
                    }
                }
                if(selected != null){
                    playerTray.remove(selected);
                }
            }
            //place a Tile
            else {
                //back to tray
                if(event.getY() > (cols+1)*(size+1)) {
                    playerTray.add(selected);
                    selected.move = false;
                    selected.setTranslateX(0);
                    selected.setTranslateY(0);
                    selected = null;
                }
                //onto board
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        if(i*(size+1) < event.getX() && (i*(size+1))+size+1 > event.getX()){
                            if(j*(size+1) < event.getY() && (j*(size+1))+size+1 > event.getY()){
                                if(tileArr[i][j].place) {
                                    board.getBoard()[i][j].setTempLetter(selected.boardSq.getTempLetter());
                                    board.getBoard()[i][j].isNew = true;
                                    tileArr[i][j].place = false;
                                    tileArr[i][j].setBoardSq(selected.boardSq.getTempLetter());
                                    stp.getChildren().remove(selected);
                                    selected.move = false;
                                    selected = null;
                                }
                            }
                        }
                    }
                }
            }
        });

        /**
         * Logic to pin the selected tile to the mouse
         */
        scene.setOnMouseMoved(event -> {
            if(selected != null){
                selected.setTranslateX(event.getX()-size/2);
                selected.setTranslateY(event.getY()-size/2);
            }
        });

        /**
         * Logic to submit a move
         * Uses the solver to determine if a move is valid and uses the solver score methods
         * If move is invalid will reset tray
         */
        checkMove.setOnMouseClicked(event -> {
            if(Solver.checkBoard(board.getBoard(), tree) && checkPlayerPlacement(tileArr)){
                playerScore += Solver.scoreMove(board.getBoard(), playerTray.tiles.size() == 0);
                playerScoreLbl.setText("Player Score: " + playerScore);
                board.resetBoard();
                playerTray.validMove(fillTray(bag,size,playerTray.tiles.size()));
                firstMove = false;
                compLogic(tree, bag);
                compScoreLbl.setText("Computer Score: "+ compScore);
            }else {
                playerTray.invalidMove();
            }
            updateTiles(tileArr);
        });

        /**
         * Allows player to input the letter for blank tiles
         */
        blankFld.setOnKeyReleased(event -> {
            if(selected != null){
                if(selected.boardSq.getLetter() == '*'){
                    if(event.getText().length() > 0) {
                        char c = event.getText().charAt(0);
                        if(c >= 65 && c <= 90 || c >= 97 && c <= 123){
                            selected.setBoardSq(Character.toUpperCase(event.getText().charAt(0)));
                            blankFld.setOpacity(0);
                        }
                    }
                }
                blankFld.setText("Enter Blank's Letter");
                blankFld.requestFocus();
            }
        });

        /**
         * shuffles the tray
         */
        shuffleTray.setOnMouseClicked(event -> {
            if(firstMove) return;
            Collections.shuffle(bag);
            bag.addAll(playerTray.shuffle(fillTray(bag, size,0)));
            compLogic(tree, bag);
            updateTiles(tileArr);
            compScoreLbl.setText("Computer Score: "+ compScore);
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Creates all the playable letters using a file for the frequency of each letter
     * @return a stack of boardSquares
     * @throws FileNotFoundException
     */
    private Stack<BoardSquare> setBag() throws FileNotFoundException {
        Stack<BoardSquare> bag = new Stack<>();
        File file = new File("resources/scrabble_tiles.txt");
        Scanner scan = new Scanner(file);
        while(scan.hasNext()){
            String c = scan.next();
            int frequency = scan.nextInt();
            for (int i = 0; i < frequency; i++) {
                bag.add(new BoardSquare(c.charAt(0)));
            }
        }
        Collections.shuffle(bag);
        return bag;
    }

    /**
     * updates the visual portion board when the computer moves or the player makes an invalid move
     * @param tileArr the visual portion of the board
     */
    private void updateTiles(Tile[][] tileArr){
        for (int i = 0; i < board.getBoard().length; i++) {
            for (int j = 0; j < board.getBoard().length; j++) {
                tileArr[i][j].boardSq.isNew = false;
                if(tileArr[i][j].boardSq.getTempLetter() != board.getBoard()[i][j].getLetter()){
                    if(tileArr[i][j].boardSq.getTempLetter() != '#'){
                        tileArr[i][j].place = true;
                    }
                    else tileArr[i][j].place = false;
                }
                tileArr[i][j].setBoardSq(board.getBoard()[i][j].getLetter());
            }
        }
    }

    /**
     * Fill the players tray up to 7 tiles
     * @param bag the bag of playable letters
     * @param size the size of a Tile
     * @param traySize the current size of the tray
     * @return an array list of Tiles to add to the tray
     */
    private ArrayList<Tile> fillTray(Stack<BoardSquare> bag, int size, int traySize){
        ArrayList<Tile> tiles = new ArrayList<>();
        for (int i = traySize; i < 7; i++) {
            if(bag.isEmpty())break;
            Tile t = new Tile(size,bag.pop(),true);
            tiles.add(t);
        }
        return tiles;
    }

    /**
     * Computer Logic: updates the board with the output from the solver and fills the computer tray with new letters
     * @param tree the dictionary
     * @param bag the bag of playable letter
     */
    private void compLogic(Tree tree, Stack<BoardSquare> bag){
        board = Solver.solveNew(compTray,board,tree);
        String compUsedChars = board.getTray();
        StringBuilder strb = new StringBuilder(compTray);
        for (int i = 0; i < compUsedChars.length(); i++) {
            strb.deleteCharAt(strb.indexOf(String.valueOf(compUsedChars.charAt(i))));
        }
        compTray = strb.toString();
        for (int i = compTray.length(); i < 7; i++) {
            compTray += bag.pop().getLetter();
        }
        compScore += board.getScore();
    }

    /**
     * Checks the players tile placements
     * @param tileArr the tile representation of the board
     * @return true if the player made a valid move
     */
    private boolean checkPlayerPlacement(Tile[][] tileArr){
        int mid = (int)Math.floor(tileArr.length/2);
        if(tileArr[mid][mid].boardSq.getTempLetter() == '#') return false;
        ArrayList<Anchor> anchors = new ArrayList<>();
        for (int i = 0; i < tileArr.length; i++) {
            for (int j = 0; j < tileArr.length; j++) {
                if(tileArr[i][j].boardSq.isNew) anchors.add(new Anchor(i,j));
            }
        }
        boolean validPlacement = true;
        int i = anchors.get(0).i;
        for (Anchor anchor : anchors) {
            if (anchor.i != i) {
                validPlacement = false;
                break;
            }
        }
        if(validPlacement) return true;

        validPlacement = true;
        int j = anchors.get(0).j;
        for (Anchor anchor : anchors) {
            if (anchor.j != j) {
                validPlacement = false;
                break;
            }
        }
        return validPlacement;
    }
}
