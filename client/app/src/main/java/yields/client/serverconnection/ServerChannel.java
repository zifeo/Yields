package yields.client.serverconnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ServerChannel implements CommunicationChannel {
    private PrintWriter sender;
    private BufferedReader receiver;
    private ConnectionStatus connectionStatus;

    protected ServerChannel(PrintWriter sender, BufferedReader receiver,
                         ConnectionStatus connectionStatus){

        this.sender = sender;
        this.receiver = receiver;
        this.connectionStatus = connectionStatus;
    }

    @Override
    public Response sendRequest(Request request)
            throws IOException {

        if (request == null) {
            throw new IllegalArgumentException();
        }
        if (!connectionStatus.working()) {
            throw new IOException("Not connected to server");
        }

        sender.print(request.message());
        sender.flush();

        Response response = null;

        String rawResponse = receiver.readLine();

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
