package egrep.main.search_engine;

import egrep.main.automaton.Automaton;
import egrep.main.exceptions.AutomatonException;
import egrep.main.exceptions.ParsingException;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * This class use the Java native API for searching in regex
 *
 * @author Emilie Siau
 * @author Hugo Guerrier
 */
public class JavaNativeStrategy implements SearchStrategy {

    // ----- Attributes -----

    private Pattern pattern;

    // ----- Constructor -----

    /**
     * Create a new java native strategy with the wanted regular expression
     *
     * @param regex The regex
     * @throws ParsingException If there is an error in the regex syntaxe
     */
    public JavaNativeStrategy(String regex) throws ParsingException {
        try {
            pattern = Pattern.compile(".*" + regex + ".*");
        } catch(PatternSyntaxException e) {
            throw new ParsingException(e.getMessage());
        }
    }

    // ----- Override methods -----

    @Override
    public boolean isMatching(Automaton automaton, String input) throws AutomatonException {
        return pattern.matcher(input).matches();
    }
}
