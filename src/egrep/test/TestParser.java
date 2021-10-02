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
            assertEquals((int) 'a', tree.getSubTrees().get(0).getRoot());

            RegExTree concat = tree.getSubTrees().get(1);
            assertEquals(2, concat.getSubTrees().size());
            assertEquals(RegExParser.CONCAT, concat.getRoot());
            assertEquals((int) 'b', concat.getSubTrees().get(0).getRoot());

            RegExTree star = concat.getSubTrees().get(1);
            assertEquals(1, star.getSubTrees().size());
            assertEquals(RegExParser.STAR, star.getRoot());
            assertEquals((int) 'c', star.getSubTrees().get(0).getRoot());

        } catch(Exception e) {
            fail(e);
        }
    }

    /**
     * Test the concat symbol
     */
    @Test
    void testConcat() {

    }

    /**
     * Test the dot
     */
    @Test
    void testDot() {

    }

    /**
     * Test the star symbol
     */
    @Test
    void testStar() {

    }

    /**
     * Test the alter symbol
     */
    @Test
    void testAltern() {

    }
    
}
