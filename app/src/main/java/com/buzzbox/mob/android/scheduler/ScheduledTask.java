package com.buzzbox.mob.android.scheduler;

/* JADX INFO: loaded from: classes.dex */
public class ScheduledTask {
    public String cron;
    public boolean deliverAsapOnDelay;
    public String taskClassName;
    public boolean enabled = true;
    public boolean paused = false;
    int autoPauseHours = 0;

    public ScheduledTask(String taskClassName, String schedulingPatternString, boolean deliverAsapOnDelay) {
        this.taskClassName = taskClassName;
        this.cron = schedulingPatternString;
        this.deliverAsapOnDelay = deliverAsapOnDelay;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public int getAutoPauseHours() {
        return this.autoPauseHours;
    }

    public void setAutoPauseHours(int autoPauseHours) {
        this.autoPauseHours = autoPauseHours;
    }
}
