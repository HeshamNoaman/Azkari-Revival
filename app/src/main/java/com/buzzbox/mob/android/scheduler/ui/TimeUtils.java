package com.buzzbox.mob.android.scheduler.ui;

import android.content.Context;
import android.text.format.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/* JADX INFO: loaded from: classes.dex */
public class TimeUtils {
    private static final String M12 = "h:mm aa";
    static final String M24 = "kk:mm";

    static Calendar getCalendar(int hour, int minute) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        int nowHour = c.get(11);
        int nowMinute = c.get(12);
        if (hour < nowHour || (hour == nowHour && minute <= nowMinute)) {
            c.add(6, 1);
        }
        c.set(11, hour);
        c.set(12, minute);
        c.set(13, 0);
        c.set(14, 0);
        return c;
    }

    public static String formatTime(int hour, int minute) {
        Calendar c = getCalendar(hour, minute);
        return (String) DateFormat.format(M12, c);
    }

    public static String formatTime(long time, Context ctx) {
        String format = M12;
        if (DateFormat.is24HourFormat(ctx)) {
            format = M24;
        }
        return new SimpleDateFormat(format).format(Long.valueOf(time));
    }
}
