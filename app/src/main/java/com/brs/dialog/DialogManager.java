package com.brs.dialog;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.brs.constvalue.ConstValues;
import com.brs.dailyweightmonitor.DailyInfo;
import com.brs.db.DBHandler;
import com.brs.dailyweightmonitor.R;
import com.brs.utils.DM;
import com.brs.utils.Util;

import java.util.Calendar;

/**
 * Created by ikban on 2015-10-27.
 */
public class DialogManager implements Dialog.OnClickListener, View.OnClickListener
        , DatePickerDialog.OnDateSetListener{

    // private DatePickerDialog mDatePicker;
    private Context mContext;
    private LayoutInflater mInflater;
    private View mLayout;

    private Button mBtnDate;

    private Handler mainHandler;

    private String[] tmpArrayString;

    private DailyInfo dailyInfo;

    /* DialogManager constructor. */
    public DialogManager(Context context, Handler handler){
        DM.i("DialogManager class constructor.");
        mContext = context;
        mainHandler = handler;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /* 체중 입력 Dialog 준비하고 보이기. <--*/
    private View makeInputDialogLayout()
    {
        Calendar calendar = Calendar.getInstance();
        DM.i(this, "makeInputDialogLayout()");

        mLayout = mInflater.inflate(R.layout.input_dialog, null);
        mBtnDate = (Button)mLayout.findViewById(R.id.btn_date);
        mBtnDate.setText(Util.convertDateWithDash(calendar.get(Calendar.YEAR)
                , calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
        mBtnDate.setOnClickListener(this);

        return mLayout;
    }

    public void showInputDialog(){
        DM.i(this, "showInputDialog()");

        new AlertDialog.Builder(mContext).setTitle(R.string.dialog_title)
                .setView(makeInputDialogLayout())
                .setNegativeButton(R.string.dialog_btn_cancel, this)
                .setPositiveButton(R.string.dialog_btn_confirm, this)
                .show();
    }
    /*--> 체중 입력.*/


    /* 체중 수정 준비하고, 보이기 <-- */
    public void showEditDialog(final String date, String weight, boolean exercise, boolean drink, String memo)
    {
        DM.i(this, "showEditDialog() / date:" + date + " / weight:" + weight + " / exercise:" + exercise + " / drink:" + drink + " / memo:" + memo);

        makeInputDialogLayout(); // layout inflate.

        mBtnDate = (Button)mLayout.findViewById(R.id.btn_date);
        mBtnDate.setText(date);
        mBtnDate.setOnClickListener(this);

        EditText et_weight = (EditText)mLayout.findViewById(R.id.et_weight);
        et_weight.setText(weight);

        CheckBox cb_exercise = (CheckBox)mLayout.findViewById(R.id.cb_exercise);
        if(exercise) cb_exercise.setChecked(true);
        else cb_exercise.setChecked(false);

        CheckBox cb_drink = (CheckBox)mLayout.findViewById(R.id.cb_drink);
        if(drink) cb_drink.setChecked(true);
        else cb_drink.setChecked(false);

        EditText et_memo = (EditText)mLayout.findViewById(R.id.et_memo);
        et_memo.setText(memo);

        dailyInfo = new DailyInfo(date, weight, exercise, drink, memo);

        new AlertDialog.Builder(mContext).setTitle(R.string.dialog_title)
                .setView(mLayout)
                .setNegativeButton(R.string.dialog_btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DM.i(this, "수정 다이얼로그 취소 !");
                    }
                })
                .setPositiveButton(R.string.dialog_btn_edit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DM.i(this, "수정 다이얼로그 확인 !");

                        EditText et_weight = (EditText)mLayout.findViewById(R.id.et_weight);
                        CheckBox cb_exercise = (CheckBox)mLayout.findViewById(R.id.cb_exercise);
                        CheckBox cb_drink = (CheckBox)mLayout.findViewById(R.id.cb_drink);
                        EditText et_memo = (EditText)mLayout.findViewById(R.id.et_memo);

                        dailyInfo = new DailyInfo(mBtnDate.getText().toString(), et_weight.getText().toString()
                                                , cb_exercise.isChecked(), cb_drink.isChecked()
                                                , et_memo.getText().toString());

                        DM.i(this, "mButton:" + mBtnDate.getText().toString() + " / weight:" + et_weight.getText().toString()
                            + " / cb_exercise:" + cb_exercise.getText().toString());


                        DBHandler dbHandler = DBHandler.open(mContext); // db 연결.
                        Cursor cursor = dbHandler.findDate(dailyInfo.getStrDate());

                        int Exercise = 0;
                        if(dailyInfo.isbExercise()) Exercise = 1;
                        else Exercise = 0;

                        int Drink = 0;
                        if(dailyInfo.isbDrink()) Drink = 1;
                        else Drink = 0;

                        if(cursor.getCount() > 0){
                            dbHandler.update(Util.convertDateToDateOrder(dailyInfo.getStrDate()), dailyInfo.getStrDate()
                                    , dailyInfo.getStrWeight(), Exercise, Drink, dailyInfo.getStrMemo());
                        }
                        else{
                            dbHandler.insert(Util.convertDateToDateOrder(dailyInfo.getStrDate()), dailyInfo.getStrDate()
                                    , dailyInfo.getStrWeight(), Exercise, Drink, dailyInfo.getStrMemo());
                        }

                        cursor.close();
                        dbHandler.close();

                        mainHandler.sendEmptyMessage(ConstValues.MAIN_HANDLER_MSG_REFRESH);// update listview
                    }
                })
                .show();
    }
    /* -->  체중 수정 준비하고, 보이기 */


    @Override
    public void onClick(DialogInterface dialog, int which) {
        DM.i(this, "onClick() / which:" + which);

        switch(which){
            case DialogInterface.BUTTON_POSITIVE:
                DM.i(this, "확인 버튼 / " + mBtnDate.getText().toString());

                DBHandler dbHandler = DBHandler.open(mContext); // db 연결.

                /* 입력처리 할 값을 준비. */
                // 날짜
                String date = mBtnDate.getText().toString(); // 날짜.
                int date_order = Util.convertDateToDateOrder(date); // 날짜 스트링 값을 order 처리 위한 int 변환.

                // 몸무게
                TextView tv_weight = (TextView)mLayout.findViewById(R.id.et_weight);
                String weight = tv_weight.getText().toString(); // 몸무게.

                // 운동체크
                CheckBox cb_exercise = (CheckBox)mLayout.findViewById(R.id.cb_exercise);
                int exercise = 0;
                if(cb_exercise.isChecked())
                    exercise = 1;
                else
                    exercise = 0;

                // 음주체크
                CheckBox cb_drink = (CheckBox)mLayout.findViewById(R.id.cb_drink);
                int drink = 0;
                if(cb_drink.isChecked())
                    drink = 1;
                else
                    drink = 0;

                // 메모
                TextView tv_memo = (TextView)mLayout.findViewById(R.id.et_memo);
                String memo = tv_memo.getText().toString();

                DM.i(this, "입력값 확인 / date:" + date + " / weight:" + weight
                        + " / exercise:" + exercise + " / drink:" + drink + " / memo:" + memo);

                Cursor cursor = dbHandler.findDate(date);

                if(cursor.getCount() > 0)
                {
                    DM.i(this, "해당 날짜의 자료가 이미 있음.");
                    dbHandler.update(date_order, date, weight, exercise, drink, memo);
                }
                else{

                    dbHandler.insert(date_order, date, weight, exercise, drink, memo);
                }

                cursor.close();
                dbHandler.close();

                mainHandler.sendEmptyMessage(ConstValues.MAIN_HANDLER_MSG_REFRESH);// update listview
                break;

            case DialogInterface.BUTTON_NEGATIVE:
                DM.i(this, "취소 버튼");

                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_date:
                DM.i(this, "onClick() / R.id.btn_date");

                /* DatePicker 처리. */
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePicker = new DatePickerDialog(mContext, this, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePicker.show();
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        DM.i(this, "onDateSet() " + Util.convertDateWithDash(year, monthOfYear + 1, dayOfMonth));
        mBtnDate.setText(Util.convertDateWithDash(year, monthOfYear + 1, dayOfMonth));
    }
}
