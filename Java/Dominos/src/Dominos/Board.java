package Dominos;


import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

/**
 * The playing field
 * extends ArrayList
 */

public class Board extends ArrayList<Domino> {
    private boolean topSpace;
    Board(){
        topSpace = false;
    }

    /**
     * prints the contents of the board to the command line
     * topSpace determines the offset of the two displayed rows
     */
    protected void showBoard(){

        System.out.println();
        System.out.println("BOARD:\n");

        if(!topSpace) {
            for (int i = 0; i < size(); i += 2) {
                System.out.print(get(i).print());
            }
            System.out.println();
            System.out.print("   ");
            for (int i = 1; i < size(); i += 2) {
                System.out.print(get(i).print());
            }
            System.out.println("\n");
        } else{
            System.out.print("   ");
            for (int i = 1; i < size(); i += 2) {
                System.out.print(get(i).print());
            }
            System.out.println();

            for (int i = 0; i < size(); i += 2) {
                System.out.print(get(i).print());
            }
            System.out.println("\n");
        }

    }
    protected void flipTopSpace(){
        topSpace = !topSpace;
    }
    protected boolean getTopSpace(){
        return topSpace;
    }

    /**
     * Checks if there is a possible move for a given tray
     * @param tray given tray
     * @return true if there is a possible move
     */
    protected boolean checkPossibleMove(Tray tray){
        if(size() == 0) return true;
        if(tray.size() == 0) return false;
        int end1 = get(0).getValues()[0];
        int end2 = get(size()-1).getValues()[1];
        if(end1 == 0 || end2 == 0) return true;
        for (Domino t: tray) {
            if(t.getValues()[0] == 0 || t.getValues()[1] == 0) return true;
            if(t.getValues()[0] == end1 || t.getValues()[1] == end1) return true;
            if(t.getValues()[0] == end2 || t.getValues()[1] == end2) return true;
        }
        return false;
    }

    /**
     * Checks whether a particular move is valid
     * @param dom domino being checked
     * @param left left or right side of the board
     * @return true if dom is a valid move
     */
    protected boolean checkValidMove(Domino dom, boolean left){
        if(isEmpty()) return true;
        int end1 = get(0).getValues()[0];
        int end2 = get(size()-1).getValues()[1];
        if(left){
            //place dom on left
            if(end1 == 0) return true;
            if(dom.getValues()[1] == 0) return true;
            if(dom.getValues()[1] == end1) return true;
        } else{
            //place dom on right
            if(end2 == 0) return true;
            if(dom.getValues()[0] == 0) return true;
            if(dom.getValues()[0] == end2) return true;
        }
        return false;
    }

    /**
     * Adds a domino to the board
     * @param domino domino being added
     * @param left which side domino is added to
     */
    protected void addToBoard(Domino domino, boolean left){
        if(left){
            add(0,domino);
            flipTopSpace();
        }else {
            add(domino);
        }
    }
}

