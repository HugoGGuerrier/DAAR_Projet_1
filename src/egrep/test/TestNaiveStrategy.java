package egrep.test;

import egrep.main.automaton.Automaton;
import egrep.main.parser.RegExParser;
import egrep.main.search_engine.NaiveStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("A test suite for the naive search strategy")
public class TestNaiveStrategy {

    /**
     * Check the test suite availability
     */
    @Test
    void available() {
        assertTrue(true);
    }

    /**
     * Test for the aho ullman (a|bc*) example match
     */
    @Test
    void testAhoUllman() {
        try {

            // Create the automaton and the strategy
            Automaton automaton = new Automaton(RegExParser.exampleAhoUllman());
            NaiveStrategy strategy = new NaiveStrategy();

            assertTrue(strategy.isMatching(automaton, "ac"));
            assertTrue(strategy.isMatching(automaton, "bc"));
            assertTrue(strategy.isMatching(automaton, "a"));
            assertTrue(strategy.isMatching(automaton, "b"));
            assertTrue(strategy.isMatching(automaton, "ab"));
            assertTrue(strategy.isMatching(automaton, "accccc"));
            assertTrue(strategy.isMatching(automaton, "bccccc"));

            assertFalse(strategy.isMatching(automaton, ""));
            assertFalse(strategy.isMatching(automaton, "c"));
            assertFalse(strategy.isMatching(automaton, "Coucou"));

        } catch(Exception e) {
            fail(e);
        }
    }

    /**
     * Test the marching with the concatenation
     */
    @Test
    void testConcat() {
        // Define the regex and the parser
        String regex = "coucou";
        RegExParser parser = new RegExParser(regex);

        try {

            Automaton automaton = new Automaton(parser.parse());
            NaiveStrategy strategy = new NaiveStrategy();

            assertTrue(strategy.isMatching(automaton, "coucou"));
            assertTrue(strategy.isMatching(automaton, "coucouaaa"));
            assertTrue(strategy.isMatching(automaton, "aaacoucou"));
            assertTrue(strategy.isMatching(automaton, "This is a coucou"));

            assertFalse(strategy.isMatching(automaton, ""));
            assertFalse(strategy.isMatching(automaton, "Coucou"));
            assertFalse(strategy.isMatching(automaton, "coUcou"));
            assertFalse(strategy.isMatching(automaton, "cou cou"));
            assertFalse(strategy.isMatching(automaton, "Rien à voir"));

        } catch(Exception e) {
            fail(e);
        }
    }

    /**
     * Test the matching with the dot
     */
    @Test
    void testDot() {
        // Define the regex and the parser
        String regex = ".";
        RegExParser parser = new RegExParser(regex);

        try {

            Automaton automaton = new Automaton(parser.parse());
            NaiveStrategy strategy = new NaiveStrategy();

            assertTrue(strategy.isMatching(automaton, "a"));
            assertTrue(strategy.isMatching(automaton, "test"));
            assertTrue(strategy.isMatching(automaton, " "));
            assertTrue(strategy.isMatching(automaton, "."));

            assertFalse(strategy.isMatching(automaton, ""));

        } catch(Exception e) {
            fail(e);
        }
    }

    /**
     * Test the matching with the star
     */
    @Test
    void testStar() {
        // Define the regex and the parser
        String regex = "a*";
        RegExParser parser = new RegExParser(regex);

        try {

            Automaton automaton = new Automaton(parser.parse());
            NaiveStrategy strategy = new NaiveStrategy();

            assertTrue(strategy.isMatching(automaton, ""));
            assertTrue(strategy.isMatching(automaton, "a"));
            assertTrue(strategy.isMatching(automaton, "aaaaaa"));
            assertTrue(strategy.isMatching(automaton, "Rien à voir"));

        } catch(Exception e) {
            fail(e);
        }
    }

    /**
     * Test the marching with the altern
     */
    @Test
    void testAltern() {
        // Define the regex and the parser
        String regex = "a|b";
        RegExParser parser = new RegExParser(regex);

        try {

            Automaton automaton = new Automaton(parser.parse());
            NaiveStrategy strategy = new NaiveStrategy();

            assertTrue(strategy.isMatching(automaton, "a"));
            assertTrue(strategy.isMatching(automaton, "b"));
            assertTrue(strategy.isMatching(automaton, "ab"));
            assertTrue(strategy.isMatching(automaton, "This is a test"));
            assertTrue(strategy.isMatching(automaton, "This is b test"));

            assertFalse(strategy.isMatching(automaton, ""));
            assertFalse(strategy.isMatching(automaton, "Nope"));

        } catch(Exception e) {
            fail(e);
        }
    }

    /**
     * Test the matching with the parenthesis
     */
    @Test
    void testParen() {
        // Define the regex and the parser
        String regex = "(ab)|(ac)";
        RegExParser parser = new RegExParser(regex);

        try {

            Automaton automaton = new Automaton(parser.parse());
            NaiveStrategy strategy = new NaiveStrategy();

            assertTrue(strategy.isMatching(automaton, "ab"));
            assertTrue(strategy.isMatching(automaton, "ac"));
            assertTrue(strategy.isMatching(automaton, "abcbbc"));
            assertTrue(strategy.isMatching(automaton, "accurate"));
            assertTrue(strategy.isMatching(automaton, "ability"));

            assertFalse(strategy.isMatching(automaton, ""));
            assertFalse(strategy.isMatching(automaton, "Nope"));
            assertFalse(strategy.isMatching(automaton, "bcause"));
            assertFalse(strategy.isMatching(automaton, "axe"));

        } catch(Exception e) {
            fail(e);
        }
    }

    /**
     * Test the matching with all the operators
     */
    @Test
    void testAll() {
        // Define the regex and the parser
        String regex = "(coucou!(!)*)|(ab.d)";
        RegExParser parser = new RegExParser(regex);

        try {

            Automaton automaton = new Automaton(parser.parse());
            NaiveStrategy strategy = new NaiveStrategy();

            assertTrue(strategy.isMatching(automaton, "coucou!"));
            assertTrue(strategy.isMatching(automaton, "coucou!!!!!!"));
            assertTrue(strategy.isMatching(automaton, "coucou!coucou"));
            assertTrue(strategy.isMatching(automaton, "coucou! comment ça va"));
            assertTrue(strategy.isMatching(automaton, "comment ça va coucou!!"));
            assertTrue(strategy.isMatching(automaton, "abcd"));
            assertTrue(strategy.isMatching(automaton, "abxd"));

            assertFalse(strategy.isMatching(automaton, ""));
            assertFalse(strategy.isMatching(automaton, "coucou"));
            assertFalse(strategy.isMatching(automaton, "Rien à voir"));
            assertFalse(strategy.isMatching(automaton, "abce"));
            assertFalse(strategy.isMatching(automaton, "abd"));

        } catch(Exception e) {
            fail(e);
        }
    }

}
