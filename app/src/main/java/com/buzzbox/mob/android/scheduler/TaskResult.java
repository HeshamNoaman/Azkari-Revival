package com.buzzbox.mob.android.scheduler;

import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class TaskResult {
    int status;
    String statusMessage;
    List<NotificationMessage> messages = new ArrayList();
    public int fixRetryMillis = 300000;
    public int exponentialRetryMillis = 60000;
    RetryPolicy retry = RetryPolicy.WAIT_EXPONENTIAL;

    public enum RetryPolicy {
        CONTINUE_NO_RETRY,
        WAIT_FIX,
        WAIT_EXPONENTIAL,
        ABORT;

        /* JADX INFO: renamed from: values, reason: to resolve conflict with enum method */
        public static RetryPolicy[] valuesCustom() {
            RetryPolicy[] retryPolicyArrValuesCustom = values();
            int length = retryPolicyArrValuesCustom.length;
            RetryPolicy[] retryPolicyArr = new RetryPolicy[length];
            System.arraycopy(retryPolicyArrValuesCustom, 0, retryPolicyArr, 0, length);
            return retryPolicyArr;
        }
    }

    public void setRetry(RetryPolicy retry) {
        this.retry = retry;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void addMessage(NotificationMessage msg) {
        this.messages.add(msg);
    }

    public List<NotificationMessage> getMessages() {
        return this.messages;
    }

    public RetryPolicy getRetry() {
        return this.retry;
    }

    public boolean isError() {
        return this.status != 0;
    }

    public int countTotal() {
        int tot = 0;
        for (NotificationMessage m : this.messages) {
            tot += m.number;
        }
        return tot;
    }
}
