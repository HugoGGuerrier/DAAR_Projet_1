package egrep.main.search_engine;

import egrep.main.automaton.Automaton;
import egrep.main.exceptions.AutomatonException;
import egrep.main.exceptions.ParsingException;
import egrep.main.parser.RegExParser;
import egrep.main.utils.Pair;

import java.io.File;
import java.util.List;

public class SearchEngine {

    // ----- Attributes -----

    private String regex;
    private File inputFile;
    private SearchStrategy strategy;
    private Automaton automaton;

    // ----- Constructors -----

    /**
     * Create a new search engine with the wanted regular expression and the input file path and the default search
     * strategy
     *
     * @param regex The regular expression
     * @param fileRelativePath The input file
     * @throws Exception If the regex is not correct
     */
    public SearchEngine(String regex, String fileRelativePath) throws AutomatonException, ParsingException {
        this(regex, fileRelativePath, new NaiveStrategy());
    }

    /**
     * Create a new search engine with the wanted regular expression, input file and search strategy
     *
     * @param regex The regex
     * @param fileRelativePath The input file relative path
     * @param strat The search strategy
     * @throws Exception If the regex is not correct
     */
    public SearchEngine(String regex, String fileRelativePath, SearchStrategy strat) throws AutomatonException, ParsingException {
        // Set the attributes
        this.regex = regex;
        inputFile = new File(fileRelativePath);
        strategy = strat;

        // Create the automaton
        RegExParser parser = new RegExParser(regex);
        automaton = new Automaton(parser.parse());
    }

    // ----- Class methods -----

    /**
     * Process the search engine on the input file and return a list of matched lines
     *
     * @return The list of pair (number, line) of the mathed lines
     */
    public List<Pair<Integer, String>> searchLines() {
        return null;
    }

}
