package com.dot.azkari;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import com.dot.adapter.database.Azkar_DB_Adapter;
import com.dot.constants.ApplicationConstants;

/* JADX INFO: loaded from: classes.dex */
public class WidgetProvider extends AppWidgetProvider {
    public static final String ACTION_WIDGET_ADD = "com.dot.azkari.WIDGET_ADD";
    public static final String ACTION_WIDGET_NEXT = "com.dot.azkari.WIDGET_NEXT";
    public static final String ACTION_WIDGET_PREV = "com.dot.azkari.WIDGET_PREV";

    public static int selectedTsbeehIndex = -1;
    public static int catID = -1;

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent == null || intent.getAction() == null) {
            return;
        }
        String action = intent.getAction();
        if (ACTION_WIDGET_ADD.equals(action)) {
            WidgetTimerService.handleAdd(context);
        } else if (ACTION_WIDGET_NEXT.equals(action)) {
            handleNext(context);
        } else if (ACTION_WIDGET_PREV.equals(action)) {
            handlePrev(context);
        }
    }

    public static void handleNext(Context context) {
        if (!WidgetTimerService.isStarted) {
            RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.app_widget);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            Azkar_DB_Adapter.Init(context);
            Azkar_DB_Adapter.open();
            ApplicationConstants.APPLICATION_LANGUAGE_ID = context.getSharedPreferences(ApplicationConstants.APPLICATION_PREFERENCES, 0).getInt(ApplicationConstants.LANGUAGE_PREFERENCES, -1);
            if (ApplicationConstants.APPLICATION_LANGUAGE_ID == -1) {
                ApplicationConstants.APPLICATION_LANGUAGE_ID = 1;
            }
            Cursor tsabeehCrs = Azkar_DB_Adapter.selectAll_Tasabeeh_Categories();
            if (tsabeehCrs != null && tsabeehCrs.getCount() > 0) {
                selectedTsbeehIndex++;
                if (selectedTsbeehIndex >= tsabeehCrs.getCount()) {
                    selectedTsbeehIndex = 0;
                } else if (selectedTsbeehIndex < 0) {
                    selectedTsbeehIndex = tsabeehCrs.getCount() - 1;
                }
                tsabeehCrs.moveToPosition(selectedTsbeehIndex);
                catID = tsabeehCrs.getInt(0);
                String tsbeehText = tsabeehCrs.getString(1);
                tsabeehCrs.close();
                remoteView.setTextViewText(R.id.txvWTsbeeh, tsbeehText);
            }
            Azkar_DB_Adapter.close();
            ComponentName widgCom = new ComponentName(context, WidgetProvider.class);
            appWidgetManager.updateAppWidget(widgCom, remoteView);
        }
    }

    public static void handlePrev(Context context) {
        if (!WidgetTimerService.isStarted) {
            RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.app_widget);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            Azkar_DB_Adapter.Init(context);
            Azkar_DB_Adapter.open();
            ApplicationConstants.APPLICATION_LANGUAGE_ID = context.getSharedPreferences(ApplicationConstants.APPLICATION_PREFERENCES, 0).getInt(ApplicationConstants.LANGUAGE_PREFERENCES, -1);
            if (ApplicationConstants.APPLICATION_LANGUAGE_ID == -1) {
                ApplicationConstants.APPLICATION_LANGUAGE_ID = 1;
            }
            Cursor tsabeehCrs = Azkar_DB_Adapter.selectAll_Tasabeeh_Categories();
            if (tsabeehCrs != null && tsabeehCrs.getCount() > 0) {
                selectedTsbeehIndex--;
                if (selectedTsbeehIndex >= tsabeehCrs.getCount()) {
                    selectedTsbeehIndex = 0;
                } else if (selectedTsbeehIndex < 0) {
                    selectedTsbeehIndex = tsabeehCrs.getCount() - 1;
                }
                tsabeehCrs.moveToPosition(selectedTsbeehIndex);
                catID = tsabeehCrs.getInt(0);
                String tsbeehText = tsabeehCrs.getString(1);
                tsabeehCrs.close();
                remoteView.setTextViewText(R.id.txvWTsbeeh, tsbeehText);
            }
            Azkar_DB_Adapter.close();
            ComponentName widgCom = new ComponentName(context, WidgetProvider.class);
            appWidgetManager.updateAppWidget(widgCom, remoteView);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);

            Intent wtsIntent = new Intent(context, WidgetProvider.class);
            wtsIntent.setAction(ACTION_WIDGET_ADD);
            wtsIntent.putExtra("appWidgetId", appWidgetId);
            PendingIntent timerServicePendingIntent = PendingIntent.getBroadcast(
                context,
                1,
                wtsIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            views.setOnClickPendingIntent(R.id.btnWAdd, timerServicePendingIntent);

            Intent menuIntent = new Intent(context, StartupActivity.class);
            PendingIntent menuPendingIntent = PendingIntent.getActivity(
                context,
                2,
                menuIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            views.setOnClickPendingIntent(R.id.rlyWdg, menuPendingIntent);

            Intent selectedTsbeehIntent = new Intent(context, WidgetProvider.class);
            selectedTsbeehIntent.setAction(ACTION_WIDGET_NEXT);
            selectedTsbeehIntent.putExtra("appWidgetId", appWidgetId);
            PendingIntent selectedTsbeehPendingIntent = PendingIntent.getBroadcast(
                context,
                3,
                selectedTsbeehIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            views.setOnClickPendingIntent(R.id.btnWNext, selectedTsbeehPendingIntent);

            Intent selectedPervTsbeehIntent = new Intent(context, WidgetProvider.class);
            selectedPervTsbeehIntent.setAction(ACTION_WIDGET_PREV);
            selectedPervTsbeehIntent.putExtra("appWidgetId", appWidgetId);
            PendingIntent selectedTsbeehPrevPendingIntent = PendingIntent.getBroadcast(
                context,
                4,
                selectedPervTsbeehIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            views.setOnClickPendingIntent(R.id.btnWBack, selectedTsbeehPrevPendingIntent);

            ApplicationConstants.APPLICATION_LANGUAGE_ID = context.getSharedPreferences(ApplicationConstants.APPLICATION_PREFERENCES, 0).getInt(ApplicationConstants.LANGUAGE_PREFERENCES, -1);
            if (ApplicationConstants.APPLICATION_LANGUAGE_ID == -1) {
                ApplicationConstants.APPLICATION_LANGUAGE_ID = 1;
            }
            Azkar_DB_Adapter.Init(context);
            Azkar_DB_Adapter.open();
            Cursor tsabeehCrs = Azkar_DB_Adapter.selectAll_Tasabeeh_Categories();
            if (tsabeehCrs != null && tsabeehCrs.moveToFirst()) {
                catID = tsabeehCrs.getInt(0);
                String tsbeehText = tsabeehCrs.getString(1);
                tsabeehCrs.close();
                selectedTsbeehIndex = 0;
                views.setTextViewText(R.id.txvWTsbeeh, tsbeehText);
            }
            Azkar_DB_Adapter.close();
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
