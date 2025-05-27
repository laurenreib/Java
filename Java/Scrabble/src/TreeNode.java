package src;
import java.util.ArrayList;

public class TreeNode {
    boolean isPossibleEnd;
    char letter;
    ArrayList<TreeNode> nextNodes;

    TreeNode(char letter){
        this.letter = letter;
        nextNodes = new ArrayList<>();
    }

    protected void addChilds(String str){
        if(str.length() == 0){
            isPossibleEnd = true;
            return;
        }
        char firstChar  = str.charAt(0);

        //node has no children
        if(nextNodes == null){
            TreeNode tn = new TreeNode(firstChar);
            nextNodes.add(tn);
            tn.addChilds(str.substring(1));
        }

        //node has children
        //has the subtree
        boolean subTreeExists = false;
        for (TreeNode tn : nextNodes) {
            if (tn.getLetter() == firstChar){
                subTreeExists = true;
                tn.addChilds(str.substring(1));
            }
        }
        //does not have the subtree
        if(!subTreeExists){
            TreeNode tn = new TreeNode(firstChar);
            nextNodes.add(tn);
            tn.addChilds(str.substring(1));
        }
    }

    protected boolean searchTree(String str){
        if(str.length() == 0) return isPossibleEnd;
        char firstChar = str.charAt(0);

        if(nextNodes == null) return false;
        for (TreeNode tn : nextNodes) {
            if (tn.getLetter() == firstChar) {
                if (tn.searchTree(str.substring(1))) return true;
            }
        }
        return false;
    }

    protected String searchBlank(String str, String blankVals) {
        if(str.length() == 0) return blankVals;
        char firstChar = str.charAt(0);
        if(firstChar == '*') {
            for (TreeNode child :
                    nextNodes) {
                blankVals += Character.toUpperCase(child.getLetter());
            }
            return blankVals;
        }
        if(nextNodes == null) return blankVals;
        for (TreeNode tn : nextNodes) {
            if (tn.getLetter() == firstChar) {
                return blankVals + tn.searchBlank(str.substring(1), blankVals);
            }
        }
        return blankVals;
    }
    protected char getLetter(){
        return letter;
    }














//    protected void addChild(int childIndex){
//        if(!nextNodes.contains(childIndex)) nextNodes.add(childIndex);
//    }
//
//    public void setDefiniteEnd(boolean definiteEnd) {
//        isDefiniteEnd = definiteEnd;
//    }
//
//    public void setPossibleEnd(boolean possibleEnd) {
//        isPossibleEnd = possibleEnd;
//    }
//    protected ArrayList getChilds(){
//        return nextNodes;
//    }
}

