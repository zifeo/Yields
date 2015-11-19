package yields.client.cache;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import yields.client.activities.MockFactory;
import yields.client.exceptions.CacheDatabaseException;
import yields.client.id.Id;
import yields.client.messages.ImageContent;
import yields.client.messages.Message;
import yields.client.messages.TextContent;
import yields.client.node.ClientUser;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * Test for the CacheDatbaseHelper class.
 */
public class CacheDatabaseTests {

    private CacheDatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mDatabase;

    /**
     * Context set up for all the tests.
     */
    @Before
    public void setUp() {
        YieldsApplication.setApplicationContext(InstrumentationRegistry.getTargetContext());
        YieldsApplication.setResources(InstrumentationRegistry.getTargetContext().getResources());
        YieldsApplication.setUser(MockFactory.generateFakeClientUser("Bobby", new Id(123),
                "lol@gmail.com", YieldsApplication.getDefaultGroupImage()));
        CacheDatabaseHelper.deleteDatabase();
        mDatabaseHelper = new CacheDatabaseHelper();
        mDatabase = mDatabaseHelper.getWritableDatabase();
        mDatabaseHelper.clearDatabase();
    }

    /**
     * Tests if a Database is created when using CacheDatabaseHelper.
     */
    @Test
    public void testDatabaseCanBeCreated() {
        mDatabaseHelper.getWritableDatabase();
        mDatabaseHelper.getReadableDatabase();
    }

    /**
     * Tests if Messages are correctly deleted from the database.
     * (Test for CacheDatabaseHelper.deleteMessage(ID id))
     */
    @Test
    public void testDatabaseCanDeleteMessages() {
        try {
            List<Message> messages = MockFactory.generateMockMessages(6);
            for (Message message : messages) {
                mDatabaseHelper.addMessage(message, new Id(666));
            }
            for (Message message : messages) {
                mDatabaseHelper.deleteMessage(message.getId(), new Id(666));
            }

            String selectQuery = "SELECT * FROM messages;";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            assertTrue(!cursor.moveToFirst());
            cursor.close();
        } catch (CacheDatabaseException exception) {
            fail(exception.getMessage());
        } finally {
            mDatabaseHelper.clearDatabase();
        }
    }

    /**
     * Tests if Messages can be added to the database.
     * (Test for addMessage(Message message, Id groupId))
     */
    @Test
    public void testDatabaseCanAddMessage() {
        try {
            List<Message> messages = MockFactory.generateMockMessages(3);
            for (Message message : messages) {
                mDatabaseHelper.addMessage(message, new Id(666));
            }
            Cursor cursor = mDatabase.rawQuery("SELECT * FROM messages;", null);
            assertEquals(3, cursor.getCount());
            assertEquals(8, cursor.getColumnCount());
            cursor.close();
        } catch (CacheDatabaseException e) {
            e.printStackTrace();
        } finally {
            mDatabaseHelper.clearDatabase();
        }
    }

    /**
     * Tests if Text Messages are correctly added to the database.
     * (Test for addMessage(Message message, Id groupId))
     */
    @Test
    public void testDatabaseAddsTextMessageCorrectly() {
        try {
            Message message = MockFactory.generateMockMessages(3).get(0);
            Group group = MockFactory.generateMockGroups(2).get(0);
            group.addUser(message.getSender());
            mDatabaseHelper.addMessage(message, group.getId());

            Message messageFromCache = mDatabaseHelper.getMessagesForGroup(group,
                    new Date(), 10).get(0);

            assertTrue(compareMessages(message, messageFromCache));
        } catch (CacheDatabaseException e) {
            e.printStackTrace();
        } finally {
            mDatabaseHelper.clearDatabase();
        }
    }

