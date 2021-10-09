package egrep.test;

import egrep.main.automaton.Automaton;
import egrep.main.automaton.NodeId;
import egrep.main.parser.RegExParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("A test suite for the DFA creation")
public class TestDFA {

    /**
     * Check the test suite availability
     */
    @Test
    void available() {
        assertTrue(true);
    }

    /**
     * Test the DFA creation on the aho ullman example
     */
    @Test
    void testAhoUllman() {
        try {

            // Create the DFA and test its structure
            Automaton automaton = new Automaton(RegExParser.exampleAhoUllman(), false);
            automaton.createNDFA();
            automaton.createDFA();
            Map<NodeId, ArrayList<List<NodeId>>> map = automaton.getAutomaton();
            NodeId currNode = automaton.getInitNode();

            assertNotNull(map.get(currNode).get('a'));
            assertNotNull(map.get(currNode).get('b'));
            assertEquals(1, map.get(currNode).get('a').size());
            assertEquals(1, map.get(currNode).get('b').size());
            NodeId leftNode = map.get(currNode).get('a').get(0);
            NodeId rightNode = map.get(currNode).get('b').get(0);

            currNode = leftNode;

            assertNotNull(map.get(currNode).get(Automaton.ACCEPT_POS));

            currNode = rightNode;

            assertNotNull(map.get(currNode).get(Automaton.ACCEPT_POS));

            assertNotNull(map.get(currNode).get('c'));
            assertEquals(1, map.get(currNode).get('c').size());
            currNode = map.get(currNode).get('c').get(0);

            assertNotNull(map.get(currNode).get(Automaton.ACCEPT_POS));

            assertNotNull(map.get(currNode).get('c'));
            assertEquals(1, map.get(currNode).get('c').size());
            assertEquals(currNode, map.get(currNode).get('c').get(0));

        } catch(Exception e) {
            fail(e);
        }
    }

    /**
     * Test the DFA creation for the concatenation
     */
    @Test
    void testConcat() {
        // Prepare the regex and the parser
        String regex = "ab";
        RegExParser parser = new RegExParser(regex);

        try {

            // Create the DFA and test its structure
            Automaton automaton = new Automaton(parser.parse(), false);
            automaton.createNDFA();
            automaton.createDFA();
            Map<NodeId, ArrayList<List<NodeId>>> map = automaton.getAutomaton();
            NodeId currNode = automaton.getInitNode();

            assertNotNull(map.get(currNode).get('a'));
            assertEquals(1, map.get(currNode).get('a').size());
            currNode = map.get(currNode).get('a').get(0);

            assertNull(map.get(currNode).get(Automaton.ACCEPT_POS));

            assertNotNull(map.get(currNode).get('b'));
            assertEquals(1, map.get(currNode).get('b').size());
            currNode = map.get(currNode).get('b').get(0);

            assertNotNull(map.get(currNode).get(Automaton.ACCEPT_POS));

        } catch(Exception e) {
            fail(e);
        }
    }

    /**
     * Test the DFA creation for the dot
     */
    @Test
    void testDot() {
        // Prepare the regex and the parser
        String regex = ".";
        RegExParser parser = new RegExParser(regex);

        try {

            // Create the DFA and test its structure
            Automaton automaton = new Automaton(parser.parse(), false);
            automaton.createNDFA();
            automaton.createDFA();
            Map<NodeId, ArrayList<List<NodeId>>> map = automaton.getAutomaton();
            NodeId currNode = automaton.getInitNode();

            assertNotNull(map.get(currNode).get(Automaton.DOT_POS));
            assertEquals(1, map.get(currNode).get(Automaton.DOT_POS).size());
            currNode = map.get(currNode).get(Automaton.DOT_POS).get(0);

            assertNotNull(map.get(currNode).get(Automaton.ACCEPT_POS));

        } catch(Exception e) {
            fail(e);
        }
    }

    /**
     * Test the DFA creation for the star
     */
    @Test
    void testStar() {
        // Prepare the regex and the parser
        String regex = "a*";
        RegExParser parser = new RegExParser(regex);

        try {

            // Create the DFA and test its structure
            Automaton automaton = new Automaton(parser.parse(), false);
            automaton.createNDFA();
            automaton.createDFA();
            Map<NodeId, ArrayList<List<NodeId>>> map = automaton.getAutomaton();
            NodeId currNode = automaton.getInitNode();

            assertNotNull(map.get(currNode).get(Automaton.ACCEPT_POS));

            assertNotNull(map.get(currNode).get('a'));
            assertEquals(1, map.get(currNode).get('a').size());
            currNode = map.get(currNode).get('a').get(0);

            assertNotNull(map.get(currNode).get(Automaton.ACCEPT_POS));

            assertNotNull(map.get(currNode).get('a'));
            assertEquals(1, map.get(currNode).get('a').size());
            assertEquals(currNode, map.get(currNode).get('a').get(0));

        } catch(Exception e) {
            fail(e);
        }
    }

    /**
     * Test the DFA creation for the altern
     */
    @Test
    void testAltern() {
        // Prepare the regex and the parser
        String regex = "a|b";
        RegExParser parser = new RegExParser(regex);

        try {

            // Create the DFA and test its structure
            Automaton automaton = new Automaton(parser.parse(), false);
            automaton.createNDFA();
            automaton.createDFA();
            Map<NodeId, ArrayList<List<NodeId>>> map = automaton.getAutomaton();
            NodeId currNode = automaton.getInitNode();

            assertNotNull(map.get(currNode).get('a'));
            assertNotNull(map.get(currNode).get('b'));
            assertEquals(1, map.get(currNode).get('a').size());
            assertEquals(1, map.get(currNode).get('b').size());
            NodeId leftNode = map.get(currNode).get('a').get(0);
            NodeId rightNode = map.get(currNode).get('b').get(0);

            currNode = leftNode;

            assertNotNull(map.get(currNode).get(Automaton.ACCEPT_POS));

            currNode = rightNode;

            assertNotNull(map.get(currNode).get(Automaton.ACCEPT_POS));

        } catch(Exception e) {
            fail(e);
        }
    }
}
