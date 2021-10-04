package egrep.main.exceptions;

/**
 * This class represent a failed matching test for a regex
 */
public class RegexMatchingException extends Exception {
    public RegexMatchingException(String msg) {
        super(msg);
    }
}
