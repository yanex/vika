package org.yanex.vika.util;

import org.yanex.vika.gui.screen.VkMainScreen;
import org.yanex.vika.local.VikaResource;

import java.util.Calendar;
import java.util.Date;

public class HappyDate {

    public final long timestamp;
    public final int month;
    public final int day;
    public final int weekday;
    public final int year;
    public final int hour;
    public final int minute;
    public final int second;
    public final int ms;

    public HappyDate() {
        this(System.currentTimeMillis());
    }

    public HappyDate(long timestamp) {
        this.timestamp = timestamp;
        Date d = new Date(timestamp);

        Calendar c = Calendar.getInstance();
        c.setTime(d);

        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH) + 1;
        day = c.get(Calendar.DAY_OF_MONTH);
        weekday = c.get(Calendar.DAY_OF_WEEK);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        second = c.get(Calendar.SECOND);
        ms = c.get(Calendar.MILLISECOND);
    }

    public String day2() {
        return HappyDate.to2(day);
    }

    public String hour2() {
        return HappyDate.to2(hour);
    }

    public String minute2() {
        return HappyDate.to2(minute);
    }

    public String month2() {
        return HappyDate.to2(month);
    }

    public String second2() {
        return HappyDate.to2(second);
    }

    public String toString() {
        return new Date(timestamp).toString();
    }

    public String year2() {
        return HappyDate.to2(year);
    }

    public static String getSimpleStringDate(long timestamp) {
        HappyDate date = new HappyDate(timestamp);

        return date.hour + ":" + date.minute2();
    }

    public static String getStringDate(long timestamp) {
        long nowMillis = System.currentTimeMillis();
        if ((nowMillis / timestamp) > 100) {
            timestamp = timestamp * 1000;
        }

        HappyDate now = new HappyDate();
        HappyDate date = new HappyDate(timestamp);

        if (now.day == date.day) {
            return date.hour2() + ":" + date.minute2();
        } else if (now.day == date.day + 1) {
            return VkMainScreen.tr(VikaResource.Yesterday);
        } else if (now.year == date.year) {
            return date.day2() + "." + date.month2();
        } else {
            return date.day2() + "." + date.month2() + "." + date.year2();
        }
    }

    private static String to2(int n) {
        if (n < 10) {
            return "0" + n;
        } else if (n > 100) {
            return HappyDate.to2(n % 100);
        } else {
            return "" + n;
        }
    }

}
