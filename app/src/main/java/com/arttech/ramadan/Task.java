package com.arttech.ramadan;

import com.dot.constants.ApplicationConstants;
import org.xmlpull.v1.XmlPullParser;

/* JADX INFO: loaded from: classes.dex */
public class Task {
    private Boolean IsNumeric;
    private String OptionAr = XmlPullParser.NO_NAMESPACE;
    private String OptionEn = XmlPullParser.NO_NAMESPACE;
    private int TaskID;
    private String TextAr;
    private String TextEn;
    private int TypeID;
    private ViewType ViewType;
    private int Weight;

    public enum ViewType {
        CheckBox,
        Numeric,
        Spinner,
        Master;

        /* JADX INFO: renamed from: values, reason: to resolve conflict with enum method */
        public static ViewType[] valuesCustom() {
            ViewType[] viewTypeArrValuesCustom = values();
            int length = viewTypeArrValuesCustom.length;
            ViewType[] viewTypeArr = new ViewType[length];
            System.arraycopy(viewTypeArrValuesCustom, 0, viewTypeArr, 0, length);
            return viewTypeArr;
        }
    }

    public int getTaskID() {
        return this.TaskID;
    }

    public void setTaskID(int taskID) {
        this.TaskID = taskID;
    }

    public int getTypeID() {
        return this.TypeID;
    }

    public void setTypeID(int typeID) {
        this.TypeID = typeID;
    }

    public int getWeight() {
        return this.Weight;
    }

    public void setWeight(int weight) {
        this.Weight = weight;
    }

    public String getText() {
        return ApplicationConstants.APPLICATION_LANGUAGE_ID == 1 ? this.TextAr : this.TextEn;
    }

    public void setTextAr(String textAr) {
        this.TextAr = textAr;
    }

    public void setTextEn(String textEn) {
        this.TextEn = textEn;
    }

    public Boolean getIsNumeric() {
        return this.IsNumeric;
    }

    private void setViewType() {
        if (this.IsNumeric.booleanValue()) {
            this.ViewType = ViewType.Numeric;
        } else if (!this.IsNumeric.booleanValue() && this.OptionAr.length() == 0 && this.OptionEn.length() == 0) {
            this.ViewType = ViewType.CheckBox;
        } else {
            this.ViewType = ViewType.Spinner;
        }
    }

    public void setIsNumeric(Boolean isNumeric) {
        this.IsNumeric = isNumeric;
        setViewType();
    }

    public void setOptionEn(String optionEn) {
        if (optionEn == null) {
            optionEn = XmlPullParser.NO_NAMESPACE;
        }
        this.OptionEn = optionEn;
        setViewType();
    }

    public String[] getOption() {
        if (ApplicationConstants.APPLICATION_LANGUAGE_ID == 1) {
            return this.OptionAr.length() != 0 ? this.OptionAr.split(",") : new String[0];
        }
        return this.OptionEn.length() != 0 ? this.OptionEn.split(",") : new String[0];
    }

    public void setOptionAr(String optionAr) {
        if (optionAr == null) {
            optionAr = XmlPullParser.NO_NAMESPACE;
        }
        this.OptionAr = optionAr;
        setViewType();
    }

    public ViewType getViewType() {
        return this.ViewType;
    }
}
