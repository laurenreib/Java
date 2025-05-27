
package Dominos;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 * @author lauren
 * CS251
 * Dominos

 * This is a JavaFX based implementation of the classic domino
 * game. It allows a player to compete against
 * a computer opponent. The game features a GUI where
 * players can select & place dominos. The game ends when no more valid moves
 * are possible. Winner is determined by total value
 * of dominos remaining in each players tray.
 * Command line arguments has a maximum value of 6 dots on a domino,
 *  * but inputs range from 3-9
 2 */


public class JFXMain extends Application {
    private static int maxDots= 6;
    public static void main(String[] args) {
        if(args.length > 0) {
            try {
                int parsedValue = Integer.parseInt(args[0]);
                if (parsedValue >= 3 && parsedValue <= 9) {
                    maxDots = parsedValue;
                } else {
                    System.err.println("Invalid argument for maxDots. Using default value of 6.");
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid argument for maxDots. Using default value of 6.");
            }
        }
        launch(args); // Arguments are lost after this call
    }
    private DispDom selectedDom;
    private boolean moveDom = false;
    private boolean animateValidMoveLbl;
    private double validMoveInt;
    private boolean compTurn;
    private TrayDisp playerDispDoms;
    private Label boneYardLbl;
    private Button boneYardBtn;
    private VBox mainVbox;
    private boolean compTurnSkipped;
    private boolean playerTurnSkipped;

    @Override
    public void start(Stage primaryStage) throws Exception {

        BoneYard boneYard = new BoneYard(maxDots);
        Tray compDoms = new Tray(true);
        Tray playerDoms = new Tray(false);
        Board board = new Board();

        boneYardBtn = new Button("Draw from BoneYard");
        Label winLbl = new Label();
        Label validMoveLbl = new Label("You Have A Valid Move!!");
        Label boardLbl = new Label("Board");
        Label trayLbl = new Label("Your Tray");
        boneYardLbl = new Label("The BoneYard contains 14 dominoes");
        validMoveLbl.setTextFill(Color.RED);
        validMoveLbl.setOpacity(0);

        for(int i = 0; i < 7; i++){
            compDoms.add(boneYard.pop());
            playerDoms.add(boneYard.pop());
        }

        VBox boardVbox = showBoard(board);

        playerDispDoms = new TrayDisp(playerDoms);

        mainVbox = new VBox(winLbl,boardLbl, boardVbox,trayLbl,playerDispDoms, boneYardBtn, validMoveLbl,boneYardLbl);
        mainVbox.setSpacing(20);
        mainVbox.setAlignment(Pos.CENTER);

        StackPane stp = new StackPane();
        stp.getChildren().addAll(mainVbox);
        Scene scene = new Scene(stp);
        /**
         * Moves the selected DispDom with the mouse
         */
        scene.setOnMouseMoved(event -> {
            if(moveDom){
                selectedDom.setTranslateX(event.getX()-35);
                selectedDom.setTranslateY(event.getY()-17.5);
            }
        });
        /**
         * Main game logic.
         * compTurn ensures player does not make a move while computer is moving
         */
        scene.setOnMouseClicked(event -> {
            if(!compTurn) {
                if (event.getButton() == MouseButton.PRIMARY) {
                    if (selectedDom != null) {
                        boolean left = false;
                        boolean playToBoard = true;
                        if (event.getSceneX() < scene.getWidth() / 2) {
                            left = true;
                        }
                        if (event.getSceneY() > scene.getHeight() / 2) {
                            playToBoard = false;
                        }
                        if (playToBoard) {
                            //puts domino into board
                            if (board.checkValidMove(selectedDom.getDomino(), left)) {
                                board.addToBoard(selectedDom.getDomino(), left);
                                playerDispDoms.update();
                                updateBoard(board);
                                stp.getChildren().remove(selectedDom);
                                selectedDom = null;
                                moveDom = false;
                                compTurn = true;
                                playerTurnSkipped = false;
                                checkWin(compDoms,playerDoms,true,winLbl);
                                compLogic(board,compDoms,boneYard);
                                checkWin(compDoms,playerDoms,false,winLbl);
                                updateBoard(board);
                            } else {
                                selectedDom.setSelected(!selectedDom.getSelected());
                            }
                            primaryStage.setWidth(updateStageWidth(board, primaryStage.getWidth()));
                        }
                        // puts selectedDom back into tray
                        else {
                            playerDoms.add(selectedDom.getDomino());
                            playerDispDoms.update();
                            updateBoard(board);
                            stp.getChildren().remove(selectedDom);
                            selectedDom = null;
                            moveDom = false;
                        }
                    }
                    //selecting selectedDom
                    if (selectedDom == null) {
                        selectDom(event,playerDoms);
                        if(selectedDom != null) stp.getChildren().add(selectedDom);
                    }
                }
            }
        });

        /**
         * Logic for drawing from the boneyard
         * Also functions to skip the players turn when there is no possible move
         */
        boneYardBtn.setOnMouseClicked(event -> {
            if (!board.checkPossibleMove(playerDoms)) {
                drawBoneYard(boneYard,playerDoms);
                checkWin(compDoms,playerDoms,true,winLbl);
                compLogic(board,compDoms,boneYard);
                checkWin(compDoms,playerDoms,false,winLbl);
                updateBoard(board);
            }
            //logic for validMoveLbl fading effect
            else {
                animateValidMoveLbl = true;
                validMoveInt = 1;
                validMoveLbl.setOpacity(1);
            }
            playerDispDoms.update();
        });

        /**
         * Creates the fading effect for validMoveLbl
         */
        animateValidMoveLbl = false;
        validMoveInt = 0;
        AnimationTimer t = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if(animateValidMoveLbl){
                    validMoveLbl.setOpacity(validMoveInt);
                    validMoveInt -= .01;
                }
                if(validMoveInt == 0 ) animateValidMoveLbl = false;
            }
        };
        t.start();

