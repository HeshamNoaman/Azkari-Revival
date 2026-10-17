package com.dot.azkari;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TableRow;
import android.widget.TextView;
import com.arttech.languageCategory.Category;
import com.arttech.ramadan.RamadanPreferences;
import com.dot.adapter.database.Azkar_DB_Adapter;
import com.dot.arabic.ArabicUtilities;
import com.dot.constants.ApplicationConstants;
import java.util.Locale;
import org.xmlpull.v1.XmlPullParser;

/* JADX INFO: loaded from: classes.dex */
public class Menu extends Activity {
    private static final int ADD_DIALOG_ID = 0;
    private static final int CONFIRM_DELETE_DIALOGE_ID = 1;
    private Cursor azkarCursor;
    ImageButton btnAdd;
    private Locale currentLocale;
    private GridView gvPrayers;
    private TabHost host;
    ImageView imgFacebook;
    ImageView imgNativeShare;
    ImageView imgTwitter;
    private View selectedTasbeha;
    private Cursor tasabeehCursor;
    private Thread updateThread;
    private Boolean wasPosted;
    GridView gvTasabeh = null;
    private final Handler applicationHandler = new Handler();
    private Boolean stopCheckingUpdate = false;
    Runnable notificationRunnable = new Runnable() { // from class: com.dot.azkari.Menu.1
        @Override // java.lang.Runnable
        public void run() {
            if (!Menu.this.wasPosted.booleanValue()) {
                if (!Menu.this.wasPosted.booleanValue() && !Utils.isOnline(Menu.this)) {
                    Utils.getToast(Menu.this, Menu.this.getString(R.string.no_internet_Access), R.drawable.failed, 1).show();
                    return;
                } else {
                    Utils.getToast(Menu.this, Menu.this.getString(R.string.failed_To_Post), R.drawable.failed, 1).show();
                    return;
                }
            }
            Utils.getToast(Menu.this, Menu.this.getString(R.string.successfully_Posted), R.drawable.success, 1).show();
        }
    };
    public Runnable checkApplicationUpdate = new Runnable() { // from class: com.dot.azkari.Menu.2
        @Override // java.lang.Runnable
        public void run() {
            // Offline-first: no remote update check
        }
    };

    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        Utils.setLocale(getBaseContext());
        initializeConstants();
        initializeAddButtonHandler();
        initializeNativeShareIconHandler();
        initializeTabHost();
        fillPrayersGridView();
        fillTasabeehGridView();
        checkNotificationPrayer();
    }

    private void checkNotificationPrayer() {
        String catId = Utils.getNotificationPrayerCatID(this);
        if (catId != null && !"-1".equals(catId.trim())) {
            try {
                if (Integer.parseInt(catId.trim()) != -1) {
                    String catId2 = Category.getLocalizedCategoryID(getApplicationContext());
                    Utils.setNotificationPrayerCatID(getApplicationContext(), "-1");
                    startPrayersActivity(catId2);
                }
            } catch (NumberFormatException ignored) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void OpenRTF() {
        Intent i = new Intent(this, (Class<?>) TasksMaster.class);
        startActivity(i);
    }

    private void initializeAddButtonHandler() {
        ImageButton rtf = (ImageButton) findViewById(R.id.imgRTf1);
        if (ApplicationConstants.APPLICATION_LANGUAGE_ID == 1) {
            rtf.setBackgroundResource(R.drawable.rtf_selector);
        } else {
            rtf.setBackgroundResource(R.drawable.rtf_selector_english);
        }
        rtf.setOnClickListener(new View.OnClickListener() { // from class: com.dot.azkari.Menu.5
            @Override // android.view.View.OnClickListener
            public void onClick(View arg0) {
                Menu.this.OpenRTF();
            }
        });
        ImageButton rtf2 = (ImageButton) findViewById(R.id.imgRTf2);
        if (ApplicationConstants.APPLICATION_LANGUAGE_ID == 1) {
            rtf2.setBackgroundResource(R.drawable.rtf_selector);
        } else {
            rtf2.setBackgroundResource(R.drawable.rtf_selector_english);
        }
        rtf2.setOnClickListener(new View.OnClickListener() { // from class: com.dot.azkari.Menu.6
            @Override // android.view.View.OnClickListener
            public void onClick(View arg0) {
                Menu.this.OpenRTF();
            }
        });
        this.btnAdd = (ImageButton) findViewById(R.id.btnCustom);
        this.btnAdd.setBackgroundResource(R.drawable.addtasbehaicon);
        this.btnAdd.setOnClickListener(new View.OnClickListener() { // from class: com.dot.azkari.Menu.7
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Menu.this.showDialog(0);
            }
        });
        ImageView img_menu = (ImageView) findViewById(R.id.imgMenu1);
        registerForContextMenu(img_menu);
        img_menu.setOnClickListener(new View.OnClickListener() { // from class: com.dot.azkari.Menu.8
            @Override // android.view.View.OnClickListener
            public void onClick(View arg0) {
                arg0.showContextMenu();
            }
        });
        ImageView img_menu2 = (ImageView) findViewById(R.id.imgMenu2);
        registerForContextMenu(img_menu2);
        img_menu2.setOnClickListener(new View.OnClickListener() { // from class: com.dot.azkari.Menu.9
            @Override // android.view.View.OnClickListener
            public void onClick(View arg0) {
                arg0.showContextMenu();
            }
        });
    }

    private void initializeFacebookIconHandler() {
    }

    private void initializeTwitterIconHandler() {
    }

    private void initializeNativeShareIconHandler() {
        this.imgNativeShare = (ImageView) findViewById(R.id.imgNateShare);
        this.imgNativeShare.setOnClickListener(new View.OnClickListener() { // from class: com.dot.azkari.Menu.10
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                try {
                    Intent sharingIntent = new Intent("android.intent.action.SEND");
                    sharingIntent.setType("text/plain");
                    String shareBody = Menu.this.getResources().getString(R.string.azkariNativeShareingBody);
                    sharingIntent.putExtra("android.intent.extra.SUBJECT", Menu.this.getResources().getString(R.string.appNamedata));
                    sharingIntent.putExtra("android.intent.extra.TEXT", shareBody);
                    Menu.this.startActivity(Intent.createChooser(sharingIntent, Menu.this.getResources().getString(R.string.azkariShareingVia)));
                    Menu.this.wasPosted = true;
                } catch (Exception e) {
                    if (!Utils.isOnline(Menu.this)) {
                        Menu.this.wasPosted = false;
                        Menu.this.applicationHandler.post(Menu.this.notificationRunnable);
                    }
                }
            }
        });
    }

    private void fillTasabeehGridView() {
        Azkar_DB_Adapter.Init(this);
        this.tasabeehCursor = Azkar_DB_Adapter.selectAll_Tasabeeh_Categories();
        this.gvTasabeh = (GridView) findViewById(R.id.gvTasabeh);
        this.gvTasabeh.setAdapter((ListAdapter) new CategoryItem(this, this.tasabeehCursor, CategoryItem.type.TASABEEH));
        this.gvTasabeh.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { // from class: com.dot.azkari.Menu.11
            @Override // android.widget.AdapterView.OnItemLongClickListener
            public boolean onItemLongClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
                TextView txvItemText = (TextView) view.findViewById(R.id.tvTxt);
                if (!txvItemText.getTag().toString().equals("0")) {
                    Menu.this.selectedTasbeha = view;
                    Menu.this.showDialog(1);
                } else {
                    Utils.getToast(Menu.this, Menu.this.getString(R.string.failedToDeleteTasbeeha), R.drawable.failed, 0).show();
                }
                return true;
            }
        });
        this.gvTasabeh.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.dot.azkari.Menu.12
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
                Menu.this.startTasabeehActivity(view.getTag().toString());
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void deleteTasbeeha(View view) {
        String catId = view.getTag().toString();
        TextView txvItemText = (TextView) view.findViewById(R.id.tvTxt);
        if (txvItemText.getTag().toString().equals("1")) {
            Azkar_DB_Adapter.open();
            Azkar_DB_Adapter.delete_Tasbeeh_Categories(Integer.parseInt(catId));
            Azkar_DB_Adapter.close();
            this.gvTasabeh.setAdapter((ListAdapter) new CategoryItem(this, Azkar_DB_Adapter.selectAll_Tasabeeh_Categories(), CategoryItem.type.TASABEEH));
            Utils.getToast(this, getString(R.string.succesfullyDeleted), R.drawable.success, 0).show();
        }
    }

    private void fillPrayersGridView() {
        Azkar_DB_Adapter.Init(this);
        this.azkarCursor = Azkar_DB_Adapter.select_categories();
        this.gvPrayers = (GridView) findViewById(R.id.gvPrayers);
        this.gvPrayers.setAdapter((ListAdapter) new CategoryItem(this, this.azkarCursor, CategoryItem.type.PRAYERS));
        this.gvPrayers.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.dot.azkari.Menu.13
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
                String catId = view.getTag().toString();
                Menu.this.startPrayersActivity(catId);
            }
        });
        Azkar_DB_Adapter.close();
    }

    private void initializeConstants() {
        this.currentLocale = ApplicationConstants.CURRENT_LOCALE;
    }

    private void initializeTabHost() {
        this.host = (TabHost) findViewById(R.id.tab);
        TabHost.TabSpec prayersTab = this.host.newTabSpec("PrayerTab");
        TabHost.TabSpec tasabehTab = this.host.newTabSpec("TasabehTab");
        this.host.setup();
        prayersTab.setIndicator(getString(R.string.azkar), getResources().getDrawable(R.drawable.prayers));
        prayersTab.setContent(R.id.gvPrayers);
        this.host.addTab(prayersTab);
        tasabehTab.setIndicator(getString(R.string.tasbeeh), getResources().getDrawable(R.drawable.tasabeeh));
        tasabehTab.setContent(R.id.rl);
        this.host.addTab(tasabehTab);
        if (ApplicationConstants.APPLICATION_LANGUAGE_ID == 1) {
            TabWidget tb = this.host.getTabWidget();
            TextView txt1 = (TextView) tb.getChildAt(0).findViewById(android.R.id.title);
            TextView txt2 = (TextView) tb.getChildAt(1).findViewById(android.R.id.title);
            Utils.setText(txt1, getString(R.string.azkar));
            Utils.setText(txt2, getString(R.string.tasbeeh));
        }
        this.host.setCurrentTabByTag("PrayerTab");
        this.host.setOnTabChangedListener(new TabHost.OnTabChangeListener() { // from class: com.dot.azkari.Menu.14
            @Override // android.widget.TabHost.OnTabChangeListener
            public void onTabChanged(String tabId) {
                if (tabId.equals("PrayerTab")) {
                    Menu.this.imgNativeShare.setVisibility(0);
                    Menu.this.btnAdd.setVisibility(8);
                } else if (tabId.equals("TasabehTab")) {
                    Menu.this.imgNativeShare.setVisibility(8);
                    Menu.this.btnAdd.setVisibility(0);
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startPrayersActivity(String categoryID) {
        Intent intent = new Intent(this, (Class<?>) Prayers.class);
        Bundle bundle = new Bundle();
        bundle.putString("CAT_ID", categoryID);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startTasabeehActivity(String TasbeehID) {
        Intent intent = new Intent(this, (Class<?>) TasabeehActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("Tasbeeh_Id", TasbeehID);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private Dialog getDialog(int dialogId) {
        TableRow.LayoutParams pars = new TableRow.LayoutParams(-2, -2);
        pars.weight = 50.0f;
        pars.gravity = 17;
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService("layout_inflater");
        switch (dialogId) {
            case 0:
                final View layout = layoutInflater.inflate(R.layout.add_new_tasbeeh_dialog, (ViewGroup) findViewById(R.id.root));
                TextView txvTasbeeha = (TextView) layout.findViewById(R.id.tv_newTasbeeh);
                final EditText edt = (EditText) layout.findViewById(R.id.EditText_newTasbeeh);
                Utils.setText(txvTasbeeha, getString(R.string.tasabeh_title_dialog));
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(layout);
                builder.setTitle(XmlPullParser.NO_NAMESPACE);
                Button btnOk = new Button(this);
                Utils.setText(btnOk, getString(R.string.ok));
                btnOk.setOnClickListener(new View.OnClickListener() { // from class: com.dot.azkari.Menu.18
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        String tasbehaTxt = edt.getText().toString();
                        if (tasbehaTxt.trim().length() <= 0 || tasbehaTxt.trim().length() > 100) {
                            if (tasbehaTxt.trim().length() >= 50) {
                                TextView txvRequiered = (TextView) layout.findViewById(R.id.txvRequiered);
                                txvRequiered.setGravity(17);
                                Utils.setText(txvRequiered, Menu.this.getString(R.string.reachLimit));
                                txvRequiered.setVisibility(0);
                                return;
                            }
                            TextView txvRequiered2 = (TextView) layout.findViewById(R.id.txvRequiered);
                            txvRequiered2.setGravity(17);
                            Utils.setText(txvRequiered2, Menu.this.getString(R.string.requiered));
                            txvRequiered2.setVisibility(0);
                            return;
                        }
                        Menu.this.insertTasbeha(tasbehaTxt);
                        Menu.this.removeDialog(0);
                        Utils.getToast(Menu.this, Menu.this.getString(R.string.succesfullyAdded), R.drawable.success, 0).show();
                    }
                });
                TableRow tblrowAddTasbeeha = (TableRow) layout.findViewById(R.id.tblrowAddTasbeeha);
                Button btnCancelAdd = new Button(this);
                Utils.setText(btnCancelAdd, getString(R.string.cancel));
                btnCancelAdd.setOnClickListener(new View.OnClickListener() { // from class: com.dot.azkari.Menu.19
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        Menu.this.removeDialog(0);
                    }
                });
                if (ApplicationConstants.APPLICATION_LANGUAGE_DIRECTION == ApplicationConstants.Direction.LTR) {
                    tblrowAddTasbeeha.addView(btnOk, pars);
                    tblrowAddTasbeeha.addView(btnCancelAdd, pars);
                    txvTasbeeha.setGravity(3);
                } else {
                    tblrowAddTasbeeha.addView(btnCancelAdd, pars);
                    tblrowAddTasbeeha.addView(btnOk, pars);
                    txvTasbeeha.setGravity(5);
                }
                AlertDialog addDialog = builder.create();
                addDialog.setCancelable(true);
                addDialog.setCanceledOnTouchOutside(false);
                addDialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.dot.azkari.Menu.20
                    @Override // android.content.DialogInterface.OnCancelListener
                    public void onCancel(DialogInterface dialog) {
                        Menu.this.removeDialog(0);
                    }
                });
                return addDialog;
            case 1:
                View confirmLayout = layoutInflater.inflate(R.layout.delete_confirm_dialog, (ViewGroup) findViewById(R.id.deleteConfirmRoot));
                TextView txvConfirm = (TextView) confirmLayout.findViewById(R.id.tvConfirm);
                Utils.setText(txvConfirm, getString(R.string.confirmDeletion));
                AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(this);
                confirmBuilder.setView(confirmLayout);
                confirmBuilder.setTitle(XmlPullParser.NO_NAMESPACE);
                confirmBuilder.setCancelable(true);
                TableRow tableRow = (TableRow) confirmLayout.findViewById(R.id.tableRow1);
                Button btnOk2 = new Button(this);
                Utils.setText(btnOk2, getString(R.string.ok));
                btnOk2.setOnClickListener(new View.OnClickListener() { // from class: com.dot.azkari.Menu.15
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        Menu.this.deleteTasbeeha(Menu.this.selectedTasbeha);
                        Menu.this.removeDialog(1);
                    }
                });
                Button btnCancel = new Button(this);
                Utils.setText(btnCancel, getString(R.string.cancel));
                if (ApplicationConstants.APPLICATION_LANGUAGE_DIRECTION == ApplicationConstants.Direction.LTR) {
                    tableRow.addView(btnOk2, pars);
                    tableRow.addView(btnCancel, pars);
                    txvConfirm.setGravity(3);
                } else {
                    tableRow.addView(btnCancel, pars);
                    tableRow.addView(btnOk2, pars);
                    txvConfirm.setGravity(5);
                }
                btnCancel.setOnClickListener(new View.OnClickListener() { // from class: com.dot.azkari.Menu.16
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        Menu.this.removeDialog(1);
                    }
                });
                AlertDialog confirmDialog = confirmBuilder.create();
                confirmDialog.setCancelable(true);
                confirmDialog.setCanceledOnTouchOutside(false);
                confirmDialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.dot.azkari.Menu.17
                    @Override // android.content.DialogInterface.OnCancelListener
                    public void onCancel(DialogInterface dialog) {
                        Menu.this.removeDialog(1);
                    }
                });
                return confirmDialog;
            default:
                return null;
        }
    }

    @Override // android.app.Activity
    protected Dialog onCreateDialog(int id) {
        return getDialog(id);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void insertTasbeha(String tasbehaTxt) {
        Azkar_DB_Adapter.open();
        Azkar_DB_Adapter.insert_Tasbeeh_Categories(tasbehaTxt.replace("\"", XmlPullParser.NO_NAMESPACE).replace("'", "â€˜").replace("--", " "), "0", "masbaha_user_defined", 0, 1);
        Azkar_DB_Adapter.close();
        this.gvTasabeh.setAdapter((ListAdapter) new CategoryItem(this, Azkar_DB_Adapter.selectAll_Tasabeeh_Categories(), CategoryItem.type.TASABEEH));
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.azkar_options_menu, menu);
        MenuItem helpItem = menu.findItem(R.id.mi_help);
        helpItem.setIntent(new Intent(this, (Class<?>) HelpActivity.class));
        MenuItem settingsItem = menu.findItem(R.id.mi_settings);
        settingsItem.setIntent(new Intent(this, (Class<?>) SettingsActivity.class));
        MenuItem notificationsSettingsItem = menu.findItem(R.id.notifications_settings);
        notificationsSettingsItem.setIntent(new Intent(this, (Class<?>) NotificationSettingsActivity.class));
        MenuItem scoresItem = menu.findItem(R.id.mi_scores);
        scoresItem.setIntent(new Intent(this, (Class<?>) LogActivity.class));
        MenuItem aboutItem = menu.findItem(R.id.mi_aboutUs);
        aboutItem.setIntent(new Intent(this, (Class<?>) AboutActivity.class));
        helpItem.setTitle(getString(R.string.help_Option_Item));
        settingsItem.setTitle(getString(R.string.settings_Option_Item));
        scoresItem.setTitle(getString(R.string.log_Option_Item));
        aboutItem.setTitle(getResources().getString(R.string.about_Option_Item));
        return true;
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean onMenuOpened(int featureId, android.view.Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.azkar_options_menu, menu);
        MenuItem helpItem = menu.findItem(R.id.mi_help);
        helpItem.setIntent(new Intent(this, (Class<?>) HelpActivity.class));
        MenuItem settingsItem = menu.findItem(R.id.mi_settings);
        settingsItem.setIntent(new Intent(this, (Class<?>) SettingsActivity.class));
        MenuItem notificationsSettingsItem = menu.findItem(R.id.notifications_settings);
        notificationsSettingsItem.setIntent(new Intent(this, (Class<?>) NotificationSettingsActivity.class));
        MenuItem scoresItem = menu.findItem(R.id.mi_scores);
        scoresItem.setIntent(new Intent(this, (Class<?>) LogActivity.class));
        MenuItem aboutItem = menu.findItem(R.id.mi_aboutUs);
        aboutItem.setIntent(new Intent(this, (Class<?>) AboutActivity.class));
        if (ApplicationConstants.APPLICATION_LANGUAGE_DIRECTION == ApplicationConstants.Direction.RTL) {
            helpItem.setTitle(ArabicUtilities.reshape(getString(R.string.help_Option_Item)));
            settingsItem.setTitle(ArabicUtilities.reshape(getString(R.string.settings_Option_Item)));
            scoresItem.setTitle(ArabicUtilities.reshape(getString(R.string.log_Option_Item)));
            aboutItem.setTitle(ArabicUtilities.reshape(getString(R.string.about_Option_Item)));
            notificationsSettingsItem.setTitle(ArabicUtilities.reshape(getString(R.string.notificationsSettings)));
        } else {
            helpItem.setTitle(getString(R.string.help_Option_Item));
            settingsItem.setTitle(getString(R.string.settings_Option_Item));
            scoresItem.setTitle(getString(R.string.log_Option_Item));
            aboutItem.setTitle(getResources().getString(R.string.about_Option_Item));
            notificationsSettingsItem.setTitle(getString(R.string.notificationsSettings));
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        if (this.tasabeehCursor != null && !this.tasabeehCursor.isClosed()) {
            this.tasabeehCursor.close();
        }
        super.onDestroy();
        this.stopCheckingUpdate = true;
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        this.stopCheckingUpdate = true;
    }

    @Override // android.app.Activity
    protected void onStop() {
        super.onStop();
        this.stopCheckingUpdate = true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        checkNotificationPrayer();
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        checkNotificationPrayer();
        if (this.currentLocale == null || ApplicationConstants.CURRENT_LOCALE == null) {
            return;
        }
        if (!this.currentLocale.equals(ApplicationConstants.CURRENT_LOCALE) || ApplicationConstants.REFILL_GRIDS) {
            ApplicationConstants.REFILL_GRIDS = false;
            this.gvTasabeh.setAdapter((ListAdapter) new CategoryItem(this, Azkar_DB_Adapter.selectAll_Tasabeeh_Categories(), CategoryItem.type.TASABEEH));
            this.gvPrayers.setAdapter((ListAdapter) new CategoryItem(this, Azkar_DB_Adapter.select_categories(), CategoryItem.type.PRAYERS));
            TabWidget tb = this.host.getTabWidget();
            TextView txt1 = (TextView) tb.getChildAt(0).findViewById(android.R.id.title);
            TextView txt2 = (TextView) tb.getChildAt(1).findViewById(android.R.id.title);
            txt2.setTypeface(null);
            txt1.setTypeface(null);
            Utils.setText(txt1, getString(R.string.azkar));
            Utils.setText(txt2, getString(R.string.tasbeeh));
            ImageButton rtf = (ImageButton) findViewById(R.id.imgRTf1);
            if (ApplicationConstants.APPLICATION_LANGUAGE_ID == 1) {
                rtf.setBackgroundResource(R.drawable.rtf_selector);
            } else {
                rtf.setBackgroundResource(R.drawable.rtf_selector_english);
            }
            ImageButton rtf2 = (ImageButton) findViewById(R.id.imgRTf2);
            if (ApplicationConstants.APPLICATION_LANGUAGE_ID == 1) {
                rtf2.setBackgroundResource(R.drawable.rtf_selector);
            } else {
                rtf2.setBackgroundResource(R.drawable.rtf_selector_english);
            }
            this.currentLocale = ApplicationConstants.CURRENT_LOCALE;
        }
    }

    @Override // android.app.Activity, android.view.View.OnCreateContextMenuListener
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.azkar_options_menu, menu);
        MenuItem helpItem = menu.findItem(R.id.mi_help);
        helpItem.setIntent(new Intent(this, (Class<?>) HelpActivity.class));
        MenuItem settingsItem = menu.findItem(R.id.mi_settings);
        settingsItem.setIntent(new Intent(this, (Class<?>) SettingsActivity.class));
        MenuItem notificationsSettingsItem = menu.findItem(R.id.notifications_settings);
        notificationsSettingsItem.setIntent(new Intent(this, (Class<?>) NotificationSettingsActivity.class));
        MenuItem scoresItem = menu.findItem(R.id.mi_scores);
        scoresItem.setIntent(new Intent(this, (Class<?>) LogActivity.class));
        MenuItem aboutItem = menu.findItem(R.id.mi_aboutUs);
        aboutItem.setIntent(new Intent(this, (Class<?>) AboutActivity.class));
        helpItem.setTitle(getString(R.string.help_Option_Item));
        settingsItem.setTitle(getString(R.string.settings_Option_Item));
        scoresItem.setTitle(getString(R.string.log_Option_Item));
        notificationsSettingsItem.setTitle(getString(R.string.notificationsSettings));
        aboutItem.setTitle(getResources().getString(R.string.about_Option_Item));
    }

    @Override // android.app.Activity
    public boolean onContextItemSelected(MenuItem item) {
        startActivity(item.getIntent());
        return true;
    }
}
