package yields.client.serverconnection;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Objects;

/**
 * The server channel connects to the server and sends message to the server
 */
public class ServerChannel implements CommunicationChannel {
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
     * Sends a request by this communication channels
     *
     * @param request Request to send
     * @return The Response of the request
     * @throws IOException If we have trouble sending the request
     */
    @Override
    public void sendRequest(Request request)
            throws IOException {

        Objects.requireNonNull(request);

        if (!mConnectionStatus.working()) {
            throw new IOException("Not connected to server");
        }

        mSender.write(request.message());
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
