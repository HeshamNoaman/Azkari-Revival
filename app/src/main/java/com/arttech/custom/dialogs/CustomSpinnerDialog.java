package com.arttech.custom.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TableRow;
import android.widget.TextView;
import com.dot.azkari.R;
import com.dot.azkari.Utils;
import com.dot.constants.ApplicationConstants;

/* JADX INFO: loaded from: classes.dex */
public class CustomSpinnerDialog extends Activity {
    protected String Item_Position;
    private String dialog_type;
    private Bundle extras;
    String[] fontOriginalName = ApplicationConstants.SYSTEM_FONTS;
    private String[] items;
    private String language;
    private ListView listview;
    private String selected_item;
    private String title_string;

    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.main);
        initializeVariablesFromExtras();
        setDialogTitle();
        initializeListView();
    }

    private void initializeListView() {
        this.listview.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.arttech.custom.dialogs.CustomSpinnerDialog.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int count = parent.getChildCount();
                for (int i = 0; i < count; i++) {
                    RadioButton radiobutton = (RadioButton) parent.getChildAt(i).findViewById(R.id.radiobutton);
                    radiobutton.setChecked(false);
                }
                RadioButton radiobutton2 = (RadioButton) parent.getChildAt(position).findViewById(R.id.radiobutton);
                radiobutton2.setChecked(true);
                Intent intent = new Intent();
                intent.putExtra(CustomSpinnerDialogExtras.CHECKED_INDEX, String.valueOf(position));
                intent.putExtra(CustomSpinnerDialogExtras.DIALOG_TYPE, CustomSpinnerDialog.this.dialog_type);
                intent.putExtra(CustomSpinnerDialogExtras.ITEM_POSITION, CustomSpinnerDialog.this.Item_Position);
                CustomSpinnerDialog.this.setResult(-1, intent);
                CustomSpinnerDialog.this.finish();
            }
        });
        CustomSpinnerDialogAdapter adapter = new CustomSpinnerDialogAdapter(this, this.items, this.selected_item, this.language, this.dialog_type, this.Item_Position);
        this.listview.setAdapter((ListAdapter) adapter);
    }

    private void initializeVariablesFromExtras() {
        this.extras = getIntent().getExtras();
        this.selected_item = this.extras.getString(CustomSpinnerDialogExtras.SELECTED_ITEM);
        this.items = this.extras.getStringArray(CustomSpinnerDialogExtras.ITEMS);
        this.language = this.extras.getString(CustomSpinnerDialogExtras.LANGUAGE);
        this.title_string = this.extras.getString(CustomSpinnerDialogExtras.TITLE);
        this.dialog_type = this.extras.getString(CustomSpinnerDialogExtras.DIALOG_TYPE);
        this.Item_Position = this.extras.getString(CustomSpinnerDialogExtras.ITEM_POSITION);
        this.listview = (ListView) findViewById(R.id.lv_items);
    }

    private void setDialogTitle() {
        if (this.title_string != null) {
            TableRow tbl_row = (TableRow) findViewById(R.id.tblrow);
            TextView tv_title = new TextView(this);
            Utils.setText(tv_title, this.title_string);
            tv_title.setBackgroundColor(-1);
            tv_title.setGravity(17);
            tv_title.setTextColor(-16777216);
            tv_title.setTextSize(20.0f);
            tbl_row.addView(tv_title);
        }
    }
}
