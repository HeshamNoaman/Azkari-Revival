package com.arttech.custom.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.dot.azkari.R;
import com.dot.azkari.Utils;
import com.dot.constants.ApplicationConstants;

/* JADX INFO: loaded from: classes.dex */
public class CustomUpdateDialog extends Activity {
    private String currentVersion;
    private String newVersion;

    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        if (ApplicationConstants.APPLICATION_LANGUAGE_ID == 1) {
            setContentView(R.layout.update_arabic);
        } else {
            setContentView(R.layout.update_eng);
        }
        initializeVariablesFromExtras();
        setVeriableAndHandler();
    }

    private void setVeriableAndHandler() {
        TextView txtHeader = (TextView) findViewById(R.id.updateString);
        TextView txtdata = (TextView) findViewById(R.id.updatedata);
        TextView txtAppName = (TextView) findViewById(R.id.appName);
        TextView txtAppNameText = (TextView) findViewById(R.id.appNametext);
        TextView txtCurrentVersion = (TextView) findViewById(R.id.installedversion);
        TextView txtCurrentVersionText = (TextView) findViewById(R.id.installedVersion);
        TextView txtLastVersion = (TextView) findViewById(R.id.updateverion);
        TextView txtLastVersionText = (TextView) findViewById(R.id.updateVerion);
        Utils.setText(txtHeader, getResources().getString(R.string.updateText));
        Utils.setText(txtdata, getResources().getString(R.string.updatedata));
        Utils.setText(txtAppName, getResources().getString(R.string.appName));
        Utils.setText(txtAppNameText, " " + getResources().getString(R.string.appNamedata));
        Utils.setText(txtCurrentVersion, getResources().getString(R.string.installedversion));
        Utils.setText(txtCurrentVersionText, " " + this.currentVersion);
        Utils.setText(txtLastVersion, getResources().getString(R.string.updateverion));
        Utils.setText(txtLastVersionText, " " + this.newVersion);
        Button btnDownloadNow = (Button) findViewById(R.id.btnDownloadNow);
        Button btnDownloadLater = (Button) findViewById(R.id.btnDownloadLater);
        Utils.setText(btnDownloadNow, getResources().getString(R.string.btnDownloadNow));
        Utils.setText(btnDownloadLater, getResources().getString(R.string.btnDownloadLater));
        btnDownloadLater.setOnClickListener(new View.OnClickListener() { // from class: com.arttech.custom.dialogs.CustomUpdateDialog.1
            @Override // android.view.View.OnClickListener
            public void onClick(View arg) {
                CustomUpdateDialog.this.finish();
            }
        });
        btnDownloadNow.setOnClickListener(new View.OnClickListener() { // from class: com.arttech.custom.dialogs.CustomUpdateDialog.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                CustomUpdateDialog.this.finish();
                try {
                    Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=com.dot.azkari"));
                    CustomUpdateDialog.this.startActivity(browserIntent);
                } catch (Exception e) {
                    Utils.AzkariLog("Exception : ", new StringBuilder(String.valueOf(e.toString())).toString());
                }
            }
        });
    }

    private void initializeVariablesFromExtras() {
        Bundle extras = getIntent().getExtras();
        this.currentVersion = extras.getString("currentversion");
        this.newVersion = extras.getString("newversion");
    }
}
