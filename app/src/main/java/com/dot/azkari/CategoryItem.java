package com.dot.azkari;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.dot.constants.ApplicationConstants;

/* JADX INFO: loaded from: classes.dex */
public class CategoryItem extends CursorAdapter {
    private ImageView imgIcon;
    private final LayoutInflater inflater;
    private String mDrawableName;
    private int resId;
    private TextView tvTxt;
    private type typeOfItem;

    public enum type {
        PRAYERS,
        TASABEEH;

        /* JADX INFO: renamed from: values, reason: to resolve conflict with enum method */
        public static type[] valuesCustom() {
            type[] typeVarArrValuesCustom = values();
            int length = typeVarArrValuesCustom.length;
            type[] typeVarArr = new type[length];
            System.arraycopy(typeVarArrValuesCustom, 0, typeVarArr, 0, length);
            return typeVarArr;
        }
    }

    public CategoryItem(Context context, Cursor c, type typeOfItem) {
        super(context, c);
        this.typeOfItem = null;
        this.inflater = LayoutInflater.from(context);
        this.typeOfItem = typeOfItem;
    }

    @Override // android.widget.CursorAdapter
    public void bindView(View view, Context context, Cursor cursor) {
        this.imgIcon = (ImageView) view.findViewById(R.id.ivIcon);
        this.tvTxt = (TextView) view.findViewById(R.id.tvTxt);
        this.mDrawableName = cursor.getString(cursor.getColumnIndex("CAT_ICON"));
        this.resId = 0;
        try {
            this.resId = context.getResources().getIdentifier(this.mDrawableName, "drawable", context.getPackageName());
            this.imgIcon.setImageResource(this.resId);
        } catch (Exception e) {
            this.imgIcon.setImageResource(R.drawable.icon);
        }
        view.setTag(cursor.getString(0));
        Utils.setText(this.tvTxt, cursor.getString(cursor.getColumnIndex("CAT_NAME")));
        if (this.typeOfItem == type.TASABEEH) {
            this.tvTxt.setTag(Integer.valueOf(cursor.getInt(cursor.getColumnIndex("CAT_IS_USER_DEFINED"))));
        }
        if (ApplicationConstants.APPLICATION_LANGUAGE_ID == 1) {
            this.tvTxt.setLines(2);
        } else {
            this.tvTxt.setLines(3);
        }
    }

    @Override // android.widget.CursorAdapter
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = this.inflater.inflate(R.layout.categoryitem, parent, false);
        return view;
    }
}
