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


        } catch(Exception e) {
            fail(e);
        }
    }
 
}