    /**
     * Tests if Image Messages are correctly added to the database.
     * (Test for addMessage(Message message, Id groupId))
     */
    @Test
    public void testDatabaseAddsImageMessageCorrectly() {
        try {
            ImageContent content = new ImageContent(YieldsApplication.getDefaultGroupImage(), "hello");
            User user = MockFactory.generateMockUsers(2).get(1);
            Message message = new Message("Bob", new Id(2), user, content, new Date());
            Group group = MockFactory.generateMockGroups(2).get(0);
            group.addUser(message.getSender());
            mDatabaseHelper.addMessage(message, group.getId());

            Message messageFromCache = mDatabaseHelper.getMessagesForGroup(group,
                    new Date(), 10).get(0);

            assertTrue(compareMessages(message, messageFromCache));
        } catch (CacheDatabaseException e) {
            e.printStackTrace();
        } finally {
            mDatabaseHelper.clearDatabase();
        }
    }


    /**
     * Tests if users are correctly deleted from the database.
     * (Test for CacheDatabaseHelper.deleteUser(ID id))
     */
    @Test
    public void testDatabaseCanDeleteUsers() {
        try {
            List<User> users = MockFactory.generateMockUsers(6);
            for (User user : users) {
                mDatabaseHelper.addUser(user);
            }
            for (User user : users) {
                mDatabaseHelper.deleteUser(user.getId());
            }

            String selectQuery = "SELECT * FROM users;";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            assertTrue(!cursor.moveToFirst());
            cursor.close();
        } catch (CacheDatabaseException exception) {
            fail(exception.getMessage());
        } finally {
            mDatabaseHelper.clearDatabase();
        }
    }

    /**
     * Tests if it can add valid Users and if the the Users are correctly added to the database.
     * (Test for CacheDatabaseHelper.addUser(User user))
     */
    @Test
    public void testDatabaseCanAddUsers() {
        try {
            List<User> users = MockFactory.generateMockUsers(6);
            for (User user : users) {
                mDatabaseHelper.addUser(user);
            }

            Cursor cursor = mDatabase.rawQuery("SELECT * FROM users;", null);
            assertEquals(6, cursor.getCount());
            assertEquals(6, cursor.getColumnCount());
            cursor.moveToFirst();

            for (int i = 0; i < 6; i++) {
                assertTrue(checkUserInformation(cursor, users.get(i)));
                if (i != 5) {
                    cursor.moveToNext();
                }
            }
        } catch (CacheDatabaseException exception) {
            fail(exception.getMessage());
        } finally {
            mDatabaseHelper.clearDatabase();
        }
    }

    /**
     * Tests if it can retrieve a User.
     * (Test for CacheDatabaseHelper.getUser(ID userID))
     */
    @Test
    public void testDatabaseCanGetUser() {
        try {
            List<User> users = MockFactory.generateMockUsers(6);
            for (User user : users) {
                mDatabaseHelper.addUser(user);
            }
            for (int i = 0; i < users.size(); i++) {
                User userFromDatabase = mDatabaseHelper.getUser(users.get(i).getId());
                assertEquals(users.get(i).getName(), userFromDatabase.getName());
                assertEquals(users.get(i).getEmail(), userFromDatabase.getEmail());
                assertEquals(users.get(i).getId().getId(), userFromDatabase.getId().getId());
            }
        } catch (CacheDatabaseException exception) {
            fail(exception.getMessage());
        } finally {
            mDatabaseHelper.clearDatabase();
        }
    }

    /**
     * Tests if it can retrieve all valid Users.
     * (Test for CacheDatabaseHelper.getAllUsers())
     */
    @Test
    public void testDatabaseCanGetAllUsers() {
        try {
            List<User> users = MockFactory.generateMockUsers(6);
            for (User user : users) {
                mDatabaseHelper.addUser(user);
            }

            List<User> usersFromDatabase = mDatabaseHelper.getAllUsers();
            assertEquals(users.size(), usersFromDatabase.size());
            for (int i = 0; i < users.size(); i++) {
                assertEquals(users.get(i).getName(), usersFromDatabase.get(i).getName());
                assertEquals(users.get(i).getEmail(), usersFromDatabase.get(i).getEmail());
                assertEquals(users.get(i).getId().getId(), usersFromDatabase.get(i).getId().getId());
            }
        } catch (CacheDatabaseException exception) {
            fail(exception.getMessage());
        } finally {
            mDatabaseHelper.clearDatabase();
        }
    }

