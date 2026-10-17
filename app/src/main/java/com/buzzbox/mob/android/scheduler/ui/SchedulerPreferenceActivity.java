package com.buzzbox.mob.android.scheduler.ui;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TimePicker;
import com.buzzbox.mob.android.scheduler.ScheduledService;
import com.buzzbox.mob.android.scheduler.cron.SchedulingPattern;
import com.buzzbox.mob.android.scheduler.cron.ValueMatcher;
import com.dot.constants.ApplicationConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;

/* JADX INFO: loaded from: classes.dex */
public class SchedulerPreferenceActivity extends PreferenceActivity {
    static final String DEF_SCHEDULE = "0 8-20/2 * * *";
    static final String PREFERENCE_TYPE_CHECKBOX = "CheckBoxPreference";
    static final String PREFERENCE_TYPE_LIST = "ListPreference";
    static final String PREFERENCE_TYPE_REPEAT = "RepeatPreference";
    private CheckBoxPreference mEnabled;
    private ListPreference mFrequencyPref;
    private Preference mFrom;
    private RepeatPreference mRepeatPref;
    private Preference mTo;
    String[] notificationTypes;
    String[] notificationTypesLabels;
    private PreferenceScreen preferenceScreen;
    private SharedPreferences prefs;
    Boolean showDaysOfWeek;
    Boolean showEnabledFromTo;
    Boolean showFrequency;
    Boolean showNotificationSettings;
    Boolean showWhatTimeFrequency;
    String taskClassName;
    Preference timeListPreference;
    private int mFromHour = 9;
    private int mFromMinutes = 0;
    private int mToHour = 20;
    private int mToMinutes = 0;
    String freqValue = "2h";
    final ArrayList<SimpleTime> times = new ArrayList<>();

