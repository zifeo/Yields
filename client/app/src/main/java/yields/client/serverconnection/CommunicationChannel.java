package yields.client.serverconnection;

import java.io.IOException;

public interface CommunicationChannel {

    /**
     * sends a serverRequest threw the communication channel
     *
     * @param serverRequest ServerRequest to send
     * @return the response of the server
     * @throws IOException if there was an error with the retrieval of the message
     */
<<<<<<< HEAD
    void sendRequest(ServerRequest request) throws IOException;
=======
    Response sendRequest(ServerRequest serverRequest) throws IOException;
>>>>>>> master
}
