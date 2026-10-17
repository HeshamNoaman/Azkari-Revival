package com.arttech.ramadan;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class TasksDBAdapter extends SQLiteOpenHelper {
    private static final String DB_NAME = "RamadanTasks.db";
    private static final String DB_PATH = "/data/data/com.dot.azkari/databases/";
    private static TasksDBAdapter mTasksDBAdapter = null;
    private final String TAG;

    public void copyDataBase(Context Ctx) {
        File dbFile = Ctx.getDatabasePath(DB_NAME);
        if (!dbFile.exists()) {
            File parentDir = dbFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            try (InputStream myInput = Ctx.getAssets().open(DB_NAME);
                 OutputStream myOutput = new FileOutputStream(dbFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }
                myOutput.flush();
                Log.i("Database", "New database has been copied to device!");
            } catch (IOException e) {
                Log.e("Database", "Failed to copy database: " + e.getMessage(), e);
            }
        }
    }

    public TasksDBAdapter(Context context) {
        super(context, DB_NAME, (SQLiteDatabase.CursorFactory) null, 1);
        this.TAG = "Tasks Db Adapter";
    }

    public static synchronized TasksDBAdapter getInstance(Context ctx) {
        if (mTasksDBAdapter == null) {
            mTasksDBAdapter = new TasksDBAdapter(ctx.getApplicationContext());
            mTasksDBAdapter.copyDataBase(ctx.getApplicationContext());
        }
        return mTasksDBAdapter;
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onCreate(SQLiteDatabase db) {
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public synchronized SQLiteDatabase getWritableDatabase() {
        return super.getWritableDatabase();
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public synchronized SQLiteDatabase getReadableDatabase() {
        return super.getReadableDatabase();
    }

    public ArrayList<TaskType> getTaskTypes() {
        ArrayList<TaskType> list_items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor mCur = null;
        try {
            try {
                mCur = db.rawQuery("SELECT TypeID,TextAr,TextEn,Icon  FROM TaskTypes", null);
                if (mCur.moveToFirst()) {
                    do {
                        int TypeID = Integer.parseInt(String.valueOf(mCur.getInt(mCur.getColumnIndex("TypeID"))));
                        String TextAr = mCur.getString(mCur.getColumnIndex("TextAr"));
                        String TextEn = mCur.getString(mCur.getColumnIndex("TextEn"));
                        String Icon = mCur.getString(mCur.getColumnIndex("Icon"));
                        TaskType tType = new TaskType();
                        tType.setID(TypeID);
                        tType.setTextAr(TextAr);
                        tType.setTextEn(TextEn);
                        tType.setIconName(Icon);
                        list_items.add(tType);
                    } while (mCur.moveToNext());
                }
            } catch (Exception e) {
                Log.e("Tasks Db Adapter", "Table Not Found");
            }
            return list_items;
        } finally {
            if (mCur != null) {
                mCur.close();
            }
            db.close();
            close();
        }
    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> list_items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor mCur = null;
        try {
            try {
                mCur = db.rawQuery("SELECT TypeID,TextAr,TextEn,isNumeric,TaskID ,Weight,OptionAr,OptionEn  FROM Tasks   ", null);
                if (mCur.moveToFirst()) {
                    do {
                        int TypeID = Integer.parseInt(String.valueOf(mCur.getInt(mCur.getColumnIndex("TypeID"))));
                        int TaskID = Integer.parseInt(String.valueOf(mCur.getInt(mCur.getColumnIndex("TaskID"))));
                        int Weight = Integer.parseInt(String.valueOf(mCur.getInt(mCur.getColumnIndex("Weight"))));
                        String TextAr = mCur.getString(mCur.getColumnIndex("TextAr"));
                        String TextEn = mCur.getString(mCur.getColumnIndex("TextEn"));
                        String OptionAr = mCur.getString(mCur.getColumnIndex("OptionAr"));
                        String OptionEn = mCur.getString(mCur.getColumnIndex("OptionEn"));
                        Boolean isNumeric = Boolean.valueOf(mCur.getInt(mCur.getColumnIndex("isNumeric")) != 0);
                        Task task = new Task();
                        task.setTaskID(TaskID);
                        task.setTypeID(TypeID);
                        task.setTextAr(TextAr);
                        task.setTextEn(TextEn);
                        task.setIsNumeric(isNumeric);
                        task.setWeight(Weight);
                        task.setOptionAr(OptionAr);
                        task.setOptionEn(OptionEn);
                        list_items.add(task);
                    } while (mCur.moveToNext());
                }
            } catch (Exception e) {
                Log.e("Tasks Db Adapter", "Table Not Found");
            }
            return list_items;
        } finally {
            if (mCur != null) {
                mCur.close();
            }
            db.close();
            close();
        }
    }

    public ArrayList<Task> getTasks(int TypeID) {
        ArrayList<Task> list_items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT TypeID,TextAr,TextEn,isNumeric,TaskID ,Weight,OptionAr,OptionEn  FROM Tasks Where TypeID =" + TypeID;
        Cursor mCur = null;
        try {
            try {
                mCur = db.rawQuery(sql, null);
                if (mCur.moveToFirst()) {
                    do {
                        int TypeID2 = Integer.parseInt(String.valueOf(mCur.getInt(mCur.getColumnIndex("TypeID"))));
                        int TaskID = Integer.parseInt(String.valueOf(mCur.getInt(mCur.getColumnIndex("TaskID"))));
                        int Weight = Integer.parseInt(String.valueOf(mCur.getInt(mCur.getColumnIndex("Weight"))));
                        String TextAr = mCur.getString(mCur.getColumnIndex("TextAr"));
                        String TextEn = mCur.getString(mCur.getColumnIndex("TextEn"));
                        String OptionAr = mCur.getString(mCur.getColumnIndex("OptionAr"));
                        String OptionEn = mCur.getString(mCur.getColumnIndex("OptionEn"));
                        Boolean isNumeric = Boolean.valueOf(mCur.getInt(mCur.getColumnIndex("isNumeric")) != 0);
                        Task task = new Task();
                        task.setTaskID(TaskID);
                        task.setTypeID(TypeID2);
                        task.setTextAr(TextAr);
                        task.setTextEn(TextEn);
                        task.setIsNumeric(isNumeric);
                        task.setWeight(Weight);
                        task.setOptionAr(OptionAr);
                        task.setOptionEn(OptionEn);
                        list_items.add(task);
                    } while (mCur.moveToNext());
                }
            } catch (Exception e) {
                Log.e("Tasks Db Adapter", "Table Not Found");
            }
            return list_items;
        } finally {
            if (mCur != null) {
                mCur.close();
            }
            db.close();
            close();
        }
    }

    public ArrayList<DailyTask> getDailyTasks(int TypeID, int Day) {
        ArrayList<DailyTask> list_items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT TypeID,TextAr,TextEn,isNumeric,tasks.TaskID ,Weight,OptionAr,OptionEn,Day,WeightAnswer,DailyTaskID,CalculatedWeight  FROM Tasks  inner join dailytasksprogress on tasks.taskid = dailytasksprogress.taskid   Where day  = " + Day + " and Tasks.TypeID =" + TypeID;
        Cursor mCur = null;
        try {
            try {
                mCur = db.rawQuery(sql, null);
                if (mCur.moveToFirst()) {
                    do {
                        int TypeID2 = Integer.parseInt(String.valueOf(mCur.getInt(mCur.getColumnIndex("TypeID"))));
                        int TaskID = Integer.parseInt(String.valueOf(mCur.getInt(mCur.getColumnIndex("TaskID"))));
                        int Weight = Integer.parseInt(String.valueOf(mCur.getInt(mCur.getColumnIndex("Weight"))));
                        String TextAr = mCur.getString(mCur.getColumnIndex("TextAr"));
                        String TextEn = mCur.getString(mCur.getColumnIndex("TextEn"));
                        String OptionAr = mCur.getString(mCur.getColumnIndex("OptionAr"));
                        String OptionEn = mCur.getString(mCur.getColumnIndex("OptionEn"));
                        Boolean isNumeric = Boolean.valueOf(mCur.getInt(mCur.getColumnIndex("isNumeric")) != 0);
                        int day = mCur.getInt(mCur.getColumnIndex("Day"));
                        int answerweight = mCur.getInt(mCur.getColumnIndex("WeightAnswer"));
                        int CalculatedWeight = mCur.getInt(mCur.getColumnIndex("CalculatedWeight"));
                        int DailyTaskID = mCur.getInt(mCur.getColumnIndex("DailyTaskID"));
                        Task task = new Task();
                        task.setTaskID(TaskID);
                        task.setTypeID(TypeID2);
                        task.setTextAr(TextAr);
                        task.setTextEn(TextEn);
                        task.setIsNumeric(isNumeric);
                        task.setWeight(Weight);
                        task.setOptionAr(OptionAr);
                        task.setOptionEn(OptionEn);
                        DailyTask dTask = new DailyTask();
                        dTask.setDailyTaskID(DailyTaskID);
                        dTask.setCalculatedWeight(CalculatedWeight);
                        dTask.setWeightAnswer(answerweight);
                        dTask.setTask(task);
                        dTask.setDay(day);
                        list_items.add(dTask);
                    } while (mCur.moveToNext());
                }
            } catch (Exception e) {
                Log.e("Tasks Db Adapter", "Table Not Found");
            }
            return list_items;
        } finally {
            if (mCur != null) {
                mCur.close();
            }
            db.close();
            close();
        }
    }

    public void InsertRows(int day) {
        Day d = new Day();
        d.setID(day);
        if (0 == 0) {
            ArrayList<Task> list = getTasks();
            SQLiteDatabase db = getReadableDatabase();
            for (Task task : list) {
                StringBuilder query = new StringBuilder("insert into DailyTasksProgress  (day,TaskID,WeightAnswer,CalculatedWeight) values ( " + day + " , " + task.getTaskID() + " ,0,0)");
                db.execSQL(query.toString());
            }
            close();
        }
    }

    public void UpdateDailyTask(DailyTask dailyTask) {
        SQLiteDatabase db = getReadableDatabase();
        int cal = dailyTask.getTask().getWeight() * dailyTask.getWeightAnswer();
        String query = "Update DailyTasksProgress set WeightAnswer=" + dailyTask.getWeightAnswer() + ",CalculatedWeight=" + cal + " where DailyTaskID=" + dailyTask.getDailyTaskID();
        db.execSQL(query.toString());
        close();
    }

    public int GetDayProgress(Day master) {
        int sum = 0;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("Select Sum (CalculatedWeight) from DailyTasksProgress where day =" + master.getID(), null);
        if (cursor.moveToFirst()) {
            sum = cursor.getInt(0);
        }
        close();
        return sum;
    }

    public int GetNumOfRows(Day master) {
        int sum = 0;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("Select Count (*) from DailyTasksProgress where day =" + master.getID(), null);
        if (cursor.moveToFirst()) {
            sum = cursor.getInt(0);
        }
        close();
        return sum;
    }

    public ArrayList<Day> getDays() {
        ArrayList<Day> list_items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor mCur = null;
        try {
            try {
                mCur = db.rawQuery("SELECT NameAr,NameEn,ID  FROM Days  ", null);
                if (mCur.moveToFirst()) {
                    do {
                        int ID = Integer.parseInt(String.valueOf(mCur.getInt(mCur.getColumnIndex("ID"))));
                        String NameAr = mCur.getString(mCur.getColumnIndex("NameAr"));
                        String NameEn = mCur.getString(mCur.getColumnIndex("NameEn"));
                        Day day = new Day();
                        day.setID(ID);
                        day.setNameAr(NameAr);
                        day.setNameEn(NameEn);
                        list_items.add(day);
                    } while (mCur.moveToNext());
                }
            } catch (Exception e) {
                Log.e("Tasks Db Adapter", "Table Not Found");
            }
            return list_items;
        } finally {
            if (mCur != null) {
                mCur.close();
            }
            db.close();
            close();
        }
    }
}
