package com.buzzbox.mob.android.scheduler;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import com.arttech.custom.dialogs.CustomSpinnerDialogExtras;
import com.buzzbox.mob.android.scheduler.db.MigrableSQLiteHelper;
import com.buzzbox.mob.android.scheduler.ui.MetaDataUtils;
import com.buzzbox.mob.android.scheduler.ui.TimeUtils;
import org.xmlpull.v1.XmlPullParser;

/* JADX INFO: loaded from: classes.dex */
public class ScheduledService extends WakefulIntentService {
    public static final String NOTIFICATION_CHANNEL_ID = "azkari_reminders";
    static final long MAX_RETRY_DELAY_MILLIS = 86400000;

    public ScheduledService(String name) {
        super(name);
    }

    public ScheduledService() {
        super(ScheduledService.class.getName());
    }

    @Override // com.buzzbox.mob.android.scheduler.WakefulIntentService
    protected void doWakefulWork(Intent intent) {
        processIntent(this, intent);
    }

    public static void processIntent(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        if (intent.getBooleanExtra("save", false)) {
            SchedulerManager.getInstance().handleConfigurationResult(context, intent);
            return;
        }
        if (intent.getExtras() == null) {
            return;
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String taskClassName = intent.getExtras().getString("taskClass");
        String cron = intent.getExtras().getString("cron");
        Log.i("buzzbox-scheduler", "Ready to run [" + taskClassName + "] with cron [" + cron + "]");
        try {
            try {
                try {
                    SQLiteDatabase db = MigrableSQLiteHelper.getWritableDb(context);
                    context.sendBroadcast(new Intent(SchedulerManager.INTENT_TASK_START_PREFIX + taskClassName));
                    context.sendBroadcast(new Intent("buzzbox.start.ANY"));
                    long startTime = System.currentTimeMillis();
                    Class<?> c = Class.forName(taskClassName);
                    Task task = (Task) c.newInstance();
                    TaskResult result = task.doWork(new ContextWrapper(context));
                    long endTime = System.currentTimeMillis();
                    Logger.log(context, task, startTime, endTime, result);
                    if (result == null) {
                        Log.e("buzzbox-scheduler", "null result on task [" + taskClassName + "] - aborted");
                        context.sendBroadcast(new Intent(SchedulerManager.INTENT_TASK_END_PREFIX + taskClassName));
                        context.sendBroadcast(new Intent("buzzbox.end.ANY"));
                        return;
                    }
                    boolean ledEnabledDefault = prefs.getBoolean("cron.led", true);
                    boolean soundEnabledDefault = prefs.getBoolean("cron.sound", false);
                    boolean vibrateEnabledDefault = prefs.getBoolean("cron.vibrate", false);
                    boolean statusBarNotificationEnabledDefault = prefs.getBoolean("cron.statusBar", true);
                    Log.d("buzzbox-scheduler", "Task [" + taskClassName + "] done. Result [" + result.status + "] Messages [" + result.getMessages().size() + "]");
                    boolean saveNotificationFlag = false;
                    if (result.getMessages() != null && result.getMessages().size() > 0) {
                        MigrableSQLiteHelper.createFirstTime(context);
                        saveNotificationFlag = MetaDataUtils.checkSchedulerActivity(context, "com.buzzbox.mob.android.scheduler.ui.NotificationHistoryActivity");
                    }
                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && mNotificationManager != null) {
                        NotificationChannel channel = new NotificationChannel(
                            NOTIFICATION_CHANNEL_ID,
                            "Azkari Reminders",
                            NotificationManager.IMPORTANCE_DEFAULT
                        );
                        channel.setDescription("Reminders for morning, evening, and daily Azkar");
                        mNotificationManager.createNotificationChannel(channel);
                    }
                    for (NotificationMessage message : result.getMessages()) {
                        if (message.getNotificationElements() != null && message.getNotificationElements().size() == 1) {
                            message = message.getNotificationElements().get(0);
                        }
                        boolean ledEnabled = ledEnabledDefault;
                        boolean soundEnabled = soundEnabledDefault;
                        boolean vibrateEnabled = vibrateEnabledDefault;
                        boolean statusBarNotificationEnabled = statusBarNotificationEnabledDefault;
                        if (message.notificationType != null && !XmlPullParser.NO_NAMESPACE.equals(message.notificationType.trim())) {
                            ledEnabled = prefs.getBoolean("cron.led." + message.notificationType, ledEnabledDefault);
                            soundEnabled = prefs.getBoolean("cron.sound." + message.notificationType, soundEnabledDefault);
                            vibrateEnabled = prefs.getBoolean("cron.vibrate." + message.notificationType, vibrateEnabledDefault);
                            statusBarNotificationEnabled = prefs.getBoolean("cron.statusBar." + message.notificationType, statusBarNotificationEnabledDefault);
                        }
                        Intent intentToLaunch = saveNotificationForHistory(context, db, message, saveNotificationFlag);
                        if (!statusBarNotificationEnabled || message.isDontShow()) {
                            Log.d("buzzbox-scheduler", "Notification disabled. Type [" + message.notificationType + "] Msg [" + message.getNotificationPanelTitle() + "]");
                        } else {
                            int defNotResource = Resources.getSystem().getIdentifier("stat_notify_error", "drawable", "android");
                            int icon = message.getNotificationIconResource() == 0 ? defNotResource : message.getNotificationIconResource();
                            if (icon == 0) {
                                icon = com.dot.azkari.R.drawable.icon;
                            }
                            int code = message.getNotificationId() != 0 ? message.getNotificationId() : (String.valueOf(taskClassName) + message.getStatusBarMessage()).hashCode();
                            String taskId = task.getId();
                            if (message.notificationType != null) {
                                taskId = String.valueOf(taskId) + "-" + message.notificationType;
                            }
                            Intent contentIntent = intentToLaunch;
                            if (contentIntent == null || "buzzbox-scheduler.nothing".equals(contentIntent.getAction())) {
                                contentIntent = new Intent(context, com.dot.azkari.StartupActivity.class);
                            }
                            contentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            if (message.notificationClickIntentBundle != null) {
                                contentIntent.putExtras(message.notificationClickIntentBundle);
                            }
                            PendingIntent contentPendingIntent = PendingIntent.getActivity(
                                context,
                                code,
                                contentIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                            );

                            Notification.Builder builder;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                builder = new Notification.Builder(context, NOTIFICATION_CHANNEL_ID);
                            } else {
                                builder = new Notification.Builder(context);
                            }
                            builder.setSmallIcon(icon)
                                   .setTicker(message.getStatusBarMessage())
                                   .setContentTitle(message.getNotificationPanelTitle())
                                   .setContentText(message.getNotificationPanelText())
                                   .setContentIntent(contentPendingIntent)
                                   .setAutoCancel(true)
                                   .setWhen(System.currentTimeMillis());

                            if (message.getNotificationTypeSet() == 1) {
                                if (message.isDoVibrate()) {
                                    builder.setVibrate(new long[]{100, 200});
                                }
                            } else if (vibrateEnabled) {
                                builder.setVibrate(new long[]{100, 200});
                            }

                            if (message.getNotificationTypeSet() == 1) {
                                if (message.isDoLed()) {
                                    builder.setLights(message.ledColor, 300, 2000);
                                }
                            } else if (ledEnabled) {
                                builder.setLights(message.ledColor, 300, 2000);
                            }

                            if (message.getNotificationTypeSet() == 1) {
                                if (message.isDoSound() && message.sound != null) {
                                    builder.setSound(Uri.parse(message.sound));
                                }
                            } else if (soundEnabled && message.sound != null) {
                                builder.setSound(Uri.parse(message.sound));
                            }

                            if (message.number > 0) {
                                builder.setNumber(message.number);
                            }

                            int customLayout = context.getResources().getIdentifier("buzzbox_notification", "layout", context.getPackageName());
                            if (customLayout > 0) {
                                RemoteViews contentView = new RemoteViews(context.getPackageName(), customLayout);
                                contentView.setImageViewResource(context.getResources().getIdentifier("icon", "id", context.getPackageName()), icon);
                                contentView.setTextViewText(context.getResources().getIdentifier(CustomSpinnerDialogExtras.TITLE, "id", context.getPackageName()), message.getNotificationPanelTitle());
                                contentView.setTextViewText(context.getResources().getIdentifier("text", "id", context.getPackageName()), message.getNotificationPanelText());
                                contentView.setTextViewText(context.getResources().getIdentifier("time", "id", context.getPackageName()), TimeUtils.formatTime(message.getTime(), context));
                                if (message.bgColor != null) {
                                    if (!message.bgColor.startsWith("#")) {
                                        message.bgColor = "#" + message.bgColor;
                                    }
                                    int bgNotColorRes = Color.parseColor(message.bgColor);
                                    if (bgNotColorRes > 0) {
                                        contentView.setInt(context.getResources().getIdentifier("notification_container", "id", context.getPackageName()), "setBackgroundColor", bgNotColorRes);
                                    }
                                }
                                if (message.flagResource > 0) {
                                    contentView.setImageViewResource(context.getResources().getIdentifier("notification_flag", "id", context.getPackageName()), message.flagResource);
                                } else {
                                    contentView.setViewVisibility(context.getResources().getIdentifier("notification_flag", "id", context.getPackageName()), 8);
                                }
                                builder.setContent(contentView);
                            }

                            Notification notification = builder.build();
                            Logger.log(context, "Notify " + taskId + " (" + code + "," + message.getStatusBarMessage() + ")", System.currentTimeMillis(), System.currentTimeMillis(), 0, 0);
                            if (mNotificationManager != null) {
                                mNotificationManager.notify(code, notification);
                            }
                        }
                    }
                    int keepNotificationOfNDays = prefs.getInt("notificationHistoryActivity.notificationLimit", -1);
                    com.buzzbox.mob.android.scheduler.db.model.Notification.cleanNotifications(db, keepNotificationOfNDays);
                    if (result.isError()) {
                        Logger.log(context, "Scheduler got error result. Retry? [" + result.getRetry() + "]", System.currentTimeMillis(), System.currentTimeMillis(), result.status, 0);
                        Log.e("buzzbox-scheduler", "error on task [" + taskClassName + "] status [" + result.status + "]");
                        SchedulerManager.getInstance().notifyError(context, taskClassName, System.currentTimeMillis());
                        if (result.getRetry().equals(TaskResult.RetryPolicy.WAIT_FIX)) {
                            SchedulerManager.getInstance().scheduleRetry(context, taskClassName, cron, result.fixRetryMillis, 0);
                            context.sendBroadcast(new Intent(SchedulerManager.INTENT_TASK_END_PREFIX + taskClassName));
                            context.sendBroadcast(new Intent("buzzbox.end.ANY"));
                            return;
                        }
                        if (result.getRetry().equals(TaskResult.RetryPolicy.WAIT_EXPONENTIAL)) {
                            int retryCount = intent.getExtras().getInt("retryCount");
                            long retryDelay = (long) (((double) result.exponentialRetryMillis) * Math.pow(2.0d, retryCount));
                            if (retryDelay > MAX_RETRY_DELAY_MILLIS) {
                                retryDelay = MAX_RETRY_DELAY_MILLIS;
                            }
                            SchedulerManager.getInstance().scheduleRetry(context, taskClassName, cron, retryDelay, retryCount + 1);
                            context.sendBroadcast(new Intent(SchedulerManager.INTENT_TASK_END_PREFIX + taskClassName));
                            context.sendBroadcast(new Intent("buzzbox.end.ANY"));
                            return;
                        }
                        if (result.getRetry().equals(TaskResult.RetryPolicy.CONTINUE_NO_RETRY)) {
                            Log.i("buzzbox-scheduler", "continue...");
                        } else if (result.getRetry() == TaskResult.RetryPolicy.ABORT) {
                            Log.i("buzzbox-scheduler", "aborting task [" + taskClassName + "]");
                            context.sendBroadcast(new Intent(SchedulerManager.INTENT_TASK_END_PREFIX + taskClassName));
                            context.sendBroadcast(new Intent("buzzbox.end.ANY"));
                            return;
                        }
                    } else {
                        SchedulerManager.getInstance().notifyOk(context, taskClassName, System.currentTimeMillis());
                    }
                    context.sendBroadcast(new Intent(SchedulerManager.INTENT_TASK_END_PREFIX + taskClassName));
                    context.sendBroadcast(new Intent("buzzbox.end.ANY"));
                    SchedulerManager.getInstance().scheduleNext(context, SchedulerManager.getInstance().loadTask(context, taskClassName), 0L);
                } catch (ClassNotFoundException e) {
                    Log.e("buzzbox-scheduler", "Class not found [" + taskClassName + "]", e);
                    Logger.log(context, "* EXCEPTION [" + taskClassName + "] " + e.getMessage(), System.currentTimeMillis(), System.currentTimeMillis(), 0, 0);
                    context.sendBroadcast(new Intent(SchedulerManager.INTENT_TASK_END_PREFIX + taskClassName));
                    context.sendBroadcast(new Intent("buzzbox.end.ANY"));
                }
            } catch (Exception e2) {
                Log.e("buzzbox-scheduler", "Exception running [" + taskClassName + "]", e2);
                Logger.log(context, "* EXCEPTION [" + taskClassName + "] " + e2.getMessage(), System.currentTimeMillis(), System.currentTimeMillis(), 0, 0);
                context.sendBroadcast(new Intent(SchedulerManager.INTENT_TASK_END_PREFIX + taskClassName));
                context.sendBroadcast(new Intent("buzzbox.end.ANY"));
            }
        } catch (Throwable th) {
            context.sendBroadcast(new Intent(SchedulerManager.INTENT_TASK_END_PREFIX + taskClassName));
            context.sendBroadcast(new Intent("buzzbox.end.ANY"));
            throw th;
        }
    }

    private static Intent saveNotificationForHistory(Context context, SQLiteDatabase db, NotificationMessage message, boolean saveNotificationFlag) {
        Intent mainIntent = saveNotificationElementForHistory(context, db, message, null, saveNotificationFlag);
        if (message.getNotificationElements() != null) {
            for (NotificationMessage element : message.getNotificationElements()) {
                saveNotificationElementForHistory(context, db, element, message.dbId, saveNotificationFlag);
            }
        }
        return mainIntent;
    }

    private static Intent saveNotificationElementForHistory(Context context, SQLiteDatabase db, NotificationMessage message, Long groupId, boolean saveNotificationFlag) {
        Intent intent;
        Intent res;
        com.buzzbox.mob.android.scheduler.db.model.Notification notifToSave = new com.buzzbox.mob.android.scheduler.db.model.Notification();
        notifToSave.setTitle(message.getNotificationPanelTitle());
        notifToSave.setText(message.getNotificationPanelText());
        notifToSave.setNotificationType(message.getNotificationType());
        notifToSave.setBgColor(message.bgColor);
        notifToSave.setTime(message.getTime());
        int mainIconResourceId = message.getNotificationIconResource();
        if (mainIconResourceId > 0) {
            notifToSave.setMainIcon(context.getResources().getResourceName(mainIconResourceId));
        }
        int smallIconResourceId = message.getFlagResource();
        if (smallIconResourceId > 0) {
            notifToSave.setSmallIcon(context.getResources().getResourceName(smallIconResourceId));
        }
        byte[] intentByteArray = message.getIntent();
        if (intentByteArray != null && intentByteArray.length > 0) {
            notifToSave.setIntent(intentByteArray);
            res = com.buzzbox.mob.android.scheduler.db.model.Notification.byteArrayToIntent(intentByteArray);
        } else {
            if (message.notificationClickIntentClass != null) {
                intent = new Intent(context, message.notificationClickIntentClass);
                intent.addFlags(message.notificationClickIntentFlags);
                intent.putExtras(message.notificationClickIntentBundle);
            } else {
                intent = new Intent("buzzbox-scheduler.nothing");
            }
            notifToSave.setIntent(com.buzzbox.mob.android.scheduler.db.model.Notification.intentToByteArray(intent));
            res = intent;
        }
        if (groupId != null) {
            notifToSave.setGroupId(groupId.longValue());
        }
        if (saveNotificationFlag) {
            message.dbId = Long.valueOf(notifToSave.save(db));
        }
        return res;
    }
}
