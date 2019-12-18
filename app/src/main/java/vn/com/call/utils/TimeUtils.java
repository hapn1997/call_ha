package vn.com.call.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ngson on 03/07/2017.
 */

public class TimeUtils {
    private int[] dayOfMonth = new int[] {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    public static String getTimeFormatConversation(long time) {
        String format;

        if (isToday(time)) format = "HH:mm";
        else if (isCurrentWeek(time)) format = "EEEE";
        else if (isCurrentYear(time)) format = "MMM dd";
        else format = "MMMM dd, yyyy";

        return getTimeWithFormat(format, time);
    }

    public static String getTimeFormatMessage(long time) {
        return getTimeWithFormat("MMM dd, HH:mm", time);
    }

    public static String getTimeFormatCallLog(long time) {
        return getTimeWithFormat("MMMM dd", time);
    }

    public static String getTimeFormatCallLogDetail(long time) {
        return getTimeWithFormat("EEEE, MMMM dd, yyyy, HH:mm", time);
    }

    public static String getTimeWithFormat(String format, long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date(time);

        String dateStr = dateFormat.format(date);

        return dateStr.substring(0, 1).toUpperCase() + dateStr.substring(1, dateStr.length());
    }

    public static boolean isCurrentWeek(long time) {
        return getWeekOfYear(time) == getWeekOfYear(System.currentTimeMillis());
    }

    public static boolean isCurrentYear(long time) {
        return getYear(time) == getYear(System.currentTimeMillis());
    }

    public static int getYear(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);

        return calendar.get(Calendar.YEAR);
    }

    public static int getWeekOfYear(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);

        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    public static boolean isToday(long time) {
        int[] today = getDayMonthYear(System.currentTimeMillis());
        int[] date = getDayMonthYear(time);

        return today[0] == date[0]
                && today[1] == date[1]
                && today[2] == date[2];
    }

    public static boolean isYesterday(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);

        int[] yesterday = getDayMonthYear(calendar.getTimeInMillis());
        int[] date = getDayMonthYear(time);

        return yesterday[0] == date[0]
                && yesterday[1] == date[1]
                && yesterday[2] == date[2];
    }

    public static boolean isSameDay(long time1, long time2) {
        int[] t1 = getDayMonthYear(time1);
        int[] t2 = getDayMonthYear(time2);

        return t1[0] == t2[0]
                && t1[1] == t2[1]
                && t1[2] == t2[2];
    }

    public static boolean isLeapYear(int year) {
        return year % 4 == 0
                && year % 100 != 0;
    }

    public static int[] getDayMonthYear(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);

        int[] dayMonthYear = new int[3];
        dayMonthYear[0] = calendar.get(Calendar.DAY_OF_MONTH);
        dayMonthYear[1] = calendar.get(Calendar.MONTH);
        dayMonthYear[2] = calendar.get(Calendar.YEAR);

        return dayMonthYear;
    }
}
