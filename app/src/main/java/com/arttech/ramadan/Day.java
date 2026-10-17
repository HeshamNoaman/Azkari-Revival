package com.arttech.ramadan;

import com.dot.constants.ApplicationConstants;

/* JADX INFO: loaded from: classes.dex */
public class Day {
    private int ID;
    private String NameAr;
    private String NameEn;

    public void setNameAr(String nameAr) {
        this.NameAr = nameAr;
    }

    public void setNameEn(String nameEn) {
        this.NameEn = nameEn;
    }

    public int getID() {
        return this.ID;
    }

    public void setID(int iD) {
        this.ID = iD;
    }

    public String getName() {
        return ApplicationConstants.APPLICATION_LANGUAGE_ID == 1 ? this.NameAr : this.NameEn;
    }
}
