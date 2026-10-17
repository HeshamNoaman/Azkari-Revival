package com.arttech.alarm.preferences;

import org.xmlpull.v1.XmlPullParser;

/* JADX INFO: loaded from: classes.dex */
public class Notifications {
    private String NotificationDescription;
    private int NotificationIcon;
    private Boolean NotificationIsEnabled;
    private String NotificationName;
    private String NotificationTime;
    private int index;

    public int getNotificationIcon() {
        return this.NotificationIcon;
    }

    public void setNotificationIcon(int notificationIcon) {
        this.NotificationIcon = notificationIcon;
    }

    public String getNotificationName() {
        return this.NotificationName;
    }

    public void setNotificationName(String notificationName) {
        this.NotificationName = notificationName;
    }

    public Boolean getNotificationIsEnabled() {
        return this.NotificationIsEnabled;
    }

    public void setNotificationIsEnabled(Boolean notificationIsEnabled) {
        this.NotificationIsEnabled = notificationIsEnabled;
    }

    public String getNotificationTime() {
        return this.NotificationTime;
    }

    public void setNotificationTime(String notificationTime) {
        this.NotificationTime = notificationTime;
    }

    public String getNotificationDescription() {
        return this.NotificationDescription;
    }

    public void setNotificationDescription(String notificationDesc) {
        this.NotificationDescription = notificationDesc;
    }

    public Notifications(String Name, int Icon, String Time, String Days, Boolean Enabled) {
        this.NotificationIcon = 0;
        this.NotificationName = XmlPullParser.NO_NAMESPACE;
        this.NotificationIsEnabled = false;
        this.NotificationTime = XmlPullParser.NO_NAMESPACE;
        this.NotificationDescription = XmlPullParser.NO_NAMESPACE;
        this.index = 0;
        this.NotificationIcon = Icon;
        this.NotificationName = Time;
        this.NotificationIsEnabled = Enabled;
        this.NotificationName = Name;
        this.NotificationDescription = Days;
    }

    public Notifications() {
        this.NotificationIcon = 0;
        this.NotificationName = XmlPullParser.NO_NAMESPACE;
        this.NotificationIsEnabled = false;
        this.NotificationTime = XmlPullParser.NO_NAMESPACE;
        this.NotificationDescription = XmlPullParser.NO_NAMESPACE;
        this.index = 0;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
