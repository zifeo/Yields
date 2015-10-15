package yields.client.node;

import java.util.List;

/**
 * Created by Nicolas on 15.10.15.
 */
abstract class Node {

    public abstract List<Node> getConnectedNode();

    public abstract List<Node> getName();

    public abstract List<Node> getId();

}
