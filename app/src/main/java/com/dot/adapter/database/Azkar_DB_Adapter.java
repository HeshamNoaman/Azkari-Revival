package com.dot.adapter.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.dot.azkari.R;
import com.dot.azkari.Utils;
import com.dot.constants.ApplicationConstants;
import org.xmlpull.v1.XmlPullParser;

/* JADX INFO: loaded from: classes.dex */
public class Azkar_DB_Adapter {
    private static final String CATEGORIES_SCHEMA = "CREATE TABLE CATEGORIES ( CAT_ID INTEGER PRIMARY KEY , CAT_NAME TEXT  , CAT_TYPE INTEGER  , CAT_IS_USER_DEFINED INTEGER  , CAT_COUNT INTEGER  , CAT_TIME TEXT  , LAN_ID INTEGER  , CAT_ICON TEXT   );";
    private static final String CATEGORIES_TABLE = "CATEGORIES";
    private static final String DATABASE_NAME = "AZKAR_DB";
    private static final int DATABASE_VERSION = 3;
    private static final String LANGUAGES_SCHEMA = "CREATE TABLE LANGUAGES  ( LAN_ID INTEGER PRIMARY KEY , LAN_NAME TEXT  ,LAN_LOCALE TEXT, LAN_DIR TEXT  );";
    private static final String LANGUAGES_TABLE = "LANGUAGES";
    private static final String PRAYERS_SCHEMA = "CREATE TABLE PRAYERS    ( PRA_ID INTEGER PRIMARY KEY , CAT_ID INTEGER  , PRA_TEXT TEXT  , PRA_PREFE TEXT  , PRA_REPETITION INTEGER  , PRA_ORDER INTEGER  , LAN_ID INTEGER , PRA_AUD TEXT );";
    private static final String PRAYERS_TABLE = "PRAYERS";
    private static SQLiteDatabase azkarDB;
    private static Azkar_DB_Factory azkarFactory;
    private static Context context = null;

    private Azkar_DB_Adapter() {
    }

    public static void Init(Context ctx) {
        if (context == null && ctx != null) {
            context = ctx.getApplicationContext();
            azkarFactory = new Azkar_DB_Factory(context);
        }
    }

    public static void open() throws SQLException {
        if (azkarDB == null || !azkarDB.isOpen()) {
            azkarDB = azkarFactory.getWritableDatabase();
        }
    }

    public static void close() {
        if (azkarDB != null && azkarDB.isOpen()) {
            azkarFactory.close();
        }
    }

    public static Cursor select_language_information_by_languageID(int languageID) {
        open();
        return azkarDB.rawQuery(" SELECT  LAN_LOCALE,LAN_DIR FROM LANGUAGES WHERE LAN_ID= " + languageID, null);
    }

    public static Cursor selectCategoryLog() {
        open();
        StringBuilder query = new StringBuilder();
        query.append("SELECT CAT_ID AS _id , CAT_NAME , CAT_COUNT , CAT_TIME ,  CAT_TYPE  FROM CATEGORIES WHERE LAN_ID = " + ApplicationConstants.APPLICATION_LANGUAGE_ID + "  ORDER BY CAT_TYPE ASC ");
        return azkarDB.rawQuery(query.toString(), null);
    }

    public static Cursor select_categories() {
        open();
        return azkarDB.rawQuery("SELECT CAT_ID AS _id , CAT_NAME,CAT_ICON  FROM CATEGORIES WHERE CAT_TYPE='A' And LAN_ID =" + ApplicationConstants.APPLICATION_LANGUAGE_ID, null);
    }

    public static Cursor select_prayers_by_catid(String categoryId) {
        open();
        return azkarDB.rawQuery("SELECT  PRA_ID,PRA_TEXT,PRA_REPETITION,PRA_PREFE,PRA_AUD FROM PRAYERS WHERE LAN_ID= " + ApplicationConstants.APPLICATION_LANGUAGE_ID + " And CAT_ID =" + categoryId + " ORDER BY PRA_ORDER ASC ", null);
    }

