package com.buzzbox.mob.android.scheduler;

import android.os.Build;

/* JADX INFO: loaded from: classes.dex */
public class CompatibilityAPI8 extends CompatibilityAPI7 {
    @Override // com.buzzbox.mob.android.scheduler.CompatibilityAPI7, com.buzzbox.mob.android.scheduler.Compatibility
    public String getUa() {
        return String.valueOf(Build.MANUFACTURER) + "|" + Build.MODEL + "|" + Build.DEVICE + "|" + Build.PRODUCT + "|" + Build.BRAND + "|" + Build.VERSION.SDK;
    }

    @Override // com.buzzbox.mob.android.scheduler.CompatibilityAPI7, com.buzzbox.mob.android.scheduler.Compatibility
    public int getFlagExternalStorage() {
        return 262144;
    }
}