        primaryStage.setMinHeight(500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Logic to make the stage bigger when needed
     * @return new stage width
     */
    private double updateStageWidth(Board board, double currStageWidth){
        double val = (80 * Math.ceil(board.size() / 2.0)) + 50;
        return Math.max(val, currStageWidth);
    }

    /**
     * Logic to select the domino that was clicked on
     */
    private void selectDom(MouseEvent event, Tray playerDoms){
        DispDom removeDom = null;
        for (DispDom d : playerDispDoms.getDispDoms()) {
            if (d.getSelected() && selectedDom == null) {
                selectedDom = d;
                selectedDom.setTranslateX(event.getX() - 35);
                selectedDom.setTranslateY(event.getY() - 17.5);
                removeDom = d;
                moveDom = true;
            }
        }
        if (removeDom != null) {
            playerDoms.remove(removeDom.getDomino());
            playerDispDoms.update();
        }
    }

    /**
     * Computer Logic(Very dumb)
     * Tries every to play every permuation of its tray and plays the first move found
     * if no move is found draws from the boneyard
     */
    private void compLogic(Board board, Tray compDoms, BoneYard boneYard){
        compTurnSkipped = false;
        if(board.checkPossibleMove(compDoms)){
            for(Domino d: compDoms){
                compTurn = false;
                if(board.checkValidMove(d,true)){
                    board.addToBoard(d,true);
                    compDoms.remove(d);
                    return;
                }
                if(board.checkValidMove(d,false)){
                    board.addToBoard(d,false);
                    compDoms.remove(d);
                    return;
                }
                d.rotate();
                if(board.checkValidMove(d,true)){
                    board.addToBoard(d,true);
                    compDoms.remove(d);
                    return;
                }
                if(board.checkValidMove(d,false)){
                    board.addToBoard(d,false);
                    compDoms.remove(d);
                    return;
                }
            }

        } else{
            drawBoneYard(boneYard,compDoms);
            compTurn = false;
        }
    }

    /**
     * Updates the board
     * The visual board is a vbox that contains 2 hboxs for each row
     * The whole vbox is replaced when updated
     * @param board the board
     */
    private void updateBoard(Board board){
        mainVbox.getChildren().remove(2);
        mainVbox.getChildren().add(2, showBoard(board));
    }

    /**
     * Logic to check for a win
     * @param compDoms computer tray
     * @param playerDoms player tray
     * @param playerWin whether the last move was made by the player or the computer
     * @param winLbl win label
     */
    private void checkWin(Tray compDoms,Tray playerDoms, boolean playerWin, Label winLbl){
        if(compTurnSkipped && playerTurnSkipped){
            int compScore = 0;
            int playerScore = 0;
            for(Domino d: compDoms) compScore += d.getValues()[0]+d.getValues()[1];
            for(Domino d: playerDoms) playerScore += d.getValues()[0]+d.getValues()[1];
            if(compScore > playerScore) winLbl.setText("You Won");
            if(compScore < playerScore) winLbl.setText("The Computer Won");
            if(compScore == playerScore){
                if(playerWin) winLbl.setText("It was a tie\nBut you played the last domino so\nYou Won");
                else winLbl.setText("It was a tie\nBut the computer played the last domino so\nThe Computer Won");
            }
            String winLblText = winLbl.getText();
            winLblText += "\nComputer's Score: " + compScore + "\nYour Score: " + playerScore;
            winLbl.setText(winLblText);
        }
    }

    /**
     * Logic to draw from the boneyard
     * @param boneYard boneyard
     * @param tray tray
     */
    private void drawBoneYard(BoneYard boneYard, Tray tray){
        if(boneYard.isEmpty()){
            if(tray.isCompTray()) compTurnSkipped = true;
            else playerTurnSkipped = true;
        }
        else {
            tray.add(boneYard.pop());
        }
        boneYardLbl.setText("The BoneYard contains " + boneYard.size() + " dominoes");
        if(boneYard.isEmpty()) boneYardBtn.setText("Skip you turn");
    }
    /**
     * The visual representation of a board
     * @return Vbox with two hboxs
     */
    protected VBox showBoard(Board board){
        HBox topRow = new HBox();
        HBox bottomRow = new HBox();
        Rectangle spacer = new Rectangle(72,35, Color.TRANSPARENT);
        if(!board.getTopSpace()) {
            for (int i = 0; i < board.size(); i += 2) {
                topRow.getChildren().add(new DispDom(board.get(i),false));
            }
            for (int i = 1; i < board.size(); i += 2) {
                bottomRow.getChildren().add(new DispDom(board.get(i),false));
            }
        } else{
            for (int i = 1; i < board.size(); i += 2) {
                topRow.getChildren().add(new DispDom(board.get(i),false));
            }
            for (int i = 0; i < board.size(); i += 2) {
                bottomRow.getChildren().add(new DispDom(board.get(i),false));
            }
        }
        //determine the row offset
        if(bottomRow.getChildren().size() == topRow.getChildren().size()) {
            if (board.getTopSpace()) {
                topRow.getChildren().add(0, spacer);
            } else {
                bottomRow.getChildren().add(0, spacer);
            }
        }
        //adds spacers to the board at the begining of the game
        if(bottomRow.getChildren().isEmpty()) bottomRow.getChildren().add(new Rectangle(72,35, Color.TRANSPARENT));
        if(topRow.getChildren().isEmpty()) topRow.getChildren().add(new Rectangle(72,35, Color.TRANSPARENT));

        topRow.setAlignment(Pos.CENTER);
        bottomRow.setAlignment(Pos.CENTER);
        VBox vbox = new VBox(topRow,bottomRow);
        vbox.setAlignment(Pos.TOP_CENTER);
        return vbox;
    }
}
