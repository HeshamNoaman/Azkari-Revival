package com.dot.azkari;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.ViewFlipper;
import com.dot.adapter.database.Azkar_DB_Adapter;
import com.dot.constants.ApplicationConstants;
import com.dot.timer.Timer;
import java.util.Calendar;
import java.util.Date;
import java.util.Stack;
import org.xmlpull.v1.XmlPullParser;

/* JADX INFO: loaded from: classes.dex */
public class Prayers extends Activity {
    private static boolean isPlayingAudio;
    private MediaPlayer CustomPlayer;
    Date applicationPauseDate;
    Date applicationResumeDate;
    private ImageButton audioBtn;
    private Boolean canPlaySound;
    private Boolean canVibrate;
    private String categoryId;
    private Chronometer chrone;
    private int currentRepetition;
    private MediaPlayer flipPagePlayer;
    private ImageButton goToNextPrayer;
    private ImageButton imgbtnPrefer;
    private boolean isAutoPlayEnabled;
    private boolean isPlaying;
    private ViewFlipper mFlipper;
    private Boolean mustShowDialog;
    private boolean pauseDialogShown;
    private MediaPlayer player;
    private Cursor prayersCursor;
    private SharedPreferences pref;
    private ProgressBar progress;
    private String progressDialogMessage;
    private Stack<Progress> progressList;
    private int repetition;
    DetectingTouchDirection swipeHandler;
    private Timer timer;
    private TextView txvPrayerTitle;
    private TextView txvPrayers;
    private TextView txvPrayersRepitition;
    private Vibrator vibrator;
    private Boolean wasPosted;
    private final int PAUSE_DIALOG = 1;
    private final int PREFER_DIALOG_ID = 2;
    private Boolean isShown = false;
    private ProgressDialog prgDialog = null;
    private Boolean isFBAuthorized = false;
    private final Handler applicationHandler = new Handler();
    private boolean callReceived = false;
    private boolean isSpeaking = false;
    Runnable dialogRunnable = new Runnable() { // from class: com.dot.azkari.Prayers.1
        @Override // java.lang.Runnable
        public void run() {
            if (!Prayers.this.isShown.booleanValue()) {
                Prayers.this.prgDialog.dismiss();
            } else {
                Prayers.this.prgDialog = ProgressDialog.show(Prayers.this, XmlPullParser.NO_NAMESPACE, Prayers.this.progressDialogMessage, true);
            }
        }
    };
    Runnable notificationRunnable = new Runnable() { // from class: com.dot.azkari.Prayers.2
        @Override // java.lang.Runnable
        public void run() {
            if (!Prayers.this.wasPosted.booleanValue()) {
                if (!Prayers.this.wasPosted.booleanValue() && !Utils.isOnline(Prayers.this)) {
                    Utils.getToast(Prayers.this, Prayers.this.getString(R.string.no_internet_Access), R.drawable.failed, 1).show();
                    return;
                } else {
                    Utils.getToast(Prayers.this, Prayers.this.getString(R.string.failed_To_Post), R.drawable.failed, 1).show();
                    return;
                }
            }
            Utils.getToast(Prayers.this, Prayers.this.getString(R.string.successfully_Posted), R.drawable.success, 1).show();
        }
    };
    GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() { // from class: com.dot.azkari.Prayers.3
        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1.getX() - e2.getX() > 80.0f) {
                if (Math.abs(e1.getY() - e2.getY()) < 100.0f) {
                    Prayers.this.loadNextPrayer(false);
                    return true;
                }
                return true;
            }
            if (e2.getX() - e1.getX() > 80.0f && Math.abs(e2.getY() - e1.getY()) < 100.0f) {
                Prayers.this.loadPreviousPrayer();
                return true;
            }
            return true;
        }
    };
    GestureDetector gestureDetector = new GestureDetector(this.simpleOnGestureListener);

    enum Sounds {
        PAGEFLIP,
        CUSROM_SOUND;

        /* JADX INFO: renamed from: values, reason: to resolve conflict with enum method */
        public static Sounds[] valuesCustom() {
            Sounds[] soundsArrValuesCustom = values();
            int length = soundsArrValuesCustom.length;
            Sounds[] soundsArr = new Sounds[length];
            System.arraycopy(soundsArrValuesCustom, 0, soundsArr, 0, length);
            return soundsArr;
        }
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prayers);
        initializeVariables();
        initiaizeAudioButton();
        startTimer();
        initializeTextViews();
        initializePlayer();
        initializeHandlers();
        initializeProgressBar();
        loadPrayers();
        setActivityStrings();
        setLayoutDirection();
        loadVibrationAndSoundStatus();
        initializeAutoPlaySettings();
        initializeToggleBtn();
        initializeCallDetector();
    }

    private void initializeCallDetector() {
        try {
            android.media.AudioManager am = (android.media.AudioManager) getSystemService(android.content.Context.AUDIO_SERVICE);
            if (am != null) {
                am.requestAudioFocus(new android.media.AudioManager.OnAudioFocusChangeListener() {
                    @Override
                    public void onAudioFocusChange(int focusChange) {
                        if (focusChange == android.media.AudioManager.AUDIOFOCUS_LOSS
                                || focusChange == android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                            if (Prayers.isPlayingAudio && Prayers.this.player != null) {
                                Prayers.this.callReceived = true;
                                try {
                                    if (Prayers.this.player.isPlaying()) {
                                        Prayers.this.applicationResumeDate = new Date();
                                        if (Prayers.this.timer != null) Prayers.this.timer.stop();
                                        Prayers.this.mustShowDialog = true;
                                        Prayers.this.player.pause();
                                    }
                                } catch (Exception ignored) {
                                }
                            }
                        }
                    }
                }, android.media.AudioManager.STREAM_MUSIC, android.media.AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
            }
        } catch (Exception ignored) {
        }
    }

    private void initiaizeAudioButton() {
        this.audioBtn = new ImageButton(this);
        isPlayingAudio = false;
        this.audioBtn.setOnClickListener(new View.OnClickListener() { // from class: com.dot.azkari.Prayers.5
            @Override // android.view.View.OnClickListener
            public void onClick(View arg0) {
                Prayers.this.audioBtn.setBackgroundDrawable(Prayers.this.getResources().getDrawable(R.drawable.stop_speech));
                Prayers.this.isSpeaking = true;
                if (!Prayers.isPlayingAudio) {
                    Prayers.this.playAudio();
                    return;
                }
                Prayers.this.stopAudio();
                Prayers.this.isSpeaking = false;
                Prayers.this.audioBtn.setBackgroundDrawable(Prayers.this.getResources().getDrawable(R.drawable.speech));
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playAudio() {
        this.player = MediaPlayer.create(this, getResources().getIdentifier(this.audioBtn.getTag().toString(), "raw", getPackageName()));
        this.player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // from class: com.dot.azkari.Prayers.6
            @Override // android.media.MediaPlayer.OnCompletionListener
            public void onCompletion(MediaPlayer arg0) {
                Prayers.isPlayingAudio = false;
                if (!Prayers.this.isAutoPlayEnabled) {
                    if (Prayers.this.isSpeaking) {
                        Prayers.this.isSpeaking = false;
                        Prayers.this.audioBtn.setBackgroundDrawable(Prayers.this.getResources().getDrawable(R.drawable.speech));
                        return;
                    }
                    return;
                }
                Prayers.this.loadNextPrayer(false);
            }
        });
        this.player.setOnErrorListener(new MediaPlayer.OnErrorListener() { // from class: com.dot.azkari.Prayers.7
            @Override // android.media.MediaPlayer.OnErrorListener
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Prayers.isPlayingAudio = false;
                return false;
            }
        });
        isPlayingAudio = true;
        this.player.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopAudio() {
        isPlayingAudio = false;
        this.player.stop();
        this.player.release();
    }

    private void initializeAutoPlaySettings() {
        this.player = MediaPlayer.create(this, getResources().getIdentifier(this.audioBtn.getTag().toString(), "raw", getPackageName()));
        ToggleButton tbtAutoPlay = (ToggleButton) findViewById(R.id.tbtAutoPlay);
        tbtAutoPlay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.dot.azkari.Prayers.8
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Prayers.this.isAutoPlayEnabled = isChecked;
                if (isChecked) {
                    if (Prayers.isPlayingAudio) {
                        Prayers.this.stopAudio();
                    }
                    Prayers.this.playAudio();
                    Prayers.this.audioBtn.setEnabled(false);
                    Prayers.this.audioBtn.setBackgroundDrawable(Prayers.this.getResources().getDrawable(R.drawable.disable_speech));
                    return;
                }
                if (Prayers.isPlayingAudio) {
                    Prayers.this.stopAudio();
                }
                Prayers.this.audioBtn.setEnabled(true);
                Prayers.this.audioBtn.setBackgroundDrawable(Prayers.this.getResources().getDrawable(R.drawable.speech));
            }
        });
    }

    private void initializeVibrationAndSoundHandler() {
        final SharedPreferences.Editor editor = this.pref.edit();
        Button btnVibrate = (Button) findViewById(R.id.btnVibtate);
        btnVibrate.setOnClickListener(new View.OnClickListener() { // from class: com.dot.azkari.Prayers.9
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Prayers.this.canVibrate = Boolean.valueOf(!Prayers.this.canVibrate.booleanValue());
                if (Prayers.this.canVibrate.booleanValue()) {
                    editor.putInt(ApplicationConstants.VIBRATE_PREFERENCES, 1);
                } else {
                    editor.putInt(ApplicationConstants.VIBRATE_PREFERENCES, 0);
                }
                editor.commit();
                Prayers.this.setIcons();
            }
        });
        Button imgbtnSound = (Button) findViewById(R.id.btnSound);
        imgbtnSound.setOnClickListener(new View.OnClickListener() { // from class: com.dot.azkari.Prayers.10
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Prayers.this.canPlaySound = Boolean.valueOf(!Prayers.this.canPlaySound.booleanValue());
                if (Prayers.this.canPlaySound.booleanValue()) {
                    editor.putInt(ApplicationConstants.SOUND_PREFERENCES, 1);
                } else {
                    editor.putInt(ApplicationConstants.SOUND_PREFERENCES, 0);
                }
                editor.commit();
                Prayers.this.setIcons();
            }
        });
    }

    private void loadVibrationAndSoundStatus() {
        int vibrate = this.pref.getInt(ApplicationConstants.VIBRATE_PREFERENCES, 0);
        if (vibrate == 1) {
            this.canVibrate = true;
        } else {
            this.canVibrate = false;
        }
        int sound = this.pref.getInt(ApplicationConstants.SOUND_PREFERENCES, 0);
        if (sound == 1) {
            this.canPlaySound = true;
        } else {
            this.canPlaySound = false;
        }
        setIcons();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setIcons() {
        Button imgbtnSound = (Button) findViewById(R.id.btnSound);
        if (this.canPlaySound.booleanValue()) {
            imgbtnSound.setBackgroundDrawable(getResources().getDrawable(R.drawable.sound));
        } else {
            imgbtnSound.setBackgroundDrawable(getResources().getDrawable(R.drawable.mute));
        }
        Button imgbtnVibrate = (Button) findViewById(R.id.btnVibtate);
        if (this.canVibrate.booleanValue()) {
            imgbtnVibrate.setBackgroundDrawable(getResources().getDrawable(R.drawable.vibrate_on));
        } else {
            imgbtnVibrate.setBackgroundDrawable(getResources().getDrawable(R.drawable.vibrate_off));
        }
    }

    private void setLayoutDirection() {
        if (ApplicationConstants.APPLICATION_LANGUAGE_DIRECTION == ApplicationConstants.Direction.LTR) {
            this.txvPrayers.setGravity(3);
            this.txvPrayers.invalidate();
            this.mFlipper.setForegroundGravity(3);
            this.mFlipper.invalidate();
            return;
        }
        this.txvPrayers.setGravity(5);
        this.txvPrayers.invalidate();
        this.mFlipper.setForegroundGravity(5);
        this.mFlipper.invalidate();
    }

    private void initializeVariables() {
        this.progressList = new Stack<>();
        this.currentRepetition = 1;
        this.txvPrayersRepitition = (TextView) findViewById(R.id.txvRepetition);
        this.vibrator = (Vibrator) getSystemService("vibrator");
        this.pref = getSharedPreferences(ApplicationConstants.APPLICATION_PREFERENCES, 0);
        this.mFlipper = (ViewFlipper) findViewById(R.id.flipper);
        this.mustShowDialog = false;
        isPlayingAudio = false;
        this.isAutoPlayEnabled = false;
    }

    private void initializeBackButtonHandler() {
        ImageButton imgbutBack = (ImageButton) findViewById(R.id.imgbtnBack);
        imgbutBack.setOnClickListener(new View.OnClickListener() { // from class: com.dot.azkari.Prayers.11
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Prayers.this.loadPreviousPrayer();
            }
        });
    }

    private void initializeNextButtonHandler() {
        ImageButton imgbutNext = (ImageButton) findViewById(R.id.imgbtnNext);
        imgbutNext.setOnClickListener(new View.OnClickListener() { // from class: com.dot.azkari.Prayers.12
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Prayers.this.loadNextPrayer(false);
            }
        });
        this.goToNextPrayer = (ImageButton) findViewById(R.id.imgbtnGoToNextPrayer);
        this.goToNextPrayer.setOnClickListener(new View.OnClickListener() { // from class: com.dot.azkari.Prayers.13
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Prayers.this.loadNextPrayer(true);
            }
        });
    }

    private void initializePlayer() {
        this.isPlaying = false;
        this.flipPagePlayer = MediaPlayer.create(this, R.raw.page_flip);
        this.flipPagePlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // from class: com.dot.azkari.Prayers.14
            @Override // android.media.MediaPlayer.OnCompletionListener
            public void onCompletion(MediaPlayer mp) {
                Prayers.this.isPlaying = false;
            }
        });
        this.CustomPlayer = MediaPlayer.create(this, R.raw.tick);
        this.CustomPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // from class: com.dot.azkari.Prayers.15
            @Override // android.media.MediaPlayer.OnCompletionListener
            public void onCompletion(MediaPlayer mp) {
                Prayers.this.isPlaying = false;
            }
        });
    }

    private void playSound(Sounds soundSrc) {
        if (this.canPlaySound.booleanValue() && !this.isPlaying) {
            this.isPlaying = true;
            if (soundSrc == Sounds.CUSROM_SOUND) {
                this.CustomPlayer.start();
            } else {
                this.flipPagePlayer.start();
            }
        }
    }

    private void setActivityStrings() {
        this.progressDialogMessage = getString(R.string.pleaseWait);
    }

    private void initializeProgressBar() {
        this.progress = (ProgressBar) findViewById(R.id.progress);
        String catid = getIntent().getExtras().getString("CAT_ID");
        Azkar_DB_Adapter.open();
        this.progress.setMax(Azkar_DB_Adapter.select_prayers_repetition_sum(catid));
        Azkar_DB_Adapter.close();
    }

    private void loadPrayers() {
        this.categoryId = getIntent().getExtras().getString("CAT_ID");
        Azkar_DB_Adapter.Init(this);
        this.prayersCursor = Azkar_DB_Adapter.select_prayers_by_catid(this.categoryId);
        if (this.prayersCursor.moveToFirst()) {
            Utils.setText(this.txvPrayers, this.prayersCursor.getString(this.prayersCursor.getColumnIndex("PRA_TEXT")));
            this.txvPrayers.setTag(this.prayersCursor.getString(this.prayersCursor.getColumnIndex("PRA_ID")));
            this.repetition = this.prayersCursor.getInt(this.prayersCursor.getColumnIndex("PRA_REPETITION"));
            Utils.setTypeFace(this.txvPrayersRepitition);
            this.txvPrayersRepitition.setText(String.valueOf(this.currentRepetition) + "/" + this.repetition);
            if (this.prayersCursor.getString(this.prayersCursor.getColumnIndex("PRA_PREFE")).trim().equals(XmlPullParser.NO_NAMESPACE)) {
                this.imgbtnPrefer.setVisibility(8);
            }
            this.audioBtn.setTag(this.prayersCursor.getString(this.prayersCursor.getColumnIndex("PRA_AUD")));
            this.progress.incrementSecondaryProgressBy(this.repetition);
            this.progress.incrementProgressBy(1);
            setEnabledBackButton(false);
        } else {
            Azkar_DB_Adapter.close();
            finish();
        }
        Azkar_DB_Adapter.close();
    }

    public void loadNextPrayer(boolean SkipCurrent) {
        if (isPlayingAudio) {
            stopAudio();
        }
        if (this.player != null) {
            this.player.release();
        }
        if (!this.isAutoPlayEnabled) {
            this.audioBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.speech));
        }
        if (!this.isPlaying) {
            if (this.progress.getSecondaryProgress() == this.progress.getProgress() || SkipCurrent) {
                if (this.prayersCursor.moveToNext()) {
                    this.currentRepetition = 1;
                    Utils.setText(this.txvPrayers, this.prayersCursor.getString(this.prayersCursor.getColumnIndex("PRA_TEXT")));
                    this.txvPrayers.setTag(this.prayersCursor.getString(this.prayersCursor.getColumnIndex("PRA_ID")));
                    this.repetition = this.prayersCursor.getInt(this.prayersCursor.getColumnIndex("PRA_REPETITION"));
                    this.txvPrayersRepitition.setText(String.valueOf(this.currentRepetition) + "/" + this.repetition);
                    if (SkipCurrent) {
                        this.progress.incrementProgressBy(this.progress.getSecondaryProgress() - this.progress.getProgress());
                        this.progressList.push(new Progress((this.progress.getProgress() + this.progress.getSecondaryProgress()) - this.progress.getProgress(), this.progress.getSecondaryProgress()));
                    } else {
                        this.progressList.push(new Progress(this.progress.getProgress(), this.progress.getSecondaryProgress()));
                    }
                    this.progress.incrementSecondaryProgressBy(this.repetition);
                    this.progress.incrementProgressBy(1);
                    if (this.prayersCursor.getString(this.prayersCursor.getColumnIndex("PRA_PREFE")).trim().equals(XmlPullParser.NO_NAMESPACE)) {
                        this.imgbtnPrefer.setVisibility(8);
                    } else {
                        this.imgbtnPrefer.setVisibility(0);
                    }
                    this.audioBtn.setTag(this.prayersCursor.getString(this.prayersCursor.getColumnIndex("PRA_AUD")));
                    if (this.isAutoPlayEnabled) {
                        playAudio();
                    } else {
                        playSound(Sounds.PAGEFLIP);
                    }
                    vibrateDevice();
                    this.mFlipper.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
                    this.mFlipper.startFlipping();
                    this.mFlipper.stopFlipping();
                } else {
                    this.timer.stop();
                    Azkar_DB_Adapter.open();
                    updateCountAndTime();
                    Azkar_DB_Adapter.close();
                    finish();
                }
            } else {
                this.progress.incrementProgressBy(1);
                this.currentRepetition++;
                this.txvPrayersRepitition.setText(String.valueOf(this.currentRepetition) + "/" + this.repetition);
                if (this.isAutoPlayEnabled) {
                    playAudio();
                } else {
                    playSound(Sounds.CUSROM_SOUND);
                }
                this.mFlipper.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
                this.mFlipper.startFlipping();
                this.mFlipper.stopFlipping();
            }
            setEnabledBackButton(true);
        }
    }

    private void updateCountAndTime() {
        Azkar_DB_Adapter.increment_Categories_Count_Time(this.timer.getSeconds(), this.categoryId);
    }

    private void initializeTextViews() {
        this.txvPrayers = (TextView) findViewById(R.id.tvPrayerTxt);
        this.txvPrayerTitle = (TextView) findViewById(R.id.tvPrayerTitle);
        this.txvPrayers.setAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
        SharedPreferences pref = getSharedPreferences(ApplicationConstants.APPLICATION_PREFERENCES, 0);
        int prayerFontSize = pref.getInt(ApplicationConstants.PRAYERS_TEXT_FONT_SIZE, 17);
        this.txvPrayers.setTextSize(prayerFontSize);
        this.txvPrayerTitle.setTextSize(19.0f);
        this.txvPrayers.setTextSize(prayerFontSize);
        this.txvPrayerTitle.setTextSize(19.0f);
        final ScrollView scroll = (ScrollView) findViewById(R.id.scrollviewText);
        scroll.setOnTouchListener(new View.OnTouchListener() { // from class: com.dot.azkari.Prayers.16
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if (Prayers.this.gestureDetector.onTouchEvent(arg1)) {
                    return true;
                }
                return scroll.onTouchEvent(arg1);
            }
        });
        String prayerTitle = Azkar_DB_Adapter.select_category_name_by_category_ID(getIntent().getExtras().getString("CAT_ID"));
        Utils.setText(this.txvPrayerTitle, prayerTitle);
    }

    private void initializeToggleBtn() {
        ToggleButton tbtAutoPlay = (ToggleButton) findViewById(R.id.tbtAutoPlay);
        Utils.setText(tbtAutoPlay, getString(R.string.stopAutoPlay), getString(R.string.startAutoPlay));
    }

    private void initializeHandlers() {
        initializeNativeShareListener();
        initializeFacebookListener();
        initializeTwitterListener();
        initializeControlListener();
        initializePrayerPreferListener();
        initializeBackButtonHandler();
        initializeNextButtonHandler();
        initializeVibrationAndSoundHandler();
    }

    private void initializeNativeShareListener() {
        ImageButton imgbtnNativeShareShare = (ImageButton) findViewById(R.id.imgbtnNativeShare);
        imgbtnNativeShareShare.setOnClickListener(new View.OnClickListener() { // from class: com.dot.azkari.Prayers.17
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                try {
                    Intent sharingIntent = new Intent("android.intent.action.SEND");
                    sharingIntent.setType("text/plain");
                    String shareBody = Prayers.this.txvPrayers.getText().toString();
                    sharingIntent.putExtra("android.intent.extra.SUBJECT", String.valueOf(Prayers.this.getResources().getString(R.string.appNamedata)) + ", " + Prayers.this.txvPrayerTitle.getText().toString());
                    sharingIntent.putExtra("android.intent.extra.TEXT", shareBody);
                    Prayers.this.startActivity(Intent.createChooser(sharingIntent, Prayers.this.getResources().getString(R.string.shareingVia)));
                    Prayers.this.wasPosted = true;
                } catch (Exception e) {
                    if (!Utils.isOnline(Prayers.this)) {
                        Prayers.this.wasPosted = false;
                        Prayers.this.applicationHandler.post(Prayers.this.notificationRunnable);
                    }
                }
            }
        });
    }

    private void initializePrayerPreferListener() {
        this.imgbtnPrefer = (ImageButton) findViewById(R.id.imgbtnPrayerPrefer);
        this.imgbtnPrefer.setOnClickListener(new View.OnClickListener() { // from class: com.dot.azkari.Prayers.18
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Prayers.this.showDialog(2);
            }
        });
    }

    @Override // android.app.Activity
    protected void onPause() {
        if (isPlayingAudio && !this.isAutoPlayEnabled) {
            this.player.pause();
            this.timer.stop();
            this.mustShowDialog = true;
        } else if (!isPlayingAudio && !this.isAutoPlayEnabled) {
            this.timer.stop();
            this.mustShowDialog = true;
        } else if (isPlayingAudio && this.isAutoPlayEnabled) {
            this.timer.stop();
            this.applicationPauseDate = new Date();
        }
        super.onPause();
    }

    @Override // android.app.Activity
    protected void onResume() {
        if (this.isAutoPlayEnabled && this.applicationPauseDate != null && !this.pauseDialogShown) {
            if (!this.callReceived) {
                this.applicationResumeDate = new Date();
            }
            long totalTime = this.applicationResumeDate.getTime() - this.applicationPauseDate.getTime();
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(totalTime);
            long milesDiff = totalTime + (Long.parseLong(this.timer.getSeconds()) * 1000);
            this.timer.setTime(Long.valueOf(milesDiff));
            c.setTimeInMillis(this.applicationResumeDate.getTime());
            c.setTimeInMillis(this.applicationPauseDate.getTime());
            c.setTimeInMillis(milesDiff);
        }
        if (this.mustShowDialog.booleanValue()) {
            showDialog(1);
            this.mustShowDialog = false;
        }
        if (!this.pauseDialogShown) {
            this.timer.start();
        }
        this.callReceived = false;
        super.onResume();
    }

    private void initializeControlListener() {
        ImageButton imgbtnControl = (ImageButton) findViewById(R.id.imgbtnControl);
        imgbtnControl.setOnClickListener(new View.OnClickListener() { // from class: com.dot.azkari.Prayers.19
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Prayers.this.timer.stop();
                if (Prayers.isPlayingAudio) {
                    Prayers.this.player.pause();
                }
                Prayers.this.showDialog(1);
            }
        });
    }

    @Override // android.app.Activity
    protected Dialog onCreateDialog(int id) {
        return getDialog(id, this.txvPrayers.getTag().toString(), this.timer);
    }

    private void initializeTwitterListener() {
    }

    private void initializeFacebookListener() {
    }

    public void postOnWall(String msg) {
        // Obsolete Facebook Graph API posting removed; native sharing is used.
    }

    private void startTimer() {
        this.chrone = (Chronometer) findViewById(R.id.ChronTimer);
        Utils.setTypeFace(this.chrone);
        this.timer = new Timer(this.chrone);
        this.timer.start();
    }

    public AlertDialog getDialog(int dialogId, String prayerID, final Timer timer) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService("layout_inflater");
        switch (dialogId) {
            case 1:
                this.pauseDialogShown = true;
                View layout = layoutInflater.inflate(R.layout.pause_dialog, (ViewGroup) findViewById(R.id.pauseRoot));
                ImageView imgPlay = (ImageView) layout.findViewById(R.id.imgPlay);
                imgPlay.setOnClickListener(new View.OnClickListener() { // from class: com.dot.azkari.Prayers.20
                    @Override // android.view.View.OnClickListener
                    public void onClick(View arg0) {
                        Prayers.this.removeDialog(1);
                        Prayers.this.pauseDialogShown = false;
                        if (Prayers.isPlayingAudio) {
                            Prayers.this.player.start();
                        }
                        timer.start();
                    }
                });
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(layout);
                builder.setTitle(XmlPullParser.NO_NAMESPACE);
                AlertDialog pauseDialog = builder.create();
                pauseDialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.dot.azkari.Prayers.21
                    @Override // android.content.DialogInterface.OnCancelListener
                    public void onCancel(DialogInterface dialog) {
                        Prayers.this.onBackPressed();
                    }
                });
                pauseDialog.setCanceledOnTouchOutside(false);
                return pauseDialog;
            case 2:
                View layout2 = layoutInflater.inflate(R.layout.prefer_dialog, (ViewGroup) findViewById(R.id.preferRoot));
                Button btnClose = (Button) layout2.findViewById(R.id.btnPreferClose);
                Utils.setText(btnClose, getString(R.string.back));
                TextView tvTitle = (TextView) layout2.findViewById(R.id.tvPreferTitle);
                Utils.setText(tvTitle, getString(R.string.preferDialogTitle));
                TextView tvPreTxt = (TextView) layout2.findViewById(R.id.tvPreferTxt);
                Azkar_DB_Adapter.open();
                Utils.setText(tvPreTxt, Azkar_DB_Adapter.select_prayer_prefer_by_prayerId(prayerID));
                Azkar_DB_Adapter.close();
                if (ApplicationConstants.APPLICATION_LANGUAGE_DIRECTION == ApplicationConstants.Direction.RTL) {
                    tvPreTxt.setGravity(5);
                } else if (ApplicationConstants.APPLICATION_LANGUAGE_DIRECTION == ApplicationConstants.Direction.LTR) {
                    tvPreTxt.setGravity(3);
                }
                btnClose.setOnClickListener(new View.OnClickListener() { // from class: com.dot.azkari.Prayers.22
                    @Override // android.view.View.OnClickListener
                    public void onClick(View arg0) {
                        Prayers.this.removeDialog(2);
                    }
                });
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setView(layout2);
                builder2.setTitle(XmlPullParser.NO_NAMESPACE);
                builder2.setCancelable(true);
                AlertDialog preferDialog = builder2.create();
                preferDialog.getWindow().setLayout(-1, -2);
                preferDialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.dot.azkari.Prayers.23
                    @Override // android.content.DialogInterface.OnCancelListener
                    public void onCancel(DialogInterface dialog) {
                        Prayers.this.removeDialog(2);
                    }
                });
                preferDialog.setCanceledOnTouchOutside(false);
                return preferDialog;
            default:
                return null;
        }
    }

    public void loadPreviousPrayer() {
        if (this.progressList.size() != 0) {
            if (isPlayingAudio) {
                stopAudio();
            }
            if (!this.isAutoPlayEnabled) {
                this.audioBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.speech));
            }
            Progress prog = this.progressList.pop();
            this.progress.setProgress(prog.getPrimaryValue());
            this.progress.setSecondaryProgress(prog.getSecondaryValue());
            if (this.prayersCursor.moveToPrevious()) {
                Utils.setText(this.txvPrayers, this.prayersCursor.getString(this.prayersCursor.getColumnIndex("PRA_TEXT")));
                this.txvPrayers.setTag(this.prayersCursor.getString(this.prayersCursor.getColumnIndex("PRA_ID")));
                this.txvPrayersRepitition.setText(String.valueOf(this.prayersCursor.getString(this.prayersCursor.getColumnIndex("PRA_REPETITION"))) + "/" + this.prayersCursor.getInt(this.prayersCursor.getColumnIndex("PRA_REPETITION")));
                this.audioBtn.setTag(this.prayersCursor.getString(this.prayersCursor.getColumnIndex("PRA_AUD")));
                if (this.prayersCursor.getString(this.prayersCursor.getColumnIndex("PRA_PREFE")).trim().equals(XmlPullParser.NO_NAMESPACE)) {
                    this.imgbtnPrefer.setVisibility(8);
                } else {
                    this.imgbtnPrefer.setVisibility(0);
                }
                if (this.isAutoPlayEnabled) {
                    playAudio();
                }
                this.currentRepetition = 1;
            }
            if (!this.isAutoPlayEnabled) {
                playSound(Sounds.PAGEFLIP);
                return;
            }
            return;
        }
        setEnabledBackButton(false);
    }

    private void setEnabledBackButton(Boolean isEnabled) {
        ImageButton imgbtnBack = (ImageButton) findViewById(R.id.imgbtnBack);
        imgbtnBack.setEnabled(isEnabled.booleanValue());
    }

    private void vibrateDevice() {
        if (this.canVibrate.booleanValue()) {
            this.vibrator.vibrate(500L);
        }
    }

    @Override // android.app.Activity
    public void finish() {
        super.finish();
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        if (isPlayingAudio) {
            stopAudio();
        }
        if (this.player != null) {
            this.player.release();
        }
        this.prayersCursor = null;
        super.onDestroy();
    }

    @Override // android.app.Activity
    protected void onStop() {
        super.onStop();
    }

    private String getShareMessage(String id, String msg, int Limit, boolean fromTwitter) {
        String linkURL;
        int Limit2 = Limit - 47;
        if (ApplicationConstants.APPLICATION_LANGUAGE_ID == 1) {
            linkURL = ApplicationConstants.CONTENT_LINK_AR;
        } else {
            linkURL = ApplicationConstants.CONTENT_LINK_EN;
        }
        if (msg.length() < Limit2) {
            return String.valueOf(msg) + "\n" + linkURL + "?id=" + id.toString() + "&c=3";
        }
        StringBuilder sb = new StringBuilder();
        Boolean hasLines = false;
        if (msg.contains("*")) {
            String[] prayers = msg.split("\\*");
            for (String prayer : prayers) {
                if (sb.toString().length() + prayer.length() + 1 < Limit2) {
                    sb.append(prayer);
                    sb.append("*");
                    hasLines = true;
                } else {
                    sb.append("\n...\n");
                    break;
                }
            }
        }
        if (msg.contains(" ") && !hasLines.booleanValue()) {
            sb = new StringBuilder();
            String[] prayers2 = msg.split(" ");
            for (String prayer2 : prayers2) {
                if (sb.length() + prayer2.length() + 1 < Limit2) {
                    sb.append(prayer2);
                    sb.append(" ");
                } else {
                    sb.append("\n...\n");
                    break;
                }
            }
        }
        String returnedMessage = sb.toString();
        if (fromTwitter) {
            int zekerId = Integer.parseInt(id);
            if (zekerId == 251) {
                returnedMessage = returnedMessage.replace("wahuwa AAala kulli shay-in", XmlPullParser.NO_NAMESPACE);
            } else if (zekerId == 277) {
                returnedMessage = returnedMessage.replace("thumma istawa", XmlPullParser.NO_NAMESPACE);
            }
            if (zekerId == 47 || zekerId == 276 || zekerId == 242 || zekerId == 260 || zekerId == 281 || zekerId == 283 || zekerId == 189 || zekerId == 190) {
                returnedMessage = new String("Azkar Al-Muslim...");
            }
        }
        return String.valueOf(returnedMessage) + linkURL + "?id=" + id.toString() + "&c=3";
    }

    @Override // android.app.Activity
    public boolean onTouchEvent(MotionEvent event) {
        return this.gestureDetector.onTouchEvent(event);
    }
}
