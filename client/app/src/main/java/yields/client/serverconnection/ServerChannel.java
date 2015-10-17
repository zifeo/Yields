package yields.client.serverconnection;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.PrintWriter;

public class ServerChannel implements CommunicationChannel {
    PrintWriter sender;
    BufferedReader receiver;

    public ServerChannel(PrintWriter sender, BufferedReader receiver){
        this.sender = sender;
        this.receiver = receiver;
    }

    @Override
    public ProtocolMessage sendRequest(ProtocolMessage request) {
        JSONObject object = request.message();

        sender.println();
        sender.flush();

        return null;
    }
}
