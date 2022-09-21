package com.example.lab5;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String TABLE_NAME = "DatabaseHelper";
    private static final String COL1 = "id";
    private static final String COL2 = "datetime";
    private static final String COL3 = "location";

    public DatabaseHelper (Context context) {
        super(context, TABLE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME +
                " (" + COL1 + " INTEGER PRIMARY KEY, " + COL2 + " TEXT, " + COL3 + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addData (int platenumber, String datetime, String location) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, platenumber);
        contentValues.put(COL2, datetime);
        contentValues.put(COL3, location);

        Log.d(TAG, "addData: Adding " + platenumber + ", " + datetime + ", " + location + " to " + TABLE_NAME);

        long result = db.insert(TABLE_NAME, null, contentValues);

        //if data was inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }
}
