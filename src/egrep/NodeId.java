package egrep;

import java.util.HashSet;
import java.util.Set;

public class NodeId {
    private Set<Integer> keys;

    public NodeId() {
        this.keys = new HashSet<>();
    }

    public void addKey(int key) {
        keys.add(key);
    }

    public NodeId union(NodeId id) {
        NodeId res = new NodeId();
        res.keys.addAll(keys);
        res.keys.addAll(id.keys);
        return res;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof NodeId)) return false;
        NodeId node = (NodeId) o;
        return keys.equals(node.keys);
    }

}
