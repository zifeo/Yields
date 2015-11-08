package yields.client.serverconnection;

import junit.framework.Assert;

import org.junit.Test;

import java.io.IOException;
import java.net.Socket;

public class YieldEmulatorSocketProviderTest {

    /**
     * needs the server running
     */
    @Test
    public void testConnectionToServer(){
        SocketProvider socketProvider;

        try {
            socketProvider = new YieldEmulatorSocketProvider();
            Socket socket;
            socket = socketProvider.getConnection();
            Assert.assertTrue(socket.isConnected());
            socket.close();
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

}
