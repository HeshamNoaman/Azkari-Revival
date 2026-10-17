package com.buzzbox.mob.android.scheduler;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import com.buzzbox.mob.android.scheduler.cron.Predictor;
import com.buzzbox.mob.android.scheduler.cron.SchedulingPattern;
import com.buzzbox.mob.android.scheduler.db.MigrableSQLiteHelper;
import com.buzzbox.mob.android.scheduler.ui.MetaDataUtils;
import com.buzzbox.mob.android.scheduler.ui.SchedulerPreferenceActivity;
import com.buzzbox.mob.android.scheduler.ui.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.xmlpull.v1.XmlPullParser;

/* JADX INFO: loaded from: classes.dex */
public class SchedulerManager {
    private static final long MILLISECS_PER_MINUTE = 60000L;
    private static final long MILLISECS_PER_HOUR = 3600000L;
    private static final String PREFS_LAST_SESSION_TS_KEY = "buzzbox.scheduler.last_session_ts";
    protected static final String BUZZBOX_SCHEDULER = "buzzbox-scheduler";
    public static final String INTENT_TASK_END_PREFIX = "buzzbox.end.";
    public static final String INTENT_TASK_START_PREFIX = "buzzbox.start.";
    private static final String RANDOMNESS_PREFERENCE_KEY = "buzzbox.scheduler.randomness";
    public static final int SCHEDULER_CONFIG_REQ_CODE = 919817235;
    private static final String STATUS_DISABLED = "DISABLED";
    private static final String STATUS_PAUSED = "PAUSED";
    private static final String TASKS_PREFERENCE_KEY = "buzzbox.scheduler.tasks";
    private static final String TASKS_STATUS_PREFERENCE_KEY_PREFIX = "buzzbox.scheduler.tasks.status.";
    private static Random random = new Random();
    static SchedulerManager instance = null;

    private SchedulerManager() {
    }

    public static SchedulerManager getInstance() {
        if (instance != null) {
            return instance;
        }
        synchronized (SchedulerManager.class) {
            if (instance == null) {
                instance = new SchedulerManager();
            }
        }
        return instance;
    }

    public boolean isRunning(Context context) {
        for (ScheduledTask saved : loadTasks(context)) {
            if (saved.enabled) {
                return true;
            }
        }
        return false;
    }

    public void startConfigurationActivity(Activity context, Class<? extends Task> taskClass) {
        ScheduledTask sTask = getInstance().loadTask(context, taskClass.getName());
        Intent intent = new Intent(context, (Class<?>) SchedulerPreferenceActivity.class);
        try {
            Task instance2 = taskClass.newInstance();
            intent.putExtra("task.name", instance2.getTitle());
        } catch (Exception e) {
        }
        intent.putExtra("task.class", taskClass.getName());
        if (sTask != null) {
            intent.putExtra("cron", sTask.cron);
            intent.putExtra("cron.enabled", sTask.isEnabled());
        }
        context.startActivityForResult(intent, SCHEDULER_CONFIG_REQ_CODE);
    }

