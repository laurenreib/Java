package Dominos;

import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

/**
 * Display version of a domino
 * 2 rectangles with images in an HBox,
 * the HBox and a background in a StackPane,
 * and the StackPane in a Pane
 */
public class DispDom extends Pane {
    private final Domino domino;
    private boolean selected;

    DispDom(Domino domino, boolean rotatable){
        this.domino = domino;
        StackPane stackPane = new StackPane();
        Rectangle background = new Rectangle(72,35, Color.WHITE);
        background.setArcHeight(10);
        background.setArcWidth(10);
        background.setStroke(Color.BLACK);
        Rectangle numContainer1 = new Rectangle(35,35,getImage(String.valueOf(domino.getValues()[0])));
        Rectangle middleDivider = new Rectangle(2,35,Color.BLACK);
        Rectangle numContainer2 = new Rectangle(35,35,getImage(String.valueOf(domino.getValues()[1])));
        HBox numHbox = new HBox(numContainer1,middleDivider,numContainer2);
        stackPane.getChildren().addAll(background, numHbox);
        getChildren().addAll(stackPane);

        setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                selected = !selected;
            }else if(event.getButton().equals(MouseButton.SECONDARY)){
                if(rotatable) rotate(numHbox);
            }
        });
    }

    /**
     * IO for DispDom image resources
     * @param path image path
     * @return image pattern for the path
     */
    private ImagePattern getImage(String path){
        path = "d" + path + ".png";
        try {
            Image image = new Image(getClass().getClassLoader().getResourceAsStream(path));
            return (new ImagePattern(image));
        } catch (Exception e){
            System.out.println(e);
        }
        return null;
    }


    /**
     * Visually rotates the DispDom
     * @param numHbox contains the domino faces
     */
    private void rotate(HBox numHbox){
        domino.rotate();
        Rectangle temp = (Rectangle) numHbox.getChildren().get(0);
        Rectangle middleDivider = new Rectangle(2,35,Color.BLACK);
        numHbox.getChildren().remove(0);
        numHbox.getChildren().remove(0);
        numHbox.getChildren().addAll(middleDivider, temp);
    }

    /**
     * Gets the underlying data structure for a domino
     */
    protected Domino getDomino(){
        return domino;
    }

    protected boolean getSelected(){
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
