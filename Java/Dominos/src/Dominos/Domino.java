package Dominos;

/**
 * Data structure for a domino
 */
public class Domino {
    private int val1;
    private int val2;

    Domino(int val1, int val2){
        this.val1 = val1;
        this.val2 = val2;
    }

    protected int[] getValues(){
        return (new int[]{val1, val2});
    }

    protected String print(){
        String s = "[" + getValues()[0];
        s += " " + getValues()[1] + "] ";
        return s;
    }
    protected void rotate(){
        int temp = val2;
        val2 = val1;
        val1 = temp;
    }
}