    /**
     * Tests if it can retrieve all valid Users in the ClientUser's entourage.
     * (Test for CacheDatabaseHelper.getClientUserEntourage())
     */
    @Test
    public void testDatabaseCanGetAllUsersFromEntourage() {
        try {
            List<User> users = MockFactory.generateMockUsers(6);

            ClientUser clientUser = MockFactory.generateFakeClientUser("ClientUser", new Id(999), "jskdfj@jpg.com",
                    YieldsApplication.getDefaultUserImage());
            YieldsApplication.setUser(clientUser);
            for (User user : users) {
                clientUser.addUserToEntourage(user);
                mDatabaseHelper.addUser(user);
            }

            List<User> usersFromDatabase = mDatabaseHelper.getClientUserEntourage();
            assertEquals(users.size(), usersFromDatabase.size());
            for (int i = 0; i < users.size(); i++) {
                assertEquals(users.get(i).getName(), usersFromDatabase.get(i).getName());
                assertEquals(users.get(i).getEmail(), usersFromDatabase.get(i).getEmail());
                assertEquals(users.get(i).getId().getId(), usersFromDatabase.get(i).getId().getId());
                assertTrue(compareImages(users.get(i).getImg(), usersFromDatabase.get(i).getImg()));
            }
        } catch (CacheDatabaseException exception) {
            fail(exception.getMessage());
        } finally {
            mDatabaseHelper.clearDatabase();
        }
    }

    /**
     * Tests if users are correctly deleted from the database.
     * (Test for CacheDatabaseHelper.deleteGroup(Group group))
     */
    @Test
    public void testDatabaseCanDeleteGroups() {
        try {
            List<Group> groups = MockFactory.generateMockGroups(6);
            for (Group group : groups) {
                mDatabaseHelper.addGroup(group);
            }
            for (Group group : groups) {
                mDatabaseHelper.deleteGroup(group.getId());
            }

            String selectQuery = "SELECT * FROM groups;";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            assertTrue(!cursor.moveToFirst());
            cursor.close();
        } catch (CacheDatabaseException exception) {
            fail(exception.getMessage());
        } finally {
            mDatabaseHelper.clearDatabase();
        }
    }

    /**
     * Tests if it can add valid Groups and if the the Groups are correctly added to the database.
     * (Test for CacheDatabaseHelper.addGroup(Group group))
     */
    @Test
    public void testDatabaseCanAddGroups() {
        try {
            List<Group> groups = MockFactory.generateMockGroups(7);
            for (Group group : groups) {
                mDatabaseHelper.addGroup(group);
            }

            Cursor cursor = mDatabase.rawQuery("SELECT * FROM groups;", null);
            assertEquals(7, cursor.getCount());
            assertEquals(7, cursor.getColumnCount());
            cursor.moveToFirst();

            for (int i = 0; i < 6; i++) {
                assertTrue(checkGroupInformation(cursor, groups.get(i), i));
                if (i != 5) {
                    cursor.moveToNext();
                }
            }
        } catch (CacheDatabaseException exception) {
            fail(exception.getMessage());
        } finally {
            mDatabaseHelper.clearDatabase();
        }
    }

    /**
     * Tests if a Group can be renamed.
     * (Test for updateGroupName(Id groupId, String newGroupName,))
     */
    @Test
    public void testDatabaseCanUpdateGroupName() {
        try {
            Group group = MockFactory.generateMockGroups(1).get(0);
            mDatabaseHelper.addGroup(group);
            mDatabaseHelper.updateGroupName(group.getId(), "New group name");
            Group fromDatabase = mDatabaseHelper.getGroup(group.getId());
            assertEquals(fromDatabase.getName(), "New group name");
            assertEquals(fromDatabase.getId().getId(), group.getId().getId());
            assertEquals(fromDatabase.getVisibility(), Group.GroupVisibility.PRIVATE);
            assertTrue(compareUsers(fromDatabase.getUsers(), group.getUsers()));
            assertEquals(group.isValidated(), fromDatabase.isValidated());
        } catch (CacheDatabaseException exception) {
            fail(exception.getMessage());
        } finally {
            mDatabaseHelper.clearDatabase();
        }
    }

