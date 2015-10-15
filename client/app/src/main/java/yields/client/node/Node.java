package yields.client.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

abstract class Node {
    private final String name;
    private final long id;
    private List<Node> connectedNodes;

    public Node(String name, long id, List<Node> connectedNodes){
        this.name = name;
        this.id = id;
        this.connectedNodes = connectedNodes;
    }

    public Node(String name, long id) {
        this(name, id, new ArrayList<Node>());
    }

    public List<Node> getConnected() {
        return Collections.unmodifiableList(connectedNodes);
    }

    protected void connectNode(Node node) {
        connectedNodes.add(node);
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

}
