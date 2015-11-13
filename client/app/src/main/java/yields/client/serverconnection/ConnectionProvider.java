package yields.client.serverconnection;

import java.io.IOException;

public interface ConnectionProvider {

    CommunicationChannel getCommunicationChannel() throws IOException;

    void subscribeToConnection(ConnectionSubscriber subscriber);

    void close() throws IOException;
}
