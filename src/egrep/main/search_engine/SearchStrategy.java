package egrep.main.search_engine;

import egrep.main.automaton.Automaton;

public interface SearchStrategy {
    boolean isMatching(Automaton automaton, String input);
}
