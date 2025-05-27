package tiles;

import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

/**
 * Each tile is a Stack Pane with 4 rectangles stacked on top of each other
 * L1, L2, L3 - Rectangles where the images are displayed
 * highLight - the top layer and visually indicates what is selected
 * L1layer, L2layer, L3layer keep track of which image is in each layer
 * empty - flags if all the layers are empty i.e. all the layers have been matched
 * selected - flags if the user has selected this tile
 */
public class Tile extends StackPane {
    protected Rectangle l1;
    protected Rectangle l2;
    protected Rectangle l3;
    private final Rectangle highLight;
    private Layer L1layer;
    private Layer L2layer;
    private Layer L3layer;
    private boolean selected;
    private boolean empty;
    private final int i;
    private final int j;

    Tile(double size, int i, int j){
        this.i = i;
        this.j = j;
        l1 = new Rectangle(size, size, Color.TRANSPARENT);
        l2 = new Rectangle(size, size, Color.TRANSPARENT);
        l3 = new Rectangle(size, size, Color.TRANSPARENT);
        highLight = new Rectangle(size,size);
        highLight.setFill(getImage("HighLight.png"));
        highLight.setOpacity(0);
        selected = false;
        L1layer = Layer1.P0;
        L2layer = Layer2.P0;
        L3layer = Layer3.P0;
        getChildren().addAll(l1, l2, l3, highLight);


        setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if(!selected) {
                    if(!empty) {
                        setHighLight();
                    }
                }
            }
        });
    }

    /**
     * Changes the fill of the rectangle associated with each layer
     * @param fill the fill chosen for a layer
     * @param layerNum which layer is chosen
     */
    protected void setLayer(Layer fill, int layerNum){
        if(layerNum == 1){
            l1.setFill(getImage(fill.getPath()));
            L1layer = fill;
        }
        if(layerNum == 2){
            l2.setFill(getImage(fill.getPath()));
            L2layer = fill;
        }
        if(layerNum == 3){
            l3.setFill(getImage(fill.getPath()));
            L3layer = fill;
        }
    }

    /**
     * Clears the layer of Tile that is specified
     * runs flagEmpty() to check if all layers are cleared
     * @param layer which layer to clear
     */
    protected void clearLayer(int layer){
        if(layer == 1){
            l1.setFill(Color.TRANSPARENT);
            L1layer = Layer1.P0;
        }
        if(layer == 2){
            l2.setFill(Color.TRANSPARENT);
            L2layer = Layer2.P0;
        }
        if(layer == 3){
            l3.setFill(Color.TRANSPARENT);
            L3layer = Layer3.P0;
        }
        flagEmpty();
    }

    protected Layer getLayer(int layer){
        if(layer == 1){
            return L1layer;
        }
        if(layer == 2){
            return L2layer;
        }
        if(layer == 3){
            return L3layer;
        }
        return null;
    }

    private ImagePattern getImage(String path){
        try {
            Image image = new Image(getClass().getClassLoader().getResourceAsStream(path));
            return (new ImagePattern(image));
        } catch (Exception e){
            System.out.println(e);
        }
        return(null);
    }

    /**
     * toggles selected flag, toggles highlight around Tile
     */
    protected void setHighLight(){
        if(!selected){
            highLight.setOpacity(1);
        } else {
            highLight.setOpacity(0);
        }
        selected = !selected;
    }

    private void flagEmpty(){
        if(L1layer != Layer1.P0) return;
        if(L2layer != Layer2.P0) return;
        if(L3layer != Layer3.P0) return;
        empty = true;
    }

    protected int getI() {
        return i;
    }

    protected int getJ() {
        return j;
    }

    protected boolean isEmpty() {
        return empty;
    }
    protected boolean isSelected(){
        return selected;
    }
}
