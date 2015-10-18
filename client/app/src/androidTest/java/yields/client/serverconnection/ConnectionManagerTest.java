package yields.client.serverconnection;

import junit.framework.Assert;

import org.junit.Before;
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
    private final static String FAKE_RESPONSE = "{" +
            "\"type\":\"test\"," +
            "\"time\":0," +
            "\"hash\":0," +
            "\"message\":{" +
            "\"text\":\"hello world\"" +
            "}" +
            "}";

    private final static String SIMPLE_REQUEST = "{" +
            "\"hash\":0," +
            "\"time\":0," +
            "\"type\":\"PING\"," +
            "\"message\":{" +
            "\"name\":\"test\"" +
            "}" +
            "}";

    private Socket mockedSocket;
    private SocketProvider sampleSocketProvider;
    private InputStream input;
    private OutputStream output;

    @Before
    public void setup(){
        //TODO : better implementation ?
        mockedSocket = Mockito.mock(Socket.class);
        sampleSocketProvider = new SocketProvider() {
            @Override
            public Socket getConnection() throws IOException {
                return mockedSocket;
            }
        };

        input = new ByteArrayInputStream(FAKE_RESPONSE.getBytes());
        output = new ByteArrayOutputStream();
    }

    @Test
    public void verifyNoException(){

        try {
            Mockito.when(mockedSocket.getInputStream()).thenReturn(input);
            Mockito.when(mockedSocket.getOutputStream()).thenReturn(output);
            Mockito.when(mockedSocket.isConnected()).thenReturn(true);
        } catch (IOException e) {
            Assert.fail("wierd IOException with mockito");
        }

        try {
            ConnectionManager manager = new ConnectionManager(sampleSocketProvider);
            manager.getCommunicationChannel();
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }

    }
}
