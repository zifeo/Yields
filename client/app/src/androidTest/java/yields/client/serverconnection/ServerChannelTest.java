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
            "\"hash\":0," +
            "\"time\":0," +
            "\"type\":\"PING\"," +
            "\"message\":{" +
            "\"name\":\"test\"" +
            "}" +
            "}";

    @Test
    public void testWorkingSendRequest() throws JSONException{
        Request simpleRequest = prepareSimpleRequest();

        ByteArrayInputStream input = new ByteArrayInputStream(
                FAKE_RESPONSE.getBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream();


        ServerChannel channel = new ServerChannel(toWriter(output),
                toReade(input), simpleStatus(true));

        Response response;
        try {
            response = channel.sendRequest(simpleRequest);
        } catch (IOException e) {
            Assert.fail(e.getMessage());
            return;
        }

        Assert.assertEquals(SIMPLE_REQUEST, output.toString());

        try {
            Assert.assertEquals("Response is wrong",
                    response.object().toString(), FAKE_RESPONSE);
        } catch (JSONException e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * This test needs to have the local server running
     *
     * @throws JSONException
     */
    @Test
    public void testActualServerConnection() throws JSONException{
        Request simpleRequest = prepareSimpleRequest();

        CommunicationChannel channel;

        try {
            channel = new ConnectionManager(
                    new YieldEmulatorSocketProvider())
                    .getCommunicationChannel();
        } catch (UnknownHostException e) {
            Assert.fail(e.getMessage());
            return;
        } catch (IOException e) {
            Assert.fail(e.getMessage());
            return;
        }

        Response response;
        try {
            response = channel.sendRequest(simpleRequest);
        } catch (IOException e) {
            Assert.fail(e.getMessage());
            return;
        }

        Assert.assertEquals("Response is wrong",
                response.rawResponse(),
                SIMPLE_REQUEST + " was server answered !");
    }

    @Test
    public void testNonWorkingConnection() throws IOException{
        Request simpleRequest = prepareSimpleRequest();

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

    private Request prepareSimpleRequest(){
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.MessageKind.PING);
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
