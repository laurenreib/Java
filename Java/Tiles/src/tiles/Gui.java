package tiles;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

/**
 * The display portion of the program
 *
 */

public class Gui extends Pane {
    private static int currScore;
    private static int highScore;
    private Label currScoreLbl;
    private Label highScoreLbl;

    Gui(Board playField){
        currScore = 0;
        highScore = 0;
        HBox mainHbox = new HBox();
        VBox scoreVbox = new VBox();

        Rectangle spacingRect = new Rectangle(140,10, Color.TRANSPARENT);
        Rectangle spacingRect2 = new Rectangle(140,10, Color.TRANSPARENT);

        currScoreLbl = new Label("Score: 0");
        highScoreLbl = new Label("  High \nScore: 0");
        Font font = new Font(30);
        currScoreLbl.setFont(font);
        highScoreLbl.setFont(font);
        scoreVbox.getChildren().addAll(spacingRect,currScoreLbl,highScoreLbl);
        scoreVbox.setAlignment(Pos.CENTER);
        scoreVbox.setSpacing(30);


        mainHbox.getChildren().addAll(spacingRect2,playField,scoreVbox);
        mainHbox.setBackground(new Background(new BackgroundFill(Color.LIGHTGREY
                ,null,null)));
        getChildren().add(mainHbox);

    }

    /**
     * updates the scores and score labels
     * @param x increments score if (1), resets the score if (-1), does nothing if (0)
     */

    protected void updateScore(int x){
        if(x == 1){
            currScore++;
            if(currScore > highScore){
                highScore = currScore;
            }
        } else if(x == -1) {
            currScore = 0;
        }
        currScoreLbl.setText("Score: " + currScore);
        highScoreLbl.setText("  High \nScore: " + highScore);
    }

    protected void win(){ currScoreLbl.setText("You Win!!!");}
}
