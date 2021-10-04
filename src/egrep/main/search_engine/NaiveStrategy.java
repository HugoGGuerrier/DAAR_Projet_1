package egrep.main.search_engine;

import egrep.main.automaton.Automaton;

public class NaiveStrategy implements SearchStrategy {

    // ----- Attributes -----

    // ----- Constructors -----

    // ----- Override methods -----

    @Override
    public boolean isMatching(Automaton automaton, String input) {
        return false;
    }

}
