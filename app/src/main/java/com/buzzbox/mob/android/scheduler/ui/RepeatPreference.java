package com.buzzbox.mob.android.scheduler.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.ListPreference;
import android.util.AttributeSet;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import org.xmlpull.v1.XmlPullParser;

/* JADX INFO: loaded from: classes.dex */
public class RepeatPreference extends ListPreference {
    private DaysOfWeek mDaysOfWeek;
    private DaysOfWeek mNewDaysOfWeek;

    public RepeatPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mDaysOfWeek = new DaysOfWeek(0);
        this.mNewDaysOfWeek = new DaysOfWeek(0);
        setPersistent(false);
        String[] weekdays = new DateFormatSymbols().getWeekdays();
        String[] values = {weekdays[2], weekdays[3], weekdays[4], weekdays[5], weekdays[6], weekdays[7], weekdays[1]};
        setEntries(values);
        setEntryValues(values);
    }

    @Override // android.preference.ListPreference, android.preference.DialogPreference
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            this.mDaysOfWeek.set(this.mNewDaysOfWeek);
            setSummary(this.mDaysOfWeek.toString(getContext(), true));
        }
    }

    @Override // android.preference.ListPreference, android.preference.DialogPreference
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        CharSequence[] entries = getEntries();
        builder.setMultiChoiceItems(entries, this.mDaysOfWeek.getBooleanArray(), new DialogInterface.OnMultiChoiceClickListener() { // from class: com.buzzbox.mob.android.scheduler.ui.RepeatPreference.1
            @Override // android.content.DialogInterface.OnMultiChoiceClickListener
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                RepeatPreference.this.mNewDaysOfWeek.set(which, isChecked);
            }
        });
    }

    public void setDaysOfWeek(DaysOfWeek dow) {
        this.mDaysOfWeek.set(dow);
        this.mNewDaysOfWeek.set(dow);
        setSummary(dow.toString(getContext(), true));
    }

    public DaysOfWeek getDaysOfWeek() {
        return this.mDaysOfWeek;
    }

    static final class DaysOfWeek {
        private static int[] DAY_MAP = {2, 3, 4, 5, 6, 7, 1};
        private int mDays;

        DaysOfWeek(int days) {
            this.mDays = days;
        }

        public String toString(Context context, boolean showNever) {
            String[] dayList;
            StringBuilder ret = new StringBuilder();
            if (this.mDays == 0) {
                return showNever ? SchedulerPreferenceActivity.getResourceString(context, "scheduler_preference_activity.daysOfWeek.summary.never", "never") : XmlPullParser.NO_NAMESPACE;
            }
            if (this.mDays == 127) {
                return SchedulerPreferenceActivity.getResourceString(context, "scheduler_preference_activity.daysOfWeek.summary.everyDay", "every day");
            }
            int dayCount = 0;
            for (int days = this.mDays; days > 0; days >>= 1) {
                if ((days & 1) == 1) {
                    dayCount++;
                }
            }
            DateFormatSymbols dfs = new DateFormatSymbols();
            if (dayCount > 1) {
                dayList = dfs.getShortWeekdays();
            } else {
                dayList = dfs.getWeekdays();
            }
            for (int i = 0; i < 7; i++) {
                if ((this.mDays & (1 << i)) != 0) {
                    ret.append(dayList[DAY_MAP[i]]);
                    dayCount--;
                    if (dayCount > 0) {
                        ret.append(",");
                    }
                }
            }
            return ret.toString();
        }

        private boolean isSet(int day) {
            return (this.mDays & (1 << day)) > 0;
        }

        public void set(int day, boolean set) {
            if (set) {
                this.mDays |= 1 << day;
            } else {
                this.mDays &= (1 << day) ^ (-1);
            }
        }

        public void set(DaysOfWeek dow) {
            this.mDays = dow.mDays;
        }

        public int getCoded() {
            return this.mDays;
        }

        public boolean[] getBooleanArray() {
            boolean[] ret = new boolean[7];
            for (int i = 0; i < 7; i++) {
                ret[i] = isSet(i);
            }
            return ret;
        }

        public boolean isRepeatSet() {
            return this.mDays != 0;
        }

        public int getNextAlarm(Calendar c) {
            if (this.mDays == 0) {
                return -1;
            }
            int today = (c.get(7) + 5) % 7;
            int dayCount = 0;
            while (dayCount < 7) {
                int day = (today + dayCount) % 7;
                if (!isSet(day)) {
                    dayCount++;
                } else {
                    return dayCount;
                }
            }
            return dayCount;
        }
    }
}
