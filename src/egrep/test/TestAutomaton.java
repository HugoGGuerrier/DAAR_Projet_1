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

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("A test suite for the automaton and all its methods")
public class TestAutomaton {

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

    }
}
