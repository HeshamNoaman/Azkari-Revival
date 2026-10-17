package com.dot.azkari;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.arttech.alarm.preferences.Notifications;
import com.dot.constants.ApplicationConstants;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
class NotificationsAdapter extends BaseAdapter {
    private Context ctx;
    private LayoutInflater inflater;
    private ArrayList<Notifications> notificationsSet;

    public NotificationsAdapter(ArrayList<Notifications> notificationsSet, Context ctx) {
        this.inflater = null;
        this.ctx = ctx;
        this.notificationsSet = notificationsSet;
        this.inflater = (LayoutInflater) ctx.getSystemService("layout_inflater");
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.notificationsSet.size();
    }

    @Override // android.widget.Adapter
    public Object getItem(int position) {
        return this.notificationsSet.get(position);
    }

    @Override // android.widget.Adapter
    public long getItemId(int pos) {
        return pos;
    }

    @Override // android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        if (ApplicationConstants.APPLICATION_LANGUAGE_DIRECTION == ApplicationConstants.Direction.LTR) {
            v = this.inflater.inflate(R.layout.notification_list_item_english, (ViewGroup) null);
        } else {
            v = this.inflater.inflate(R.layout.notification_list_item_arabic, (ViewGroup) null);
        }
        TextView tvNotificationTime = (TextView) v.findViewById(R.id.tv_notification_time);
        TextView tvZekerTitle = (TextView) v.findViewById(R.id.tv_zeker_title);
        ImageView notification_Icon = (ImageView) v.findViewById(R.id.notification_icon);
        tvNotificationTime.setTag(this.notificationsSet.get(position));
        tvZekerTitle.setTag(this.notificationsSet.get(position));
        notification_Icon.setImageResource(this.notificationsSet.get(position).getNotificationIcon());
        Utils.setText(tvZekerTitle, this.notificationsSet.get(position).getNotificationName());
        if (this.notificationsSet.get(position).getNotificationIsEnabled().booleanValue()) {
            Utils.setText(tvNotificationTime, this.notificationsSet.get(position).getNotificationTime());
        } else {
            Utils.setText(tvNotificationTime, this.ctx.getString(R.string.NotificationNotSet));
        }
        return v;
    }
}
