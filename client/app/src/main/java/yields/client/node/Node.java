package yields.client.node;

abstract class Node {
    private final String name;
    private final long id;

    public Node(String name, long id){
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

}
