package yields.client.node;

import java.util.ArrayList;
import java.util.List;

abstract class Node {
    private String name;
    private long id;

    public Node(){
        this.name = "";
        this.id = 0l;
    }

    public Node(String name, long id) {
        this.name = name;
        this.id = id;
    }

    public List<Node> getConnectedNode() {
        return new ArrayList<Node>();
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

}
