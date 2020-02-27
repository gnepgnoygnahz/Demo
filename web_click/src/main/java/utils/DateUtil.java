package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
    public static String formatDate(String time_local) {

        SimpleDateFormat df1 = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss", Locale.US);
        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

        try {
            return df2.format(df1.parse(time_local));
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date toDate(String timeStr) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        return df.parse(timeStr);
    }

    public static String toStr(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        return df.format(date);
    }

    public static long timeDiff(String time1, String time2) throws ParseException {
        Date d1 = toDate(time1);
        Date d2 = toDate(time2);
        return d1.getTime() - d2.getTime();
    }

    public static long timeDiff(Date time1, Date time2) throws ParseException {
        return time1.getTime() - time2.getTime();
    }
}
