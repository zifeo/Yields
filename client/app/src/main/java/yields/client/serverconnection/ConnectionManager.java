package yields.client.serverconnection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class ConnectionManager implements ConnectionStatus, ConnectionProvider {
    private Socket socket;

    public ConnectionManager(SocketProvider socketProvider) throws IOException{
        socket = socketProvider.getConnection();
    }

    @Override
    public boolean working(){
        return socket.isConnected();
    }

    @Override
    public CommunicationChannel getCommunicationChannel() throws IOException{
        PrintWriter sender = new PrintWriter(
                new BufferedWriter(
                        new OutputStreamWriter(
                                socket.getOutputStream())), true);

        BufferedReader receiver = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));

        return new ServerChannel(sender, receiver, this);
    }

    @Override
    public void subscribeToConnection() {

    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
