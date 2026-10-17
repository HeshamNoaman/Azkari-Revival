package com.dot.azkari;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.arttech.ramadan.ListTasksCategoriesMaster;
import com.arttech.ramadan.RamadanPreferences;
import com.arttech.ramadan.TaskType;
import com.arttech.ramadan.TasksDBAdapter;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class MasterCategories extends Activity {
    ListTasksCategoriesMaster LTCM;
    private String TAG = "Master";
    ListView lv;

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        final Bundle bundle = getIntent().getExtras();
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.activity_master_categories);
        this.lv = (ListView) findViewById(R.id.listTasksMaster);
        TasksDBAdapter mTasksDBAdapter = TasksDBAdapter.getInstance(this);
        ArrayList<TaskType> listTasksTypes = mTasksDBAdapter.getTaskTypes();
        this.lv.setAdapter((ListAdapter) new ListTasksCategoriesMaster(this, listTasksTypes));
        this.lv.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.dot.azkari.MasterCategories.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> arg0, View arg1, int idx, long arg3) {
                Intent i = new Intent(MasterCategories.this, (Class<?>) DetailsTasks.class);
                i.putExtra(RamadanPreferences.ExtrasSelectedDay, bundle.getString(RamadanPreferences.ExtrasSelectedDay));
                i.putExtra(RamadanPreferences.ExtrasSelectedType, MasterCategories.this.lv.getChildAt(idx).getTag().toString());
                Log.i(MasterCategories.this.TAG, MasterCategories.this.lv.getChildAt(idx).getTag().toString());
                MasterCategories.this.startActivity(i);
            }
        });
        TextView tvHeader = (TextView) findViewById(R.id.txv__Header);
        Utils.setText(tvHeader, getString(R.string.taskCatsListTitle));
    }
}
