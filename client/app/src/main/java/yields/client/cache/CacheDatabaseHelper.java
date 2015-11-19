package yields.client.cache;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

import yields.client.exceptions.CacheDatabaseException;
import yields.client.exceptions.ContentException;
import yields.client.id.Id;
import yields.client.messages.Content;
import yields.client.messages.ImageContent;
import yields.client.messages.Message;
import yields.client.messages.TextContent;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.serverconnection.DateSerialization;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * SQLite Database Helper
 */
public class CacheDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "CacheDatabase";

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
    private static final String KEY_GROUP_VISIBILITY = "groupVisibility";

    private static final String KEY_MESSAGE_NODEID = "nodeID";
    private static final String KEY_MESSAGE_GROUPID = "messageGroup";
    private static final String KEY_MESSAGE_SENDERID = "messageSender";
    private static final String KEY_MESSAGE_TEXT = "messageText";
    private static final String KEY_MESSAGE_CONTENT = "messageContent";
    private static final String KEY_MESSAGE_CONTENT_TYPE = "messageContentType";
    private static final String KEY_MESSAGE_DATE = "messageDate";


    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_USER_NODEID + " TEXT,"
            + KEY_USER_NAME + " TEXT," + KEY_USER_EMAIL + " TEXT," + KEY_USER_IMAGE
            + " BLOB" + ")";

    private static final String CREATE_TABLE_GROUPS = "CREATE TABLE " + TABLE_GROUPS
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_GROUP_NODEID + " TEXT,"
            + KEY_GROUP_NAME + " TEXT," + KEY_GROUP_USERS + " TEXT," + KEY_GROUP_IMAGE
            + " BLOB," + KEY_GROUP_VISIBILITY + " TEXT" + ")";

    private static final String CREATE_TABLE_MESSAGES = "CREATE TABLE " + TABLE_MESSAGES
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_MESSAGE_NODEID + " TEXT,"
            + KEY_MESSAGE_GROUPID + " TEXT," + KEY_MESSAGE_SENDERID + " TEXT,"
            + KEY_MESSAGE_TEXT + " TEXT," + KEY_MESSAGE_CONTENT_TYPE + " TEXT,"
            + KEY_MESSAGE_CONTENT + " BLOB," + KEY_MESSAGE_DATE + " TEXT" + ")";

    private final SQLiteDatabase mDatabase;

    /**
     * Main constructor, creates the database.
     *
     * @param context The contex for the database.
     */
    public CacheDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mDatabase = this.getWritableDatabase();
    }

    /**
     * Secondary constructor, creates the database with the Yields application context.
     */
    public CacheDatabaseHelper() {
        this(YieldsApplication.getApplicationContext());
    }



    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
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
     * Description copied from class: SQLiteOpenHelper Called when the database
     * needs to be upgraded. The implementation should use this method to drop
     * tables, add tables, or do anything else it needs to upgrade to the new
     * schema version.
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
     * @param messageId The Id of the Message to be deleted.
     */
    public void deleteMessage(Id messageId, Id groupId) {
        Objects.requireNonNull(messageId);

        mDatabase.delete(TABLE_MESSAGES, KEY_MESSAGE_NODEID + " = ? AND " + KEY_MESSAGE_GROUPID
                        + " = ?", new String[]{String.valueOf(messageId.getId()), groupId.getId()});
    }

    /**
     * Adds the given Message to the database.
     *
     * @param message The Message to be added to the database.
     * @param groupId The Id of the Group to which this Message was sent.
     * @throws CacheDatabaseException If the message could not be added to the
     *                                database.
     */
    public void addMessage(Message message, Id groupId)
            throws CacheDatabaseException {
        Objects.requireNonNull(message);
        Objects.requireNonNull(groupId);

        addUser(message.getSender());

        try {
            String selectQuery = "SELECT * FROM " + TABLE_MESSAGES
                    + " WHERE " + KEY_MESSAGE_NODEID + " = ?";
            Cursor cursor = mDatabase.rawQuery(selectQuery,
                    new String[]{message.getId().getId()});
            if (cursor.getCount() != 0) {
                cursor.close();
                deleteMessage(message.getId(), groupId);
            }
            mDatabase.insert(TABLE_MESSAGES, null,
                    createContentValuesForMessage(message, groupId));
            cursor.close();
        } catch (CacheDatabaseException exception) {
            Log.d(TAG, "Unable to insert Message with id: "
                    + message.getId().getId(), exception);
            throw exception;
        }
    }

    /**
     * Deletes the User from the database.
     *
     * @param userId The Id of the User to be deleted.
     */
    public void deleteUser(Id userId) {
        Objects.requireNonNull(userId);

        mDatabase.delete(TABLE_USERS, KEY_USER_NODEID + " = ?",
                new String[]{String.valueOf(userId.getId())});
    }

    /**
     * Adds the User from the database.
     *
     * @param user The User to be added.
     * @throws CacheDatabaseException If the User could not be inserted into the
     *                                database.
     */
    public void addUser(User user)
            throws CacheDatabaseException {
        Objects.requireNonNull(user);

        String selectQuery = "SELECT * FROM " + TABLE_USERS
                + " WHERE " + KEY_USER_NODEID + " = ?";
        Cursor cursor = mDatabase.rawQuery(selectQuery,
                new String[]{user.getId().getId()});
        if (cursor.getCount() == 1) {
            cursor.close();
            updateUser(user);
        } else if (cursor.getCount() > 1) { //There should not be several Users with the same Id.
            deleteUser(user.getId());
        }

        if (cursor.getCount() > 1 || cursor.getCount() == 0) {
            try {
                ContentValues values = createContentValuesForUser(user);
                cursor.close();
                mDatabase.insert(TABLE_USERS, null, values);
            } catch (CacheDatabaseException exception) {
                Log.d(TAG, "Unable to insert User with id: "
                        + user.getId().getId(), exception);
                throw exception;
            }
        }
    }

    /**
     * Updates the User in the database.
     *
     * @param user The User to be updated.
     * @throws CacheDatabaseException If the User could not be updated in the
     *                                database.
     */
    public void updateUser(User user)
            throws CacheDatabaseException {
        Objects.requireNonNull(user);

        try {
            ContentValues values = createContentValuesForUser(user);
            mDatabase.update(TABLE_USERS, values, KEY_USER_NODEID + " = ?",
                    new String[]{user.getId().getId()});
        } catch (CacheDatabaseException exception) {
            Log.d(TAG, "Unable to update User with id: " + user.getId().getId(),
                    exception);
            throw exception;
        }
    }

    /**
     * Retrieves a User according to its Id, returns null if such a User is not
     * in the database. Also returns null if the User could not be correctly
     * extracted from the database.
     *
     * @param userID The Id of the wanted User.
     * @return The User which has userID as its Id or null if there is no such
     * User in the database.
     */
    public User getUser(Id userID) {
        Objects.requireNonNull(userID);

        String selectUserQuery = "SELECT * FROM " + TABLE_USERS + " WHERE "
                + KEY_USER_NODEID + " = ?";
        Cursor userCursor = mDatabase.rawQuery(selectUserQuery,
                new String[]{userID.getId()});
        if (!userCursor.moveToFirst()) {
            userCursor.close();
            return null;
        } else {
            String userName = userCursor.getString(
                    userCursor.getColumnIndex(KEY_USER_NAME));
            String userEmail = userCursor.getString(
                    userCursor.getColumnIndex(KEY_USER_EMAIL));
            Bitmap userImage = deserializeBitmap(userCursor.getBlob
                    (userCursor.getColumnIndex(KEY_USER_IMAGE)));
            userCursor.close();
            return new User(userName, userID, userEmail, userImage);
        }
    }

    /**
     * Returns all Users currently in the database.
     *
     * @return An exhaustive List of all Users in the database.
     */
    public List<User> getAllUsers() {
        String selectAllUsersQuery = "SELECT * FROM " + TABLE_USERS;
        Cursor cursor = mDatabase.rawQuery(selectAllUsersQuery, null);
        List<User> users = new ArrayList<>();
        if (!cursor.moveToFirst()) {
            cursor.close();
            return users;
        } else {
            do {
                Id userId = new Id(cursor.getLong(
                        cursor.getColumnIndex(KEY_USER_NODEID)));
                users.add(getUser(userId));
            } while (cursor.moveToNext());
            cursor.close();
            return users;
        }
    }

    /**
     * Deletes the Group and all it's Messages from the database.
     *
     * @param group The Group to be deleted.
     */
    public void deleteGroup(Group group) {
        Objects.requireNonNull(group);

        mDatabase.delete(TABLE_MESSAGES, KEY_MESSAGE_GROUPID + " = ?",
                new String[]{group.getId().getId()});
        mDatabase.delete(TABLE_GROUPS, KEY_GROUP_NODEID + " = ?",
                new String[]{group.getId().getId()});
    }

    /**
     * Adds the Group to the database and all the Users it contains if they
     * are not in the database already.
     *
     * @param group The Group to be added.
     * @throws CacheDatabaseException If the Group could not be inserted into
     *                                the database.
     */
    public void addGroup(Group group)
            throws CacheDatabaseException {
        Objects.requireNonNull(group);

        String selectQuery = "SELECT * FROM " + TABLE_GROUPS
                + " WHERE " + KEY_GROUP_NODEID + " = ?";
        Cursor cursor = mDatabase.rawQuery(selectQuery,
                new String[]{group.getId().getId()});
        if (cursor.getCount() >= 1) {
            cursor.close();
            updateGroup(group);
        } else if (cursor.getCount() > 1) { //There should not be several Groups with the same Id.
            deleteGroup(group);
        }

        if (cursor.getCount() > 1 || cursor.getCount() == 0) {
            try {
                ContentValues values = createContentValuesForGroup(group);
                mDatabase.insert(TABLE_GROUPS, null, values);
                for (User user : group.getUsers()) {
                    addUser(user);
                }
                for (Message message : group.getLastMessages().values()) {
                    addMessage(message, group.getId());
                }
                cursor.close();
            } catch (CacheDatabaseException exception) {
                Log.d(TAG, "Unable to add Group with id: "
                        + group.getId().getId(), exception);
                throw new CacheDatabaseException(exception);
            }
        }
    }

    /**
     * Updates the Group in the database.
     *
     * @param group The Group to be updated.
     * @throws CacheDatabaseException If the Group could not be updated
     *                                in the database.
     */
    public void updateGroup(Group group)
            throws CacheDatabaseException {
        Objects.requireNonNull(group);

        try {
            ContentValues values = createContentValuesForGroup(group);
            mDatabase.update(TABLE_GROUPS, values, KEY_GROUP_NODEID + " = ?",
                    new String[]{group.getId().getId()});
            for (User user : group.getUsers()) {
                addUser(user);
            }
            for (Message message : group.getLastMessages().values()) {
                addMessage(message, group.getId());
            }
        } catch (CacheDatabaseException exception) {
            Log.d(TAG, "Unable to update Group with id: "
                    + group.getId().getId(), exception);
            throw exception;
        }
    }


    /**
     * Updates the name of a Group in the database.
     *
     * @param groupId      The Id field of the Group that will have it's name changed.
     * @param newGroupName The new name of the Group.
     */
    public void updateGroupName(Id groupId, String newGroupName) {
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(newGroupName);

        ContentValues values = new ContentValues();
        values.put(KEY_GROUP_NAME, newGroupName);
        mDatabase.update(TABLE_GROUPS, values, KEY_GROUP_NODEID + " = ?",
                new String[]{groupId.getId()});
    }

    /**
     * Updates the image of a Group in the database.
     *
     * @param groupId       The Id field of the Group that will have it's image changed.
     * @param newGroupImage The new image for the Group.
     */
    public void updateGroupImage(Id groupId, Bitmap newGroupImage) {
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(newGroupImage);

        ContentValues values = new ContentValues();
        values.put(KEY_GROUP_IMAGE, serializeBitmap(newGroupImage));
        mDatabase.update(TABLE_GROUPS, values, KEY_GROUP_NODEID + " = ?",
                new String[]{groupId.getId()});
    }

    /**
     * Updates the visibility of a Group in the database.
     *
     * @param groupId    The Id field of the Group that will have it's visibility changed.
     * @param visibility The new visibility of the Group.
     */
    public void updateGroupVisibility(Id groupId, Group.GroupVisibility visibility) {
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(visibility);

        ContentValues values = new ContentValues();
        values.put(KEY_GROUP_VISIBILITY, visibility.getValue());
        mDatabase.update(TABLE_GROUPS, values, KEY_GROUP_NODEID + " = ?",
                new String[]{groupId.getId()});
    }

    /**
     * Removes a User from a Group in the database.
     *
     * @param groupId The Id of the Group form which the User shall be removed.
     * @param userId  The Id of the User that will bre removed from the Group.
     */
    public void removeUserFromGroup(Id groupId, Id userId) {
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(userId);

        List<Id> ids = getUserIdsFromGroup(groupId);
        Iterator<Id> idIterator = ids.iterator();
        while (idIterator.hasNext()) {
            if (idIterator.next().getId().equals(userId.getId())) {
                idIterator.remove();
            }
        }
        ContentValues values = new ContentValues();
        values.put(KEY_GROUP_USERS, getStringFromIds(ids));
        mDatabase.update(TABLE_GROUPS, values, KEY_GROUP_NODEID + " = ?",
                new String[]{groupId.getId()});
    }

    /**
     * Adds a User to a Group in the database.
     *
     * @param groupId The Id of the Group to which the User shall be added.
     * @param user    The User that will be added to the Group.
     * @throws CacheDatabaseException If the User couldn't be added in the database.
     */
    public void addUserToGroup(Id groupId, User user)
            throws CacheDatabaseException {
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(user);

        addUser(user);

        List<Id> ids = getUserIdsFromGroup(groupId);
        boolean userIsInCache = false;
        for (Id id : ids) {
            if (id.getId().equals(user.getId().getId())) {
                userIsInCache = true;
            }
        }
        if (!userIsInCache) {
            ids.add(user.getId());
            ContentValues values = new ContentValues();
            values.put(KEY_GROUP_USERS, getStringFromIds(ids));
            mDatabase.update(TABLE_GROUPS, values, KEY_GROUP_NODEID + " = ?",
                    new String[]{groupId.getId()});
        }
    }

    /**
     * Retrieves the Ids of all the Users for a Group.
     * Also returns null if the Group is not in the database.
     *
     * @param groupId The Group's Id.
     * @return The Users' id from the Group, or null if there is no such Group
     * in the database.
     */
    public List<Id> getUserIdsFromGroup(Id groupId) {
        Objects.requireNonNull(groupId);

        String selectQuery = "SELECT * FROM " + TABLE_GROUPS + " WHERE "
                + KEY_GROUP_NODEID + " = ?";
        Cursor cursor = mDatabase.rawQuery(selectQuery,
                new String[]{groupId.getId()});
        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        } else {
            String users = cursor.getString(cursor.getColumnIndex(KEY_GROUP_USERS));
            cursor.close();
            return getIdListFromString(users);
        }
    }

    /**
     * Retrieves a Group according to its Id, returns null if such a Group is
     * not in the database. Also returns null if the Group could not be
     * correctly extracted from the database.
     *
     * @param groupId The Id of the wanted Group.
     * @return The Group which has groupId as its Id or null if there is no
     * such Group in the database.
     */
    public Group getGroup(Id groupId)
            throws CacheDatabaseException {
        Objects.requireNonNull(groupId);

        String selectQuery = "SELECT * FROM " + TABLE_GROUPS + " WHERE "
                + KEY_GROUP_NODEID + " = ?";
        Cursor cursor = mDatabase.rawQuery(selectQuery,
                new String[]{groupId.getId()});

        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        } else {
            String groupName = cursor.getString(
                    cursor.getColumnIndex(KEY_GROUP_NAME));
            Bitmap groupImage = deserializeBitmap(
                    cursor.getBlob(cursor.getColumnIndex(KEY_GROUP_IMAGE)));
            Group.GroupVisibility groupVisibility = Group.GroupVisibility.valueOf(
                    cursor.getString(cursor.getColumnIndex(KEY_GROUP_VISIBILITY)));

            String allUsers = cursor.getString(
                    cursor.getColumnIndex(KEY_GROUP_USERS));
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
            cursor.close();
            return new Group(groupName, groupId, groupUsers, groupImage, groupVisibility);
        }
    }

    /**
     * Returns all Groups currently in the database.
     *
     * @return An exhaustive List of all Groups in the database.
     */
    public List<Group> getAllGroups()
            throws CacheDatabaseException {
        String selectQuery = "SELECT * FROM " + TABLE_GROUPS;
        Cursor groupCursor = mDatabase.rawQuery(selectQuery, null);

        List<Group> groups = new ArrayList<>();
        if (!groupCursor.moveToFirst()) {
            groupCursor.close();
            return groups;
        } else {
            do {
                Id groupId = new Id(groupCursor.getLong(
                        groupCursor.getColumnIndex(KEY_GROUP_NODEID)));
                Group group = getGroup(groupId);
                if (group != null) {
                    groups.add(group);
                }
            } while (groupCursor.moveToNext());
            groupCursor.close();
            return groups;
        }
    }

    /**
     * Retrieves all Messages from a specified Group which are older than the Date
     * passed as an argument. Only messageCount Messages are retrieves, or less if
     * there aren't as many.
     *
     * @param group        The Group from which we want to retrieve the Messages.
     * @param furthestDate The Date boundary.
     * @param messageCount The number of Messages that should be retrieved..
     * @return The list of Messages from the Group.
     * @throws CacheDatabaseException If the database was unable to fetch some
     *                                information.
     */
    public List<Message> getMessagesForGroup(Group group, Date furthestDate, int messageCount)
            throws CacheDatabaseException {
        Objects.requireNonNull(group);
        Objects.requireNonNull(furthestDate);

        addGroup(group);

        if (messageCount < 0) {
            throw new IllegalArgumentException("Illegal messageCount ! The number of messages must" +
                    "be equal or grater than 0 !");
        }

        String selectQuery = "SELECT * FROM " + TABLE_MESSAGES + " WHERE "
                + KEY_MESSAGE_GROUPID + " = ? " + " ORDER BY "
                + "strftime('%Y-%m-%d %H:%M:%f', " + KEY_MESSAGE_DATE + ") DESC";

        Cursor cursor = mDatabase.rawQuery(selectQuery,
                new String[]{group.getId().getId()});

        List<Message> messages = new ArrayList<>();
        if (!cursor.moveToFirst()) {
            cursor.close();
            return new ArrayList<>();
        } else if (!goToFirstOccurrenceOfEarlierDate(cursor, furthestDate)) {
            cursor.close();
            return new ArrayList<>();
        } else {
            int counter = 0;
            do {
                Id id = new Id(cursor.getLong(cursor.getColumnIndex(KEY_MESSAGE_NODEID)));
                String nodeName = ""; //TODO: Define message's Node name attribute

                List<User> users = group.getUsers();
                Iterator iterator = users.iterator();
                User messageSender = null;
                boolean foundUser = false;
                while (iterator.hasNext() && !foundUser) {
                    User tmpUser = (User) iterator.next();
                    String userID = tmpUser.getId().getId();
                    if (userID.equals(cursor.getString(cursor.getColumnIndex(KEY_MESSAGE_SENDERID)))) {
                        messageSender = tmpUser;
                        foundUser = true;
                    }
                }

                try {
                    String contentType = cursor.getString(
                            cursor.getColumnIndex(KEY_MESSAGE_CONTENT_TYPE));
                    Content content = deserializeContent(cursor,
                            Content.ContentType.valueOf(contentType));
                    String dateAsString = cursor.getString(cursor.getColumnIndex(KEY_MESSAGE_DATE));
                    Date date = DateSerialization.toDateForCache(dateAsString);
                    if (messageSender != null && content != null) {
                        messages.add(new Message(nodeName, id, messageSender, content, date));
                    }
                } catch (CacheDatabaseException | ParseException exception) {
                    Log.d(TAG, "Unable to retrieve Messages from Group with id: "
                            + group.getId().getId(), exception);
                    throw new CacheDatabaseException("Unable to retrieve Messages from Group "
                            + group.getId().getId());
                }
                counter++;
            } while (cursor.moveToNext() && counter < 10);
            cursor.close();
            return messages;
        }
    }

    /**
     * Moves the Cursor passed as an argument to the first occurrence of a row where the DATE field
     * contains a Date that is older than the Date passed as an argument.
     * It returns true if the row exists and false otherwise.
     *
     * @param cursor       The cursor than is moved until it reaches an accepting row.
     * @param furthestDate The Date that serves as a limit to this search.
     * @return True if there exists such a row, false otherwise.
     * @throws CacheDatabaseException If one row that the cursor passed couldn't be read.
     */
    private boolean goToFirstOccurrenceOfEarlierDate(Cursor cursor, Date furthestDate)
            throws CacheDatabaseException {
        boolean done = false;
        boolean retValue = false;
        while (!done) {
            try {
                String dateAsString = cursor.getString(cursor.getColumnIndex(KEY_MESSAGE_DATE));
                Date date = DateSerialization.toDateForCache(dateAsString);
                if (date.compareTo(furthestDate) <= 0) {
                    done = true;
                    retValue = true;
                } else if (!cursor.moveToNext()) {
                    done = true;
                    retValue = false;
                }
            } catch (ParseException e) {
                throw new CacheDatabaseException("Unable to retrieve Message !");
            }
        }
        return retValue;
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
     * Deletes the Database file in the file system.
     */
    public static void deleteDatabase() {
        YieldsApplication.getApplicationContext().deleteDatabase(DATABASE_NAME);
    }

    /**
     * Serializes a Bitmap image into a byte array.
     *
     * @param bitmap The Bitmap image to be serialized.
     * @return The serialized version of the bitmap.
     */
    private static byte[] serializeBitmap(Bitmap bitmap) {
        Objects.requireNonNull(bitmap);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    /**
     * Deserializes a Byte array into a Bitmap.
     *
     * @param bytes The bytes to be deserialized.
     * @return The deserialized array as a Bitmap object.
     */
    private static Bitmap deserializeBitmap(byte[] bytes) {
        Objects.requireNonNull(bytes);

        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * Serializes a Content into a byte array.
     *
     * @param content The Content to be serialized.
     * @return The bytes corresponding to the serialization of the Content.
     * @throws CacheDatabaseException If the Content could not be serialized.
     */
    private static byte[] serializeContent(Content content)
            throws CacheDatabaseException {
        Objects.requireNonNull(content);

        try {
            switch (content.getType()) {
                case TEXT:
                    return serializeTextContent((TextContent) content);
                case IMAGE:
                    return serializeImageContent((ImageContent) content);
                default:
                    throw new ContentException("No such content exists !");
            }
        } catch (CacheDatabaseException exception) {
            Log.d(TAG, "Could not serialize content !", exception);
            throw exception;
        }
    }

    /**
     * Deserializes a byte array into a Content.
     *
     * @param cursor      The byte array to be deserialized.
     * @param contentType The type of the Content.
     * @return The Content corresponding to the byte array.
     * @throws CacheDatabaseException If the Content could not be deserialized.
     */
    private static Content deserializeContent(Cursor cursor, Content.ContentType contentType)
            throws CacheDatabaseException {
        Objects.requireNonNull(contentType);
        Objects.requireNonNull(cursor);

        try {
            switch (contentType) {
                case TEXT:
                    return deserializeTextContent(cursor);
                case IMAGE:
                    return deserializeImageContent(cursor);
                default:
                    throw new ContentException("No such content exists !");
            }
        } catch (CacheDatabaseException exception) {
            Log.d(TAG, "Could not deserialize content !", exception);
            throw new CacheDatabaseException("Could not deserialize Content !");
        }
    }

    /**
     * Serializes the TextContent into a Byte array.
     *
     * @param content The TextContent to be serialized.
     * @return The serialized version of the TextContent.
     * @throws CacheDatabaseException if the TextContent could not be serialized.
     */
    private static byte[] serializeImageContent(ImageContent content) {
        Objects.requireNonNull(content);

        return serializeBitmap(content.getImage());
    }

    /**
     * Deserializes a Byte array into an ImageContent.
     *
     * @param cursor The bytes to be deserialized.
     * @return The deserialized version of the bytes.
     */
    private static Content deserializeImageContent(Cursor cursor) {
        Bitmap image = deserializeBitmap(cursor.getBlob(cursor.getColumnIndex(KEY_MESSAGE_CONTENT)));
        String caption = cursor.getString(cursor.getColumnIndex(KEY_MESSAGE_TEXT));
        return new ImageContent(image, caption);
    }

    /**
     * Serializes the TextContent into a Byte array.
     *
     * @param content The TextContent to be serialized.
     * @return The serialized version of the TextContent.
     * @throws CacheDatabaseException if the TextContent could not be serialized.
     */
    private static byte[] serializeTextContent(TextContent content)
            throws CacheDatabaseException {
        Objects.requireNonNull(content);

        try {
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream outputStream = new ObjectOutputStream(byteOutputStream);
            outputStream.writeUTF(content.getText());
            outputStream.close();
            byte[] bytes = byteOutputStream.toByteArray();
            byteOutputStream.close();
            return bytes;
        } catch (IOException exception) {
            Log.d(TAG, "Could not serialize TextContent !", exception);
            throw new CacheDatabaseException("Could not serialize TextContent !");
        }
    }

    /**
     * Deserializes a Byte array into a TextContent.
     *
     * @param cursor The bytes to be deserialized.
     * @return The deserialized version of the bytes.
     * @throws CacheDatabaseException if the TextContent could not be deserialized.
     */
    private static Content deserializeTextContent(Cursor cursor)
            throws CacheDatabaseException {
        Objects.requireNonNull(cursor);

        try {
            byte[] bytes = cursor.getBlob(cursor.getColumnIndex(KEY_MESSAGE_CONTENT));
            ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteInputStream);
            String text = objectInputStream.readUTF();
            TextContent content = new TextContent(text);
            objectInputStream.close();
            byteInputStream.close();
            return content;
        } catch (IOException exception) {
            Log.d(TAG, "Could not deserialize TextContent !", exception);
            throw new CacheDatabaseException("Could not deserialize Content !");
        }
    }

    /**
     * Creates the appropriate row entry (ContentValues) for a Message.
     *
     * @param message The Message for which a ContentValues is built.
     * @param groupId The Id of the Group to which the Message is added.
     * @return A ContentValues object which corresponds to the Message.
     * @throws CacheDatabaseException If some of the Message information could
     *                                not be serialized.
     */
    private static ContentValues createContentValuesForMessage(Message message, Id groupId)
            throws CacheDatabaseException {
        Objects.requireNonNull(message);
        Objects.requireNonNull(groupId);

        ContentValues values = new ContentValues();
        values.put(KEY_MESSAGE_NODEID, message.getId().getId());
        values.put(KEY_MESSAGE_SENDERID, message.getSender().getId().getId());
        values.put(KEY_MESSAGE_GROUPID, groupId.getId());
        values.put(KEY_MESSAGE_TEXT, message.getContent().getTextForRequest());
        values.put(KEY_MESSAGE_CONTENT_TYPE, message.getContent().getType().getType());
        values.put(KEY_MESSAGE_CONTENT, serializeContent(message.getContent()));
        values.put(KEY_MESSAGE_DATE, DateSerialization.toStringForCache(message.getDate()));

        return values;
    }

    /**
     * Creates the appropriate row entry (ContentValues) for a User.
     *
     * @param user The User for which a ContentValues is built.
     * @return A ContentValues object which corresponds to the User.
     * @throws CacheDatabaseException If some of the User information
     *                                could not be serialized.
     */
    private static ContentValues createContentValuesForUser(User user)
            throws CacheDatabaseException {
        Objects.requireNonNull(user);

        ContentValues values = new ContentValues();
        values.put(KEY_USER_NODEID, user.getId().getId());
        values.put(KEY_USER_NAME, user.getName());
        values.put(KEY_USER_EMAIL, user.getEmail());
        values.put(KEY_USER_IMAGE, serializeBitmap(user.getImg()));
        return values;
    }

    /**
     * Creates the appropriate row entry (ContentValues) for a Group.
     *
     * @param group The Group for which a ContentValues is built.
     * @return A ContentValues object which corresponds to the Group.
     * @throws CacheDatabaseException If some of the Group information
     *                                could not be serialized.
     */
    private static ContentValues createContentValuesForGroup(Group group)
            throws CacheDatabaseException {
        Objects.requireNonNull(group);

        ContentValues values = new ContentValues();
        values.put(KEY_GROUP_NODEID, group.getId().getId());
        values.put(KEY_GROUP_IMAGE, serializeBitmap(group.getImage()));
        values.put(KEY_GROUP_VISIBILITY, group.getVisibility().getValue());
        values.put(KEY_GROUP_NAME, group.getName());
        List<User> users = group.getUsers();
        List<Id> userIDs = new ArrayList<>();
        for (User user : users) {
            userIDs.add(user.getId());
        }
        values.put(KEY_GROUP_USERS, getStringFromIds(userIDs));
        return values;
    }

    /**
     * Transform a String of ids concatenated with a comma into a List of Id.
     *
     * @param idsAsString The String of Ids.
     * @return The List of Ids corresponding to idsAdString.
     */
    private static List<Id> getIdListFromString(String idsAsString) {
        Objects.requireNonNull(idsAsString);

        String[] idsAsArray = idsAsString.split(",");
        List<Id> ids = new ArrayList<>();
        for (String string : idsAsArray) {
            ids.add(new Id(string));
        }
        return ids;
    }

    /**
     * Creates a String from a List of Ids by concatenating their values
     * together with a comma as a
     * separator.
     *
     * @param ids The List of Ids from which the String is created.
     * @return The String corresponding to the List of Id.
     */
    private static String getStringFromIds(List<Id> ids) {
        Objects.requireNonNull(ids);

        StringBuilder idsAsString = new StringBuilder();
        for (Id id : ids) {
            idsAsString.append(id.getId()).append(",");
        }
        if (ids.size() != 0) {
            idsAsString.deleteCharAt(idsAsString.length() - 1);
        }
        return idsAsString.toString();
    }
}
