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


    @Test
    void testIsValidKMP() {
        assertTrue(KMPStrategy.isValidKMP("abcdef!"));
        assertTrue(KMPStrategy.isValidKMP("A.B"));
        assertTrue(KMPStrategy.isValidKMP("A.(def)"));
        assertTrue(KMPStrategy.isValidKMP("\"123"));
        assertTrue(KMPStrategy.isValidKMP("ghijklmnopqrstuvwxyz"));

        assertFalse(KMPStrategy.isValidKMP("a*"));
        assertFalse(KMPStrategy.isValidKMP("a|b"));
        //assertFalse(KMPStrategy.isValidKMP("o+"));
    }

    @Test
    void testCarryOut() {
        KMPStrategy kmp = new KMPStrategy("mamamia");
        kmp.equalsCarryOver(new int[]{-1, 0, -1, 0, -1, 3, 0});
    }

    @Test
    void testConcat() {
        // Prepare the regex
        String regex = "Babylon";
        assertTrue(KMPStrategy.isValidKMP(regex));

        KMPStrategy kmp = new KMPStrategy(regex);

        try {
            assertTrue(kmp.isMatching(null, regex));
        } catch(Exception e) {
            fail(e);
        }
    }
}
