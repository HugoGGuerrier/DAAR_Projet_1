package egrep.main.search_engine;

import egrep.main.automaton.Automaton;
import egrep.main.exceptions.AutomatonException;
import egrep.main.exceptions.CharacterException;

public interface SearchStrategy {
    boolean isMatching(Automaton automaton, String input) throws AutomatonException, CharacterException;
}
