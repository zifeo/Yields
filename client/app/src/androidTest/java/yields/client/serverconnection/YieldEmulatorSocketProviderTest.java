package yields.client.serverconnection;

import junit.framework.Assert;

import org.junit.Test;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class YieldEmulatorSocketProviderTest {

    /**
     * needs the server running
     */
    @Test
    public void testConnectionToServer(){
        SocketProvider socketProvider;

        try {
            socketProvider = new YieldEmulatorSocketProvider();
        } catch (UnknownHostException e) {
            Assert.fail(e.getMessage());
            return;
        }

        Socket socket;

        try {
            socket = socketProvider.getConnection();
        } catch (IOException e) {
            Assert.fail(e.getMessage());
            return;
        }

        Assert.assertTrue(socket.isConnected());

        try {
            socket.close();
        } catch (IOException e) {
            Assert.fail(e.getMessage());
            return;
        }
    }

}
