package src;
import java.util.ArrayList;
import java.util.Scanner;


public class Solver {
    static Board board;

    /**
     * Runs the solve logic and returns the relevant information
     * @param tray board's tray
     * @param board board state
     * @param tree dictionary
     * @return the final board state of the best move
     */
    protected static Board solveNew(String tray, Board board, Tree tree) {
        Solver.board = board;
        ArrayList<String> permutedTray = new ArrayList<>();
        permuteTray(tray, permutedTray);
        ArrayList<Anchor> anchors = getAnchors(board.getBoard());

        if (tray.contains("*")) {
            checkPlacementBlanks(anchors, board, tree, permutedTray);
        } else {
            checkPlacement(anchors, board, tree, permutedTray);
        }

        for (int i = 0; i < board.getBoard().length; i++) {
            for (int j = 0; j < board.getBoard().length; j++) {
                board.getBoard()[i][j].setLetter(board.getBoard()[i][j].getBestLetter());
            }
        }
        return board;
    }
    

    /**
     * Takes in a String and finds all the substrings.
     * Adds all permutations to the list without repetition
     * @param tray seven letter String
     * @param permutedTray all permutations of the tray
     */
    private static void permuteTray(String tray, ArrayList<String> permutedTray) {
        //one letter words
        for (int i = 0; i < tray.length(); i++) {
            permutedTray.add(String.valueOf(tray.charAt(i)));
        }
        for (int x = 0; x < tray.length(); x++) {
            //shifts letters from the end to the beginning
            if (x > 0) {
                tray = tray.charAt(tray.length() - 1) + tray.substring(0, tray.length() - 1);
            }
            for (int len = 1; len < tray.length(); len++) {         //various length of the words
                for (int i = 0; i < tray.length() - len; i++) {     //various starts of the words
                    int j = i + len + 1;
                    permuteString(tray.substring(i, j), "", permutedTray);
                }
            }
        }

    }

