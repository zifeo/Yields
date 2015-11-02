package yields.client.serverconnection;

import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * The server channel connects to the server and sends message to the server
 */
public class ServerChannel implements CommunicationChannel {
    private BufferedWriter mSender;
    private BufferedReader mReceiver;
    private ConnectionStatus mConnectionStatus;

    protected ServerChannel(BufferedWriter sender, BufferedReader receiver,
                         ConnectionStatus connectionStatus){

        this.mSender = sender;
        this.mReceiver = receiver;
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
    public Response sendRequest(Request request)
            throws IOException {

        Objects.requireNonNull(request);

        if (!mConnectionStatus.working()) {
            throw new IOException("Not connected to server");
        }

        mSender.write(request.message());
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

    private boolean isValid(String response){
        //TODO: verify response validity with regex maybe
        return true;
    }
}
