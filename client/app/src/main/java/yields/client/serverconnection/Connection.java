package yields.client.serverconnection;

public interface Connection {

    CommunicationChannel getCommunicationChannel();

    void subscribeToConnection();
}
