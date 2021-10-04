package egrep.main.search_engine;

import egrep.main.automaton.Automaton;
import egrep.main.exceptions.AutomatonException;
import egrep.main.exceptions.CharacterException;

/**
 * Interface used to abstract searching methods, uses the strategy design pattern
 *
 * @author Emilie SIAU
 * @author Hugo GUERRIER
 */
public interface SearchStrategy {
    boolean isMatching(Automaton automaton, String input) throws AutomatonException, CharacterException;
}
