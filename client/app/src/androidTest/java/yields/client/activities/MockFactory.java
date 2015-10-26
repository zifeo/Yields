package yields.client.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.Date;
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

    public static List<Message> generateMockMessages(int number){
        ArrayList<Message> messages = new ArrayList<>();
        for (int i = 0; i < number; i ++){
            Content content = generateFakeTextContent(i);
            messages.add(generateMockMessage("Mock node name " + i, new Id(-i), generateFakeUser("Mock user " + i,
                            new Id(123), "mock email"), content,
                    createMockGroup("Mock group " + number, new Id(123), new ArrayList<User>())));
        }
        return messages;
    }

    public static List<User> generateMockUsers(int number){
        ArrayList<User> users = new ArrayList<>();
        for (int i = 0; i < number; i ++){
            users.add(generateFakeUser("Mock user name " + i, new Id(-i), "Mock email " + i));
        }
        return users;
    }

    public static List<Group> generateMockGroups(int number){
        ArrayList<Group> groups = new ArrayList<>();
        for (int i = 0; i < number; i ++){
            groups.add(createMockGroup("Mock group name " + i, new Id(-i), generateMockUsers(i)));
        }
        return groups;
    }


    public static Group createMockGroup(String name, Id id, List<User> connectedUsers){
        Bitmap image1 = BitmapFactory.decodeResource(YieldsApplication.getResources(),
                R.drawable.userpicture);
        return new Group(name, id, connectedUsers, image1);
    }

    public static Message generateMockMessage(String nodeName, Id nodeID, User sender,
                                              Content content, Group group){
        return new Message(nodeName, nodeID, sender, content, new Date(), group);
    }

    public static TextContent generateFakeTextContent(int i){
        return new TextContent("Mock message #" + (i));
    }

    public static User generateFakeUser(String name, Id id, String email){
        Bitmap image1 = BitmapFactory.decodeResource(YieldsApplication.getResources(),
                R.drawable.userpicture);
            return new User(name, id, email, image1);
    }

    public static FakeClientUser generateFakeClientUser(String name, Id id, String email, Bitmap img){
            return new FakeClientUser(name, id, email, img);
    }

    public static ImageContent generateFakeImageContent(Bitmap img, String caption){
            return new ImageContent(img, caption);
    }

    public static TextContent generateFakeTextContent(String text){
        return new TextContent(text);
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
            return  generateMockMessages(4);
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
        public Map<User, String> getHistory(Group group, Date from) {
            return null;
        }
    }
}