    /**
     * Tests if a Group can have it's image changed.
     * (Test for updateGroupImage(Id groupId, Bitmap newGroupImage))
     */
    @Test
    public void testDatabaseCanUpdateGroupImage() {
        try {
            Group group = MockFactory.generateMockGroups(1).get(0);
            mDatabaseHelper.addGroup(group);
            mDatabaseHelper.updateGroupImage(group.getId(),
                    Bitmap.createBitmap(60, 60, Bitmap.Config.RGB_565));
            Group fromDatabase = mDatabaseHelper.getGroup(group.getId());
            assertEquals(fromDatabase.getName(), group.getName());
            assertEquals(fromDatabase.getId().getId(), group.getId().getId());
            assertTrue(compareUsers(fromDatabase.getUsers(), group.getUsers()));
            assertEquals(fromDatabase.getVisibility(), Group.GroupVisibility.PRIVATE);
            assertTrue(compareImages(Bitmap.createBitmap(60, 60, Bitmap.Config.RGB_565),
                    fromDatabase.getImage()));
            assertEquals(group.isValidated(), fromDatabase.isValidated());
        } catch (CacheDatabaseException exception) {
            fail(exception.getMessage());
        } finally {
            mDatabaseHelper.clearDatabase();
        }
    }

    /**
     * Tests if a Group can have it's visibility changed.
     * (Test for updateGroupVisibility(Id groupId, Group.GroupVisibility visibility))
     */
    @Test
    public void testDatabaseCanUpdateGroupVisibility() {
        try {
            Group group = MockFactory.generateMockGroups(1).get(0);
            mDatabaseHelper.addGroup(group);
            mDatabaseHelper.updateGroupVisibility(group.getId(), Group.GroupVisibility.PUBLIC);
            Group fromDatabase = mDatabaseHelper.getGroup(group.getId());
            assertEquals(fromDatabase.getName(), group.getName());
            assertEquals(fromDatabase.getId().getId(), group.getId().getId());
            assertTrue(compareUsers(fromDatabase.getUsers(), group.getUsers()));
            assertTrue(compareImages(group.getImage(),
                    fromDatabase.getImage()));
            assertEquals(fromDatabase.getVisibility(), Group.GroupVisibility.PUBLIC);
            assertEquals(group.isValidated(), fromDatabase.isValidated());
        } catch (CacheDatabaseException exception) {
            fail(exception.getMessage());
        } finally {
            mDatabaseHelper.clearDatabase();
        }
    }

    /**
     * Tests if a Group can have it's validity changed.
     * (Test for updateGroupValidity(Id groupId, boolean validity))
     */
    @Test
    public void testDatabaseCanUpdateGroupValidity() {
        try {
            Group group = MockFactory.generateMockGroups(1).get(0);
            mDatabaseHelper.addGroup(group);
            mDatabaseHelper.updateGroupValidity(group.getId(), true);
            Group fromDatabase = mDatabaseHelper.getGroup(group.getId());
            assertEquals(fromDatabase.getName(), group.getName());
            assertEquals(fromDatabase.getId().getId(), group.getId().getId());
            assertTrue(compareUsers(fromDatabase.getUsers(), group.getUsers()));
            assertTrue(compareImages(group.getImage(),
                    fromDatabase.getImage()));
            assertEquals(true, fromDatabase.isValidated());
            assertEquals(fromDatabase.getVisibility(), group.getVisibility());
        } catch (CacheDatabaseException exception) {
            fail(exception.getMessage());
        } finally {
            mDatabaseHelper.clearDatabase();
        }
    }

