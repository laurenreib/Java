package Dominos;

import java.util.ArrayList;

/**
 * Backend Data Structure for the player or computer's tray
 * Just an arraylist with some extras
 */
public class Tray extends ArrayList<Domino> {
    private final boolean isCompTray;

    Tray(boolean isCompTray){
        this.isCompTray = isCompTray;
    }

    /**
     * Prints the contents of the tray to the command line
     */
    protected void show(){
        if(size() == 0){
            System.out.println("[empty]");
            return;
        }
        for (Domino d: this) {
            System.out.print(d.print());
        }
        System.out.println();
        System.out.print("  ");
        for (int i = 0; i < size(); i++) {
            System.out.print(i + "     ");
        }
        System.out.println();
    }

    protected boolean isCompTray() {
        return isCompTray;
    }
}
