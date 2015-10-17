package yields.client.serverconnection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class ConnectionManager implements Connection{
    private Socket connection;

    public ConnectionManager(InetAddress localAdress) throws IOException{
        connection = new YieldSocketProviderEmulator().getConnection();
    }

    @Override
    public CommunicationChannel getCommunicationChannel() {
        return null;
    }

    @Override
    public void subscribeToConnection() {

    }
}
