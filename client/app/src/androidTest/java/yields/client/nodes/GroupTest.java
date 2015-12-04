package yields.client.nodes;

import android.graphics.Bitmap;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import yields.client.activities.MessageActivity;
import yields.client.activities.MockFactory;
import yields.client.id.Id;
import yields.client.messages.Message;
import yields.client.messages.TextContent;
import yields.client.node.ClientUser;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.serverconnection.DateSerialization;
import yields.client.yieldsapplication.YieldsApplication;

import static java.lang.Thread.sleep;

public class GroupTest extends ActivityInstrumentationTestCase2<MessageActivity>{

    private static final int MOCK_MESSAGE_COUNT = 20;
    private JSONArray jsonGroup;
    private Group mG;

    public GroupTest(){
        super(MessageActivity.class);
    }

    /**
     * Set up for the tests.
     */
    @Before
    public void setUp() throws Exception {
        jsonGroup = new JSONArray("[0, \"sweng\", \"2015-11-23T13:25:51.157+01:00\"]");
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        YieldsApplication.setApplicationContext(InstrumentationRegistry.getTargetContext());
        YieldsApplication.setResources(getInstrumentation().getTargetContext
                ().getResources());ClientUser lastUser = YieldsApplication.getUser();
        YieldsApplication.setUser(MockFactory.generateFakeClientUser("Bob " +
                "Ross", new Id(1337), "HappyLittleTree@joyOfPainting.ru", Bitmap
                .createBitmap(80, 80, Bitmap.Config.RGB_565)));

        mG = new FakeGroup("Group", new Id(32), new ArrayList<Id>());
        YieldsApplication.setGroup(mG);
    }

    @Test
    public void testGroupIdParsingFromResponse() throws JSONException, ParseException{
        Group g = new Group(jsonGroup);
        assertEquals(g.getId().getId(), new Long(0));
    }

    @Test
    public void testGroupDateParsingFromResponse() throws JSONException, ParseException{
        Group g = new Group(jsonGroup);
        assertEquals(g.getLastUpdate(),
                DateSerialization.dateSerializer.toDate("2015-11-23T13:25:51.157+01:00"));
    }

    @Test
    public void testGroupNameParsingFromResponse() throws JSONException, ParseException{
        Group g = new Group(jsonGroup);
        assertEquals(g.getName(), "sweng");
    }

    @Test
    public void testTextMessagesAreCorrectlySortedByDate() throws IOException, InstantiationException {

        SortedMap<Date, Message> lastMessages = mG.getLastMessages();
        List<Message> messages = new ArrayList<>();
        for(Message entry : lastMessages.values()){
            messages.add(entry);
        }

        for (int i = 0 ; i < MOCK_MESSAGE_COUNT ; i ++){
            Assert.assertEquals("Mock message #" + i, ((TextContent) messages.get(i).getContent()).getText());
        }
    }

    private class FakeGroup extends Group{

        public FakeGroup(String name, Id id, List<Id> users) {
            super(name, id, users);
        }

        synchronized public SortedMap<Date, Message> getLastMessages() {
            SortedMap<Date, Message> map = new TreeMap<>();
            for (int i = 0 ; i < MOCK_MESSAGE_COUNT ; i ++){
                TextContent mockContent = MockFactory.generateFakeTextContent(i);
                List<User> usersInGroup = new ArrayList<User>();
                Message m = MockFactory.generateMockMessage("node name", new
                                Id(i), MockFactory.generateFakeUser("topkek",
                                new Id(2), "bobRoss"), mockContent);
                map.put(new java.util.Date(), m);
                try {
                    sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return map;
        }
    }
}
