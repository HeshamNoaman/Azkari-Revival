package com.buzzbox.mob.android.scheduler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class SchedulerReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        String intentAction = intent.getAction();
        if ("android.intent.action.BOOT_COMPLETED".equals(intentAction)) {
            Log.i("buzzbox-scheduler", "starting OnBootReceiver " + intent.getAction());
            SchedulerManager.getInstance().restartAll(context);
            return;
        }
        if ("android.intent.action.TIME_SET".equals(intentAction) || "android.intent.action.TIMEZONE_CHANGED".equals(intentAction) || "android.intent.action.DATE_CHANGED".equals(intentAction)) {
            SchedulerManager.getInstance().restartAll(context);
            return;
        }
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intentAction)) {
            return;
        }
        String taskId = intent.getStringExtra("message.task.id");
        if (taskId != null) {
            Intent startActivityIntent = (Intent) intent.getParcelableExtra("message.intent");
            if (startActivityIntent != null) {
                if (!"buzzbox-scheduler.nothing".equals(startActivityIntent.getAction())) {
                    try {
                        startActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(startActivityIntent);
                    } catch (Exception e) {
                        Log.w("buzzbox-scheduler", "Unable to start activity from receiver: " + e.getMessage());
                    }
                    return;
                }
                return;
            }
            Log.i("buzzbox-scheduler", "Nothing to do with this notification");
            return;
        }
        String taskClass = intent.getStringExtra("taskClass");
        if (taskClass != null) {
            Log.i("buzzbox-scheduler", "Time to run the Scheduler Service:" + taskClass);
            ScheduledService.processIntent(context, intent);
        }
    }
}
