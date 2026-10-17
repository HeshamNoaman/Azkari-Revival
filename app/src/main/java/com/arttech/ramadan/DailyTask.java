package com.arttech.ramadan;

/* JADX INFO: loaded from: classes.dex */
public class DailyTask {
    private int CalculatedWeight;
    private int DailyTaskID;
    private int Day;
    private int TaskID;
    private int WeightAnswer;
    private Task task;

    public int getDailyTaskID() {
        return this.DailyTaskID;
    }

    public void setDailyTaskID(int dailyTaskID) {
        this.DailyTaskID = dailyTaskID;
    }

    public int getTaskID() {
        return this.TaskID;
    }

    public void setTaskID(int taskID) {
        this.TaskID = taskID;
    }

    public int getWeightAnswer() {
        return this.WeightAnswer;
    }

    public void setWeightAnswer(int weightAnswer) {
        this.WeightAnswer = weightAnswer;
    }

    public int getCalculatedWeight() {
        return this.CalculatedWeight;
    }

    public void setCalculatedWeight(int calculatedWeight) {
        this.CalculatedWeight = calculatedWeight;
    }

    public int getDay() {
        return this.Day;
    }

    public void setDay(int day) {
        this.Day = day;
    }

    public Task getTask() {
        return this.task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
