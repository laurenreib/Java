package src;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * graphical representation of a board square and a playable tile
 */
public class Tile extends Pane {
    private StackPane stp;
    protected BoardSquare boardSq;
    private Rectangle background;
    private Color backColor;
    private Label label;
    private char dispChar;
    private boolean moveable;
    protected boolean move;
    protected boolean place;
    Tile(double size, BoardSquare boardSq, boolean moveable){
        this.boardSq = boardSq;
        this.moveable = moveable;
        this.place = true;
        stp = new StackPane();
        move = false;
        dispChar = ' ';
        background = new Rectangle(size,size,backColor);
        background.setStroke(Color.BLACK);
        background.setStrokeWidth(1);
        label = new Label();
        setLabel();
        stp.getChildren().add(background);
        stp.getChildren().add(label);
        getChildren().add(stp);
        //when clicked makes a flag to move this tile
        setOnMouseClicked(event -> {
            if(moveable){
                move = true;
            }
        });
    }

    /**
     * sets the color and display character of the tile
     */
    protected void setLabel(){
        backColor = Color.WHITE;
        if(boardSq.getLettMult() == 2){
            backColor = Color.LIGHTBLUE;
            dispChar = '2';
        } else if (boardSq.getLettMult() == 3){
            backColor = Color.BLUE;
            dispChar = '3';
        }
        if(boardSq.getWordMult() == 2){
            backColor = Color.PINK;
            dispChar = '2';
        } else if (boardSq.getWordMult() == 3){
            backColor = Color.RED;
            dispChar = '3';
        }
        if(boardSq.getTempLetter() != '#'){
            backColor = Color.SANDYBROWN;
            dispChar = boardSq.getTempLetter();
        }
        label.setText(String.valueOf(dispChar));
        background.setFill(backColor);
    }

    /**
     * changes the value of the tile with a input char
     * @param c input char
     */
    protected void setBoardSq(char c){
        boardSq.setTempLetter(c);
        dispChar = ' ';
        if(c != '#') dispChar = c;
        background.setFill(Color.WHITE);
        setLabel();
    }
}
