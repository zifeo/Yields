package yields.client.nodes;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import yields.client.node.ClientUser;

import static org.junit.Assert.assertEquals;

public class ClientUserTest {
    private JSONObject jsonUserInfoResponse;
    private ClientUser user;

    @Before
    public void setUp() throws JSONException{
        jsonUserInfoResponse = new JSONObject("{" +
                "name: \"Nicolas\"," +
                "email: \"fake@fake.ch\"," +
                "entourage: \"[1,2,3,4,5,6]\"" +
                "}");

        user = new ClientUser("fake@fake.ch");

        user.update(jsonUserInfoResponse);
    }

    @Test
    public void testClientUserNameUpdate() {
        assertEquals(user.getName(), "Nicolas");
    }

    @Test
    public void testClientUserEmailUpdate() {
        assertEquals(user.getEmail(), "fake@fake.ch");
    }


}
