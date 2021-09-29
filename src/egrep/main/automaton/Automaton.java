package egrep.main.automaton;

import egrep.main.parser.RegExTree;

import java.util.HashMap;
import java.util.Map;

/**
 * An automaton is represented by a Map of keys:NodeId and values:[259 NodeId]
 */
public class Automaton {

    // ----- Attributes -----

    private Map<NodeId, NodeId[]> automaton;
    private RegExTree tree;
    private int cptNodes;

    // ----- Constructors -----

    public Automaton(RegExTree tree) {
        this.tree = tree;
        automaton = new HashMap<>();
        cptNodes = 0;
        process();
    }

    public void process() {

    }

}
