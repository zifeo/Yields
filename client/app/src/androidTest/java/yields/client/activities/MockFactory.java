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
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Factory used to create Mock Groups, Messages, Users, ClientUsers, Contents.
 */
public class MockFactory {

    public static Group createMockGroup(String name, Id id, List<User> connectedUsers){
        return new Group(name, id, connectedUsers);
    }

    public static TextContent generateFakeTextContent(int i){
        return new TextContent("Mock message #" + (i));
    }

    public static User generateFakeUser(String name, Id id, String email, Bitmap img){
            return new User(name, id, email, img);
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

    public static Message generateMockMessage(String nodeName, Id nodeID, User sender, Content content){
            return new Message(nodeName, nodeID, sender, content);
    }

    private static class FakeClientUser extends ClientUser {
        public FakeClientUser(String name, Id id, String email, Bitmap img) throws NodeException {
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
        public void addNewGroup(Group group) {


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
