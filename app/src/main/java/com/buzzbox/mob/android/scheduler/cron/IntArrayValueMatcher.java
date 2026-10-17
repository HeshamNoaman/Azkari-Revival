package com.buzzbox.mob.android.scheduler.cron;

import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
class IntArrayValueMatcher implements ValueMatcher {
    String asString;
    private int[] values;

    public IntArrayValueMatcher(String str, ArrayList<Integer> integers) {
        this.asString = str;
        int size = integers.size();
        this.values = new int[size];
        for (int i = 0; i < size; i++) {
            try {
                this.values[i] = integers.get(i).intValue();
            } catch (Exception e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
    }

    @Override // com.buzzbox.mob.android.scheduler.cron.ValueMatcher
    public boolean match(int value) {
        for (int i = 0; i < this.values.length; i++) {
            if (this.values[i] == value) {
                return true;
            }
        }
        return false;
    }

    @Override // com.buzzbox.mob.android.scheduler.cron.ValueMatcher
    public String asString() {
        return this.asString;
    }
}
