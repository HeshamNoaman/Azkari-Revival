package com.arttech.alarm.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.widget.TimePicker;
import com.buzzbox.mob.android.scheduler.SchedulerManager;
import com.dot.azkari.R;
import com.dot.azkari.Utils;
import com.dot.constants.ApplicationConstants;
import com.dot.tasks.AzkarAlmasaTask;
import com.dot.tasks.AzkarAlsabahTask;
import com.dot.tasks.AzkarSleepTask;
import com.dot.tasks.AzkarWakeUpTask;
import java.util.ArrayList;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class NotificationsManager {
    public static final String AZKAR_AL_ESTIQAD_NAOM_PREFERENCES_Days = "AZKAR_AL_ESTIQAD_NAOM_PREF_Days";
    public static final int AZKAR_AL_ESTIQAD_NAOM_PREFERENCES_Icon = 2130837586;
    public static final String AZKAR_AL_MASAA_PREFERENCES_Days = "AZKAR_AL_MASAA_PREF_Days";
    public static final int AZKAR_AL_MASAA_PREFERENCES_Icon = 2130837583;
    public static final String AZKAR_AL_SABAH_ALARM_PREFERENCES_Days = "AZKAR_AL_SABAH_ALARM_Days";
    public static final int AZKAR_AL_SABAH_ALARM_PREFERENCES_Icon = 2130837584;
    public static final String AZKAR_MAQABL_AL_NAOM_PREFERENCES_Days = "AZKAR_MAQABL_AL_NAOM_PREF_Days";
    public static final int AZKAR_MAQABL_AL_NAOM_PREFERENCES_Icon = 2130837585;
    public static final String Azkar_Notifications_Is_Set = "AZKAR_NOTIFICATIONS_IS_ENABLED";
    private static SharedPreferences.Editor editor;
    private static SharedPreferences shared;
    public static final String AZKARI_PREFERENCES = ApplicationConstants.APPLICATION_PREFERENCES;
    public static final String AZKAR_AL_SABAH_ALARM_PREFERENCES = "AZKAR_AL_SABAH_ALARM_PREF";
    public static final String AZKAR_AL_MASAA_PREFERENCES = "AZKAR_AL_MASAA_PREF";
    public static final String AZKAR_MAQABL_AL_NAOM_PREFERENCES = "AZKAR_MAQABL_AL_NAOM_PREF";
    public static final String AZKAR_AL_ESTIQAD_NAOM_PREFERENCES = "AZKAR_AL_ESTIQAD_NAOM_PREF";
    private static final String[] Alarm_Time_Preferences = {AZKAR_AL_SABAH_ALARM_PREFERENCES, AZKAR_AL_MASAA_PREFERENCES, AZKAR_MAQABL_AL_NAOM_PREFERENCES, AZKAR_AL_ESTIQAD_NAOM_PREFERENCES};
    public static final String AZKAR_AL_SABAH_ALARM_PREFERENCES_Is_Enabled = "AZKAR_AL_SABAH_ALARM_PREF_Enabled";
    public static final String AZKAR_AL_MASAA_PREFERENCES_Is_Enabled = "AZKAR_AL_MASAA_PREF_Enabled";
    public static final String AZKAR_MAQABL_AL_NAOM_PREFERENCES_Is_Enabled = "AZKAR_MAQABL_AL_NAOM_PREF_Enabled";
    public static final String AZKAR_AL_ESTIQAD_NAOM_PREFERENCES_Is_Enabled = "AZKAR_AL_ESTIQAD_NAOM_PREF_Enabled";
    private static final String[] Alarm_Enabled_Preferences = {AZKAR_AL_SABAH_ALARM_PREFERENCES_Is_Enabled, AZKAR_AL_MASAA_PREFERENCES_Is_Enabled, AZKAR_MAQABL_AL_NAOM_PREFERENCES_Is_Enabled, AZKAR_AL_ESTIQAD_NAOM_PREFERENCES_Is_Enabled};
    private static final int[] Alarm_Icons = {R.drawable.noti_morning_prayers, R.drawable.noti_evening_prayers, R.drawable.noti_sleep, R.drawable.noti_wake_up};
    private static Class[] Tasks = {AzkarAlsabahTask.class, AzkarAlmasaTask.class, AzkarSleepTask.class, AzkarWakeUpTask.class};

    private NotificationsManager() {
    }

    public static void ensureNotificationChannel(Context ctx) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O && ctx != null) {
            android.app.NotificationManager nm = (android.app.NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm != null) {
                android.app.NotificationChannel channel = new android.app.NotificationChannel(
                    com.buzzbox.mob.android.scheduler.ScheduledService.NOTIFICATION_CHANNEL_ID,
                    "Azkari Reminders",
                    android.app.NotificationManager.IMPORTANCE_DEFAULT
                );
                channel.setDescription("Reminders for morning, evening, and daily Azkar");
                nm.createNotificationChannel(channel);
            }
        }
    }

    public static void init(Context ctx) {
        ensureNotificationChannel(ctx);
        shared = ctx.getSharedPreferences(AZKARI_PREFERENCES, 0);
        editor = shared.edit();
        shared = ctx.getSharedPreferences(AZKARI_PREFERENCES, 0);
        editor = shared.edit();
        editor.putString(AZKAR_AL_SABAH_ALARM_PREFERENCES, "12:00 am");
        editor.putString(AZKAR_AL_MASAA_PREFERENCES, "12:00 am");
        editor.putString(AZKAR_MAQABL_AL_NAOM_PREFERENCES, "12:00 am");
        editor.putString(AZKAR_AL_ESTIQAD_NAOM_PREFERENCES, "12:00 am");
        editor.putString(AZKAR_AL_SABAH_ALARM_PREFERENCES_Days, "All");
        editor.putString(AZKAR_AL_MASAA_PREFERENCES_Days, "All");
        editor.putString(AZKAR_MAQABL_AL_NAOM_PREFERENCES_Days, "All");
        editor.putString(AZKAR_AL_ESTIQAD_NAOM_PREFERENCES_Days, "All");
        editor.putBoolean(AZKAR_AL_SABAH_ALARM_PREFERENCES_Is_Enabled, false);
        editor.putBoolean(AZKAR_AL_MASAA_PREFERENCES_Is_Enabled, false);
        editor.putBoolean(AZKAR_MAQABL_AL_NAOM_PREFERENCES_Is_Enabled, false);
        editor.putBoolean(AZKAR_AL_ESTIQAD_NAOM_PREFERENCES_Is_Enabled, false);
        editor.putBoolean(Azkar_Notifications_Is_Set, true);
        editor.commit();
    }

    public static void saveChanges(Context ctx, Notifications notification, TimePicker tp) {
        ensureNotificationChannel(ctx);
        shared = ctx.getSharedPreferences(AZKARI_PREFERENCES, 0);
        SharedPreferences.Editor editor2 = shared.edit();
        notification.getNotificationTime();
        int idx = notification.getIndex();
        editor2.putBoolean(Alarm_Enabled_Preferences[idx], notification.getNotificationIsEnabled().booleanValue());
        editor2.putString(Alarm_Time_Preferences[idx], notification.getNotificationTime());
        if (notification.getNotificationIsEnabled().booleanValue()) {
            SchedulerManager smn = SchedulerManager.getInstance();
            String Cronn;
            if (tp != null) {
                Cronn = tp.getCurrentMinute() + " " + tp.getCurrentHour() + " * * *";
            } else {
                String[] date = Utils.convertTo24Hour(notification.getNotificationTime()).split(",");
                Cronn = Integer.parseInt(date[1].trim()) + " " + Integer.parseInt(date[0].trim()) + " * * *";
            }
            smn.saveTask(ctx, Cronn, Tasks[notification.getIndex()]);
            smn.restart(ctx, Tasks[notification.getIndex()]);
        } else {
            SchedulerManager.getInstance().stop(ctx, Tasks[notification.getIndex()]);
        }
        editor2.commit();
    }

    private static String getStringPref(Context ctx, String PrefToGet) {
        shared = ctx.getSharedPreferences(AZKARI_PREFERENCES, 0);
        return shared.getString(PrefToGet, "-1");
    }

    private static Boolean getBoolPref(Context ctx, String PrefName) {
        shared = ctx.getSharedPreferences(AZKARI_PREFERENCES, 0);
        return Boolean.valueOf(shared.getBoolean(PrefName, false));
    }

    public static void setNotificationsPreferencesStatus(Context ctx, Boolean Value) {
        shared = ctx.getSharedPreferences(AZKARI_PREFERENCES, 0);
        SharedPreferences.Editor editor2 = shared.edit();
        editor2.putBoolean(Azkar_Notifications_Is_Set, Value.booleanValue());
        editor2.commit();
    }

    public static boolean getNotificationsPreferencesStatus(Context ctx) {
        shared = ctx.getSharedPreferences(AZKARI_PREFERENCES, 0);
        return shared.getBoolean(Azkar_Notifications_Is_Set, false);
    }

    public static ArrayList<Notifications> getNotifications(Context ctx) {
        ArrayList<Notifications> ret = new ArrayList<>();
        String[] N_Names = ctx.getResources().getStringArray(R.array.notifications_items);
        for (int i = 0; i < 4; i++) {
            Notifications not = new Notifications();
            not.setNotificationIcon(Alarm_Icons[i]);
            not.setNotificationIsEnabled(getBoolPref(ctx, Alarm_Enabled_Preferences[i]));
            not.setNotificationName(N_Names[i]);
            not.setNotificationTime(getStringPref(ctx, Alarm_Time_Preferences[i]));
            not.setIndex(i);
            ret.add(not);
        }
        return ret;
    }

    public static Notifications getNotifications(Context ctx, int NotificationIndex) {
        String[] N_Names;
        String[] Notification_description;
        if (Utils.getLanguageIDFromPreferences(ctx) == 1) {
            Configuration conf = ctx.getResources().getConfiguration();
            conf.locale = new Locale("ar");
            DisplayMetrics metrics = new DisplayMetrics();
            Resources resources = new Resources(ctx.getAssets(), metrics, conf);
            N_Names = resources.getStringArray(R.array.notifications_items);
            Notification_description = resources.getStringArray(R.array.notifications_items_description);
        } else {
            Configuration conf2 = ctx.getResources().getConfiguration();
            conf2.locale = new Locale("en");
            DisplayMetrics metrics2 = new DisplayMetrics();
            Resources resources2 = new Resources(ctx.getAssets(), metrics2, conf2);
            N_Names = resources2.getStringArray(R.array.notifications_items);
            Notification_description = resources2.getStringArray(R.array.notifications_items_description);
        }
        Notifications not = new Notifications();
        not.setNotificationDescription(Notification_description[NotificationIndex]);
        not.setNotificationIcon(Alarm_Icons[NotificationIndex]);
        not.setNotificationIsEnabled(getBoolPref(ctx, Alarm_Enabled_Preferences[NotificationIndex]));
        not.setNotificationName(N_Names[NotificationIndex]);
        not.setNotificationTime(getStringPref(ctx, Alarm_Time_Preferences[NotificationIndex]));
        not.setIndex(NotificationIndex);
        return not;
    }
}
