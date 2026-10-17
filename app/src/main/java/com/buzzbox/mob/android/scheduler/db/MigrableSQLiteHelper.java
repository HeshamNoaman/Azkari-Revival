package com.buzzbox.mob.android.scheduler.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/* JADX INFO: loaded from: classes.dex */
public class MigrableSQLiteHelper {
    private static final int SCHEMA_VERSION = 0;
    static int dbCount = 0;
    static SQLiteDatabase instance;

    private static String getInternalDBName(Context context) {
        return "/data/data/" + context.getPackageName() + "/databases/sdk_migrable.db";
    }

    private static String getSDCardDir(Context context) {
        return "/sdcard/data/" + context.getPackageName() + "/db/";
    }

    private static String getSDCardDBName(Context context) {
        return String.valueOf(getSDCardDir(context)) + "sdk_migrable.db";
    }

    private static String getDefaultDBName(Context context) {
        return getInternalDBName(context);
    }

    public static void migrateStorage(String newPath, String oldPath) throws Exception {
        Log.w("buzzbox-scheduler", "Migrating database from '" + oldPath + "' to '" + newPath + "'");
        File of = new File(oldPath);
        File nf = new File(newPath);
        boolean isCleanup = true;
        if (!nf.exists()) {
            String dirp = newPath.substring(0, newPath.lastIndexOf("/") + 1);
            File dir = new File(dirp);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        } else {
            isCleanup = nf.delete();
        }
        if (!isCleanup) {
            throw new Exception("Impossible to clean existing database file on " + newPath);
        }
        nf.createNewFile();
        FileInputStream fis = new FileInputStream(of);
        FileOutputStream fos = new FileOutputStream(nf);
        byte[] buf = new byte[2048];
        while (true) {
            int read = fis.read(buf);
            if (read > 0) {
                fos.write(buf, 0, read);
            } else {
                fis.close();
                fos.flush();
                fos.close();
                of.delete();
                return;
            }
        }
    }

    public static synchronized void createFirstTime(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String databaseName = getDbPath(context);
        File dbFile = new File(databaseName);
        if (!dbFile.exists()) {
            SharedPreferences.Editor edit = prefs.edit();
            edit.putInt("db.schema_version", 0);
            edit.commit();
            File f = new File(databaseName);
            if (f.exists()) {
                f.delete();
            } else {
                String dirp = databaseName.substring(0, databaseName.lastIndexOf("/"));
                File dir = new File(dirp);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
            }
            SQLiteDatabase db = null;
            try {
                try {
                    db = SQLiteDatabase.openOrCreateDatabase(databaseName, (SQLiteDatabase.CursorFactory) null);
                    onCreateFirstTime(db);
                    if (db != null) {
                        db.close();
                    }
                } catch (Exception e) {
                    Log.e("buzzbox-scheduler", "error creating db", e);
                    if (db != null) {
                        db.close();
                    }
                }
            } catch (Throwable th) {
                if (db != null) {
                    db.close();
                }
                throw th;
            }
        }
    }

    public static String getDbPath(Context context) {
        String storageMethod = PreferenceManager.getDefaultSharedPreferences(context).getString("storage.method", "internal");
        String databaseName = getDefaultDBName(context);
        if ("sdcard".equals(storageMethod)) {
            String databaseName2 = getSDCardDBName(context);
            return databaseName2;
        }
        if ("internal".equals(storageMethod)) {
            String databaseName3 = getInternalDBName(context);
            return databaseName3;
        }
        return databaseName;
    }

    public static synchronized SQLiteDatabase getDatabase(Context context, int perms) {
        SQLiteDatabase db;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String databaseName = getDbPath(context);
        db = SQLiteDatabase.openDatabase(databaseName, null, perms);
        int ver = prefs.getInt("db.schema_version", -1);
        if (ver != 0) {
            if (ver > 0) {
                onUpgrade(db, ver, 0);
            } else {
                onCreateFirstTime(db);
            }
            SharedPreferences.Editor edit = prefs.edit();
            edit.putInt("db.schema_version", 0);
            edit.commit();
        }
        return db;
    }

    public static SQLiteDatabase getReadableDb(Context context) {
        return getWritableDb(context);
    }

    public static SQLiteDatabase getWritableDb(Context context) {
        dbCount++;
        if (instance != null) {
            return instance;
        }
        synchronized (MigrableSQLiteHelper.class) {
            if (instance == null) {
                instance = getDatabase(context, 0);
            }
        }
        Log.i("buzzbox-db", "Got database count: " + dbCount);
        return instance;
    }

    public static void closeDb(SQLiteDatabase db) {
        dbCount--;
        if (dbCount <= 0 && db != null && db.isOpen()) {
            try {
                Log.d("buzzbox-scheduler", "Closing database");
                synchronized (MigrableSQLiteHelper.class) {
                    instance = null;
                    dbCount = 0;
                    db.close();
                }
            } catch (Exception e) {
                Log.e("buzzbox-scheduler", "error closing db instance", e);
            }
        }
    }

    public static void onCreateFirstTime(SQLiteDatabase db) {
        onCreate(db, "notification");
    }

    public static void onCreate(SQLiteDatabase db, String table) {
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='" + table + "'", null);
        try {
            if (c.getCount() == 0 && "notification".equals(table)) {
                Log.i("buzzbox-scheduler", "Creating notification table");
                db.execSQL("CREATE TABLE notification (_id INTEGER PRIMARY KEY, group_id INTEGER, title TEXT, text TEXT, intent BLOB, notification_type TEXT, bg_color TEXT, main_icon_path TEXT, small_icon_path TEXT, created INTEGER);");
            }
        } catch (Throwable e) {
            Log.e("buzzbox-scheduler", "error creating buzzbox database", e);
        } finally {
            c.close();
        }
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("buzzbox-scheduler", "Upgrading database from " + oldVersion + " to " + newVersion);
    }
}
