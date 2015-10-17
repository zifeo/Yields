package yields.client.serverconnection;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class ServerChannelTest {
    private final static String FAKE_RESPONSE = "{" +
            "\"type\":\"test\"" +
            "\"time\":\"0\"" +
            "\"hash\":\"0\"" +
            "\"message\":\"{" +
            "\"text\":\"hello world\"" +
            "}\"" +
            "}";

    @Test
    public void testSendRequest() {
        Request simpleRequest = prepareSimpleRequest();

        InputStream input = new ByteArrayInputStream(FAKE_RESPONSE.getBytes());
        OutputStream output = new ByteArrayOutputStream();


        ServerChannel channel = new ServerChannel(toWriter(output),
                toReade(input), simpleStatus(true));

        channel.sendRequest()
    }

    private ConnectionStatus simpleStatus(final boolean status) {
        return new ConnectionStatus() {
            @Override
            public boolean working() {
                return status;
            }
        };
    }

    private Request prepareSimpleRequest(){
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.MessageTypes.PING);
        requestBuilder.addField(RequestBuilder.Fields.NAME, "test");

        return requestBuilder.request();
    }

    private PrintWriter toWriter(OutputStream output) {
        return new PrintWriter( new BufferedWriter(
                new OutputStreamWriter(output)), true);
    }

    private BufferedReader toReade(InputStream input) {
        return new BufferedReader( new InputStreamReader(input));
    }
}
