package yields.client.serverconnection;

import java.io.IOException;

public interface CommunicationChannel {

    ProtocolMessage sendRequest(ProtocolMessage request) throws IOException;
}
