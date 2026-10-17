package com.dot.azkari;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.arttech.ramadan.ListDaysAdapter;
import com.arttech.ramadan.RamadanPreferences;
import com.arttech.ramadan.TasksDBAdapter;

/* JADX INFO: loaded from: classes.dex */
public class TasksMaster extends Activity implements AdapterView.OnItemClickListener {
    Button infoBtn;
    ListView lv;

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.activity_tasks_master);
        SharedPreferences shared = getSharedPreferences(RamadanPreferences.AZKARI_PREFERENCES, 0);
        if (!shared.getBoolean(RamadanPreferences.PreferencesRamadanIsHintShown, false)) {
            showDialogInfo();
            shared.edit().putBoolean(RamadanPreferences.PreferencesRamadanIsHintShown, true).commit();
        }
        this.lv = (ListView) findViewById(R.id.ListTasksDays);
        this.lv.setAdapter((ListAdapter) new ListDaysAdapter(this, TasksDBAdapter.getInstance(this).getDays()));
        this.lv.setOnItemClickListener(this);
        this.infoBtn = (Button) findViewById(R.id.Info);
        this.infoBtn.setOnClickListener(new View.OnClickListener() { // from class: com.dot.azkari.TasksMaster.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                TasksMaster.this.showDialogInfo();
            }
        });
        TextView tvHeader = (TextView) findViewById(R.id.txv__Header);
        Utils.setText(tvHeader, getString(R.string.taskDaysListTitle));
    }

    protected void showDialogInfo() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.info_msg);
        alertDialogBuilder.setTitle(R.string.info_title);
        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.info_btn), new DialogInterface.OnClickListener() { // from class: com.dot.azkari.TasksMaster.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        ((ListDaysAdapter) this.lv.getAdapter()).notifyDataSetChanged();
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> arg0, View arg1, int idx, long arg3) {
        Intent i = new Intent(this, (Class<?>) MasterCategories.class);
        i.putExtra(RamadanPreferences.ExtrasSelectedDay, arg1.getTag().toString());
        startActivity(i);
    }
}
