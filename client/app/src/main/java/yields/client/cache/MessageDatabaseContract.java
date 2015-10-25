package yields.client.cache;

import android.provider.BaseColumns;

public final class MessageDatabaseContract {

    private  MessageDatabaseContract() {
    }

    public static abstract class MessageEntry implements BaseColumns {
        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_NAME_ENTRY_ID = "entryid";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_SUBTITLE = "subtitle";
    }
}
