package com.dot.timer;

import android.widget.Chronometer;
import org.xmlpull.v1.XmlPullParser;

/* JADX INFO: loaded from: classes.dex */
public class Timer {
    private Chronometer chrone;
    private long elapsedTime = 0;
    private String currentTime = XmlPullParser.NO_NAMESPACE;
    private boolean isStarted = false;

    public Timer(Chronometer Chrone) {
        this.chrone = Chrone;
        setChronometerListener();
    }

    private void setChronometerListener() {
        this.chrone.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() { // from class: com.dot.timer.Timer.1
            @Override // android.widget.Chronometer.OnChronometerTickListener
            public void onChronometerTick(Chronometer chronometer) {
                long minutes = ((Timer.this.elapsedTime - Timer.this.chrone.getBase()) / 1000) / 60;
                long seconds = ((Timer.this.elapsedTime - Timer.this.chrone.getBase()) / 1000) % 60;
                Timer.this.currentTime = String.valueOf(minutes) + ":" + seconds;
                chronometer.setText(Timer.this.currentTime);
                Timer.this.elapsedTime += 1000;
            }
        });
    }

    public void start() {
        if (!this.isStarted) {
            this.chrone.setBase(0L);
            this.isStarted = true;
        }
        this.chrone.start();
    }

    public void stop() {
        this.chrone.stop();
        this.chrone.setText(this.currentTime);
    }

    public void reset() {
        this.chrone.stop();
        this.chrone.setText("0:0");
    }

    public void setTime(Long miles) {
        this.elapsedTime = miles.longValue();
    }

    public String getSeconds() {
        Long period = Long.valueOf(this.elapsedTime / 1000);
        return period.toString();
    }
}
