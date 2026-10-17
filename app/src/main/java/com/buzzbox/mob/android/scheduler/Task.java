package com.buzzbox.mob.android.scheduler;

import android.content.ContextWrapper;

/* JADX INFO: loaded from: classes.dex */
public interface Task {
    TaskResult doWork(ContextWrapper contextWrapper);

    String getId();

    String getTitle();
}
