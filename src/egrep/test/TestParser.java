package egrep.test;

import egrep.main.parser.RegExParser;
import egrep.main.parser.RegExTree;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("A test suite for the RegEx parser")
public class TestParser {

    /**
     * Check the test suite availability
     */
    @Test
    void available() {
        assertTrue(true);
    }

    /**
     * Test the Aho Ullman tree parsing
     */
    @Test
    void testAhoUllman() {
        // Define the regex and prepare the parser
        String regex = "a|bc*";
        RegExParser parser = new RegExParser(regex);

        try {

            // Get the result tree
            RegExTree tree = parser.parse();

            // Verify the tree structure
            assertEquals(RegExParser.ALTERN, tree.getRoot());
            assertEquals(2, tree.getSubTrees().size());
            assertEquals('a', tree.getSubTrees().get(0).getRoot());

            RegExTree concat = tree.getSubTrees().get(1);
            assertEquals(2, concat.getSubTrees().size());
            assertEquals(RegExParser.CONCAT, concat.getRoot());
            assertEquals('b', concat.getSubTrees().get(0).getRoot());

            RegExTree star = concat.getSubTrees().get(1);
            assertEquals(1, star.getSubTrees().size());
            assertEquals(RegExParser.STAR, star.getRoot());
            assertEquals('c', star.getSubTrees().get(0).getRoot());

        } catch(Exception e) {
            fail(e);
        }
    }

    /**
     * Test the concat symbol
     */
    @Test
    void testConcat() {
        // Define the regex and prepare the parser
        String regex = "ab";
        RegExParser parser = new RegExParser(regex);

        try {

            // Get the result tree
            RegExTree tree = parser.parse();

            // Test the tree
            assertEquals(RegExParser.CONCAT, tree.getRoot());
            assertEquals(2, tree.getSubTrees().size());
            assertEquals('a', tree.getSubTrees().get(0).getRoot());
            assertEquals('b', tree.getSubTrees().get(1).getRoot());

        } catch(Exception e) {
            fail(e);
        }
    }

    /**
     * Test the dot
     */
    @Test
    void testDot() {
        // Define the regex and prepare the parser
        String regex = ".";
        RegExParser parser = new RegExParser(regex);

        try {

            // Get the result tree
            RegExTree tree = parser.parse();

            // Test the tree
            assertEquals(RegExParser.DOT, tree.getRoot());
            assertEquals(0, tree.getSubTrees().size());

        } catch(Exception e) {
            fail(e);
        }
    }

    /**
     * Test the star symbol
     */
    @Test
    void testStar() {
        // Define the regex and prepare the parser
        String regex = "a*";
        RegExParser parser = new RegExParser(regex);

        try {

            // Get the result tree
            RegExTree tree = parser.parse();

            // Test the tree
            assertEquals(RegExParser.STAR, tree.getRoot());
            assertEquals(1, tree.getSubTrees().size());
            assertEquals('a', tree.getSubTrees().get(0).getRoot());

        } catch(Exception e) {
            fail(e);
        }
    }

    /**
     * Test the altern symbol
     */
    @Test
    void testAltern() {
        // Define the regex and prepare the parser
        String regex = "a|b";
        RegExParser parser = new RegExParser(regex);

        try {

            // Get the result tree
            RegExTree tree = parser.parse();

            // Test the tree
            assertEquals(RegExParser.ALTERN, tree.getRoot());
            assertEquals(2, tree.getSubTrees().size());
            assertEquals('a', tree.getSubTrees().get(0).getRoot());
            assertEquals('b', tree.getSubTrees().get(1).getRoot());

        } catch(Exception e) {
            fail(e);
        }
    }

}
