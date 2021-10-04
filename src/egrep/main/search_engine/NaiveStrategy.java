package egrep.main.search_engine;

import egrep.main.automaton.Automaton;
import egrep.main.exceptions.AutomatonException;
import egrep.main.exceptions.CharacterException;
import egrep.main.exceptions.RegexMatchingException;

/**
 * This class implements the naive regex search
 *
 * @author Emilie SIAU
 * @author Hugo GUERRIER
 */
public class NaiveStrategy implements SearchStrategy {

    // ----- Override methods -----

    @Override
    public boolean isMatching(Automaton automaton, String input) throws AutomatonException, CharacterException {
        // Prepare the working variables
        int cursor = 0;

        // Iterate over all the input
        while(cursor < input.length()) {
            int nextInput = cursor;

            try {

                // Input the next character in the automaton
                while(nextInput < input.length()) {
                    if(automaton.input(input.charAt(nextInput))) return true;
                    nextInput++;
                }

                // Reset the automaton
                automaton.reset();

            } catch (RegexMatchingException e) {
                return false;
            } finally {
                cursor++;
            }

        }

        // The default result, if the input cannot match
        return false;
    }

}