    @Override // android.preference.PreferenceActivity, android.app.Activity
    protected void onCreate(Bundle icicle) {
        String title;
        super.onCreate(icicle);
        this.showDaysOfWeek = MetaDataUtils.getBoolean(this, "SchedulerPreferenceActivity.show.daysOfWeek", true);
        this.showFrequency = MetaDataUtils.getBoolean(this, "SchedulerPreferenceActivity.show.frequency", true);
        this.showWhatTimeFrequency = MetaDataUtils.getBoolean(this, "SchedulerPreferenceActivity.show.SelectAtWhatTimes", true);
        this.showEnabledFromTo = MetaDataUtils.getBoolean(this, "SchedulerPreferenceActivity.show.enabledFromTo", true);
        this.showNotificationSettings = MetaDataUtils.getBoolean(this, "SchedulerPreferenceActivity.show.notificationSettings", true);
        String notificationTypesString = MetaDataUtils.getString(this, "SchedulerPreferenceActivity.notificationTypes", null);
        if (notificationTypesString != null && !XmlPullParser.NO_NAMESPACE.equals(notificationTypesString.trim())) {
            this.notificationTypes = notificationTypesString.split(",");
        }
        this.notificationTypesLabels = MetaDataUtils.getString(this, "SchedulerPreferenceActivity.notificationTypesLabels", XmlPullParser.NO_NAMESPACE).split(",");
        if (this.notificationTypes != null && this.notificationTypesLabels.length != this.notificationTypes.length) {
            this.notificationTypesLabels = new String[this.notificationTypes.length];
            for (int i = 0; i < this.notificationTypes.length; i++) {
                this.notificationTypesLabels[i] = "Notification type: " + this.notificationTypes[i];
            }
        }
        this.preferenceScreen = getPreferenceManager().createPreferenceScreen(this);
        setPreferenceScreen(this.preferenceScreen);
        this.preferenceScreen.setTitle("General Settings");
        PreferenceCategory backgroundCategory = new PreferenceCategory(this);
        backgroundCategory.setTitle("Scheduler Configuration");
        backgroundCategory.setOrder(1);
        this.preferenceScreen.addPreference(backgroundCategory);
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String prefTitle = getResourceString(this, "scheduler_preference_activity.enabled.title", "Enabled");
        String prefSummary = getResourceString(this, "scheduler_preference_activity.enabled.summary", "Uncheck to disable");
        this.mEnabled = (CheckBoxPreference) createPreference(PREFERENCE_TYPE_CHECKBOX, prefTitle, prefSummary, "cron.enabled", -1, false, null, null, null);
        backgroundCategory.addPreference(this.mEnabled);
        int timeListPreferenceCount = 1 + 1;
        if (this.showDaysOfWeek.booleanValue()) {
            String prefTitle2 = getResourceString(this, "scheduler_preference_activity.daysOfWeek.title", "Days of the week");
            this.mRepeatPref = (RepeatPreference) createPreference(PREFERENCE_TYPE_REPEAT, prefTitle2, null, "cron.daysofweek", -1, false, null, null, null);
            backgroundCategory.addPreference(this.mRepeatPref);
            timeListPreferenceCount++;
        }
        String prefTitle3 = getResourceString(this, "scheduler_preference_activity.atThisTime.title", "At This Times");
        this.timeListPreference = createPreference(null, prefTitle3, null, "times", -1, false, null, null, null);
        if (this.showFrequency.booleanValue()) {
            Map<String, String> frequencyMap = StringUtils.optionsInteger2map(";", ':', getResourceString(this, "scheduler_preference_activity.frequency", "Every 5 Minutes:5m;Every 10 Minutes:10m;Every 15 Minutes:15m;Every 30 Minutes:30m;Every Hour:1h;Every 2 Hours:2h;Every 3 Hours:3h;Every 4 Hours:4h"));
            String[] frequenciesTitles = (String[]) frequencyMap.keySet().toArray(new String[frequencyMap.keySet().size()]);
            String[] frequenciesValues = (String[]) frequencyMap.values().toArray(new String[frequencyMap.values().size()]);
            if (this.showWhatTimeFrequency.booleanValue()) {
                String[] frequenciesTitlesNew = new String[frequenciesTitles.length + 1];
                System.arraycopy(frequenciesTitles, 0, frequenciesTitlesNew, 0, frequenciesTitles.length);
                frequenciesTitlesNew[frequenciesTitles.length] = getResourceString(this, "scheduler_preference_activity.frequency.summary.selectAtWhatTimes", "Select At what Times");
                frequenciesTitles = frequenciesTitlesNew;
                String[] frequenciesValuesNew = new String[frequenciesValues.length + 1];
                System.arraycopy(frequenciesValues, 0, frequenciesValuesNew, 0, frequenciesValues.length);
                frequenciesValuesNew[frequenciesValues.length] = "s";
                frequenciesValues = frequenciesValuesNew;
            }
            String prefTitle4 = getResourceString(this, "scheduler_preference_activity.frequency.title", "Frequency");
            this.mFrequencyPref = (ListPreference) createPreference(PREFERENCE_TYPE_LIST, prefTitle4, null, "cron.daysofweek", -1, false, prefTitle4, frequenciesTitles, frequenciesValues);
            this.mFrequencyPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.buzzbox.mob.android.scheduler.ui.SchedulerPreferenceActivity.1
                @Override // android.preference.Preference.OnPreferenceChangeListener
                public boolean onPreferenceChange(Preference pref, Object newValue) {
                    SchedulerPreferenceActivity.this.freqValue = String.valueOf(newValue);
                    SchedulerPreferenceActivity.this.applyDatasetChanges();
                    return true;
                }
            });
            backgroundCategory.addPreference(this.mFrequencyPref);
            this.timeListPreference.setEnabled(false);
            timeListPreferenceCount++;
        }
        this.timeListPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.buzzbox.mob.android.scheduler.ui.SchedulerPreferenceActivity.2
            @Override // android.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference arg0) {
                new TimePickerDialog(SchedulerPreferenceActivity.this, new TimePickerDialog.OnTimeSetListener() { // from class: com.buzzbox.mob.android.scheduler.ui.SchedulerPreferenceActivity.2.1
                    @Override // android.app.TimePickerDialog.OnTimeSetListener
                    public void onTimeSet(TimePicker arg1, int h, int m) {
                        SchedulerPreferenceActivity.this.times.add(new SimpleTime(h, m));
                        Collections.sort(SchedulerPreferenceActivity.this.times);
                        SchedulerPreferenceActivity.this.applyDatasetChanges();
                    }
                }, getLastHour(), getLastMinute(), DateFormat.is24HourFormat(SchedulerPreferenceActivity.this)).show();
                return false;
            }

            private int getLastHour() {
                if (SchedulerPreferenceActivity.this.times.size() == 0) {
                    return 9;
                }
                return SchedulerPreferenceActivity.this.times.get(SchedulerPreferenceActivity.this.times.size() - 1).h.intValue();
            }

            private int getLastMinute() {
                if (SchedulerPreferenceActivity.this.times.size() == 0) {
                    return 0;
                }
                return SchedulerPreferenceActivity.this.times.get(SchedulerPreferenceActivity.this.times.size() - 1).m.intValue();
            }
        });
        backgroundCategory.addPreference(this.timeListPreference);
        final int timeListPreferenceCount2 = timeListPreferenceCount + 1;
        if (this.showEnabledFromTo.booleanValue()) {
            String prefTitle5 = getResourceString(this, "scheduler_preference_activity.enabledFrom.title", "Enabled From...");
            this.mFrom = createPreference(null, prefTitle5, null, "cron.from", -1, false, null, null, null);
            String prefTitle6 = getResourceString(this, "scheduler_preference_activity.enabledTo.title", "Enabled To...");
            this.mTo = createPreference(null, prefTitle6, null, "cron.to", -1, false, null, null, null);
            backgroundCategory.addPreference(this.mFrom);
            backgroundCategory.addPreference(this.mTo);
        }
        if (this.showNotificationSettings.booleanValue()) {
            if (this.notificationTypes != null) {
                for (int i2 = 0; i2 < this.notificationTypes.length; i2++) {
                    addNotificationSettingSection(this.notificationTypes[i2], this.notificationTypesLabels[i2]);
                }
            } else {
                addNotificationSettingSection(null, null);
            }
        }
        String cron = getIntent().getStringExtra("cron");
        this.mEnabled.setChecked(getIntent().getBooleanExtra("cron.enabled", true));
        String title2 = getIntent().getStringExtra("task.name");
        if (title2 == null) {
            title = "Scheduler Configuration";
        } else {
            title = String.valueOf(title2) + " Configuration";
        }
        Log.i("buzzbox-scheduler", "Config Scheduler for: " + title);
        setTitle(title);
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { // from class: com.buzzbox.mob.android.scheduler.ui.SchedulerPreferenceActivity.3
            @Override // android.widget.AdapterView.OnItemLongClickListener
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                if (pos != timeListPreferenceCount2) {
                    return false;
                }
                SchedulerPreferenceActivity.this.times.clear();
                SchedulerPreferenceActivity.this.applyDatasetChanges();
                return true;
            }
        });
        this.taskClassName = getIntent().getStringExtra("task.class");
        if (cron == null || cron.length() == 0) {
            cron = DEF_SCHEDULE;
        }
        SchedulingPattern pattern = new SchedulingPattern(cron);
        initializePreferences(pattern);
    }

    private void addNotificationSettingSection(String notificationType, String notificationTypeLabel) {
        String notificationKeyPostfix = notificationType == null ? XmlPullParser.NO_NAMESPACE : "." + notificationType;
        PreferenceCategory settingsCategory = new PreferenceCategory(this);
        if (notificationType == null) {
            settingsCategory.setTitle("Notification options");
        } else {
            settingsCategory.setTitle(String.valueOf(notificationTypeLabel) + " Options");
        }
        settingsCategory.setOrder(2);
        this.preferenceScreen.addPreference(settingsCategory);
        CheckBoxPreference statusBarNotifEnabled = (CheckBoxPreference) createPreference(PREFERENCE_TYPE_CHECKBOX, notificationTypeLabel == null ? getResourceString(this, "scheduler_preference_activity.notification_type.enabled.title", "Status bar Notification") : notificationTypeLabel, getResourceString(this, "scheduler_preference_activity.notification_type.enabled.summary", "Enable notifications in the status bar when new notification arrives"), "cron.statusBar" + notificationKeyPostfix, -1, false, null, null, null);
        settingsCategory.addPreference(statusBarNotifEnabled);
        String prefTitle = getResourceString(this, "scheduler_preference_activity.sound.title", ApplicationConstants.SOUND_PREFERENCES);
        String prefSummary = getResourceString(this, "scheduler_preference_activity.sound.summary", "Also play sound when notification arrives");
        CheckBoxPreference soundEnabled = (CheckBoxPreference) createPreference(PREFERENCE_TYPE_CHECKBOX, prefTitle, prefSummary, "cron.sound" + notificationKeyPostfix, -1, false, null, null, null);
        settingsCategory.addPreference(soundEnabled);
        soundEnabled.setDependency(statusBarNotifEnabled.getKey());
        String prefTitle2 = getResourceString(this, "scheduler_preference_activity.vibrate.title", "Vibrate");
        String prefSummary2 = getResourceString(this, "scheduler_preference_activity.vibrate.summary", "Also vibrate when notification arrives");
        CheckBoxPreference vibrateEnabled = (CheckBoxPreference) createPreference(PREFERENCE_TYPE_CHECKBOX, prefTitle2, prefSummary2, "cron.vibrate" + notificationKeyPostfix, -1, false, null, null, null);
        settingsCategory.addPreference(vibrateEnabled);
        vibrateEnabled.setDependency(statusBarNotifEnabled.getKey());
        String prefTitle3 = getResourceString(this, "scheduler_preference_activity.led.title", "Led");
        String prefSummary3 = getResourceString(this, "scheduler_preference_activity.led.summary", "Also blink led when notification arrives");
        CheckBoxPreference ledEnabled = (CheckBoxPreference) createPreference(PREFERENCE_TYPE_CHECKBOX, prefTitle3, prefSummary3, "cron.led" + notificationKeyPostfix, -1, false, null, null, null);
        settingsCategory.addPreference(ledEnabled);
        ledEnabled.setDependency(statusBarNotifEnabled.getKey());
        String defaultSettingsString = MetaDataUtils.getString(this, "SchedulerPreferenceActivity.notificationTypes.defaultSettings" + (notificationType == null ? XmlPullParser.NO_NAMESPACE : "." + notificationType), "statusBar=enabled,vibrate=disabled,led=disabled,sound=disabled");
        Map<String, String> defaultSettingsMap = StringUtils.optionsInteger2map(",", '=', defaultSettingsString);
        ledEnabled.setChecked(this.prefs.getBoolean("cron.led" + notificationKeyPostfix, "enabled".equals(defaultSettingsMap.get("led"))));
        soundEnabled.setChecked(this.prefs.getBoolean("cron.sound" + notificationKeyPostfix, "enabled".equals(defaultSettingsMap.get("sound"))));
        vibrateEnabled.setChecked(this.prefs.getBoolean("cron.vibrate" + notificationKeyPostfix, "enabled".equals(defaultSettingsMap.get("vibrate"))));
        statusBarNotifEnabled.setChecked(this.prefs.getBoolean("cron.statusBar" + notificationKeyPostfix, "enabled".equals(defaultSettingsMap.get("statusBar"))));
    }

    @Override // android.preference.PreferenceActivity
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == this.mFrom) {
            new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() { // from class: com.buzzbox.mob.android.scheduler.ui.SchedulerPreferenceActivity.4
                @Override // android.app.TimePickerDialog.OnTimeSetListener
                public void onTimeSet(TimePicker arg0, int h, int m) {
                    SchedulerPreferenceActivity.this.mFromHour = h;
                    SchedulerPreferenceActivity.this.mFromMinutes = m;
                    SchedulerPreferenceActivity.this.applyDatasetChanges();
                }
            }, this.mFromHour, this.mFromMinutes, DateFormat.is24HourFormat(this)).show();
        } else if (preference == this.mTo) {
            new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() { // from class: com.buzzbox.mob.android.scheduler.ui.SchedulerPreferenceActivity.5
                @Override // android.app.TimePickerDialog.OnTimeSetListener
                public void onTimeSet(TimePicker arg0, int h, int m) {
                    SchedulerPreferenceActivity.this.mToHour = h;
                    SchedulerPreferenceActivity.this.mToMinutes = m;
                    SchedulerPreferenceActivity.this.applyDatasetChanges();
                }
            }, this.mToHour, this.mToMinutes, DateFormat.is24HourFormat(this)).show();
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override // android.app.Activity
    protected void onPause() {
        Intent res = saveAlarm();
        ScheduledService.processIntent(this, res);
        super.onPause();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void applyDatasetChanges() {
        if (this.showFrequency.booleanValue()) {
            this.mFrequencyPref.setValue(this.freqValue);
        } else {
            this.freqValue = "s";
        }
        if (this.showEnabledFromTo.booleanValue() && !this.showFrequency.booleanValue()) {
            this.timeListPreference.setEnabled(false);
            this.mFrom.setEnabled(true);
            this.mTo.setEnabled(true);
        } else if (this.freqValue.equals("s")) {
            if (this.showFrequency.booleanValue()) {
                this.mFrequencyPref.setSummary(getResourceString(this, "scheduler_preference_activity.frequency.summary.atSpecificTime", "At Specific Times"));
            }
            this.timeListPreference.setEnabled(true);
            if (this.showEnabledFromTo.booleanValue()) {
                this.mFrom.setEnabled(false);
                this.mTo.setEnabled(false);
            }
            if (this.times.isEmpty()) {
                this.timeListPreference.setSummary(getResourceString(this, "scheduler_preference_activity.atThisTime.summary.clickToSelectWhatTime", "Click to select what time"));
            } else {
                this.timeListPreference.setSummary(String.valueOf(this.times.toString()) + " " + getResourceString(this, "scheduler_preference_activity.atThisTime.summary.clickToAddLongClickToReset", "click to add, long click to reset"));
            }
        } else {
            int n = Integer.parseInt(this.freqValue.substring(0, this.freqValue.length() - 1));
            if (this.freqValue.endsWith("m")) {
                this.mFrequencyPref.setSummary(String.valueOf(getResourceString(this, "scheduler_preference_activity.frequency.summary.every", "Every")) + " " + n + " " + getResourceString(this, "scheduler_preference_activity.frequency.summary.minutes", "Minutes"));
            } else {
                this.mFrequencyPref.setSummary(String.valueOf(getResourceString(this, "scheduler_preference_activity.frequency.summary.every", "Every")) + " " + n + " " + getResourceString(this, "scheduler_preference_activity.frequency.summary.hours", "Hours"));
            }
            this.timeListPreference.setEnabled(false);
            if (this.showEnabledFromTo.booleanValue()) {
                this.mFrom.setEnabled(true);
                this.mTo.setEnabled(true);
            }
            this.timeListPreference.setSummary(getResourceString(this, "scheduler_preference_activity.atThisTime.summary", "Run 1,2,3... times a day. Choose Frequency > Select Times to enable"));
        }
        if (this.showEnabledFromTo.booleanValue()) {
            this.mFrom.setSummary(String.valueOf(getResourceString(this, "scheduler_preference_activity.enabledFrom.summary", "From")) + " " + TimeUtils.formatTime(this.mFromHour, 0));
            this.mTo.setSummary(String.valueOf(getResourceString(this, "scheduler_preference_activity.enabledTo.summary", "To")) + " " + TimeUtils.formatTime(this.mToHour, 0));
        }
    }

    private void initializePreferences(SchedulingPattern pattern) {
        if (this.showDaysOfWeek.booleanValue()) {
            ValueMatcher vm = pattern.dayOfWeekMatchers.get(0);
            RepeatPreference.DaysOfWeek d = new RepeatPreference.DaysOfWeek(0);
            d.set(0, vm.match(1));
            d.set(1, vm.match(2));
            d.set(2, vm.match(3));
            d.set(3, vm.match(4));
            d.set(4, vm.match(5));
            d.set(5, vm.match(6));
            d.set(6, vm.match(0));
            this.mRepeatPref.setDaysOfWeek(d);
        }
        if (!this.showEnabledFromTo.booleanValue() || this.showFrequency.booleanValue()) {
            if (pattern.hourMatchers.size() > 1 || !pattern.toString().contains("/")) {
                this.freqValue = "s";
                for (int i = 0; i < pattern.hourMatchers.size(); i++) {
                    this.times.add(new SimpleTime(Integer.parseInt(pattern.hourMatchers.get(i).asString()), Integer.parseInt(pattern.minuteMatchers.get(i).asString())));
                }
                Collections.sort(this.times);
            } else {
                String minutes = pattern.minuteMatchers.get(0).asString();
                String hours = pattern.hourMatchers.get(0).asString();
                if (minutes.contains("/")) {
                    int m = Integer.parseInt(minutes.substring(minutes.indexOf("/") + 1, minutes.length()));
                    this.freqValue = String.valueOf(m) + "m";
                } else if (hours.contains("/")) {
                    int h = Integer.parseInt(hours.substring(hours.indexOf("/") + 1, hours.length()));
                    this.freqValue = String.valueOf(h) + "h";
                }
                if (!hours.startsWith("*")) {
                    this.mFromHour = Integer.parseInt(hours.substring(0, hours.indexOf("-")));
                    int endHoursIndexSlash = hours.indexOf("/");
                    int endHours = hours.length();
                    if (endHoursIndexSlash != -1) {
                        endHours = endHoursIndexSlash;
                    }
                    this.mToHour = Integer.parseInt(hours.substring(hours.indexOf("-") + 1, endHours));
                }
            }
        }
        applyDatasetChanges();
    }

    private Intent saveAlarm() {
        StringBuilder resultCron = new StringBuilder();
        if (this.timeListPreference.isEnabled()) {
            if (this.times.isEmpty()) {
                this.times.add(new SimpleTime(9, 0));
            }
            for (SimpleTime t : this.times) {
                if (resultCron.length() > 0) {
                    resultCron.append("|");
                }
                resultCron.append(t.m).append(" ").append(t.h).append(" * * ");
                appendDaysOfWeek(resultCron);
            }
        } else {
            if (this.freqValue.endsWith("m")) {
                if ("5m".equals(this.freqValue)) {
                    resultCron.append("*/5 ");
                } else if ("10m".equals(this.freqValue)) {
                    resultCron.append("*/10 ");
                } else if ("15m".equals(this.freqValue)) {
                    resultCron.append("*/15 ");
                } else if ("20m".equals(this.freqValue)) {
                    resultCron.append("*/20 ");
                } else if ("30m".equals(this.freqValue)) {
                    resultCron.append("*/30 ");
                }
            } else {
                resultCron.append("0 ");
            }
            if (this.showEnabledFromTo.booleanValue()) {
                resultCron.append(this.mFromHour).append("-").append(this.mToHour);
            } else {
                resultCron.append(" *");
            }
            if (this.freqValue.endsWith("h")) {
                if ("1h".equals(this.freqValue)) {
                    resultCron.append("/1 ");
                } else if ("2h".equals(this.freqValue)) {
                    resultCron.append("/2 ");
                } else if ("3h".equals(this.freqValue)) {
                    resultCron.append("/3 ");
                } else if ("4h".equals(this.freqValue)) {
                    resultCron.append("/4 ");
                } else if ("6h".equals(this.freqValue)) {
                    resultCron.append("/6 ");
                } else if ("8h".equals(this.freqValue)) {
                    resultCron.append("/8 ");
                } else {
                    resultCron.append(" ");
                }
            } else {
                resultCron.append(" ");
            }
            resultCron.append("* * ");
            appendDaysOfWeek(resultCron);
        }
        Intent i = new Intent(this, (Class<?>) ScheduledService.class);
        i.putExtra("save", true);
        i.putExtra("cron", resultCron.toString());
        i.putExtra("cron.enabled", this.mEnabled.isChecked());
        i.putExtra("task.class", this.taskClassName);
        if (this.showNotificationSettings.booleanValue()) {
            if (this.notificationTypes != null) {
                i.putExtra("notificationTypes", this.notificationTypes);
                for (int j = 0; j < this.notificationTypes.length; j++) {
                    String key = "cron.statusBar." + this.notificationTypes[j];
                    CheckBoxPreference temp = (CheckBoxPreference) this.preferenceScreen.findPreference(key);
                    i.putExtra(key, temp.isChecked());
                    String key2 = "cron.vibrate." + this.notificationTypes[j];
                    CheckBoxPreference temp2 = (CheckBoxPreference) this.preferenceScreen.findPreference(key2);
                    i.putExtra(key2, temp2.isChecked());
                    String key3 = "cron.led." + this.notificationTypes[j];
                    CheckBoxPreference temp3 = (CheckBoxPreference) this.preferenceScreen.findPreference(key3);
                    i.putExtra(key3, temp3.isChecked());
                    String key4 = "cron.sound." + this.notificationTypes[j];
                    CheckBoxPreference temp4 = (CheckBoxPreference) this.preferenceScreen.findPreference(key4);
                    i.putExtra(key4, temp4.isChecked());
                }
            } else {
                CheckBoxPreference temp5 = (CheckBoxPreference) this.preferenceScreen.findPreference("cron.statusBar");
                i.putExtra("cron.statusBar", temp5.isChecked());
                CheckBoxPreference temp6 = (CheckBoxPreference) this.preferenceScreen.findPreference("cron.vibrate");
                i.putExtra("cron.vibrate", temp6.isChecked());
                CheckBoxPreference temp7 = (CheckBoxPreference) this.preferenceScreen.findPreference("cron.led");
                i.putExtra("cron.led", temp7.isChecked());
                CheckBoxPreference temp8 = (CheckBoxPreference) this.preferenceScreen.findPreference("cron.sound");
                i.putExtra("cron.sound", temp8.isChecked());
            }
        }
        return i;
    }

    private void appendDaysOfWeek(StringBuilder resultCron) {
        StringBuffer daysOfWeek = new StringBuffer();
        boolean[] dowArray = new boolean[0];
        if (this.showDaysOfWeek.booleanValue()) {
            dowArray = this.mRepeatPref.getDaysOfWeek().getBooleanArray();
        }
        for (int i = 0; i < dowArray.length; i++) {
            if (dowArray[i]) {
                daysOfWeek.append(i + 1);
                daysOfWeek.append(",");
            }
        }
        if (daysOfWeek.length() > 0) {
            daysOfWeek.delete(daysOfWeek.length() - 1, daysOfWeek.length());
        }
        if (daysOfWeek.length() == 0) {
            daysOfWeek.append("*");
        }
        resultCron.append(daysOfWeek.toString());
    }

    static class SimpleTime implements Comparable<SimpleTime> {
        Integer h;
        Integer m;

        public SimpleTime(int h, int m) {
            this.h = Integer.valueOf(h);
            this.m = Integer.valueOf(m);
        }

        public String toString() {
            return TimeUtils.formatTime(this.h.intValue(), this.m.intValue());
        }

        @Override // java.lang.Comparable
        public int compareTo(SimpleTime t) {
            int r = this.h.compareTo(t.h);
            return r == 0 ? this.m.compareTo(t.m) : r;
        }
    }

    private Preference createPreference(String type, String title, String summary, String key, int order, boolean isPersistent, CharSequence dialogTitle, CharSequence[] entries, CharSequence[] entryValues) {
        Preference newPreference;
        if (PREFERENCE_TYPE_CHECKBOX.equals(type)) {
            newPreference = new CheckBoxPreference(this);
        } else if (PREFERENCE_TYPE_REPEAT.equals(type)) {
            newPreference = new RepeatPreference(this, null);
        } else if (PREFERENCE_TYPE_LIST.equals(type)) {
            newPreference = new ListPreference(this);
            ((ListPreference) newPreference).setEntries(entries);
            ((ListPreference) newPreference).setDialogTitle(dialogTitle);
            ((ListPreference) newPreference).setEntryValues(entryValues);
        } else {
            newPreference = new Preference(this);
        }
        newPreference.setKey(key);
        if (order != -1) {
            newPreference.setOrder(order);
        }
        newPreference.setTitle(title);
        newPreference.setSummary(summary);
        newPreference.setPersistent(isPersistent);
        return newPreference;
    }

    protected static String getResourceString(Context ctx, String name, String defaultValue) {
        try {
            int resId = ctx.getResources().getIdentifier(name, "string", ctx.getPackageName());
            String defaultValue2 = ctx.getResources().getString(resId);
            return defaultValue2;
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
