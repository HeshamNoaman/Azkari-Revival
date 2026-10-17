package com.arttech.ramadan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.dot.azkari.R;
import com.dot.azkari.Utils;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class ListTasksCategoriesMaster extends BaseAdapter {
    Context ctx;
    ArrayList<TaskType> list;

    public ListTasksCategoriesMaster(Context ctx, ArrayList<TaskType> list) {
        this.list = null;
        this.ctx = null;
        this.ctx = ctx;
        this.list = list;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.list.size();
    }

    @Override // android.widget.Adapter
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @Override // android.widget.Adapter
    public long getItemId(int position) {
        return 0L;
    }

    @Override // android.widget.Adapter
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater mInflater = LayoutInflater.from(this.ctx);
        TaskType type = this.list.get(position);
        if (view == null) {
            view = mInflater.inflate(R.layout.tasks_master_details_item_ar, parent, false);
            view.setTag(Integer.valueOf(type.getID()));
        }
        TextView tv = (TextView) view.findViewById(R.id.tvMaster);
        view.setTag(Integer.valueOf(type.getID()));
        Utils.setText(tv, type.getText());
        int resId = this.ctx.getResources().getIdentifier(String.valueOf(this.list.get(position).getIconName()), "drawable", this.ctx.getPackageName());
        ImageView img = (ImageView) view.findViewById(R.id.img_icon);
        img.setBackgroundResource(resId);
        return view;
    }
}
