package com.buzzbox.mob.android.scheduler.cron;

import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
class DayOfMonthValueMatcher extends IntArrayValueMatcher {
    private static final int[] lastDays = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    public DayOfMonthValueMatcher(String str, ArrayList<Integer> integers) {
        super(str, integers);
    }

    public boolean match(int value, int month, boolean isLeapYear) {
        return super.match(value) || (value > 27 && match(32) && isLastDayOfMonth(value, month, isLeapYear));
    }

    public boolean isLastDayOfMonth(int value, int month, boolean isLeapYear) {
        if (isLeapYear && month == 2) {
            return value == 29;
        }
        return value == lastDays[month + (-1)];
    }
}
