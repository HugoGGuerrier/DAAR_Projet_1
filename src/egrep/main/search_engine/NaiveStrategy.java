package egrep.main.search_engine;

import egrep.main.automaton.Automaton;
import egrep.main.exceptions.AutomatonException;
import egrep.main.exceptions.RegexMatchingException;

/**
 * This class implements the naive regex search
 *
 * @author Emilie SIAU
 * @author Hugo GUERRIER
 */
public class NaiveStrategy implements SearchStrategy {

    // ----- Override methods -----

    /**
     * Tell if the input corresponds to the regex-base automaton, using the naive strategy
     *
     * @param automaton The automaton to use, built from a regex
     * @param input The input string
     * @return true if the input matches with the automaton (built from the regex), false otherwise
     * @throws AutomatonException if the automaton encounters a problem (not deterministic automaton)
     */
    @Override
    public boolean isMatching(Automaton automaton, String input) throws AutomatonException {
        // Reset the automaton securely
        automaton.reset();

        // Prepare the working variables
        int cursor = 0;

        // Iterate over all the input
        while(cursor < input.length()) {
            // Init the next input to the current cursor
            int nextInput = cursor;

            try {

                // Input the next character in the automaton
                while(nextInput < input.length()) {
                    if(automaton.input(input.charAt(nextInput))) return true;
                    nextInput++;
                }

            } catch(RegexMatchingException ignored) {
            } finally {
                // Reset the automaton
                automaton.reset();

                // Increase the cursor
                cursor++;
            }
        }

        // The default result, if the input cannot match
        return automaton.isInFinalState();
    }

}
