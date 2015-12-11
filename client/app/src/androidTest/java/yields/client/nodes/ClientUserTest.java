package yields.client.nodes;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import yields.client.id.Id;
import yields.client.node.ClientUser;
import yields.client.node.Group;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void testAddGroups(){
        // Clear the groups.
        user.addGroups(new ArrayList<Group>());
        ArrayList<Group> groups = new ArrayList<>();
        for (int i = 0 ; i < 23 ; i ++){
            groups.add(new Group("EZ SKIN EZ LIFE", new Id(i + 100), new ArrayList<Id>()));
        }
        user.addGroups(groups);
        assertEquals(23, user.getUserGroups().size());
        List<Group> userGroups = user.getUserGroups();
        for (int i = 0 ; i < 23 ; i ++){
            assertTrue(userGroups.contains(groups.get(i)));
        }
    }

    @Test
    public void testAddSameGroupMultipleTime(){
        Group group = new Group("EZ SKIN EZ LIFE", new Id(420), new ArrayList<Id>());
        // Clear the groups.
        user.addGroups(new ArrayList<Group>());
        user.addGroup(group);
        user.addGroup(group);
        assertEquals(1, user.getUserGroups().size());
        assertEquals(group, user.getUserGroups().get(0));
    }

    @Test
    public void testUserGetGroupWithKnownGroup() {
        Group group = new Group("EZ SKIN EZ LIFE", new Id(420), new ArrayList<Id>());
        // Clear the groups.
        user.addGroups(new ArrayList<Group>());
        user.addGroup(group);
        assertEquals(group, user.getGroup(new Id(420)));
    }

    @Test
    public void testUserGetGroupWithUnknownGroup() {
        Group group = new Group("EZ SKIN EZ LIFE", new Id(420), new ArrayList<Id>());
        // Clear the groups.
        user.addGroups(new ArrayList<Group>());
        user.addGroup(group);
        assertEquals(null, user.getGroup(new Id(1111111)));
    }

}
