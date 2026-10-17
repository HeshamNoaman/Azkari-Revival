package com.buzzbox.mob.android.scheduler;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public abstract class WakefulIntentService extends IntentService {
    public static final String LOCK_NAME_STATIC = "com.buzzbox.mob.android.scheduler.WakefulIntentService";
    private static PowerManager.WakeLock lockStatic = null;

    protected abstract void doWakefulWork(Intent intent);

    public static void acquireStaticLock(Context context) {
        getLock(context).acquire();
    }

    private static synchronized PowerManager.WakeLock getLock(Context context) {
        if (lockStatic == null) {
            PowerManager mgr = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            lockStatic = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK_NAME_STATIC);
            lockStatic.setReferenceCounted(true);
        }
        return lockStatic;
    }

    public static void sendWakefulWork(Context ctxt, Intent i) {
        acquireStaticLock(ctxt);
        ctxt.startService(i);
    }

    public static void sendWakefulWork(Context ctxt, Class<?> clsService) {
        sendWakefulWork(ctxt, new Intent(ctxt, clsService));
    }

    public WakefulIntentService(String name) {
        super(name);
    }

    @Override // android.app.IntentService, android.app.Service
    public void onStart(Intent intent, int startId) {
        if (!getLock(this).isHeld()) {
            getLock(this).acquire();
            Log.d("buzzbox-scheduler", "Acquired CPU lock ");
        }
        super.onStart(intent, startId);
    }

    @Override // android.app.IntentService
    protected final void onHandleIntent(Intent intent) {
        long start = System.currentTimeMillis();
        try {
            doWakefulWork(intent);
        } finally {
            try {
                Log.d("buzzbox-scheduler", "Releasing CPU lock after " + (System.currentTimeMillis() - start));
                getLock(this).release();
            } catch (Exception e) {
                Log.e("buzzbox-scheduler", "Failed to release the wake lock", e);
            }
        }
    }
}
