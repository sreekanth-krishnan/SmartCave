package com.smartcave.app.data;

/**
 * Created by USER on 25-05-2016.
 */
public class Queries {

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    public static final String SQL_CREATE_APPLIANCES =
            "CREATE TABLE " + DBContract.Appliance.TABLE_NAME + " (" +
                    DBContract.Appliance._ID + " INTEGER PRIMARY KEY," +
                    DBContract.Appliance.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    DBContract.Appliance.COLUMN_TYPE + INTEGER_TYPE + COMMA_SEP +
                    DBContract.Appliance.COLUMN_ID + TEXT_TYPE + COMMA_SEP +
                    DBContract.Appliance.COLUMN_ROOM_ID + INTEGER_TYPE + COMMA_SEP +
                    DBContract.Appliance.COLUMN_STATE + INTEGER_TYPE + COMMA_SEP +
                    DBContract.Appliance.COLUMN_STATE_TIME + INTEGER_TYPE + COMMA_SEP +
                    DBContract.Appliance.COLUMN_OPERATED_TIME + INTEGER_TYPE + " DEFAULT 0" + COMMA_SEP +
                    DBContract.Appliance.COLUMN_POWER_RATING + INTEGER_TYPE + " DEFAULT 0" +
                    " )";

    public static final String SQL_CREATE_ROOMS =
            "CREATE TABLE " + DBContract.Room.TABLE_NAME + " (" +
                    DBContract.Room._ID + " INTEGER PRIMARY KEY," +
                    DBContract.Room.COLUMN_NAME + TEXT_TYPE +
                    " )";

    public static final String SQL_ADD_ROOM_ID_APPLIANCE =
            "ALTER TABLE " + DBContract.Appliance.TABLE_NAME + " ADD COLUMN " +
                    DBContract.Appliance.COLUMN_ROOM_ID + INTEGER_TYPE ;

    public static final String SQL_ADD_STATE_APPLIANCE =
            "ALTER TABLE " + DBContract.Appliance.TABLE_NAME + " ADD COLUMN " +
                    DBContract.Appliance.COLUMN_STATE + INTEGER_TYPE ;

    public static final String SQL_ADD_STATE_TIME_APPLIANCE =
            "ALTER TABLE " + DBContract.Appliance.TABLE_NAME + " ADD COLUMN " +
                    DBContract.Appliance.COLUMN_STATE_TIME + INTEGER_TYPE ;

    public static final String SQL_CREATE_LOGS =
            "CREATE TABLE " + DBContract.Log.TABLE_NAME + " (" +
                    DBContract.Log._ID + " INTEGER PRIMARY KEY," +
                    DBContract.Log.COLUMN_APPLIANCE+ INTEGER_TYPE +
                    DBContract.Log.COLUMN_ON_TIME+ INTEGER_TYPE +
                    DBContract.Log.COLUMN_OFF_TIME+ INTEGER_TYPE +
                    " )";
    public static final String SQL_ADD_TOTAL_OPERATED_TIME_APPLIANCE =
            "ALTER TABLE " + DBContract.Appliance.TABLE_NAME + " ADD COLUMN " +
                    DBContract.Appliance.COLUMN_OPERATED_TIME + INTEGER_TYPE + " DEFAULT 0" ;

    public static final String SQL_ADD_POWER_RATING_APPLIANCE =
            "ALTER TABLE " + DBContract.Appliance.TABLE_NAME + " ADD COLUMN " +
                    DBContract.Appliance.COLUMN_POWER_RATING + INTEGER_TYPE + " DEFAULT 0" ;
}
