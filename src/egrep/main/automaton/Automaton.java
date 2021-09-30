package egrep.main.automaton;

import egrep.main.parser.RegExTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static egrep.main.parser.RegExParser.*;

/**
 * An automaton is represented by a Map of keys:NodeId and values:[259 NodeId]
 */
public class Automaton {

    // ----- Attributes -----

    private Map<NodeId, List<NodeId>[]> automaton;
    private RegExTree tree;
    private int cptNodes;

    // ----- Constructors -----

    public Automaton(RegExTree tree) {
        this.tree = tree;
        automaton = new HashMap<>();
        process(tree, -1);
    }

    // ----- Methods -----

    public void process(RegExTree curr_tree, int imposedId) {
        /*
            NodeId startNode = new NodeId();
            startNode.addKey(0);
            List<NodeId>[] startNodeList = new List[259];
            automaton.put(startNode, startNodeList);
        */
        switch (curr_tree.getRoot()) {
            case ALTERN:

                break;
            case CONCAT:
                break;
            case STAR:
                break;
        }

    }

}
