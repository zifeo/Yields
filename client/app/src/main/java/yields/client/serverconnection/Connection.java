package yields.client.serverconnection;

public interface Connection {

    Sender getSender();

    void subscribeToConnection();
}
