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
    private BufferedReader mReceiver;
    private ConnectionStatus mConnectionStatus;

    /**
     * Constructor for the server channel.
     * @param sender The sender.
     * @param receiver The receiver.
     * @param connectionStatus Status of the connection.
     */
    protected ServerChannel(BufferedWriter sender, BufferedReader receiver,
                         ConnectionStatus connectionStatus){

        this.mSender = sender;
        this.mReceiver = receiver;
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
    public Response sendRequest(ServerRequest serverRequest)
            throws IOException {

        Objects.requireNonNull(serverRequest);

        if (!mConnectionStatus.working()) {
            throw new IOException("Not connected to server");
        }

        mSender.write(serverRequest.message());
        mSender.newLine();
        mSender.flush();

        Response response = null;

        String rawResponse = ""; //mReceiver.readLine();

        try {
            response = new Response(rawResponse);
        } catch (JSONException e){
            throw new IOException("Invalid response");
        }

        return response;
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
