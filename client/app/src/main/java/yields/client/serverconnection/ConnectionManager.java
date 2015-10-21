package yields.client.serverconnection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionManager implements ConnectionStatus, ConnectionProvider {
    private Socket mSocket;

    public ConnectionManager(SocketProvider socketProvider) throws IOException{
        mSocket = socketProvider.getConnection();
    }

    @Override
    public boolean working(){
        return mSocket.isConnected();
    }

    @Override
    public CommunicationChannel getCommunicationChannel() throws IOException{
        PrintWriter sender = new PrintWriter(
                new BufferedWriter(
                        new OutputStreamWriter(
                                mSocket.getOutputStream())), true);

        BufferedReader receiver = new BufferedReader(
                new InputStreamReader(mSocket.getInputStream()));

        return new ServerChannel(sender, receiver, this);
    }

    @Override
    public void subscribeToConnection() {
        //TODO : Implement listener
    }

    @Override
    public void close() throws IOException {
        mSocket.close();
    }
}
