package yields.client.nodes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import yields.client.R;
import yields.client.activities.MessageActivity;
import yields.client.activities.MockFactory;
import yields.client.exceptions.NodeException;
import yields.client.id.Id;
import yields.client.messages.Message;
import yields.client.messages.TextContent;
import yields.client.node.ClientUser;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

import static java.lang.Thread.sleep;

public class GroupTest extends ActivityInstrumentationTestCase2<MessageActivity> {

    private static final int MOCK_MESSAGE_COUNT = 20;

    public GroupTest(){
        super(MessageActivity.class);
    }

    /**
     * Set up for the tests.
     */
    @Before
    public void setUp() throws Exception {
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        YieldsApplication.setApplicationContext(InstrumentationRegistry.getContext());
        getInstrumentation().getContext().getResources();
    }

    @Test
    public void testTextMessagesAreCorrectlySortedByDate() throws IOException {
        ClientUser lastUser = YieldsApplication.getUser();
        YieldsApplication.setUser(new FakeUser("wqef", new Id(2), "d",BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.send_icon) ));
        int index = 0;
        Group g = new Group("Group", new Id(32), new ArrayList<User>());

        SortedMap<Date, Message> lastMessages = g.getLastMessages();
        List<Message> messages = new ArrayList<>();
        for(Message entry : lastMessages.values()){
            messages.add(entry);
        }

        for (int i = 0 ; i < MOCK_MESSAGE_COUNT ; i ++){
            assertEquals("Mock message " + i, ((TextContent) messages.get(i).getContent()).getText());
        }
        YieldsApplication.setUser(lastUser);
    }

    private class FakeUser extends ClientUser{

        public FakeUser(String name, Id id, String email, Bitmap img) throws NodeException {
            super(name, id, email, img);
        }

        @Override
        public void sendMessage(Group group, Message message) throws IOException {

        }

        @Override
        public List<Message> getGroupMessages(Group group, Date last) throws IOException {
            List<Message> messages = new ArrayList<>();
            for (int i = 0 ; i < MOCK_MESSAGE_COUNT ; i ++){
                TextContent mockContent = MockFactory.generateFakeTextContent("Mock message " + i);
                Message m = MockFactory.generateMockMessage("node name", new Id(i), this, mockContent);
                messages.add(m);
                try {
                    sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return messages;
        }

        @Override
        public void addNewGroup(Group group) throws IOException {

        }

        @Override
        public void deleteGroup(Group group) {

        }

        @Override
        public Map<User, String> getHistory(Group group, Date from) {
            return null;
        }
    }
}
