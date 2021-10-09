package egrep.test;

import egrep.main.search_engine.KMPStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("A test suite for the KMP algorithm")
public class TestKMP {


    /**
     * Check the test suite availability
     */
    @Test
    void available() {
        assertTrue(true);
    }

    /**
     * Test the validation methods for KMP
     */
    @Test
    void testIsValidKMP() {
        assertTrue(KMPStrategy.isValidKMP("abcdef!"));
        assertTrue(KMPStrategy.isValidKMP("A.B"));
        assertTrue(KMPStrategy.isValidKMP("A.(def)"));
        assertTrue(KMPStrategy.isValidKMP("\"123"));
        assertTrue(KMPStrategy.isValidKMP("ghijklmnopqrstuvwxyz"));

        assertFalse(KMPStrategy.isValidKMP("a*"));
        assertFalse(KMPStrategy.isValidKMP("a|b"));
    }

    /**
     * Test the carry over creation
     */
    @Test
    void testCarryOver() {
        KMPStrategy kmp = new KMPStrategy("mamamia");
        assertTrue(kmp.equalsCarryOver(new int[]{-1, 0, -1, 0, -1, 3, 0}));
    }

    @Test
    void testConcat() {
        // Prepare the regex
        String regex = "Babylon";
        assertTrue(KMPStrategy.isValidKMP(regex));

        KMPStrategy kmp = new KMPStrategy(regex);

        try {

            assertTrue(kmp.isMatching(null, "Babylon"));
            assertTrue(kmp.isMatching(null, "This is a Babylonian story"));

            assertFalse(kmp.isMatching(null, ""));
            assertFalse(kmp.isMatching(null, "Babylol"));
            assertFalse(kmp.isMatching(null, "Rien à voir"));

        } catch(Exception e) {
            fail(e);
        }
    }
}
