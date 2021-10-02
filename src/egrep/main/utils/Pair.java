package egrep.main.utils;

/**
 * This class represents a polymorphic pair of object
 *
 * @param <T> The key type
 * @param <U> The value type
 * @author Hugo Guerrier
 */
public class Pair<T, U> {

    // ----- Attributes -----

    private final T key;
    private final U value;

    // ----- Constructor -----

    /**
     * Create a new pair with key and value
     *
     * @param k The key
     * @param v The value
     */
    public Pair(T k, U v) {
        key = k;
        value = v;
    }

    // ----- Getters -----

    public T getKey() {
        return key;
    }

    public U getValue() {
        return value;
    }

}
