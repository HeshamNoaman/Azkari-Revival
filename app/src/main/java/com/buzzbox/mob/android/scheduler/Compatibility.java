package com.buzzbox.mob.android.scheduler;

import android.graphics.BitmapFactory;
import android.webkit.WebSettings;
import java.lang.reflect.Field;

/* JADX INFO: loaded from: classes.dex */
public abstract class Compatibility {
    private static Compatibility instance;
    private static int sdkInt = 0;

    public abstract int getFlagExternalStorage();

    public abstract String getUa();

    public abstract void setAppCacheEnabled(WebSettings webSettings, boolean z);

    public abstract void setBitmatOptions(BitmapFactory.Options options);

    public static int getPlatformVersion() {
        try {
            Field field = Class.forName("android.os.Build$VERSION").getField("SDK_INT");
            return field.getInt(field);
        } catch (Exception e) {
            try {
                Field field2 = Class.forName("android.os.Build$VERSION").getField("SDK");
                String verString = (String) field2.get(field2);
                return Integer.parseInt(verString);
            } catch (Exception e2) {
                return 3;
            }
        }
    }

    public static Compatibility getInstance() {
        if (instance != null) {
            return instance;
        }
        sdkInt = getPlatformVersion();
        if (sdkInt < 7) {
            instance = new CompatibilityAPI3();
        } else if (sdkInt < 8) {
            instance = new CompatibilityAPI7();
        } else {
            instance = new CompatibilityAPI8();
        }
        return instance;
    }

    public int getSdkInt() {
        return sdkInt;
    }
}
