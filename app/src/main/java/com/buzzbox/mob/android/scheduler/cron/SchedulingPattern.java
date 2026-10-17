package com.buzzbox.mob.android.scheduler.cron;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes.dex */
public class SchedulingPattern {
    private String asString;
    private static final ValueParser MINUTE_VALUE_PARSER = new MinuteValueParser();
    private static final ValueParser HOUR_VALUE_PARSER = new HourValueParser();
    private static final ValueParser DAY_OF_MONTH_VALUE_PARSER = new DayOfMonthValueParser();
    private static final ValueParser MONTH_VALUE_PARSER = new MonthValueParser();
    private static final ValueParser DAY_OF_WEEK_VALUE_PARSER = new DayOfWeekValueParser();
    public ArrayList<ValueMatcher> minuteMatchers = new ArrayList<>();
    public ArrayList<ValueMatcher> hourMatchers = new ArrayList<>();
    public ArrayList<ValueMatcher> dayOfMonthMatchers = new ArrayList<>();
    public ArrayList<ValueMatcher> monthMatchers = new ArrayList<>();
    public ArrayList<ValueMatcher> dayOfWeekMatchers = new ArrayList<>();
    protected int matcherSize = 0;

    private interface ValueParser {
        int getMaxValue();

        int getMinValue();

        int parse(String str) throws Exception;
    }

    public static boolean validate(String schedulingPattern) {
        try {
            new SchedulingPattern(schedulingPattern);
            return true;
        } catch (InvalidPatternException e) {
            return false;
        }
    }

    public SchedulingPattern(String pattern) throws InvalidPatternException {
        this.asString = pattern;
        StringTokenizer st1 = new StringTokenizer(pattern, "|");
        if (st1.countTokens() < 1) {
            throw new InvalidPatternException("invalid pattern: \"" + pattern + "\"");
        }
        while (st1.hasMoreTokens()) {
            String localPattern = st1.nextToken();
            StringTokenizer st2 = new StringTokenizer(localPattern, " \t");
            if (st2.countTokens() != 5) {
                throw new InvalidPatternException("invalid pattern: \"" + localPattern + "\"");
            }
            try {
                this.minuteMatchers.add(buildValueMatcher(st2.nextToken(), MINUTE_VALUE_PARSER));
                try {
                    this.hourMatchers.add(buildValueMatcher(st2.nextToken(), HOUR_VALUE_PARSER));
                    try {
                        this.dayOfMonthMatchers.add(buildValueMatcher(st2.nextToken(), DAY_OF_MONTH_VALUE_PARSER));
                        try {
                            this.monthMatchers.add(buildValueMatcher(st2.nextToken(), MONTH_VALUE_PARSER));
                            try {
                                this.dayOfWeekMatchers.add(buildValueMatcher(st2.nextToken(), DAY_OF_WEEK_VALUE_PARSER));
                                this.matcherSize++;
                            } catch (Exception e) {
                                throw new InvalidPatternException("invalid pattern \"" + localPattern + "\". Error parsing days of week field: " + e.getMessage() + ".");
                            }
                        } catch (Exception e2) {
                            throw new InvalidPatternException("invalid pattern \"" + localPattern + "\". Error parsing months field: " + e2.getMessage() + ".");
                        }
                    } catch (Exception e3) {
                        throw new InvalidPatternException("invalid pattern \"" + localPattern + "\". Error parsing days of month field: " + e3.getMessage() + ".");
                    }
                } catch (Exception e4) {
                    throw new InvalidPatternException("invalid pattern \"" + localPattern + "\". Error parsing hours field: " + e4.getMessage() + ".");
                }
            } catch (Exception e5) {
                throw new InvalidPatternException("invalid pattern \"" + localPattern + "\". Error parsing minutes field: " + e5.getMessage() + ".");
            }
        }
    }

