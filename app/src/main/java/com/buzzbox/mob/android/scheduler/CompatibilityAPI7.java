package com.buzzbox.mob.android.scheduler;

import android.graphics.BitmapFactory;
import android.os.Build;
import android.webkit.WebSettings;

/* JADX INFO: loaded from: classes.dex */
public class CompatibilityAPI7 extends Compatibility {
    @Override // com.buzzbox.mob.android.scheduler.Compatibility
    public String getUa() {
        return String.valueOf(Build.MANUFACTURER) + "|" + Build.MODEL + "|" + Build.DEVICE + "|" + Build.PRODUCT + "|" + Build.BRAND + "|" + Build.VERSION.SDK;
    }

    @Override // com.buzzbox.mob.android.scheduler.Compatibility
    public int getFlagExternalStorage() {
        return 0;
    }

    @Override // com.buzzbox.mob.android.scheduler.Compatibility
    public void setBitmatOptions(BitmapFactory.Options options) {
        if (options != null) {
            options.inPurgeable = true;
            options.inInputShareable = true;
        }
    }

    @Override // com.buzzbox.mob.android.scheduler.Compatibility
    public void setAppCacheEnabled(WebSettings settings, boolean val) {
    }
}
