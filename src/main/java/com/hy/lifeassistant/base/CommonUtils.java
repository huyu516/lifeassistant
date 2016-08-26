package com.hy.lifeassistant.base;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

// 工具类
public abstract class CommonUtils {

    private static final SimpleDateFormat SDF_DATE_TIME = new SimpleDateFormat("MM-dd HH:mm");

    private static final SimpleDateFormat SDF_YEAR_DATE = new SimpleDateFormat("yyyy-MM-dd");

    private static final DecimalFormat FLOAT_FMT = new DecimalFormat("###,####.##");

    public static final String formatDateTime(Date date) {
        return SDF_DATE_TIME.format(date);
    }

    public static final String formatDate(Date date) {
        return SDF_YEAR_DATE.format(date);
    }

    public static String formatFloat(float f) {
        return FLOAT_FMT.format(f);
    }

    public static String getUUid() {
        return UUID.randomUUID().toString();
    }

    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }


    public static int daysBetween(Date start, Date end) {
        try {
            Calendar cal = Calendar.getInstance();

            start = SDF_YEAR_DATE.parse(formatDate(start));
            cal.setTime(start);
            long time1 = cal.getTimeInMillis();

            end = SDF_YEAR_DATE.parse(formatDate(end));
            cal.setTime(end);
            long time2 = cal.getTimeInMillis();

            long days = (time2 - time1) / (1000 * 3600 * 24); // end - start
            return (int) days;
        } catch (ParseException e) {
            throw new RuntimeException("日期转化错误");
        }
    }

}
