package yields.client.serverconnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;

public class ServerChannel implements CommunicationChannel {
    private PrintWriter mSender;
    private BufferedReader mReceiver;
    private ConnectionStatus mConnectionStatus;

    protected ServerChannel(PrintWriter sender, BufferedReader receiver,
                         ConnectionStatus connectionStatus){

        this.mSender = sender;
        this.mReceiver = receiver;
        this.mConnectionStatus = connectionStatus;
    }

    @Override
    public Response sendRequest(Request request)
            throws IOException {

        Objects.requireNonNull(request);

        if (!mConnectionStatus.working()) {
            throw new IOException("Not connected to server");
        }

        mSender.print(request.message());
        mSender.flush();

        Response response = null;

        String rawResponse = mReceiver.readLine();

        if (isValid(rawResponse)) {
            response = new Response(rawResponse);
        } else {
            throw new IOException("Invalid response");
        }

        return response;
    }

    private boolean isValid(String response){
        //TODO: verify response validity with regex maybe
        return true;
    }
}
