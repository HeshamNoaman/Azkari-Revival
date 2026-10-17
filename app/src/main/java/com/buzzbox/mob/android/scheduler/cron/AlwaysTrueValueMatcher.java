package com.buzzbox.mob.android.scheduler.cron;

/* JADX INFO: loaded from: classes.dex */
class AlwaysTrueValueMatcher implements ValueMatcher {
    AlwaysTrueValueMatcher() {
    }

    @Override // com.buzzbox.mob.android.scheduler.cron.ValueMatcher
    public boolean match(int value) {
        return true;
    }

    @Override // com.buzzbox.mob.android.scheduler.cron.ValueMatcher
    public String asString() {
        return "*";
    }
}
