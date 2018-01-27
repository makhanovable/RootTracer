package com.takealookonit.roottracer.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Makhanov Madiyar
 * Date: 1/27/18.
 */

public class EmailDB extends SQLiteOpenHelper {

    public EmailDB(Context context) {
        super(context, "email", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE ema ( _id INTEGER PRIMARY KEY, email TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS ema");
        onCreate(sqLiteDatabase);
    }

}