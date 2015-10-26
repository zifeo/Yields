package yields.client.cache;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.core.deps.guava.cache.Cache;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import yields.client.R;
import yields.client.activities.MockFactory;
import yields.client.id.Id;
import yields.client.messages.Message;
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
     * Tests if it can add valid Users and if the the Users are correctly added to the database.
     */
    @Test
    public void testDatabaseCanAddUsers() {
        List<User> users =  MockFactory.generateMockUsers(6);

        for(User user : users) {
            try {
                mDatabaseHelper.addUser(user);
            } catch (IOException e) {
                fail();
            }
        }

        Cursor cursor = mDatabase.rawQuery("SELECT * FROM users;", null);
        assertEquals(6, cursor.getCount());
        assertEquals(5, cursor.getColumnCount());
        cursor.moveToFirst();

        for(int i = 0; i < 6; i++) {
            assertTrue(checkUserInformation(cursor, users.get(i), i));
            //TODO: Find out how to check image information
            if(i != 5){
                cursor.moveToNext();
            }
        }
        mDatabaseHelper.clearDatabase();
    }

    /**
     * Tests if it can add valid Groups and if the the Groups are correctly added to the database.
     */
    @Test
    public  void testDatabaseCanAddGroups(){
        List<Group> groups = MockFactory.generateMockGroups(6);
        for(Group group : groups){
            try {
                mDatabaseHelper.addGroup(group);
            } catch (IOException e) {
                fail();
            }
        }

        Cursor cursor = mDatabase.rawQuery("SELECT * FROM groups", null);
        assertEquals(6, cursor.getCount());
        assertEquals(5, cursor.getColumnCount());
        cursor.moveToFirst();

        for(int i = 0; i < 6; i++) {
            assertTrue(checkGroupInformation(cursor, groups.get(i), i));
            if(i != 5){
                cursor.moveToNext();
            }
        }

        mDatabaseHelper.clearDatabase();
    }

    /**
     * Checks if the User information is correct in the row which cursor points to.
     *
     * @param cursor The row where the User information is to be checked.
     * @param user The User which should have its data at the row pointed at by cursor.
     * @param i The integer used to specify which index the User has when it was created with
     *          MockFactory.generateMockUsers(int number).
     * @return A boolean which is true if the information is correct, and false otherwise.
     */
    private boolean checkUserInformation(Cursor cursor, User user, int i){
        boolean idIsCorrect = -i == cursor.getLong(cursor.getColumnIndex("nodeID"));
        boolean userNameIsCorrect = ("Mock user name " + i).equals(
                cursor.getString(cursor.getColumnIndex("userName")));
        boolean userEmailIsCorrect = ("Mock email " + i).equals(cursor.getString(cursor.getColumnIndex("userEmail")));
        return  idIsCorrect && userNameIsCorrect && userEmailIsCorrect;
    }


    /**
     * Checks if the Group information is correct in the row which cursor points to.
     *
     * @param cursor The row where the Group information is to be checked.
     * @param group The Group which should have its data at the row pointed at by cursor.
     * @param i The integer used to specify which index the Group has when it was created with
     *          MockFactory.generateMockGroups(int number).
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

        return  idIsCorrect && groupNameIsCorrect && groupUserIDsAreCorrect;
    }
}
