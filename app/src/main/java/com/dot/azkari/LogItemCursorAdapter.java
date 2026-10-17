package com.dot.azkari;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.dot.arabic.ArabicUtilities;
import com.dot.constants.ApplicationConstants;

/* JADX INFO: loaded from: classes.dex */
public class LogItemCursorAdapter extends CursorAdapter {
    private int firstAzkarId;
    private int firstTsabeehId;
    private final LayoutInflater mInflater;

    public LogItemCursorAdapter(Context context, Cursor c) {
        super(context, c, true);
        this.firstTsabeehId = -1;
        this.firstAzkarId = -1;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override // android.widget.CursorAdapter
    public void bindView(View rowView, Context context, Cursor cursor) {
        rowView.setTag(cursor.getString(0));
        TextView tvT = (TextView) rowView.findViewById(R.id.tvHeaderSep);
        int catId = Integer.parseInt(cursor.getString(0));
        if (cursor.getString(cursor.getColumnIndex("CAT_TYPE")).equals("T")) {
            if (this.firstTsabeehId == -1) {
                this.firstTsabeehId = catId;
            }
        } else if (this.firstAzkarId == -1) {
            this.firstAzkarId = catId;
        }
        if (catId == this.firstAzkarId) {
            tvT.setVisibility(0);
            Utils.setText(tvT, context.getResources().getString(R.string.azkar_title));
            tvT.setLines(1);
        } else if (catId == this.firstTsabeehId) {
            tvT.setVisibility(0);
            Utils.setText(tvT, context.getResources().getString(R.string.tasabeh_title));
            tvT.setLines(1);
        } else {
            tvT.setVisibility(8);
        }
        if (ApplicationConstants.APPLICATION_LANGUAGE_DIRECTION == ApplicationConstants.Direction.LTR) {
            tvT.setGravity(3);
        } else if (ApplicationConstants.APPLICATION_LANGUAGE_DIRECTION == ApplicationConstants.Direction.RTL) {
            tvT.setGravity(5);
        }
        TextView tvTitle = (TextView) rowView.findViewById(R.id.tvTitle);
        TextView tvCountLabel = (TextView) rowView.findViewById(R.id.tvCountLabel);
        TextView tvCount = (TextView) rowView.findViewById(R.id.tvCount);
        TextView tvPeriodLabel = (TextView) rowView.findViewById(R.id.tvPeriodLabel);
        TextView tvPeriod = (TextView) rowView.findViewById(R.id.tvPeriod);
        long periodInSeconds = Long.parseLong(cursor.getString(3));
        int hours = 0;
        int minute = 0;
        int seconds = 0;
        if (periodInSeconds > 0) {
            hours = (int) (periodInSeconds / 3600);
            long periodInSeconds2 = periodInSeconds % 3600;
            minute = (int) (periodInSeconds2 / 60);
            seconds = (int) (periodInSeconds2 % 60);
        }
        Utils.setText(tvTitle, cursor.getString(1));
        tvTitle.setLines(1);
        Utils.setText(tvCountLabel, context.getResources().getString(R.string.count));
        tvCountLabel.setLines(1);
        Utils.setText(tvPeriodLabel, context.getResources().getString(R.string.period));
        tvPeriodLabel.setLines(1);
        if (!Boolean.parseBoolean(tvCount.getTag().toString())) {
            String sec = ApplicationConstants.WrapTwoDigits(seconds);
            String min = ApplicationConstants.WrapTwoDigits(minute);
            String hour = ApplicationConstants.WrapTwoDigits(hours);
            String prd = String.valueOf(hour) + ":" + min + ":" + sec;
            Utils.setText(tvPeriod, prd);
            tvPeriod.setLines(1);
            tvCount.setLines(1);
            Utils.setText(tvCount, cursor.getString(2));
            return;
        }
        tvPeriod.setTypeface(ApplicationConstants.ARABIC_TYPE_FACE);
        tvPeriod.setText("00:00:00");
        tvPeriod.setLines(1);
        tvCount.setTypeface(ApplicationConstants.ARABIC_TYPE_FACE);
        tvCount.setText(ArabicUtilities.reshape("0"));
        tvCount.setLines(1);
    }

    @Override // android.widget.CursorAdapter
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        if (ApplicationConstants.APPLICATION_LANGUAGE_DIRECTION == ApplicationConstants.Direction.LTR) {
            View view = this.mInflater.inflate(R.layout.english_tsbeeh_item_count, parent, false);
            return view;
        }
        if (ApplicationConstants.APPLICATION_LANGUAGE_DIRECTION != ApplicationConstants.Direction.RTL) {
            return null;
        }
        View view2 = this.mInflater.inflate(R.layout.tsbeeh_item_count, parent, false);
        return view2;
    }
}
