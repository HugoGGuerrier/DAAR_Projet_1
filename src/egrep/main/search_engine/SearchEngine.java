package egrep.main.search_engine;

import egrep.main.automaton.Automaton;
import egrep.main.exceptions.AutomatonException;
import egrep.main.exceptions.ParsingException;
import egrep.main.exceptions.SearchEngineException;
import egrep.main.parser.RegExParser;
import egrep.main.utils.Pair;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * This class represents a search engine for a file
 *
 * @author Emilie SIAU
 * @author Hugo GUERRIER
 */
public class SearchEngine {

    // ----- Macros -----

    public enum Strategy {
        NAIVE,
        KMP,
        JAVA_NATIVE
    }

    // ----- Attributes -----

    private String regex;
    private File inputFile;
    private SearchStrategy strategy;
    private Automaton automaton;

    // ----- Constructors -----

    /**
     * Create a new search engine with the wanted regular expression and input file.
     * It selects the suiting strategy (naive or KMP) for the given regex
     *
     * @param regex The regex
     * @param fileRelativePath The input file relative path
     * @throws ParsingException If the regex is not correct
     */
    public SearchEngine(String regex, String fileRelativePath) throws ParsingException {
        // Set the attributes
        this.regex = regex;
        inputFile = new File(fileRelativePath);

        // Select the best strategy, following if the regex is only concatenations or not
        if (KMPStrategy.isValidKMP(regex)){

            strategy = new KMPStrategy(regex);
            automaton = null;

        } else {

            strategy = new NaiveStrategy();

            // Create the automaton
            RegExParser parser = new RegExParser(regex);
            try {
                automaton = new Automaton(parser.parse());
            } catch (AutomatonException e) {
                System.err.println("This CANNOT happen");
                e.printStackTrace();
            }
        }
    }

    /**
     * Create a new search engine with the wanted regex, input file and force it to use the wanted strategy
     * If the strategy is not suitable, it throws a special exception
     *
     * @param regex The regular expression
     * @param fileRelativePath The file to search in
     * @param strat The wanted strategy
     * @throws ParsingException If there is an error during the regex parsing
     * @throws SearchEngineException If the wanted strategy is not suitable for the given regex
     */
    public SearchEngine(String regex, String fileRelativePath, Strategy strat) throws ParsingException, SearchEngineException {
        // Set the common attributes
        this.regex = regex;
        inputFile = new File(fileRelativePath);

        // Set up the searching strategy
        switch (strat) {

            case KMP:
                if(KMPStrategy.isValidKMP(regex)) {
                    strategy = new KMPStrategy(this.regex);
                    automaton = null;
                } else {
                    throw new SearchEngineException("Cannot require the KMP strategy for the regex " + regex);
                }
                break;

            case JAVA_NATIVE:
                strategy = new JavaNativeStrategy(this.regex);
                automaton = null;
                break;

            case NAIVE:
            default:
                strategy = new NaiveStrategy();
                RegExParser parser = new RegExParser(regex);
                try {
                    automaton = new Automaton(parser.parse());
                } catch (AutomatonException e) {
                    System.err.println("This CANNOT happen");
                    e.printStackTrace();
                }

        }
    }

    // ----- Class methods -----

    /**
     * Process the search engine on the input file and return a list of matched lines
     *
     * @return The list of pair (number, line) of the matched lines
     */
    public List<Pair<Integer, String>> searchLines() throws IOException, AutomatonException {
        // Prepare the result
        List<Pair<Integer, String>> res = new LinkedList<>();

        // Open the input file and process it line by line
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        String line;
        int lineNumber = 1;
        while((line = reader.readLine()) != null) {
            if(strategy.isMatching(automaton, line)) res.add(new Pair<>(lineNumber, line));
            lineNumber++;
        }

        // Return the result
        return res;
    }

}
