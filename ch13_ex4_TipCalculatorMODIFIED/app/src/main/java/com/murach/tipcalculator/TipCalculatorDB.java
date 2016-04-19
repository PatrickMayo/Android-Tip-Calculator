package com.murach.tipcalculator;

/**
 * Created by Patrick on 3/3/2016.
 */

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class TipCalculatorDB {

    //Database Constants
    public static final String DB_NAME = "tipcalculator.db";
    public static  final int   DB_VERSION = 2;

    //Table constants
    public static final String TIP_TABLE = "tip";

    public static final String TIP_ID = "_id";
    public static final int TIP_ID_COL = 0;

    public static final String BILL_DATE = "bill_date";
    public static final int BILL_DATE_COL = 1;

    public static final String BILL_AMOUNT = "bill_amount";
    public static final int BILL_AMOUNT_COL = 2;

    public static final String TIP_PERCENT = "tip_percent";
    public static final int TIP_PERCENT_COL = 3;

    //Create Table statement
    public static final String CREATE_TIP_TABLE =
            "CREATE TABLE " + TIP_TABLE + " (" +
                    TIP_ID    + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    BILL_DATE   + " INTEGER ,    " +
                    BILL_AMOUNT + " REAL    ,    " +
                    TIP_PERCENT + " REAL    );";

    //Drop Table statement
    public static final String DROP_TIP_TABLE =
            "DROP TABLE IF EXISTS " + TIP_TABLE;

    public static class DBHelper extends SQLiteOpenHelper{

        public DBHelper(Context context, String name, CursorFactory factory, int version){
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(CREATE_TIP_TABLE);

            //Insert test data
            //db.execSQL("INSERT INTO tip VALUES (1, 400000, 20.00, 10)");
            //db.execSQL("INSERT INTO tip VALUES (2, 5000000, 50.00, 35)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            Log.d("Table", "Upgrading db from version " + oldVersion + " to " + newVersion);

            db.execSQL(TipCalculatorDB.DROP_TIP_TABLE);
            onCreate(db);
        }
    }

    //Database object and database helper object
    private SQLiteDatabase db;
    private DBHelper dbHelper;

    //Constructor
    public TipCalculatorDB(Context context){
        dbHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
    }

    //Private methods
    private void openReadableDB(){ db = dbHelper.getReadableDatabase(); }
    private void openWriteableDB(){
        db = dbHelper.getWritableDatabase();
    }
    private void closeDB(){
        if(db != null)
            db.close();
    }
    private void closeCursor(Cursor cursor){
        if(cursor != null)
            cursor.close();
    }

    private static Tip getTipFromCursor(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0){
            return null;
        }
        else {
            try {
                Tip tip = new Tip(
                        cursor.getLong(TIP_ID_COL),
                        cursor.getLong(BILL_DATE_COL),
                        cursor.getFloat(BILL_AMOUNT_COL),
                        cursor.getFloat(TIP_PERCENT_COL));
                return tip;
            }
            catch(Exception e) {
                return null;
            }
        }
    }

    //Public methods
    public ArrayList<Tip> getTips(){

        this.openReadableDB();
        Cursor cursor = db.query(TIP_TABLE, null,
                                null, null,
                                null, null, null);

        ArrayList<Tip> tips = new ArrayList<Tip>();
        while(cursor.moveToNext()){
            tips.add(getTipFromCursor(cursor));
        }
        this.closeCursor(cursor);
        this.closeDB();

        return tips;
    }

    public Tip getTip(long tipID) {
        String where = TIP_ID + "= ?";
        String[] whereArgs = { Long.toString(tipID) };

        this.openReadableDB();
        Cursor cursor = db.query(TIP_TABLE,
                null, where, whereArgs, null, null, null);
        cursor.moveToFirst();
        Tip tip = getTipFromCursor(cursor);
        this.closeCursor(cursor);
        this.closeDB();
        return tip;
    }

    public long saveTip(Tip tip){
        ContentValues cv = new ContentValues();
        cv.put(TIP_ID, tip.getId());
        cv.put(BILL_DATE, tip.getDateMillis());
        cv.put(BILL_AMOUNT, tip.getBillAmount());
        cv.put(TIP_PERCENT, tip.getTipPercent());

        this.openWriteableDB();
        long rowID = db.insert(TIP_TABLE, null, cv);
        this.closeDB();
        return rowID;
    }

    public Tip getLastTip(){
        this.openReadableDB();
        Cursor cursor = db.query(TIP_TABLE,
                null, null, null, null, null, BILL_DATE);

        cursor.moveToLast();
        Tip tip = getTipFromCursor(cursor);

        this.closeCursor(cursor);
        this.closeDB();
        return tip;
    }

    public float getAvgTipPerc(){
        this.openReadableDB();
        String column[] = {"AVG(" + TIP_PERCENT + ")"};

        Cursor cursor = db.query(TIP_TABLE, column,
                null, null,
                null, null, null);

        cursor.moveToFirst();
        float avg = cursor.getFloat(0);
        this.closeCursor(cursor);
        this.closeDB();

        return avg;
    }

    public int deleteTip(long id){
        String where = TIP_ID + "= ?";
        String[] whereArgs = { String.valueOf(id) };

        this.openWriteableDB();
        int rowCount = db.delete(TIP_TABLE, where, whereArgs);
        this.closeDB();

        return rowCount;
    }
}