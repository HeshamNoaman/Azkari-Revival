package com.dot.azkari;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.arttech.ramadan.DailyTask;
import com.arttech.ramadan.RamadanPreferences;
import com.arttech.ramadan.TasksDBAdapter;
import com.arttech.ramadan.TasksDetailsAdapter;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class DetailsTasks extends Activity {
    private static String TAG = "DetailsTasks";
    private ListView lv = null;
    private TasksDetailsAdapter mTasksDetailsDBAdapter;

    @Override // android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            super.onActivityResult(requestCode, resultCode, data);
            this.mTasksDetailsDBAdapter.onActivityResult(requestCode, resultCode, data, this.lv);
        }
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.activity_details_tasks);
        this.lv = (ListView) findViewById(R.id.list);
        TasksDBAdapter mTasksDBAdapter = TasksDBAdapter.getInstance(this);
        ArrayList<DailyTask> listTasksTypes = mTasksDBAdapter.getDailyTasks(Integer.parseInt(bundle.getString(RamadanPreferences.ExtrasSelectedType)), Integer.parseInt(bundle.getString(RamadanPreferences.ExtrasSelectedDay)));
        ListView listView = this.lv;
        TasksDetailsAdapter tasksDetailsAdapter = new TasksDetailsAdapter(this, listTasksTypes);
        this.mTasksDetailsDBAdapter = tasksDetailsAdapter;
        listView.setAdapter((ListAdapter) tasksDetailsAdapter);
        TextView tvHeader = (TextView) findViewById(R.id.txv__Header);
        Utils.setText(tvHeader, getString(R.string.taskListItemsTitle));
    }
}
