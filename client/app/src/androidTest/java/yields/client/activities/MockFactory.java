package yields.client.activities;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import yields.client.exceptions.NodeException;
import yields.client.id.Id;
import yields.client.messages.Content;
import yields.client.messages.Message;
import yields.client.messages.TextContent;
import yields.client.node.ClientUser;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Factory used to create Mock Groups, Messages, Users, ClientUsers, Contents.
 */
public class MockFactory {


    public static Group createMockGroup(String name, Id id, List<Id> connectedUsers) {
        return new FakeGroup(name, id, connectedUsers);
    }

    private static class FakeGroup extends Group {

        public FakeGroup(String name, Id id, List<Id> users) throws NodeException {
            super(name, id, users, Bitmap.createBitmap(80, 80, Bitmap.Config.RGB_565));
        }

        public void addMessage(Message newMessage) {
        }
    }

    public static List<Message> generateMockMessages(int number) {
        ArrayList<Message> messages = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            Content content = generateFakeTextContent(i);
            messages.add(generateMockMessage("Mock node name " + i, new Id(-i), generateFakeUser("Mock user " + i,
                    new Id(123), "mock email"), content));
        }
        return messages;
    }

    public static List<Id> generateMockUsers(int number) {
        ArrayList<Id> users = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            User u = generateFakeUser("Mock user name " + i, new Id(-i), "Mock email " + i);
            YieldsApplication.getUser().addUserToEntourage(u);
            users.add(u.getId());
        }
        return users;
    }

    public static List<Group> generateMockGroups(int number) {
        ArrayList<Group> groups = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            groups.add(createMockGroup("Mock group name " + i, new Id(-i), generateMockUsers(i)));
        }
        return groups;
    }

    public static Message generateMockMessage(String nodeName, Id nodeID, User sender, Content content) {
        return new Message(nodeName, nodeID, sender.getId(), content, new Date());
    }

    public static TextContent generateFakeTextContent(int i) {
        return new TextContent("Mock message #" + (i));
    }

    public static User generateFakeUser(String name, Id id, String email) {
        Bitmap image1 = YieldsApplication.getDefaultUserImage();
        User user = new User(name, id, email, image1);
        YieldsApplication.getUser().addUserToEntourage(user);
        return user;
    }

    public static FakeClientUser generateFakeClientUser(String name, Id id, String email, Bitmap img) {
        try {
            return new FakeClientUser(name, id, email, img);
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    public static Bitmap generateMockImage() {
        return Bitmap.createBitmap(10, 10, Bitmap.Config.RGB_565);
    }

    private static class FakeClientUser extends ClientUser {

        public FakeClientUser(String name, Id id, String email, Bitmap img) throws NodeException, InstantiationException {
            super(name, id, email, img);
        }
    }
}
