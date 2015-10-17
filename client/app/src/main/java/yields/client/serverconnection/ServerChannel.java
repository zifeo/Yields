package yields.client.serverconnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ServerChannel implements CommunicationChannel {
    PrintWriter sender;
    BufferedReader receiver;
    ConnectionStatus connectionStatus;

    public ServerChannel(PrintWriter sender, BufferedReader receiver,
                         ConnectionStatus connectionStatus){

        this.sender = sender;
        this.receiver = receiver;
        this.connectionStatus = connectionStatus;
    }

    @Override
    public ProtocolMessage sendRequest(ProtocolMessage request)
            throws IOException {

        if (request == null) {
            throw new IllegalArgumentException();
        }
        if (!connectionStatus.working()) {
            throw new IOException("Not connected to server");
        }

        sender.print(serverMessage(request));
        sender.flush();

        ResponseParser response = null;

        String rawResponse = receiver.readLine();

        if (isValid(rawResponse)) {
            response = new ResponseParser(rawResponse);
        } else {
            throw new IOException("Invalid response");
        }

        return response;
    }

    private String serverMessage(ProtocolMessage request){
        return request.message().toString();
    }

    private boolean isValid(String response){
        //TODO: verify response validity with regex maybe
        return true;
    }
}