    public static int select_prayers_repetition_count(String categoryId) {
        Cursor tmp_cursor = azkarDB.rawQuery("SELECT count(PRA_ID) FROM PRAYERS WHERE LAN_ID= " + ApplicationConstants.APPLICATION_LANGUAGE_ID + " And CAT_ID =" + categoryId, null);
        int count = 0;
        if (tmp_cursor.moveToFirst()) {
            count = Integer.parseInt(tmp_cursor.getString(0));
        }
        tmp_cursor.close();
        return count;
    }

    public static String select_prayer_prefer_by_prayerId(String prayerID) {
        Cursor tmp_cursor = azkarDB.rawQuery("SELECT PRA_PREFE  FROM PRAYERS WHERE LAN_ID= " + ApplicationConstants.APPLICATION_LANGUAGE_ID + " And PRA_ID =" + prayerID, null);
        String txt = XmlPullParser.NO_NAMESPACE;
        if (tmp_cursor.moveToFirst()) {
            txt = tmp_cursor.getString(0);
        }
        tmp_cursor.close();
        return txt;
    }

    public static int select_prayers_repetition_sum(String categoryId) {
        Cursor tmp_cursor = azkarDB.rawQuery("SELECT sum(PRA_REPETITION) FROM PRAYERS WHERE LAN_ID= " + ApplicationConstants.APPLICATION_LANGUAGE_ID + " And CAT_ID =" + categoryId, null);
        int sum = 0;
        if (tmp_cursor.moveToFirst()) {
            try {
                sum = Integer.parseInt(tmp_cursor.getString(0));
            } catch (Exception e) {
                sum = -1;
            }
        }
        tmp_cursor.close();
        return sum;
    }

    public static void increment_Categories_Count_Time(String timeIncrementInSeconds, String catID) {
        azkarDB.execSQL(" UPDATE CATEGORIES SET CAT_COUNT=CAT_COUNT+1 , CAT_TIME=CAT_TIME+" + timeIncrementInSeconds + " WHERE CAT_ID= " + catID);
    }

    public static void increment_Categories_Count_Time(String count, String timeIncrementInSeconds, String catID) {
        azkarDB.execSQL(" UPDATE CATEGORIES SET CAT_COUNT=CAT_COUNT+" + count + " , CAT_TIME=CAT_TIME+" + timeIncrementInSeconds + " WHERE CAT_ID= " + catID);
    }

    public static void insert_Tasbeeh_Categories(String name, String time, String icon, int count, int isUserDefined) {
        if (azkarDB != null) {
            azkarDB.execSQL("INSERT INTO " + CATEGORIES_TABLE + " ( CAT_NAME, CAT_TYPE, CAT_IS_USER_DEFINED, CAT_COUNT, CAT_TIME, LAN_ID, CAT_ICON ) VALUES (  \"" + name + "\", \"T\", " + isUserDefined + ", " + count + ", \"" + time + "\", " + ApplicationConstants.APPLICATION_LANGUAGE_ID + ", \"" + icon + "\" );");
        }
    }

    public static Cursor select_Language() {
        open();
        return azkarDB.rawQuery(" SELECT * FROM LANGUAGES", null);
    }

    public static Cursor select_tasbeehName_Categories(int id) {
        open();
        return azkarDB.rawQuery(" SELECT CAT_NAME FROM " + CATEGORIES_TABLE + " WHERE CAT_ID=" + id + " AND LAN_ID=" + ApplicationConstants.APPLICATION_LANGUAGE_ID, null);
    }

    public static Cursor select_tasbeehCount_Categories(int id) {
        open();
        return azkarDB.rawQuery(" SELECT CAT_COUNT FROM " + CATEGORIES_TABLE + " WHERE CAT_ID=" + id + " AND LAN_ID=" + ApplicationConstants.APPLICATION_LANGUAGE_ID, null);
    }

