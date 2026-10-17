package com.buzzbox.mob.android.scheduler.ui;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.util.Log;
import org.xmlpull.v1.XmlPullParser;

/* JADX INFO: loaded from: classes.dex */
public class MetaDataUtils {
    public static String getString(Context context, String keyName, String defaultValue) {
        String res = (String) getValue(context, keyName);
        return res == null ? defaultValue : res;
    }

    public static Boolean getBoolean(Context context, String keyName, boolean defaultValue) {
        Boolean res = (Boolean) getValue(context, keyName);
        if (res == null) {
            return Boolean.valueOf(defaultValue);
        }
        return res;
    }

    private static Object getValue(Context context, String keyName) {
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo applicationInfo = pm.getApplicationInfo(context.getPackageName(), 128);
            Bundle bundle = applicationInfo.metaData;
            if (bundle == null) {
                return null;
            }
            Object value = bundle.get(keyName);
            return value;
        } catch (Throwable ex) {
            Log.i(XmlPullParser.NO_NAMESPACE, "readKey", ex);
            return null;
        }
    }

    public static boolean checkSchedulerPermission(Context context) {
        PackageInfo packageInfo = null;
        PackageManager pm = context.getPackageManager();
        try {
            packageInfo = pm.getPackageInfo(context.getPackageName(), 4096);
        } catch (PackageManager.NameNotFoundException e) {
        }
        if (packageInfo == null || packageInfo.requestedPermissions == null) {
            return true;
        }
        boolean hasWakeLock = false;
        boolean hasVibrate = false;
        for (String perm : packageInfo.requestedPermissions) {
            if ("android.permission.WAKE_LOCK".equals(perm)) {
                hasWakeLock = true;
            } else if ("android.permission.VIBRATE".equals(perm)) {
                hasVibrate = true;
            }
        }
        if (hasWakeLock && hasVibrate) {
            return true;
        }
        Log.e("Manifest - Permissions Error", "You need to include the following permissions in the manifest.xml:\n\n\tandroid.permission.WAKE_LOCK\n\tandroid.permission.VIBRATE\n");
        return false;
    }

    public static boolean checkSchedulerReceivers(Context context) {
        boolean z = true;
        int count = 0;
        PackageInfo packageInfo = null;
        PackageManager pm = context.getPackageManager();
        try {
            try {
                packageInfo = pm.getPackageInfo(context.getPackageName(), 2);
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("scheduler", e.getMessage(), e);
            }
            ActivityInfo[] receivers = packageInfo.receivers;
            for (int j = 0; receivers != null && j < receivers.length; j++) {
                if ("com.buzzbox.mob.android.scheduler.SchedulerReceiver".equals(receivers[j].name)) {
                    count++;
                }
                if (count == 1) {
                    return true;
                }
            }
            if (count == 1) {
                return true;
            }
            Log.e("Manifest - Error", "You need to include the following receiver in the manifest.xml:\n\n\tcom.buzzbox.mob.android.scheduler.SchedulerReceiver\n");
            z = false;
            return false;
        } catch (Exception e2) {
            Log.e("scheduler", e2.getMessage(), e2);
            return z;
        }
    }

    public static boolean checkSchedulerServices(Context context) {
        int count = 0;
        PackageInfo packageInfo = null;
        PackageManager pm = context.getPackageManager();
        try {
            packageInfo = pm.getPackageInfo(context.getPackageName(), 4);
        } catch (PackageManager.NameNotFoundException e) {
        }
        ServiceInfo[] services = packageInfo.services;
        for (int j = 0; services != null && j < services.length; j++) {
            if ("com.buzzbox.mob.android.scheduler.ScheduledService".equals(services[j].name)) {
                count++;
            }
            if (count == 1) {
                return true;
            }
        }
        if (count == 1) {
            return true;
        }
        Log.e("Manifest - Error", "You need to include the following services in the manifest.xml:\n\n\tcom.buzzbox.mob.android.scheduler.ScheduledService\n");
        return false;
    }

    public static boolean checkSchedulerActivity(Context context, String activity) {
        int count = 0;
        PackageInfo packageInfo = null;
        PackageManager pm = context.getPackageManager();
        try {
            packageInfo = pm.getPackageInfo(context.getPackageName(), 1);
        } catch (PackageManager.NameNotFoundException e) {
        }
        ActivityInfo[] activities = packageInfo.activities;
        for (int j = 0; activities != null && j < activities.length; j++) {
            if (activity.equals(activities[j].name)) {
                count++;
            }
            if (count == 1) {
                return true;
            }
        }
        if (count == 1) {
            return true;
        }
        Log.i("Manifest - Info", "You will not save the notification history\n");
        return false;
    }
}
