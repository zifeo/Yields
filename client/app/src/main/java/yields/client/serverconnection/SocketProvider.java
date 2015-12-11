package yields.client.serverconnection;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;

/**
 * Constructs {@link HttpURLConnection} objects that can be used to
 * retrieve data from a given {@link URL}.
 */
public interface SocketProvider {
    /**
     * Returns a new {@link Socket} object on the application server.
     *
     * @return a new {@link Socket} object for successful
     * connections to the server.
     * @throws IOException if the server is not available.
     */
    Socket getConnection() throws IOException;

}