package com.dot.azkari;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.RemoteViews;
import com.dot.adapter.database.Azkar_DB_Adapter;
import com.dot.constants.ApplicationConstants;

/* JADX INFO: loaded from: classes.dex */
public class WidgetTimerService extends Service {
    private static final int CHECK_PERIOD = 5;
    private static Handler tickHandler = new Handler(Looper.getMainLooper());
    private static Long startTime;
    private static int timeCheckCounter = 0;
    private static int tsbeehStartCount = 0;
    private static int tsbeehEndtCount = 0;
    public static boolean isStarted = false;
    public static int tsbeehCount = 0;

    private static Runnable createTimerRunnable(final Context context) {
        return new Runnable() {
            @Override
            public void run() {
                WidgetTimerService.isStarted = true;
                RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.app_widget);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                Long spentTime = Long.valueOf(System.currentTimeMillis() - (WidgetTimerService.startTime != null ? WidgetTimerService.startTime.longValue() : System.currentTimeMillis()));
                Long minius = Long.valueOf((spentTime.longValue() / 1000) / 60);
                Long seconds = Long.valueOf((spentTime.longValue() / 1000) % 60);
                remoteView.setTextViewText(R.id.txvWTime, ApplicationConstants.WrapTwoDigits(minius.longValue()) + ":" + ApplicationConstants.WrapTwoDigits(seconds.longValue()));
                if (WidgetTimerService.timeCheckCounter != 0) {
                    if (WidgetTimerService.timeCheckCounter == 5) {
                        WidgetTimerService.timeCheckCounter = 0;
                        WidgetTimerService.tsbeehEndtCount = WidgetTimerService.tsbeehCount;
                        if (WidgetTimerService.tsbeehStartCount == WidgetTimerService.tsbeehEndtCount) {
                            WidgetTimerService.isStarted = false;
                            WidgetTimerService.tickHandler.removeCallbacksAndMessages(null);
                            Azkar_DB_Adapter.Init(context);
                            Azkar_DB_Adapter.open();
                            int time = (int) (spentTime.longValue() / 1000);
                            Azkar_DB_Adapter.increment_Categories_Count_Time(String.valueOf(WidgetTimerService.tsbeehCount), String.valueOf(time), String.valueOf(WidgetProvider.catID));
                            Azkar_DB_Adapter.close();
                            WidgetTimerService.tsbeehCount = 0;
                            remoteView.setTextViewText(R.id.txvWTime, "00:00");
                            remoteView.setTextViewText(R.id.txvWCount, "0");
                            remoteView.setImageViewResource(R.id.btnWNext, R.drawable.next_on);
                            remoteView.setImageViewResource(R.id.btnWBack, R.drawable.prev_on);
                        }
                    } else {
                        WidgetTimerService.timeCheckCounter++;
                    }
                } else {
                    WidgetTimerService.tsbeehStartCount = WidgetTimerService.tsbeehCount;
                    WidgetTimerService.timeCheckCounter++;
                }
                ComponentName widgCom = new ComponentName(context, WidgetProvider.class);
                appWidgetManager.updateAppWidget(widgCom, remoteView);
                if (WidgetTimerService.isStarted) {
                    WidgetTimerService.tickHandler.postDelayed(this, 1000L);
                }
            }
        };
    }

    public static void handleAdd(Context context) {
        Context appContext = context.getApplicationContext();
        RemoteViews remoteView = new RemoteViews(appContext.getPackageName(), R.layout.app_widget);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(appContext);
        tsbeehCount++;
        remoteView.setTextViewText(R.id.txvWCount, String.valueOf(tsbeehCount));
        if (!isStarted) {
            startTime = Long.valueOf(System.currentTimeMillis() - 1000);
            tickHandler.removeCallbacksAndMessages(null);
            tickHandler.post(createTimerRunnable(appContext));
            remoteView.setImageViewResource(R.id.btnWNext, R.drawable.next_off);
            remoteView.setImageViewResource(R.id.btnWBack, R.drawable.prev_off);
        }
        ComponentName widgCom = new ComponentName(appContext, WidgetProvider.class);
        appWidgetManager.updateAppWidget(widgCom, remoteView);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        handleAdd(getApplicationContext());
        super.onStart(intent, startId);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
