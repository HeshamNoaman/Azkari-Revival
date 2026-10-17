package com.buzzbox.mob.android.scheduler;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class Logger {
    static final long AUTO_DELETE_TIME = 1209600000;
    private static final String BUZZBOX_SCHEDULER_CREATED_KEY = "buzzbox-scheduler.created";
    static final String LOG_FILENAME = "buzzbox-scheduler.log";
    static DateFormat df = new SimpleDateFormat();

    public static void log(Context context, String eventName, long start, long end, int status, int totals) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        long created = prefs.getLong(BUZZBOX_SCHEDULER_CREATED_KEY, 0L);
        if (System.currentTimeMillis() - created > AUTO_DELETE_TIME) {
            deleteLog(context);
        }
        Log.d("scheduler-log", String.valueOf(eventName) + " took [" + (end - start) + "] notif=" + totals + " status=" + status);
        StringBuilder sb = new StringBuilder(eventName);
        sb.append("\t").append(start).append("\t").append(end).append("\t").append(end - start).append("\t").append(status).append("\t").append(totals).append("\n");
        try {
            FileOutputStream fos = context.openFileOutput(LOG_FILENAME, Context.MODE_PRIVATE | Context.MODE_APPEND);
            fos.write(sb.toString().getBytes());
            fos.close();
        } catch (Exception e) {
            Log.e("buzzbox-scheduler", "Error writing to log file [buzzbox-scheduler.log]", e);
        }
    }

    public static void log(Context context, Task task, long start, long end, TaskResult result) {
        log(context, task.getId(), start, end, result.status, result.messages.size());
    }

    public static void deleteLog(Context context) {
        long now = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder("Log Reset");
        sb.append("\t").append(now).append("\t").append(now).append("\t").append(0).append("\t").append(0).append("\t").append(0).append("\n");
        try {
            FileOutputStream fos = context.openFileOutput(LOG_FILENAME, Context.MODE_PRIVATE);
            fos.write(sb.toString().getBytes());
            fos.close();
        } catch (Exception e) {
            Log.e("buzzbox-scheduler", "Error writing to log file [buzzbox-scheduler.log]", e);
        }
        SharedPreferences.Editor e2 = PreferenceManager.getDefaultSharedPreferences(context).edit();
        e2.putLong(BUZZBOX_SCHEDULER_CREATED_KEY, System.currentTimeMillis());
        e2.commit();
    }

    public static List<Entry> loadLog(Context context) {
        List<Entry> res = new LinkedList<>();
        try {
            FileInputStream fis = context.openFileInput(LOG_FILENAME);
            BufferedReader buf = new BufferedReader(new InputStreamReader(fis));
            while (true) {
                String line = buf.readLine();
                if (line == null) {
                    break;
                }
                String[] parts = line.split("\t");
                Entry e = new Entry();
                e.taskName = parts[0];
                e.start = Long.parseLong(parts[1]);
                e.end = Long.parseLong(parts[2]);
                e.duration = Long.parseLong(parts[3]);
                e.status = Integer.parseInt(parts[4]);
                e.num = Integer.parseInt(parts[5]);
                res.add(0, e);
            }
        } catch (FileNotFoundException e2) {
            e2.printStackTrace();
        } catch (IOException e3) {
            e3.printStackTrace();
        }
        return res;
    }

    public static class Entry {
        long duration;
        long end;
        int num;
        long start;
        int status;
        String taskName;

        public String toString() {
            return String.valueOf(Logger.df.format(new Date(this.start))) + " : " + this.taskName + " #" + this.num + " " + this.duration + " ms";
        }
    }
}
