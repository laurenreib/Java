package tiles;

import javafx.scene.layout.GridPane;

/**
 * Board contains the game Tiles in as a Gridpane.
 * tileArr a 2d array to reference individual Tiles.
 * currSelected keeps track of the current Tile selected.
 * Is constructed with empty Tiles.
 */
public class Board extends GridPane {
    protected Tile[][] tileArr;
    private Tile currSelected;

    Board(int rows, int cols, double size) {
        setRowSpan(this, rows);
        setColumnSpan(this, cols);
        tileArr = new Tile[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Tile t = new Tile(size, i, j);
                add(t, j, i);
                tileArr[i][j] = t;
            }
        }
        currSelected = null;
    }

    //pass through to initiate a Tile
    protected void setTile(int row, int col, Layer fill, int layerNum) {
        tileArr[row][col].setLayer(fill,layerNum);
    }


    /**
     * Runs every time the user clicks.
     * Finds new selected tile, returns if special case.
     * Compares the 2 selected tiles.
     * @return (-1) to reset score, (0) to do nothing, (1) to increment score.
     */
    protected int runGameLogic() {
        Tile newSelected = null;
        for (int i = 0; i < tileArr.length; i++) {
            for (int j = 0; j < tileArr[0].length; j++) {
                if (tileArr[i][j].isSelected()) {
                    if(currSelected != null) {
                        if (!(currSelected.getI()== i && currSelected.getJ() == j)) {
                            newSelected = tileArr[i][j];
                        }
                    } else {
                        newSelected = tileArr[i][j];
                    }
                }
            }
        }
        //start case i.e. one Tile selected
        if(currSelected == null){
            currSelected = newSelected;
            return(-1);
        }
        //clicking same tile twice case
        if(newSelected == null) return (0);

        //check match
        boolean matched = false;
        if(compareTiles(currSelected,newSelected, 1)) matched = true;
        if(compareTiles(currSelected,newSelected, 2)) matched = true;
        if(compareTiles(currSelected,newSelected, 3)) matched = true;

        tileArr[currSelected.getI()][currSelected.getJ()].setHighLight();

        //output defaults to no match case(-1)
        int output = -1;

        //current Tile is empty case(0)
        if(currSelected.isEmpty()) output = 0;

        //correct match case(1) (match case overrides empty case if tile get cleared to empty)
        if(matched) output = 1;

        currSelected = newSelected;
        return (output);
    }

    /**
     * Compares two tiles using their layers.
     * @param currSelected the Tile that was clicked last click.
     * @param newSelected the Tile that was clicked this click.
     * @param layer determines which layer is being compared.
     * @return true if matched, false if not matched.
     */
    private boolean compareTiles(Tile currSelected, Tile newSelected, int layer){
        if (currSelected.getLayer(layer) == newSelected.getLayer(layer)) {
            if(currSelected.getLayer(layer).getIndex() != 0){
                tileArr[currSelected.getI()][currSelected.getJ()].clearLayer(layer);
                tileArr[newSelected.getI()][newSelected.getJ()].clearLayer(layer);
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if all tiles are flagged empty.
     * @return if all tiles are empty true, else false.
     */
    boolean checkWin(){
        boolean win = true;
        for (int i = 0; i < tileArr.length; i++) {
            for (int j = 0; j < tileArr[0].length; j++) {
                if(!tileArr[i][j].isEmpty()){
                    win = false;
                }
            }
        }
        return(win);
    }
}