package yields.client.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yields.client.R;
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

    public static Group createMockGroup(String name, Id id, List<User> connectedUsers){
        return new FakeGroup(name, id, connectedUsers);
    }

    private static class FakeGroup extends Group{

        public FakeGroup(String name, Id id, List<User> users) throws NodeException {
            super(name, id, users);
        }

        public void addMessage(Message newMessage){
        }
    }
    public static List<Message> generateMockMessages(int number){
        ArrayList<Message> messages = new ArrayList<>();
        for (int i = 0; i < number; i ++){
            Content content = generateFakeTextContent(i);
            Bitmap image1 = Bitmap.createBitmap(80, 80, Bitmap.Config.RGB_565);
            messages.add(new Message("Mock node name " + i, new Id(-i), generateFakeUser("Mock user " + i, new Id(123), "Mock email " + i, image1), content));
        }
        return messages;
    }

    public static TextContent generateFakeTextContent(int i){
        return new TextContent("Mock message #" + (i));
    }

    public static User generateFakeUser(String name, Id id, String email, Bitmap img){
            return new User(name, id, email, img);
    }

    public static FakeClientUser generateFakeClientUser(String name, Id id, String email, Bitmap img){
        try {
            return new FakeClientUser(name, id, email, img);
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    public static ImageContent generateFakeImageContent(Bitmap img, String caption){
            return new ImageContent(img, caption);
    }

    public static TextContent generateFakeTextContent(String text){
        return new TextContent(text);
    }

    public static Message generateMockMessage(String nodeName, Id nodeID, User sender, Content content){
            return new Message(nodeName, nodeID, sender, content);
    }

    private static class FakeClientUser extends ClientUser {
        public FakeClientUser(String name, Id id, String email, Bitmap img) throws NodeException, InstantiationException {
            super(name, id, email, img);
        }

        @Override
        public void sendMessage(Group group, Message message) {
        }

        @Override
        public List<Message> getGroupMessages(Group group, Date lastDate) throws IOException {
            return new ArrayList<>();

        }

        @Override
        public void createNewGroup(Group group) {


        }

        @Override
        public void deleteGroup(Group group) {

        }

        @Override
        public Map<User, String> getHistory(Group group, Date from) {
            return new HashMap<>();
        }
    }
}
