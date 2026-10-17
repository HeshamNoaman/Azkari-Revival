package com.arttech.ramadan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.arttech.custom.dialogs.CustomSpinnerDialog;
import com.arttech.custom.dialogs.CustomSpinnerDialogExtras;
import com.dot.azkari.DetailsTasks;
import com.dot.azkari.R;
import com.dot.azkari.Utils;
import com.dot.constants.ApplicationConstants;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class TasksDetailsAdapter extends BaseAdapter {
    public static final int REQUESTCODE = 1;
    public Context ctx;
    public ArrayList<DailyTask> list;

    public TasksDetailsAdapter(DetailsTasks detailsTasks, ArrayList<DailyTask> listTasksTypes) {
        this.list = listTasksTypes;
        this.ctx = detailsTasks;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.list.size();
    }

    @Override // android.widget.Adapter
    public Object getItem(int arg0) {
        return null;
    }

    @Override // android.widget.Adapter
    public long getItemId(int arg0) {
        return 0L;
    }

    @Override // android.widget.Adapter
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater mInflater = LayoutInflater.from(this.ctx);
        DailyTask dTask = this.list.get(position);
        Task task = dTask.getTask();
        if (view == null) {
            if (task.getViewType() == Task.ViewType.Numeric) {
                view = mInflater.inflate(R.layout.tasksnumericitem_ar, parent, false);
                view.setTag(Task.ViewType.Numeric);
            } else if (task.getViewType() == Task.ViewType.CheckBox) {
                view = mInflater.inflate(R.layout.tasks_checkbox_item_ar, parent, false);
                view.setTag(Task.ViewType.CheckBox);
            } else if (task.getViewType() == Task.ViewType.Spinner) {
                view = mInflater.inflate(R.layout.tasksdropdownitem_ar, parent, false);
                view.setTag(Task.ViewType.Spinner);
            }
        } else if (task.getViewType() == Task.ViewType.Numeric && !view.getTag().equals(Task.ViewType.Numeric)) {
            view = mInflater.inflate(R.layout.tasksnumericitem_ar, parent, false);
            view.setTag(Task.ViewType.Numeric);
        } else if (task.getViewType() == Task.ViewType.CheckBox && !view.getTag().equals(Task.ViewType.CheckBox)) {
            view = mInflater.inflate(R.layout.tasks_checkbox_item_ar, parent, false);
            view.setTag(Task.ViewType.CheckBox);
        } else if (task.getViewType() == Task.ViewType.Spinner && !view.getTag().equals(Task.ViewType.Spinner)) {
            view = mInflater.inflate(R.layout.tasksdropdownitem_ar, parent, false);
            view.setTag(Task.ViewType.Spinner);
        } else {
            Log.i("ListTasks", "  reused");
        }
        if (task.getViewType() == Task.ViewType.Numeric) {
            EditText t2v = (EditText) view.findViewById(R.id.etValue);
            t2v.setText(task.getText());
        } else if (task.getViewType() == Task.ViewType.CheckBox) {
            CheckBox tsv = (CheckBox) view.findViewById(R.id.ChkIsDone);
            tsv.setTag(dTask);
            TextView taskNameTextView = (TextView) view.findViewById(R.id.tvTaskName);
            Utils.setText(taskNameTextView, task.getText());
            tsv.setOnClickListener(new View.OnClickListener() { // from class: com.arttech.ramadan.TasksDetailsAdapter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    DailyTask dTask2 = (DailyTask) ((CheckBox) v).getTag();
                    if (((CheckBox) v).isChecked()) {
                        dTask2.setWeightAnswer(1);
                        dTask2.setCalculatedWeight(dTask2.getTask().getWeight());
                        TasksDBAdapter.getInstance(TasksDetailsAdapter.this.ctx).UpdateDailyTask(dTask2);
                    } else {
                        dTask2.setWeightAnswer(0);
                        dTask2.setCalculatedWeight(0);
                        TasksDBAdapter.getInstance(TasksDetailsAdapter.this.ctx).UpdateDailyTask(dTask2);
                    }
                }
            });
            tsv.setChecked(dTask.getWeightAnswer() != 0);
        } else if (task.getViewType() == Task.ViewType.Spinner) {
            Button spin = (Button) view.findViewById(R.id.spinnerOptions);
            spin.setBackgroundResource(android.R.drawable.btn_dropdown);
            spin.setTextSize(13.0f);
            TextView tv = (TextView) view.findViewById(R.id.tvTaskName);
            Utils.setText(tv, dTask.getTask().getText());
            Utils.setText(spin, dTask.getTask().getOption()[dTask.getWeightAnswer()]);
            spin.setOnClickListener(new View.OnClickListener() { // from class: com.arttech.ramadan.TasksDetailsAdapter.2
                @Override // android.view.View.OnClickListener
                public void onClick(View arg0) {
                    String Lang;
                    DailyTask dTask2 = TasksDetailsAdapter.this.list.get(position);
                    Intent intent = new Intent(TasksDetailsAdapter.this.ctx.getApplicationContext(), (Class<?>) CustomSpinnerDialog.class);
                    intent.putExtra(CustomSpinnerDialogExtras.ITEMS, dTask2.getTask().getOption());
                    intent.putExtra(CustomSpinnerDialogExtras.SELECTED_ITEM, String.valueOf(dTask2.getTask().getOption()[dTask2.getWeightAnswer()]));
                    if (ApplicationConstants.APPLICATION_LANGUAGE_DIRECTION == ApplicationConstants.Direction.LTR) {
                        Lang = "english";
                    } else {
                        Lang = "arabic";
                    }
                    intent.putExtra(CustomSpinnerDialogExtras.LANGUAGE, Lang);
                    intent.putExtra(CustomSpinnerDialogExtras.DIALOG_TYPE, "Options");
                    intent.putExtra(CustomSpinnerDialogExtras.ITEM_POSITION, String.valueOf(position));
                    ((Activity) TasksDetailsAdapter.this.ctx).startActivityForResult(intent, 1);
                }
            });
        }
        return view;
    }

    private View GetVisibleItemAtPostion(ListView listView, int Position) {
        int firstPosition = listView.getFirstVisiblePosition() - listView.getHeaderViewsCount();
        int wantedChild = Position - firstPosition;
        if (wantedChild < 0 || wantedChild >= listView.getChildCount()) {
            return null;
        }
        return listView.getChildAt(wantedChild);
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data, ListView lv) {
        if (requestCode == 1 && data != null) {
            int index = Integer.parseInt(data.getStringExtra(CustomSpinnerDialogExtras.CHECKED_INDEX));
            int pos = Integer.parseInt(data.getStringExtra(CustomSpinnerDialogExtras.ITEM_POSITION));
            DailyTask dTask = this.list.get(pos);
            dTask.setWeightAnswer(index);
            dTask.setCalculatedWeight(dTask.getTask().getWeight() * index);
            TasksDBAdapter.getInstance(this.ctx).UpdateDailyTask(dTask);
            ((Button) GetVisibleItemAtPostion(lv, pos).findViewById(R.id.spinnerOptions)).setText(dTask.getTask().getOption()[index]);
            return false;
        }
        return false;
    }
}