    private ValueMatcher buildValueMatcher(String str, ValueParser parser) throws Exception {
        if (str.length() == 1 && str.equals("*")) {
            return new AlwaysTrueValueMatcher();
        }
        ArrayList values = new ArrayList();
        StringTokenizer st = new StringTokenizer(str, ",");
        while (st.hasMoreTokens()) {
            String element = st.nextToken();
            try {
                ArrayList local = parseListElement(element, parser);
                for (Object value : local) {
                    if (!values.contains(value)) {
                        values.add(value);
                    }
                }
            } catch (Exception e) {
                throw new Exception("invalid field \"" + str + "\", invalid element \"" + element + "\", " + e.getMessage());
            }
        }
        if (values.size() == 0) {
            throw new Exception("invalid field \"" + str + "\"");
        }
        if (parser == DAY_OF_MONTH_VALUE_PARSER) {
            return new DayOfMonthValueMatcher(str, values);
        }
        return new IntArrayValueMatcher(str, values);
    }

    private ArrayList parseListElement(String str, ValueParser parser) throws Exception {
        StringTokenizer st = new StringTokenizer(str, "/");
        int size = st.countTokens();
        if (size < 1 || size > 2) {
            throw new Exception("syntax error");
        }
        try {
            ArrayList values = parseRange(st.nextToken(), parser);
            if (size != 2) {
                return values;
            }
            String dStr = st.nextToken();
            try {
                int div = Integer.parseInt(dStr);
                if (div < 1) {
                    throw new Exception("non positive divisor \"" + div + "\"");
                }
                ArrayList values2 = new ArrayList();
                for (int i = 0; i < values.size(); i += div) {
                    values2.add(values.get(i));
                }
                return values2;
            } catch (NumberFormatException e) {
                throw new Exception("invalid divisor \"" + dStr + "\"");
            }
        } catch (Exception e2) {
            throw new Exception("invalid range, " + e2.getMessage());
        }
    }

    private ArrayList parseRange(String str, ValueParser parser) throws Exception {
        if (str.equals("*")) {
            int min = parser.getMinValue();
            int max = parser.getMaxValue();
            ArrayList values = new ArrayList();
            for (int i = min; i <= max; i++) {
                values.add(new Integer(i));
            }
            return values;
        }
        StringTokenizer st = new StringTokenizer(str, "-");
        int size = st.countTokens();
        if (size < 1 || size > 2) {
            throw new Exception("syntax error");
        }
        String v1Str = st.nextToken();
        try {
            int v1 = parser.parse(v1Str);
            if (size == 1) {
                ArrayList values2 = new ArrayList();
                values2.add(new Integer(v1));
                return values2;
            }
            String v2Str = st.nextToken();
            try {
                int v2 = parser.parse(v2Str);
                ArrayList values3 = new ArrayList();
                if (v1 < v2) {
                    for (int i2 = v1; i2 <= v2; i2++) {
                        values3.add(new Integer(i2));
                    }
                    return values3;
                }
                if (v1 > v2) {
                    int min2 = parser.getMinValue();
                    int max2 = parser.getMaxValue();
                    for (int i3 = v1; i3 <= max2; i3++) {
                        values3.add(new Integer(i3));
                    }
                    for (int i4 = min2; i4 <= v2; i4++) {
                        values3.add(new Integer(i4));
                    }
                    return values3;
                }
                values3.add(new Integer(v1));
                return values3;
            } catch (Exception e) {
                throw new Exception("invalid value \"" + v2Str + "\", " + e.getMessage());
            }
        } catch (Exception e2) {
            throw new Exception("invalid value \"" + v1Str + "\", " + e2.getMessage());
        }
    }

