package yields.client.cache;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.test.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
import yields.client.serverconnection.ImageSerialization;
import yields.client.yieldsapplication.YieldsApplication;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * Test for the CacheDatabaseHelper class.
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
        YieldsApplication.setUser(MockFactory.generateFakeClientUser(
                "Bobby", new Id(123), "lol@gmail.com", YieldsApplication.getDefaultGroupImage()));
        CacheDatabaseHelper.deleteDatabase();
        mDatabaseHelper = new CacheDatabaseHelper();
        mDatabase = mDatabaseHelper.getWritableDatabase();
        mDatabaseHelper.clearDatabase();
        YieldsApplication.setUser(
                new ClientUser("Johny", new Id(999999), "topKeke@gmail.com", YieldsApplication.getDefaultUserImage()));
    }

    /**
     * Clearing the database for the following tests.
     */
    @After
    public void tearDown() {
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
     * (Test for CacheDatabaseHelper.deleteMessage(Message message, Id groupId))
     */
    @Test
    public void testDatabaseCanDeleteMessages() {
        List<Message> messages = MockFactory.generateMockMessages(6);
        for (Message message : messages) {
            mDatabaseHelper.addMessage(message, new Id(666));
        }
        for (Message message : messages) {
            mDatabaseHelper.deleteMessage(message, new Id(666));
        }

        String selectQuery = "SELECT * FROM messages;";
        Cursor cursor = mDatabase.rawQuery(selectQuery, null);
        assertTrue(!cursor.moveToFirst());
        cursor.close();
    }

    /**
     * Tests if Messages can be added to the database.
     * (Test for addMessage(Message message, Id groupId))
     */
    @Test
    public void testDatabaseCanAddMessage() {
        for (int i = 0; i < 5; i++) {
            User user = new User("John" + i, new Id(-i), "lol@jpg", YieldsApplication.getDefaultUserImage());
            YieldsApplication.addNotKnown(user);
            Message message = new Message( new Id(i), new Id(-i),
                    MockFactory.generateFakeTextContent(i), new Date(), Message.MessageStatus.NOT_SENT);
            mDatabaseHelper.addMessage(message, new Id(666));
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                fail("Thread couldn't sleep !");
            }
        }

        Cursor cursor = mDatabase.rawQuery("SELECT * FROM messages;", null);
        assertEquals(5, cursor.getCount());
        assertEquals(8, cursor.getColumnCount());
        cursor.close();
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

            Message messageFromCache = mDatabaseHelper.getMessagesForGroup(group.getId(),
                    new Date(), 10).get(0);

            assertTrue(compareMessages(message, messageFromCache));
        } catch (CacheDatabaseException e) {
            fail("Couldn't retrieve messages from Group !");
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
            Id user = MockFactory.generateMockUsers(2).get(1);
            Message message = new Message( new Id(2), user, content, new Date());
            Group group = MockFactory.generateMockGroups(2).get(0);
            group.addUser(message.getSender());
            mDatabaseHelper.addMessage(message, group.getId());

            Message messageFromCache = mDatabaseHelper.getMessagesForGroup(group.getId(),
                    new Date(), 10).get(0);

            assertTrue(compareMessages(message, messageFromCache));
        } catch (CacheDatabaseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests if users are correctly deleted from the database.
     * (Test for CacheDatabaseHelper.deleteUser(ID id))
     */
    @Test
    public void testDatabaseCanDeleteUsers() {
        List<Id> users = MockFactory.generateMockUsers(6);
        for (Id user : users) {
            mDatabaseHelper.addUser(YieldsApplication.getUserFromId(user));
        }
        for (Id user : users) {
            mDatabaseHelper.deleteUser(user);

        }

        String selectQuery = "SELECT * FROM users;";
        Cursor cursor = mDatabase.rawQuery(selectQuery, null);
        assertTrue(!cursor.moveToFirst());
        cursor.close();
    }

    /**
     * Tests if it can add valid Users and if the the Users are correctly added to the database.
     * (Test for CacheDatabaseHelper.addUser(User user))
     */
    @Test
    public void testDatabaseCanAddUsers() {
        List<Id> users = MockFactory.generateMockUsers(6);
        for (Id user : users) {
            mDatabaseHelper.addUser(YieldsApplication.getUserFromId(user));
        }


        Cursor cursor = mDatabase.rawQuery("SELECT * FROM users;", null);
        cursor.moveToFirst();
        assertEquals(6, cursor.getCount());
        assertEquals(6, cursor.getColumnCount());

        for (int i = users.size() - 1; i >= 0; i--) {
            assertTrue(checkUserInformation(cursor, YieldsApplication.getUserFromId(users.get(i))));
            cursor.moveToNext();
        }
        cursor.close();
    }

    /**
     * Tests if it can update valid Users and if the the Users are correctly updated in the database.
     * (Test for CacheDatabaseHelper.updateUser(User user)
     */
    @Test
    public void testDatabaseCanUpdateUser() {
        List<Id> users = MockFactory.generateMockUsers(6);
        for (Id user : users) {
            mDatabaseHelper.addUser(YieldsApplication.getUserFromId(user));
        }

        Id userToUpdate = users.get(3);
        User updatedUser = YieldsApplication.getUserFromId(userToUpdate);

        updatedUser.setName("Johhny Cash");
        updatedUser.setEmail("rialtoGaming@jpg.com");
        updatedUser.setImage(YieldsApplication.getDefaultGroupImage());

        mDatabaseHelper.updateUser(updatedUser);

        User userFromDatabase = mDatabaseHelper.getUser(userToUpdate);

        assertEquals(updatedUser.getName(),
                userFromDatabase.getName());

        assertEquals(updatedUser.getEmail(),
                userFromDatabase.getEmail());

        assertEquals(updatedUser.getId().getId(),
                userFromDatabase.getId().getId());

        assertTrue(compareImages(updatedUser.getImage(),
                userFromDatabase.getImage()));
    }

    /**
     * Tests if it can rename valid Users and if the the Users are correctly renamed in the database.
     * (Test for CacheDatabaseHelper.updateUserName(Id userId, String newUserName))
     */
    @Test
    public void testDatabaseCanUpdateUserName() {
        List<Id> users = MockFactory.generateMockUsers(6);
        for (Id user : users) {
            mDatabaseHelper.addUser(YieldsApplication.getUserFromId(user));
        }

        Id userToRename = users.get(3);
        String newName = "New username YAY !";
        mDatabaseHelper.updateUserName(userToRename, newName);

        User userFromDatabase = mDatabaseHelper.getUser(userToRename);

        assertEquals(newName,
                userFromDatabase.getName());

        assertEquals(YieldsApplication.getUserFromId(userToRename).getEmail(),
                userFromDatabase.getEmail());

        assertEquals(userToRename.getId(),
                userFromDatabase.getId().getId());

        assertTrue(compareImages(YieldsApplication.getUserFromId(userToRename).getImage(),
                userFromDatabase.getImage()));
    }

    /**
     * Tests if it can update valid Users image and if the the Users are correctly updated in the database.
     * (Test for CacheDatabaseHelper.updateUserImage(Id userId, String newUserEmail))
     */
    @Test
    public void testDatabaseCanUpdateUserImage() {
        List<Id> users = MockFactory.generateMockUsers(6);
        for (Id user : users) {
            mDatabaseHelper.addUser(YieldsApplication.getUserFromId(user));
        }

        Id userToUpdateEmail = users.get(3);
        String newEmail = "THEBEST@gmail.com";
        mDatabaseHelper.updateUserEmail(userToUpdateEmail, newEmail);

        User userFromDatabase = mDatabaseHelper.getUser(userToUpdateEmail);

        assertEquals(YieldsApplication.getUserFromId(userToUpdateEmail).getName(),
                userFromDatabase.getName());

        assertEquals(newEmail,
                userFromDatabase.getEmail());

        assertEquals(userToUpdateEmail.getId(),
                userFromDatabase.getId().getId());

        assertTrue(compareImages(YieldsApplication.getUserFromId(userToUpdateEmail).getImage(),
                userFromDatabase.getImage()));
    }

    /**
     * Tests if it can update valid Users email and if the the Users are correctly updated in the database.
     * (Test for CacheDatabaseHelper.updateUserEmail(Id userId, String newUserName))
     */
    @Test
    public void testDatabaseCanUpdateUserEmail() {
        List<Id> users = MockFactory.generateMockUsers(6);
        for (Id user : users) {
            mDatabaseHelper.addUser(YieldsApplication.getUserFromId(user));
        }

        Id userToUpdateImage = users.get(3);
        Bitmap newImage = YieldsApplication.getDefaultGroupImage();
        mDatabaseHelper.updateUserImage(userToUpdateImage, newImage);

        User userFromDatabase = mDatabaseHelper.getUser(userToUpdateImage);

        assertEquals(YieldsApplication.getUserFromId(userToUpdateImage).getName(),
                userFromDatabase.getName());

        assertEquals(YieldsApplication.getUserFromId(userToUpdateImage).getEmail(),
                userFromDatabase.getEmail());

        assertEquals(userToUpdateImage.getId(),
                userFromDatabase.getId().getId());

        assertTrue(compareImages(newImage,
                userFromDatabase.getImage()));
    }

    /**
     * Tests if it can retrieve a User.
     * (Test for CacheDatabaseHelper.getUser(ID userID))
     */
    @Test
    public void testDatabaseCanGetUser() {
        List<Id> users = MockFactory.generateMockUsers(6);
        for (Id user : users) {
            mDatabaseHelper.addUser(YieldsApplication.getUserFromId(user));
        }

        for (int i = 0; i < users.size(); i++) {
            User userFromDatabase = mDatabaseHelper.getUser(users.get(i));

            assertEquals(YieldsApplication.getUserFromId(users.get(i)).getName(),
                    userFromDatabase.getName());

            assertEquals(YieldsApplication.getUserFromId(users.get(i)).getEmail(),
                    userFromDatabase.getEmail());

            assertEquals(users.get(i).getId(),
                    userFromDatabase.getId().getId());

            assertTrue(compareImages(YieldsApplication.getUserFromId(users.get(i)).getImage(),
                    userFromDatabase.getImage()));
        }
    }

    /**
     * Tests if it can retrieve all valid Users.
     * (Test for CacheDatabaseHelper.getAllUsers())
     */
    @Test
    public void testDatabaseCanGetAllUsers() {
        List<Id> users = MockFactory.generateMockUsers(6);
        for (Id user : users) {
            mDatabaseHelper.addUser(YieldsApplication.getUserFromId(user));
        }

        List<User> usersFromDatabase = mDatabaseHelper.getAllUsers();
        assertEquals(users.size(), usersFromDatabase.size());

        for (int i = users.size() - 1; i != 0; i--) {
            assertEquals(YieldsApplication.getUserFromId(users.get(i)).getName(),
                    usersFromDatabase.get(users.size() - i - 1).getName());

            assertEquals(YieldsApplication.getUserFromId(users.get(i)).getEmail(),
                    usersFromDatabase.get(users.size() - i - 1).getEmail());

            assertEquals(users.get(i).getId(),
                    usersFromDatabase.get(users.size() - i - 1).getId().getId());

            assertTrue(compareImages(YieldsApplication.getUserFromId(users.get(i)).getImage(),
                    usersFromDatabase.get(users.size() - i - 1).getImage()));
        }
    }

    /**
     * Tests if it can retrieve all valid Users in the ClientUser's entourage.
     * (Test for CacheDatabaseHelper.getClientUserEntourage())
     */
    @Test
    public void testDatabaseCanGetAllUsersFromEntourage() {
        ClientUser clientUser = MockFactory.generateFakeClientUser("ClientUser", new Id(999), "jskdfj@jpg.com",
                YieldsApplication.getDefaultUserImage());
        YieldsApplication.setUser(clientUser);

        List<Id> users = MockFactory.generateMockUsers(6);
        for (Id userId : users) {
            User user = YieldsApplication.getUserFromId(userId);
            clientUser.addUserToEntourage(user);
            mDatabaseHelper.addUser(user);
        }

        List<User> usersFromDatabase = mDatabaseHelper.getClientUserEntourage();
        assertEquals(users.size(), usersFromDatabase.size());

        for (int i = 0; i < users.size(); i++) {
            assertEquals(YieldsApplication.getUserFromId(users.get(i)).getName(),
                    usersFromDatabase.get(users.size() - i - 1).getName());

            assertEquals(YieldsApplication.getUserFromId(users.get(i)).getEmail(),
                    usersFromDatabase.get(users.size() - i - 1).getEmail());

            assertEquals(users.get(i).getId(),
                    usersFromDatabase.get(users.size() - i - 1).getId().getId());

            assertTrue(compareImages(YieldsApplication.getUserFromId(users.get(i)).getImage(),
                    usersFromDatabase.get(users.size() - i - 1).getImage()));
        }
    }

    /**
     * Tests if users are correctly deleted from the database.
     * (Test for CacheDatabaseHelper.deleteGroup(Group group))
     */
    @Test
    public void testDatabaseCanDeleteGroups() {
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
    }

    /**
     * Tests if users can be updated from the enourage (removed or added)
     * (Test for CacheDatabaseHelper.updateEntourage(Id userId, boolean inEntourage))
     */
    @Test
    public void testCanUpdateEntourage(){
        List<Id> users = MockFactory.generateMockUsers(6);
        for (Id user : users) {
            mDatabaseHelper.addUser(YieldsApplication.getUserFromId(user));
        }

        Id userToUpdate = users.get(3);
        User updatedUser = YieldsApplication.getUserFromId(userToUpdate);
        mDatabaseHelper.updateEntourage(userToUpdate, false);
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM users WHERE nodeID = ?;",
                new String[]{"-3"});
        cursor.moveToFirst();
        String inEntourage = cursor.getString(cursor.getColumnIndex("userEntourage"));

        assertEquals("0", inEntourage);

        User userFromDatabase = mDatabaseHelper.getUser(userToUpdate);

        assertEquals(updatedUser.getName(),
                userFromDatabase.getName());

        assertEquals(updatedUser.getEmail(),
                userFromDatabase.getEmail());

        assertEquals(updatedUser.getId().getId(),
                userFromDatabase.getId().getId());

        assertTrue(compareImages(updatedUser.getImage(),
                userFromDatabase.getImage()));
    }

    /**
     * Tests if it can add valid Groups and if the the Groups are correctly added to the database.
     * (Test for CacheDatabaseHelper.addGroup(Group group))
     */
    @Test
    public void testDatabaseCanAddGroups() {
        List<Group> groups = MockFactory.generateMockGroups(7);
        for (Group group : groups) {
            mDatabaseHelper.addGroup(group);
        }

        Cursor cursor = mDatabase.rawQuery("SELECT * FROM groups;", null);
        assertEquals(groups.size(), cursor.getCount());
        assertEquals(6, cursor.getColumnCount());
        cursor.moveToFirst();

        for (int i = groups.size() - 1; i >= 0; i--) {
            assertTrue(checkGroupInformation(cursor, groups.get(i)));
            cursor.moveToNext();
        }
        cursor.close();
    }

    /**
     * Tests if a Group can be entirely updated.
     * (Test for CacheDatabaseHelper.updateGroup(Group group))
     */
    @Test
    public void testDatabaseCanUpdateGroup() {
        Group group = MockFactory.generateMockGroups(5).get(3);
        mDatabaseHelper.addGroup(group);

        User newUser = new User("Johny", new Id(-99999), "john@gmail.com", YieldsApplication.getDefaultUserImage());
        YieldsApplication.addNotKnown(newUser);
        group.addUser(newUser.getId());

        String updatedName = "Updated !";
        group.setName(updatedName);

        Bitmap updatedImage = YieldsApplication.getDefaultUserImage();
        group.setImage(updatedImage);

        group.setType(Group.GroupType.RSS);

        mDatabaseHelper.updateGroup(group);
        Group fromDatabase = mDatabaseHelper.getGroup(group.getId());

        assertEquals(updatedName,
                fromDatabase.getName());

        assertEquals(group.getId().getId(),
                fromDatabase.getId().getId());

        assertEquals(group.getType(),
                fromDatabase.getType());

        assertTrue(compareImages(group.getImage(),
                fromDatabase.getImage()));

        assertTrue(compareUsers(group.getUsers(),
                fromDatabase.getUsers()));

        assertEquals(group.isValidated(),
                fromDatabase.isValidated());
    }

    /**
     * Tests if a Group can be renamed.
     * (Test for updateGroupName(Id groupId, String newGroupName))
     */
    @Test
    public void testDatabaseCanUpdateGroupName() {
        Group group = MockFactory.generateMockGroups(5).get(3);
        mDatabaseHelper.addGroup(group);
        mDatabaseHelper.updateGroupName(group.getId(), "New group name");
        Group fromDatabase = mDatabaseHelper.getGroup(group.getId());

        assertEquals(fromDatabase.getName(),
                "New group name");

        assertEquals(group.getId().getId(),
                fromDatabase.getId().getId());

        assertEquals(Group.GroupType.PRIVATE,
                fromDatabase.getType());

        assertTrue(compareImages(group.getImage(),
                fromDatabase.getImage()));

        assertTrue(compareUsers(group.getUsers(),
                fromDatabase.getUsers()));

        assertEquals(group.isValidated(),
                fromDatabase.isValidated());
    }

    /**
     * Tests if a Group can have it's image changed.
     * (Test for updateGroupImage(Id groupId, Bitmap newGroupImage))
     */
    @Test
    public void testDatabaseCanUpdateGroupImage() {
        Group group = MockFactory.generateMockGroups(1).get(0);
        mDatabaseHelper.addGroup(group);
        mDatabaseHelper.updateGroupImage(group.getId(),
                Bitmap.createBitmap(60, 60, Bitmap.Config.RGB_565));
        Group fromDatabase = mDatabaseHelper.getGroup(group.getId());
        assertEquals(fromDatabase.getName(), group.getName());
        assertEquals(fromDatabase.getId().getId(), group.getId().getId());
        assertTrue(compareUsers(fromDatabase.getUsers(), group.getUsers()));
        assertEquals(fromDatabase.getType(), Group.GroupType.PRIVATE);
        assertTrue(compareImages(Bitmap.createBitmap(60, 60, Bitmap.Config.RGB_565),
                fromDatabase.getImage()));
        assertEquals(group.isValidated(), fromDatabase.isValidated());
    }

    /**
     * Tests if a Group can have it's visibility changed.
     * (Test for updateGroupType(Id groupId, Group.GroupType type))
     */
    @Test
    public void testDatabaseCanUpdateGroupType() {
        Group group = MockFactory.generateMockGroups(1).get(0);
        mDatabaseHelper.addGroup(group);
        mDatabaseHelper.updateGroupType(group.getId(), Group.GroupType.PUBLISHER);
        Group fromDatabase = mDatabaseHelper.getGroup(group.getId());
        assertEquals(fromDatabase.getName(), group.getName());
        assertEquals(fromDatabase.getId().getId(), group.getId().getId());
        assertTrue(compareUsers(fromDatabase.getUsers(), group.getUsers()));
        assertTrue(compareImages(group.getImage(),
                fromDatabase.getImage()));
        assertEquals(fromDatabase.getType(), Group.GroupType.PUBLISHER);
        assertEquals(group.isValidated(), fromDatabase.isValidated());
    }

    /**
     * Tests if a Group can have it's validity changed.
     * (Test for updateGroupValidity(Id groupId, boolean validity))
     */
    @Test
    public void testDatabaseCanUpdateGroupValidity() {
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
        assertEquals(fromDatabase.getType(), group.getType());
    }

    /**
     * Tests if a User can be removed from a Group.
     * (Test for removeUserFromGroup(Id groupId, Id userId))
     */
    @Test
    public void testDatabaseCanRemoveUserFromGroup() {
        Group group = MockFactory.generateMockGroups(3).get(2);
        mDatabaseHelper.addGroup(group);
        ArrayList<User> usersToRemove = new ArrayList<>();
        usersToRemove.add(group.getUsers().get(0));
        mDatabaseHelper.removeUsersFromGroup(group.getId(), usersToRemove);
        Group fromDatabase = mDatabaseHelper.getGroup(group.getId());

        assertEquals(group.getName(),
                fromDatabase.getName());

        assertEquals(group.getId().getId(),
                fromDatabase.getId().getId());

        assertTrue(compareImages(group.getImage(),
                fromDatabase.getImage()));

        assertEquals(group.getUsers().size() - 1,
                fromDatabase.getUsers().size());

        assertEquals(group.isValidated(),
                fromDatabase.isValidated());

        ArrayList<User> usersCopy = new ArrayList<>(group.getUsers());
        usersCopy.remove(0);
        assertTrue(compareUsers(usersCopy,
                fromDatabase.getUsers()));
    }

    /**
     * Tests if a User can be added to a Group.
     * (Test for addUserToGroup(Id groupId, Id userId))
     */
    @Test
    public void testDatabaseCanAddUserToGroup() {
        Group group = MockFactory.generateMockGroups(3).get(2);
        mDatabaseHelper.addGroup(group);
        Id userToAdd = MockFactory.generateMockUsers(15).get(14);
        mDatabaseHelper.addUser(YieldsApplication.getUserFromId(userToAdd));
        ArrayList<User> usersToAdd = new ArrayList<>();
        usersToAdd.add(YieldsApplication.getUserFromId(userToAdd));
        mDatabaseHelper.addUsersToGroup(group.getId(), usersToAdd);
        Group fromDatabase = mDatabaseHelper.getGroup(group.getId());
        assertEquals(fromDatabase.getName(), group.getName());
        assertEquals(fromDatabase.getId().getId(), group.getId().getId());
        assertTrue(compareImages(group.getImage(), fromDatabase.getImage()));
        assertEquals(group.getUsers().size() + 1, fromDatabase.getUsers().size());
        assertEquals(group.isValidated(), fromDatabase.isValidated());

        ArrayList<User> usersCopy = new ArrayList<>(group.getUsers());
        usersCopy.add(YieldsApplication.getUserFromId(userToAdd));
        assertTrue(compareUsers(fromDatabase.getUsers(), usersCopy));
    }

    /**
     * Tests if it can retrieve a Group.
     * (Test for CacheDatabaseHelper.getGroup(ID userID))
     */
    @Test
    public void testDatabaseCanGetGroup() {
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
    }

    /**
     * Tests if it can retrieve all valid Groups.
     * (Test for CacheDatabaseHelper.getAllGroups())
     */
    @Test
    public void testDatabaseCanGetAllGroups() {
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
    }

    /**
     * Test for getMessagesForGroup(Group group, int lowerBoundary, int upperBoundary)
     */
    @Test
    public void testDatabaseGetMessagesForGroup() {
        try {
            User user1 = MockFactory.generateFakeUser("User 1", new Id(1), "u@j.ch");
            User user2 = MockFactory.generateFakeUser("User 2", new Id(2), "u@j.fr");
            List<Id> users = new ArrayList<>();
            users.add(user1.getId());
            users.add(user2.getId());

            Group group = MockFactory.createMockGroup("Group name", new Id(1233), users);
            mDatabaseHelper.addGroup(group);

            for (int i = 0; i < 30; i++) {
                Message message = new Message(new Id(-i),
                        user1.getId(), MockFactory.generateFakeTextContent(i), new Date());
                mDatabaseHelper.addMessage(message, group.getId());
                Thread.sleep(15);
            }
            for (int i = 0; i < 30; i++) {
                Message message = new Message( new Id(-i - 30),
                        user2.getId(), MockFactory.generateFakeTextContent(i + 30), new Date());
                mDatabaseHelper.addMessage(message, group.getId());
                Thread.sleep(15);
            }

            List<Message> messagesFromDatabase = mDatabaseHelper.getMessagesForGroup(group.getId(),
                    new Date(), 10);
            assertEquals(10, messagesFromDatabase.size());
            for (int i = 0; i < 10; i++) {
                Message message = messagesFromDatabase.get(i);
                assertEquals(Long.valueOf(2), message.getSender().getId());
                assertEquals("Mock message #" + (59 - i), ((TextContent) message.getContent()).getText());
            }
        } catch (CacheDatabaseException | InterruptedException exception) {
            fail(exception.getMessage());
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
        boolean idIsCorrect =
                user.getId().getId().equals(cursor.getLong(cursor.getColumnIndex("nodeID")));

        boolean userNameIsCorrect =
                user.getName().equals(cursor.getString(cursor.getColumnIndex("userName")));

        boolean userEmailIsCorrect =
                user.getEmail().equals(cursor.getString(cursor.getColumnIndex("userEmail")));

        Bitmap imageFromCache =
                ImageSerialization.unSerializeImage(cursor.getString(cursor.getColumnIndex("userImage")));
        boolean userImageIsCorrect =
                compareImages(user.getImage(), imageFromCache);

        return idIsCorrect && userNameIsCorrect && userEmailIsCorrect && userImageIsCorrect;
    }

    /**
     * Checks if the Group information is correct in the row which cursor points to.
     *
     * @param cursor The row where the Group information is to be checked.
     * @param group  The Group which should have its data at the row pointed at by cursor.
     * @return A boolean which is true if the information is correct, and false otherwise.
     */
    private boolean checkGroupInformation(Cursor cursor, Group group) {
        boolean idIsCorrect = group.getId().getId() == cursor.getLong(cursor.getColumnIndex("nodeID"));
        boolean groupNameIsCorrect = group.getName().equals(
                cursor.getString(cursor.getColumnIndex("groupName")));

        String idsAsString = cursor.getString(cursor.getColumnIndex("groupUsers"));
        List<Id> idsOfCache = new ArrayList<>();

        String[] idsAsArray = idsAsString.split(",");

        for (String string : idsAsArray) {
            if (!string.isEmpty()) {
                idsOfCache.add(new Id(Long.parseLong(string)));
            }
        }

        List<Id> idsOriginal = new ArrayList<>();
        for (User user : group.getUsers()) {
            idsOriginal.add(user.getId());
        }

        boolean groupUserIDsAreCorrect = true;
        for (Id idOriginal : idsOriginal) {
            boolean hasId = false;
            for (Id idCache : idsOfCache) {
                if (idCache.equals(idOriginal)) {
                    hasId = true;
                }
            }
            groupUserIDsAreCorrect &= hasId;
        }

        String serializedImage = cursor.getString(cursor.getColumnIndex("groupImage"));
        boolean groupImageIsCorrect = compareImages(group.getImage(),
                ImageSerialization.unSerializeImage(serializedImage));
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
    private boolean compareUser(Id user1, Id user2) {
        boolean sameName = YieldsApplication.getUserFromId(user1).getName().equals(
                YieldsApplication.getUserFromId(user2).getName());
        boolean sameEmail = YieldsApplication.getUserFromId(user1).getEmail().equals(
                YieldsApplication.getUserFromId(user2).getEmail());
        boolean sameId = user1.getId().equals(user2.getId());
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
                same = same && compareUser(users1.get(i).getId(), users2.get(i).getId());
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
        String originalImageSerialized = ImageSerialization.serializeImage(originalImage, ImageSerialization.SIZE_IMAGE);
        Bitmap originalImageDeserialized = ImageSerialization.unSerializeImage(originalImageSerialized);
        return originalImageDeserialized.sameAs(imageFromCache);
    }

    /**
     * Compare two Messages (where one passed through the cache).
     *
     * @param originalMessage  The original message (didn't go through cache).
     * @param messageFromCache The message that went through the cache.
     * @return True if the Messages are the same, false otherwise.
     */
    private boolean compareMessages(Message originalMessage, Message messageFromCache) {
        boolean equal = originalMessage.getSender().equals(messageFromCache.getSender());

        equal = equal && originalMessage.getCommentGroupId().getId()
                .equals(messageFromCache.getCommentGroupId().getId());
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