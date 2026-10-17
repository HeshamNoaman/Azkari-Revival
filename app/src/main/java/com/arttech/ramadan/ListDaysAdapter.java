package com.arttech.ramadan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.dot.azkari.R;
import com.dot.azkari.Utils;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class ListDaysAdapter extends BaseAdapter {
    private Context ctx;
    private ArrayList<Day> list;

    public ListDaysAdapter(Context ctx, ArrayList<Day> list) {
        this.ctx = ctx;
        this.list = list;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.list.size();
    }

    @Override // android.widget.Adapter
    public Day getItem(int idx) {
        return this.list.get(idx);
    }

    @Override // android.widget.Adapter
    public long getItemId(int idx) {
        return idx;
    }

    @Override // android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = LayoutInflater.from(this.ctx);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.task_days, parent, false);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.tvDay);
        convertView.setTag(Integer.valueOf(this.list.get(position).getID()));
        ProgressBar Progress = (ProgressBar) convertView.findViewById(R.id.Progress);
        Progress.setMax(100);
        Utils.setTypeFace(tv);
        Utils.setText(tv, this.list.get(position).getName());
        float percentage = TasksDBAdapter.getInstance(this.ctx).GetDayProgress(this.list.get(position)) / 10.0f;
        Progress.setProgress((int) percentage);
        ImageView img_icon = (ImageView) convertView.findViewById(R.id.img_icon);
        int resId = this.ctx.getResources().getIdentifier(String.valueOf("ramadan" + this.list.get(position).getID()), "drawable", this.ctx.getPackageName());
        img_icon.setImageResource(resId);
        TextView tvPrecentage = (TextView) convertView.findViewById(R.id.tvPrecentage);
        Utils.setText(tvPrecentage, String.valueOf(String.valueOf((int) percentage)) + "%");
        return convertView;
    }
}
