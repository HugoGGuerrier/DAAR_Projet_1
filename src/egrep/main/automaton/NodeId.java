package egrep.main.automaton;

import java.util.HashSet;
import java.util.Set;

public class NodeId {

    // ----- Attributes -----

    private final Set<Integer> keys;

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

    @Override
    public String toString() {
        // Prepare the working vars
        int cpt = 0;
        StringBuilder res = new StringBuilder();

        // For each element, add it to the result
        for(Integer id : keys) {
            if(cpt > 0) res.append(", ");
            res.append(id);
            cpt++;
        }

        // If there is more than one element, put some bracket
        if(cpt > 1) {
            res.insert(0, '{');
            res.append('}');
        }

        // Return the result
        return res.toString();
    }

    // ----- Class methods -----

    public NodeId union(NodeId id) {
        NodeId res = new NodeId();
        res.keys.addAll(keys);
        res.keys.addAll(id.keys);
        return res;
    }

}
