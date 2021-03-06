package yields.client.serverconnection;

import junit.framework.Assert;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import yields.client.id.Id;

public class ServerChannelTest {
    private final static String FAKE_RESPONSE = "{" +
            "\"type\":\"test\"," +
            "\"time\":0," +
            "\"hash\":0," +
            "\"message\":{" +
            "\"text\":\"hello world\"" +
            "}" +
            "}";

    private static String sSimpleRequest = "{" +
            "\"metadata\":{" +
            "\"sender\":0," +
            "\"datetime\":\"TIME\"}," +
            "\"kind\":\"PING\"," +
            "\"message\":" +
            "{\"content\":\"test\"}" +
            "}\n";

    @Test
    public void testNonWorkingConnection() {
        ServerRequest simpleServerRequest = RequestBuilder.userInfoRequest(new Id(0), new Id(1));

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        ServerChannel channel = new ServerChannel(toWriter(output), simpleStatus(false));

        try {
            channel.sendRequest(simpleServerRequest);
            Assert.fail("");
        } catch (IOException e) {
            //Assert.fail(e.getMessage());
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

    private BufferedWriter toWriter(OutputStream output) {
        return new BufferedWriter(
                new OutputStreamWriter(output));
    }

    private BufferedReader toReade(InputStream input) {
        return new BufferedReader( new InputStreamReader(input));
    }
}
