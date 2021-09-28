package egrep.parser;

import java.util.ArrayList;

import java.lang.Exception;

public class RegExParser {

    // ----- Static values -----

    static final int CONCAT = 0xC04CA7;
    static final int STAR = 0xE7011E;
    static final int ALTERN = 0xA17E54;
    static final int PROTECTION = 0xBADDAD;

    static final int L_PAREN = 0x16641664;
    static final int R_PAREN = 0x51515151;
    static final int DOT = 0xD07;

    // ----- Attributes -----

    private String regEx;
    private RegExTree parseResult;

    // ----- Constructors -----

    /**
     * Create a new regex parser with the regex string
     *
     * @param regEx The regex string
     */
    public RegExParser(String regEx){
        this.regEx = regEx;
        this.parseResult = null;
    }

    // ----- Getters -----

    public String getRegEx() {
        return regEx;
    }

    // ----- Setters -----

    public void setRegEx(String regEx) {
        this.regEx = regEx;
    }

    // ----- Class methods -----

    /**
     * Start the parsing process and return the result
     *
     * @return The regex tree
     * @throws Exception If there is an error during the parsing process
     */
    public RegExTree parse() throws Exception {
        // Avoid parsing repetition
        if(parseResult != null) {
            return parseResult;
        }

        // Prepare the result
        ArrayList<RegExTree> result = new ArrayList<>();
        for (int i=0 ; i<regEx.length() ; i++) {
            result.add(new RegExTree(charToRoot(regEx.charAt(i)), new ArrayList<>()));
        }

        // Parse the regex
        parseResult = parse(result);

        // Return the result
        return parseResult;
    }

    private int charToRoot(char c) {
        if (c=='.') return DOT;
        if (c=='*') return STAR;
        if (c=='|') return ALTERN;
        if (c=='(') return L_PAREN;
        if (c==')') return R_PAREN;
        return c;
    }

    private RegExTree parse(ArrayList<RegExTree> result) throws Exception {
        // Process all special chars
        while (containsParenthesis(result)) result= processParenthesis(result);
        while (containsStar(result)) result=processEtoile(result);
        while (containsConcat(result)) result=processConcat(result);
        while (containsAltern(result)) result=processAltern(result);

        // Verify that the result doesn't have multiple root
        if (result.size() > 1) {
            throw new Exception("Multiple root tree is impossible");
        }

        // Return the result removing the protection
        return removeProtection(result.get(0));
    }

    private boolean containsParenthesis(ArrayList<RegExTree> trees) {
        // Look for the left parenthesis or right parenthesis
        for (RegExTree t: trees) {
            return t.getRoot() == L_PAREN || t.getRoot() == R_PAREN;
        }

        // If nothing found, return false
        return false;
    }

    private ArrayList<RegExTree> processParenthesis(ArrayList<RegExTree> trees) throws Exception {
        // Prepare the result
        ArrayList<RegExTree> result = new ArrayList<>();

        // Prepare the working variables
        boolean found = false;
        boolean done = false;

        for (RegExTree t: trees) {
            if (!found && t.getRoot() == R_PAREN) {

                // Prepare the parenthesis content
                ArrayList<RegExTree> content = new ArrayList<>();

                // While there is no left parenthesis, add the precedent tree to the parent content
                while (!done && !result.isEmpty()) {
                    if (result.get(result.size() - 1).getRoot() == L_PAREN) {
                        done = true;
                        result.remove(result.size() - 1);
                    } else {
                        content.add(0, result.remove(result.size() - 1));
                    }
                }

                // If there is not left parenthesis throw an exception
                if (!done) {
                    throw new Exception("Error during parsing : missing '(' parenthesis");
                }

                // We found the parent content, lets parse it !
                found = true;
                ArrayList<RegExTree> subTrees = new ArrayList<>();
                subTrees.add(parse(content));
                result.add(new RegExTree(PROTECTION, subTrees));

            } else {
                result.add(t);
            }
        }

        // Throw exception if there is a missing parenthesis
        if (!found) {
            throw new Exception("Error during parsing : missing ')' parenthesis");
        }

        // Return the parsing result
        return result;
    }

    private boolean containsStar(ArrayList<RegExTree> trees) {
        // Look for a star operator
        for (RegExTree t: trees) {
            return t.getRoot() == STAR && t.getSubTrees().isEmpty();
        }

        // If nothing found, return false
        return false;
    }

