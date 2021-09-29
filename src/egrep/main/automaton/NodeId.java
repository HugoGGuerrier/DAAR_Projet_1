package egrep.main.automaton;

import java.util.HashSet;
import java.util.Set;

public class NodeId {

    // ----- Attributes -----

    private Set<Integer> keys;

    // ----- Constructors -----

    public NodeId() {
        this.keys = new HashSet<>();
    }

    // ----- Getters -----

    public Set<Integer> getKeys() {
        return keys;
    }

    // ----- Setters -----

    public void addKey(int key) {
        keys.add(key);
    }

    public void removeKey(int key) {
        keys.remove(key);
    }

    // ----- Override methods -----

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof NodeId)) return false;
        NodeId node = (NodeId) o;
        return keys.equals(node.keys);
    }

    // ----- Class methods -----

    public NodeId union(NodeId id) {
        NodeId res = new NodeId();
        res.keys.addAll(keys);
        res.keys.addAll(id.keys);
        return res;
    }

}
