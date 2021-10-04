package egrep.main.automaton;

import egrep.main.parser.RegExTree;
import egrep.main.utils.Pair;

import java.util.*;

import static egrep.main.parser.RegExParser.*;

/**
 * An automaton is represented by a Map of keys:NodeId and values:[259 NodeId]
 */
public class Automaton {

    // ----- Macros -----

    private static final int AUTOMATON_TABLE_SIZE = 259;
    private static final int CHAR_NUMBER = 256;
    private static final int EPSILON_POS = 256;
    private static final int INIT_POS = 257;
    private static final int ACCEPT_POS = 258;

    // ----- Attributes -----

    private final RegExTree tree;
    private Map<NodeId, ArrayList<List<NodeId>>> automaton;
    private NodeId initNode;
    private NodeId ndfaFinalNode;
    private int nextNodeId;
    private final List<Pair<Set<NodeId>, NodeId>> nodeIdInstances;
    private boolean determinist;

    // ----- Constructors -----

    /**
     * Init an automaton with the wanted regex tree
     *
     * @param t The regex tree
     */
    public Automaton(RegExTree t) {
        tree = t;
        automaton = null;
        initNode = null;
        ndfaFinalNode = null;
        nextNodeId = 0;
        nodeIdInstances = new LinkedList<>();
        determinist = false;
    }

    // ----- Override methods -----

    @Override
    public String toString() {
        // Prepare the string builder
        StringBuilder res = new StringBuilder();

        // Create a counter to avoid last \n
        int nodeCpt = 0;

        // For every node, display the transition
        for(NodeId currNode : automaton.keySet()) {
            res.append(currNode.getKeys().toString()).append(" {");

            // Create the transitions string for all characters
            StringBuilder transitionString = new StringBuilder();
            int transCpt = 0;
            for(int i = 0 ; i < CHAR_NUMBER ; i ++) {
                List<NodeId> targets = automaton.get(currNode).get(i);
                if(targets != null) {
                    if(transCpt > 0) transitionString.append(", ");
                    transitionString.append((char) i).append(" -> ").append(targets);
                    transCpt++;
                }
            }

            // Process the epsilon transition
            List<NodeId> epsilonTargets = automaton.get(currNode).get(EPSILON_POS);
            if(epsilonTargets != null) {
                if(transCpt > 0) transitionString.append(", ");
                transitionString.append("EPS -> ").append(epsilonTargets);
                transCpt++;
            }

            // Process the init and accept
            if(automaton.get(currNode).get(INIT_POS) != null) {
                if(transCpt > 0) transitionString.append(", ");
                transitionString.append("INIT = 1");
                transCpt++;
            }

            if(automaton.get(currNode).get(ACCEPT_POS) != null) {
                if(transCpt > 0) transitionString.append(", ");
                transitionString.append("ACCEPT = 1");
            }

            // Add the result to the general result
            res.append(transitionString);
            res.append("}");

            // Add the new line
            nodeCpt++;
            if(nodeCpt < automaton.keySet().size()) res.append('\n');
        }

        // Return the result
        return res.toString();
    }

    // ----- Class methods -----

    /**
     * Create the full automaton
     *
     * @throws Exception If there is an exception during the automaton creation
     */
    public void create() throws Exception {
        createNDFA();
        createDFA();
    }

    /**
     * Create the Non-Deterministic Finite Automaton for the provided regex tree
     *
     * @throws Exception If there is an exception during the automaton creation
     */
    public void createNDFA() throws Exception {
        // Verify that the automaton is not already created
        if(automaton == null) {
            // Instantiate the automaton
            automaton = new HashMap<>();

            // Create the final node
            nextNodeId = 0;
            NodeId finalNode = getNextNodeId();

            // Set the final transitions
            ArrayList<List<NodeId>> finalTransitions = getNewTransitionList();

            // Set the final position to an empty linked list to represent the true
            finalTransitions.set(ACCEPT_POS, new LinkedList<>());
            automaton.put(finalNode, finalTransitions);

            // Start the automaton creation
            NodeId initialNode = processNDFA(tree, finalNode);

            // Set the initial state, same as the final state
            automaton.get(initialNode).set(INIT_POS, new LinkedList<>());

            // Save the initial and final node of the ndfa
            initNode = initialNode;
            ndfaFinalNode = finalNode;
        }
    }

    /**
     * Determine the non-deterministic automaton
     */
    public void createDFA() {
        if(!determinist) {
            // Reset the counter and process the automaton
            nextNodeId = 0;
            processDFA();

            // Set the automaton determinist
            determinist = true;
        }
    }

    /**
     * Reset the automaton by removing all state and transitions
     */
    public void reset() {
        automaton = null;
        initNode = null;
        ndfaFinalNode = null;
        nodeIdInstances.clear();
        determinist = false;
        nextNodeId = 0;
    }

    // ----- Internal methods -----