    private ArrayList<RegExTree> processEtoile(ArrayList<RegExTree> trees) throws Exception {
        // Prepare the result
        ArrayList<RegExTree> result = new ArrayList<>();

        // Prepare the working variables
        boolean found = false;

        for (RegExTree t: trees) {
            if (!found && t.getRoot() == STAR && t.getSubTrees().isEmpty()) {

                // If there is no char for * , throw an exception
                if (result.isEmpty()) {
                    throw new Exception("Error during parsing : '*' need a character");
                }

                // We found it, let's add the result
                found = true;
                RegExTree last = result.remove(result.size()-1);
                ArrayList<RegExTree> subTrees = new ArrayList<>();
                subTrees.add(last);
                result.add(new RegExTree(STAR, subTrees));

            } else {
                result.add(t);
            }
        }

        // Return the parsing result
        return result;
    }

    private boolean containsConcat(ArrayList<RegExTree> trees) {
        // Prepare the working variable
        boolean firstFound = false;

        // Look for the concatenation (implicit)
        for (RegExTree t: trees) {
            if(!firstFound && t.getRoot() != ALTERN) {
                firstFound = true;
            } else if(firstFound) {
                if (t.getRoot() != ALTERN) {
                    return true;
                } else {
                    firstFound = false;
                }
            }
        }

        // If nothing found, return false
        return false;
    }

    private ArrayList<RegExTree> processConcat(ArrayList<RegExTree> trees) {
        // Prepare the result
        ArrayList<RegExTree> result = new ArrayList<>();

        // Prepare the working variables
        boolean found = false;
        boolean firstFound = false;

        for (RegExTree t: trees) {
            if (!found && !firstFound && t.getRoot() != ALTERN) {
                firstFound = true;
                result.add(t);
            } else if (!found && firstFound && t.getRoot() == ALTERN) {
                firstFound = false;
                result.add(t);
            } else if (!found && firstFound && t.getRoot() != ALTERN) {
                // We found a concatenation let's add it
                found = true;
                RegExTree last = result.remove(result.size()-1);
                ArrayList<RegExTree> subTrees = new ArrayList<>();
                subTrees.add(last);
                subTrees.add(t);
                result.add(new RegExTree(CONCAT, subTrees));
            } else {
                result.add(t);
            }
        }

        // Return the result
        return result;
    }

    private boolean containsAltern(ArrayList<RegExTree> trees) {
        // Look for the alternative symbol
        for (RegExTree t: trees) {
            return t.getRoot() == ALTERN && t.getSubTrees().isEmpty();
        }

        // Return the result
        return false;
    }

    private ArrayList<RegExTree> processAltern(ArrayList<RegExTree> trees) throws Exception {
        // Prepare the result
        ArrayList<RegExTree> result = new ArrayList<>();

        // Prepare the working variables
        boolean found = false;
        boolean done = false;
        RegExTree gauche = null;

        for (RegExTree t: trees) {
            if (!found && t.getRoot() == ALTERN && t.getSubTrees().isEmpty()) {
                if (result.isEmpty()) {
                    throw new Exception("Error during parsing : missing '|' left part");
                }
                found = true;
                gauche = result.remove(result.size()-1);
            } else if (found && !done) {
                if (gauche==null) throw new Exception();
                done=true;
                ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
                subTrees.add(gauche);
                subTrees.add(t);
                result.add(new RegExTree(ALTERN, subTrees));
            } else {
                result.add(t);
            }
        }
        return result;
    }

    private RegExTree removeProtection(RegExTree tree) throws Exception {
        if (tree.root==PROTECTION && tree.subTrees.size()!=1) throw new Exception();
        if (tree.subTrees.isEmpty()) return tree;
        if (tree.root==PROTECTION) return removeProtection(tree.subTrees.get(0));

        ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
        for (RegExTree t: tree.subTrees) subTrees.add(removeProtection(t));
        return new RegExTree(tree.root, subTrees);
    }

    //EXAMPLE
    // --> RegEx from Aho-Ullman book Chap.10 Example 10.25
    private RegExTree exampleAhoUllman() {
        RegExTree a = new RegExTree((int)'a', new ArrayList<RegExTree>());
        RegExTree b = new RegExTree((int)'b', new ArrayList<RegExTree>());
        RegExTree c = new RegExTree((int)'c', new ArrayList<RegExTree>());
        ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
        subTrees.add(c);
        RegExTree cEtoile = new RegExTree(ETOILE, subTrees);
        subTrees = new ArrayList<RegExTree>();
        subTrees.add(b);
        subTrees.add(cEtoile);
        RegExTree dotBCEtoile = new RegExTree(CONCAT, subTrees);
        subTrees = new ArrayList<RegExTree>();
        subTrees.add(a);
        subTrees.add(dotBCEtoile);
        return new RegExTree(ALTERN, subTrees);
    }
}
