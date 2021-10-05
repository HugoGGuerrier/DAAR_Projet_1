package egrep.main.parser;

import egrep.main.exceptions.ParsingException;

import java.util.ArrayList;

import java.lang.Exception;

/**
 * The regular expression parser
 *
 * @author Emilie SIAU
 * @author Hugo GUERRIER
 */
public class RegExParser {

    // ----- Macros -----

    public static final int CONCAT = 0xFF1;
    public static final int STAR = 0xFF2;
    public static final int ALTERN = 0xFF3;
    public static final int DOT = 0xFF7;

    public static final int L_PAREN = 0xFF5;
    public static final int R_PAREN = 0xFF6;
    public static final int PROTECTION = 0xFF4;

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

    /**
     * Create a new regex without aunything
     */
    public RegExParser() {
        this.regEx = null;
        this.parseResult = null;
    }

    // ----- Getters -----

    public String getRegEx() {
        return regEx;
    }

    // ----- Setters -----

    public void setRegEx(String regEx) {
        this.regEx = regEx;
        this.parseResult = null;
    }

    // ----- Class methods -----

    /**
     * Start the parsing process and return the result
     *
     * @return The regex tree
     * @throws ParsingException If there is an error during the parsing process
     */
    public RegExTree parse() throws ParsingException {
        // Avoid parsing repetition
        if(parseResult != null) {
            return parseResult;
        }

        // Verify the regex non nullity
        if(regEx == null || regEx.isEmpty()) {
            throw new ParsingException("Regex cannot be null or empty");
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

    private RegExTree parse(ArrayList<RegExTree> result) throws ParsingException {
        // Process all special chars
        while (containsParenthesis(result)) result= processParenthesis(result);
        while (containsStar(result)) result= processStar(result);
        while (containsConcat(result)) result=processConcat(result);
        while (containsAltern(result)) result=processAltern(result);

        // Verify that the result doesn't have multiple root
        if (result.size() > 1) {
            throw new ParsingException("Multiple root tree is impossible");
        }

        // Return the result removing the protection
        return removeProtection(result.get(0));
    }

    private boolean containsParenthesis(ArrayList<RegExTree> trees) {
        // Look for the left parenthesis or right parenthesis
        for (RegExTree t: trees) {
            if(t.getRoot() == L_PAREN || t.getRoot() == R_PAREN) {
                return true;
            }
        }

        // If nothing found, return false
        return false;
    }

    private ArrayList<RegExTree> processParenthesis(ArrayList<RegExTree> trees) throws ParsingException {
        // Prepare the result
        ArrayList<RegExTree> result = new ArrayList<>();

        // Prepare the working variables
        boolean found = false;

        for (RegExTree t: trees) {
            if (!found && t.getRoot() == R_PAREN) {

                // Create the done marker
                boolean done = false;

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
                    throw new ParsingException("Error during parsing : missing '(' parenthesis");
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
            throw new ParsingException("Error during parsing : missing ')' parenthesis");
        }

        // Return the parsing result
        return result;
    }

    private boolean containsStar(ArrayList<RegExTree> trees) {
        // Look for a star operator
        for (RegExTree t: trees) {
            if(t.getRoot() == STAR && t.getSubTrees().isEmpty()) {
                return true;
            }
        }

        // If nothing found, return false
        return false;
    }

    private ArrayList<RegExTree> processStar(ArrayList<RegExTree> trees) throws ParsingException {
        // Prepare the result
        ArrayList<RegExTree> result = new ArrayList<>();

        // Prepare the working variables
        boolean found = false;

        for (RegExTree t: trees) {
            if (!found && t.getRoot() == STAR && t.getSubTrees().isEmpty()) {

                // If there is no char for * , throw an exception
                if (result.isEmpty()) {
                    throw new ParsingException("Error during parsing : '*' need a character");
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
            if(t.getRoot() == ALTERN && t.getSubTrees().isEmpty()) {
                return true;
            }
        }

        // Return the result
        return false;
    }

    private ArrayList<RegExTree> processAltern(ArrayList<RegExTree> trees) throws ParsingException {
        // Prepare the result
        ArrayList<RegExTree> result = new ArrayList<>();

        // Prepare the working variables
        boolean found = false;
        boolean done = false;
        RegExTree gauche = null;

        for (int i = 0 ; i < trees.size() ; i++) {
            RegExTree t = trees.get(i);

            if (!found && t.getRoot() == ALTERN && t.getSubTrees().isEmpty()) {
                // If there is no left altern part throw an exception
                if(result.isEmpty()) {
                    throw new ParsingException("Error during parsing : missing '|' left part");
                }

                // If there is not right altern part throw an exception
                if(i >= trees.size() - 1) {
                    throw new ParsingException("Error during parsing : missing '|' right part");
                }

                // Found the altern symbol, get the left part
                found = true;
                gauche = result.remove(result.size()-1);
            } else if (found && !done) {
                // Found the altern symbol so get the right part
                done=true;
                ArrayList<RegExTree> subTrees = new ArrayList<>();
                subTrees.add(gauche);
                subTrees.add(t);
                result.add(new RegExTree(ALTERN, subTrees));
            } else {
                result.add(t);
            }
        }

        // Return the result
        return result;
    }

    private RegExTree removeProtection(RegExTree tree) throws ParsingException {
        // If the tree is a protection and there is more than 1 child
        if (tree.getRoot() == PROTECTION && tree.getSubTrees().size() != 1) {
            throw new ParsingException("Error during parsing : protection with more than 1 child");
        }

        // If the subtree is empty, just return it
        if (tree.getSubTrees().isEmpty()) {
            return tree;
        }

        // If the tree is a protection return the first child
        if (tree.getRoot() == PROTECTION) {
            return removeProtection(tree.getSubTrees().get(0));
        }

        // If the tree is not a protection just remove the protection from all subtrees
        ArrayList<RegExTree> subTrees = new ArrayList<>();
        for (RegExTree t: tree.getSubTrees()) {
            subTrees.add(removeProtection(t));
        }
        return new RegExTree(tree.getRoot(), subTrees);
    }

    /**
     * Get an example from Aho-Ullman book Chap.10 Example 10.25
     *
     * @return The example regex tree
     */
    public static RegExTree exampleAhoUllman() {
        // Prepare the leaf
        RegExTree a = new RegExTree('a', new ArrayList<>());
        RegExTree b = new RegExTree('b', new ArrayList<>());
        RegExTree c = new RegExTree('c', new ArrayList<>());

        // Create the '*' node
        ArrayList<RegExTree> subTrees = new ArrayList<>();
        subTrees.add(c);
        RegExTree cEtoile = new RegExTree(STAR, subTrees);

        // Create the concat node
        subTrees = new ArrayList<>();
        subTrees.add(b);
        subTrees.add(cEtoile);
        RegExTree dotBCEtoile = new RegExTree(CONCAT, subTrees);

        // Create the '|' node
        subTrees = new ArrayList<>();
        subTrees.add(a);
        subTrees.add(dotBCEtoile);

        // Return the wanted regex tree
        return new RegExTree(ALTERN, subTrees);
    }
}
