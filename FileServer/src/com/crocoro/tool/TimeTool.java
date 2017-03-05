package com.crocoro.tool;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by z9195 on 2017/3/5.
 */
public class TimeTool {
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    public static String getTime(String dt) {
        Date date = new Date(dt);
        return sdf.format(date);
    }

    public static String convSqlDate(String sqlDate) {
        return sqlDate.replaceAll(" ", "-").replaceAll(":", "-");
    }
}
