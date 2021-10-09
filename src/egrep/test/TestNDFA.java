package egrep.test;

import egrep.main.automaton.Automaton;
import egrep.main.automaton.NodeId;
import egrep.main.parser.RegExParser;
import egrep.main.parser.RegExTree;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("A test suite for the NDFA creation")
public class TestNDFA {

    /**
     * Check the test suite availability
     */
    @Test
    void available() {
        assertTrue(true);
    }

    @Test
    void testAhoUllman() {
        RegExTree tree = RegExParser.exampleAhoUllman();
        // TODO : Suite
    }

    @Test
    void testConcat() {
        // Prepare the regex and the parser
        String regex = "ab";
        RegExParser parser = new RegExParser(regex);

        try {

            // Get the ndfa for this regex and test its structure
            Automaton automaton = new Automaton(parser.parse(), false);
            automaton.createNDFA();
            Map<NodeId, ArrayList<List<NodeId>>> map = automaton.getAutomaton();
            NodeId currNode = automaton.getInitNode();

            assertNotNull(map.get(currNode).get('a'));
            assertEquals(1, map.get(currNode).get('a').size());
            currNode = map.get(currNode).get('a').get(0);

            assertNotNull(map.get(currNode).get(Automaton.EPSILON_POS));
            assertEquals(1, map.get(currNode).get(Automaton.EPSILON_POS).size());
            currNode = map.get(currNode).get(Automaton.EPSILON_POS).get(0);

            assertNotNull(map.get(currNode).get('b'));
            assertEquals(1, map.get(currNode).get('b').size());
            currNode = map.get(currNode).get('b').get(0);

            assertNotNull(map.get(currNode).get(Automaton.EPSILON_POS));
            assertEquals(1, map.get(currNode).get(Automaton.EPSILON_POS).size());
            currNode = map.get(currNode).get(Automaton.EPSILON_POS).get(0);

            assertNotNull(map.get(currNode).get(Automaton.ACCEPT_POS));

        } catch(Exception e) {
            fail(e);
        }
    }

    @Test
    void testDot() {
        // Prepare the regex and the parser
        String regex = ".";
        RegExParser parser = new RegExParser(regex);

        try {

            // Get the ndfa for this regex and test its structure
            Automaton automaton = new Automaton(parser.parse(), false);
            automaton.createNDFA();
            Map<NodeId, ArrayList<List<NodeId>>> map = automaton.getAutomaton();
            NodeId currNode = automaton.getInitNode();

            assertNotNull(map.get(currNode).get(Automaton.DOT_POS));
            assertEquals(1, map.get(currNode).get(Automaton.DOT_POS).size());
            currNode = map.get(currNode).get(Automaton.DOT_POS).get(0);

            assertNotNull(map.get(currNode).get(Automaton.EPSILON_POS));
            assertEquals(1, map.get(currNode).get(Automaton.EPSILON_POS).size());
            currNode = map.get(currNode).get(Automaton.EPSILON_POS).get(0);

            assertNotNull(map.get(currNode).get(Automaton.ACCEPT_POS));

        } catch(Exception e) {
            fail(e);
        }
    }

    @Test
    void testStar() {
        // Prepare the regex and the parser
        String regex = "a*";
        RegExParser parser = new RegExParser(regex);

        try {

            // Get the ndfa for this regex and test its structure
            Automaton automaton = new Automaton(parser.parse(), false);
            automaton.createNDFA();
            Map<NodeId, ArrayList<List<NodeId>>> map = automaton.getAutomaton();
            NodeId currNode = automaton.getInitNode();

            assertNotNull(map.get(currNode).get(Automaton.EPSILON_POS));
            assertEquals(2, map.get(currNode).get(Automaton.EPSILON_POS).size());
            NodeId loopEntryNode = map.get(currNode).get(Automaton.EPSILON_POS).get(0);
            NodeId exitNode = map.get(currNode).get(Automaton.EPSILON_POS).get(1);
            currNode = map.get(currNode).get(Automaton.EPSILON_POS).get(0);

            assertNotNull(map.get(currNode).get('a'));
            assertEquals(1, map.get(currNode).get('a').size());
            currNode = map.get(currNode).get('a').get(0);

            assertNotNull(map.get(currNode).get(Automaton.EPSILON_POS));
            assertEquals(1, map.get(currNode).get(Automaton.EPSILON_POS).size());
            currNode = map.get(currNode).get(Automaton.EPSILON_POS).get(0);

            assertNotNull(map.get(currNode).get(Automaton.EPSILON_POS));
            assertEquals(2, map.get(currNode).get(Automaton.EPSILON_POS).size());
            assertEquals(loopEntryNode, map.get(currNode).get(Automaton.EPSILON_POS).get(0));
            currNode = map.get(currNode).get(Automaton.EPSILON_POS).get(1);
            assertEquals(exitNode, currNode);

            assertNotNull(map.get(currNode).get(Automaton.EPSILON_POS));
            assertEquals(1, map.get(currNode).get(Automaton.EPSILON_POS).size());
            currNode = map.get(currNode).get(Automaton.EPSILON_POS).get(0);

            assertNotNull(map.get(currNode).get(Automaton.ACCEPT_POS));

        } catch(Exception e) {
            fail(e);
        }
    }

    @Test
    void testAltern() {
        // Prepare the regex and the parser
        String regex = "a|b";
        RegExParser parser = new RegExParser(regex);

        try {

            // Get the ndfa for this regex and test its structure
            Automaton automaton = new Automaton(parser.parse(), false);
            automaton.createNDFA();
            Map<NodeId, ArrayList<List<NodeId>>> map = automaton.getAutomaton();
            NodeId currNode = automaton.getInitNode();

            

        } catch(Exception e) {
            fail(e);
        }
    }
}
