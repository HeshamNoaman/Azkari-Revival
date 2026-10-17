package com.dot.tasks;

import android.content.ContextWrapper;
import android.content.Intent;
import com.arttech.alarm.preferences.Notifications;
import com.arttech.alarm.preferences.NotificationsManager;
import com.buzzbox.mob.android.scheduler.NotificationMessage;
import com.buzzbox.mob.android.scheduler.Task;
import com.buzzbox.mob.android.scheduler.TaskResult;
import com.dot.azkari.StartupActivity;
import com.dot.azkari.Utils;
import com.dot.constants.ApplicationConstants;

/* JADX INFO: loaded from: classes.dex */
public class AzkarSleepTask implements Task {
    @Override // com.buzzbox.mob.android.scheduler.Task
    public String getTitle() {
        return "SleepReminder";
    }

    @Override // com.buzzbox.mob.android.scheduler.Task
    public String getId() {
        return "SleepReminder";
    }

    @Override // com.buzzbox.mob.android.scheduler.Task
    public TaskResult doWork(ContextWrapper ctx) {
        TaskResult res = new TaskResult();
        Notifications c_notification = NotificationsManager.getNotifications(ctx, 2);
        NotificationMessage notification = new NotificationMessage(c_notification.getNotificationName(), c_notification.getNotificationDescription());
        notification.setNotificationIconResource(c_notification.getNotificationIcon());
        notification.setNotificationSettings(true, true, false);
        Intent sabahIntent = new Intent();
        sabahIntent.setClass(ctx, StartupActivity.class);
        ApplicationConstants.CATEGORY_ID = "5";
        notification.setIntent(sabahIntent);
        Utils.setNotificationPrayerCatID(ctx, ApplicationConstants.CATEGORY_ID);
        res.addMessage(notification);
        return res;
    }
}
