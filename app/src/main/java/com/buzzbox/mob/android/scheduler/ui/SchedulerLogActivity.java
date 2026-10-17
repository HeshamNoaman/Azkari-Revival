package com.buzzbox.mob.android.scheduler.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.buzzbox.mob.android.scheduler.Logger;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class SchedulerLogActivity extends Activity {
    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("BuzzBox Sdk Scheduler Log");
        List<Logger.Entry> entries = Logger.loadLog(this);
        final StringBuilder sb = new StringBuilder();
        for (Logger.Entry e : entries) {
            sb.append(e.toString()).append("\n");
        }
        ViewGroup.LayoutParams paramS = new LinearLayout.LayoutParams(-1, -2, 1.0f);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(1);
        ScrollView scroll = new ScrollView(this);
        scroll.setLayoutParams(paramS);
        TextView text = new TextView(this);
        text.setText("Scheduler powered by BuzzBox Sdk 0.6/Milano:\n\n" + sb.toString());
        text.setPadding(3, 3, 3, 3);
        scroll.addView(text);
        Button send = new Button(this);
        send.setText("Send");
        send.setOnClickListener(new View.OnClickListener() { // from class: com.buzzbox.mob.android.scheduler.ui.SchedulerLogActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Intent sendMailIntent = new Intent("android.intent.action.SEND");
                sendMailIntent.putExtra("android.intent.extra.SUBJECT", "Log");
                sendMailIntent.putExtra("android.intent.extra.TEXT", sb.toString());
                sendMailIntent.setType("text/plain");
                SchedulerLogActivity.this.startActivity(Intent.createChooser(sendMailIntent, "Send Log"));
            }
        });
        linearLayout.addView(send);
        linearLayout.addView(scroll);
        setContentView(linearLayout);
    }
}
