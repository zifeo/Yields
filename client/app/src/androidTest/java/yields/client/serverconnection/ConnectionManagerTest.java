package yields.client.serverconnection;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ConnectionManagerTest {
    private final String TESTTEXT = "HELLO WORLD";
    private Socket mockedSocket;
    private SocketProvider sampleSocketProvider;
    private InputStream input;
    private OutputStream output;

    @BeforeClass
    public void setup(){
        mockedSocket = Mockito.mock(Socket.class);
        sampleSocketProvider = new SocketProvider() {
            @Override
            public Socket getConnection() throws IOException {
                return mockedSocket;
            }
        };

        input = new ByteArrayInputStream(TESTTEXT.getBytes());
        output = new ByteArrayOutputStream();
    }

    @Test
    public void verify(){

        try {
            Mockito.when(mockedSocket.getInputStream()).thenReturn(input);
            Mockito.when(mockedSocket.getInputStream()).thenReturn(input);
        } catch (IOException e) {
            Assert.fail("wierd IOException with mockito");
        }

        try {
            ConnectionManager manager = new ConnectionManager(sampleSocketProvider);
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }

    }
}
