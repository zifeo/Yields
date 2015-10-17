package yields.client;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Before;
import org.junit.Test;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import yields.client.activities.MessageActivity;
import yields.client.id.Id;
import yields.client.messages.Content;
import yields.client.messages.Message;
import yields.client.messages.TextContent;
import yields.client.node.ClientUser;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

import org.junit.runner.RunWith;
import org.mockito.Mockito;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MessageActivityTests extends ActivityInstrumentationTestCase2<MessageActivity> {
    private static final List<Message> MOCK_MESSAGES = generateMockMessages(10);
    private static final FakeClientUser MOCK_CLIENT_USER = new FakeClientUser("Mock client user", new Id(117), "Mock email client user");
    private static final Group MOCK_GROUP = new Group("Mock group", new Id(11111), new ArrayList<User>());

    public MessageActivityTests() {
        super(MessageActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
    }

    @Test
    public void testDisplay(){
        getActivity();
        YieldsApplication.setApplicationContext(getActivity().getApplicationContext());
        YieldsApplication.setGroup(MOCK_GROUP);
        YieldsApplication.setUser(MOCK_CLIENT_USER);
        assertEquals(true, true);
    }

    private static class FakeClientUser extends ClientUser{
        public FakeClientUser(String name, Id id, String email) {
            super(name, id, email);
        }

        @Override
        public void sendMessage(Group group, Message message) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Message> getGroupMessages(Group group) {
            return MOCK_MESSAGES;
        }

        @Override
        public void addNewGroup(Group group) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void deleteGroup(Group group) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Map<User, String> getHistory(Date from) {
            return null;
        }
    }

    private static List<Message> generateMockMessages(int number){
        ArrayList<Message> messages = new ArrayList<>();
        for (int i = 0; i < number; i ++){
            int rand = new Random().nextInt();
            Content content = generateFakeContent();
            messages.add(new Message("Mock node name " + rand, new Id(rand), generateFakeUser(), content));
        }
        return messages;
    }

    private static Content generateFakeContent(){
        return new TextContent("Mock message #" + (new Random()).nextInt());
    }

    private static User generateFakeUser(){
        int rand = new Random().nextInt();
        return new User("Mock user " + rand, new Id(rand), "Mock email " + rand);
    }
}
