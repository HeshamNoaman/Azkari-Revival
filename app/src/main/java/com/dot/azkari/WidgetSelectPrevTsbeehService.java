package com.dot.azkari;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.widget.RemoteViews;
import com.dot.adapter.database.Azkar_DB_Adapter;
import com.dot.constants.ApplicationConstants;

/* JADX INFO: loaded from: classes.dex */
public class WidgetSelectPrevTsbeehService extends Service {
    @Override // android.app.Service
    public void onStart(Intent intent, int startId) {
        if (!WidgetTimerService.isStarted) {
            RemoteViews remoteView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.app_widget);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
            Azkar_DB_Adapter.Init(getApplicationContext());
            Azkar_DB_Adapter.open();
            getApplicationContext();
            ApplicationConstants.APPLICATION_LANGUAGE_ID = getApplicationContext().getSharedPreferences(ApplicationConstants.APPLICATION_PREFERENCES, 0).getInt(ApplicationConstants.LANGUAGE_PREFERENCES, -1);
            if (ApplicationConstants.APPLICATION_LANGUAGE_ID == -1) {
                ApplicationConstants.APPLICATION_LANGUAGE_ID = 1;
            }
            Cursor tsabeehCrs = Azkar_DB_Adapter.selectAll_Tasabeeh_Categories();
            WidgetProvider.selectedTsbeehIndex--;
            if (WidgetProvider.selectedTsbeehIndex >= tsabeehCrs.getCount()) {
                WidgetProvider.selectedTsbeehIndex = 0;
            } else if (WidgetProvider.selectedTsbeehIndex <= 0) {
                WidgetProvider.selectedTsbeehIndex = tsabeehCrs.getCount() - 1;
            }
            tsabeehCrs.moveToPosition(WidgetProvider.selectedTsbeehIndex);
            WidgetProvider.catID = tsabeehCrs.getInt(0);
            String tsbeehText = tsabeehCrs.getString(1);
            tsabeehCrs.close();
            Azkar_DB_Adapter.close();
            remoteView.setTextViewText(R.id.txvWTsbeeh, tsbeehText);
            ComponentName widgCom = new ComponentName(getApplicationContext(), (Class<?>) WidgetProvider.class);
            appWidgetManager.updateAppWidget(widgCom, remoteView);
            super.onStart(intent, startId);
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
