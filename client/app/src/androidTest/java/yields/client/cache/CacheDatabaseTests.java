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
import yields.client.id.Id;
import yields.client.messages.Message;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

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
        mDatabaseHelper.clearDatabase();
    }

    /**
     * Tests if it can add valid Users and if the the Users are correctly added to the database.
     * (Test for CacheDatabaseHelper.addUser(User user))
     */
    @Test
    public void testDatabaseCanAddUsers() {
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
        mDatabaseHelper.clearDatabase();
    }

    /**
     * Tests if it can retrieve a User.
     * (Test for CacheDatabaseHelper.getUser(ID userID))
     */
    @Test
    public void testDatabasCanGetUser() {
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
        mDatabaseHelper.clearDatabase();
    }

    /**
     * Tests if it can retrieve all valid Users.
     * (Test for CacheDatabaseHelper.getAllUsers())
     */
    @Test
    public void testDatabaseCanGetAllUsers() {
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
        mDatabaseHelper.clearDatabase();
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
            mDatabaseHelper.deleteGroup(group);
        }

        String selectQuery = "SELECT * FROM groups;";
        Cursor cursor = mDatabase.rawQuery(selectQuery, null);
        assertTrue(!cursor.moveToFirst());
        mDatabaseHelper.clearDatabase();
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
        assertEquals(7, cursor.getCount());
        assertEquals(5, cursor.getColumnCount());
        cursor.moveToFirst();

        for (int i = 0; i < 6; i++) {
            assertTrue(checkGroupInformation(cursor, groups.get(i), i));
            if (i != 5) {
                cursor.moveToNext();
            }
        }
        mDatabaseHelper.clearDatabase();
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
            assertTrue(compareUsers(groups.get(i).getUsers(), groupFromDatabase.getUsers()));
        }
        mDatabaseHelper.clearDatabase();
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
            assertTrue(compareUsers(groups.get(i).getUsers(), groupFromDatabase.getUsers()));
        }
        mDatabaseHelper.clearDatabase();
    }

    /**
     * Test for getMessageIntervalForGroup(Group group, int lowerBoundary, int upperBoundary)
     */
    @Test
    public void testDatabaseGetMessageIntervalForGroup(){
        List<Message> messages = new ArrayList<>();
        User user1 = MockFactory.generateFakeUser("User 1", new Id(1), "u@j.ch");
        User user2 = MockFactory.generateFakeUser("User 2", new Id(2), "u@j.fr");
        List <User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        Group group = MockFactory.createMockGroup("Group name", new Id(1233), users);

        for(int i = 0; i < 30; i++){
            Message message = new Message("Mock node name " + i, new Id(-i),
                   user1, MockFactory.generateFakeTextContent(i), new Date(), group);
            mDatabaseHelper.addMessage(message);
        }
        for(int i = 0; i < 30; i++){
            Message message = new Message("Mock node name " + i, new Id(-i),
                    user2, MockFactory.generateFakeTextContent(i), new Date(), group);
            mDatabaseHelper.addMessage(message);
        }

        mDatabaseHelper.addGroup(group);

        List<Message> messagesFromDatabase = mDatabaseHelper.getMessageIntervalForGroup(group, 1, 10);
        assertEquals(10, messagesFromDatabase.size());
        mDatabaseHelper.clearDatabase();
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

    private boolean compareUser(User user1, User user2){
        boolean sameName = user1.getName().equals(user2.getName());
        boolean sameEmail = user1.getEmail().equals(user2.getEmail());
        boolean sameId = user1.getId().getId() == user2.getId().getId();
        return  sameName && sameEmail && sameId;
    }

    private boolean compareUsers(List<User> users1, List<User> users2){
        boolean sameSize = users1.size() == users2.size();
        if(!sameSize) {
            return sameSize;
        } else {
            boolean same = true;
            for (int i = 0; i < users1.size(); i++) {
                same = same && compareUser(users1.get(i), users2.get(i));
            }
            return same;
        }
    }

    private String createUserIDsString(Group group){
        StringBuilder userIDS = new StringBuilder();
        List<User> users = group.getUsers();
        for (User user : users) {
            userIDS.append(user.getId().getId() + ",");
        }
        if (users.size() != 0) {
            userIDS.deleteCharAt(userIDS.length() - 1);
        }
        return userIDS.toString();
    }

    private List<Id> retrieveUserIDsFromString(String string){
        ArrayList<Id> ids = new ArrayList<>();
        if (!string.equals("")) {
            String[] usersIDs = string.split(",");
            for (String userID : usersIDs) {
                Id id = new Id(Long.parseLong(userID));
                ids.add(id);
            }
        }
        return ids;
    }
}