package yields.client.cache;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import yields.client.activities.MockFactory;
import yields.client.exceptions.CacheDatabaseException;
import yields.client.id.Id;
import yields.client.messages.Message;
import yields.client.messages.TextContent;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

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
                mDatabaseHelper.deleteUser(user);
            }

            String selectQuery = "SELECT * FROM users;";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            assertTrue(!cursor.moveToFirst());
        } catch (CacheDatabaseException exception){
            fail();
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
            assertEquals(5, cursor.getColumnCount());
            cursor.moveToFirst();

            for (int i = 0; i < 6; i++) {
                assertTrue(checkUserInformation(cursor, users.get(i), i));
                if (i != 5) {
                    cursor.moveToNext();
                }
            }
        } catch (CacheDatabaseException exception){
            fail();
        } finally {
            mDatabaseHelper.clearDatabase();
        }
    }

    /**
     * Tests if it can retrieve a User.
     * (Test for CacheDatabaseHelper.getUser(ID userID))
     */
    @Test
    public void testDatabasCanGetUser() {
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
        } catch (CacheDatabaseException exception){
            fail();
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
        } catch (CacheDatabaseException exception){
            fail();
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
                mDatabaseHelper.deleteGroup(group);
            }

            String selectQuery = "SELECT * FROM groups;";
            Cursor cursor = mDatabase.rawQuery(selectQuery, null);
            assertTrue(!cursor.moveToFirst());
        } catch (CacheDatabaseException exception){
            fail();
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
            assertEquals(5, cursor.getColumnCount());
            cursor.moveToFirst();

            for (int i = 0; i < 6; i++) {
                assertTrue(checkGroupInformation(cursor, groups.get(i), i));
                if (i != 5) {
                    cursor.moveToNext();
                }
            }
        } catch (CacheDatabaseException exception){
            fail();
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
                assertTrue(compareUsers(groups.get(i).getUsers(), groupFromDatabase.getUsers()));
            }
        } catch (CacheDatabaseException exception){
            fail();
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
                assertTrue(compareUsers(groups.get(i).getUsers(), groupFromDatabase.getUsers()));
            }
        } catch (CacheDatabaseException exception){
            fail();
        } finally {
            mDatabaseHelper.clearDatabase();
        }
    }

    /**
     * Test for getMessageIntervalForGroup(Group group, int lowerBoundary, int upperBoundary)
     */
    @Test
    public void testDatabaseGetMessageIntervalForGroup() {
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
                        user1, MockFactory.generateFakeTextContent(i), new Date(), group);
                mDatabaseHelper.addMessage(message);
            }
            for (int i = 0; i < 30; i++) {
                Message message = new Message("Mock node name User2 " + i, new Id(-i - 30),
                        user2, MockFactory.generateFakeTextContent(i), new Date(), group);
                mDatabaseHelper.addMessage(message);
            }

            List<Message> messagesFromDatabase = mDatabaseHelper.getMessageIntervalForGroup(group, 0, 10);
            assertEquals(10, messagesFromDatabase.size());
            for (int i = 0; i < 10; i++) {
                Message message = messagesFromDatabase.get(i);
                assertEquals(new Long(1), message.getSender().getId().getId());
                assertEquals("Mock message #" + i, ((TextContent) message.getContent()).getText());
            }
        } catch (CacheDatabaseException exception){
            fail();
        } finally {
            mDatabaseHelper.clearDatabase();
        }
    }

    /**
     * Checks if the User information is correct in the row which cursor points to.
     *
     * @param cursor The row where the User information is to be checked.
     * @param user   The User which should have its data at the row pointed at by cursor.
     * @param i      The integer used to specify which index the User has when it was created with
     *               MockFactory.generateMockUsers(int number).
     * @return A boolean which is true if the information is correct, and false otherwise.
     */
    private boolean checkUserInformation(Cursor cursor, User user, int i) {
        boolean idIsCorrect = -i == cursor.getLong(cursor.getColumnIndex("nodeID"));
        boolean userNameIsCorrect = ("Mock user name " + i).equals(
                cursor.getString(cursor.getColumnIndex("userName")));
        boolean userEmailIsCorrect = ("Mock email " + i).equals(cursor.getString(
                cursor.getColumnIndex("userEmail")));
        //TODO: Find out how to check image information

        return idIsCorrect && userNameIsCorrect && userEmailIsCorrect;
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
            userIDS.append(user.getId().getId() + ",");
        }
        if (users.size() != 0) {
            userIDS.deleteCharAt(userIDS.length() - 1);
        }
        boolean groupUserIDsAreCorrect = userIDS.toString().equals(
                cursor.getString(cursor.getColumnIndex("groupUsers")));
        //TODO: Find out how to check image information

        return idIsCorrect && groupNameIsCorrect && groupUserIDsAreCorrect;
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
        boolean sameId = user1.getId().getId() == user2.getId().getId();
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
            return sameSize;
        } else {
            boolean same = true;
            for (int i = 0; i < users1.size(); i++) {
                same = same && compareUser(users1.get(i), users2.get(i));
            }
            return same;
        }
    }
}