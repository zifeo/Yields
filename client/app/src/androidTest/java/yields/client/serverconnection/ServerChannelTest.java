package yields.client.serverconnection;

import junit.framework.Assert;

import org.json.JSONException;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.UnknownHostException;

public class ServerChannelTest {
    private final static String FAKE_RESPONSE = "{" +
            "\"type\":\"test\"," +
            "\"time\":0," +
            "\"hash\":0," +
            "\"message\":{" +
            "\"text\":\"hello world\"" +
            "}" +
            "}";

    private final static String SIMPLE_REQUEST = "{" +
            "\"metadata\":{" +
            "\"sender\":0," +
            "\"time\":0" +
            "}," +
            "\"kind\":\"PING\"," +
            "\"message\":{" +
            "\"content\":\"test\"" +
            "}" +
            "}";

    @Test
    public void testWorkingSendRequestAndReadResponse() throws JSONException{
        Request simpleRequest = RequestBuilder.simpleRequest("test");

        ByteArrayInputStream input = new ByteArrayInputStream(
                FAKE_RESPONSE.getBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream();


        ServerChannel channel = new ServerChannel(toWriter(output),
                toReade(input), simpleStatus(true));


        try {
            Response response = channel.sendRequest(simpleRequest);
            Assert.assertEquals(SIMPLE_REQUEST, output.toString());
            Assert.assertEquals("Response is wrong",
                    response.object().toString(), FAKE_RESPONSE);
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testNonWorkingConnection() throws IOException{
        Request simpleRequest = RequestBuilder.simpleRequest("test");

        ByteArrayInputStream input = new ByteArrayInputStream(
                FAKE_RESPONSE.getBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        ServerChannel channel = new ServerChannel(toWriter(output),
                toReade(input), simpleStatus(false));

        try {
            channel.sendRequest(simpleRequest);
            Assert.fail("");
        } catch (IOException e) {
        }

        Assert.assertEquals("", output.toString());
    }

    private ConnectionStatus simpleStatus(final boolean status) {
        return new ConnectionStatus() {
            @Override
            public boolean working() {
                return status;
            }
        };
    }

    private PrintWriter toWriter(OutputStream output) {
        return new PrintWriter( new BufferedWriter(
                new OutputStreamWriter(output)), true);
    }

    private BufferedReader toReade(InputStream input) {
        return new BufferedReader( new InputStreamReader(input));
    }
}
