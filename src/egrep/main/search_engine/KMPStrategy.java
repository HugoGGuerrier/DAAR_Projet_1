package egrep.main.search_engine;

import egrep.main.automaton.Automaton;
import egrep.main.exceptions.AutomatonException;
import egrep.main.parser.RegExParser;

import java.util.HashSet;
import java.util.Set;

/**
 * This class implements the Knuth-Morris-Pratt (KMP) algorithm search on simple strings
 *
 * @author Emilie SIAU
 * @author Hugo GUERRIER
 */
public class KMPStrategy implements SearchStrategy {

    // ----- Attributes -----

    private final Set<Character> ignoredChars;
    private String factor;      // The regex factor representation
    private int[] carryOver;    // The carry over array

    // ----- Constructors -----

    public KMPStrategy(String regex) {
        // Build the ignored characters set
        ignoredChars = new HashSet<>();
        ignoredChars.add('(');
        ignoredChars.add(')');

        // Sanitize the string by ignoring those ignored characters
        sanitize(regex);

        // Create the carry over array from the sanitized string
        buildCarryOver();
    }

    // ----- Class methods -----

    /**
     * Verify if the given string is valid for using the KMP strategy : no handled operators,
     * only simple characters
     *
     * @param regex The string to verify
     * @return true if the string is valid, false if the string is not valid
     */
    public static boolean isValidKMP(String regex) {
        for (int i = 0 ; i < regex.length() ; i++) {
            for (int j = 0 ; j < RegExParser.HANDLED_OPERATORS.length ; j++) {
                if (regex.charAt(i) == RegExParser.HANDLED_OPERATORS[j]) return false;
            }
        }
        return true;
    }

    /**
     * Sanitize the string factor of the strategy
     */
    private void sanitize(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i < str.length() ; i++) {
            char c = str.charAt(i);
            if (!ignoredChars.contains(c)) sb.append(c);
        }
        factor = sb.toString();
    }

    /**
     * Build the carry over array for the KMP algorithm
     */
    private void buildCarryOver() {
        // Initialize the carry over array
        carryOver = new int[factor.length() + 1];
        carryOver[0] = -1;                     // First value is always -1
        carryOver[carryOver.length - 1] = 0;   // Last value is always 0

        // Iterate on the factor (F) (ignoring first character) to compute each corresponding value of the carry over (CO)
        for (int i = 1 ; i < factor.length() ; i++) {
            // CO[i] = size of the greatest suffix of F[1:i[ that is also a prefix of F

            // --- Compute all the suffixes for F[1:i[
            Set<String> suffixes = new HashSet<>();
            for (int j = 1 ; j < i ; j++) {
                suffixes.add(factor.substring(j, i-1));
            }

            // --- Match the suffixes as prefixes of F and pick the longest one
            int maxSize = 0;
            for (String suffix : suffixes) {
                if (factor.startsWith(suffix)) {
                    if (suffix.length() > maxSize) maxSize = suffix.length();
                }
            }

            carryOver[i] = maxSize;
        }

        // Optimizing the carry over values (eliminating redundancy)
        for (int i = 0 ; i < factor.length() ; i++) {
            if (factor.charAt(i) == factor.charAt(carryOver[i]) && carryOver[i] >= 0) {
                carryOver[i] = carryOver[carryOver[i]];
            }
        }
    }

    // ----- Override methods -----

    /**
     * Tell if the input corresponds to the regex, using the KMP strategy
     *
     * @param ignored The automaton is ignored. You can give a null automaton
     * @param input The input string
     * @return true if the input matches with the regex, false otherwise
     * @throws AutomatonException Cannot happen. Does not use automaton (ignored)
     */
    @Override
    public boolean isMatching(Automaton ignored, String input) throws AutomatonException {
        // Prepare the working variables
        int inputPos = 0;
        int factorPos = 0;

        // Iterate over all the input
        while (inputPos < input.length()) {

            // If we tested all the regex without problems, return true
            if (factorPos == factor.length()) return true;

            // Test if there is a match
            if (input.charAt(inputPos) == factor.charAt(factorPos)) {
                // It's a match ! Go to the next character to test
                inputPos++;
                factorPos++;
            } else {
                // Not a match. Use the carry over to update the input position, and return to the start of the regex
                inputPos -= carryOver[factorPos];
                factorPos = 0;
            }
        }

        // The default result, if we tested all the input without finding a match
        return false;
    }

}
