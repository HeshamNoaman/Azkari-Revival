package com.dot.azkari;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.dot.constants.ApplicationConstants;

/* JADX INFO: loaded from: classes.dex */
public class HelpViewActivity extends Activity {
    private static String helpTopic;

    public static String getHelpTopic() {
        return helpTopic;
    }

    public static void setHelpTopic(String helpTopic2) {
        helpTopic = helpTopic2;
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_view);
        initializeHeader();
        initializeHelpText();
    }

    private void initializeHeader() {
        TextView txv = (TextView) findViewById(R.id.txv_helpViewHeader);
        Utils.setText(txv, helpTopic);
    }

    private void initializeHelpText() {
        TextView txv = (TextView) findViewById(R.id.txv_helpViewText);
        txv.setTextSize(15.0f);
        ImageView img1 = (ImageView) findViewById(R.id.imv_helpViewSnap1);
        ImageView img2 = (ImageView) findViewById(R.id.imv_helpViewSnap2);
        if (ApplicationConstants.APPLICATION_LANGUAGE_DIRECTION == ApplicationConstants.Direction.RTL) {
            txv.setGravity(5);
        } else if (ApplicationConstants.APPLICATION_LANGUAGE_DIRECTION == ApplicationConstants.Direction.LTR) {
            txv.setGravity(3);
        }
        if (helpTopic.equals(getResources().getString(R.string.help_menu_item_main))) {
            Utils.setText(txv, getResources().getString(R.string.help_main));
            if (ApplicationConstants.APPLICATION_LANGUAGE_ID == 1) {
                img1.setImageDrawable(getResources().getDrawable(R.drawable.main_screen_1_arabic));
                img2.setImageDrawable(getResources().getDrawable(R.drawable.main_screen_2_arabic));
                return;
            } else {
                img1.setImageDrawable(getResources().getDrawable(R.drawable.main_screen_1_english));
                img2.setImageDrawable(getResources().getDrawable(R.drawable.main_screen_2_english));
                return;
            }
        }
        if (helpTopic.equals(getResources().getString(R.string.help_menu_item_widget))) {
            Utils.setText(txv, getResources().getString(R.string.help_widget));
            if (ApplicationConstants.APPLICATION_LANGUAGE_ID == 1) {
                img1.setImageDrawable(getResources().getDrawable(R.drawable.widget_1_arabic));
                img2.setVisibility(8);
                return;
            } else {
                img1.setImageDrawable(getResources().getDrawable(R.drawable.widget_1_english));
                img2.setVisibility(8);
                return;
            }
        }
        if (helpTopic.equals(getResources().getString(R.string.help_menu_item_tsbeeh))) {
            Utils.setText(txv, getResources().getString(R.string.help_tsbeeh));
            if (ApplicationConstants.APPLICATION_LANGUAGE_ID == 1) {
                img1.setImageDrawable(getResources().getDrawable(R.drawable.tsbeha_1_arabic));
                img2.setImageDrawable(getResources().getDrawable(R.drawable.tsbeha_2_arabic));
                return;
            } else {
                img1.setImageDrawable(getResources().getDrawable(R.drawable.tsbeha_1_english));
                img2.setImageDrawable(getResources().getDrawable(R.drawable.tsbeha_2_english));
                return;
            }
        }
        if (helpTopic.equals(getResources().getString(R.string.help_menu_item_azkar))) {
            Utils.setText(txv, getResources().getString(R.string.help_azkar));
            if (ApplicationConstants.APPLICATION_LANGUAGE_ID == 1) {
                img1.setImageDrawable(getResources().getDrawable(R.drawable.aladkar_1_arabic));
                img2.setImageDrawable(getResources().getDrawable(R.drawable.aladkar_2_arabic));
                return;
            } else {
                img1.setImageDrawable(getResources().getDrawable(R.drawable.aladkar_1_english));
                img2.setImageDrawable(getResources().getDrawable(R.drawable.aladkar_2_english));
                return;
            }
        }
        if (helpTopic.equals(getResources().getString(R.string.help_menu_item_setting))) {
            Utils.setText(txv, getResources().getString(R.string.help_setting));
            if (ApplicationConstants.APPLICATION_LANGUAGE_ID == 1) {
                img1.setImageDrawable(getResources().getDrawable(R.drawable.setting_1_arabic));
                img2.setVisibility(8);
                return;
            } else {
                img1.setImageDrawable(getResources().getDrawable(R.drawable.setting_1_english));
                img2.setVisibility(8);
                return;
            }
        }
        if (helpTopic.equals(getResources().getString(R.string.help_menu_addTasbeeha))) {
            Utils.setText(txv, getResources().getString(R.string.help_addTasbeeha));
            if (ApplicationConstants.APPLICATION_LANGUAGE_ID == 1) {
                img1.setImageDrawable(getResources().getDrawable(R.drawable.add_tsbeha_1_arabic));
                img2.setVisibility(8);
                return;
            } else {
                img1.setImageDrawable(getResources().getDrawable(R.drawable.add_tsbeha_1_eglish));
                img2.setVisibility(8);
                return;
            }
        }
        if (helpTopic.equals(getResources().getString(R.string.help_menu_deleteTasbeeha))) {
            Utils.setText(txv, getResources().getString(R.string.help_deleteTasbeeha));
            if (ApplicationConstants.APPLICATION_LANGUAGE_ID == 1) {
                img1.setImageDrawable(getResources().getDrawable(R.drawable.hzf_tsbeha_1_arabic));
                img2.setVisibility(8);
                return;
            } else {
                img1.setImageDrawable(getResources().getDrawable(R.drawable.hzf_tsbeha_1_english));
                img2.setVisibility(8);
                return;
            }
        }
        if (helpTopic.equals(getResources().getString(R.string.help_menu_log))) {
            Utils.setText(txv, getResources().getString(R.string.help_log));
            if (ApplicationConstants.APPLICATION_LANGUAGE_ID == 1) {
                img1.setImageDrawable(getResources().getDrawable(R.drawable.log_1_arabic));
                img2.setImageDrawable(getResources().getDrawable(R.drawable.log_2_arabic));
                return;
            } else {
                img1.setImageDrawable(getResources().getDrawable(R.drawable.log_1_english));
                img2.setImageDrawable(getResources().getDrawable(R.drawable.log_2_english));
                return;
            }
        }
        if (helpTopic.equals(getResources().getString(R.string.notificationsSettings))) {
            Utils.setText(txv, getResources().getString(R.string.help_notifications));
            if (ApplicationConstants.APPLICATION_LANGUAGE_ID == 1) {
                img1.setImageDrawable(getResources().getDrawable(R.drawable.tnbehat_1_arabic));
                img2.setImageDrawable(getResources().getDrawable(R.drawable.tnbehat_2_arabic));
            } else {
                img1.setImageDrawable(getResources().getDrawable(R.drawable.tnbehat_1_english));
                img2.setImageDrawable(getResources().getDrawable(R.drawable.tnbehat_2_english));
            }
        }
    }
}
