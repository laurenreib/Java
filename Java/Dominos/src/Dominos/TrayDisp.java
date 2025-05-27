package Dominos;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import java.util.ArrayList;

/**
 * Frontend JFX representation of a Tray
 */
public class TrayDisp extends HBox {
    private final Tray tray;
    private final ArrayList<DispDom> dispDoms;

    TrayDisp(Tray tray){
        this.tray = tray;
        dispDoms = new ArrayList<>();
        update();  // Make sure tray is initialized correctly in the constructor
        setAlignment(Pos.CENTER);
    }

    /**
     * Updates the hbox with the tray data structure
     */
    protected void update(){
        getChildren().clear();
        dispDoms.clear();  // Clear the old DispDom list to prevent duplicates
        for(Domino d: tray){
            DispDom dd = new DispDom(d,true);
            dispDoms.add(dd);
            getChildren().add(dd);
        }
        setAlignment(Pos.CENTER);
    }

    protected ArrayList<DispDom> getDispDoms() {
        return dispDoms;
    }
}
