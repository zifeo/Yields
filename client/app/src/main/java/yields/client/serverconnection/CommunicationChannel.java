package yields.client.serverconnection;

import java.io.IOException;

public interface CommunicationChannel {

    Response sendRequest(Request request) throws IOException;
}
