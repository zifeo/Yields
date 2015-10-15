package yields.client.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

abstract class Node {
    private String name;
    private long id;
    private List<Node> connectedNodes;

    public Node(){
        this.name = "";
        this.id = 0l;
        connectedNodes = new ArrayList<>();
    }

    public Node(String name, long id) {
        this.name = name;
        this.id = id;
    }

    public Node(String name, long id, List connectedNodes){
        this(name, id);
        this.connectedNodes = connectedNodes;
    }

    public List<Node> getConnectedNode() {
        return Collections.unmodifiableList(connectedNodes);
    }

    public void connectNode(User user) {
        connectedNodes.add(user);
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

}
