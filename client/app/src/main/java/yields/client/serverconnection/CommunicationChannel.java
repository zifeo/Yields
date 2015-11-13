package yields.client.serverconnection;

import java.io.IOException;

public interface CommunicationChannel {

    /**
     * sends a request threw the communication channel
     *
     * @param request Request to send
     * @return the response of the server
     * @throws IOException if there was an error with the retrieval of the message
     */
    void sendRequest(Request request) throws IOException;
}
