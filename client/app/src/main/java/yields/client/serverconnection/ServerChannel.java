package yields.client.serverconnection;

import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Objects;

/**
 * The server channel connects to the server and sends message to the server
 */
public final class ServerChannel implements CommunicationChannel {
    private BufferedWriter mSender;
    private ConnectionStatus mConnectionStatus;

    /**
     * Constructor for the server channel.
     * @param sender The sender.
     * @param connectionStatus Status of the connection.
     */
    protected ServerChannel(BufferedWriter sender,
                         ConnectionStatus connectionStatus){

        this.mSender = sender;
        this.mConnectionStatus = connectionStatus;
    }

    /**
     * Sends a serverRequest by this communication channels
     *
     * @param serverRequest ServerRequest to send
     * @return The Response of the serverRequest
     * @throws IOException If we have trouble sending the serverRequest
     */
    @Override
    public void sendRequest(ServerRequest serverRequest)
            throws IOException {

        Objects.requireNonNull(serverRequest);

        if (!mConnectionStatus.working()) {
            throw new IOException("Not connected to server");
        }

        Log.d("Y:" + this.getClass().getName(), "sending : " + serverRequest.message());
        mSender.write(serverRequest.message());
        mSender.newLine();
        mSender.flush();
    }

    /**
     * Test the validity of the response.
     * @param response The response to test.
     * @return True if valid, false otherwise.
     */
    private boolean isValid(String response){
        //TODO: verify response validity with regex maybe
        return true;
    }
}
