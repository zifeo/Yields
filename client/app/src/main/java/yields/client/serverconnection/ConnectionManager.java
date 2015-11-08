package yields.client.serverconnection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Objects;

/**
 *The connection manager manages the connection from which we can send and receive messages
 */
public class ConnectionManager implements ConnectionStatus, ConnectionProvider {
    private Socket mSocket;

    /**
     * Creates a connection manager linked to the Socket provided by the socket provider
     *
     * @param socketProvider The socket provider
     * @throws IOException If there is a connection problem
     */
    public ConnectionManager(SocketProvider socketProvider) throws IOException{
        Objects.requireNonNull(socketProvider);
        mSocket = socketProvider.getConnection();
    }

    /**
     * Returns the state of the connection
     *
     * @return true if the connection works false otherwise
     */
    @Override
    public boolean working(){
        return mSocket.isConnected() && mSocket.isBound()
                && !mSocket.isOutputShutdown() && !mSocket.isInputShutdown();
    }

    /**
     * retrieves the communication channel with the socket
     *
     * @return The sus mentioned communication channel
     * @throws IOException In case of IO error with the server
     */
    @Override
    public CommunicationChannel getCommunicationChannel() throws IOException{
        BufferedWriter sender = new BufferedWriter(
                        new OutputStreamWriter(
                                mSocket.getOutputStream()));

        BufferedReader receiver = new BufferedReader(
                new InputStreamReader(mSocket.getInputStream()));

        return new ServerChannel(sender, receiver, this);
    }

    /**
     * subscribes to the push connection
     */
    @Override
    public void subscribeToConnection() {
        //TODO : Implement listener
    }

    /**
     * Closes the socket
     *
     * @throws IOException In case of IO errors while closing the socket
     */
    @Override
    public void close() throws IOException {
        mSocket.close();
    }
}
