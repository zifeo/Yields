package yields.client.serverconnection;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateSerialization {

    public static final DateSerialization dateSerializer = new DateSerialization();

    private SimpleDateFormat mDateFormatISO6101;

    private SimpleDateFormat mDateFormatForCache;

    public DateSerialization() {
        mDateFormatISO6101 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ");
        mDateFormatISO6101.setTimeZone(TimeZone.getDefault());
        mDateFormatForCache = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        mDateFormatForCache.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    /**
     * Gets the string representation of the Date.
     *
     * @param date The date to serialize.
     * @return the serialized version of the date.
     */
    synchronized public String toString(Date date) {
        return mDateFormatISO6101.format(date);
    }

    /**
     * Gets the date from the serialized form.
     *
     * @param date The serialized date.
     * @return The corresponding Date object.
     * @throws ParseException In case of parsing error.
     */
    public Date toDate(String date) throws ParseException{
        date = date.replace("Z","+00:00");
        if (date.length() < 28) {
            date = date.substring(0, 19) + ".000" + date.substring(19);
        }

        return mDateFormatISO6101.parse(date);
    }

    /**
     * Gets the string representation of the Date for the Cache.
     *
     * @param date The date to serialize for the Cache.
     * @return the serialized version of the date for the Cache.
     */
    public String toStringForCache(Date date) {
        return mDateFormatForCache.format(date);
    }

    /**
     * Gets the date from the serialized Date for the Cache.
     *
     * @param date The serialized date for the Cache.
     * @return The corresponding Date object for the Cache.
     * @throws ParseException In case of parsing error.
     */
    public Date toDateForCache(String date) throws ParseException {
        return mDateFormatForCache.parse(date);
    }
}
