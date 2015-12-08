package yields.client.cache;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import yields.client.exceptions.CacheDatabaseException;
import yields.client.exceptions.ContentException;
import yields.client.id.Id;
import yields.client.messages.Content;
import yields.client.messages.ImageContent;
import yields.client.messages.Message;
import yields.client.messages.TextContent;
import yields.client.messages.UrlContent;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.serverconnection.DateSerialization;
import yields.client.serverconnection.ImageSerialization;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * SQLite Database Helper.
 */
public class CacheDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "CacheDatabase";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "cache.db";

    private static final String TABLE_USERS = "users";
    private static final String TABLE_GROUPS = "groups";
    private static final String TABLE_MESSAGES = "messages";
    private static final String TABLE_INTERNAL = "internal";

    private static final String KEY_USER_NODE_ID = "nodeID";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_IMAGE = "userImage";
    private static final String KEY_USER_ENTOURAGE = "userEntourage";
    private static final String KEY_USER_LAST_REFRESH = "userRefreshDate";

    private static final String KEY_GROUP_NODE_ID = "nodeID";
    private static final String KEY_GROUP_NAME = "groupName";
    private static final String KEY_GROUP_USERS = "groupUsers";
    private static final String KEY_GROUP_IMAGE = "groupImage";
    private static final String KEY_GROUP_VISIBILITY = "groupVisibility";
    private static final String KEY_GROUP_VALIDATED = "groupValidated";

    private static final String KEY_MESSAGE_NODE_ID = "nodeID";
    private static final String KEY_MESSAGE_GROUP_ID = "messageGroup";
    private static final String KEY_MESSAGE_SENDER_ID = "messageSender";
    private static final String KEY_MESSAGE_TEXT = "messageText";
    private static final String KEY_MESSAGE_CONTENT = "messageContent";
    private static final String KEY_MESSAGE_CONTENT_TYPE = "messageContentType";
    private static final String KEY_MESSAGE_STATUS = "messageStatus";
    private static final String KEY_MESSAGE_DATE = "messageDate";

    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS
            + "(" + KEY_USER_NODE_ID + " INTEGER PRIMARY KEY,"
            + KEY_USER_NAME + " TEXT," + KEY_USER_EMAIL + " TEXT," + KEY_USER_IMAGE
            + " TEXT, " + KEY_USER_ENTOURAGE + " BOOLEAN," + KEY_USER_LAST_REFRESH + " TEXT" + ")";

    private static final String CREATE_TABLE_GROUPS = "CREATE TABLE " + TABLE_GROUPS
            + "(" + KEY_GROUP_NODE_ID + " INTEGER PRIMARY KEY,"
            + KEY_GROUP_NAME + " TEXT," + KEY_GROUP_USERS + " TEXT," + KEY_GROUP_IMAGE
            + " TEXT," + KEY_GROUP_VISIBILITY + " TEXT," + KEY_GROUP_VALIDATED + " BOOLEAN" + ")";

    private static final String CREATE_TABLE_MESSAGES = "CREATE TABLE " + TABLE_MESSAGES
            + "(" + KEY_MESSAGE_DATE + " TEXT PRIMARY KEY," + KEY_MESSAGE_NODE_ID + " INTEGER,"
            + KEY_MESSAGE_GROUP_ID + " TEXT," + KEY_MESSAGE_SENDER_ID + " TEXT,"
            + KEY_MESSAGE_TEXT + " TEXT," + KEY_MESSAGE_CONTENT_TYPE + " TEXT,"
            + KEY_MESSAGE_CONTENT + " TEXT," + KEY_MESSAGE_STATUS + " TEXT" + ")";

    private final SQLiteDatabase mDatabase;

    /**
     * Main constructor, creates the database.
     *
     * @param context The context for the database.
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
     * Description copied from class: SQLiteOpenHelper. Called when the database
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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INTERNAL);
        onCreate(db);
    }

    /**
     * Deletes the given Message from the database.
     *
     * @param message The Message to be deleted.
     * @param groupId The Id of the Group to which the message was sent.
     */
    public void deleteMessage(Message message, Id groupId) {
        Objects.requireNonNull(message);
        Objects.requireNonNull(groupId);

        mDatabase.execSQL("DELETE FROM " + TABLE_MESSAGES + " WHERE " + KEY_MESSAGE_GROUP_ID + " = ? " + "AND "
                        + KEY_MESSAGE_DATE + " = ? AND " + KEY_MESSAGE_TEXT + " = ? AND " + KEY_MESSAGE_SENDER_ID + " = ?",
                new Object[]{
                        groupId.getId().toString(),
                        DateSerialization.dateSerializer.toStringForCache(message.getDate()),
                        message.getContent().getTextForRequest(),
                        message.getSender().getId().toString()});
    }

    /**
     * Adds the given Message to the database.
     *
     * @param message The Message to be added to the database.
     * @param groupId The Id of the Group to which this Message was sent.
     */
    public void addMessage(Message message, Id groupId) {
        Objects.requireNonNull(message);
        Objects.requireNonNull(groupId);

        mDatabase.insertWithOnConflict(TABLE_MESSAGES, null, createContentValuesForMessage(message, groupId), SQLiteDatabase.CONFLICT_REPLACE);
    }

    /**
     * Deletes the User from the database.
     *
     * @param userId The Id of the User to be deleted.
     */
    public void deleteUser(Id userId) {
        Objects.requireNonNull(userId);

        mDatabase.delete(TABLE_USERS, KEY_USER_NODE_ID + " = ?", new String[]{String.valueOf(userId.getId())});
    }

    /**
     * Adds the User to the database or updates it if it's already in it.
     *
     * @param user The User to be added.
     */
    public void addUser(User user) {
        Objects.requireNonNull(user);

        ContentValues values = createContentValuesForUser(user);
        mDatabase.insertWithOnConflict(TABLE_USERS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    /**
     * Updates a User in the database.
     *
     * @param user The new values of the User.
     */
    public void updateUser(User user) {
        Objects.requireNonNull(user);

        ContentValues values = createContentValuesForUser(user);
        mDatabase.update(TABLE_USERS, values, KEY_USER_NODE_ID + " = ?",
                new String[]{user.getId().getId().toString()});
    }


    /**
     * Updates the name of a User in the database.
     *
     * @param userId      The Id field of the User that will have it's name changed.
     * @param newUserName The new name of the User.
     */
    public void updateUserName(Id userId, String newUserName) {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(newUserName);

        ContentValues values = new ContentValues();
        values.put(KEY_USER_NAME, newUserName);
        mDatabase.update(TABLE_USERS, values, KEY_USER_NODE_ID + " = ?",
                new String[]{userId.getId().toString()});
    }

    /**
     * Updates the image of a User in the database.
     *
     * @param userId       The Id field of the User that will have it's image changed.
     * @param newUserImage The new image of the User.
     */
    public void updateUserImage(Id userId, Bitmap newUserImage) {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(newUserImage);

        ContentValues values = new ContentValues();
        values.put(KEY_USER_IMAGE, ImageSerialization.serializeImage(newUserImage, ImageSerialization.SIZE_IMAGE));
        mDatabase.update(TABLE_USERS, values, KEY_USER_NODE_ID + " = ?",
                new String[]{userId.getId().toString()});
    }

    /**
     * Updates the email of a User in the database.
     *
     * @param userId   The Id field of the User that will have it's email changed.
     * @param newEmail The new email of the User.
     */
    public void updateUserEmail(Id userId, String newEmail) {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(newEmail);

        ContentValues values = new ContentValues();
        values.put(KEY_USER_EMAIL, newEmail);
        mDatabase.update(TABLE_USERS, values, KEY_USER_NODE_ID + " = ?",
                new String[]{userId.getId().toString()});
    }

    /**
     * Updates a Users entourage field in the cache.
     *
     * @param userId      The Id field of the User that will have it's entourage membership changed.
     * @param inEntourage True if the User should be in it's entourage and false if shouldn't.
     */
    public void updateEntourage(Id userId, boolean inEntourage) {
        Objects.requireNonNull(userId);

        ContentValues values = new ContentValues();
        int entourageMembership = inEntourage ? 1 : 0;
        values.put(KEY_USER_ENTOURAGE, entourageMembership);
        mDatabase.update(TABLE_USERS, values, KEY_USER_NODE_ID + " = ?",
                new String[]{userId.getId().toString()});
    }

    /**
     * Retrieves a User according to its Id, returns null if such a User is not
     * in the database.
     *
     * @param userID The Id of the wanted User.
     * @return The User which has userID as its Id or null if there is no such
     * User in the database.
     */
    public User getUser(Id userID) {
        Objects.requireNonNull(userID);

        String selectUserQuery = "SELECT * FROM " + TABLE_USERS + " WHERE " + KEY_USER_NODE_ID + " = ?";
        Cursor userCursor = mDatabase.rawQuery(selectUserQuery, new String[]{userID.getId().toString()});

        if (!userCursor.moveToFirst()) {
            userCursor.close();
            return null;
        } else {
            String userName = userCursor.getString(userCursor.getColumnIndex(KEY_USER_NAME));
            String userEmail = userCursor.getString(userCursor.getColumnIndex(KEY_USER_EMAIL));
            Bitmap userImage = ImageSerialization.unSerializeImage(userCursor.getString(
                    userCursor.getColumnIndex(KEY_USER_IMAGE)));

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
                String userName = cursor.getString(
                        cursor.getColumnIndex(KEY_USER_NAME));
                String userEmail = cursor.getString(
                        cursor.getColumnIndex(KEY_USER_EMAIL));
                Bitmap userImage = ImageSerialization.unSerializeImage(cursor.getString
                        (cursor.getColumnIndex(KEY_USER_IMAGE)));
                Id userId = new Id(cursor.getLong(cursor.getColumnIndex(KEY_USER_NODE_ID)));
                users.add(new User(userName, userId, userEmail, userImage));
            } while (cursor.moveToNext());

            cursor.close();
            return users;
        }
    }

    /**
     * Returns all Users that are in the ClientUser's entourage in the database.
     *
     * @return An exhaustive List of all Users in the database that are in the ClientUser's entourage.
     */
    public List<User> getClientUserEntourage() {
        String selectAllUsersQuery = "SELECT * FROM " + TABLE_USERS + " WHERE " + KEY_USER_ENTOURAGE + " = ?";
        Cursor cursor = mDatabase.rawQuery(selectAllUsersQuery, new String[]{"1"});
        List<User> users = new ArrayList<>();
        if (!cursor.moveToFirst()) {
            cursor.close();
            return users;
        } else {
            do {
                String userName = cursor.getString(
                        cursor.getColumnIndex(KEY_USER_NAME));
                String userEmail = cursor.getString(
                        cursor.getColumnIndex(KEY_USER_EMAIL));
                Bitmap userImage = ImageSerialization.unSerializeImage(cursor.getString
                        (cursor.getColumnIndex(KEY_USER_IMAGE)));
                Id userId = new Id(cursor.getLong(cursor.getColumnIndex(KEY_USER_NODE_ID)));
                users.add(new User(userName, userId, userEmail, userImage));
            } while (cursor.moveToNext());

            cursor.close();
            return users;
        }
    }

    /**
     * Deletes the Group and all it's Messages from the database.
     *
     * @param groupId The Id of the Group to be deleted.
     */
    public void deleteGroup(Id groupId) {
        Objects.requireNonNull(groupId);

        String idOfDeletedGroup = groupId.getId().toString();
        mDatabase.delete(TABLE_MESSAGES, KEY_MESSAGE_GROUP_ID + " = ?",
                new String[]{idOfDeletedGroup});
        mDatabase.delete(TABLE_GROUPS, KEY_GROUP_NODE_ID + " = ?",
                new String[]{idOfDeletedGroup});
    }

    /**
     * Adds the Group to the database and all the Users it contains if they
     * are not in the database already. If the Group is already in the database
     * it's updated.
     *
     * @param group The Group to be added.
     */
    public void addGroup(Group group) {
        Objects.requireNonNull(group);

        //TODO: Check if adding Users is necessary !
        List<User> usersInGroup = group.getUsers();
        for (User user : usersInGroup) {
            ContentValues userValues = createContentValuesForUser(user);
            mDatabase.insertWithOnConflict(TABLE_USERS, null, userValues, SQLiteDatabase.CONFLICT_REPLACE);
        }

        ContentValues groupValues = createContentValuesForGroup(group);
        mDatabase.insertWithOnConflict(TABLE_GROUPS, null, groupValues, SQLiteDatabase.CONFLICT_REPLACE);
    }

    /**
     * Updates a Group in the database.
     *
     * @param group The new values of the Group.
     */
    public void updateGroup(Group group) {
        Objects.requireNonNull(group);

        ContentValues values = createContentValuesForGroup(group);
        mDatabase.update(TABLE_GROUPS, values, KEY_GROUP_NODE_ID + " = ?",
                new String[]{group.getId().getId().toString()});
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
        mDatabase.update(TABLE_GROUPS, values, KEY_GROUP_NODE_ID + " = ?",
                new String[]{groupId.getId().toString()});
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
        values.put(KEY_GROUP_IMAGE, ImageSerialization.serializeImage(newGroupImage, ImageSerialization.SIZE_IMAGE));
        mDatabase.update(TABLE_GROUPS, values, KEY_GROUP_NODE_ID + " = ?",
                new String[]{groupId.getId().toString()});
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
        mDatabase.update(TABLE_GROUPS, values, KEY_GROUP_NODE_ID + " = ?",
                new String[]{groupId.getId().toString()});
    }

    /**
     * Updates the validity of a Group in the database.
     *
     * @param groupId  The Id field of the Group that will have it's validity changed.
     * @param validity The new validity of the Group.
     */
    public void updateGroupValidity(Id groupId, boolean validity) {
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(validity);

        ContentValues values = new ContentValues();
        int validated = validity ? 1 : 0;
        values.put(KEY_GROUP_VALIDATED, validated);
        mDatabase.update(TABLE_GROUPS, values, KEY_GROUP_NODE_ID + " = ?",
                new String[]{groupId.getId().toString()});
    }

    /**
     * Removes Users from a Group in the database.
     * If the ClientUser of the app is in the list of Users to be removed
     * the group is deleted in the database.
     *
     * @param groupId The Id of the Group form which the User shall be removed.
     * @param users   The Users that will be removed from the Group.
     */
    public void removeUsersFromGroup(Id groupId, List<User> users) {
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(users);

        for (User user : users) {
            if (user.getId().equals(YieldsApplication.getUser().getId())) {
                deleteGroup(groupId);
                return;
            }
        }

        //TODO: CHECK CONCURRENCY ISSUES
        List<Id> ids = getUserIdsFromGroup(groupId);
        Iterator<Id> idIterator = ids.iterator();
        while (idIterator.hasNext()) {
            Id nextId = idIterator.next();
            for (User user : users) {
                if (user.getId().equals(nextId)) {
                    idIterator.remove();
                }
            }
        }

        ContentValues values = new ContentValues();
        values.put(KEY_GROUP_USERS, getStringFromIds(ids));
        mDatabase.update(TABLE_GROUPS, values, KEY_GROUP_NODE_ID + " = ?",
                new String[]{groupId.getId().toString()});
    }

    /**
     * Adds Users to a Group in the database, and also adds the user to the database if it isn't
     * in it already.
     *
     * @param groupId The Id of the Group to which the User shall be added.
     * @param users   Users that will be added to the Group.
     */
    public void addUsersToGroup(Id groupId, List<User> users) {
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(users);

        for (User user : users) {
            Objects.requireNonNull(user);

            //TODO : Check if adding the user is necessary
            addUser(user);
            //TODO: CHECK CONCURRENCY ISSUES
            List<Id> ids = getUserIdsFromGroup(groupId);
            ids.add(user.getId());
            ContentValues values = new ContentValues();
            values.put(KEY_GROUP_USERS, getStringFromIds(ids));
            mDatabase.update(TABLE_GROUPS, values, KEY_GROUP_NODE_ID + " = ?",
                    new String[]{groupId.getId().toString()});
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
                + KEY_GROUP_NODE_ID + " = ?";
        Cursor cursor = mDatabase.rawQuery(selectQuery,
                new String[]{groupId.getId().toString()});
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
     * not in the database.
     *
     * @param groupId The Id of the wanted Group.
     * @return The Group which has groupId as its Id or null if there is no
     * such Group in the database.
     */
    public Group getGroup(Id groupId) {
        Objects.requireNonNull(groupId);

        String selectQuery = "SELECT * FROM " + TABLE_GROUPS + " WHERE "
                + KEY_GROUP_NODE_ID + " = ?";
        Cursor cursor = mDatabase.rawQuery(selectQuery,
                new String[]{groupId.getId().toString()});

        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        } else {
            String groupName = cursor.getString(cursor.getColumnIndex(KEY_GROUP_NAME));

            Bitmap groupImage =
                    ImageSerialization.unSerializeImage(cursor.getString(cursor.getColumnIndex(KEY_GROUP_IMAGE)));

            Group.GroupVisibility groupVisibility =
                    Group.GroupVisibility.valueOf(cursor.getString(cursor.getColumnIndex(KEY_GROUP_VISIBILITY)));

            int validated = cursor.getInt(cursor.getColumnIndex(KEY_GROUP_VALIDATED));
            boolean groupValidated = (1 == validated);

            String allUsers =
                    cursor.getString(cursor.getColumnIndex(KEY_GROUP_USERS));
            List<Id> groupUsers = getIdListFromString(allUsers);
            cursor.close();

            return new Group(groupName, groupId, groupUsers, groupImage, groupVisibility,
                    groupValidated, new Date());
        }
    }

    /**
     * Returns all Groups currently in the database.
     *
     * @return An exhaustive List of all Groups in the database.
     */
    public List<Group> getAllGroups() {
        String selectQuery = "SELECT * FROM " + TABLE_GROUPS;
        Cursor cursor = mDatabase.rawQuery(selectQuery, null);

        List<Group> groups = new ArrayList<>();
        if (!cursor.moveToFirst()) {
            cursor.close();
            return groups;
        } else {
            do {
                Id groupId = new Id(cursor.getLong(cursor.getColumnIndex(KEY_GROUP_NODE_ID)));
                String groupName = cursor.getString(cursor.getColumnIndex(KEY_GROUP_NAME));

                Bitmap groupImage =
                        ImageSerialization.unSerializeImage(cursor.getString(cursor.getColumnIndex(KEY_GROUP_IMAGE)));

                Group.GroupVisibility groupVisibility =
                        Group.GroupVisibility.valueOf(cursor.getString(cursor.getColumnIndex(KEY_GROUP_VISIBILITY)));

                int validated = cursor.getInt(cursor.getColumnIndex(KEY_GROUP_VALIDATED));
                boolean groupValidated = (1 == validated);

                String allUsers = cursor.getString(
                        cursor.getColumnIndex(KEY_GROUP_USERS));
                List<Id> groupUsers = getIdListFromString(allUsers);

                groups.add(new Group(groupName, groupId, groupUsers, groupImage, groupVisibility, groupValidated,
                        new Date()));
            } while (cursor.moveToNext());

            cursor.close();
            return groups;
        }
    }

    /**
     * Retrieves all Messages from a specified Node which are older than the Date
     * passed as an argument. Only messageCount Messages are retrieves, or less if
     * there aren't as many.
     *
     * @param nodeId       The Id of the Node from which we want to retrieve the Messages.
     * @param furthestDate The Date boundary.
     * @param messageCount The number of Messages that should be retrieved..
     * @return The list of Messages from the Group.
     * @throws CacheDatabaseException If the database was unable to fetch some
     *                                information.
     */
    public List<Message> getMessagesForGroup(Id nodeId, Date furthestDate, int messageCount)
            throws CacheDatabaseException {
        Objects.requireNonNull(nodeId);
        Objects.requireNonNull(furthestDate);

        if (messageCount <= 0) {
            throw new IllegalArgumentException("Illegal messageCount ! The number of messages must" +
                    "be greater than 0 !");
        }

        String selectQuery = "SELECT * FROM " + TABLE_MESSAGES + " WHERE "
                + KEY_MESSAGE_GROUP_ID + " = ? " + " ORDER BY "
                + KEY_MESSAGE_DATE + " DESC" + " LIMIT " + messageCount;

        Cursor cursor = mDatabase.rawQuery(selectQuery,
                new String[]{nodeId.getId().toString()});

        List<Message> messages = new ArrayList<>();
        if (!cursor.moveToFirst()) {
            cursor.close();
            return new ArrayList<>();
        } else {
            do {
                Id messageId = new Id(cursor.getLong(cursor.getColumnIndex(KEY_MESSAGE_NODE_ID)));
                String nodeName = ""; //TODO: Define message's Node name attribute
                Id senderId = new Id(cursor.getLong(cursor.getColumnIndex(KEY_MESSAGE_SENDER_ID)));
                String contentType = cursor.getString(cursor.getColumnIndex(KEY_MESSAGE_CONTENT_TYPE));
                Content content = deserializeContent(cursor, Content.ContentType.valueOf(contentType));
                String dateAsString = cursor.getString(cursor.getColumnIndex(KEY_MESSAGE_DATE));
                String messageStatusAsString = cursor.getString(cursor.getColumnIndex(KEY_MESSAGE_STATUS));
                Message.MessageStatus messageStatus = Message.MessageStatus.valueOf(messageStatusAsString);
                try {
                    Date date = DateSerialization.dateSerializer.toDateForCache(dateAsString);
                    messages.add(new Message(nodeName, messageId, senderId, content, date, messageStatus));
                } catch (ParseException exception) {
                    exception.printStackTrace();
                    Log.d(TAG, "Unable to retrieve Messages from Group with id: "
                            + nodeId.getId(), exception);
                    throw new CacheDatabaseException("Unable to retrieve Messages from Group "
                            + nodeId.getId());
                }
            } while (cursor.moveToNext());

            cursor.close();
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
     * Deletes the Database file in the file system.
     */
    public static void deleteDatabase() {
        YieldsApplication.getApplicationContext().deleteDatabase(DATABASE_NAME);
    }

    /**
     * Serializes a Content into a String in base 64.
     *
     * @param content The Content to be serialized.
     * @return The bytes corresponding to the serialization of the Content.
     */
    private static String serializeContent(Content content) {
        Objects.requireNonNull(content);

        switch (content.getType()) {
            case TEXT:
                return serializeTextContent((TextContent) content);
            case IMAGE:
                return serializeImageContent((ImageContent) content);
            case URL:
                return serializeURLContent((UrlContent) content);
            default:
                throw new ContentException("No such content exists !");
        }
    }

    /**
     * Deserializes a a String in base 64 into a Content.
     *
     * @param cursor      The cursor pointing to the Message which has the desired Content.
     * @param contentType The type of the Content.
     * @return The Content corresponding to the byte array.
     */
    private static Content deserializeContent(Cursor cursor, Content.ContentType contentType) {
        Objects.requireNonNull(contentType);
        Objects.requireNonNull(cursor);

        switch (contentType) {
            case TEXT:
                return deserializeTextContent(cursor);
            case IMAGE:
                return deserializeImageContent(cursor);
            case URL:
                return deserializeURLContent(cursor);
            default:
                throw new ContentException("No such content exists !");
        }
    }

    /**
     * Serializes the ImageContent into a String in base 64.
     *
     * @param content The ImageContent to be serialized.
     * @return The serialized version of the ImageContent.
     */
    private static String serializeImageContent(ImageContent content) {
        Objects.requireNonNull(content);

        return ImageSerialization.serializeImage(content.getImage(), ImageSerialization.SIZE_IMAGE);
    }

    /**
     * Deserializes an ImageContent.
     *
     * @param cursor A cursor pointing to the current Message to be deserialized.
     * @return The deserialized version of the ImageContent.
     */
    private static Content deserializeImageContent(Cursor cursor) {
        Objects.requireNonNull(cursor);

        Bitmap image = ImageSerialization.unSerializeImage(cursor.getString(cursor.getColumnIndex(KEY_MESSAGE_CONTENT)));
        String caption = cursor.getString(cursor.getColumnIndex(KEY_MESSAGE_TEXT));
        return new ImageContent(image, caption);
    }

    /**
     * Serializes the TextContent into a base 64 String.
     *
     * @param content The TextContent to be serialized.
     * @return The serialized version of the TextContent.
     */
    private static String serializeTextContent(TextContent content) {
        Objects.requireNonNull(content);

        return null;
    }

    /**
     * Deserializes a String in base 64 into a TextContent.
     *
     * @param cursor The cursor pointing to the Message which has the desired Content.
     * @return The deserialized version of the String.
     */
    private static Content deserializeTextContent(Cursor cursor) {
        Objects.requireNonNull(cursor);

        String contentText = cursor.getString(cursor.getColumnIndex(KEY_MESSAGE_TEXT));
        return new TextContent(contentText);
    }

    /**
     * Serializes the URLContent into a base 64 String.
     *
     * @param content The URLContent to be serialized.
     * @return The serialized version of the TextContent.
     */
    private static String serializeURLContent(UrlContent content) {
        Objects.requireNonNull(content);

        return content.getUrl();
    }

    /**
     * Deserializes a String in base 64 into a URLContent.
     *
     * @param cursor The cursor pointing to the Message which has the desired Content.
     * @return The deserialized version of the String.
     */
    private static Content deserializeURLContent(Cursor cursor) {
        Objects.requireNonNull(cursor);

        String urlText = cursor.getString(cursor.getColumnIndex(KEY_MESSAGE_TEXT));
        return new UrlContent(urlText);
    }

    /**
     * Creates the appropriate row entry (ContentValues) for a Message.
     *
     * @param message The Message for which a ContentValues is built.
     * @param groupId The Id of the Group to which the Message is added.
     * @return A ContentValues object which corresponds to the Message.
     */
    private static ContentValues createContentValuesForMessage(Message message, Id groupId) {
        Objects.requireNonNull(message);
        Objects.requireNonNull(groupId);

        ContentValues values = new ContentValues();
        values.put(KEY_MESSAGE_NODE_ID, message.getId().getId());
        values.put(KEY_MESSAGE_SENDER_ID, message.getSender().getId());
        values.put(KEY_MESSAGE_GROUP_ID, groupId.getId());
        values.put(KEY_MESSAGE_TEXT, message.getContent().getTextForRequest());
        values.put(KEY_MESSAGE_CONTENT_TYPE, message.getContent().getType().toString());
        values.put(KEY_MESSAGE_CONTENT, serializeContent(message.getContent()));
        values.put(KEY_MESSAGE_STATUS, message.getStatus().getValue());
        values.put(KEY_MESSAGE_DATE, DateSerialization.dateSerializer.toStringForCache(message.getDate()));

        return values;
    }

    /**
     * Creates the appropriate row entry (ContentValues) for a User.
     *
     * @param user The User for which a ContentValues is built.
     * @return A ContentValues object which corresponds to the User.
     */
    private static ContentValues createContentValuesForUser(User user) {
        Objects.requireNonNull(user);

        ContentValues values = new ContentValues();
        values.put(KEY_USER_NODE_ID, user.getId().getId());
        values.put(KEY_USER_NAME, user.getName());
        values.put(KEY_USER_EMAIL, user.getEmail());
        values.put(KEY_USER_IMAGE, ImageSerialization.serializeImage(user.getImg(), ImageSerialization.SIZE_IMAGE));

        List<User> entourage = YieldsApplication.getUser().getEntourage();
        List<Id> entourageIds = new ArrayList<>();
        for (User entourageUser : entourage) {
            entourageIds.add(entourageUser.getId());
        }
        int inEntourage = entourageIds.contains(user.getId()) ? 1 : 0;
        values.put(KEY_USER_ENTOURAGE, inEntourage);
        //values.put(KEY_USER_LAST_REFRESH, DateSerialization.dateSerializer.toStringForCache(new Date()));

        return values;
    }

    /**
     * Creates the appropriate row entry (ContentValues) for a Group.
     *
     * @param group The Group for which a ContentValues is built.
     * @return A ContentValues object which corresponds to the Group.
     */
    private static ContentValues createContentValuesForGroup(Group group) {
        Objects.requireNonNull(group);

        ContentValues values = new ContentValues();
        values.put(KEY_GROUP_NODE_ID, group.getId().getId());
        values.put(KEY_GROUP_IMAGE, ImageSerialization.serializeImage(group.getImage(), ImageSerialization.SIZE_IMAGE));
        values.put(KEY_GROUP_VISIBILITY, group.getVisibility().getValue());
        values.put(KEY_GROUP_NAME, group.getName());

        int validated = group.isValidated() ? 1 : 0;
        values.put(KEY_GROUP_VALIDATED, validated);

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

        List<Id> ids = new ArrayList<>();
        if (idsAsString.isEmpty()) {
            return ids;
        }

        String[] idsAsArray = idsAsString.split(",");
        for (String string : idsAsArray) {
            ids.add(new Id(Long.parseLong(string)));
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