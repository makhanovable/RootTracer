package com.takealookonit.roottracer.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Makhanov Madiyar
 * Date: 1/28/18.
 */

public class LastDB extends SQLiteOpenHelper {

    public LastDB(Context context) {
        super(context, "last", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE lastroute ( _id INTEGER PRIMARY KEY, route TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS lastroute");
        onCreate(sqLiteDatabase);
    }

}