    public boolean match(TimeZone timezone, long millis) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(millis);
        gc.setTimeZone(timezone);
        int minute = gc.get(12);
        int hour = gc.get(11);
        int dayOfMonth = gc.get(5);
        int month = gc.get(2) + 1;
        int dayOfWeek = gc.get(7) - 1;
        int year = gc.get(1);
        for (int i = 0; i < this.matcherSize; i++) {
            ValueMatcher minuteMatcher = this.minuteMatchers.get(i);
            ValueMatcher hourMatcher = this.hourMatchers.get(i);
            ValueMatcher dayOfMonthMatcher = this.dayOfMonthMatchers.get(i);
            ValueMatcher monthMatcher = this.monthMatchers.get(i);
            ValueMatcher dayOfWeekMatcher = this.dayOfWeekMatchers.get(i);
            boolean eval = minuteMatcher.match(minute) && hourMatcher.match(hour) && (!(dayOfMonthMatcher instanceof DayOfMonthValueMatcher) ? !dayOfMonthMatcher.match(dayOfMonth) : !((DayOfMonthValueMatcher) dayOfMonthMatcher).match(dayOfMonth, month, gc.isLeapYear(year))) && monthMatcher.match(month) && dayOfWeekMatcher.match(dayOfWeek);
            if (eval) {
                return true;
            }
        }
        return false;
    }

    public boolean match(long millis) {
        return match(TimeZone.getDefault(), millis);
    }

    public String toString() {
        return this.asString;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int parseAlias(String value, String[] aliases, int offset) throws Exception {
        for (int i = 0; i < aliases.length; i++) {
            if (aliases[i].equalsIgnoreCase(value)) {
                return offset + i;
            }
        }
        throw new Exception("invalid alias \"" + value + "\"");
    }

    private static class SimpleValueParser implements ValueParser {
        protected int maxValue;
        protected int minValue;

        public SimpleValueParser(int minValue, int maxValue) {
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        @Override // com.buzzbox.mob.android.scheduler.cron.SchedulingPattern.ValueParser
        public int parse(String value) throws Exception {
            try {
                int i = Integer.parseInt(value);
                if (i < this.minValue || i > this.maxValue) {
                    throw new Exception("value out of range");
                }
                return i;
            } catch (NumberFormatException e) {
                throw new Exception("invalid integer value");
            }
        }

        @Override // com.buzzbox.mob.android.scheduler.cron.SchedulingPattern.ValueParser
        public int getMinValue() {
            return this.minValue;
        }

        @Override // com.buzzbox.mob.android.scheduler.cron.SchedulingPattern.ValueParser
        public int getMaxValue() {
            return this.maxValue;
        }
    }

    private static class MinuteValueParser extends SimpleValueParser {
        public MinuteValueParser() {
            super(0, 59);
        }
    }

    private static class HourValueParser extends SimpleValueParser {
        public HourValueParser() {
            super(0, 23);
        }
    }

    private static class DayOfMonthValueParser extends SimpleValueParser {
        public DayOfMonthValueParser() {
            super(1, 31);
        }

        @Override // com.buzzbox.mob.android.scheduler.cron.SchedulingPattern.SimpleValueParser, com.buzzbox.mob.android.scheduler.cron.SchedulingPattern.ValueParser
        public int parse(String value) throws Exception {
            if (value.equalsIgnoreCase("L")) {
                return 32;
            }
            return super.parse(value);
        }
    }

    private static class MonthValueParser extends SimpleValueParser {
        private static String[] ALIASES = {"jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"};

        public MonthValueParser() {
            super(1, 12);
        }

        @Override // com.buzzbox.mob.android.scheduler.cron.SchedulingPattern.SimpleValueParser, com.buzzbox.mob.android.scheduler.cron.SchedulingPattern.ValueParser
        public int parse(String value) throws Exception {
            try {
                return super.parse(value);
            } catch (Exception e) {
                return SchedulingPattern.parseAlias(value, ALIASES, 1);
            }
        }
    }

    private static class DayOfWeekValueParser extends SimpleValueParser {
        private static String[] ALIASES = {"sun", "mon", "tue", "wed", "thu", "fri", "sat"};

        public DayOfWeekValueParser() {
            super(0, 7);
        }

        @Override // com.buzzbox.mob.android.scheduler.cron.SchedulingPattern.SimpleValueParser, com.buzzbox.mob.android.scheduler.cron.SchedulingPattern.ValueParser
        public int parse(String value) throws Exception {
            try {
                return super.parse(value) % 7;
            } catch (Exception e) {
                return SchedulingPattern.parseAlias(value, ALIASES, 0);
            }
        }
    }
}
