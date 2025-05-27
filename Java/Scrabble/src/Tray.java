package src;
import javafx.scene.layout.HBox;

import java.util.ArrayList;

public class Tray extends HBox {
    ArrayList<Tile> tiles;
    ArrayList<Tile> removedTiles;
    Tray(ArrayList<Tile> tiles){
        this.tiles = tiles;
        getChildren().addAll(tiles);
        removedTiles = new ArrayList<>();
    }
    protected void remove(Tile tile){
        removedTiles.add(tile);
        tiles.remove(tile);
        getChildren().remove(tile);
    }
    protected void add(ArrayList<Tile> tilesToAdd){
        tiles.addAll(tilesToAdd);
        getChildren().addAll(tilesToAdd);
    }
    protected void add(Tile tile){
        tiles.add(tile);
        getChildren().add(tile);
    }
    protected void validMove(ArrayList<Tile> tilesToAdd){
        removedTiles.clear();
        add(tilesToAdd);
    }
    protected void invalidMove(){
        tiles.addAll(removedTiles);
        removedTiles.clear();
        for (Tile t : tiles){
            if(!getChildren().contains(t)){
                t.setTranslateX(0);
                t.setTranslateY(0);
                getChildren().add(t);
            }
        }
    }
    protected ArrayList<BoardSquare> shuffle(ArrayList<Tile> tilesToAdd){
        ArrayList<BoardSquare> boardSquares = new ArrayList<>();
        for (Tile t: tiles) boardSquares.add(t.boardSq);
        tiles.clear();
        getChildren().clear();
        add(tilesToAdd);
        return boardSquares;
    }
}
