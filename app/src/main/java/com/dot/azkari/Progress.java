package com.dot.azkari;

/* JADX INFO: loaded from: classes.dex */
public class Progress {
    private int SecondaryValue;
    private int primaryValue;

    public Progress(int progress, int secondaryProgress) {
        this.primaryValue = 0;
        this.SecondaryValue = 0;
        this.primaryValue = progress;
        this.SecondaryValue = secondaryProgress;
    }

    public int getPrimaryValue() {
        return this.primaryValue;
    }

    public void setPrimaryValue(int primaryValue) {
        this.primaryValue = primaryValue;
    }

    public int getSecondaryValue() {
        return this.SecondaryValue;
    }

    public void setSecondaryValue(int secondaryValue) {
        this.SecondaryValue = secondaryValue;
    }
}
