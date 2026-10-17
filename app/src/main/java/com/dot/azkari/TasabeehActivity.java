package com.dot.azkari;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.dot.adapter.database.Azkar_DB_Adapter;
import com.dot.constants.ApplicationConstants;
import com.dot.timer.Timer;
import org.xmlpull.v1.XmlPullParser;

/* JADX INFO: loaded from: classes.dex */
public class TasabeehActivity extends Activity implements GestureDetector.OnGestureListener {
    private static final int PAUSE_DIALOGE_ID = 0;
    private static int tasbeehId;
    private String endTimeString;
    private GestureDetector gestureScanner;
    private MediaPlayer mPlayer;
    AnimationDrawable msbhaAnimation;
    private AlertDialog pauseDialog;
    private Timer timer;
    private Vibrator vibrator;
    private static int count = 0;
    private static boolean isMute = false;
    private static boolean isBackFromPause = false;
    private static boolean isTimerStarted = false;
    private boolean isShahedShown = false;
    private boolean isPaused = false;
    private boolean isPlayerDone = true;

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasabeeh);
        Chronometer chrono = (Chronometer) findViewById(R.id.chronometer_tasbeeh);
        Utils.setTypeFace(chrono);
        this.timer = new Timer(chrono);
        this.isShahedShown = false;
        isTimerStarted = false;
        this.isPaused = true;
        isBackFromPause = false;
        this.endTimeString = getResources().getString(R.string.zero_time);
        tasbeehId = Integer.parseInt(getIntent().getStringExtra("Tasbeeh_Id"));
        this.gestureScanner = new GestureDetector(this);
        Azkar_DB_Adapter.Init(this);
        Azkar_DB_Adapter.open();
        initializeTvTsabeehName();
        initializeTasbeehCountAndTime();
        initializePauseButton();
        initilizeCheckBox();
        initilalizeMuteButton();
        initializeMasbahaAnimation();
        View.OnTouchListener touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return TasabeehActivity.this.gestureScanner.onTouchEvent(event);
            }
        };
        View root = findViewById(R.id.relativeLayout1);
        if (root != null) {
            root.setOnTouchListener(touchListener);
        }
        View upper = findViewById(R.id.rlvUpper);
        if (upper != null) {
            upper.setOnTouchListener(touchListener);
        }
        View masbaha = findViewById(R.id.imgvMasbaha);
        if (masbaha != null) {
            masbaha.setOnTouchListener(touchListener);
        }
        if (isBackFromPause) {
            pauseCurrentActivity();
        }
    }

    private void reintializeAll() {
        count = 0;
        this.isShahedShown = false;
        isTimerStarted = false;
        this.isPaused = true;
        isBackFromPause = false;
        this.endTimeString = "00:00:00";
        this.mPlayer = MediaPlayer.create(this, R.raw.tick);
        this.mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // from class: com.dot.azkari.TasabeehActivity.1
            @Override // android.media.MediaPlayer.OnCompletionListener
            public void onCompletion(MediaPlayer arg0) {
                TasabeehActivity.this.isPlayerDone = true;
            }
        });
        initializeTvTsabeehName();
        initializeTasbeehCountAndTime();
        initializeMasbahaAnimation();
        initilizeCheckBox();
        initilalizeMuteButton();
        initializePauseButton();
        Azkar_DB_Adapter.Init(this);
        if (this.pauseDialog != null) {
            this.pauseDialog.dismiss();
        }
        this.vibrator = (Vibrator) getSystemService("vibrator");
        enableAll();
    }

    private void initializeMasbahaAnimation() {
        ImageView movieImg = (ImageView) findViewById(R.id.imgvMasbaha);
        movieImg.setBackgroundResource(R.anim.move_m);
        this.msbhaAnimation = (AnimationDrawable) movieImg.getBackground();
        this.msbhaAnimation.start();
    }

    private void initilalizeMuteButton() {
        Button bt = (Button) findViewById(R.id.btnIsMutable);
        SharedPreferences pref = getSharedPreferences(ApplicationConstants.APPLICATION_PREFERENCES, 0);
        isMute = pref.getInt(ApplicationConstants.SOUND_PREFERENCES, 0) == 0;
        if (!isMute) {
            bt.setBackgroundDrawable(getResources().getDrawable(R.drawable.sound));
        } else {
            bt.setBackgroundDrawable(getResources().getDrawable(R.drawable.mute));
        }
        final Button finalBt = bt;
        final SharedPreferences finalPref = pref;
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                SharedPreferences.Editor edit = finalPref.edit();
                if (TasabeehActivity.isMute) {
                    finalBt.setBackgroundDrawable(TasabeehActivity.this.getResources().getDrawable(R.drawable.sound));
                    TasabeehActivity.isMute = false;
                    edit.putInt(ApplicationConstants.SOUND_PREFERENCES, 1);
                    edit.commit();
                    return;
                }
                finalBt.setBackgroundDrawable(TasabeehActivity.this.getResources().getDrawable(R.drawable.mute));
                TasabeehActivity.isMute = true;
                edit.putInt(ApplicationConstants.SOUND_PREFERENCES, 0);
                edit.commit();
            }
        });
    }

    private void initilizeCheckBox() {
        CheckBox ck = (CheckBox) findViewById(R.id.ckShahed);
        Utils.setText(ck, getResources().getString(R.string.show_shahed));
        ck.setChecked(false);
        ck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.dot.azkari.TasabeehActivity.3
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    TasabeehActivity.this.isShahedShown = true;
                } else {
                    TasabeehActivity.this.isShahedShown = false;
                }
            }
        });
    }

    private void processShahed() {
        if (count != 0 && count % 11 == 0 && this.isShahedShown) {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.shahed_toast, (ViewGroup) findViewById(R.id.toast_layout_root));
            Toast shahedTost = new Toast(getApplicationContext());
            shahedTost.setGravity(80, 0, 50);
            shahedTost.setDuration(0);
            shahedTost.setView(layout);
            shahedTost.show();
            vibrateDevice();
        }
    }

    private void vibrateDevice() {
        SharedPreferences pref = getSharedPreferences(ApplicationConstants.APPLICATION_PREFERENCES, 0);
        boolean isVibrateEnabled = pref.getInt(ApplicationConstants.VIBRATE_PREFERENCES, 0) == 1;
        if (isVibrateEnabled) {
            this.vibrator.vibrate(500L);
        }
    }

    private void startTimer() {
        isTimerStarted = true;
        this.timer.start();
    }

    private void pauseTimer() {
        this.timer.stop();
        this.endTimeString = this.timer.getSeconds();
    }

    private void resumeTimer() {
        this.timer.start();
    }

    private void initializePauseButton() {
        final Button bt = (Button) findViewById(R.id.btPause);
        bt.setBackgroundDrawable(getResources().getDrawable(R.drawable.pause));
        bt.setOnClickListener(new View.OnClickListener() { // from class: com.dot.azkari.TasabeehActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View arg0) {
                if (!TasabeehActivity.this.isPaused) {
                    TasabeehActivity.this.isPaused = true;
                    TasabeehActivity.this.pauseCurrentActivity();
                } else {
                    TasabeehActivity.this.enableAll();
                    bt.setBackgroundDrawable(TasabeehActivity.this.getResources().getDrawable(R.drawable.pause));
                    TasabeehActivity.this.isPaused = false;
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void pauseCurrentActivity() {
        showDialog(0);
        if (this.timer != null) {
            pauseTimer();
        }
        this.isPaused = true;
    }

    @Override // android.app.Activity
    protected Dialog onCreateDialog(int id) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService("layout_inflater");
        switch (id) {
            case 0:
                View layout = layoutInflater.inflate(R.layout.pause_dialog, (ViewGroup) findViewById(R.id.pauseRoot));
                ImageView img = (ImageView) layout.findViewById(R.id.imgPlay);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(layout);
                builder.setTitle(XmlPullParser.NO_NAMESPACE);
                this.pauseDialog = builder.create();
                img.setOnClickListener(new View.OnClickListener() { // from class: com.dot.azkari.TasabeehActivity.5
                    @Override // android.view.View.OnClickListener
                    public void onClick(View arg0) {
                        TasabeehActivity.this.isPaused = false;
                        TasabeehActivity.isBackFromPause = true;
                        TasabeehActivity.this.pauseDialog.dismiss();
                        TasabeehActivity.this.removeDialog(0);
                        TasabeehActivity.this.enableAll();
                    }
                });
                this.pauseDialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.dot.azkari.TasabeehActivity.6
                    @Override // android.content.DialogInterface.OnCancelListener
                    public void onCancel(DialogInterface arg0) {
                        TasabeehActivity.this.onBackPressed();
                    }
                });
                this.pauseDialog.setCanceledOnTouchOutside(false);
                return this.pauseDialog;
            default:
                return super.onCreateDialog(id);
        }
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        super.onBackPressed();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enableAll() {
        this.isPaused = false;
        if (isBackFromPause && isTimerStarted) {
            resumeTimer();
        } else {
            if (this.timer != null) {
                this.timer.reset();
            }
            startTimer();
        }
    }

    private void initializeTvTsabeehName() {
        TextView tv = (TextView) findViewById(R.id.tvTsbeehName);
        Utils.setText(tv, getTasbeehName(tasbeehId));
    }

    private void initializeTasbeehCountAndTime() {
        TextView tv = (TextView) findViewById(R.id.tvCounter);
        Utils.setTypeFace(tv);
        tv.setText(String.valueOf(count));
    }

    private String getTasbeehName(int id) {
        Cursor c = Azkar_DB_Adapter.select_tasbeehName_Categories(id);
        if (!c.moveToFirst()) {
            return XmlPullParser.NO_NAMESPACE;
        }
        String name = c.getString(c.getColumnIndex("CAT_NAME"));
        return name;
    }

    private void playSound() {
        if (this.isPlayerDone) {
            this.mPlayer.start();
            this.isPlayerDone = false;
        }
    }

    private void updateTvTasbeehCount() {
        TextView tv = (TextView) findViewById(R.id.tvCounter);
        tv.setText(String.valueOf(count));
    }

    @Override // android.app.Activity
    protected void onPause() {
        pauseCurrentActivity();
        isBackFromPause = true;
        super.onPause();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        if (isTimerStarted) {
            if ((this.endTimeString == null) | this.endTimeString.equals(XmlPullParser.NO_NAMESPACE)) {
                this.endTimeString = "00:00:00";
            }
            pauseTimer();
            Azkar_DB_Adapter.open();
            Azkar_DB_Adapter.increment_Categories_Count_Time(String.valueOf(count), this.endTimeString, String.valueOf(tasbeehId));
            Azkar_DB_Adapter.close();
        }
        super.onDestroy();
    }

    @Override // android.app.Activity
    protected void onStop() {
        if (this.pauseDialog != null) {
            this.pauseDialog.dismiss();
            removeDialog(0);
        }
        super.onStop();
    }

    public static int getTasbeehId() {
        return tasbeehId;
    }

    public static void setTasbeehId(int tasbeehId2) {
        tasbeehId = tasbeehId2;
    }

    @Override // android.app.Activity
    protected void onRestart() {
        super.onRestart();
    }

    @Override // android.app.Activity
    protected void onStart() {
        if (!isBackFromPause) {
            reintializeAll();
        } else {
            pauseCurrentActivity();
        }
        super.onStart();
    }

    private synchronized void performTasbeehIncrement() {
        if (this.isPaused) {
            return;
        }
        count++;
        android.util.Log.i("TasabeehActivity", "performTasbeehIncrement count=" + count);
        if (!isMute) {
            playSound();
        }
        processShahed();
        if (this.msbhaAnimation != null && this.msbhaAnimation.isRunning()) {
            this.msbhaAnimation.stop();
        }
        ImageView movieImg = (ImageView) findViewById(R.id.imgvMasbaha);
        if (movieImg != null) {
            movieImg.setBackgroundResource(R.anim.move_m);
            this.msbhaAnimation = (AnimationDrawable) movieImg.getBackground();
            if (this.msbhaAnimation != null) {
                this.msbhaAnimation.start();
            }
        }
        updateTvTasbeehCount();
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onDown(MotionEvent arg0) {
        return true;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
        performTasbeehIncrement();
        return true;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public void onLongPress(MotionEvent arg0) {
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override // android.app.Activity
    public boolean onTouchEvent(MotionEvent event) {
        return this.gestureScanner.onTouchEvent(event);
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public void onShowPress(MotionEvent arg0) {
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onSingleTapUp(MotionEvent arg0) {
        performTasbeehIncrement();
        return true;
    }
}
