package com.arttech.ramadan;

import com.dot.constants.ApplicationConstants;

/* JADX INFO: loaded from: classes.dex */
public class TaskType {
    private int ID;
    private String IconName;
    private String TextAr;
    private String TextEn;

    public void setTextEn(String textEn) {
        this.TextEn = textEn;
    }

    public void setTextAr(String textAr) {
        this.TextAr = textAr;
    }

    public int getID() {
        return this.ID;
    }

    public void setID(int iD) {
        this.ID = iD;
    }

    public String getIconName() {
        return this.IconName;
    }

    public void setIconName(String iconName) {
        this.IconName = iconName;
    }

    public String getText() {
        return ApplicationConstants.APPLICATION_LANGUAGE_ID == 1 ? this.TextAr : this.TextEn;
    }
}