    /**
     * Tests if a User can be removed from a Group.
     * (Test for removeUserFromGroup(Id groupId, Id userId))
     */
    @Test
    public void testDatabaseCanRemoveUserFromGroup() {
        try {
            Group group = MockFactory.generateMockGroups(3).get(2);
            mDatabaseHelper.addGroup(group);
            mDatabaseHelper.removeUserFromGroup(group.getId(), group.getUsers().get(0).getId());
            Group fromDatabase = mDatabaseHelper.getGroup(group.getId());
            assertEquals(fromDatabase.getName(), group.getName());
            assertEquals(fromDatabase.getId().getId(), group.getId().getId());
            assertTrue(compareImages(group.getImage(), fromDatabase.getImage()));
            assertEquals(group.getUsers().size() - 1, fromDatabase.getUsers().size());
            assertEquals(group.isValidated(), fromDatabase.isValidated());

            ArrayList<User> usersCopy = new ArrayList<>(group.getUsers());
            usersCopy.remove(0);
            assertTrue(compareUsers(fromDatabase.getUsers(), usersCopy));
        } catch (CacheDatabaseException exception) {
            fail(exception.getMessage());
        } finally {
            mDatabaseHelper.clearDatabase();
        }
    }

    /**
     * Tests if a User can be added to a Group.
     * (Test for addUserToGroup(Id groupId, Id userId))
     */
    @Test
    public void testDatabaseCanAddUserToGroup() {
        try {
            Group group = MockFactory.generateMockGroups(3).get(2);
            mDatabaseHelper.addGroup(group);
            User userToAdd = MockFactory.generateMockUsers(15).get(14);
            mDatabaseHelper.addUser(userToAdd);
            mDatabaseHelper.addUserToGroup(group.getId(), userToAdd);
            Group fromDatabase = mDatabaseHelper.getGroup(group.getId());
            assertEquals(fromDatabase.getName(), group.getName());
            assertEquals(fromDatabase.getId().getId(), group.getId().getId());
            assertTrue(compareImages(group.getImage(), fromDatabase.getImage()));
            assertEquals(group.getUsers().size() + 1, fromDatabase.getUsers().size());
            assertEquals(group.isValidated(), fromDatabase.isValidated());

            ArrayList<User> usersCopy = new ArrayList<>(group.getUsers());
            usersCopy.add(userToAdd);
            assertTrue(compareUsers(fromDatabase.getUsers(), usersCopy));
        } catch (CacheDatabaseException exception) {
            fail(exception.getMessage());
        } finally {
            mDatabaseHelper.clearDatabase();
        }
    }


    /**
     * Tests if it can retrieve a Group.
     * (Test for CacheDatabaseHelper.getGroup(ID userID))
     */
    @Test
    public void testDatabaseCanGetGroup() {
        try {
            List<Group> groups = MockFactory.generateMockGroups(6);
            for (Group group : groups) {
                mDatabaseHelper.addGroup(group);
            }
            for (int i = 0; i < groups.size(); i++) {
                Group groupFromDatabase = mDatabaseHelper.getGroup(groups.get(i).getId());
                assertEquals(groups.get(i).getName(), groupFromDatabase.getName());
                assertEquals(groups.get(i).getId().getId(), groupFromDatabase.getId().getId());
                assertEquals(groups.get(i).isValidated(), groupFromDatabase.isValidated());
                assertTrue(compareUsers(groups.get(i).getUsers(), groupFromDatabase.getUsers()));
            }
        } catch (CacheDatabaseException exception) {
            fail(exception.getMessage());
        } finally {
            mDatabaseHelper.clearDatabase();
        }
    }

    /**
     * Tests if it can retrieve all valid Groups.
     * (Test for CacheDatabaseHelper.getAllGroups())
     */
    @Test
    public void testDatabaseCanGetAllGroups() {
        try {
            List<Group> groups = MockFactory.generateMockGroups(6);
            for (Group group : groups) {
                mDatabaseHelper.addGroup(group);
            }

            List<Group> groupsFromDatabase = mDatabaseHelper.getAllGroups();
            assertEquals(groups.size(), groupsFromDatabase.size());
            for (int i = 0; i < groups.size(); i++) {
                Group groupFromDatabase = mDatabaseHelper.getGroup(groups.get(i).getId());
                assertEquals(groups.get(i).getName(), groupFromDatabase.getName());
                assertEquals(groups.get(i).getId().getId(), groupFromDatabase.getId().getId());
                assertEquals(groups.get(i).isValidated(), groupFromDatabase.isValidated());
                assertTrue(compareUsers(groups.get(i).getUsers(), groupFromDatabase.getUsers()));
            }
        } catch (CacheDatabaseException exception) {
            fail(exception.getMessage());
        } finally {
            mDatabaseHelper.clearDatabase();
        }
    }

