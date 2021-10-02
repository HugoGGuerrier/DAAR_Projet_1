package egrep.main.automaton;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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

    /**
     * Get if a node id is contained in the current node id
     *
     * @return True if the node id is contained
     */
    public boolean contains(NodeId id) {
        return keys.containsAll(id.keys);
    }

    /**
     * Create a node id from a collection of other node id
     *
     * @param col The collection
     * @return The new node id
     */
    public static NodeId fromCollection(Collection<NodeId> col) {
        // Prepare the result
        NodeId res = new NodeId();

        // For each node id, add all keys to the result
        for(NodeId id : col) {
            res.keys.addAll(id.keys);
        }

        // Return the result
        return res;
    }

}
