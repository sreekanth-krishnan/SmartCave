package com.smartcave.app.data;

import android.net.Uri;
import android.provider.BaseColumns;

import com.smartcave.app.providers.ApplianceProvider;

/**
 * Created by USER on 25-05-2016.
 */
public final class DBContract {
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + ApplianceProvider.AUTHORITY);

    public static abstract class Appliance implements BaseColumns{
        public static final String TABLE_NAME = "appliance";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_ROOM_ID = "room_id";
        public static final String COLUMN_STATE = "state";
        public static final String COLUMN_STATE_TIME = "state_time";
        public static final String COLUMN_OPERATED_TIME = "operated_time";
        public static final String COLUMN_POWER_RATING = "power_rating";
    }

    public static abstract class Room implements BaseColumns{
        public static final String TABLE_NAME = "room";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();
        public static final String COLUMN_NAME = "name";
    }

    public static abstract class Log implements BaseColumns{
        public static final String TABLE_NAME = "log";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();
        public static final String COLUMN_APPLIANCE = "appliance";
        public static final String COLUMN_ON_TIME = "on_time";
        public static final String COLUMN_OFF_TIME = "off_time";
    }
}
