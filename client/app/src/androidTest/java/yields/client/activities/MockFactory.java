package yields.client.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import yields.client.R;
import yields.client.exceptions.ContentException;
import yields.client.exceptions.MessageException;
import yields.client.exceptions.NodeException;
import yields.client.id.Id;
import yields.client.messages.Content;
import yields.client.messages.ImageContent;
import yields.client.messages.Message;
import yields.client.messages.TextContent;
import yields.client.node.ClientUser;
import yields.client.node.Group;
import yields.client.node.Node;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Factory used to create Mock Groups, Messages, Users, ClientUsers, Contents.
 */
public class MockFactory {

    private static final List<Message> MOCK_MESSAGES = generateMockMessages(4);

    public static Group createMockGroup(String name, Id id, List<Node> nodes){
        try {
            return new Group(name, id, nodes);
        } catch (NodeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Message> generateMockMessages(int number){
        ArrayList<Message> messages = new ArrayList<>();
        for (int i = 0; i < number; i ++){
            Content content = generateFakeTextContent(i);
            try {
                Bitmap image1 = BitmapFactory.decodeResource(YieldsApplication.getResources(), R.drawable.userpicture);
                messages.add(new Message("Mock node name " + i, new Id(-i), generateFakeUser("Mock user " + i, new Id(123), "mock email", image1), content));
            } catch (MessageException e) {
                e.printStackTrace();
            } catch (NodeException e) {
                e.printStackTrace();
            }
        }
        return messages;
    }

    public static Content generateFakeTextContent(int i){
        return new TextContent("Mock message #" + (i));
    }

    public static User generateFakeUser(String name, Id id, String email, Bitmap img){
        try {
            return new User(name, id, email, img);
        } catch (NodeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static FakeClientUser generateFakeClientUser(String name, Id id, String email, Bitmap img){
        try {
            return new FakeClientUser(name, id, email, img);
        } catch (NodeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ImageContent generateFakeImageContent(Bitmap img, String caption){
        try {
            return new ImageContent(img, caption);
        } catch (ContentException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static TextContent generateFakeTextContent(String text){
        return new TextContent(text);
    }

    public static Message generateMockMessage(String nodeName, Id nodeID, User sender, Content content){
        try {
            return new Message(nodeName, nodeID, sender, content);
        } catch (MessageException e) {
            e.printStackTrace();
        } catch (NodeException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class FakeClientUser extends ClientUser {
        public FakeClientUser(String name, Id id, String email, Bitmap img) throws NodeException {
            super(name, id, email, img);
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
}
