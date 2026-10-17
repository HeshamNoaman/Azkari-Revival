package com.dot.azkari;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.dot.constants.ApplicationConstants;

/* JADX INFO: loaded from: classes.dex */
public class HelpActivity extends Activity {
    private HelpMenuItemAdapter helpAdapter;

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
        initializeHelpHeader();
        this.helpAdapter = new HelpMenuItemAdapter(getResources().getStringArray(R.array.help_menu));
        ListView helpLis = (ListView) findViewById(R.id.lsv_helpMenu);
        helpLis.setAdapter((ListAdapter) this.helpAdapter);
        helpLis.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.dot.azkari.HelpActivity.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> parent, View selectedItem, int postition, long arg3) {
                TextView txt = (TextView) selectedItem.findViewById(R.id.txv_helpMenuItem);
                HelpViewActivity.setHelpTopic(txt.getTag().toString());
                HelpActivity.this.startActivity(new Intent(HelpActivity.this, (Class<?>) HelpViewActivity.class));
            }
        });
    }

    private void initializeHelpHeader() {
        TextView txv = (TextView) findViewById(R.id.txv_helpHeader);
        Utils.setText(txv, getResources().getString(R.string.help_menu));
    }

    private class HelpMenuItemAdapter extends BaseAdapter {
        private String[] dataSet;
        private LayoutInflater inflater;

        public HelpMenuItemAdapter(String[] data) {
            this.inflater = null;
            this.dataSet = data;
            this.inflater = (LayoutInflater) HelpActivity.this.getSystemService("layout_inflater");
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this.dataSet.length;
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            return this.dataSet[position];
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        @Override // android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = this.inflater.inflate(R.layout.help_menu_item, (ViewGroup) null);
            TextView txv = (TextView) v.findViewById(R.id.txv_helpMenuItem);
            txv.setTag(this.dataSet[position]);
            LinearLayout layout = (LinearLayout) v.findViewById(R.id.lil_helpItem);
            layout.removeAllViews();
            if (ApplicationConstants.APPLICATION_LANGUAGE_DIRECTION == ApplicationConstants.Direction.RTL) {
                txv.setTypeface(ApplicationConstants.ARABIC_TYPE_FACE);
                txv.setGravity(5);
                layout.setGravity(5);
                layout.addView(txv);
            } else if (ApplicationConstants.APPLICATION_LANGUAGE_DIRECTION == ApplicationConstants.Direction.LTR) {
                layout.setGravity(3);
                txv.setGravity(3);
                layout.addView(txv);
            }
            Utils.setText(txv, this.dataSet[position]);
            return v;
        }
    }

    @Override // android.app.Activity
    protected void onResume() {
        Utils.setLocale(getBaseContext());
        super.onResume();
    }
}
