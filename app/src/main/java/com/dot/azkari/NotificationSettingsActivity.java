package com.dot.azkari;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import com.arttech.alarm.preferences.Notifications;
import com.arttech.alarm.preferences.NotificationsManager;
import com.dot.constants.ApplicationConstants;
import java.util.ArrayList;
import java.util.Calendar;

/* JADX INFO: loaded from: classes.dex */
public class NotificationSettingsActivity extends Activity {
    Notifications SelectedNotification = null;
    final int TIME_PICKER_DIALOG_ID = 100;

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_settings);
        ensureNotificationChannel();
        checkNotificationPermission();
        setListViewProperties();
    }

    private void ensureNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            android.app.NotificationManager nm = (android.app.NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
            if (nm != null) {
                android.app.NotificationChannel channel = new android.app.NotificationChannel(
                    com.buzzbox.mob.android.scheduler.ScheduledService.NOTIFICATION_CHANNEL_ID,
                    "Azkari Reminders",
                    android.app.NotificationManager.IMPORTANCE_DEFAULT
                );
                channel.setDescription("Reminders for morning, evening, and daily Azkar");
                nm.createNotificationChannel(channel);
            }
        }
    }

    private void checkNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            if (checkSelfPermission("android.permission.POST_NOTIFICATIONS") != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"}, 101);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                Utils.getToast(this, "Notification permission is required to receive Azkar reminders", R.drawable.failed, 1).show();
            }
        }
    }

    private void setListViewProperties() {
        TextView TvActivityTitle = (TextView) findViewById(R.id.txv_notification_settings_Header);
        Utils.setText(TvActivityTitle, getString(R.string.notificationsSettings));
        TextView txv_disable_hint = (TextView) findViewById(R.id.txv_disable_hint);
        txv_disable_hint.setTextSize(17.0f);
        if (ApplicationConstants.APPLICATION_LANGUAGE_DIRECTION == ApplicationConstants.Direction.LTR) {
            txv_disable_hint.setGravity(3);
        } else {
            txv_disable_hint.setGravity(5);
        }
        Utils.setText(txv_disable_hint, getString(R.string.disableNotifications));
        final ListView lv_items = (ListView) findViewById(R.id.lv_notifications);
        ArrayList<Notifications> notifications = NotificationsManager.getNotifications(getBaseContext());
        lv_items.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.dot.azkari.NotificationSettingsActivity.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
                TextView tv = (TextView) view.findViewById(R.id.tv_zeker_title);
                NotificationSettingsActivity.this.SelectedNotification = (Notifications) tv.getTag();
                NotificationSettingsActivity.this.showDialog(100);
            }
        });
        lv_items.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { // from class: com.dot.azkari.NotificationSettingsActivity.2
            @Override // android.widget.AdapterView.OnItemLongClickListener
            public boolean onItemLongClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
                Notifications SelectedNotification = (Notifications) lv_items.getItemAtPosition(arg2);
                if (!SelectedNotification.getNotificationIsEnabled().booleanValue()) {
                    return false;
                }
                SelectedNotification.setNotificationIsEnabled(false);
                Utils.getToast(NotificationSettingsActivity.this, NotificationSettingsActivity.this.getString(R.string.NotificationDisabled), R.drawable.success, 0).show();
                NotificationsManager.saveChanges(NotificationSettingsActivity.this.getBaseContext(), SelectedNotification, null);
                ListView lv = (ListView) NotificationSettingsActivity.this.findViewById(R.id.lv_notifications);
                ArrayList<Notifications> notifications2 = NotificationsManager.getNotifications(NotificationSettingsActivity.this.getBaseContext());
                lv.setAdapter((ListAdapter) new NotificationsAdapter(notifications2, NotificationSettingsActivity.this.getBaseContext()));
                return true;
            }
        });
        lv_items.setAdapter((ListAdapter) new NotificationsAdapter(notifications, getBaseContext()));
    }

    @Override // android.app.Activity
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 100:
                return getTimePickerDialog();
            default:
                return null;
        }
    }

    private Dialog getTimePickerDialog() {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService("layout_inflater");
        final View TimePickerDialogLayout = layoutInflater.inflate(R.layout.time_picker_dialog, (ViewGroup) findViewById(R.id.time_picker_dialog));
        AlertDialog.Builder DialogBuilder = new AlertDialog.Builder(this);
        DialogBuilder.setView(TimePickerDialogLayout);
        TextView tvDialogTitle = new TextView(getBaseContext());
        tvDialogTitle.setTextSize(25.0f);
        tvDialogTitle.setGravity(17);
        Utils.setText(tvDialogTitle, getString(R.string.setAlarm));
        DialogBuilder.setCustomTitle(tvDialogTitle);
        TimePicker timePicker = (TimePicker) TimePickerDialogLayout.findViewById(R.id.tp_notification_time);
        String[] date = Utils.convertTo24Hour(this.SelectedNotification.getNotificationTime()).split(",");
        timePicker.setCurrentHour(Integer.valueOf(Integer.parseInt(date[0])));
        timePicker.setCurrentMinute(Integer.valueOf(Integer.parseInt(date[1])));
        Button okBtn = new Button(this);
        Utils.setText(okBtn, getString(R.string.ok));
        Button cancelBtn = new Button(this);
        Utils.setText(cancelBtn, getString(R.string.cancel));
        TableRow.LayoutParams pars = new TableRow.LayoutParams(-2, -2);
        pars.weight = 50.0f;
        pars.gravity = 17;
        TableRow row = (TableRow) TimePickerDialogLayout.findViewById(R.id.tableRow1);
        cancelBtn.setOnClickListener(new View.OnClickListener() { // from class: com.dot.azkari.NotificationSettingsActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                NotificationSettingsActivity.this.removeDialog(100);
            }
        });
        okBtn.setOnClickListener(new View.OnClickListener() { // from class: com.dot.azkari.NotificationSettingsActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                TimePicker timepicker = (TimePicker) TimePickerDialogLayout.findViewById(R.id.tp_notification_time);
                timepicker.clearFocus();
                Calendar calendar = Calendar.getInstance();
                calendar.set(11, timepicker.getCurrentHour().intValue());
                calendar.set(12, timepicker.getCurrentMinute().intValue());
                calendar.set(13, 0);
                NotificationSettingsActivity.this.removeDialog(100);
                int _24Hours_hrs = calendar.getTime().getHours();
                int _24Hours_min = calendar.getTime().getMinutes();
                String _12HourFormat = Utils.convertTo12Hour(_24Hours_hrs, Integer.valueOf(_24Hours_min));
                NotificationSettingsActivity.this.SelectedNotification.setNotificationTime(_12HourFormat);
                NotificationSettingsActivity.this.SelectedNotification.setNotificationIsEnabled(true);
                NotificationsManager.saveChanges(NotificationSettingsActivity.this.getBaseContext(), NotificationSettingsActivity.this.SelectedNotification, timepicker);
                ListView lv = (ListView) NotificationSettingsActivity.this.findViewById(R.id.lv_notifications);
                ArrayList<Notifications> notifications = NotificationsManager.getNotifications(NotificationSettingsActivity.this.getBaseContext());
                lv.setAdapter((ListAdapter) new NotificationsAdapter(notifications, NotificationSettingsActivity.this.getBaseContext()));
                Utils.getToast(NotificationSettingsActivity.this, NotificationSettingsActivity.this.getString(R.string.DoneNotificationSet), R.drawable.success, 0).show();
                NotificationSettingsActivity.this.SelectedNotification = null;
            }
        });
        if (ApplicationConstants.APPLICATION_LANGUAGE_DIRECTION == ApplicationConstants.Direction.RTL) {
            row.addView(cancelBtn, pars);
            row.addView(okBtn, pars);
        } else if (ApplicationConstants.APPLICATION_LANGUAGE_DIRECTION == ApplicationConstants.Direction.LTR) {
            row.addView(okBtn, pars);
            row.addView(cancelBtn, pars);
        }
        Dialog timeDialog = DialogBuilder.create();
        timeDialog.setCanceledOnTouchOutside(false);
        return timeDialog;
    }
}
