package com.brs.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.brs.utils.DM;

/**
 * Created by ikban on 2015-10-27.
 */
public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context){
        super(context, "distorage.db", null, 1);
        DM.i(this, "DBHelper()");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        DM.i(this, "onCreate");

        /* 날짜 순서로 정렬하기 위해서 date_order 값을 추가 하였다
        * date 를 2015-01-01 형태의 텍스트로 넣고 비교할 예정이라 sorting 에 어려움이 있어 동일한 값을
        * INTEGER 형태로 20150101로 함께 넣기로 하였다. 날짜별 정렬에 date_order 컬럼을 사용하자. */
        String table = "CREATE TABLE di (_id INTEGER PRIMARY KEY AUTOINCREMENT, date_order INT, date TEXT NOT NULL, weight TEXT, exercise INT, drink INT, memo TEXT);";
        db.execSQL(table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DM.i(this, "onUpgrade");

        db.execSQL("DROP TABLE IF EXISTS di");
        onCreate(db);
    }
}
