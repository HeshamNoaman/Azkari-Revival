package com.dot.azkari;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.arttech.alarm.preferences.NotificationsManager;
import com.arttech.ramadan.RamadanPreferences;
import com.arttech.ramadan.TasksDBAdapter;
import com.dot.adapter.database.Azkar_DB_Adapter;
import com.dot.constants.ApplicationConstants;

public class SplashActivity extends Activity {
    private Boolean isMenuStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        initializeSharedPreferences();
        ApplicationConstants.initTypeFaces(getBaseContext());
        initializeApplicationConstants();
        initializeDataBase();
        SharedPreferences shared = getSharedPreferences(RamadanPreferences.AZKARI_PREFERENCES, 0);
        if (!shared.getBoolean(RamadanPreferences.PreferencesRamadanDBCreated, false)) {
            TasksDBAdapter.getInstance(this).copyDataBase(this);
            shared.edit().putBoolean(RamadanPreferences.PreferencesRamadanDBCreated, true).apply();
        }

        String catId = Utils.getNotificationPrayerCatID(this);
        if (catId != null && !"-1".equals(catId.trim())) {
            startMenu();
            return;
        }

        initializeSplash();
    }

    private final android.os.Handler splashHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    private final Runnable menuLaunchRunnable = new Runnable() {
        @Override
        public void run() {
            startMenu();
        }
    };

    private synchronized void startMenu() {
        if (!this.isMenuStarted) {
            this.isMenuStarted = true;
            this.splashHandler.removeCallbacks(this.menuLaunchRunnable);
            startActivity(new Intent(this, Menu.class));
            finish();
        }
    }

    private void initializeSplash() {
        ImageView azk = (ImageView) findViewById(R.id.splash);
        Animation fade1 = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation logofade = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        fade1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startMenu();
            }
        });

        ImageView logo = (ImageView) findViewById(R.id.arttech_logo);
        if (logo != null) {
            logo.startAnimation(logofade);
        }
        if (azk != null) {
            azk.startAnimation(fade1);
        }

        // Guaranteed transition fallback after 2.5 seconds regardless of animation state
        this.splashHandler.postDelayed(this.menuLaunchRunnable, 2500L);

        // Allow instant skip on tap
        android.view.View root = findViewById(android.R.id.content);
        if (root != null) {
            root.setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(android.view.View v) {
                    startMenu();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        this.splashHandler.removeCallbacks(this.menuLaunchRunnable);
        super.onDestroy();
    }

    private void initializeSharedPreferences() {
        SharedPreferences pref = getSharedPreferences(ApplicationConstants.APPLICATION_PREFERENCES, 0);
        boolean isPrefCreated = pref.getInt(ApplicationConstants.FIRST_TIME_PREFERENCES, 0) == 1;
        if (!isPrefCreated) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt(ApplicationConstants.SOUND_PREFERENCES, 1);
            editor.putInt(ApplicationConstants.VIBRATE_PREFERENCES, 1);
            editor.putInt(ApplicationConstants.FIRST_TIME_PREFERENCES, 1);
            editor.putInt(ApplicationConstants.PRAYERS_TEXT_FONT_SIZE, 17);
            editor.apply();
        }
        if (!NotificationsManager.getNotificationsPreferencesStatus(getBaseContext())) {
            NotificationsManager.init(getApplicationContext());
        }
        if (pref.getInt(ApplicationConstants.LANGUAGE_PREFERENCES, -100) == -100) {
            SharedPreferences.Editor editor2 = pref.edit();
            editor2.putInt(ApplicationConstants.LANGUAGE_PREFERENCES, 1);
            editor2.apply();
        }
        if (pref.getInt(ApplicationConstants.FONT_TYPE_FACE_PREFERENCES, -1) == -1) {
            SharedPreferences.Editor editor3 = pref.edit();
            editor3.putInt(ApplicationConstants.FONT_TYPE_FACE_PREFERENCES, 0);
            editor3.apply();
        }
        Utils.setCurrentLocaleFromShared(this);
    }

    private void initializeApplicationConstants() {
        ApplicationConstants.SetTypeFace(getAssets(), this);
    }

    private void initializeDataBase() {
        Azkar_DB_Adapter.Init(this);
        Azkar_DB_Adapter.open();
    }
}
