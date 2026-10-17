package com.buzzbox.mob.android.scheduler.db.model;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.util.Log;
import com.arttech.custom.dialogs.CustomSpinnerDialogExtras;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class Notification {
    private String bgColor;
    private long groupId;
    private long id;
    private byte[] intent;
    private String mainIcon;
    private String notificationType;
    private String smallIcon;
    private String text;
    private long time;
    private String title;

    public long save(SQLiteDatabase db) {
        return insert(db);
    }

    private long insert(SQLiteDatabase db) {
        ContentValues cv = getContentValues();
        try {
            return db.insertOrThrow("notification", "_id", cv);
        } catch (SQLiteConstraintException ce) {
            Log.w("buzzbox-scheduler", "already inserted", ce);
            return -1L;
        }
    }

    public static ArrayList<Notification> loadNotifications(SQLiteDatabase db) {
        Cursor c = null;
        ArrayList<Notification> results = new ArrayList<>();
        try {
            try {
                if (db.isOpen()) {
                    c = db.rawQuery("SELECT _id, title, text, intent, notification_type, bg_color, main_icon_path, small_icon_path, created, group_id FROM notification ORDER BY created DESC", null);
                    Log.e("buzzbox-scheduler", "SELECT _id, title, text, intent, notification_type, bg_color, main_icon_path, small_icon_path, created, group_id FROM notification ORDER BY created DESC -> count = " + c.getCount());
                    if (c.getCount() > 0) {
                        c.moveToFirst();
                        do {
                            results.add(new Notification().loadFrom(c));
                        } while (c.moveToNext());
                    }
                } else {
                    Log.e("buzzbox-scheduler", "db is not open?!");
                }
            } catch (Throwable e) {
                Log.e("notification-app", "Error trying to load all items", e);
            }
            return results;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public static int cleanNotifications(SQLiteDatabase db, int keepNotificationOfNDays) {
        if (keepNotificationOfNDays == -1) {
            return 0;
        }
        long lastCreatedTime = getLastCreatedTime(db);
        long temp = lastCreatedTime - ((long) (86400000 * keepNotificationOfNDays));
        return deleteOlderThan(db, temp);
    }

    public static long getLastCreatedTime(SQLiteDatabase db) {
        long result = 0;
        Cursor c = null;
        try {
            c = db.rawQuery("SELECT max(created) FROM notification", null);
            c.moveToFirst();
            result = c.getLong(0);
        } catch (Exception e) {
            Log.e("buzzbox-scheduler", "Error getLastCreatedTime", e);
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return result;
    }

    public static int getCount(SQLiteDatabase db) {
        int result = 0;
        Cursor c = null;
        try {
            c = db.rawQuery("SELECT count(1) FROM notification", null);
            c.moveToFirst();
            result = c.getInt(0);
        } catch (Exception e) {
            Log.e("buzzbox-scheduler", "Error counting notifications", e);
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return result;
    }

    public static int deleteOlderThan(SQLiteDatabase db, long created) {
        Log.d("buzzbox-scheduler", "Deleting notifications older than[" + created + "]");
        try {
            int res = db.delete("notification", "created < " + created, null);
            return res;
        } catch (Exception e) {
            Log.e("buzzbox-scheduler", "Error deleting notifications older than", e);
            return 0;
        }
    }

    public static int deleteById(SQLiteDatabase db, long id) {
        try {
            int res = db.delete("notification", "_id = " + id, null);
            return res;
        } catch (Exception e) {
            Log.e("buzzbox-scheduler", "Error deleting single notification", e);
            return 0;
        }
    }

    public static int deleteAll(SQLiteDatabase db) {
        try {
            int res = db.delete("notification", null, null);
            return res;
        } catch (Exception e) {
            Log.e("buzzbox-scheduler", "Error deleting all notifications", e);
            return 0;
        }
    }

    public Notification loadFrom(Cursor c) {
        this.id = c.getLong(0);
        this.title = c.getString(1);
        this.text = c.getString(2);
        this.intent = c.getBlob(3);
        this.notificationType = c.getString(4);
        this.bgColor = c.getString(5);
        this.mainIcon = c.getString(6);
        this.smallIcon = c.getString(7);
        this.time = c.getLong(8);
        this.groupId = c.getLong(9);
        return this;
    }

    private ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        cv.put("text", this.text);
        cv.put(CustomSpinnerDialogExtras.TITLE, this.title);
        cv.put("created", Long.valueOf(this.time));
        cv.put("intent", this.intent);
        cv.put("bg_color", this.bgColor);
        cv.put("main_icon_path", this.mainIcon);
        cv.put("small_icon_path", this.smallIcon);
        cv.put("notification_type", getNotificationType());
        cv.put("group_id", Long.valueOf(getGroupId()));
        return cv;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setIntent(byte[] intent) {
        this.intent = intent;
    }

    public long getId() {
        return this.id;
    }

    public String getText() {
        return this.text;
    }

    public String getTitle() {
        return this.title;
    }

    public byte[] getIntent() {
        return this.intent;
    }

    public long getGroupId() {
        return this.groupId;
    }

    public long getTime() {
        return this.time;
    }

    public String getBgColor() {
        return this.bgColor;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public String getMainIcon() {
        return this.mainIcon;
    }

    public String getSmallIcon() {
        return this.smallIcon;
    }

    public String getNotificationType() {
        return this.notificationType;
    }

    public void setMainIcon(String mainIcon) {
        this.mainIcon = mainIcon;
    }

    public void setSmallIcon(String smallIcon) {
        this.smallIcon = smallIcon;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public static byte[] intentToByteArray(Intent i) {
        Parcel p = Parcel.obtain();
        p.writeValue(i);
        return p.marshall();
    }

    public static Intent byteArrayToIntent(byte[] a) {
        Parcel p = Parcel.obtain();
        p.unmarshall(a, 0, a.length);
        p.setDataPosition(0);
        return (Intent) p.readValue(Intent.class.getClassLoader());
    }
}
