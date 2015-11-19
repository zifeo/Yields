package yields.client.serverconnection;

import java.io.IOException;

public interface CommunicationChannel {

    /**
     * sends a serverRequest threw the communication channel
     *
     * @param serverRequest ServerRequest to send
     * @throws IOException if there was an error with the retrieval of the message
     */
    void sendRequest(ServerRequest serverRequest) throws IOException;
}
