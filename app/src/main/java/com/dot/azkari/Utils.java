package com.dot.azkari;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.dot.adapter.database.Azkar_DB_Adapter;
import com.dot.arabic.ArabicUtilities;
import com.dot.constants.ApplicationConstants;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import org.xmlpull.v1.XmlPullParser;

/* JADX INFO: loaded from: classes.dex */
public class Utils {
    public static void setText(TextView tv, String txt) {
        if (ApplicationConstants.APPLICATION_LANGUAGE_ID == 1) {
            tv.setTypeface(ApplicationConstants.ARABIC_TYPE_FACE);
            tv.setText(ArabicUtilities.reshape(txt));
        } else {
            tv.setText(txt);
        }
    }

    public static void setText(Button btn, String txt) {
        if (ApplicationConstants.APPLICATION_LANGUAGE_ID == 1) {
            btn.setTypeface(ApplicationConstants.ARABIC_TYPE_FACE);
            btn.setText(ArabicUtilities.reshape(txt));
        } else {
            btn.setText(txt);
        }
    }

    public static void setText(ToggleButton tglBtn, String txtOn, String txtOff) {
        if (ApplicationConstants.APPLICATION_LANGUAGE_ID == 1) {
            tglBtn.setTypeface(ApplicationConstants.ARABIC_TYPE_FACE);
            tglBtn.setTextOn(ArabicUtilities.reshape(txtOn));
            tglBtn.setTextOff(ArabicUtilities.reshape(txtOff));
            tglBtn.setText(ArabicUtilities.reshape(txtOff));
            return;
        }
        tglBtn.setTextOn(txtOn);
        tglBtn.setTextOff(txtOff);
        tglBtn.setText(txtOff);
    }

    public static boolean isOnline(Context context) {
        // App is strictly offline-first with zero network permissions.
        return false;
    }

    public static Toast getToast(Context context, String message, int imageID, int toastLength) {
        if (context == null) {
            return null;
        }
        String displayMsg = message != null ? message : "";
        // On Android 11+ (API 30+), toast.getView() is null and custom views are restricted.
        // Standard text toast created via Toast.makeText is safe and works reliably across all API levels.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Toast toast = Toast.makeText(context, displayMsg, toastLength);
            try {
                toast.setGravity(17, 0, 0);
            } catch (Throwable ignored) {
            }
            return toast;
        }