    public static Map<String, Boolean> getNotificationTypeStatus(Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        Map<String, Boolean> res = new HashMap<>();
        String notificationTypesString = MetaDataUtils.getString(ctx, "SchedulerPreferenceActivity.notificationTypes", null);
        if (notificationTypesString != null && !XmlPullParser.NO_NAMESPACE.equals(notificationTypesString.trim())) {
            String[] notificationTypes = notificationTypesString.split(",");
            for (int i = 0; i < notificationTypes.length; i++) {
                String defaultSettingsString = MetaDataUtils.getString(ctx, "SchedulerPreferenceActivity.notificationTypes.defaultSettings." + notificationTypes[i], "statusBar=enabled,vibrate=disabled,led=disabled,sound=disabled");
                Map<String, String> settingsMap = StringUtils.optionsInteger2map(",", '=', defaultSettingsString);
                res.put(notificationTypes[i], Boolean.valueOf(prefs.getBoolean("cron.statusBar." + notificationTypes[i], "enabled".equals(settingsMap.get("statusBar")))));
            }
        }
        return res;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public synchronized void handleConfigurationResult(Context context, Intent data) {
        if (data == null) {
            Log.e(BUZZBOX_SCHEDULER, "nothing to save");
        } else {
            try {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editPrefs = prefs.edit();
                String[] notificationTypes = data.getStringArrayExtra("notificationTypes");
                if (notificationTypes != null && notificationTypes.length > 0) {
                    for (int j = 0; j < notificationTypes.length; j++) {
                        if (data.hasExtra("cron.led." + notificationTypes[j])) {
                            editPrefs.putBoolean("cron.led." + notificationTypes[j], data.getBooleanExtra("cron.led." + notificationTypes[j], true));
                        }
                        if (data.hasExtra("cron.sound." + notificationTypes[j])) {
                            editPrefs.putBoolean("cron.sound." + notificationTypes[j], data.getBooleanExtra("cron.sound." + notificationTypes[j], false));
                        }
                        if (data.hasExtra("cron.vibrate." + notificationTypes[j])) {
                            editPrefs.putBoolean("cron.vibrate." + notificationTypes[j], data.getBooleanExtra("cron.vibrate." + notificationTypes[j], false));
                        }
                        if (data.hasExtra("cron.statusBar." + notificationTypes[j])) {
                            editPrefs.putBoolean("cron.statusBar." + notificationTypes[j], data.getBooleanExtra("cron.statusBar." + notificationTypes[j], false));
                        }
                    }
                } else {
                    if (data.hasExtra("cron.led")) {
                        editPrefs.putBoolean("cron.led", data.getBooleanExtra("cron.led", true));
                    }
                    if (data.hasExtra("cron.sound")) {
                        editPrefs.putBoolean("cron.sound", data.getBooleanExtra("cron.sound", false));
                    }
                    if (data.hasExtra("cron.vibrate")) {
                        editPrefs.putBoolean("cron.vibrate", data.getBooleanExtra("cron.vibrate", false));
                    }
                    if (data.hasExtra("cron.statusBar")) {
                        editPrefs.putBoolean("cron.statusBar", data.getBooleanExtra("cron.statusBar", false));
                    }
                }
                String cron = data.getStringExtra("cron");
                @SuppressWarnings("unchecked")
                Class<? extends Task> cls = (Class<? extends Task>) Class.forName(data.getStringExtra("task.class"));
                ScheduledTask saved = loadTask(context, cls.getName());
                if (saved == null) {
                    getInstance().saveTask(context, cron, cls);
                } else {
                    getInstance().saveTask(context, cron, cls, saved.deliverAsapOnDelay, saved.autoPauseHours);
                }
                editPrefs.commit();
                boolean enabled = data.getBooleanExtra("cron.enabled", true);
                if (enabled) {
                    getInstance().restart(context, cls);
                } else {
                    getInstance().stop(context, cls);
                }
            } catch (Exception e) {
                Log.e(BUZZBOX_SCHEDULER, XmlPullParser.NO_NAMESPACE, e);
            }
        }
    }

    public void saveTask(Context context, String cron, Class<? extends Task> taskClass) {
        saveTask(context, cron, taskClass, false, 0);
    }

    private void saveNotificationTypesDefault(Context context) {
        String notificationTypesString;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean defaultAlreadySaved = prefs.getBoolean("manifest.default.already.saved", false);
        if (!defaultAlreadySaved && (notificationTypesString = MetaDataUtils.getString(context, "SchedulerPreferenceActivity.notificationTypes", null)) != null && !XmlPullParser.NO_NAMESPACE.equals(notificationTypesString.trim())) {
            String[] notificationTypes = notificationTypesString.split(",");
            SharedPreferences.Editor editPrefs = prefs.edit();
            editPrefs.putBoolean("manifest.default.already.saved", true);
            for (int i = 0; i < notificationTypes.length; i++) {
                String defaultSettingsString = MetaDataUtils.getString(context, "SchedulerPreferenceActivity.notificationTypes.defaultSettings." + notificationTypes[i], "statusBar=enabled,vibrate=disabled,led=disabled,sound=disabled");
                Map<String, String> defaultSettingsMap = StringUtils.optionsInteger2map(",", '=', defaultSettingsString);
                editPrefs.putBoolean("cron.led." + notificationTypes[i], "enabled".equals(defaultSettingsMap.get("led")));
                editPrefs.putBoolean("cron.sound." + notificationTypes[i], "enabled".equals(defaultSettingsMap.get("sound")));
                editPrefs.putBoolean("cron.vibrate." + notificationTypes[i], "enabled".equals(defaultSettingsMap.get("vibrate")));
                editPrefs.putBoolean("cron.statusBar." + notificationTypes[i], "enabled".equals(defaultSettingsMap.get("statusBar")));
            }
            editPrefs.commit();
        }
    }

    public void saveTask(Context context, String cron, Class<? extends Task> taskClass, boolean deliverAsapOnDelay, int autoPauseHours) {
        String taskClassName = taskClass.getName();
        MigrableSQLiteHelper.createFirstTime(context);
        saveNotificationTypesDefault(context);
        boolean manifestRequirementsOk = true;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        boolean permissionsChecked = prefs.getBoolean("manifest.permissions.checked", false);
        if (!permissionsChecked) {
            edit.putBoolean("manifest.permissions.checked", true);
            if (!MetaDataUtils.checkSchedulerPermission(context)) {
                manifestRequirementsOk = false;
                Log.e(BUZZBOX_SCHEDULER, "<task not saved!>");
            }
        }
        boolean servicesChecked = prefs.getBoolean("manifest.services.checked", false);
        if (!servicesChecked) {
            edit.putBoolean("manifest.services.checked", true);
            if (!MetaDataUtils.checkSchedulerServices(context)) {
                manifestRequirementsOk = false;
                Log.e(BUZZBOX_SCHEDULER, "<task not saved!>");
            }
        }
        boolean receiversChecked = prefs.getBoolean("manifest.receivers.checked", false);
        if (!receiversChecked) {
            edit.putBoolean("manifest.receivers.checked", true);
            if (!MetaDataUtils.checkSchedulerReceivers(context)) {
                manifestRequirementsOk = false;
                Log.e(BUZZBOX_SCHEDULER, "<task not saved!>");
            }
        }
        edit.commit();
        if (manifestRequirementsOk) {
            List<ScheduledTask> tasks = loadTasks(context);
            new SchedulingPattern(cron);
            StringBuilder serialized = new StringBuilder();
            for (ScheduledTask savedTask : tasks) {
                if (!savedTask.taskClassName.equals(taskClassName)) {
                    serialized.append(savedTask.taskClassName);
                    serialized.append("@");
                    serialized.append(savedTask.cron);
                    serialized.append("@");
                    serialized.append(savedTask.deliverAsapOnDelay ? "1" : "0");
                    serialized.append("@");
                    serialized.append(savedTask.autoPauseHours);
                    serialized.append(";");
                }
            }
            serialized.append(taskClassName);
            serialized.append("@");
            serialized.append(cron);
            serialized.append("@");
            serialized.append(deliverAsapOnDelay ? "1" : "0");
            serialized.append("@");
            serialized.append(autoPauseHours);
            Log.d(BUZZBOX_SCHEDULER, serialized.toString());
            edit.putString(TASKS_PREFERENCE_KEY, serialized.toString());
            edit.commit();
        }
    }

    protected ScheduledTask loadTask(Context context, String taskClassName) {
        for (ScheduledTask saved : loadTasks(context)) {
            if (saved.taskClassName.equals(taskClassName)) {
                return saved;
            }
        }
        Log.e(BUZZBOX_SCHEDULER, "Class " + taskClassName + " has never been saved. Use SchdulerManager.getInstance().saveTask(...)");
        return null;
    }

    protected List<ScheduledTask> loadTasks(Context context) {
        List<ScheduledTask> tasks = new ArrayList<>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String serialized = prefs.getString(TASKS_PREFERENCE_KEY, XmlPullParser.NO_NAMESPACE);
        if (serialized.length() != 0) {
            for (String oneTask : serialized.split(";")) {
                try {
                    String[] elements = oneTask.split("@");
                    boolean needsNetwork = "1".equals(elements[2]);
                    ScheduledTask saved = new ScheduledTask(elements[0], elements[1], needsNetwork);
                    if (elements.length >= 4) {
                        int h = Integer.parseInt(elements[3]);
                        saved.autoPauseHours = h;
                    }
                    String status = getStatus(context, elements[0]);
                    if (status.startsWith(STATUS_DISABLED)) {
                        saved.setEnabled(false);
                    }
                    if (status.equals(STATUS_PAUSED)) {
                        saved.paused = true;
                    }
                    tasks.add(saved);
                } catch (Exception e) {
                    Log.e(BUZZBOX_SCHEDULER, "can't parse task [" + oneTask + "]");
                }
            }
        }
        return tasks;
    }

    public void runNow(Context ctx, Class<? extends Task> taskClass, long inMillisDelay) {
        ScheduledTask task = loadTask(ctx, taskClass.getName());
        if (task != null) {
            scheduleRetry(ctx, taskClass.getName(), task.cron, inMillisDelay, 0);
        }
    }

    public void runAllNow(Context ctx, long inMillisDelay) {
        for (ScheduledTask scheduledTask : loadTasks(ctx)) {
            scheduleRetry(ctx, scheduledTask.taskClassName, scheduledTask.cron, inMillisDelay, 0);
        }
    }

    protected void scheduleRetry(Context context, String className, String cron, long inMillisDelay, int retryCount) {
        AlarmManager mgr = (AlarmManager) context.getSystemService("alarm");
        Intent i = new Intent(context, (Class<?>) SchedulerReceiver.class);
        i.setAction(className);
        i.putExtra("taskClass", className);
        i.putExtra("cron", cron);
        i.putExtra("retryCount", retryCount);
        PendingIntent pi = PendingIntent.getBroadcast(context, className.hashCode(), i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        setAlarmSafe(mgr, AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + inMillisDelay, pi);
        long now = System.currentTimeMillis();
        try {
            Class<?> taskClass = Class.forName(className);
            String taskId = ((Task) taskClass.newInstance()).getId();
            Logger.log(context, String.valueOf(taskId) + "-scheduled-" + (inMillisDelay / 1000), now, now, 0, retryCount);
        } catch (Exception e) {
            Logger.log(context, e.getMessage(), now, now, 0, retryCount);
        }
        Log.i(BUZZBOX_SCHEDULER, "Alarm Set for Retry Task [" + className + "] in [" + (inMillisDelay / MILLISECS_PER_MINUTE) + "] minutes");
    }

    public void restart(Context context, Class<?> taskClass) {
        String className = taskClass.getName();
        for (ScheduledTask saved : loadTasks(context)) {
            if (saved.taskClassName.equals(className)) {
                restartSavedTask(context, taskClass, saved);
                return;
            }
        }
    }

    private void restartSavedTask(Context context, Class<?> taskClass, ScheduledTask saved) {
        setStatus(context, taskClass.getName(), "OK", System.currentTimeMillis());
        saved.paused = false;
        saved.enabled = true;
        scheduleNext(context, saved, 0L);
    }

    public void stop(Context context, Class<?> taskClass) {
        String taskClassName = taskClass.getName();
        AlarmManager mgr = (AlarmManager) context.getSystemService("alarm");
        Intent i = new Intent(context, (Class<?>) SchedulerReceiver.class);
        i.setAction(taskClassName);
        i.putExtra("taskClass", taskClassName);
        PendingIntent pi = PendingIntent.getBroadcast(context, taskClassName.hashCode(), i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        mgr.cancel(pi);
        setStatus(context, taskClassName, STATUS_DISABLED, System.currentTimeMillis());
        Log.i(BUZZBOX_SCHEDULER, "Canceled Alarm for Task [" + taskClassName + "]");
        long now = System.currentTimeMillis();
        try {
            String taskId = ((Task) taskClass.newInstance()).getId();
            Logger.log(context, String.valueOf(taskId) + "-disabled", now, now, 0, 0);
        } catch (Exception e) {
        }
    }

    protected void scheduleNext(Context context, ScheduledTask task, long delay) {
        if (task == null) {
            Log.e(BUZZBOX_SCHEDULER, "Cannot schedule next: task is null");
            return;
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        long lastOpen = prefs.getLong(PREFS_LAST_SESSION_TS_KEY, 0L);
        long now = System.currentTimeMillis();
        long lastOpenDiff = now - lastOpen;
        if (task.paused || !task.enabled) {
            Logger.log(context, String.valueOf(task.taskClassName) + " SKIP schedule. paused: " + task.paused + " enabled: " + task.enabled, now, now, 0, 0);
            return;
        }
        if (task.autoPauseHours > 0 && lastOpen > 0 && lastOpenDiff > ((long) task.autoPauseHours) * MILLISECS_PER_HOUR) {
            try {
                Class<?> taskClass = Class.forName(task.taskClassName);
                String taskId = ((Task) taskClass.newInstance()).getId();
                Logger.log(context, String.valueOf(taskId) + "-paused", now, now, 0, 0);
                setStatus(context, task.taskClassName, STATUS_PAUSED, now);
                return;
            } catch (Exception e) {
                e.printStackTrace();
                Logger.log(context, e.getMessage(), now, now, 0, 0);
                return;
            }
        }
        AlarmManager mgr = (AlarmManager) context.getSystemService("alarm");
        Predictor predictor = new Predictor(task.cron);
        Intent i = new Intent(context, (Class<?>) SchedulerReceiver.class);
        i.setAction(task.taskClassName);
        i.putExtra("taskClass", task.taskClassName);
        i.putExtra("cron", task.cron);
        PendingIntent pi = PendingIntent.getBroadcast(context, task.taskClassName.hashCode(), i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        long matchingTime = predictor.nextMatchingTime() + delay;
        int randomness = prefs.getInt(RANDOMNESS_PREFERENCE_KEY, 60000);
        if (randomness > 0 && matchingTime - now > randomness) {
            int addingMillis = random.nextInt(randomness);
            matchingTime += (long) addingMillis;
        }
        setAlarmSafe(mgr, AlarmManager.RTC_WAKEUP, matchingTime, pi);
        Log.i(BUZZBOX_SCHEDULER, "Alarm Set for Task [" + task.taskClassName + "] with Schedule [" + task.cron + "] in [" + ((matchingTime - now) / MILLISECS_PER_MINUTE) + "] minutes");
    }

    private void setAlarmSafe(AlarmManager mgr, int type, long triggerAtMillis, PendingIntent pi) {
        if (mgr == null) {
            return;
        }
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                if (mgr.canScheduleExactAlarms()) {
                    mgr.setExactAndAllowWhileIdle(type, triggerAtMillis, pi);
                } else {
                    mgr.setAndAllowWhileIdle(type, triggerAtMillis, pi);
                }
            } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                mgr.setExactAndAllowWhileIdle(type, triggerAtMillis, pi);
            } else {
                mgr.set(type, triggerAtMillis, pi);
            }
        } catch (SecurityException se) {
            Log.w(BUZZBOX_SCHEDULER, "SecurityException scheduling exact alarm; falling back to inexact alarm", se);
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    mgr.setAndAllowWhileIdle(type, triggerAtMillis, pi);
                } else {
                    mgr.set(type, triggerAtMillis, pi);
                }
            } catch (Exception ex) {
                Log.e(BUZZBOX_SCHEDULER, "Failed to schedule alarm fallback", ex);
            }
        }
    }

