package com.smartcave.app.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.smartcave.app.data.DBContract;
import com.smartcave.app.data.DBHelper;

public class ApplianceProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static final String AUTHORITY = "com.smartcave.app.provider";

    public static final int APPLIANCES = 1;
    public static final int APPLIANCE = 2;
    public static final int ROOMS = 3;
    public static final int ROOM = 4;
    public static final int LOGS = 5;
    public static final int LOG = 6;

    static{
        sUriMatcher.addURI(AUTHORITY, "appliance", APPLIANCES);
        sUriMatcher.addURI(AUTHORITY, "appliance/#", APPLIANCE);
        sUriMatcher.addURI(AUTHORITY, "room", ROOMS);
        sUriMatcher.addURI(AUTHORITY, "room/#", ROOM);
        sUriMatcher.addURI(AUTHORITY, "log", LOGS);
        sUriMatcher.addURI(AUTHORITY, "log/#", LOG);
    }

    private DBHelper mDBHelper;

    public ApplianceProvider() {

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            case APPLIANCE:
                long id = ContentUris.parseId(uri);
                selection = DBContract.Appliance._ID + "=?";
                selectionArgs = new String[]{Long.toString(id)};
                int rowCount = mDBHelper.getWritableDatabase().delete(
                        DBContract.Appliance.TABLE_NAME,
                        selection,
                        selectionArgs
                );

                getContext().getContentResolver().notifyChange(uri, null);
                return rowCount;
            default:
                return 0;
        }
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (sUriMatcher.match(uri)){
            case APPLIANCES:
                long rowId = mDBHelper.getWritableDatabase().insert(
                        DBContract.Appliance.TABLE_NAME,
                        null,
                        values
                );

                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, rowId);

            case LOGS:
                long rowId2 = mDBHelper.getWritableDatabase().insert(
                        DBContract.Log.TABLE_NAME,
                        null,
                        values
                );

                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, rowId2);
            case ROOMS:
                long rowId3 = mDBHelper.getWritableDatabase().insert(
                        DBContract.Room.TABLE_NAME,
                        null,
                        values
                );

                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, rowId3);
        }

        return null;
    }

    @Override
    public boolean onCreate() {
        mDBHelper = new DBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        switch (sUriMatcher.match(uri)) {
            case APPLIANCES:
                Cursor cursor = mDBHelper.getReadableDatabase().query(
                        DBContract.Appliance.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;
            case LOGS:
                Cursor cursor3 = mDBHelper.getReadableDatabase().query(
                        DBContract.Log.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

                cursor3.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor3;
            case ROOMS:
                Cursor cursor2 = mDBHelper.getReadableDatabase().query(
                        DBContract.Room.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

                cursor2.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor2;
        }

        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            case APPLIANCE:
                long id = ContentUris.parseId(uri);
                selection = DBContract.Appliance._ID + "=?";
                selectionArgs = new String[]{Long.toString(id)};
                int rowCount = mDBHelper.getWritableDatabase().update(
                        DBContract.Appliance.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );

                getContext().getContentResolver().notifyChange(uri, null);
                return rowCount;

            default:
                return 0;
        }
    }
}
