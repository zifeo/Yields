package yields.client.serverconnection;

public interface ConnectionProvider {

    CommunicationChannel getCommunicationChannel();

    void subscribeToConnection();
}
