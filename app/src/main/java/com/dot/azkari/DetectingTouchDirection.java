package com.dot.azkari;

import android.view.MotionEvent;
import android.view.View;

/* JADX INFO: loaded from: classes.dex */
public class DetectingTouchDirection implements View.OnTouchListener {
    static final int MIN_DISTANCE = 100;
    static final String logTag = "ActivitySwipeDetector";
    private Prayers activity;
    private float downX;
    private float downY;
    private float upX;
    private float upY;

    public DetectingTouchDirection(Prayers activity) {
        this.activity = activity;
    }

    public void onRightToLeftSwipe(View view) {
        this.activity.loadNextPrayer(false);
    }

    public void onLeftToRightSwipe(View view) {
        this.activity.loadPreviousPrayer();
    }

    public void onTopToBottomSwipe(View view) {
    }

    public void onBottomToTopSwipe(View view) {
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case 0:
                this.downX = event.getX();
                this.downY = event.getY();
                return true;
            case 1:
                this.upX = event.getX();
                this.upY = event.getY();
                float deltaX = this.downX - this.upX;
                float deltaY = this.downY - this.upY;
                if (Math.abs(deltaX) <= 100.0f) {
                    return false;
                }
                if (deltaX < 0.0f) {
                    onLeftToRightSwipe(view);
                    return true;
                }
                if (deltaX > 0.0f) {
                    onRightToLeftSwipe(view);
                    return true;
                }
                if (Math.abs(deltaY) <= 100.0f) {
                    return false;
                }
                if (deltaY < 0.0f) {
                    onTopToBottomSwipe(view);
                    return true;
                }
                if (deltaY <= 0.0f) {
                    return true;
                }
                onBottomToTopSwipe(view);
                return true;
            default:
                return false;
        }
    }
}
