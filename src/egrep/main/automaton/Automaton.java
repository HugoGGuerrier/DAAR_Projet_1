package egrep.main.automaton;

import egrep.main.exceptions.AutomatonException;
import egrep.main.exceptions.CharacterException;
import egrep.main.exceptions.RegexMatchingException;
import egrep.main.parser.RegExTree;
import egrep.main.utils.Pair;

import java.nio.charset.CharacterCodingException;
import java.util.*;

import static egrep.main.parser.RegExParser.*;

/**
 * This class is the regex searching automaton
 * An automaton is represented by a Map of keys:NodeId and values:[260 NodeId]
 *
 * @author Emilie SIAU
 * @author Hugo GUERRIER
 */
public class Automaton {

    // ----- Macros -----

    private static final int AUTOMATON_TABLE_SIZE = 260;
    private static final int CHAR_NUMBER = 256;
    private static final int DOT_POS = 256;
    private static final int EPSILON_POS = 257;
    private static final int INIT_POS = 258;
    private static final int ACCEPT_POS = 259;

    // ----- Attributes -----

    // --- Data attributes
    private final RegExTree tree;

    // --- Attributes about the automaton
    private Map<NodeId, ArrayList<List<NodeId>>> automaton;
    private NodeId initNode;
    private NodeId ndfaFinalNode;
    private NodeId currentNode;

    // --- Utils attributes
    private int nextNodeId;
    private final List<Pair<Set<NodeId>, NodeId>> nodeIdInstances;
    private boolean determinist;

    // ----- Constructors -----

    /**
     * Create a new automaton from the regex tree and automatically process it to a DFA
     *
     * @param tree The tree to create the automaton from
     * @throws AutomatonException if the passed tree is null
     */
    public Automaton(RegExTree tree) throws AutomatonException {
        this(tree, true);
    }

    /**
     * Init an automaton with the wanted regex tree
     *
     * @param t The regex tree
     * @param autoCreate Create automatically the automaton or not (for benchmarking purpose)
     * @throws AutomatonException if the passed tree is null
     */
    public Automaton(RegExTree t, boolean autoCreate) throws AutomatonException {
        tree = t;
        automaton = null;
        initNode = null;
        ndfaFinalNode = null;
        currentNode = null;
        nextNodeId = 0;
        nodeIdInstances = new LinkedList<>();
        determinist = false;

        if(autoCreate) create();
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

            // Process the dot transtion
            List<NodeId> dotTargets = automaton.get(currNode).get(DOT_POS);
            if(dotTargets != null) {
                if(transCpt > 0) transitionString.append(", ");
                transitionString.append("DOT -> ").append(dotTargets);
                transCpt++;
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
     * @throws AutomatonException If there is an exception during the automaton creation
     */
    public void create() throws AutomatonException {
        createNDFA();
        createDFA();
    }

    /**
     * Create id needed the Non-Deterministic Finite Automaton for the provided regex tree
     *
     * @throws AutomatonException If there is an exception during the automaton creation
     */
    public void createNDFA() throws AutomatonException {
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
     * Determine if needed the non-deterministic automaton
     */
    public void createDFA() {
        if(!determinist) {
            // Reset the counter and process the automaton
            nextNodeId = 0;
            processDFA();

            // Set the automaton determinist and init it for the input
            determinist = true;
            currentNode = initNode;
        }
    }

    /**
     * Input a character in the automaton
     *
     * @param c The character to input
     * @return True if the automaton is in a final state after the input
     * @throws RegexMatchingException If the input cannot match the automaton
     * @throws AutomatonException If the automaton is not deterministic
     * @throws CharacterException If the character is not in the ASCII table
     */
    public boolean input(char c) throws RegexMatchingException, AutomatonException, CharacterException {
        // Test for the input in the automaton
        if(determinist) {
            if((int) c <= CHAR_NUMBER) {

                List<NodeId> nextNode = automaton.get(currentNode).get(c);
                List<NodeId> dotNode = automaton.get(currentNode).get(DOT_POS);
                if(nextNode != null) {
                    currentNode = nextNode.get(0);
                } else if(dotNode != null) {
                    currentNode = dotNode.get(0);
                } else {
                    throw new RegexMatchingException("Cannot match the input");
                }

            } else {
                throw new CharacterException("The input character need to be conform to the ASCII standard");
            }
        } else {
            throw new AutomatonException("The automaton need to be deterministic");
        }

        // Return if the automaton is in a final state
        return automaton.get(currentNode).get(ACCEPT_POS) != null;
    }

    /**
     * Reset the automaton from the inputs
     *
     * @throws AutomatonException If the automaton is not deterministic
     */
    public void reset() throws AutomatonException {
        if(determinist) {
            currentNode = initNode;
        } else {
            throw new AutomatonException("The automaton need to be deterministic");
        }
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
     * @throws AutomatonException If the passed tree is null
     */
    private NodeId processNDFA(RegExTree currTree, NodeId finalNode) throws AutomatonException {
        // If the current tree is null, throw an exception
        if(currTree == null) {
            throw new AutomatonException("Error during automaton creation : null tree cannot be proccess");
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
                List<NodeId> dotTargets = new LinkedList<>();
                dotTargets.add(nextNode);
                transitions.set(DOT_POS, dotTargets);

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

                    // Process dot targets
                    List<NodeId> dotTargets = automaton.get(subNode).get(DOT_POS);
                    if(dotTargets != null) {
                        if(newTransitions.get(DOT_POS) == null) newTransitions.set(DOT_POS, new LinkedList<>());
                        newTransitions.get(DOT_POS).addAll(dotTargets);
                    }
                }

                // Do the epsilon closure for each char transition
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

                // Do the epsilon closure for the dot
                List<NodeId> newDotTargets = newTransitions.get(DOT_POS);
                if(newDotTargets != null) {
                    // Get the epsilon closure
                    Set<NodeId> epsilon = getEpsilonClosure(newDotTargets);

                    // Add the set to the process list
                    processList.add(epsilon);

                    // Set the transition
                    newDotTargets.clear();
                    newDotTargets.add(getNodeIdForSet(epsilon));
                }

                // If the new node contains the init node, make it init
                if(currentSet.contains(initNode)) {
                    newTransitions.set(INIT_POS, new LinkedList<>());
                    initNode = currentNode;
                }

                // If the new node contains the NDFA finale node, make it final
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
