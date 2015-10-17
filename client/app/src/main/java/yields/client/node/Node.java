package yields.client.node;

import yields.client.id.Id;

public abstract class Node {
    private final String mName;
    private final Id mId;

    public Node(String name, long id){
        this.mName = name;
        this.mId = new Id(id);
    }

    public String getName() {
        return mName;
    }

    public Id getId() {
        return mId;
    }

}
