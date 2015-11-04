package com.brs.dailyweightmonitor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.brs.db.DBHandler;
import com.brs.utils.DM;

public class PrefActivity extends AppCompatActivity
    implements OnPrefItemSelected{

    Intent intent;
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pref);
        mContext = this;
        intent = new Intent(this, MainActivity.class);
        getFragmentManager().beginTransaction().replace(R.id.setting_content_frame, new PrefFragment()).commit();
    }

    @Override
    public void onPrefListItemSelected() {
        DM.i(this, "onPrefListItemSelected:");
        finish();
    }

    @Override
    public void onPrefDeleteAllDatabase() {
        DM.i(this, "onPrefDeleteAllDatabase()");

        new AlertDialog.Builder(this).setTitle(R.string.delete_all_title).setMessage(R.string.delete_all_message)
                .setNegativeButton(R.string.dialog_btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DM.i(this, "삭제 취소");
                    }
                }).setPositiveButton(R.string.dialog_btn_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DM.i(this, "전체 삭제 됨.");
                        DBHandler dh = DBHandler.open(mContext);
                        dh.deleteAll();
                        dh.close();
                    }
                }).show();
    }

    public static class PrefFragment extends PreferenceFragment {

        Preference removeAll;
        OnPrefItemSelected mPrefItemSelectListener;

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            mPrefItemSelectListener = (OnPrefItemSelected) activity;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preference from an XML
            addPreferencesFromResource(R.xml.preferences);

            /* 툴바 처리. */
            Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.pref_toolBar);
            toolbar.setTitle(R.string.pref_toolbar_title);
            toolbar.setTitleTextColor(Color.WHITE);
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

//            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

            toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DM.i("tool bar click");
                    mPrefItemSelectListener.onPrefListItemSelected();
                }
            });

            removeAll = findPreference("delet_all_key");
            removeAll.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    DM.v(this, "onPreferenceClick() - remove all data");
                    mPrefItemSelectListener.onPrefDeleteAllDatabase();
                    return false;
                }
            });
        }
    }


}
