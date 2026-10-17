package com.buzzbox.mob.android.scheduler;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import com.buzzbox.mob.android.scheduler.db.model.Notification;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class NotificationMessage {
    int BLUE_FLAG;
    int GREEN_FLAG;
    int PURPLE_FLAG;
    int RED_FLAG;
    int YELLOW_FLAG;
    public String bgColor;
    protected Long dbId;
    private boolean doLed;
    private boolean doSound;
    private boolean doVibrate;
    boolean dontShow;
    int flagResource;
    int flags;
    private byte[] intentByteArray;
    int ledColor;
    Bundle notificationClickIntentBundle;
    Class<?> notificationClickIntentClass;
    int notificationClickIntentFlags;
    List<NotificationMessage> notificationElements;
    private int notificationIconResource;
    int notificationId;
    private String notificationPanelText;
    private String notificationPanelTitle;
    int notificationPendingIntentFlags;
    private int notificationSettingsEnabled;
    private boolean notificationStatusBarEnabled;
    String notificationType;
    int number;
    private int priority;
    String sound;
    private String statusBarMessage;
    private String[] tags;
    long time;

    public List<NotificationMessage> getNotificationElements() {
        return this.notificationElements;
    }

    public void setNotificationElements(List<NotificationMessage> notificationElements) {
        this.notificationElements = notificationElements;
    }

    public NotificationMessage addNotificationElement(NotificationMessage notificationElement) {
        if (this.notificationElements == null) {
            this.notificationElements = new ArrayList();
        }
        this.notificationElements.add(notificationElement);
        return this;
    }

    public NotificationMessage(String title, String text) {
        this.notificationIconResource = 0;
        this.doVibrate = false;
        this.doSound = false;
        this.doLed = false;
        this.notificationSettingsEnabled = 0;
        this.ledColor = -65281;
        this.sound = "content://settings/system/notification_sound";
        this.number = 0;
        this.notificationClickIntentFlags = 872415232;
        this.notificationClickIntentBundle = new Bundle();
        this.notificationPendingIntentFlags = PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE;
        this.notificationId = 0;
        this.flags = 0;
        this.RED_FLAG = 1;
        this.BLUE_FLAG = 2;
        this.GREEN_FLAG = 3;
        this.YELLOW_FLAG = 4;
        this.PURPLE_FLAG = 5;
        this.flagResource = 0;
        this.dontShow = false;
        this.notificationType = null;
        this.statusBarMessage = title;
        this.notificationPanelTitle = title;
        this.notificationPanelText = text;
        this.notificationIconResource = 0;
    }

    public NotificationMessage(String statusBarMessage, String title, String text, int notificationIconResource) {
        this(null, statusBarMessage, title, text, notificationIconResource);
    }

    public NotificationMessage(String notificationType, String statusBarMessage, String title, String text, int notificationIconResource) {
        this.notificationIconResource = 0;
        this.doVibrate = false;
        this.doSound = false;
        this.doLed = false;
        this.notificationSettingsEnabled = 0;
        this.ledColor = -65281;
        this.sound = "content://settings/system/notification_sound";
        this.number = 0;
        this.notificationClickIntentFlags = 872415232;
        this.notificationClickIntentBundle = new Bundle();
        this.notificationPendingIntentFlags = PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE;
        this.notificationId = 0;
        this.flags = 0;
        this.RED_FLAG = 1;
        this.BLUE_FLAG = 2;
        this.GREEN_FLAG = 3;
        this.YELLOW_FLAG = 4;
        this.PURPLE_FLAG = 5;
        this.flagResource = 0;
        this.dontShow = false;
        this.notificationType = notificationType;
        this.statusBarMessage = statusBarMessage;
        this.notificationPanelTitle = title;
        this.notificationPanelText = text;
        this.notificationIconResource = notificationIconResource;
    }

    public NotificationMessage setTags(String... tags) {
        this.tags = tags;
        return this;
    }

    public String[] getTags() {
        return this.tags;
    }

    public int getFlagResource() {
        return this.flagResource;
    }

    public void setFlagResource(int flagResource) {
        this.flagResource = flagResource;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return this.time > 0 ? this.time : System.currentTimeMillis();
    }

    public String getStatusBarMessage() {
        return this.statusBarMessage;
    }

    public void setStatusBarMessage(String statusBarMessage) {
        this.statusBarMessage = statusBarMessage;
    }

    public String getNotificationPanelText() {
        return this.notificationPanelText;
    }

    public String getNotificationPanelTitle() {
        return this.notificationPanelTitle;
    }

    public NotificationMessage setNumber(int number) {
        this.number = number;
        return this;
    }

    public NotificationMessage setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    public NotificationMessage setFlags(int flags) {
        this.flags = flags;
        return this;
    }

    public NotificationMessage setSound(String soundUri) {
        this.sound = soundUri;
        return this;
    }

    public NotificationMessage setNotificationIconResource(int notificationIconResource) {
        this.notificationIconResource = notificationIconResource;
        return this;
    }

    public int getNotificationTypeSet() {
        return this.notificationSettingsEnabled;
    }

    public boolean isNotificationStatusBarEnabled() {
        return this.notificationStatusBarEnabled;
    }

    public void setNotificationStatusBarEnabled(boolean notificationStatusBarEnabled) {
        this.notificationStatusBarEnabled = notificationStatusBarEnabled;
    }

    public NotificationMessage setNotificationSettings(boolean doSound, boolean doVibrate, boolean doLed) {
        this.notificationSettingsEnabled = 1;
        this.doVibrate = doVibrate;
        this.doSound = doSound;
        this.doLed = doLed;
        return this;
    }

    public NotificationMessage setNotificationClickIntentBundle(Bundle notificationClickIntentBundle) {
        this.notificationClickIntentBundle = notificationClickIntentBundle;
        return this;
    }

    public NotificationMessage setNotificationClickIntentClass(Class<?> notificationClickIntentClass) {
        this.notificationClickIntentClass = notificationClickIntentClass;
        return this;
    }

    public NotificationMessage setNotificationId(int notificationId) {
        this.notificationId = notificationId;
        return this;
    }

    public int getNotificationId() {
        return this.notificationId;
    }

    public int getPriority() {
        return this.priority;
    }

    public int getNotificationIconResource() {
        return this.notificationIconResource;
    }

    public boolean isDoVibrate() {
        return this.doVibrate;
    }

    public boolean isDoSound() {
        return this.doSound;
    }

    public boolean isDoLed() {
        return this.doLed;
    }

    public String getNotificationType() {
        return this.notificationType;
    }

    public int getLedColor() {
        return this.ledColor;
    }

    public String getSound() {
        return this.sound;
    }

    public int getNumber() {
        return this.number;
    }

    public void setIntent(Intent intent) {
        this.intentByteArray = Notification.intentToByteArray(intent);
    }

    public byte[] getIntent() {
        return this.intentByteArray;
    }

    public void setDontShow(boolean dontShow) {
        this.dontShow = dontShow;
    }

    public boolean isDontShow() {
        return this.dontShow;
    }
}
