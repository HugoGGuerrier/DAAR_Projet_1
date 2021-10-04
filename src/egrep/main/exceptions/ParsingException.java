package egrep.main.exceptions;

/**
 * This class represent an exception during the regex parsing
 */
public class ParsingException extends Exception {
    public ParsingException(String msg) {
        super(msg);
    }
}
