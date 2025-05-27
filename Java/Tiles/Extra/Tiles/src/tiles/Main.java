package tiles;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Collections;
import java.util.Stack;

public class Main extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        int gpRows = 5;
        int gpCols = 6;

        Board playField = new Board(gpRows, gpCols, 100);
        initBoard(gpRows, gpCols, playField);
        Gui root = new Gui(playField);
        Scene scene = new Scene(root);
        scene.setOnMouseClicked(t -> {
            root.updateScore(playField.runGameLogic());
            if (playField.checkWin()) root.win();
        });
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    /**
     * Initializes the Board
     * Has three stacks of patterns that get rows*cols number of patterns
     * Each time a pattern is added a duplicate is also added so that each pattern
     * is guaranteed to have a match
     * The patterns are chosen randomly but are the same order in each stack
     * The stacks are shuffled so that they are not the in the same order
     * Then each tile is initiated with all 3 patterns
     * @param rows
     * @param cols
     * @param playField
     */
    private void initBoard(int rows, int cols, Board playField){
        Stack<String> PatBag1 = new Stack<>();
        Stack<String> PatBag2 = new Stack<>();
        Stack<String> PatBag3 = new Stack<>();
        for(int i = 0; i < (rows*cols/2); i++){
            int randPatNum = (int)(Math.floor(Math.random()*6)+1);
            String patternName = "P" + randPatNum;
            PatBag1.add(patternName);
            PatBag1.add(patternName);
            PatBag2.add(patternName);
            PatBag2.add(patternName);
            PatBag3.add(patternName);
            PatBag3.add(patternName);
        }

        Collections.shuffle(PatBag1);
        Collections.shuffle(PatBag2);
        Collections.shuffle(PatBag3);

        for(int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                playField.setTile(i,j,Layer1.valueOf(PatBag1.pop()),1);
                playField.setTile(i,j,Layer2.valueOf(PatBag2.pop()),2);
                playField.setTile(i,j,Layer3.valueOf(PatBag3.pop()),3);
            }
        }
    }


}