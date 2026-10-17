package com.buzzbox.mob.android.scheduler.cron;

import java.util.Date;
import java.util.GregorianCalendar;

/* JADX INFO: loaded from: classes.dex */
public class Predictor {
    private static final long MILLISECS_PER_MINUTE = 60000L;
    private SchedulingPattern schedulingPattern;
    private long time;

    public Predictor(String schedulingPattern, long start) throws InvalidPatternException {
        this.schedulingPattern = new SchedulingPattern(schedulingPattern);
        this.time = (start / MILLISECS_PER_MINUTE) * 1000 * 60;
    }

    public Predictor(String schedulingPattern, Date start) throws InvalidPatternException {
        this(schedulingPattern, start.getTime());
    }

    public Predictor(String schedulingPattern) throws InvalidPatternException {
        this(schedulingPattern, System.currentTimeMillis());
    }

    public Predictor(SchedulingPattern schedulingPattern, long start) {
        this.schedulingPattern = schedulingPattern;
        this.time = (start / MILLISECS_PER_MINUTE) * 1000 * 60;
    }

    public Predictor(SchedulingPattern schedulingPattern, Date start) {
        this(schedulingPattern, start.getTime());
    }

    public Predictor(SchedulingPattern schedulingPattern) {
        this(schedulingPattern, System.currentTimeMillis());
    }

    /* JADX WARN: Code duplicated, block: B:68:0x01ed A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:78:0x0146 A[SYNTHETIC] */
    public synchronized long nextMatchingTime() {
        long j;
        int oldMonth;
        this.time += MILLISECS_PER_MINUTE;
        if (this.schedulingPattern.match(this.time)) {
            j = this.time;
        } else {
            int size = this.schedulingPattern.matcherSize;
            long[] times = new long[size];
            for (int k = 0; k < size; k++) {
                GregorianCalendar c = new GregorianCalendar();
                c.setTimeInMillis(this.time);
                int minute = c.get(12);
                int hour = c.get(11);
                int dayOfMonth = c.get(5);
                int month = c.get(2);
                int year = c.get(1);
                ValueMatcher minuteMatcher = this.schedulingPattern.minuteMatchers.get(k);
                ValueMatcher hourMatcher = this.schedulingPattern.hourMatchers.get(k);
                ValueMatcher dayOfMonthMatcher = this.schedulingPattern.dayOfMonthMatchers.get(k);
                ValueMatcher dayOfWeekMatcher = this.schedulingPattern.dayOfWeekMatchers.get(k);
                ValueMatcher monthMatcher = this.schedulingPattern.monthMatchers.get(k);
                while (true) {
                    if (!minuteMatcher.match(minute)) {
                        minute++;
                        if (minute > 59) {
                            minute = 0;
                            hour++;
                        }
                    } else {
                        if (hour > 23) {
                            hour = 0;
                            dayOfMonth++;
                        }
                        if (!hourMatcher.match(hour)) {
                            hour++;
                            minute = 0;
                        } else {
                            if (dayOfMonth > 31) {
                                dayOfMonth = 1;
                                month++;
                            }
                            if (month > 11) {
                                month = 0;
                                year++;
                            }
                            if (dayOfMonthMatcher instanceof DayOfMonthValueMatcher) {
                                DayOfMonthValueMatcher aux = (DayOfMonthValueMatcher) dayOfMonthMatcher;
                                if (!aux.match(dayOfMonth, month + 1, c.isLeapYear(year))) {
                                    dayOfMonth++;
                                    hour = 0;
                                    minute = 0;
                                } else if (!monthMatcher.match(month + 1)) {
                                    month++;
                                    dayOfMonth = 1;
                                    hour = 0;
                                    minute = 0;
                                } else {
                                    c = new GregorianCalendar();
                                    c.set(12, minute);
                                    c.set(11, hour);
                                    c.set(5, dayOfMonth);
                                    c.set(2, month);
                                    c.set(1, year);
                                    int oldDayOfMonth = dayOfMonth;
                                    oldMonth = month;
                                    int oldYear = year;
                                    dayOfMonth = c.get(5);
                                    month = c.get(2);
                                    year = c.get(1);
                                    if (month == oldMonth && dayOfMonth == oldDayOfMonth && year == oldYear) {
                                        int dayOfWeek = c.get(7);
                                        if (dayOfWeekMatcher.match(dayOfWeek - 1)) {
                                            break;
                                        }
                                        dayOfMonth++;
                                        hour = 0;
                                        minute = 0;
                                        if (dayOfMonth > 31) {
                                            dayOfMonth = 1;
                                            month++;
                                            if (month > 11) {
                                                month = 0;
                                                year++;
                                            }
                                        }
                                    }
                                }
                            } else if (!dayOfMonthMatcher.match(dayOfMonth)) {
                                dayOfMonth++;
                                hour = 0;
                                minute = 0;
                            } else if (!monthMatcher.match(month + 1)) {
                                month++;
                                dayOfMonth = 1;
                                hour = 0;
                                minute = 0;
                            } else {
                                c = new GregorianCalendar();
                                c.set(12, minute);
                                c.set(11, hour);
                                c.set(5, dayOfMonth);
                                c.set(2, month);
                                c.set(1, year);
                                int oldDayOfMonth2 = dayOfMonth;
                                oldMonth = month;
                                int oldYear2 = year;
                                dayOfMonth = c.get(5);
                                month = c.get(2);
                                year = c.get(1);
                                if (month == oldMonth && dayOfMonth == oldDayOfMonth2 && year == oldYear2) {
                                    int dayOfWeek = c.get(7);
                                    if (dayOfWeekMatcher.match(dayOfWeek - 1)) {
                                        break;
                                    }
                                    dayOfMonth++;
                                    hour = 0;
                                    minute = 0;
                                    if (dayOfMonth > 31) {
                                        dayOfMonth = 1;
                                        month++;
                                        if (month > 11) {
                                            month = 0;
                                            year++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                times[k] = (c.getTimeInMillis() / MILLISECS_PER_MINUTE) * 1000 * 60;
            }
            long min = Long.MAX_VALUE;
            for (int k2 = 0; k2 < size; k2++) {
                if (times[k2] < min) {
                    min = times[k2];
                }
            }
            this.time = min;
            j = this.time;
        }
        return j;
    }

    public synchronized Date nextMatchingDate() {
        return new Date(nextMatchingTime());
    }
}