    /**
     * Get the next node id
     *
     * @return The next node id
     */
    private NodeId getNextNodeId() {
        NodeId res = new NodeId();
        res.addKey(nextNodeId++);
        return res;
    }

    /**
     * Create an return a new transition list
     *
     * @return The trnasition list with every transition to null
     */
    private ArrayList<List<NodeId>> getNewTransitionList() {
        ArrayList<List<NodeId>> res = new ArrayList<>();
        for(int i = 0 ; i < AUTOMATON_TABLE_SIZE ; i++) {
            res.add(null);
        }
        return res;
    }

    /**
     * Create the Non-Deterministic Finite Automaton (NDFA) from the regex tree
     *
     * @param currTree The current tree to process recursively
     * @param finalNode The node where to go to
     * @return The initial node id of the created automaton
     * @throws Exception If the passed tree is null
     */
    private NodeId processNDFA(RegExTree currTree, NodeId finalNode) throws Exception {
        // If the current tree is null, throw an exception
        if(currTree == null) {
            throw new Exception("Error during automaton creation : null tree cannot be proccess");
        }

        // Prepare the node id to return
        NodeId entryNode = getNextNodeId();

        // Declaration of utility variables for the switch
        NodeId leftNode;
        NodeId rightNode;
        NodeId nextNode;
        List<NodeId> epsilon;
        ArrayList<List<NodeId>> transitions;
        ArrayList<List<NodeId>> nextTransitions;

        // Process the current tree
        switch (currTree.getRoot()) {
            case ALTERN:
                // Process on the two subtrees. They need to join on the same final node
                leftNode = processNDFA(currTree.getSubTrees().get(0), finalNode);
                rightNode = processNDFA(currTree.getSubTrees().get(1), finalNode);

                // Create the node id list corresponding to the transitions of the current node
                transitions = getNewTransitionList();

                // Make epsilon transitions pointing the subtrees
                epsilon = new LinkedList<>();
                epsilon.add(leftNode);
                epsilon.add(rightNode);
                transitions.set(EPSILON_POS, epsilon);

                // The node is ready to be put in the automaton
                automaton.put(entryNode, transitions);
                break;

            case CONCAT:
                // Process on the right subtree first
                rightNode = processNDFA(currTree.getSubTrees().get(1), finalNode);

                // Process on the left subtree then. Its final node is the right subtree
                leftNode = processNDFA(currTree.getSubTrees().get(0), rightNode);

                // The returned node is the left node. We are not creating a new row in the automaton table
                entryNode = leftNode;
                break;

            case STAR:
                // Prepare the 3 other nodes that are necessary to represent the star (the entry node is already created)
                NodeId loopEntryNode;
                NodeId loopExitNode = getNextNodeId();
                NodeId exitNode = getNextNodeId();

                // --- LOOP ENTRY NODE:

                // Process on the only one subtree which is the loop entry. Its final node corresponds to the exit of the loop
                loopEntryNode = processNDFA(currTree.getSubTrees().get(0), loopExitNode);

                // --- ENTRY NODE:

                // Create the node id list corresponding to the transitions of the entry node
                transitions = getNewTransitionList();

                // It has epsilon transitions to the loop entry node & the exit node
                epsilon = new LinkedList<>();
                epsilon.add(loopEntryNode);
                epsilon.add(exitNode);
                transitions.set(EPSILON_POS, epsilon);

                // The node is ready to be put in the automaton
                automaton.put(entryNode, transitions);

                // --- LOOP EXIT NODE:

                // Create the node id list corresponding to the transitions of the loop exit node
                transitions = getNewTransitionList();

                // It has epsilon transitions to the loop entry node & the exit node
                epsilon = new LinkedList<>();
                epsilon.add(loopEntryNode);
                epsilon.add(exitNode);
                transitions.set(EPSILON_POS, epsilon);

                // The node is ready to be put in the automaton
                automaton.put(loopExitNode, transitions);

                // --- EXIT NODE:

                // Create the node id list corresponding to the transitions of the exit node
                transitions = getNewTransitionList();

                // It has an epsilon transition to the final node
                epsilon = new LinkedList<>();
                epsilon.add(finalNode);
                transitions.set(EPSILON_POS, epsilon);

                // The node is ready to be put in the automaton
                automaton.put(exitNode, transitions);
                break;

            case DOT:
                // Create the out node
                nextNode = getNextNodeId();

                // Create the next node transitions
                nextTransitions = getNewTransitionList();
                epsilon = new LinkedList<>();
                epsilon.add(finalNode);
                nextTransitions.set(EPSILON_POS, epsilon);

                // Set the node transitions
                transitions = getNewTransitionList();
                for(int i = 0 ; i < CHAR_NUMBER ; i++) {
                    List<NodeId> currChar = new LinkedList<>();
                    currChar.add(nextNode);
                    transitions.set(i, currChar);
                }

                // Put the nodes in the automaton
                automaton.put(entryNode, transitions);
                automaton.put(nextNode, nextTransitions);
                break;

            default:
                // Get the character value
                int charIndex = currTree.getRoot();

                // Create the out node
                nextNode = getNextNodeId();

                // Create the next node transitions
                nextTransitions = getNewTransitionList();
                epsilon = new LinkedList<>();
                epsilon.add(finalNode);
                nextTransitions.set(EPSILON_POS, epsilon);

                // Set the node transitions
                transitions = getNewTransitionList();
                List<NodeId> character = new LinkedList<>();
                character.add(nextNode);
                transitions.set(charIndex, character);

                // Put the nodes in the automaton
                automaton.put(entryNode, transitions);
                automaton.put(nextNode, nextTransitions);
        }

        // Return the built entry node
        return entryNode;
    }


