package com.smartcave.app;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.smartcave.app.data.DBContract;
import com.smartcave.app.loaders.CursorRecyclerViewAdapter;
import com.smartcave.app.util.CalcUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

/**
 * Created by USER on 25-05-2016.
 */
public class AppliancesFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int APPLIANCE_LOADER = 1;
    private RecyclerView mRecyclerView;
    private AppliancesAdapter appliancesAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.fragment_appliances);

    }

    // fast way to call Toast
    private void msg(String s) {
        Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setup();

        loadData();
    }

    private void loadData() {
        getLoaderManager().initLoader(APPLIANCE_LOADER, null, this);
    }

    private void setup() {

        mRecyclerView = (RecyclerView) this.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));//new GridLayoutManager(getContext(), 2));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));

        appliancesAdapter = new AppliancesAdapter(getContext(), null);
        mRecyclerView.setAdapter(appliancesAdapter);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{
                DBContract.Appliance._ID,
                DBContract.Appliance.COLUMN_NAME,
                DBContract.Appliance.COLUMN_TYPE,
                DBContract.Appliance.COLUMN_ID,
                DBContract.Appliance.COLUMN_STATE,
                DBContract.Appliance.COLUMN_STATE_TIME,
                DBContract.Appliance.COLUMN_OPERATED_TIME,
                DBContract.Appliance.COLUMN_POWER_RATING
        };

        return new CursorLoader(getContext(),
                DBContract.Appliance.CONTENT_URI,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        appliancesAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        appliancesAdapter.swapCursor(null).close();
    }

    private void onSwitchClicked(final long l, String id, final int state, final long stateTime, final long currentOperatedTime) {


        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... voids) {
                updateUsage(l, state, stateTime, currentOperatedTime);
                return null;
            }
        }.execute();


        sendCommand(id);
    }

    private void sendCommand(String p) {
        ((HomeActivity)getActivity()).sendCommand(p);
    }

    private void updateUsage(long l, int state, long stateTime, long currentOperatedTime) {
        int newState = state == 0 ? 1 : 0;

        long now = System.currentTimeMillis();

        ContentValues values = new ContentValues();
        values.put(DBContract.Appliance.COLUMN_STATE, newState);
        values.put(DBContract.Appliance.COLUMN_STATE_TIME, now);

        if (newState == 0) {
            long operatedTime = now - stateTime;

            //TODO value may change by this time. Use transaction?
            values.put(DBContract.Appliance.COLUMN_OPERATED_TIME, currentOperatedTime + operatedTime);
        }

        getContext().getContentResolver().update(ContentUris.withAppendedId(DBContract.Appliance.CONTENT_URI, l),
                values,
                null,
                null);
    }


    class AppliancesAdapter extends CursorRecyclerViewAdapter<AppliancesAdapter.ViewHoder> {
        public AppliancesAdapter(Context context, Cursor cursor) {
            super(context, cursor);
            setHasStableIds(true);
        }

        @Override
        public void onBindViewHolder(ViewHoder viewHolder, Cursor cursor) {
            viewHolder.bind(cursor);
        }

        @Override
        public ViewHoder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_appliance, parent, false);
            ViewHoder vh = new ViewHoder(itemView);
            return vh;
        }

        class ViewHoder extends RecyclerView.ViewHolder {

            private final TextView mTextView;
            private final SwitchCompat mSwitch;
            private final TextView mUsageView;
            private final Chronometer mChronometer;
            private String commandCode;
            private long id;
            private int state;
            private long stateTime;
            private long operatedTime;
            private long powerRating;

            public ViewHoder(View itemView) {
                super(itemView);

                mTextView = (TextView) itemView.findViewById(R.id.text_appliance_name);
                mUsageView = (TextView) itemView.findViewById(R.id.text_appliance_usage);
                mChronometer = (Chronometer) itemView.findViewById(R.id.chronometer);
                mSwitch = (SwitchCompat) itemView.findViewById(R.id.switch_appliance);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mSwitch.setChecked(state == 0);
//                        mSwitch.setText(state == 0 ? "ON" : "OFF");
                        onSwitchClicked(id, commandCode, state, stateTime, operatedTime);
                    }
                });

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        deleteAppliance(id);
                        return true;
                    }
                });
            }

            public void bind(Cursor cursor) {
                String applianceName = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Appliance.COLUMN_NAME));
                mTextView.setText(applianceName);
                commandCode = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Appliance.COLUMN_ID));
                id = cursor.getLong(cursor.getColumnIndexOrThrow(DBContract.Appliance._ID));
                state = cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.Appliance.COLUMN_STATE));
                stateTime = cursor.getLong(cursor.getColumnIndexOrThrow(DBContract.Appliance.COLUMN_STATE_TIME));
                operatedTime = cursor.getLong(cursor.getColumnIndexOrThrow(DBContract.Appliance.COLUMN_OPERATED_TIME));
                powerRating = cursor.getLong(cursor.getColumnIndexOrThrow(DBContract.Appliance.COLUMN_POWER_RATING));
//                mSwitch.setText(state == 1 ? "ON" : "OFF");
                mSwitch.setChecked(cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.Appliance.COLUMN_STATE)) == 1);

                updateUsage();

                mChronometer.setVisibility(state == 1 ? View.VISIBLE : View.GONE);

                if(state == 1){
                    mChronometer.setBase(SystemClock.elapsedRealtime() - (System.currentTimeMillis() - stateTime));
                    mChronometer.start();
                    mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                        @Override
                        public void onChronometerTick(Chronometer chronometer) {
                            updateUsage();
                        }
                    });
                }else{
                    mChronometer.stop();
                    mChronometer.setOnChronometerTickListener(null);
                }
            }

            private void updateUsage() {
                double unit = CalcUtil.calculateUnitConsumed(powerRating, getOperatedTime()/1000);
                double price = CalcUtil.getExpenseForUnit(unit);
                mUsageView.setText(getString(R.string.usage, unit, price));
            }

            private long getOperatedTime() {
                return operatedTime + (state == 1 ? (System.currentTimeMillis() - stateTime) : 0);
            }
        }
    }

    private void deleteAppliance(long id) {
        getContext().getContentResolver().delete(ContentUris.withAppendedId(DBContract.Appliance.CONTENT_URI, id), null, null);
    }

}
