package com.takealookonit.roottracer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Makhanov Madiyar
 * Date: 1/27/18.
 */

public class RoutsDB extends SQLiteOpenHelper {

    public RoutsDB(Context context) {
        super(context, "points", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE routes ( _id INTEGER PRIMARY KEY, lat TEXT, " +
                "lot TEXT, time TEXT, route TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS routes");
        onCreate(sqLiteDatabase);
    }

}