    /**
     * Get the unique instance of node id for a given node id set
     *
     * @param nodeIdSet The node id set
     * @return The instance of node id
     */
    private NodeId getNodeIdForSet(Set<NodeId> nodeIdSet) {
        // Look for the node id instance
        for(Pair<Set<NodeId>, NodeId> pair : nodeIdInstances) {
            if(pair.getKey().equals(nodeIdSet)) return pair.getValue();
        }

        // If nothing was found, create it
//        Pair<Set<NodeId>, NodeId> newPair = new Pair<>(nodeIdSet, NodeId.fromCollection(nodeIdSet));
        Pair<Set<NodeId>, NodeId> newPair = new Pair<>(nodeIdSet, getNextNodeId());
        nodeIdInstances.add(newPair);
        return newPair.getValue();
    }

    /**
     * Get the epsilon closure for a wanted node id list
     *
     * @param nodeIdList The node id list to do the epsilon closure on
     * @return The epsilon closure in a list
     */
    private Set<NodeId> getEpsilonClosure(List<NodeId> nodeIdList) {
        // Prepare the result
        Set<NodeId> res = new HashSet<>();

        // Prepare the working list
        List<NodeId> processList = new LinkedList<>(nodeIdList);

        // Process the epsilon closure
        while(!processList.isEmpty()) {
            NodeId currentNode = processList.remove(0);
            if(res.add(currentNode)) {
                List<NodeId> epsilonTrans = automaton.get(currentNode).get(EPSILON_POS);
                if(epsilonTrans != null) processList.addAll(epsilonTrans);
            }
        }

        // Return the result
        return res;
    }

    /**
     * Determine the current automaton using an alternate version of the sub-set method from Aho Ullman book
     */
    private void processDFA() {
        // Create the new map which contains the DFA
        Map<NodeId, ArrayList<List<NodeId>>> dfa = new HashMap<>();

        // Create the working variables
        List<Set<NodeId>> processList = new LinkedList<>();

        // Init process list by entering the ndfa from the epsilon closure of the init node
        List<NodeId> initList = new LinkedList<>();
        initList.add(initNode);
        Set<NodeId> initSet = getEpsilonClosure(initList);

        // Add the init list in the process list
        processList.add(initSet);

        // While the process list is not empty, do the process
        while(!processList.isEmpty()) {
            // Get the current list
            Set<NodeId> currentSet = processList.remove(0);
            NodeId currentNode = getNodeIdForSet(currentSet);

            // If the node already exists in the dfa, pass the current list
            if(dfa.getOrDefault(currentNode, null) == null) {

                // Create the new transition list
                ArrayList<List<NodeId>> newTransitions = getNewTransitionList();

                // Add all targets from the sub nodes
                for(NodeId subNode : currentSet) {
                    for(int i = 0 ; i < CHAR_NUMBER ; i++) {
                        List<NodeId> targets = automaton.get(subNode).get(i);
                        if(targets != null) {
                            if(newTransitions.get(i) == null) newTransitions.set(i, new LinkedList<>());
                            newTransitions.get(i).addAll(targets);
                        }
                    }
                }

                // Do the epsilon closure for each transition
                for(int i = 0 ; i < CHAR_NUMBER ; i++) {
                    List<NodeId> newTargets = newTransitions.get(i);
                    if(newTargets != null) {
                        // Get the epsilon closure
                        Set<NodeId> epsilon = getEpsilonClosure(newTargets);

                        // Add the set to the process list
                        processList.add(epsilon);

                        // Set the transition
                        newTargets.clear();
                        newTargets.add(getNodeIdForSet(epsilon));
                    }
                }

                // Set init and accept
                if(currentSet.contains(initNode)) {
                    newTransitions.set(INIT_POS, new LinkedList<>());
                    initNode = currentNode;
                }

                if(currentSet.contains(ndfaFinalNode)) {
                    newTransitions.set(ACCEPT_POS, new LinkedList<>());
                }

                // Add the new node and transitions to the dfa
                dfa.put(currentNode, newTransitions);

            }
        }

        // Affect the DFA to the automaton
        automaton = dfa;
    }

}
