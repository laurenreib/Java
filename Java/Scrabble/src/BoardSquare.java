package src;
/**
 * Represent a square on the board
 * stores the values of the word/letter multipliers
 * letter - the true board state
 * templetter - used to try different moves
 * bestletter - keeps track of the highest scoring move
 */
public class BoardSquare {
    private int wordMult;
    private int lettMult;
    private char letter;
    private char tempLetter;
    private char bestLetter;
    boolean isNew;
    BoardSquare(char wordMult, char lettMult){
        if(wordMult != '.') this.wordMult = Integer.parseInt(String.valueOf(wordMult));
        else this.wordMult = 1;

        if(lettMult != '.') this.lettMult = Integer.parseInt(String.valueOf(lettMult));
        else this.lettMult = 1;

        this.letter = '#';
        this.tempLetter = '#';
        isNew = false;
    }
    BoardSquare(char letter){
        wordMult = 1;
        lettMult = 1;
        this.letter = letter;
        this.tempLetter =  letter;
        isNew = false;

    }

    protected void setLetter(char letter){
        this.letter = letter;
        if(isNew){
            wordMult = 1;
            lettMult = 1;
        }
    }

    protected char getLetter() {
        return letter;
    }

    protected void setTempLetter(char tempLetter) {
        this.tempLetter = tempLetter;
    }

    protected char getTempLetter() {
        return tempLetter;
    }

    protected void setBestLetter(char bestLetter) {
        this.bestLetter = bestLetter;
    }

    protected char getBestLetter() {
        return bestLetter;
    }

    protected int getLettMult() {
        return lettMult;
    }

    protected int getWordMult() {
        return wordMult;
    }
}