    /**
     * Test for getMessagesForGroup(Group group, int lowerBoundary, int upperBoundary)
     */
    @Test
    public void testDatabaseGetMessagesForGroup() {
        try {
            User user1 = MockFactory.generateFakeUser("User 1", new Id(1), "u@j.ch");
            User user2 = MockFactory.generateFakeUser("User 2", new Id(2), "u@j.fr");
            List<User> users = new ArrayList<>();
            users.add(user1);
            users.add(user2);

            Group group = MockFactory.createMockGroup("Group name", new Id(1233), users);
            mDatabaseHelper.addGroup(group);

            for (int i = 0; i < 30; i++) {
                Message message = new Message("Mock node name User1 " + i, new Id(-i),
                        user1, MockFactory.generateFakeTextContent(i), new Date());
                mDatabaseHelper.addMessage(message, group.getId());
                Thread.sleep(15);
            }
            for (int i = 0; i < 30; i++) {
                Message message = new Message("Mock node name User2 " + i, new Id(-i - 30),
                        user2, MockFactory.generateFakeTextContent(i + 30), new Date());
                mDatabaseHelper.addMessage(message, group.getId());
                Thread.sleep(15);
            }

            List<Message> messagesFromDatabase = mDatabaseHelper.getMessagesForGroup(group,
                    new Date(), 10);
            assertEquals(10, messagesFromDatabase.size());
            for (int i = 0; i < 10; i++) {
                Message message = messagesFromDatabase.get(i);
                assertEquals("2", message.getSender().getId().getId());
                assertEquals("Mock message #" + (59 - i), ((TextContent) message.getContent()).getText());
            }
        } catch (CacheDatabaseException exception) {
            fail(exception.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mDatabaseHelper.clearDatabase();
        }
    }

    /**
     * Checks if the User information is correct in the row which cursor points to.
     *
     * @param cursor The row where the User information is to be checked.
     * @param user   The User which should have its data at the row pointed at by cursor.
     * @return A boolean which is true if the information is correct, and false otherwise.
     */
    private boolean checkUserInformation(Cursor cursor, User user) {
        boolean idIsCorrect = user.getId().getId().equals(cursor.getString(cursor.getColumnIndex("nodeID")));
        boolean userNameIsCorrect = user.getName().equals(
                cursor.getString(cursor.getColumnIndex("userName")));
        boolean userEmailIsCorrect = user.getEmail().equals(cursor.getString(
                cursor.getColumnIndex("userEmail")));
        byte[] imageFromCache = cursor.getBlob(cursor.getColumnIndex("userImage"));
        boolean userImageIsCorrect = compareImages(user.getImg(),
                BitmapFactory.decodeByteArray(imageFromCache, 0, imageFromCache.length));
        return idIsCorrect && userNameIsCorrect && userEmailIsCorrect && userImageIsCorrect;
    }

    /**
     * Checks if the Group information is correct in the row which cursor points to.
     *
     * @param cursor The row where the Group information is to be checked.
     * @param group  The Group which should have its data at the row pointed at by cursor.
     * @param i      The integer used to specify which index the Group has when it was created with
     *               MockFactory.generateMockGroups(int number).
     * @return A boolean which is true if the information is correct, and false otherwise.
     */
    private boolean checkGroupInformation(Cursor cursor, Group group, int i) {
        boolean idIsCorrect = -i == cursor.getLong(cursor.getColumnIndex("nodeID"));
        boolean groupNameIsCorrect = ("Mock group name " + i).equals(
                cursor.getString(cursor.getColumnIndex("groupName")));

        StringBuilder userIDS = new StringBuilder();
        List<User> users = group.getUsers();
        for (User user : users) {
            userIDS.append(user.getId().getId()).append(",");
        }
        if (users.size() != 0) {
            userIDS.deleteCharAt(userIDS.length() - 1);
        }
        boolean groupUserIDsAreCorrect = userIDS.toString().equals(
                cursor.getString(cursor.getColumnIndex("groupUsers")));
        byte[] imageFromCache = cursor.getBlob(cursor.getColumnIndex("groupImage"));
        boolean groupImageIsCorrect = compareImages(group.getImage(),
                BitmapFactory.decodeByteArray(imageFromCache, 0, imageFromCache.length));
        boolean validity = cursor.getColumnIndex("groupValidated") == 1;
        boolean groupValidityIsCorrect = validity == group.isValidated();
        return idIsCorrect && groupNameIsCorrect && groupUserIDsAreCorrect && groupImageIsCorrect && groupValidityIsCorrect;
    }

    /**
     * Compares two users.
     *
     * @param user1 The first User.
     * @param user2 The second User.
     * @return True if the two Users have the same name, email and Id. False otherwise.
     */
    private boolean compareUser(User user1, User user2) {
        boolean sameName = user1.getName().equals(user2.getName());
        boolean sameEmail = user1.getEmail().equals(user2.getEmail());
        boolean sameId = user1.getId().getId().equals(user2.getId().getId());
        return sameName && sameEmail && sameId;
    }

    /**
     * Compares two user lists.
     * The user in the lists must be in the same order.
     *
     * @param users1 The first list of Users.
     * @param users2 The second list of Users.
     * @return True if for every index of the lists, the Users have the same name, email and Id.
     * False otherwise.
     */
    private boolean compareUsers(List<User> users1, List<User> users2) {
        boolean sameSize = users1.size() == users2.size();
        if (!sameSize) {
            return false;
        } else {
            boolean same = true;
            for (int i = 0; i < users1.size(); i++) {
                same = same && compareUser(users1.get(i), users2.get(i));
            }
            return same;
        }
    }

    /**
     * Compares two Images (where one of them passed through the cache).
     *
     * @param originalImage  The original image (didn't go through cache).
     * @param imageFromCache The image that went through the cache.
     * @return True if the images are the same, false otherwise.
     */
    private boolean compareImages(Bitmap originalImage, Bitmap imageFromCache) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        originalImage.compress(Bitmap.CompressFormat.PNG, 0, stream);
        byte[] bytesOriginalImage = stream.toByteArray();
        Bitmap reconstructedOriginalImage = BitmapFactory.decodeByteArray(bytesOriginalImage, 0,
                bytesOriginalImage.length);
        return reconstructedOriginalImage.sameAs(imageFromCache);
    }

