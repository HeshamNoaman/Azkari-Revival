package com.buzzbox.mob.android.scheduler.ui;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.buzzbox.mob.android.scheduler.SchedulerManager;
import com.buzzbox.mob.android.scheduler.Task;
import com.buzzbox.mob.android.scheduler.db.MigrableSQLiteHelper;
import com.buzzbox.mob.android.scheduler.db.model.Notification;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;

/* JADX INFO: loaded from: classes.dex */
public class NotificationHistoryActivity extends ListActivity {
    private static final String LONG_PRESS_MENU_DELETE_ALL = "Delete All";
    private static final String LONG_PRESS_MENU_DELETE_OLDER_THAN = "Delete older than this";
    private static final String LONG_PRESS_MENU_DELETE_SINGLE = "Delete";
    private static final int ROW_CONTAINER = 21989;
    private static final int ROW_FIRST_LINE_MAIN_ICON = 21992;
    private static final int ROW_FIRST_LINE_SMALL_ICON = 21993;
    private static final int ROW_FIRST_LINE_TITLE = 21991;
    private static final int ROW_SECOND_LINE_TEXT = 21994;
    private static final int ROW_SECOND_LINE_TIME = 21995;
    private static final int TYPE_DATE = 1;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_MAX_COUNT = 2;
    static int list_selector_id = Resources.getSystem().getIdentifier("list_selector_background", "drawable", "android");
    private SQLiteDatabase db;
    TextView headerView;
    private String[] notificationTypes;
    private NotificationsCursorAdapter notificationsCursorAdapter;
    private ArrayList<Notification> notificationsList;
    Class<? extends Task> taskClass;
    private final SimpleDateFormat dfMonthDay = new SimpleDateFormat("MMMM d");
    private final SimpleDateFormat dfTime = new SimpleDateFormat("hh:mm a");
    private String type = null;
    private final BroadcastReceiver receiverUpdateEnd = new BroadcastReceiver() { // from class: com.buzzbox.mob.android.scheduler.ui.NotificationHistoryActivity.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context ctx, Intent arg1) {
            NotificationHistoryActivity.this.notificationsList = Notification.loadNotifications(NotificationHistoryActivity.this.db);
            NotificationHistoryActivity.this.notificationsCursorAdapter.setItems(NotificationHistoryActivity.this.notificationsList, null);
            NotificationHistoryActivity.this.showNoResultsMessage();
            NotificationHistoryActivity.this.notificationsCursorAdapter.notifyDataSetChanged();
            NotificationHistoryActivity.this.setProgressBarIndeterminateVisibility(false);
            Toast.makeText(ctx, "refreshed", 0).show();
        }
    };
    private final BroadcastReceiver receiverUpdateStart = new BroadcastReceiver() { // from class: com.buzzbox.mob.android.scheduler.ui.NotificationHistoryActivity.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context arg0, Intent arg1) {
            NotificationHistoryActivity.this.setProgressBarIndeterminateVisibility(true);
        }
    };

    @Override // android.app.ListActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override // android.app.Activity
    protected void onStart() {
        super.onStart();
        registerReceiver(this.receiverUpdateStart, new IntentFilter(SchedulerManager.INTENT_TASK_START_PREFIX + this.taskClass.getName()));
        registerReceiver(this.receiverUpdateEnd, new IntentFilter(SchedulerManager.INTENT_TASK_END_PREFIX + this.taskClass.getName()));
    }

    @Override // android.app.Activity
    protected void onStop() {
        super.onStop();
        unregisterReceiver(this.receiverUpdateStart);
        unregisterReceiver(this.receiverUpdateEnd);
    }

    @Override // android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (919817235 == requestCode && data != null) {
            SchedulerManager.getInstance().handleConfigurationResult(this, data);
        }
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(5);
        getResources().getIdentifier("buzzbox_notification_history_header", "layout", getPackageName());
        String title = getIntent().getStringExtra("windowTitle");
        if (title == null) {
            title = "Notification History";
        }
        setTitle(title);
        this.taskClass = (Class) getIntent().getSerializableExtra("taskClass");
        getListView().setBackgroundColor(-1);
        getListView().setCacheColorHint(-1);
        getListView().setDrawingCacheBackgroundColor(-1);
        getListView().setDrawingCacheEnabled(true);
        MigrableSQLiteHelper.createFirstTime(this);
        this.db = MigrableSQLiteHelper.getReadableDb(this);
        this.notificationsList = Notification.loadNotifications(this.db);
        addEmptyHeader();
        setData();
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.buzzbox.mob.android.scheduler.ui.NotificationHistoryActivity.3
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> arg0, View view, int pos, long id) {
                int listPos = pos - 1;
                try {
                    if (view.getId() == -99) {
                        TextView headerText = (TextView) view;
                        headerText.setText("refreshing...");
                        SchedulerManager.getInstance().runNow(NotificationHistoryActivity.this, NotificationHistoryActivity.this.taskClass, 0L);
                    } else if (1 != NotificationHistoryActivity.this.notificationsCursorAdapter.getItemViewType(listPos)) {
                        Notification notification = (Notification) NotificationHistoryActivity.this.notificationsCursorAdapter.getItem(listPos);
                        Log.i("D", notification.getTitle());
                        Intent i = Notification.byteArrayToIntent(notification.getIntent());
                        if (!"buzzbox-scheduler.nothing".equals(i.getAction())) {
                            NotificationHistoryActivity.this.startActivity(i);
                        }
                    }
                } catch (Exception e) {
                    Log.e("buzzbox-scheduler", "error on setOnItemClickListener", e);
                }
            }
        });
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { // from class: com.buzzbox.mob.android.scheduler.ui.NotificationHistoryActivity.4
            @Override // android.widget.AdapterView.OnItemLongClickListener
            public boolean onItemLongClick(AdapterView<?> arg0, View view, int pos, long id) {
                Notification notification;
                if (1 != NotificationHistoryActivity.this.notificationsCursorAdapter.getItemViewType(pos - 1) && (notification = (Notification) NotificationHistoryActivity.this.notificationsCursorAdapter.getItem(pos - 1)) != null) {
                    createDialogDeleteOptions(notification);
                }
                return true;
            }

            private void createDialogDeleteOptions(final Notification n) {
                final CharSequence[] items = {NotificationHistoryActivity.LONG_PRESS_MENU_DELETE_SINGLE, NotificationHistoryActivity.LONG_PRESS_MENU_DELETE_ALL, NotificationHistoryActivity.LONG_PRESS_MENU_DELETE_OLDER_THAN};
                AlertDialog.Builder builder = new AlertDialog.Builder(NotificationHistoryActivity.this);
                builder.setTitle("Delete options");
                builder.setItems(items, new DialogInterface.OnClickListener() { // from class: com.buzzbox.mob.android.scheduler.ui.NotificationHistoryActivity.4.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int item) {
                        int res = 0;
                        if (NotificationHistoryActivity.LONG_PRESS_MENU_DELETE_SINGLE.equals(items[item])) {
                            res = Notification.deleteById(NotificationHistoryActivity.this.db, n.getId());
                        } else if (NotificationHistoryActivity.LONG_PRESS_MENU_DELETE_OLDER_THAN.equals(items[item])) {
                            res = Notification.deleteOlderThan(NotificationHistoryActivity.this.db, n.getTime());
                        } else if (NotificationHistoryActivity.LONG_PRESS_MENU_DELETE_ALL.equals(items[item])) {
                            res = Notification.deleteAll(NotificationHistoryActivity.this.db);
                        }
                        Toast.makeText(NotificationHistoryActivity.this, "Deleted " + res + " notifications", 0).show();
                        NotificationHistoryActivity.this.notificationsList = Notification.loadNotifications(NotificationHistoryActivity.this.db);
                        NotificationHistoryActivity.this.setData();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    private void addEmptyHeader() {
        this.headerView = new TextView(this);
        this.headerView.setId(-99);
        this.headerView.setPadding(20, 20, 20, 20);
        this.headerView.setGravity(17);
        this.headerView.setText("Empty List\n(click to refresh)");
        this.headerView.setTextSize(20.0f);
        this.headerView.setTextColor(-16777216);
        this.headerView.setHeight(0);
        getListView().addHeaderView(this.headerView);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showNoResultsMessage() {
        if (this.notificationsList != null && this.notificationsList.size() == 0) {
            this.headerView.setHeight(140);
        } else {
            this.headerView.setHeight(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setData() {
        if (this.notificationsCursorAdapter == null) {
            this.notificationsCursorAdapter = new NotificationsCursorAdapter(this, this.notificationsList, this.type);
            setListAdapter(this.notificationsCursorAdapter);
        } else {
            this.notificationsCursorAdapter.setItems(this.notificationsList, this.type);
        }
        showNoResultsMessage();
        this.notificationsCursorAdapter.notifyDataSetChanged();
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        String notificationTypesString;
        if (this.notificationTypes == null && (notificationTypesString = MetaDataUtils.getString(this, "SchedulerPreferenceActivity.notificationTypes", null)) != null && !XmlPullParser.NO_NAMESPACE.equals(notificationTypesString.trim())) {
            this.notificationTypes = notificationTypesString.split(",");
            SubMenu filterMenu = menu.addSubMenu("Filter by type");
            for (int i = 0; i < this.notificationTypes.length; i++) {
                filterMenu.add(0, i, i, this.notificationTypes[i]);
            }
            if (this.notificationTypes.length > 0) {
                filterMenu.add(0, this.notificationTypes.length, this.notificationTypes.length, "All");
            }
        }
        menu.add(1, 0, 0, "Notification Limit");
        menu.add("Refresh");
        menu.add("Settings");
        return true;
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem item) {
        String menuItemTitle = item.getTitle().toString();
        this.type = menuItemTitle;
        if ("Notification Limit".equals(menuItemTitle)) {
            createDialogListForStorageLimit();
            return true;
        }
        if ("Refresh".equals(menuItemTitle)) {
            SchedulerManager.getInstance().runNow(this, this.taskClass, 0L);
            return true;
        }
        if ("Settings".equals(menuItemTitle)) {
            SchedulerManager.getInstance().startConfigurationActivity(this, this.taskClass);
            return true;
        }
        if ("All".equals(this.type)) {
            this.type = null;
            setData();
            return true;
        }
        if (!"Filter by type".equals(this.type)) {
            setData();
            return true;
        }
        return true;
    }

    private void createDialogListForStorageLimit() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editPrefs = prefs.edit();
        final CharSequence[] items = {"1 day", "2 days", "3 days", "Ever"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Keep notifications of...");
        int keepNotificationOfNDays = prefs.getInt("notificationHistoryActivity.notificationLimit", -1);
        builder.setSingleChoiceItems(items, keepNotificationOfNDays != -1 ? keepNotificationOfNDays - 1 : items.length - 1, new DialogInterface.OnClickListener() { // from class: com.buzzbox.mob.android.scheduler.ui.NotificationHistoryActivity.5
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int item) {
                int tmp = -1;
                if ("1 day".equals(items[item])) {
                    tmp = 1;
                } else if ("2 days".equals(items[item])) {
                    tmp = 2;
                } else if ("3 days".equals(items[item])) {
                    tmp = 3;
                } else if ("Ever".equals(items[item])) {
                }
                editPrefs.putInt("notificationHistoryActivity.notificationLimit", tmp);
                editPrefs.commit();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    class NotificationsCursorAdapter extends BaseAdapter {
        Context context;
        private HashSet datePos = new HashSet();
        ArrayList<Notification> listItems = new ArrayList<>();

        public NotificationsCursorAdapter(Context context, ArrayList<Notification> itemList, String type) {
            this.context = context;
            setItems(itemList, type);
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return ((Notification) getItem(position)).getId();
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            if (this.listItems.size() == 0) {
                return null;
            }
            return this.listItems.get(position);
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        @Override // android.widget.Adapter
        public View getView(int position, View row, ViewGroup parent) {
            ViewHolder holder;
            int type = getItemViewType(position);
            Notification notification = (Notification) getItem(position);
            if (row == null) {
                holder = new ViewHolder();
                row = NotificationHistoryActivity.this.getRow(this.context);
                row.setBackgroundResource(NotificationHistoryActivity.list_selector_id);
                holder.row = row;
                holder.title = (TextView) row.findViewById(NotificationHistoryActivity.ROW_FIRST_LINE_TITLE);
                holder.text = (TextView) row.findViewById(NotificationHistoryActivity.ROW_SECOND_LINE_TEXT);
                holder.time = (TextView) row.findViewById(NotificationHistoryActivity.ROW_SECOND_LINE_TIME);
                holder.mainIcon = (ImageView) row.findViewById(NotificationHistoryActivity.ROW_FIRST_LINE_MAIN_ICON);
                holder.smallIcon = (ImageView) row.findViewById(NotificationHistoryActivity.ROW_FIRST_LINE_SMALL_ICON);
                holder.container = row.findViewById(NotificationHistoryActivity.ROW_CONTAINER);
                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
            }
            switch (type) {
                case 0:
                    holder.container.setPadding(notification.getGroupId() > 0 ? 30 : 5, 7, 0, 0);
                    holder.title.setText(notification.getTitle());
                    holder.title.setTextColor(-16777216);
                    holder.text.setText(notification.getText());
                    holder.time.setText(NotificationHistoryActivity.this.dfTime.format(Long.valueOf(notification.getTime())));
                    String mainIconName = notification.getMainIcon();
                    if (mainIconName != null && !XmlPullParser.NO_NAMESPACE.equals(mainIconName.trim())) {
                        int mainIconResourceId = this.context.getResources().getIdentifier(mainIconName, null, null);
                        if (mainIconResourceId > 0) {
                            holder.mainIcon.setImageResource(mainIconResourceId);
                        }
                    } else {
                        holder.mainIcon.setImageResource(0);
                    }
                    String smallIconName = notification.getSmallIcon();
                    if (smallIconName != null && !XmlPullParser.NO_NAMESPACE.equals(smallIconName.trim())) {
                        int smallIconResourceId = this.context.getResources().getIdentifier(smallIconName, null, null);
                        if (smallIconResourceId > 0) {
                            holder.smallIcon.setImageResource(smallIconResourceId);
                        }
                    } else {
                        holder.smallIcon.setImageResource(0);
                    }
                    String bgColor = notification.getBgColor();
                    if (bgColor != null && !XmlPullParser.NO_NAMESPACE.equals(bgColor.trim())) {
                        holder.container.setBackgroundColor(Color.parseColor(bgColor));
                    } else {
                        holder.container.setBackgroundColor(0);
                    }
                    return row;
                case 1:
                    holder.title.setText(String.valueOf(NotificationHistoryActivity.this.dfMonthDay.format(Long.valueOf(notification.getTime()))) + "th");
                    holder.text.setVisibility(8);
                    holder.time.setVisibility(8);
                    holder.container.setBackgroundColor(Color.parseColor("#BBBBBB"));
                    holder.title.setTextColor(-1);
                    return row;
                default:
                    return row;
            }
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public int getViewTypeCount() {
            return 2;
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this.listItems.size();
        }

        public int getSeparatorCount() {
            return this.datePos.size();
        }

        public void addItem(Notification item) {
            this.listItems.add(item);
        }

        public void addDateItem(Notification item) {
            this.listItems.add(item);
            this.datePos.add(Integer.valueOf(this.listItems.size() - 1));
        }

        public void setItems(List<Notification> arrayList, String type) {
            this.listItems.clear();
            this.datePos.clear();
            String currDate = XmlPullParser.NO_NAMESPACE;
            for (int i = 0; i < arrayList.size(); i++) {
                Notification currItem = arrayList.get(i);
                String tempDate = NotificationHistoryActivity.this.dfMonthDay.format(new Date(currItem.getTime()));
                if (!currDate.equals(tempDate) && (type == null || type.equals(currItem.getNotificationType()))) {
                    currDate = tempDate;
                    addDateItem(currItem);
                }
                if (type == null || type.equals(currItem.getNotificationType())) {
                    addItem(currItem);
                }
            }
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public int getItemViewType(int position) {
            return this.datePos.contains(Integer.valueOf(position)) ? 1 : 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public RelativeLayout getRow(Context ctx) {
        RelativeLayout relativeLayoutCreateRelativeLayout = createRelativeLayout(ctx, -1, -1, 0);
        LinearLayout linearLayoutCreateLinearLayout = createLinearLayout(ctx, 1, -1, -1, 0);
        linearLayoutCreateLinearLayout.setId(ROW_CONTAINER);
        linearLayoutCreateLinearLayout.setPadding(5, 7, 0, 0);
        LinearLayout firstLine = createLinearLayout(ctx, 0, -1, -2, 0);
        LinearLayout secondLine = createLinearLayout(ctx, 0, -1, -2, 0);
        ImageView mainIcon = new ImageView(ctx);
        mainIcon.setId(ROW_FIRST_LINE_MAIN_ICON);
        mainIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mainIcon.setPadding(3, 3, 0, 0);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(30, 30);
        mainIcon.setLayoutParams(layoutParams);
        firstLine.addView(mainIcon);
        TextView titleTextView = createTextView(ctx, ROW_FIRST_LINE_TITLE, -1, -2, 1, null);
        titleTextView.setPadding(4, 0, 0, 0);
        titleTextView.setSingleLine();
        titleTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        titleTextView.setTextAppearance(ctx, android.R.style.TextAppearance_Medium_Inverse);
        titleTextView.setTypeface(null, 1);
        firstLine.addView(titleTextView);
        ImageView smallIcon = new ImageView(ctx);
        smallIcon.setId(ROW_FIRST_LINE_SMALL_ICON);
        smallIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        RelativeLayout.LayoutParams relLayoutParams = new RelativeLayout.LayoutParams(-2, -2);
        relLayoutParams.addRule(11);
        relLayoutParams.addRule(6);
        relLayoutParams.rightMargin = 3;
        smallIcon.setLayoutParams(relLayoutParams);
        relativeLayoutCreateRelativeLayout.addView(smallIcon);
        TextView textTextView = createTextView(ctx, ROW_SECOND_LINE_TEXT, -2, -2, 1, null);
        secondLine.setPadding(7, 0, 0, 3);
        textTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        textTextView.setLines(2);
        textTextView.setTextAppearance(ctx, android.R.style.TextAppearance_Small_Inverse);
        secondLine.addView(textTextView);
        LinearLayout.LayoutParams layoutParamsTime = new LinearLayout.LayoutParams(-2, -2, 0.0f);
        layoutParamsTime.gravity = 85;
        layoutParamsTime.rightMargin = 3;
        TextView timetTextView = createTextView(ctx, ROW_SECOND_LINE_TIME, -1, -1, -1, layoutParamsTime);
        timetTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        timetTextView.setPadding(0, 0, 5, 0);
        timetTextView.setTextAppearance(ctx, android.R.style.TextAppearance_Small_Inverse);
        secondLine.addView(timetTextView);
        relativeLayoutCreateRelativeLayout.addView(linearLayoutCreateLinearLayout);
        linearLayoutCreateLinearLayout.addView(firstLine);
        linearLayoutCreateLinearLayout.addView(secondLine);
        return relativeLayoutCreateRelativeLayout;
    }

    private RelativeLayout createRelativeLayout(Context ctx, int w, int h, int weight) {
        RelativeLayout rl = new RelativeLayout(ctx);
        return rl;
    }

    private LinearLayout createLinearLayout(Context ctx, int orientation, int w, int h, int weight) {
        LinearLayout ll = new LinearLayout(ctx);
        ll.setOrientation(orientation);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w, h, weight);
        ll.setLayoutParams(params);
        return ll;
    }

    private TextView createTextView(Context ctx, int id, int w, int h, int weight, LinearLayout.LayoutParams layoutParams) {
        TextView textView = new TextView(ctx);
        textView.setId(id);
        if (layoutParams == null) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w, h, weight);
            params.gravity = 16;
            textView.setLayoutParams(params);
        } else {
            textView.setLayoutParams(layoutParams);
        }
        return textView;
    }
}
