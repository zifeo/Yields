package yields.client.cache;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import yields.client.exceptions.CacheDatabaseException;
import yields.client.id.Id;
import yields.client.messages.Content;
import yields.client.messages.Message;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * SQLite Database Helper
 */
public class CacheDatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "cache.db";

    private static final String TABLE_USERS = "users";
    private static final String TABLE_GROUPS = "groups";
    private static final String TABLE_MESSAGES = "messages";

    private static final String KEY_ID = "id";

    private static final String KEY_USER_NODEID = "nodeID";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_IMAGE = "userImage";

    private static final String KEY_GROUP_NODEID = "nodeID";
    private static final String KEY_GROUP_NAME = "groupName";
    private static final String KEY_GROUP_USERS = "groupUsers";
    private static final String KEY_GROUP_IMAGE = "groupImage";

    private static final String KEY_MESSAGE_NODEID = "nodeID";
    private static final String KEY_MESSAGE_GROUPID = "messageGroup";
    private static final String KEY_MESSAGE_SENDERID = "messageSender";
    private static final String KEY_MESSAGE_CONTENT = "messageContent";
    private static final String KEY_MESSAGE_DATE = "messageDate";


    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_USER_NODEID + " INTEGER,"
            + KEY_USER_NAME + " TEXT," + KEY_USER_EMAIL + " TEXT," + KEY_USER_IMAGE
            + " BLOB" + ")";

    private static final String CREATE_TABLE_GROUPS = "CREATE TABLE " + TABLE_GROUPS
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_GROUP_NODEID + " INTEGER,"
            + KEY_GROUP_NAME + " TEXT," + KEY_GROUP_USERS + " TEXT," + KEY_GROUP_IMAGE
            + " BLOB" + ")";

    private static final String CREATE_TABLE_MESSAGES = "CREATE TABLE " + TABLE_MESSAGES
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_MESSAGE_NODEID + " INTEGER,"
            + KEY_MESSAGE_GROUPID + " INTEGER," + KEY_MESSAGE_SENDERID + " ," + KEY_MESSAGE_CONTENT
            + " BLOB," + KEY_MESSAGE_DATE + " DATETIME" + ")";

    private final SQLiteDatabase mDatabase;

    /**
     * Main constructor, creates the database.
     */
    public CacheDatabaseHelper() {
        super(YieldsApplication.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
        mDatabase = this.getWritableDatabase();
    }

    /**
     * Called when the database is created for the first time. This is where the creation of
     * tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_GROUPS);
        db.execSQL(CREATE_TABLE_MESSAGES);
    }

    /**
     * Description copied from class: SQLiteOpenHelper Called when the database needs
     * to be upgraded. The implementation should use this method to drop tables, add tables,
     * or do anything else it needs to upgrade to the new schema version.
     *
     * @param db         The database.
     * @param oldVersion The old version identifier.
     * @param newVersion The new version identifier.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        onCreate(db);
    }

    /**
     * Deletes the given Message from the database.
     *
     * @param message The Message to be deleted.
     */
    public void deleteMessage(Message message) {
        mDatabase.delete(TABLE_MESSAGES, KEY_MESSAGE_NODEID + " = ?",
                new String[]{String.valueOf(message.getId().getId())});
    }

    /**
     * Adds the given Message to the database.
     *
     * @param message The Message to the database.
     * @throws IOException If the Message Content could not be serialized.
     */
    public void addMessage(Message message) {
        deleteMessage(message);

        try{
            ContentValues values = new ContentValues();
            values.put(KEY_MESSAGE_NODEID, message.getId().getId());
            values.put(KEY_MESSAGE_GROUPID, message.getReceivingGroup().getId().getId());
            values.put(KEY_MESSAGE_SENDERID, message.getSender().getId().getId());
            values.put(KEY_MESSAGE_CONTENT, serialize(message.getContent()));
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            values.put(KEY_MESSAGE_DATE, dateFormat.format(message.getDate()));

        mDatabase.insert(TABLE_MESSAGES, null, values);
    }

    /**
     * Deletes the User from the database.
     *
     * @param user The User to be deleted.
     */
    public void deleteUser(User user) {
        mDatabase.delete(TABLE_USERS, KEY_USER_NODEID + " = ?",
                new String[]{String.valueOf(user.getId().getId())});
    }

    /**
     * Adds the User from the database.
     *
     * @param user The User to be added.
     */
    public void addUser(User user) throws IOException {
        deleteUser(user);
        ContentValues values = createContentValuesForUser(user);
        mDatabase.insert(TABLE_USERS, null, values);
    }

    /**
     * Updates the User in the database.
     *
     * @param user The User to be updated.
     */
    public void updateUser(User user) throws IOException {
        ContentValues values = createContentValuesForUser(user);
        mDatabase.update(TABLE_USERS, values, KEY_USER_NODEID + " = ?",
                new String[]{String.valueOf(user.getId().getId())});
    }

    /**
     * Retrieves a User according to his Id, returns null if such a User is not in the database.
     *
     * @param userID The Id of the wanted User.
     * @return The User which has userID as his Id or null if there is no such User in the
     * database.
     */
    public User getUser(Id userID) throws IOException, ClassNotFoundException {
        String selectUserQuery = "SELECT * FROM " + TABLE_USERS + " WHERE "
                + KEY_USER_NODEID + " = ?";
        Cursor userCursor = mDatabase.rawQuery(selectUserQuery,
                new String[]{userID.getId().toString()});
        if (!userCursor.moveToFirst()) {
            return null;
        } else {
            String userName = userCursor.getString(userCursor.getColumnIndex(KEY_USER_NAME));
            String userEmail = userCursor.getString(userCursor.getColumnIndex(KEY_USER_EMAIL));
            Bitmap userImage = deserializeBitmap(userCursor.getBlob
                    (userCursor.getColumnIndex(KEY_USER_IMAGE)));
            return new User(userName, userID, userEmail, userImage);
        }
    }

    /**
     * Returns all Users currently in the database.
     *
     * @return An exhaustive List of all Users in the database.
     * @throws IOException            If some User information was not correctly extracted
     *                                from the database.
     * @throws ClassNotFoundException If some User information was not correctly extracted
     *                                from the database.
     */
    public List<User> getAllUsers() throws IOException, ClassNotFoundException {
        String selectAllUsersQuery = "SELECT * FROM " + TABLE_USERS;
        Cursor cursor = mDatabase.rawQuery(selectAllUsersQuery, null);
        List<User> users = new ArrayList<>();
        if (!cursor.moveToFirst()) {
            return users;
        } else {
            do {
                Id userId = new Id(cursor.getLong(cursor.getColumnIndex(KEY_USER_NODEID)));
                users.add(getUser(userId));
            } while (cursor.moveToNext());
            return users;
        }
    }

    /**
     * Deletes the Group and all it's Messages from the database.
     *
     * @param group The Group to be deleted.
     */
    public void deleteGroup(Group group) {
        mDatabase.delete(TABLE_MESSAGES, KEY_MESSAGE_GROUPID + " = ?",
                new String[]{String.valueOf(group.getId().getId())});
        mDatabase.delete(TABLE_GROUPS, KEY_GROUP_NODEID + " = ?",
                new String[]{String.valueOf(group.getId().getId())});
    }

    /**
     * Adds the Group to the database and all the Users it contains if they
     * are not in the database already.
     *
     * @param group The Group to be added.
     * @throws IOException if the Group image could not be serialized.
     */
    public void addGroup(Group group) throws IOException {
        deleteGroup(group);
        ContentValues values = createContentValuesForGroup(group);
        mDatabase.insert(TABLE_GROUPS, null, values);
        for(User user : group.getUsers()){
            addUser(user);
        }
    }

    /**
     * Updates the Group in the database.
     *
     * @param group The Group to be updated.
     * @throws IOException If the updated Group image could not be serialized.
     */
    public void updateGroup(Group group) throws IOException {
        ContentValues values = createContentValuesForGroup(group);
        mDatabase.update(TABLE_GROUPS, values, KEY_GROUP_NODEID + " = ?",
                new String[]{String.valueOf(group.getId().getId())});
    }

    /**
     * Retrieves a Group according to his Id, returns null if such a Group is not in the database.
     *
     * @param groupID The Id of the wanted Group.
     * @return The Group which has groupID as its Id or null if there is no such Group
     * in the database.
     * @throws IOException            If some Group information was not correctly extracted
     *                                from the database.
     * @throws ClassNotFoundException If some Group information was not correctly extracted
     *                                from the database.
     */
    public Group getGroup(Id groupID) throws IOException, ClassNotFoundException {
        String selectQuery = "SELECT * FROM " + TABLE_GROUPS + " WHERE "
                + KEY_GROUP_NODEID + " = ?";
        Cursor cursor = mDatabase.rawQuery(selectQuery,
                new String[]{groupID.getId().toString()});
        if (!cursor.moveToFirst()) {
            return null;
        } else {
            Id groupId = new Id(cursor.getLong(cursor.getColumnIndex(KEY_GROUP_NODEID)));
            String groupName = cursor.getString(cursor.getColumnIndex(KEY_GROUP_NAME));
            Bitmap groupImage = deserializeBitmap(
                    cursor.getBlob(cursor.getColumnIndex(KEY_GROUP_IMAGE)));

            String allUsers = cursor.getString(cursor.getColumnIndex(KEY_GROUP_USERS));
            List<User> groupUsers = new ArrayList<>();
            if (!allUsers.equals("")) {
                String[] usersIDs = allUsers.split(",");
                for (String userID : usersIDs) {
                    User user = getUser(new Id(Long.parseLong(userID)));
                    if (user != null) {
                        groupUsers.add(user);
                    }
                }
            }
            return new Group(groupName, groupId, groupUsers, groupImage);
        }
    }

    /**
     * Returns all Groups currently in the database.
     *
     * @return An exhaustive List of all Groups in the database.
     * @throws IOException            If some Group information was not correctly extracted
     *                                from the database.
     * @throws ClassNotFoundException If some Group information was not correctly extracted
     *                                from the database.
     */
    public List<Group> getAllGroups() throws IOException, ClassNotFoundException {
        String selectQuery = "SELECT * FROM " + TABLE_GROUPS;
        Cursor groupCursor = mDatabase.rawQuery(selectQuery, null);

        List<Group> groups = new ArrayList<>();
        if (!groupCursor.moveToFirst()) {
            return groups;
        } else {
            do {
                Id groupId = new Id(groupCursor.getLong(groupCursor.getColumnIndex(KEY_GROUP_NODEID)));
                Group group = getGroup(groupId);
                if (group != null) {
                    groups.add(group);
                }
            } while (groupCursor.moveToNext());
            return groups;
        }
    }

    /**
     * Retrieves all Messages from a specified Group which are in the interval described by the
     * boundaries.
     *
     * @param group         The Group from which we want to retrieve the Messages.
     * @param lowerBoundary The lower boundary.
     * @param upperBoundary The upper boundary.
     * @return The list of Messages from the interval for the Group.
     * @throws IOException            If some information could not be deserialized.
     * @throws ClassNotFoundException If some information could not be deserialized.
     */
    public List<Message> getMessageIntervalForGroup(Group group, int lowerBoundary, int upperBoundary)
            throws IOException, ClassNotFoundException
    {
        String selectQuery = "SELECT * FROM " + TABLE_MESSAGES + " WHERE "
                + KEY_MESSAGE_GROUPID + " = " + group.getId().getId() + " ORDER BY "
                + "datetime(" + KEY_MESSAGE_DATE + ")" + " DESC LIMIT" + lowerBoundary
                + (upperBoundary - lowerBoundary);

        Cursor cursor = mDatabase.rawQuery(selectQuery, null);

        List<Message> messages = new ArrayList<>();
        if (!cursor.moveToFirst()) {
            return messages;
        } else {
            do {
                Id id = new Id(cursor.getLong(cursor.getColumnIndex(KEY_MESSAGE_NODEID)));
                String nodeName = ""; //TODO: Define message's Node name attribute

                List<User> users = group.getUsers();
                Iterator iterator = users.iterator();
                User messageSender = null;
                boolean foundUser = false;
                while (iterator.hasNext() && !foundUser) {
                    User tmpUser = (User) iterator.next();
                    Long userID = tmpUser.getId().getId();
                    if (userID == cursor.getLong(cursor.getColumnIndex(KEY_MESSAGE_SENDERID))) {
                        messageSender = tmpUser;
                        foundUser = true;
                    }
                }

                byte[] contentAsBytes = cursor.getBlob(cursor.getColumnIndex(KEY_MESSAGE_CONTENT));
                ByteArrayInputStream byteInputStream = new ByteArrayInputStream(contentAsBytes);
                ObjectInputStream objectInputStream = new ObjectInputStream(byteInputStream);
                Content content = (Content) objectInputStream.readObject();

                String dateAsString = cursor.getString(cursor.getColumnIndex(KEY_MESSAGE_DATE));
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                Date date = null;
                try {
                    date = dateFormat.parse(dateAsString);
                    if (messageSender != null && content != null) {
                        messages.add(new Message(nodeName, id, messageSender, content, date, group));
                    }
                } catch (ParseException e) {
                    //TODO : Define what should happen if date could not be parsed.
                }
            } while (cursor.moveToNext());
            return messages;
        }
    }

    /**
     * Clears the database entirely.
     */
    public void clearDatabase() {
        mDatabase.delete("users", null, null);
        mDatabase.delete("groups", null, null);
        mDatabase.delete("messages", null, null);
    }

    /**
     * Serializes the Object into a Byte array.
     *
     * @param object The Object to be serialized.
     * @return The serialized version of the Object.
     * @throws CacheDatabaseException If the Object could not be serialized.
     */
    private static byte[] serialize(Object object) throws CacheDatabaseException {
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream o = null;
        try {
            o = new ObjectOutputStream(byteOutputStream);
            o.writeObject(object);
        } catch (IOException e) {
            throw new CacheDatabaseException("Could not serialize Object !");
        }
        return byteOutputStream.toByteArray();
    }

    /**
     * Deserializes a Byte array into a Bitmap.
     * @param bytes The bytes to be deserialized.
     * @return The deserialized array as a Bitmap object.
     * @throws IOException If the deserialization was not done correctly.
     * @throws ClassNotFoundException If the deserialization was not done correctly.
     */
    private static Bitmap deserializeBitmap(byte[] bytes) throws CacheDatabaseException {
        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
        Bitmap bitmap;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(byteInputStream);
            return (Bitmap) objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            throw new CacheDatabaseException("Could not deserialize Bitmap !");
        } catch (IOException e) {
            throw new CacheDatabaseException("Could not deserialize Bitmap !");
        }
    }

    /**
     * Creates the appropriate row entry (ContentValues) for a User.
     *
     * @param user The User for which a ContentValues is built.
     * @return A ContentValues object which corresponds to the User.
     * @throws IOException If some of the User information could not be serialized.
     */
    private static ContentValues createContentValuesForUser(User user) throws IOException {
        ContentValues values = new ContentValues();
        values.put(KEY_USER_NODEID, user.getId().getId());
        values.put(KEY_USER_NAME, user.getName());
        values.put(KEY_USER_EMAIL, user.getEmail());
        values.put(KEY_USER_IMAGE, serialize(user.getImg()));
        return values;
    }

    /**
     * Creates the appropriate row entry (ContentValues) for a Group.
     *
     * @param group The Group for which a ContentValues is built.
     * @return A ContentValues object which corresponds to the Group.
     * @throws IOException If some of the Group information could not be serialized.
     */
    private static ContentValues createContentValuesForGroup(Group group) throws IOException {
        ContentValues values = new ContentValues();

        values.put(KEY_GROUP_NODEID, group.getId().getId());
        values.put(KEY_GROUP_IMAGE, serialize(group.getCircularImage()));
        values.put(KEY_GROUP_NAME, group.getName());
        StringBuilder userIDS = new StringBuilder();
        List<User> users = group.getUsers();

        for (User user : users) {
            userIDS.append(user.getId().getId() + ",");
        }
        if (users.size() != 0) {
            userIDS.deleteCharAt(userIDS.length() - 1);
        }

        values.put(KEY_GROUP_USERS, userIDS.toString());
        return values;
    }
}
