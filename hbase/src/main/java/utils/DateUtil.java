package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {


    /**
     * 得到当前日期格式yyyy-mm-dd
     * @return
     */
    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
        String date = sdf.format(new Date());
        return date;
    }

    /**
     * 得到当前日期格式yyyymmdd
     * @return
     */
    public static String getCurrentDateNoLine() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyymmdd");
        String date = sdf.format(new Date());
        return date;
    }

    /**
     * 得到当前日期格式yyyymmddHHmmss
     * @return
     */
    public static String getCurrentDateWithTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyymmddHHmmss");
        String date = sdf.format(new Date());
        return date;
    }

}
