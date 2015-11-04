package com.brs.dailyweightmonitor;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.brs.constvalue.ConstValues;
import com.brs.db.DBHandler;
import com.brs.dialog.DialogManager;
import com.brs.handler.MainHandler;
import com.brs.utils.DM;
import com.brs.utils.Util;
import com.brs.utils.VersionControl;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity
    implements ListView.OnItemClickListener{

    private long l_BackKey_RepeatTimer = 0;

    Context mContext;
    ArrayList<DailyInfo> arrayDailyInfo;
    DialogManager dialogManager;
    DailyInfoAdapter dailyInfoAdapter;
    ListView dailyInfoListView;
    DBHandler dbHandler;
    Cursor cursor;

    // Handler -  WeakReference 처리.
    MainHandler mainHandler;

    boolean bAutoInputPopup;
    String todayWeight;

    String today;
    SharedPreferences todayCheckPref;
    SharedPreferences.Editor todayPrefEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Admob. */
        AdView mAdView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        DM.x("onCreate()");

        mContext = this;
        mainHandler = new MainHandler(MainActivity.this);

        /* 툴바 처리. */
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolBar);
        toolbar.setTitle(R.string.toolbar_title);
        toolbar.setTitleTextColor(Color.WHITE);

        //toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert); // tool bar navigation icon test_code.
        //toolbar.setLogo(android.R.drawable.ic_menu_agenda); // tool bar logo icon test_code.

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);

        arrayDailyInfo = new ArrayList<DailyInfo>();

        /* db 연결 준비 */
        dbHandler = DBHandler.open(this);

        /* 오늘 날짜 확인. */
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        today = Util.convertDateWithDash(year, month, day);
        int today_order = Util.convertDateForOrderColumn(year, month, day);

        DM.i(this, "today:" + today + "today_order:" + today_order);

        // DB 에 오늘 데이터가 있는지 검색.
        cursor = dbHandler.findDate(today);
        DM.i(this, "cursor count:" + cursor.getCount());

        if(cursor.getCount() > 0){
            DM.i(this, "find today: index 0:" + cursor.getString(0) + " / index 1:" + cursor.getString(1) + " / index 2:" + cursor.getString(2)
                    + " / index 3:" + cursor.getInt(3) + " / index 4:" + cursor.getInt(4) + " / index 5:" + cursor.getString(5) + " / index 6:" + cursor.getString(6));
            todayWeight = cursor.getString(3);
        }
        else{
            // 오늘자 데이터가 아무것도 없으면 날짜만 가지는 데이터를 추가한다.
            DM.i(this, "insert today_order & today ");
            dbHandler.insert(today_order, today, "", 0, 0, "");
            todayWeight = "";
        }
        cursor.close();



        /* data 전체 조회 */
        cursor = dbHandler.selectAll();
        DM.i(this, "cursor count:" + cursor.getCount());

        /* list data 생성. */
        // TODO: Custom CursorAdapter
        if(cursor.getCount() > 0) {
            do {
                DM.i(this, "index 0:" + cursor.getString(0) + " / index 1:" + cursor.getString(1) + " / index 2:" + cursor.getString(2)
                        + " / index 3:" + cursor.getInt(3) + " / index 4:" + cursor.getInt(4) + " / index 5:" + cursor.getString(5) + " / index 6:" + cursor.getString(6));

                arrayDailyInfo.add(new DailyInfo(cursor.getString(2), Util.addFloatingPoint(cursor.getString(3)), cursor.getInt(4) == 1 ? true : false
                        , cursor.getInt(5) == 1 ? true : false, cursor.getString(6)));
            } while (cursor.moveToPrevious());
        }

        cursor.close();
        dbHandler.close();

        /* adapter 생성 */
        dailyInfoAdapter = new DailyInfoAdapter(this, arrayDailyInfo);

        /* ListView 에 adapter 설정 */
        dailyInfoListView = (ListView)findViewById(R.id.list_dailyinfo);
        dailyInfoListView.setAdapter(dailyInfoAdapter);

        /* ListView click listener 설정 */
        dailyInfoListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        dailyInfoListView.setOnItemClickListener(this);

        dialogManager = new DialogManager(mContext, mainHandler);

        // 오늘자 팝업을 띄우기 위한 처리.
        todayCheckPref = getSharedPreferences("today_check_pref", MODE_PRIVATE);
        todayPrefEditor = todayCheckPref.edit();

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        VersionControl versionControl = new VersionControl(this);
        versionControl.checkVersionUpgrade();
    } // onCreate()

    /* tool bar 에 menu 설정 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();

        DM.v(this, "onResume() / todayWeight:" + todayWeight);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        bAutoInputPopup = prefs.getBoolean("auto_input_key", false);

        DM.i("start on popup:" + prefs.getBoolean("auto_input_key", false));
        DM.i(this, "today pref check:" + todayCheckPref.getString("today_check_pref", "0000-00-00") + " / today weight:" + todayWeight);

        mainHandler.sendEmptyMessage(ConstValues.MAIN_HANDLER_MSG_REFRESH);// update listview


        /* 오늘자 자동 입력 팝업 처리 - 하루에 한번만 뜨도록 처리.*/
        if(bAutoInputPopup && !todayCheckPref.getString("today_check_pref", "0000-00-00").equals(today)){
//                && (todayWeight.equals("00") || todayWeight.equals("0") || todayWeight.equals("00.00"))){
            DM.v(this, "오늘 자동 팝업을 한번만 띄운다. ");
            dialogManager.showInputDialog();
        }

        todayPrefEditor.putString("today_check_pref", today);
        todayPrefEditor.commit();
    }

    /* tool bar menu 선택 처리 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case android.R.id.home:
                DM.i(this, "tool bar - R.id.home:");
                break;

            case R.id.menu_write: // 쓰기 선택 - 입력 popup 띄워야 함
                DM.i(this, "tool bar - R.id.menu_write:");
                dialogManager.showInputDialog();
                break;

            case R.id.menu_setting:
                DM.i(this, "tool bar - R.id.menu_setting:");
                Intent intent = new Intent(this, PrefActivity.class);
                startActivity(intent);
                break;

            //개발시 사용한 코드임. - ConstValues.FOR_DEV
            /*case R.id.dev_cleartable:
                DM.i(this, "clear data in di table.");
                if(ConstValues.FOR_DEV)
                {
                    dbHandler = DBHandler.open(this);
                    dbHandler.deleteAll();
                    dbHandler.close();
                    mainHandler.sendEmptyMessage(ConstValues.MAIN_HANDLER_MSG_REFRESH);
                }
                break;*/
        }
        return super.onOptionsItemSelected(item);
    }

    /* ListView Item 선택 처리 */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        DM.i(this, "onItemClick / position:" + position + " / date string:" + arrayDailyInfo.get(position).getStrDate());

        dialogManager.showEditDialog(arrayDailyInfo.get(position).getStrDate(), arrayDailyInfo.get(position).getStrWeight()
                , arrayDailyInfo.get(position).isbExercise(), arrayDailyInfo.get(position).isbDrink()
                , arrayDailyInfo.get(position).getStrMemo());
    }

    /* 백키처리 - Press the 'Back' button one more to exit - */
    @Override
    public void onBackPressed() {
        DM.i(this, "onBackPressed()");
        if((System.currentTimeMillis() - l_BackKey_RepeatTimer) < 1500){
            finish();
        }
        else {
            l_BackKey_RepeatTimer = System.currentTimeMillis();
            Toast.makeText(this, R.string.msg_onemorepress, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        DM.i(this, "onDestroy()");

        if(this.cursor != null) cursor.close();
        if(this.dbHandler != null) dbHandler.close();
    }

    public void weakHandleMessage(Message msg){
        DM.i("weakHandleMessage()");

        switch(msg.what) {
            case ConstValues.MAIN_HANDLER_MSG_REFRESH: {
                DM.i(this, "ConstValues.MAIN_HANDLER_MSG_REFRESH:(리스트 업데이트)");

                /* data 조회 */
                if (cursor != null) cursor.close();
                if (dbHandler != null) dbHandler.close();
                if (arrayDailyInfo != null) arrayDailyInfo.clear();

                dbHandler = DBHandler.open(mContext);
                cursor = dbHandler.selectAll();

                /* arraylist data 생성. */
                // TODO: Custom CursorAdapter
                if (cursor.getCount() > 0) {
                    do {
                        DM.i(this, "index 0:" + cursor.getString(0) + " / index 1:" + cursor.getString(1) + " / index 2:" + cursor.getString(2)
                                + " / index 3:" + cursor.getInt(3) + " / index 4:" + cursor.getInt(4) + " / index 5:" + cursor.getString(5));

                        arrayDailyInfo.add(new DailyInfo(cursor.getString(2), Util.addFloatingPoint(cursor.getString(3)), cursor.getInt(4) == 1 ? true : false
                                , cursor.getInt(5) == 1 ? true : false, cursor.getString(6)));
                    } while (cursor.moveToPrevious());
                }

                cursor.close();
                dbHandler.close();

                dailyInfoAdapter.notifyDataSetChanged();
                break;
            }
            case ConstValues.MAIN_HANDLER_MSG_UPGRADE:{
                DM.i(this, "ConstValues.MAIN_HANDLER_MSG_UPGRADE:");

                new AlertDialog.Builder(this).setTitle(R.string.upgrade_title).setMessage(R.string.upgrade_msg)
                        .setCancelable(false)
                        .setNegativeButton(R.string.dialog_btn_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ;
                            }
                        })
                        .setPositiveButton(R.string.dialog_btn_confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mainHandler.sendEmptyMessage(ConstValues.MAIN_HANDLER_MSG_GO_MARKET);
                            }
                        }).show();

                break;
            }
            case ConstValues.MAIN_HANDLER_MSG_GO_MARKET:{
                Uri uri = Uri.parse(ConstValues.UPGRADE_PAGE_URL);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            }
        }
    }

    public void sendUpgradeMsg(){
        DM.i(this, "sendUpgradeMsg()");
        mainHandler.sendEmptyMessageDelayed(ConstValues.MAIN_HANDLER_MSG_UPGRADE, 1000);
    }
}
