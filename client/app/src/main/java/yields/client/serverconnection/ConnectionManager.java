package yields.client.serverconnection;

import android.util.Log;

import org.json.JSONException;

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

        return new ServerChannel(sender, this);
    }

    /**
     * subscribes to the push connection
     */
    @Override
    public void subscribeToConnection(ConnectionSubscriber subscriber) {
        //TODO connect to controller (not created yet)

        BufferedReader receiver = null;

        try {
           receiver = new BufferedReader(
                    new InputStreamReader(mSocket.getInputStream()));
        } catch (IOException e) {
            subscriber.updateOnConnectionProblem(e);
            return;
        }

        String pushMessage = null;
        do {
            try {
                pushMessage = receiver.readLine();
                if(pushMessage != null) {
                    Response response = new Response(pushMessage);
                    subscriber.updateOn(response);
                }
            } catch (IOException e) {
                subscriber.updateOnConnectionProblem(e);
            } catch (JSONException e) {
                subscriber.updateOnParsingProblem(e);
            }
        } while (pushMessage != null);

        try {
            this.close();
        } catch (IOException e) {
            Log.d("Y:" + this.getClass().getName(),"Connection was already closed.");
        } finally {
            subscriber.updateOnConnectionProblem(new IOException("Server imput is shutdown"));
        }
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
