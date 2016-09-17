package com.smartcave.app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by USER on 25-05-2016.
 */
public class DBHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "Appliance.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Queries.SQL_CREATE_APPLIANCES);
        db.execSQL(Queries.SQL_CREATE_ROOMS);
        db.execSQL(Queries.SQL_CREATE_LOGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion == 1 && newVersion == 2){
            db.execSQL(Queries.SQL_ADD_ROOM_ID_APPLIANCE);
            db.execSQL(Queries.SQL_CREATE_ROOMS);
        }

        if(newVersion == 3){
            db.execSQL(Queries.SQL_ADD_STATE_APPLIANCE);
            db.execSQL(Queries.SQL_ADD_STATE_TIME_APPLIANCE);
            db.execSQL(Queries.SQL_CREATE_LOGS);
        }

        if(newVersion == 4){
            db.execSQL(Queries.SQL_ADD_TOTAL_OPERATED_TIME_APPLIANCE);
        }

        if(newVersion == 5){
            db.execSQL(Queries.SQL_ADD_POWER_RATING_APPLIANCE);
        }
    }
}
