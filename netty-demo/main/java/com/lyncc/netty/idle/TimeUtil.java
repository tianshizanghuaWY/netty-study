package com.lyncc.netty.idle;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <br>
 *
 * @author sunzhongshuai
 */
public class TimeUtil {
    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public static String getCurrentFormatDateStr(){
        return formatter.format(new Date());
    }
}
