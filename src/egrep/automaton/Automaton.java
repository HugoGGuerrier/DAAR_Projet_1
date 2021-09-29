package egrep.automaton;

import egrep.parser.RegExTree;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
