package com.arttech.custom.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import com.dot.arabic.ArabicUtilities;
import com.dot.azkari.R;
import com.dot.constants.ApplicationConstants;

/* JADX INFO: loaded from: classes.dex */
public class CustomSpinnerDialogAdapter extends ArrayAdapter<String> {
    private String Item_Position;
    private final Context context;
    private String dialog_type;
    String[] fontOriginalName;
    private String[] items;
    private String language;
    private String selected_item;

    public CustomSpinnerDialogAdapter(Context context, String[] items, String selected_item, String language, String dialog_type, String Item_Position) {
        super(context, R.layout.main);
        this.fontOriginalName = ApplicationConstants.SYSTEM_FONTS;
        this.context = context;
        this.language = language;
        this.items = items;
        this.selected_item = selected_item;
        this.dialog_type = dialog_type;
        this.Item_Position = Item_Position;
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public int getCount() {
        return this.items.length;
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int position, View View, ViewGroup parent) {
        View row_view;
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService("layout_inflater");
        if (this.language.equals("english")) {
            row_view = inflater.inflate(R.layout.listviewlayoutenglish, (ViewGroup) null);
        } else {
            row_view = inflater.inflate(R.layout.listviewlayoutarabic, (ViewGroup) null);
        }
        TextView tv_data = (TextView) row_view.findViewById(R.id.label);
        if (ApplicationConstants.APPLICATION_LANGUAGE_ID == 1 && this.dialog_type.equals("Fonts_Type")) {
            Typeface tf = ApplicationConstants.SYSTEM_TYPE_FACES.get(position);
            tv_data.setTypeface(tf);
            tv_data.setText(ArabicUtilities.reshape(this.items[position]));
        } else {
            Typeface tf2 = ApplicationConstants.SYSTEM_TYPE_FACES.get(0);
            tv_data.setTypeface(tf2);
            tv_data.setText(this.items[position]);
        }
        RadioButton radioButton = (RadioButton) row_view.findViewById(R.id.radiobutton);
        radioButton.setTag(Integer.valueOf(position));
        if (this.items[position].equals(this.selected_item)) {
            radioButton.setChecked(true);
        }
        radioButton.setOnClickListener(new View.OnClickListener() { // from class: com.arttech.custom.dialogs.CustomSpinnerDialogAdapter.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                ListView lv = (ListView) ((Activity) CustomSpinnerDialogAdapter.this.context).findViewById(R.id.lv_items);
                for (int i = 0; i < lv.getChildCount(); i++) {
                    RadioButton radiobutton = (RadioButton) lv.getChildAt(i).findViewById(R.id.radiobutton);
                    radiobutton.setChecked(false);
                }
                RadioButton rbn = (RadioButton) v;
                rbn.setChecked(true);
                Intent intent = new Intent();
                intent.putExtra(CustomSpinnerDialogExtras.CHECKED_INDEX, v.getTag().toString());
                intent.putExtra(CustomSpinnerDialogExtras.DIALOG_TYPE, CustomSpinnerDialogAdapter.this.dialog_type);
                intent.putExtra(CustomSpinnerDialogExtras.ITEM_POSITION, CustomSpinnerDialogAdapter.this.Item_Position);
                ((Activity) CustomSpinnerDialogAdapter.this.context).setResult(-1, intent);
                ((Activity) CustomSpinnerDialogAdapter.this.context).finish();
            }
        });
        return row_view;
    }
}
