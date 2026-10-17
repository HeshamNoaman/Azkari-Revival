package com.dot.azkari;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/* JADX INFO: loaded from: classes.dex */
public class AboutActivity extends Activity {
    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us);
        TextView txv = (TextView) findViewById(R.id.txv_aboutUs_header);
        Utils.setText(txv, getResources().getString(R.string.about_Option_Item));
        TextView txv2 = (TextView) findViewById(R.id.txv_aboutUs_body);
        Utils.setText(txv2, getResources().getString(R.string.about_us_body));
        TextView txv_link = (TextView) findViewById(R.id.txv_aboutUs_Link);
        Utils.setText(txv_link, getResources().getString(R.string.about_us_Link));
        TextView txvabout_developer = (TextView) findViewById(R.id.txv_about_developer);
        Utils.setText(txvabout_developer, getResources().getString(R.string.about_developer));
        TextView txv_Developer_Link = (TextView) findViewById(R.id.txv_developer_Link);
        Utils.setText(txv_Developer_Link, getResources().getString(R.string.about_developer_Link));
        TextView txvCopy = (TextView) findViewById(R.id.txvRights);
        Utils.setText(txvCopy, getResources().getString(R.string.copy_rights));
        ImageView imgFb = (ImageView) findViewById(R.id.imgFb);
        ImageView imgYou = (ImageView) findViewById(R.id.imgYouTube);
        ImageView imgTwitter = (ImageView) findViewById(R.id.imgTwitter);
        imgFb.setOnClickListener(new View.OnClickListener() { // from class: com.dot.azkari.AboutActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                AboutActivity.this.openWebURL("http://www.facebook.com/pages/Azkari/248467215177460");
            }
        });
        imgYou.setOnClickListener(new View.OnClickListener() { // from class: com.dot.azkari.AboutActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                AboutActivity.this.openWebURL("http://youtu.be/m65R6vvsM_k");
            }
        });
        imgTwitter.setOnClickListener(new View.OnClickListener() { // from class: com.dot.azkari.AboutActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                AboutActivity.this.openWebURL("http://twitter.com/Azkari_MobApp");
            }
        });
    }

    @Override // android.app.Activity
    protected void onResume() {
        Utils.setLocale(getBaseContext());
        super.onResume();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openWebURL(String inURL) {
        Intent browse = new Intent("android.intent.action.VIEW", Uri.parse(inURL));
        startActivity(browse);
    }
}
