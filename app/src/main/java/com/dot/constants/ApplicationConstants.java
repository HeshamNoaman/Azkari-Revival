package com.dot.constants;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import java.util.ArrayList;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class ApplicationConstants {
    public static Direction APPLICATION_LANGUAGE_DIRECTION = null;
    public static int APPLICATION_LANGUAGE_ID = 0;
    public static Typeface ARABIC_TYPE_FACE = null;
    public static String CATEGORY_ID = null;
    public static final String CONSUMER_KEY = "cgSIavTVNL6tQ9vbblDbg";
    public static final String CONSUMER_SECRET = "ovNCYHBDVtniOZ8QHEqMGohusOkWUgFI3JI4i8KeHc";
    public static final String CONTENT_LINK_AR = "http://azkari.arttech.ps/ar.php";
    public static final String CONTENT_LINK_EN = "http://azkari.arttech.ps/en.php";
    public static Locale CURRENT_LOCALE = null;
    public static final String FACEBOOK_APPLICATION_ID = "149734775042501";
    public static final String FIRST_TIME_PREFERENCES = "isFirstTime";
    public static final String FONT_TYPE_FACE_PREFERENCES = "FontType";
    public static final String LANGUAGE_PREFERENCES = "Language";
    public static final String PRAYER_CAT_ID_NOTIFICATION_BAR = "PRAYER_CAT_ID_FROM_NOTIFICATION";
    public static final String SOUND_PREFERENCES = "Sound";
    public static final String TWITTER_ACCESS_SECRET_PREF = "ACCESS_SECRET_PREF";
    public static final String TWITTER_ACCESS_TOKEN_PREF = "ACCESS_TOCKEN_PREF";
    public static final String TWITTER_CALLBACK_URL = "twitter://callback";
    public static final String TWITTER_CANCELBACK_URL = "twitter://cancel";
    public static final String VIBRATE_PREFERENCES = "Vibration";
    public static Boolean isCheckApplicationUpdate = true;
    public static Long application_ID = 1L;
    public static ArrayList<Typeface> SYSTEM_TYPE_FACES = new ArrayList<>();
    public static String APPLICATION_PREFERENCES = "AZKARI_SHARED_PREF";
    public static String PRAYERS_TEXT_FONT_SIZE = "PRAYERS_TEXT_SIZE_PREF";
    public static final String[] SYSTEM_FONTS = {"DroidSansArabic.ttf", "DroidNaskh-Regular.ttf", "DroidKufi-Regular.ttf", "DroidSansFallback.ttf"};
    public static boolean REFILL_GRIDS = false;

    public enum Direction {
        RTL,
        LTR;

        /* JADX INFO: renamed from: values, reason: to resolve conflict with enum method */
        public static Direction[] valuesCustom() {
            Direction[] directionArrValuesCustom = values();
            int length = directionArrValuesCustom.length;
            Direction[] directionArr = new Direction[length];
            System.arraycopy(directionArrValuesCustom, 0, directionArr, 0, length);
            return directionArr;
        }
    }

    public static void SetTypeFace(AssetManager assetManager, Activity activity) {
        SharedPreferences pref = activity.getSharedPreferences(APPLICATION_PREFERENCES, 0);
        int typeFaceIndex = pref.getInt(FONT_TYPE_FACE_PREFERENCES, 0);
        ARABIC_TYPE_FACE = SYSTEM_TYPE_FACES.get(typeFaceIndex);
    }

    public static String WrapTwoDigits(long number) {
        if (number < 10) {
            String digit = "0" + number;
            return digit;
        }
        String digit2 = new StringBuilder().append(number).toString();
        return digit2;
    }

    public static void initTypeFaces(Context ctx) {
        for (int i = 0; i < SYSTEM_FONTS.length; i++) {
            SYSTEM_TYPE_FACES.add(Typeface.createFromAsset(ctx.getAssets(), "fonts/" + SYSTEM_FONTS[i]));
        }
    }
}