        try {
            Toast customToast = new Toast(context);
            LinearLayout toastLayout = new LinearLayout(context);
            toastLayout.setOrientation(LinearLayout.VERTICAL);
            toastLayout.setGravity(17);
            toastLayout.setPadding(30, 20, 30, 20);

            if (imageID != 0) {
                ImageView imageView = new ImageView(context);
                imageView.setImageResource(imageID);
                toastLayout.addView(imageView);
            }

            TextView txtview = new TextView(context);
            if (ApplicationConstants.APPLICATION_LANGUAGE_ID == 1) {
                if (ApplicationConstants.ARABIC_TYPE_FACE != null) {
                    txtview.setTypeface(ApplicationConstants.ARABIC_TYPE_FACE);
                }
                txtview.setText(ArabicUtilities.reshape(displayMsg));
            } else {
                txtview.setText(displayMsg);
            }
            txtview.setGravity(17);
            toastLayout.addView(txtview);

            customToast.setView(toastLayout);
            customToast.setDuration(toastLength);
            customToast.setGravity(17, 0, 0);
            return customToast;
        } catch (Throwable t) {
            return Toast.makeText(context, displayMsg, toastLength);
        }
    }

    public static void setCurrentLocaleFromShared(Activity activity) {
        String localeSymbol;
        String languageDir;
        SharedPreferences pref = activity.getSharedPreferences(ApplicationConstants.APPLICATION_PREFERENCES, 0);
        int currentLanguageID = pref.getInt(ApplicationConstants.LANGUAGE_PREFERENCES, -1);
        if (currentLanguageID != -1) {
            Azkar_DB_Adapter.Init(activity.getApplicationContext());
            Cursor languageCursor = Azkar_DB_Adapter.select_language_information_by_languageID(currentLanguageID);
            languageCursor.moveToPosition(0);
            localeSymbol = languageCursor.getString(0);
            languageDir = languageCursor.getString(1);
            languageCursor.close();
            Azkar_DB_Adapter.close();
        } else {
            currentLanguageID = 1;
            localeSymbol = "ar";
            languageDir = "RTL";
        }
        if (languageDir.equals("RTL")) {
            ApplicationConstants.APPLICATION_LANGUAGE_DIRECTION = ApplicationConstants.Direction.RTL;
        } else if (languageDir.equals("LTR")) {
            ApplicationConstants.APPLICATION_LANGUAGE_DIRECTION = ApplicationConstants.Direction.LTR;
        }
        ApplicationConstants.APPLICATION_LANGUAGE_ID = currentLanguageID;
        Locale locale = new Locale(localeSymbol);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        activity.getApplicationContext().getResources().updateConfiguration(config, activity.getBaseContext().getResources().getDisplayMetrics());
        ApplicationConstants.CURRENT_LOCALE = locale;
    }

    public static void setApplicationLocaleAndPreferences(Activity activity, int languageID) {
        SharedPreferences pref = activity.getSharedPreferences(ApplicationConstants.APPLICATION_PREFERENCES, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(ApplicationConstants.LANGUAGE_PREFERENCES, languageID);
        editor.commit();
        ApplicationConstants.APPLICATION_LANGUAGE_ID = languageID;
        Azkar_DB_Adapter.Init(activity.getApplicationContext());
        Cursor languageCursor = Azkar_DB_Adapter.select_language_information_by_languageID(languageID);
        languageCursor.moveToPosition(0);
        String localeSymbol = languageCursor.getString(0);
        String languageDir = languageCursor.getString(1);
        languageCursor.close();
        Azkar_DB_Adapter.close();
        Locale locale = new Locale(localeSymbol);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        activity.getBaseContext().getResources().updateConfiguration(config, activity.getBaseContext().getResources().getDisplayMetrics());
        if (languageDir.equals("RTL")) {
            ApplicationConstants.APPLICATION_LANGUAGE_DIRECTION = ApplicationConstants.Direction.RTL;
        } else if (languageDir.equals("LTR")) {
            ApplicationConstants.APPLICATION_LANGUAGE_DIRECTION = ApplicationConstants.Direction.LTR;
        }
        ApplicationConstants.CURRENT_LOCALE = locale;
    }

    public static void setLocale(Context baseContext) {
        Configuration config = new Configuration();
        config.locale = ApplicationConstants.CURRENT_LOCALE;
        baseContext.getResources().updateConfiguration(config, baseContext.getResources().getDisplayMetrics());
    }

    public static void setText(CheckBox chk, String txt) {
        if (ApplicationConstants.APPLICATION_LANGUAGE_ID == 1) {
            chk.setTypeface(ApplicationConstants.ARABIC_TYPE_FACE);
            chk.setText(ArabicUtilities.reshape(txt));
        } else {
            chk.setText(txt);
        }
    }

    public static void setTypeFace(View chrone) {
        if (chrone instanceof Chronometer) {
            if (ApplicationConstants.APPLICATION_LANGUAGE_ID == 1) {
                ((Chronometer) chrone).setTypeface(ApplicationConstants.SYSTEM_TYPE_FACES.get(0));
            }
        } else if (chrone instanceof TextView) {
            ((TextView) chrone).setTypeface(ApplicationConstants.SYSTEM_TYPE_FACES.get(0));
        }
    }

    public static String convertTo24Hour(String date) {
        if (date == null) {
            return "0,0";
        }
        try {
            String[] parts = date.trim().split(":");
            int Hr = Integer.parseInt(parts[0].trim());
            String[] minAndState = parts[1].trim().split("\\s+");
            int min = Integer.parseInt(minAndState[0].trim());
            String state = minAndState.length > 1 ? minAndState[1].trim() : "";
            if (state.equalsIgnoreCase("PM") && Hr != 12) {
                Hr += 12;
            } else if (state.equalsIgnoreCase("AM") && Hr == 12) {
                Hr = 0;
            }
            return Hr + "," + min;
        } catch (Exception e) {
            return "0,0";
        }
    }

    public static String convertTo12Hour(int Hours, Integer Mins) {
        String hr;
        String minuets;
        String pm_am = XmlPullParser.NO_NAMESPACE;
        if (Hours == 12) {
            pm_am = " PM";
        } else if (Hours < 12) {
            pm_am = " AM";
        } else if (Hours > 12) {
            Hours -= 12;
            pm_am = " PM";
        }
        if (Hours < 10) {
            hr = "0" + Hours;
        } else {
            hr = String.valueOf(Hours);
        }
        if (Mins.intValue() < 10) {
            minuets = "0" + Mins;
        } else {
            minuets = String.valueOf(Mins);
        }
        if (Hours == 0) {
            hr = "12";
        }
        return hr + ":" + minuets + pm_am;
    }

    public static void setNotificationPrayerCatID(Context ctx, String cat_id) {
        SharedPreferences shared = ctx.getSharedPreferences(ApplicationConstants.APPLICATION_PREFERENCES, 0);
        shared.edit().putString(ApplicationConstants.PRAYER_CAT_ID_NOTIFICATION_BAR, cat_id).commit();
    }

    public static String getNotificationPrayerCatID(Context ctx) {
        SharedPreferences shared = ctx.getSharedPreferences(ApplicationConstants.APPLICATION_PREFERENCES, 0);
        return shared.getString(ApplicationConstants.PRAYER_CAT_ID_NOTIFICATION_BAR, "-1");
    }

    public static int getScreenWidth(Activity activity) {
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        return width;
    }

    public static int getScreenHeight(Activity activity) {
        int height = activity.getWindowManager().getDefaultDisplay().getHeight();
        return height;
    }

    public static String getOsVersion() {
        String OsVersion = "NA";
        try {
            String[] androidOS = Build.VERSION.RELEASE.split("-");
            OsVersion = androidOS[0];
        } catch (Exception e) {
        }
        return "Android " + OsVersion;
    }

    public static int getLanguageIDFromPreferences(Context ctx) {
        int id = ctx.getSharedPreferences(ApplicationConstants.APPLICATION_PREFERENCES, 0).getInt(ApplicationConstants.LANGUAGE_PREFERENCES, 1);
        return id;
    }

    public static String getDeviceManufacturer() {
        String deviceManufacturer = Build.MANUFACTURER;
        if (deviceManufacturer == null) {
            return "Default";
        }
        return deviceManufacturer;
    }

    public static String getDeviceName() {
        String deviceName = Build.DEVICE;
        if (deviceName == null) {
            return "Default";
        }
        return deviceName;
    }

    public static String getDeviceModle() {
        String deviceModle = Build.MODEL;
        if (deviceModle == null) {
            return "Default";
        }
        return deviceModle;
    }

    public static String getDeviceLocale() {
        String deviceLocale = Resources.getSystem().getConfiguration().locale.getDisplayLanguage();
        if (deviceLocale == null) {
            return "English";
        }
        return deviceLocale;
    }

    public static String getApplicationLocale() {
        switch (ApplicationConstants.APPLICATION_LANGUAGE_ID) {
            case 1:
                return "Arabic";
            case 2:
                return "English";
            default:
                return "English";
        }
    }

    public static String getDevicePixelDensity(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        String screenDensity = new StringBuilder(String.valueOf(metrics.densityDpi)).toString();
        if (screenDensity.equals(XmlPullParser.NO_NAMESPACE)) {
            return "120";
        }
        return screenDensity;
    }

    public static String getDeviceResolution(Activity activity) {
        int screenDensity = Integer.parseInt(getDevicePixelDensity(activity));
        switch (screenDensity) {
            case 120:
                return "LD";
            case 160:
                return "MD";
            case 240:
                return "HD";
            default:
                return "LD";
        }
    }

    public static String getLastApplicationVersion() {
        return null;
    }

    public static void AzkariLog(String tag, String msg) {
    }

    public static String getDeviceTime() {
        String hours;
        String minutes;
        Calendar calendar = Calendar.getInstance();
        int _24Hours_hrs = calendar.getTime().getHours();
        int _24Hours_min = calendar.getTime().getMinutes();
        if (_24Hours_hrs < 10) {
            hours = "0" + _24Hours_hrs;
        } else {
            hours = new StringBuilder().append(_24Hours_hrs).toString();
        }
        if (_24Hours_min < 10) {
            minutes = "0" + _24Hours_min;
        } else {
            minutes = new StringBuilder().append(_24Hours_min).toString();
        }
        String time = String.valueOf(hours) + ":" + minutes;
        return time;
    }
}
