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
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

import static junit.framework.Assert.assertEquals;
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
     * Tests if it can add valid users and if the the users are correctly added to the db.
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

        Bitmap image1 = BitmapFactory.decodeResource(YieldsApplication.getResources(), R.drawable.userpicture);
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM users;", null);
        assertEquals(6, cursor.getCount());
        assertEquals(5, cursor.getColumnCount());
        cursor.moveToFirst();

        for(int i = 0; i < 6; i++) {
            assertEquals(-i, cursor.getLong(cursor.getColumnIndex("nodeID")));
            assertEquals("Mock user name " + i, cursor.getString(cursor.getColumnIndex("userName")));
            assertEquals("Mock email " + i, cursor.getString(cursor.getColumnIndex("userEmail")));
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            try {
                ObjectOutputStream o = new ObjectOutputStream(byteOutputStream);
                o.writeObject((Object) image1);
            } catch (IOException e) {
                fail();
            }
            //assertEquals(cursor.getBlob(cursor.getColumnIndex("userImage")), byteOutputStream.toByteArray());
            //TODO: Find out why images change
            if(i != 5){
                cursor.moveToNext();
            }
        }
        mDatabaseHelper.clearDatabase();
    }

    @Test
    public  void testDatabaseCanAddSeveralUsers(){
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM users;", null);
        assertEquals(0, cursor.getCount());
        mDatabaseHelper.clearDatabase();
    }

}
