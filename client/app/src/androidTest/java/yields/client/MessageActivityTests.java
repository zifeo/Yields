package yields.client;

import java.util.Date;
import java.util.List;
import java.util.Map;

import yields.client.messages.Message;
import yields.client.node.ClientUser;
import yields.client.node.Group;
import yields.client.node.User;

public class MessageActivityTests {


    private class FakeUser extends ClientUser{

        public FakeUser(String name, long id, String email) {
            super(name, id, email);
        }

        @Override
        public void sendMessage(Group group, Message message) {

        }

        @Override
        public List<Message> getGroupMessages(Group group) {
            return null;
        }

        @Override
        public void addNewGroup(Group group) {

        }

        @Override
        public void deleteGroup(Group group) {

        }

        @Override
        public Map<User, String> getHistory(Date from) {
            return null;
        }
    }
}
