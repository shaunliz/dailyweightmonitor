package com.brs.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.brs.utils.DM;

/**
 * Created by ikban on 2015-10-27.
 */
public class DBHandler {

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    private DBHandler(Context context){
        this.dbHelper = new DBHelper(context);
        this.db = dbHelper.getWritableDatabase();
    }

    public static DBHandler open(Context context) throws SQLiteException{
        DBHandler handler = new DBHandler(context);

        return handler;
    }

    public void close(){
        dbHelper.close();
    }

    public long insert(int date_order, String date, String weight, int exercise, int drink, String memo){

        DM.i(this, "insert() / date:" + date + " / weight:" + weight
            + " / exercise:" + exercise + " / drink:" + drink + " / memo:" + memo);

        ContentValues values = new ContentValues();
        values.put("date_order", date_order);
        values.put("date", date);
        values.put("weight", weight);
        values.put("exercise", exercise);
        values.put("drink", drink);
        values.put("memo", memo);

        return db.insert("di", null, values);
    }

    public Cursor selectAll() throws  SQLiteException{

        DM.i(this, "selectAll()");

        // Cursor cursor = db.query(true, "di", null, null, null, null, null, null, null);
        Cursor cursor = db.query(true, "di", null, null, null, null, null, "date_order asc", null);

        if(cursor != null)
            cursor.moveToLast();

        return cursor;
    }

    public Cursor findDate(String date){
        DM.i(this, "findDate() / date:" + date);
        Cursor cursor = db.query(true, "di", null, "date=?", new String[]{date}, null, null, null, null);

        if(cursor != null)
            cursor.moveToLast();

        return cursor;
    }

    public long update(int date_order, String date, String weight, int exercise, int drink, String memo){
        DM.i(this, "update() / date:" + date + " / weight:" + weight
                + " / exercise:" + exercise + " / drink:" + drink + " / memo:" + memo);

        ContentValues values = new ContentValues();
        values.put("date_order", date_order);
        values.put("date", date);
        values.put("weight", weight);
        values.put("exercise", exercise);
        values.put("drink", drink);
        values.put("memo", memo);

        return db.update("di", values, "date=?", new String[]{date});
    }

    public void deleteAll() throws SQLiteException{
        DM.e(this, "deleteAll()");
        db.delete("di", null, null);
    }
}