    public static Cursor select_tasbeehTime_Categories(int id) {
        open();
        return azkarDB.rawQuery(" SELECT CAT_TIME FROM " + CATEGORIES_TABLE + " WHERE CAT_ID=" + id + " AND LAN_ID=" + ApplicationConstants.APPLICATION_LANGUAGE_ID, null);
    }

    public static void update_Categories_CountAndTime(int id, int count, String time) {
        azkarDB.execSQL(" UPDATE " + CATEGORIES_TABLE + " SET CAT_COUNT=" + count + ", CAT_TIME=\"" + time + "\" WHERE CAT_ID=" + id);
    }

    public static Cursor select_ifUserDefined_Categories(int id) {
        open();
        return azkarDB.rawQuery(" SELECT CAT_IS_USER_DEFINED FROM " + CATEGORIES_TABLE + " WHERE CAT_ID=" + id + " AND LAN_ID=" + ApplicationConstants.APPLICATION_LANGUAGE_ID, null);
    }

    public static Cursor selectAll_Tasabeeh_Categories() {
        open();
        return azkarDB.rawQuery("SELECT CAT_ID as _id,CAT_NAME, CAT_IS_USER_DEFINED,CAT_ICON FROM " + CATEGORIES_TABLE + " WHERE CAT_TYPE=\"T\" AND LAN_ID=" + ApplicationConstants.APPLICATION_LANGUAGE_ID, null);
    }

    public static void delete_Tasbeeh_Categories(int id) {
        azkarDB.execSQL("DELETE FROM " + CATEGORIES_TABLE + " WHERE CAT_ID=" + id);
    }

    public static void reset_Categories_TimeAndCount(int id) {
        open();
        azkarDB.execSQL(" UPDATE CATEGORIES SET CAT_COUNT  = 0 , CAT_TIME = 0  WHERE  LAN_ID = " + ApplicationConstants.APPLICATION_LANGUAGE_ID + " AND CAT_ID = " + id);
        close();
    }

    private static class Azkar_DB_Factory extends SQLiteOpenHelper {
        Azkar_DB_Factory(Context context) {
            super(context, Azkar_DB_Adapter.DATABASE_NAME, (SQLiteDatabase.CursorFactory) null, 3);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(Azkar_DB_Adapter.LANGUAGES_SCHEMA);
            db.execSQL(Azkar_DB_Adapter.CATEGORIES_SCHEMA);
            db.execSQL(Azkar_DB_Adapter.PRAYERS_SCHEMA);
            db.execSQL("INSERT INTO LANGUAGES (LAN_NAME,LAN_LOCALE,LAN_DIR) VALUES ('Arabic','ar','RTL')");
            db.execSQL("INSERT INTO LANGUAGES (LAN_NAME,LAN_LOCALE,LAN_DIR) VALUES ('English','en','LTR')");
            String[] data = Azkar_DB_Adapter.context.getResources().getStringArray(R.array.arr_fillData);
            for (String str : data) {
                try {
                    db.execSQL(str);
                } catch (SQLException e) {
                    Utils.AzkariLog("SQLException", e.getMessage());
                } catch (IllegalArgumentException e2) {
                    Utils.AzkariLog("exception", String.valueOf(str) + "**" + e2.getMessage());
                }
            }
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS PRAYERS");
            db.execSQL("DROP TABLE IF EXISTS CATEGORIES");
            db.execSQL("DROP TABLE IF EXISTS LANGUAGES");
            onCreate(db);
        }
    }

    public static String select_category_name_by_category_ID(String catID) {
        open();
        Cursor tmp_cursor = azkarDB.rawQuery("SELECT CAT_NAME  FROM CATEGORIES WHERE CAT_ID= " + catID, null);
        String txt = XmlPullParser.NO_NAMESPACE;
        if (tmp_cursor.moveToFirst()) {
            txt = tmp_cursor.getString(0);
        }
        tmp_cursor.close();
        close();
        return txt;
    }

    public static Cursor selectAll_Lanaguages() {
        return azkarDB.rawQuery("SELECT * FROM " + LANGUAGES_TABLE, null);
    }
}
