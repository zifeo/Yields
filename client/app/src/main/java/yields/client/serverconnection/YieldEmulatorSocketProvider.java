package yields.client.serverconnection;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * A default implementation of the {@link SocketProvider} interface that uses
 * the mechanism available in the {@link URL} object to create
 * {@link HttpURLConnection} objects.
 *
 */
public class YieldEmulatorSocketProvider implements SocketProvider {
    private final static int DST_PORT = 7777;
    private final static String LOCAL_ADDRESS = "10.0.2.2";
    private final InetAddress dstAddress;

    public YieldEmulatorSocketProvider() throws UnknownHostException{
        this.dstAddress = InetAddress.getByName(LOCAL_ADDRESS);
    }

    public Socket getConnection() throws IOException {

        Socket socket = new Socket(dstAddress, DST_PORT);
        return socket;
    }
}