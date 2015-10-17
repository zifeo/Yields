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
public class YieldSocketProviderEmulator implements SocketProvider {
    private final static int DST_PORT = 10;
    private final static int LOCAL_PORT = 10;
    private final static String LOCAL_ADDRESS = "10.0.2.2";
    private final static String PHONE_ADDRESS = "localhost";
    private final InetAddress localAddress;
    private final InetAddress dstAddress;

    public YieldSocketProviderEmulator() throws UnknownHostException{
        this.dstAddress = InetAddress.getByName(LOCAL_ADDRESS);
        this.localAddress = InetAddress.getByName(PHONE_ADDRESS);
    }

    public Socket getConnection() throws IOException {

        Socket socket = new Socket(dstAddress, DST_PORT,
                localAddress, LOCAL_PORT);
        return socket;
    }
}