    protected boolean reScheduleNetworkTasks(Context context) {
        boolean tasksRescheduled = false;
        for (ScheduledTask task : loadTasks(context)) {
            if (task.deliverAsapOnDelay) {
                String s = getStatus(context, task.taskClassName);
                Log.i("scheduler-manager", "STATUS " + task.taskClassName + " = " + s);
                if (s != null && s.startsWith("ERR")) {
                    scheduleRetry(context, task.taskClassName, task.cron, ((random.nextInt(12) * 10) + 60) * 1000, 0);
                    tasksRescheduled = true;
                }
            }
        }
        return tasksRescheduled;
    }

    protected String getStatus(Context context, String taskClass) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(TASKS_STATUS_PREFERENCE_KEY_PREFIX + taskClass, "UNK");
    }

    protected void setStatus(Context context, String taskClass, String status, long ts) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(TASKS_STATUS_PREFERENCE_KEY_PREFIX + taskClass, String.valueOf(status) + "@" + ts);
        edit.commit();
    }

    protected void notifyError(Context context, String taskClass, long ts) {
        setStatus(context, taskClass, "ERR", ts);
    }

    protected void notifyOk(Context context, String taskClass, long ts) {
        String status = getStatus(context, taskClass);
        if ("ERR".equals(status)) {
            setStatus(context, taskClass, "OK", ts);
        }
    }

    public boolean restartAll(Context context) {
        for (ScheduledTask savedTask : loadTasks(context)) {
            try {
                if (savedTask.isEnabled()) {
                    restartSavedTask(context, Class.forName(savedTask.taskClassName), savedTask);
                }
            } catch (ClassNotFoundException e) {
            }
        }
        return true;
    }

    public boolean restartPaused(Context context) {
        for (ScheduledTask savedTask : loadTasks(context)) {
            if (savedTask.paused) {
                setStatus(context, savedTask.taskClassName, "OK", System.currentTimeMillis());
                savedTask.paused = false;
                scheduleNext(context, savedTask, 0L);
            }
        }
        return true;
    }

    public boolean stopAll(Context context) {
        for (ScheduledTask savedTask : loadTasks(context)) {
            try {
                stop(context, Class.forName(savedTask.taskClassName));
            } catch (ClassNotFoundException e) {
            }
        }
        return true;
    }

    public void saveGlobalRandomness(Context context, int millis) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putInt(RANDOMNESS_PREFERENCE_KEY, millis);
        edit.commit();
    }
}
