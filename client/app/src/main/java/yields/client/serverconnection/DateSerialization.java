package yields.client.serverconnection;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateSerialization {

    private static SimpleDateFormat dateFormatISO6101 =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ");

    private static SimpleDateFormat dateFormatForCache =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * Gets the string representation of the Date.
     *
     * @param date The date to serialize.
     * @return the serialized version of the date.
     */
    public static String toString(Date date) {
        dateFormatISO6101.setTimeZone(TimeZone.getDefault());
        return dateFormatISO6101.format(date);
    }

    /**
     * Gets the date from the serialized form.
     *
     * @param date The serialized date.
     * @return The corresponding Date object.
     * @throws ParseException In case of parsing error.
     */
    public static Date toDate(String date) throws ParseException{
        date = date.replace("Z","+00:00");
        dateFormatISO6101.setTimeZone(TimeZone.getDefault());
        return dateFormatISO6101.parse(date);
    }


    /**
     * Gets the string representation of the Date for the Cache.
     *
     * @param date The date to serialize for the Cache.
     * @return the serialized version of the date for the Cache.
     */
    public static String toStringForCache(Date date) {
        dateFormatForCache.setTimeZone(TimeZone.getDefault());
        return dateFormatForCache.format(date);
    }

    /**
     * Gets the date from the serialized Date for the Cache.
     *
     * @param date The serialized date for the Cache.
     * @return The corresponding Date object for the Cache.
     * @throws ParseException In case of parsing error.
     */
    public static Date toDateForCache(String date) throws ParseException{
        dateFormatForCache.setTimeZone(TimeZone.getDefault());
        return dateFormatForCache.parse(date);
    }

}
