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
 */
public class YieldsSocketProvider implements SocketProvider {
    private final static int DST_PORT = 27777;
    private final static String LOCAL_ADDRESS = "avalan.ch";
    private final InetAddress mDstAddress;

    /**
     * Constructs a Socket Provider for the yield application on an Emulator
     *
     * @throws UnknownHostException In case of trouble connecting
     */
    public YieldsSocketProvider() throws UnknownHostException {
        this.mDstAddress = InetAddress.getByName(LOCAL_ADDRESS);
    }

    /**
     * Gives the socket connected to the server
     *
     * @return The sus mentioned socket
     * @throws IOException In case of trouble getting connected
     */
    public Socket getConnection() throws IOException {

        Socket socket = new Socket(mDstAddress, DST_PORT);
        return socket;
    }
}