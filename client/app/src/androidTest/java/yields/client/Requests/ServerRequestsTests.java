package yields.client.Requests;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Date;

import yields.client.id.Id;
import yields.client.serverconnection.DateSerialization;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.ServerRequest;
import yields.client.servicerequest.ServiceRequest;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 * Tests for the RequestBuilder class
 */
public class ServerRequestsTests {

    /**
     * Tests if a GroupHistoryRequest is correclty built.
     * (Test for GroupHistoryRequest(Id senderId, Id groupId, Date last, int messageCount))
     */
    @Test
    public void testGroupHistoryRequest() {
        try {
            Id groupId = new Id(12);
            Id senderId = new Id(11);
            Date date = new Date();
            int messageCount = 10;

            ServerRequest serverRequest = RequestBuilder.GroupHistoryRequest(senderId, groupId,
                    date, messageCount);
            JSONObject json = new JSONObject(serverRequest.message());

            assertEquals(json.getString(RequestBuilder.Fields.KIND.getValue()),
                    ServiceRequest.RequestKind.GROUPHISTORY.getValue());
            assertEquals(json.getJSONObject("metadata").getString("sender"), senderId
                    .getId());
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.LAST.getValue()),
                    DateSerialization.toString(date));
            assertEquals(json.getJSONObject("message").getInt(RequestBuilder.Fields.COUNT.getValue
                    ()), messageCount);
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.GID.getValue()),
                    groupId.getId());
        } catch (JSONException e) {
            fail();
        }
    }
}
