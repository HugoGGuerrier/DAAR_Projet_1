package egrep.main.parser;

import java.util.ArrayList;

/**
 * This class represent a regular expression tree
 *
 * @author Emilie SIAU
 * @author Hugo GUERRIER
 */
public class RegExTree {

    // ----- Attributes -----

    private int root;
    private ArrayList<RegExTree> subTrees;

    // ----- Constructors -----

    /**
     * Create a regex tree with its root and its subtrees
     *
     * @param root The root
     * @param subTrees The subtrees
     */
    public RegExTree(int root, ArrayList<RegExTree> subTrees) {
        this.root = root;
        this.subTrees = subTrees;
    }

    // ----- Getters -----

    public int getRoot() {
        return root;
    }

    public ArrayList<RegExTree> getSubTrees() {
        return subTrees;
    }

    // ----- Setters -----

    public void setRoot(int root) {
        this.root = root;
    }

    public void setSubTrees(ArrayList<RegExTree> subTrees) {
        this.subTrees = subTrees;
    }

    public void addSubTree(RegExTree tree) {
        subTrees.add(tree);
    }

    public void removeSubTree(RegExTree tree) {
        subTrees.remove(tree);
    }

    // ----- Override methods -----

    @Override
    public String toString() {
        if (subTrees.isEmpty()){
            return rootToString();
        }

        StringBuilder res = new StringBuilder();
        res.append(rootToString()).append('(');
        for(int i = 0 ; i < subTrees.size() ; i++) {
            RegExTree subTree = subTrees.get(i);
            res.append(subTree.toString());
            if(i < subTrees.size() - 1) {
                res.append(", ");
            }
        }
        res.append(')');

        return res.toString();
    }

    // ----- Class methods -----

    /**
     * Get the string from the root
     *
     * @return The string representing the root
     */
    private String rootToString() {
        if (root== RegExParser.CONCAT) return ".";
        if (root== RegExParser.STAR) return "*";
        if (root== RegExParser.ALTERN) return "|";
        if (root== RegExParser.DOT) return ".";
        return Character.toString((char) root);
    }

}
