package yields.client.serverconnection;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateSerialization {
    private static SimpleDateFormat dateFormatISO6101 =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    public static String toString(Date date) {
        dateFormatISO6101.setTimeZone(TimeZone.getDefault());
        return dateFormatISO6101.format(date);
    }

    public static Date toDate(String date) throws ParseException{
        dateFormatISO6101.setTimeZone(TimeZone.getDefault());
        return dateFormatISO6101.parse(date);
    }
}
