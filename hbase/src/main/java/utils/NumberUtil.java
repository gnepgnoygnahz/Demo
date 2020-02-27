package utils;

import java.text.DecimalFormat;

public class NumberUtil {

    /**
     * 格式化数字
     * @param number
     * @param pattern
     * @return
     */
    public static String formatNumber(int number,String pattern){
        DecimalFormat df = new DecimalFormat(pattern);
        return df.format(number);
    }
}
