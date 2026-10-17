package com.dot.azkari;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.database.Cursor;
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
import com.dot.adapter.database.Azkar_DB_Adapter;
import com.dot.arabic.ArabicUtilities;
import com.dot.constants.ApplicationConstants;

/* JADX INFO: loaded from: classes.dex */
public class LogActivity extends Activity {
    private static final int RESET_COUNTERS_DIALOG_ID = 1;
    int catId;
    private View selectedItem;

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scores);
        ListView lsvAzkar = (ListView) findViewById(R.id.lsvAzkar);
        Azkar_DB_Adapter.Init(this);
        Azkar_DB_Adapter.open();
        Cursor cr = Azkar_DB_Adapter.selectCategoryLog();
        LogItemCursorAdapter log = new LogItemCursorAdapter(this, cr);
        lsvAzkar.setAdapter((ListAdapter) log);
        lsvAzkar.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.dot.azkari.LogActivity.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> parent, View rowView, int position, long id) {
                if (rowView.getTag() != null) {
                    LogActivity.this.catId = Integer.parseInt(rowView.getTag().toString());
                    LogActivity.this.selectedItem = rowView;
                    LogActivity.this.showDialog(1);
                }
            }
        });
    }

    @Override // android.app.Activity
    protected Dialog onCreateDialog(int id) {
        TableRow.LayoutParams pars = new TableRow.LayoutParams(-2, -2);
        pars.weight = 50.0f;
        pars.gravity = 17;
        switch (id) {
            case 1:
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService("layout_inflater");
                View resetDialogLayout = layoutInflater.inflate(R.layout.delete_confirm_dialog, (ViewGroup) findViewById(R.id.deleteConfirmRoot));
                AlertDialog.Builder resetDialogBuilder = new AlertDialog.Builder(this);
                resetDialogBuilder.setView(resetDialogLayout);
                TextView tvDialogTitle = new TextView(this);
                tvDialogTitle.setTextSize(25.0f);
                tvDialogTitle.setGravity(17);
                Utils.setText(tvDialogTitle, getString(R.string.rseset_dialog_title));
                tvDialogTitle.setGravity(17);
                resetDialogBuilder.setCustomTitle(tvDialogTitle);
                TextView tvResetText = (TextView) resetDialogLayout.findViewById(R.id.tvConfirm);
                tvResetText.setGravity(5);
                Utils.setText(tvResetText, getString(R.string.reset_dialog_body));
                tvResetText.setLines(2);
                Button okBtn = new Button(this);
                Utils.setText(okBtn, getString(R.string.ok));
                okBtn.setOnClickListener(new View.OnClickListener() { // from class: com.dot.azkari.LogActivity.2
                    @Override // android.view.View.OnClickListener
                    public void onClick(View arg0) {
                        TextView tvCount = (TextView) LogActivity.this.selectedItem.findViewById(R.id.tvCount);
                        TextView tvPeriod = (TextView) LogActivity.this.selectedItem.findViewById(R.id.tvPeriod);
                        tvCount.setTag(true);
                        tvCount.setText("0 ");
                        tvPeriod.setText("00:00:00 ");
                        Azkar_DB_Adapter.reset_Categories_TimeAndCount(LogActivity.this.catId);
                        LogActivity.this.removeDialog(1);
                    }
                });
                Button cancelBtn = new Button(this);
                Utils.setText(cancelBtn, getString(R.string.cancel));
                cancelBtn.setText(ArabicUtilities.reshape(getResources().getString(R.string.cancel)));
                cancelBtn.setOnClickListener(new View.OnClickListener() { // from class: com.dot.azkari.LogActivity.3
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        LogActivity.this.removeDialog(1);
                    }
                });
                TableRow row = (TableRow) resetDialogLayout.findViewById(R.id.tableRow1);
                if (ApplicationConstants.APPLICATION_LANGUAGE_DIRECTION == ApplicationConstants.Direction.LTR) {
                    row.addView(okBtn, pars);
                    row.addView(cancelBtn, pars);
                    tvResetText.setGravity(3);
                    tvDialogTitle.setGravity(17);
                } else {
                    row.addView(cancelBtn, pars);
                    row.addView(okBtn, pars);
                    tvResetText.setGravity(5);
                    tvDialogTitle.setGravity(17);
                }
                return resetDialogBuilder.create();
            default:
                return super.onCreateDialog(id);
        }
    }
}
