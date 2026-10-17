package com.dot.azkari;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.arttech.custom.dialogs.CustomSpinnerDialog;
import com.arttech.custom.dialogs.CustomSpinnerDialogExtras;
import com.dot.adapter.database.Azkar_DB_Adapter;
import com.dot.arabic.ArabicUtilities;
import com.dot.constants.ApplicationConstants;
import java.io.IOException;
import java.lang.reflect.Array;
import org.xmlpull.v1.XmlPullParser;

/* JADX INFO: loaded from: classes.dex */
public class SettingsActivity extends Activity {
    private static int soundSettings;
    private static int vibrateSettings;
    private String[] appLanguages;
    private String[][] appLanguagesData;
    private boolean isFirstTime;
    private settingsMenuAdapter settingsAdapter;
    private ListView settingsList;
    String[] fontOriginalName = ApplicationConstants.SYSTEM_FONTS;
    private int itemPosition = 0;
    private Boolean isDialogClicked = false;

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        this.isFirstTime = true;
        Utils.setCurrentLocaleFromShared(this);
        initializeSettingsHeader();
        this.settingsList = (ListView) findViewById(R.id.lsv_settingMenu);
        settingItems();
    }

    private void settingItems() {
        String[] data_dependingOnLanguage;
        if (ApplicationConstants.APPLICATION_LANGUAGE_ID == 1) {
            data_dependingOnLanguage = getResources().getStringArray(R.array.ArabicSettings);
        } else {
            data_dependingOnLanguage = getResources().getStringArray(R.array.EnglishSettings);
        }
        this.settingsAdapter = new settingsMenuAdapter(data_dependingOnLanguage);
        this.settingsList.setAdapter((ListAdapter) this.settingsAdapter);
    }

    private void initializeSettingsHeader() {
        TextView txt = (TextView) findViewById(R.id.txv_settingsHeader);
        txt.setTypeface(null);
        Utils.setText(txt, getResources().getString(R.string.settings_Option_Item));
    }

    private class settingsMenuAdapter extends BaseAdapter {
        private String[] data;
        private LayoutInflater inflater;

        public settingsMenuAdapter(String[] data) {
            this.inflater = null;
            this.data = data;
            this.inflater = (LayoutInflater) SettingsActivity.this.getSystemService("layout_inflater");
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this.data.length;
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            return this.data[position];
        }

        @Override // android.widget.Adapter
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override // android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            String dataValue = this.data[position];
            if (!dataValue.equals(SettingsActivity.this.getResources().getString(R.string.settings_menu_item_language))) {
                if (!dataValue.equals(SettingsActivity.this.getResources().getString(R.string.settings_menu_item_vibrate)) && !dataValue.equals(SettingsActivity.this.getResources().getString(R.string.settings_menu_item_sound))) {
                    if (!dataValue.equals(SettingsActivity.this.getResources().getString(R.string.settings_menu_item_font_size))) {
                        if (!dataValue.equals(SettingsActivity.this.getResources().getString(R.string.FontType))) {
                            return convertView;
                        }
                        View v = SettingsActivity.this.initializeFontFace(convertView, this.inflater, dataValue);
                        return v;
                    }
                    View v2 = SettingsActivity.this.initializeFontSettings(convertView, this.inflater, dataValue);
                    return v2;
                }
                View v3 = SettingsActivity.this.initializeSoundVibrateSettings(convertView, this.inflater, dataValue);
                return v3;
            }
            View v4 = SettingsActivity.this.initializeLanguageSettings(convertView, this.inflater, dataValue);
            return v4;
        }
    }

    private void processVibrateSettings(final ImageButton imBtn) {
        final SharedPreferences pref = getSharedPreferences(ApplicationConstants.APPLICATION_PREFERENCES, 0);
        vibrateSettings = pref.getInt(ApplicationConstants.VIBRATE_PREFERENCES, -1);
        if (vibrateSettings != -1) {
            if (vibrateSettings == 1) {
                imBtn.setImageDrawable(getResources().getDrawable(R.drawable.vibrate_on));
            } else {
                imBtn.setImageDrawable(getResources().getDrawable(R.drawable.vibrate_off));
            }
            imBtn.setOnClickListener(new View.OnClickListener() { // from class: com.dot.azkari.SettingsActivity.1
                @Override // android.view.View.OnClickListener
                public void onClick(View arg0) {
                    SharedPreferences.Editor edit = pref.edit();
                    if (SettingsActivity.vibrateSettings != 1) {
                        if (SettingsActivity.vibrateSettings == 0) {
                            edit.putInt(ApplicationConstants.VIBRATE_PREFERENCES, 1);
                            SettingsActivity.vibrateSettings = 1;
                            imBtn.setImageDrawable(SettingsActivity.this.getResources().getDrawable(R.drawable.vibrate_on));
                        }
                    } else {
                        edit.putInt(ApplicationConstants.VIBRATE_PREFERENCES, 0);
                        SettingsActivity.vibrateSettings = 0;
                        imBtn.setImageDrawable(SettingsActivity.this.getResources().getDrawable(R.drawable.vibrate_off));
                    }
                    edit.commit();
                }
            });
        }
    }

    private void processSoundSettings(final ImageButton imBtn) {
        final SharedPreferences pref = getSharedPreferences(ApplicationConstants.APPLICATION_PREFERENCES, 0);
        soundSettings = pref.getInt(ApplicationConstants.SOUND_PREFERENCES, -1);
        if (soundSettings == 1) {
            imBtn.setImageDrawable(getResources().getDrawable(R.drawable.sound));
        } else {
            imBtn.setImageDrawable(getResources().getDrawable(R.drawable.mute));
        }
        imBtn.setOnClickListener(new View.OnClickListener() { // from class: com.dot.azkari.SettingsActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View arg0) {
                SharedPreferences.Editor edit = pref.edit();
                if (SettingsActivity.soundSettings != 1) {
                    if (SettingsActivity.soundSettings == 0) {
                        edit.putInt(ApplicationConstants.SOUND_PREFERENCES, 1);
                        edit.commit();
                        SettingsActivity.soundSettings = 1;
                        imBtn.setImageDrawable(SettingsActivity.this.getResources().getDrawable(R.drawable.sound));
                        return;
                    }
                    return;
                }
                edit.putInt(ApplicationConstants.SOUND_PREFERENCES, 0);
                edit.commit();
                SettingsActivity.soundSettings = 0;
                imBtn.setImageDrawable(SettingsActivity.this.getResources().getDrawable(R.drawable.mute));
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public View initializeSoundVibrateSettings(View v, LayoutInflater inflater, String dataValue) {
        if (ApplicationConstants.APPLICATION_LANGUAGE_DIRECTION == ApplicationConstants.Direction.RTL) {
            v = inflater.inflate(R.layout.settings_menu_item_sound_vibrate_arabic, (ViewGroup) null);
            TextView txv = (TextView) v.findViewById(R.id.txv_soundVibrateSettings);
            ImageButton imb = (ImageButton) v.findViewById(R.id.imb_soundVibrateSettings);
            txv.setTypeface(ApplicationConstants.ARABIC_TYPE_FACE);
            txv.setText(ArabicUtilities.reshape(dataValue));
            if (dataValue.equals(getResources().getString(R.string.settings_menu_item_sound))) {
                processSoundSettings(imb);
            } else if (dataValue.equals(getResources().getString(R.string.settings_menu_item_vibrate))) {
                imb.setImageDrawable(getResources().getDrawable(R.drawable.vibrate_on));
                processVibrateSettings(imb);
            }
        } else if (ApplicationConstants.APPLICATION_LANGUAGE_DIRECTION == ApplicationConstants.Direction.LTR) {
            v = inflater.inflate(R.layout.settings_menu_item_sound_vibrate_english, (ViewGroup) null);
            TextView txv2 = (TextView) v.findViewById(R.id.txv_soundVibrateSettings_english);
            ImageButton imb2 = (ImageButton) v.findViewById(R.id.imb_soundVibrateSettings_english);
            txv2.setText(dataValue);
            if (dataValue.equals(getResources().getString(R.string.settings_menu_item_sound))) {
                processSoundSettings(imb2);
            } else if (dataValue.equals(getResources().getString(R.string.settings_menu_item_vibrate))) {
                imb2.setImageDrawable(getResources().getDrawable(R.drawable.vibrate_on));
                processVibrateSettings(imb2);
            }
        }
        return v;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public View initializeLanguageSettings(View v, LayoutInflater inflater, String dataValue) {
        if (ApplicationConstants.APPLICATION_LANGUAGE_DIRECTION == ApplicationConstants.Direction.RTL) {
            v = inflater.inflate(R.layout.settings_menu_item_language_default_tasbeeha_arabic, (ViewGroup) null);
        } else if (ApplicationConstants.APPLICATION_LANGUAGE_DIRECTION == ApplicationConstants.Direction.LTR) {
            v = inflater.inflate(R.layout.settings_menu_item_language_default_tasbeeha_english, (ViewGroup) null);
        }
        initializeLanguages();
        if (this.isFirstTime) {
            initializeSelectedLanguage();
            this.isFirstTime = false;
        }
        TextView txv = (TextView) v.findViewById(R.id.txv_language_default_tasbeeha);
        Utils.setText(txv, getResources().getString(R.string.settings_menu_item_language));
        Button btn = (Button) v.findViewById(R.id.btn_language_default_tasbeeha);
        btn.setBackgroundResource(android.R.drawable.btn_dropdown);
        btn.setTextSize(13.0f);
        Utils.setText(btn, this.appLanguages[this.itemPosition]);
        btn.setOnClickListener(new View.OnClickListener() { // from class: com.dot.azkari.SettingsActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View arg0) {
                if (!SettingsActivity.this.isDialogClicked.booleanValue()) {
                    SettingsActivity.this.isDialogClicked = true;
                    Intent intent = new Intent(SettingsActivity.this.getApplicationContext(), (Class<?>) CustomSpinnerDialog.class);
                    intent.putExtra(CustomSpinnerDialogExtras.ITEMS, SettingsActivity.this.appLanguages);
                    intent.putExtra(CustomSpinnerDialogExtras.SELECTED_ITEM, SettingsActivity.this.appLanguages[SettingsActivity.this.itemPosition]);
                    intent.putExtra(CustomSpinnerDialogExtras.LANGUAGE, "english");
                    intent.putExtra(CustomSpinnerDialogExtras.DIALOG_TYPE, ApplicationConstants.LANGUAGE_PREFERENCES);
                    SettingsActivity.this.startActivityForResult(intent, 1);
                }
            }
        });
        return v;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public View initializeFontSettings(View v, LayoutInflater inflater, String dataValue) {
        int layoutID;
        String[] font_size_values = getResources().getStringArray(R.array.fonts_size_value_array);
        SharedPreferences pref = getSharedPreferences(ApplicationConstants.APPLICATION_PREFERENCES, 0);
        final int font_size_index = getIndexOfFontSize(font_size_values, pref.getInt(ApplicationConstants.PRAYERS_TEXT_FONT_SIZE, 17));
        final String[] font_size_name = getResources().getStringArray(R.array.fonts_size_array);
        if (ApplicationConstants.APPLICATION_LANGUAGE_DIRECTION == ApplicationConstants.Direction.RTL) {
            layoutID = R.layout.settings_menu_item_prayer_font_size_arabic;
        } else {
            layoutID = R.layout.settings_menu_item_prayer_font_size_englsih;
        }
        View v2 = inflater.inflate(layoutID, (ViewGroup) null);
        TextView txv = (TextView) v2.findViewById(R.id.txv_FontSize);
        Utils.setText(txv, getResources().getString(R.string.settings_menu_item_font_size));
        Button btn = (Button) v2.findViewById(R.id.btn_FontSize);
        btn.setBackgroundResource(android.R.drawable.btn_dropdown);
        btn.setTextSize(13.0f);
        Utils.setText(btn, font_size_name[font_size_index]);
        btn.setOnClickListener(new View.OnClickListener() { // from class: com.dot.azkari.SettingsActivity.5
            @Override // android.view.View.OnClickListener
            public void onClick(View arg0) {
                String Lang;
                if (!SettingsActivity.this.isDialogClicked.booleanValue()) {
                    SettingsActivity.this.isDialogClicked = true;
                    Intent intent = new Intent(SettingsActivity.this.getApplicationContext(), (Class<?>) CustomSpinnerDialog.class);
                    intent.putExtra(CustomSpinnerDialogExtras.ITEMS, font_size_name);
                    intent.putExtra(CustomSpinnerDialogExtras.SELECTED_ITEM, font_size_name[font_size_index]);
                    if (ApplicationConstants.APPLICATION_LANGUAGE_DIRECTION == ApplicationConstants.Direction.LTR) {
                        Lang = "english";
                    } else {
                        Lang = "arabic";
                    }
                    intent.putExtra(CustomSpinnerDialogExtras.LANGUAGE, Lang);
                    intent.putExtra(CustomSpinnerDialogExtras.DIALOG_TYPE, "Fonts_Size");
                    SettingsActivity.this.startActivityForResult(intent, 1);
                }
            }
        });
        return v2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public View initializeFontFace(View v, LayoutInflater inflater, String dataValue) {
        View v2 = inflater.inflate(R.layout.settings_menu_app_font_type_arabic, (ViewGroup) null);
        TextView txv = (TextView) v2.findViewById(R.id.txv);
        Utils.setText(txv, getResources().getString(R.string.select_type_face));
        Button btn = (Button) v2.findViewById(R.id.btn_font_type);
        btn.setBackgroundResource(android.R.drawable.btn_dropdown);
        SharedPreferences pref = getSharedPreferences(ApplicationConstants.APPLICATION_PREFERENCES, 0);
        final int typeFaceIndex = pref.getInt(ApplicationConstants.FONT_TYPE_FACE_PREFERENCES, 0);
        btn.setTextSize(13.0f);
        Utils.setText(btn, getResources().getStringArray(R.array.Arr_fontsTypeFace)[typeFaceIndex]);
        btn.setOnClickListener(new View.OnClickListener() { // from class: com.dot.azkari.SettingsActivity.6
            @Override // android.view.View.OnClickListener
            public void onClick(View arg0) {
                String Lang;
                if (!SettingsActivity.this.isDialogClicked.booleanValue()) {
                    SettingsActivity.this.isDialogClicked = true;
                    Intent intent = new Intent(SettingsActivity.this.getApplicationContext(), (Class<?>) CustomSpinnerDialog.class);
                    String[] data = SettingsActivity.this.getResources().getStringArray(R.array.Arr_fontsTypeFace);
                    intent.putExtra(CustomSpinnerDialogExtras.ITEMS, data);
                    intent.putExtra(CustomSpinnerDialogExtras.SELECTED_ITEM, data[typeFaceIndex]);
                    if (ApplicationConstants.APPLICATION_LANGUAGE_DIRECTION == ApplicationConstants.Direction.LTR) {
                        Lang = "english";
                    } else {
                        Lang = "arabic";
                    }
                    intent.putExtra(CustomSpinnerDialogExtras.LANGUAGE, Lang);
                    intent.putExtra(CustomSpinnerDialogExtras.DIALOG_TYPE, "Fonts_Type");
                    SettingsActivity.this.startActivityForResult(intent, 1);
                }
            }
        });
        return v2;
    }

    @Override // android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.isDialogClicked = false;
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            String dialog_type = data.getStringExtra(CustomSpinnerDialogExtras.DIALOG_TYPE);
            int index = Integer.parseInt(data.getStringExtra(CustomSpinnerDialogExtras.CHECKED_INDEX));
            if (dialog_type.equals("Fonts_Type")) {
                ApplicationConstants.ARABIC_TYPE_FACE = ApplicationConstants.SYSTEM_TYPE_FACES.get(index);
                setContentView(R.layout.settings);
                Utils.setCurrentLocaleFromShared(this);
                initializeSettingsHeader();
                SharedPreferences pref = getSharedPreferences(ApplicationConstants.APPLICATION_PREFERENCES, 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt(ApplicationConstants.FONT_TYPE_FACE_PREFERENCES, index);
                editor.commit();
                ApplicationConstants.REFILL_GRIDS = true;
                this.settingsAdapter = new settingsMenuAdapter(getResources().getStringArray(R.array.ArabicSettings));
                this.settingsList = (ListView) findViewById(R.id.lsv_settingMenu);
                this.settingsList.setAdapter((ListAdapter) this.settingsAdapter);
                return;
            }
            if (dialog_type.equals("Fonts_Size")) {
                Button btn = (Button) findViewById(R.id.btn_FontSize);
                Utils.setText(btn, getResources().getStringArray(R.array.fonts_size_array)[index]);
                String[] font_size_values = getResources().getStringArray(R.array.fonts_size_value_array);
                SharedPreferences pref2 = getSharedPreferences(ApplicationConstants.APPLICATION_PREFERENCES, 0);
                SharedPreferences.Editor ed = pref2.edit();
                ed.putInt(ApplicationConstants.PRAYERS_TEXT_FONT_SIZE, Integer.parseInt(font_size_values[index]));
                ed.commit();
                settingItems();
                return;
            }
            if (dialog_type.equals(ApplicationConstants.LANGUAGE_PREFERENCES)) {
                this.itemPosition = index;
                if (ApplicationConstants.APPLICATION_LANGUAGE_ID != this.itemPosition + 1) {
                    Button btn2 = (Button) findViewById(R.id.btn_language_default_tasbeeha);
                    Utils.setText(btn2, this.appLanguages[this.itemPosition]);
                    Utils.setApplicationLocaleAndPreferences(this, this.itemPosition + 1);
                    settingItems();
                    initializeSettingsHeader();
                }
            }
        }
    }

    private int getIndexOfFontSize(String[] font_size_values, int value) {
        if (font_size_values == null) {
            return 0;
        }
        for (int i = 0; i < font_size_values.length; i++) {
            if (font_size_values[i] != null) {
                try {
                    if (Integer.parseInt(font_size_values[i].trim()) == value) {
                        return i;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return 0;
    }

    private void initializeLanguages() {
        Azkar_DB_Adapter.Init(this);
        Azkar_DB_Adapter.open();
        Cursor c = Azkar_DB_Adapter.selectAll_Lanaguages();
        if (c != null) {
            try {
                this.appLanguagesData = (String[][]) Array.newInstance((Class<?>) String.class, c.getCount(), 2);
                this.appLanguages = new String[c.getCount()];
                int i = 0;
                while (c.moveToNext()) {
                    this.appLanguagesData[i][0] = String.valueOf(c.getInt(0));
                    this.appLanguages[i] = c.getString(c.getColumnIndex("LAN_NAME"));
                    this.appLanguagesData[i][1] = c.getString(c.getColumnIndex("LAN_NAME"));
                    i++;
                }
            } finally {
                c.close();
            }
        }
    }

    private void initializeSelectedLanguage() {
        String currentLanguage = getCurrentLanguage(ApplicationConstants.APPLICATION_LANGUAGE_ID);
        if (this.appLanguages != null) {
            for (int i = 0; i < this.appLanguages.length; i++) {
                if (this.appLanguages[i] != null && this.appLanguages[i].equals(currentLanguage)) {
                    this.itemPosition = i;
                    return;
                }
            }
        }
    }

    private String getCurrentLanguage(int id) {
        if (this.appLanguagesData == null) {
            return XmlPullParser.NO_NAMESPACE;
        }
        for (int i = 0; i < this.appLanguagesData.length; i++) {
            if (this.appLanguagesData[i] != null && this.appLanguagesData[i][0] != null) {
                try {
                    if (Integer.parseInt(this.appLanguagesData[i][0]) == id) {
                        return this.appLanguagesData[i][1] != null ? this.appLanguagesData[i][1] : XmlPullParser.NO_NAMESPACE;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return XmlPullParser.NO_NAMESPACE;
    }
}
