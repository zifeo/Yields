package yields.client.cache;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import yields.client.id.Id;
import yields.client.messages.Content;
import yields.client.messages.Message;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;


public class YieldsDatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "yields.db";

    private static final String TABLE_USERS = "users";
    private static final String TABLE_GROUPS = "groups";
    private static final String TABLE_MESSAGES = "messages";

    private static final String KEY_ID = "id";

    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_IMAGE = "userImage";

    private static final String KEY_GROUP_NAME = "groupName";
    private static final String KEY_GROUP_USERS = "group";
    private static final String KEY_GROUP_IMAGE = "groupImage";

    private static final String KEY_MESSAGE_GROUPID = "messageGroup";
    private static final String KEY_MESSAGE_SENDERID = "messageSender";
    private static final String KEY_MESSAGE_CONTENT = "messageContent";
    private static final String KEY_MESSAGE_DATE = "messageDate";


    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_USER_NAME + " TEXT,"
            + KEY_USER_EMAIL + " TEXT," + KEY_USER_IMAGE + " BLOB" + ")";

    private static final String CREATE_TABLE_GROUPS = "CREATE TABLE " + TABLE_GROUPS
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_GROUP_NAME + " TEXT,"
            + KEY_GROUP_USERS + " TEXT," + KEY_GROUP_IMAGE + " BLOB" + ")";

    private static final String CREATE_TABLE_MESSAGES = "CREATE TABLE " + TABLE_MESSAGES
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_MESSAGE_GROUPID
            + " INTEGER," + KEY_MESSAGE_SENDERID + " ," + KEY_MESSAGE_CONTENT
            + " BLOB," + KEY_MESSAGE_DATE + " DATETIME" + ")";

    public YieldsDatabaseHelper() {
        super(YieldsApplication.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_GROUPS);
        db.execSQL(CREATE_TABLE_MESSAGES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        onCreate(db);
    }

    public List<Message> getMessageIntervalForGroup(Group group, int lowerBoundary, int upperBoundary)
            throws IOException, ClassNotFoundException
    {
        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_MESSAGES + " WHERE "
                + KEY_MESSAGE_GROUPID + " = " + group.getId().getId()+ " ORDER BY "
                + "datetime("+ KEY_MESSAGE_DATE + ")" + " DESC LIMIT" + lowerBoundary
                + (upperBoundary-lowerBoundary);

        Cursor cursor = database.rawQuery(selectQuery, null);

        List<Message> messages = new ArrayList<>();
        if(!cursor.moveToFirst()){
            return messages;
        }
        else{
            do {
                Id id  = new Id(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
                String name = ""; //TODO: Define message's Node name attribute

                List<User> users = group.getUsers();
                Iterator iterator = users.iterator();
                User messageSender = null;
                boolean foundUser = false;
                while(iterator.hasNext() && !foundUser){
                    User tmpUser = (User) iterator.next();
                    Long userID = tmpUser.getId().getId();
                    if(userID == cursor.getLong(cursor.getColumnIndex(KEY_MESSAGE_SENDERID))){
                        messageSender = tmpUser;
                        foundUser = true;
                    }
                }

                byte[] contentAsBytes = cursor.getBlob(cursor.getColumnIndex(KEY_MESSAGE_CONTENT));
                ByteArrayInputStream byteInputStream = new ByteArrayInputStream(contentAsBytes);
                ObjectInputStream objectInputStream = new ObjectInputStream(byteInputStream);
                Content content = (Content) objectInputStream.readObject();

                if(messageSender != null && content != null){
                    messages.add(new Message(name, id, messageSender, content));
                }
            } while (cursor.moveToNext());

            return messages;
        }
    }
}
