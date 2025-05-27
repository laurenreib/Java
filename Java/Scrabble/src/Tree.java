package src;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Tree {

    TreeNode[] rootNodes;

    Tree(File file){
        rootNodes = new TreeNode[26];
        for (int i = 0; i < 26; i++) {
            TreeNode tn = new TreeNode((char)(i+97));
            rootNodes[i] = tn;
        }
        Scanner scan = null;
        try{
            scan = new Scanner(file);
        } catch (FileNotFoundException fnfe){
            System.out.println("warning");
        }
        String word = "";
        while(scan.hasNext()){
            word = scan.nextLine();
            if(word.length() > 0) addToTree(word);
        }
    }

    protected void addToTree(String str){
        char rootChar = str.charAt(0);
        if(str.length() == 1){
            rootNodes[((int)rootChar)-97].isPossibleEnd = true;
            return;
        }
        rootNodes[((int)rootChar)-97].addChilds(str.substring(1));
    }

    protected boolean searchTree(String str){
        String lowerCaseStr = "";
        for (int i = 0; i < str.length(); i++) {
            lowerCaseStr += Character.toLowerCase(str.charAt(i));
        }
        str = lowerCaseStr;
        char rootChar = str.charAt(0);
        if(str.length() == 1){
            return false;
        } else {
            return rootNodes[((int) rootChar) - 97].searchTree(str.substring(1));
        }
    }
    protected String searchBlank(String str, String blankVals){
        String lowerCaseStr = "";
        for (int i = 0; i < str.length(); i++) {
            lowerCaseStr += Character.toLowerCase(str.charAt(i));
        }
        str = lowerCaseStr;
        char rootChar = str.charAt(0);
        if(rootChar == '*') {
            for (TreeNode child:
                    rootNodes) {
                blankVals += Character.toUpperCase(child.getLetter());
            }
            return blankVals;
        }
        if(str.length() == 1){
            return blankVals;
        } else {
            return blankVals + rootNodes[((int) rootChar) - 97].searchBlank(str.substring(1),blankVals);
        }

    }
}