    /**
     * Compare two Messages (where one passed through the cache).
     *
     * @param originalMessage  The original message (didn't go through cache).
     * @param messageFromCache The message that went through the cache.
     * @return True if the Messages are the same, false otherwise.
     */
    private boolean compareMessages(Message originalMessage, Message messageFromCache) {
        boolean equal = originalMessage.getSender().getId().getId().equals(
                messageFromCache.getSender().getId().getId());
        equal = equal && originalMessage.getSender().getEmail().equals(
                messageFromCache.getSender().getEmail());
        equal = equal && originalMessage.getSender().getName().equals(
                messageFromCache.getSender().getName());

        equal = equal && originalMessage.getId().getId().equals(messageFromCache.getId().getId());
        equal = equal && (originalMessage.getDate().compareTo(messageFromCache.getDate()) == 0);
        equal = equal && originalMessage.getPreview().equals(messageFromCache.getPreview());
        equal = equal && originalMessage.getStatus().equals(messageFromCache.getStatus());

        equal = equal && originalMessage.getContent().getPreview().equals(
                messageFromCache.getContent().getPreview());

        boolean contentEqual;
        switch (originalMessage.getContent().getType()) {
            case TEXT:
                contentEqual = ((TextContent) originalMessage.getContent()).getText().equals(
                        ((TextContent) messageFromCache.getContent()).getText());
                break;
            case IMAGE:
                contentEqual = compareImages(((ImageContent) originalMessage.getContent()).getImage(),
                        ((ImageContent) messageFromCache.getContent()).getImage());
                break;
            default:
                contentEqual = false;
                fail("Impossible content type !");
        }

        equal = equal && contentEqual;
        equal = equal && originalMessage.getContent().getTextForRequest().equals(
                messageFromCache.getContent().getTextForRequest());

        return equal;
    }
}