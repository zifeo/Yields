package yields.client.cache;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import yields.client.activities.MockFactory;
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
            try {
                mDatabaseHelper.addUser(user);
            } catch (IOException e) {
                mDatabaseHelper.clearDatabase();
                fail();
            }
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
            try {
                mDatabaseHelper.addUser(user);
            } catch (IOException e) {
                mDatabaseHelper.clearDatabase();
                fail();
            }
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
            try {
                mDatabaseHelper.addUser(user);
            } catch (IOException e) {
                mDatabaseHelper.clearDatabase();
                fail();
            }
        }

        try {
            for (int i = 0; i < users.size(); i++) {
                User userFromDatabase = mDatabaseHelper.getUser(users.get(i).getId());
                assertEquals(users.get(i).getName(), userFromDatabase.getName());
                assertEquals(users.get(i).getEmail(), userFromDatabase.getEmail());
                assertEquals(users.get(i).getId().getId(), userFromDatabase.getId().getId());
            }
        } catch (Exception e) {
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
        List<User> users = MockFactory.generateMockUsers(6);

        for (User user : users) {
            try {
                mDatabaseHelper.addUser(user);
            } catch (IOException e) {
                mDatabaseHelper.clearDatabase();
                fail();
            }
        }

        try {
            List<User> usersFromDatabase = mDatabaseHelper.getAllUsers();
            assertEquals(users.size(), usersFromDatabase.size());
            for (int i = 0; i < users.size(); i++) {
                assertEquals(users.get(i).getName(), usersFromDatabase.get(i).getName());
                assertEquals(users.get(i).getEmail(), usersFromDatabase.get(i).getEmail());
                assertEquals(users.get(i).getId().getId(), usersFromDatabase.get(i).getId().getId());
            }
        } catch (Exception e) {
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
        List<Group> groups = MockFactory.generateMockGroups(6);
        for (Group group : groups) {
            try {
                mDatabaseHelper.addGroup(group);
            } catch (IOException e) {
                mDatabaseHelper.clearDatabase();
                fail();
            }
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
            try {
                mDatabaseHelper.addGroup(group);
            } catch (IOException e) {
                mDatabaseHelper.clearDatabase();
                fail();
            }
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
            try {
                mDatabaseHelper.addGroup(group);
            } catch (IOException e) {
                mDatabaseHelper.clearDatabase();
                fail();
            }
        }

        try {
            for (int i = 0; i < groups.size(); i++) {
                Group groupFromDatabase = mDatabaseHelper.getGroup(groups.get(i).getId());
                assertEquals(groups.get(i).getName(), groupFromDatabase.getName());
                assertEquals(groups.get(i).getId().getId(), groupFromDatabase.getId().getId());
            }
        } catch (Exception e) {
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
}