    /**
     * Recursively finds the all the permutations of a string
     * @param string the String being permuted
     * @param ans the permutation
     * @param permutedTray all permutations of the tray
     */
    private static void permuteString(String string, String ans, ArrayList<String> permutedTray){
        if(string.length() == 0){
            if(!permutedTray.contains(ans)){
                permutedTray.add(ans);
            }
        }
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            String strNew = string.substring(0,i) + string.substring(i+1);
            permuteString(strNew, ans + c, permutedTray);
        }
    }

    /**
     * Place every permutation of the tray in every anchor's row and column. Uses the internal startInd to place the
     * the word in all the possible placements in a given row or column. After a word gets placed it is immediately
     * checked in order to "fail-fast." Then it calls checkBoard to verify crossChecks and calls scoreMove to get the
     * score. After each iteration the board's temp letters are reset.
     * @param anchors the anchors
     * @param board the board
     * @param tree the dictionary
     * @param permutedTray the permuted tray
     */
    private static void checkPlacement(ArrayList<Anchor> anchors, Board board,Tree tree, ArrayList<String> permutedTray){
        BoardSquare[][] boardArr = board.getBoard();
        int boardSize = boardArr.length;
        boolean bingo;
        int highestScore = 0;
        String word;
        for (Anchor anchor:
                anchors) {
            for (String move:
                    permutedTray) {

                //horizontal
                int start = 0;  //determines the start of the start index
                if((move.length()-1) - (anchor.j) < 0){
                    start = anchor.j - (move.length()-1);
                }

                for (int startInd = start; startInd <= anchor.j; startInd++) { //all the startInd for each word
                    bingo = false;
                    word = "";
                    boolean goodPlacement = true;
                    int moveIncrement = 0;
                    //places the word on the board
                    for (int j = startInd; j < boardSize; j++) {
                        if (moveIncrement < move.length()) {
                            if (boardArr[anchor.i][j].getTempLetter() == '#') {
                                boardArr[anchor.i][j].setTempLetter(move.charAt(moveIncrement));
                                boardArr[anchor.i][j].isNew = true;
                                word += move.charAt(moveIncrement);
                                moveIncrement++;
                            } else word += boardArr[anchor.i][j].getTempLetter();
                        }
                    }
                    //checks the word
                    if (!tree.searchTree(word)) {
                        goodPlacement = false;
                    }

                    if (goodPlacement) {
                        if(moveIncrement == 7) bingo = true;
                        if (checkBoard(boardArr,tree)) { //cross-checks
                            int score = scoreMove(boardArr, bingo); // scoring
                            //saves the highest scoring move
                            if(highestScore < score){
                                highestScore = score;
                                for (int i = 0; i < boardSize; i++) {
                                    for (int j = 0; j < boardSize; j++) {
                                        boardArr[i][j].setBestLetter(boardArr[i][j].getTempLetter());
                                    }
                                }
                                board.setTray(move);
                                board.setBestMove(word);
                            }
                        }
                    }
                    board.resetBoard();
                }

                //Vertical
                //The same as above just vertical placements

                start = 0;   //determines the start of the start index
                if((move.length()-1) - (anchor.i) < 0){
                    start = anchor.i - (move.length()-1);
                }
                for (int startInd = start; startInd <= anchor.i; startInd++) {
                    bingo = false;
                    int moveIncrement = 0;
                    word = "";
                    boolean goodPlacement = true;
                    for (int i = startInd; i < boardSize; i++) {
                        if (moveIncrement < move.length()) {
                            if (boardArr[i][anchor.j].getTempLetter() == '#') {
                                boardArr[i][anchor.j].setTempLetter(move.charAt(moveIncrement));
                                boardArr[i][anchor.j].isNew = true;
                                word += move.charAt(moveIncrement);
                                moveIncrement++;
                            } else word += boardArr[i][anchor.j].getTempLetter();
                        }
                    }

                    if (!tree.searchTree(word)) {
                        goodPlacement = false;
                    }

                    if (goodPlacement) {
                        if(moveIncrement == 7) bingo = true;
                        if (checkBoard(boardArr,tree)) {
                            int score = scoreMove(boardArr, bingo);
                            if(highestScore < score){
                                highestScore = score;
                                for (int i = 0; i < boardSize; i++) {
                                    for (int j = 0; j < boardSize; j++) {
                                        boardArr[i][j].setBestLetter(boardArr[i][j].getTempLetter());
                                    }
                                }
                                board.setTray(move);
                                board.setBestMove(word);
                            }
                        }
                    }
                    board.resetBoard();
                }
            }
        }
        board.setScore(highestScore);
    }

    /**
     * Same logic as checkPlacement with additional logic for checking blank values
     */
    private static void checkPlacementBlanks(ArrayList<Anchor> anchors, Board board,Tree tree, ArrayList<String> permutedTray){
        BoardSquare[][] boardArr = board.getBoard();
        int boardSize = boardArr.length;
        boolean bingo;
        int highestScore = 0;
        String word;
        for (Anchor anchor:
                anchors) {
            for (String move:
                    permutedTray) {
                //horizontal
                //determines the start of the start index
                int start = 0;
                if ((move.length() - 1) - (anchor.j) < 0) {
                    start = anchor.j - (move.length() - 1);
                }
                for (int startInd = start; startInd <= anchor.j; startInd++) {
                    bingo = false;
                    word = "";
                    boolean goodPlacement = true;
                    int moveIncrement = 0;
                    for (int j = startInd; j < boardSize; j++) {
                        if (moveIncrement < move.length()) {
                            if (boardArr[anchor.i][j].getTempLetter() == '#') {
                                boardArr[anchor.i][j].setTempLetter(move.charAt(moveIncrement));
                                boardArr[anchor.i][j].isNew = true;
                                word += move.charAt(moveIncrement);
                                moveIncrement++;
                            } else word += boardArr[anchor.i][j].getTempLetter();
                        } else {
                            if(boardArr[anchor.i][j].getTempLetter() != '#') word += boardArr[anchor.i][j].getTempLetter();
                        }
                    }
                    String blankVals = " ";
                    String temp = "";
                    if (move.contains("*")) {
                        blankVals = tree.searchBlank(word, "");
                    }
                    for (int x = 0; x < blankVals.length(); x++) {
                        int replacedI = 0;
                        int replacedJ = 0;
                        if (blankVals.contains(" ")) {
                            if (!tree.searchTree(word)) {
                                goodPlacement = false;
                            }
                        } else {
                            temp = word.replace('*', blankVals.charAt(x));
                            if (!tree.searchTree(temp)) {
                                goodPlacement = false;
                            } else {
                                for (int j = startInd; j < boardSize; j++) {
                                    if (boardArr[anchor.i][j].getTempLetter() == '*') {
                                        replacedI = anchor.i;
                                        replacedJ = j;
                                        boardArr[anchor.i][j].setTempLetter(blankVals.charAt(x));
                                    }
                                }
                            }
                        }
                        if (moveIncrement == 7) bingo = true;
                        if (goodPlacement) {
                            if (checkBoard(boardArr, tree)) {
                                int score = scoreMove(boardArr, bingo);
                                if (highestScore < score) {
                                    highestScore = score;
                                    for (int i = 0; i < boardSize; i++) {
                                        for (int j = 0; j < boardSize; j++) {
                                            boardArr[i][j].setBestLetter(boardArr[i][j].getTempLetter());
                                        }
                                    }
                                    board.setTray(move);
                                    board.setBestMove(temp);
                                }
                            }
                        }
                        boardArr[replacedI][replacedJ].setTempLetter('*');
                    }
                    board.resetBoard();
                }

                //vertical

                //determines the start of the start index
                start = 0;
                if ((move.length() - 1) - (anchor.i) < 0) {
                    start = anchor.i - (move.length() - 1);
                }
                for (int startInd = start; startInd <= anchor.i; startInd++) {
                    bingo = false;
                    int moveIncrement = 0;
                    word = "";
                    for (int i = startInd; i < boardSize; i++) {
                        if (moveIncrement < move.length()) {
                            if (boardArr[i][anchor.j].getTempLetter() == '#') {
                                boardArr[i][anchor.j].setTempLetter(move.charAt(moveIncrement));
                                boardArr[i][anchor.j].isNew = true;
                                word += move.charAt(moveIncrement);
                                moveIncrement++;
                            } else word += boardArr[i][anchor.j].getTempLetter();
                        }
                        else {
                            if(boardArr[i][anchor.j].getTempLetter() != '#') word += boardArr[i][anchor.j].getTempLetter();
                        }
                    }
                    String blankVals = " ";
                    String temp = "";
                    if (move.contains("*")) {
                        blankVals = tree.searchBlank(word, "");
                    }

                    for (int x = 0; x < blankVals.length(); x++) {
                        boolean goodPlacement = true;
                        int replacedI = -1;
                        int replacedJ = -1;
                        if (blankVals.contains(" ")) {
                            if (!tree.searchTree(word)) {
                                goodPlacement = false;
                            }
                        } else {
                            temp = word.replace('*', blankVals.charAt(x));
                            if (!tree.searchTree(temp)) {
                                goodPlacement = false;
                            } else {
                                for (int i = startInd; i < boardSize; i++) {
                                    if (boardArr[i][anchor.j].getTempLetter() == '*') {
                                        replacedI = i;
                                        replacedJ = anchor.j;
                                        boardArr[i][anchor.j].setTempLetter(blankVals.charAt(x));
                                    }
                                }
                            }
                        }
                        if (moveIncrement == 7) bingo = true;
                        if (goodPlacement) {
                            if (checkBoard(boardArr, tree)) {
                                int score = scoreMove(boardArr, bingo);
                                if (highestScore < score) {
                                    highestScore = score;
                                    for (int i = 0; i < boardSize; i++) {
                                        for (int j = 0; j < boardSize; j++) {
                                            boardArr[i][j].setBestLetter(boardArr[i][j].getTempLetter());
                                        }
                                    }
                                    board.setTray(move);
                                    board.setBestMove(temp);
                                }
                            }
                        }
                        if(replacedI > 0 && replacedJ > 0) {
                            boardArr[replacedI][replacedJ].setTempLetter('*');
                        }
                    }
                    board.resetBoard();
                }

            }
        }
        board.setScore(highestScore);
    }

    /**
     * Checks all the words on the board line by line
     * @param board the board
     * @param tree the dictionary
     * @return true if all words are in the dictionary
     */
    protected static boolean checkBoard(BoardSquare[][] board,Tree tree){
        int boardSize = board.length;
        Scanner scan;
        String word;
        //horizontal
        for (int i = 0; i < boardSize; i++) {
            word = "";
            for (int j = 0; j < boardSize; j++) {
                word += board[i][j].getTempLetter();
            }
            scan = new Scanner(word);
            scan.useDelimiter("#+");
            while (scan.hasNext()) {
                String scanned = scan.next();
                if (scanned.length() > 1) {
                    if (!tree.searchTree(scanned)) return false;

                }
            }
        }
        //vertical
        for (int j = 0; j < boardSize; j++) {
            word = "";
            for (int i = 0; i < boardSize; i++) {
                word += board[i][j].getTempLetter();
            }
            scan = new Scanner(word);
            scan.useDelimiter("#+");
            while (scan.hasNext()) {
                String scanned = scan.next();
                if (scanned.length() > 1) {
                    if (!tree.searchTree(scanned)) return false;

                }
            }
        }
        return true;
    }

    /**
     * Finds all the empty spaces adjacent to existing letters on the board
     * @param board the board
     * @return a non-repeating Arraylist of anchors
     */
    private static ArrayList<Anchor> getAnchors(BoardSquare[][] board){
        int boardSize = board.length;
        ArrayList<Anchor> anchors = new ArrayList<>();
        boolean anchorFound;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                anchorFound = false;
                if (board[i][j].getTempLetter() == '#') {
                    if (i > 0) {
                        if (board[i - 1][j].getTempLetter() != '#') {
                            anchors.add(new Anchor(i,j));
                            anchorFound = true;
                        }
                    }
                    if (j > 0) {
                        if (board[i][j - 1].getTempLetter() != '#') {
                            if(!anchorFound){
                                anchors.add(new Anchor(i,j));
                                anchorFound = true;
                            }
                        }
                    }
                    if (i < boardSize - 1) {
                        if (board[i + 1][j].getTempLetter() != '#'){
                            if(!anchorFound){
                                anchors.add(new Anchor(i,j));
                                anchorFound = true;
                            }
                        }
                    }
                    if (j < boardSize - 1) {
                        if (board[i][j + 1].getTempLetter() != '#'){
                            if(!anchorFound) {
                                anchors.add(new Anchor(i, j));
                            }
                        }
                    }
                }
            }
        }
        return anchors;
    }

    /**
     * Scores the move. Finds the new letters and scores by adding up the move itself and then all the cross words
     * @param board the board
     * @param bingo if all 7 letters were used
     * @return the score
     */
    protected static int scoreMove(BoardSquare[][] board,boolean bingo){
        int boardSize = board.length;
        int scoreSum = 0;
        int dim1;
        boolean horizontal = false;
        ArrayList<Integer> rowsToCheck = new ArrayList<>();
        ArrayList<Integer> colsToCheck = new ArrayList<>();

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if(board[i][j].isNew){
                    if(!rowsToCheck.contains(i)) rowsToCheck.add(i);
                    if(!colsToCheck.contains(j)) colsToCheck.add(j);
                }
            }
        }
        //determine if horizontal or vertical placement
        if(rowsToCheck.size() == 1){
            dim1 = rowsToCheck.get(0);
            horizontal = true;
        }
        else dim1 = colsToCheck.get(0);

        int wordSum = 0;
        int wordMult = 1;
        boolean wordNew = false;

        if(horizontal) {
            //the word that was placed
            for (int j = 0; j < boardSize; j++) {
                if (board[dim1][j].getTempLetter() != '#') {
                    if(board[dim1][j].isNew) wordNew = true;
                    wordSum += getTileVal(board[dim1][j].getTempLetter()) * board[dim1][j].getLettMult();
                    wordMult *= board[dim1][j].getWordMult();
                } else {
                    if(wordNew) break;
                    wordSum = 0;
                    wordMult = 1;
                }
            }
            wordSum *= wordMult;
            scoreSum += wordSum;
            //cross words
            for (Integer j :
                    colsToCheck) {
                wordSum = 0;
                wordMult = 1;
                //find the first new letter
                int startInd = 0;
                for (int i = dim1; i > 0; i--) {
                    if (board[i][j].getTempLetter() == '#'){
                        startInd = i+1;
                        break;
                    }
                }

                for (int i = startInd; i < boardSize; i++) {
                    if (board[i][j].getTempLetter() != '#') {
                        wordSum += getTileVal(board[i][j].getTempLetter()) * board[i][j].getLettMult();
                        wordMult *= board[i][j].getWordMult();
                    } else {
                        if(i <= startInd+1){
                            wordMult = 1;
                            wordSum = 0;
                        }
                        break;
                    }
                }
                wordSum *= wordMult;
                scoreSum += wordSum;
            }
        } else {
            //the word that was placed
            for (int i = 0; i < boardSize; i++) {
                if (board[i][dim1].getTempLetter() != '#') {
                    if(board[i][dim1].isNew) wordNew = true;
                    wordSum += getTileVal(board[i][dim1].getTempLetter()) * board[i][dim1].getLettMult();
                    wordMult *= board[i][dim1].getWordMult();
                } else {
                    if(wordNew) break;
                    wordSum = 0;
                    wordMult = 1;
                }
            }
            wordSum *= wordMult;
            scoreSum += wordSum;
            //cross words
            for (Integer i :
                    rowsToCheck) {
                wordSum = 0;
                wordMult = 1;
                //find the first new letter
                int startInd = 0;
                for (int j = dim1; j > 0; j--) {
                    if (board[i][j].getTempLetter() == '#') {
                        startInd = j + 1;
                        break;
                    }
                }
                for (int j = startInd; j < boardSize; j++) {
                    if (board[i][j].getTempLetter() != '#') {
                        wordSum += getTileVal(board[i][j].getTempLetter()) * board[i][j].getLettMult();
                        wordMult *= board[i][j].getWordMult();
                    } else {
                        if(j <= startInd+1){
                            wordMult = 1;
                            wordSum = 0;
                        }
                        break;
                    }
                }
                wordSum *= wordMult;
                scoreSum += wordSum;
            }
        }
        if(bingo){
            scoreSum += 50;
        }
        return scoreSum;
    }

    /**
     * The tile value of char
     * @param c the char
     * @return the value
     */
    private static int getTileVal(char c){
        if(Character.isUpperCase(c)) return 0;
        return switch (c) {
            case 'a', 'e', 'i', 'o', 'u', 'l', 'n', 's', 't', 'r' -> 1;
            case 'd', 'g' -> 2;
            case 'b', 'c', 'm', 'p' -> 3;
            case 'f', 'h', 'v', 'w', 'y' -> 4;
            case 'k' -> 5;
            case 'j', 'x' -> 8;
            case 'z', 'q' -> 10;
            default -> 0;
        };
    }
}
