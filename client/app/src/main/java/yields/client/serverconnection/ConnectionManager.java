package yields.client.serverconnection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class ConnectionManager implements Connection, ConnectionProvider {
    private Socket connection;

    public ConnectionManager(InetAddress localAdress) throws IOException{
        connection = new YieldSocketProviderEmulator().getConnection();
    }

    @Override
    public boolean working(){
        return connection.isConnected();
    }

    @Override
    public CommunicationChannel getCommunicationChannel() {
        //sender = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        return null;
    }

    @Override
    public void subscribeToConnection() {

    }
}
