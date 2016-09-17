package com.smartcave.app;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.smartcave.app.data.DBContract;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

public class HomeActivity extends AppCompatActivity {

    private static final int REQUEST_PAIR_DEVICE = 10;
    private static final int RC_CONFIGURE = 11;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private FloatingActionButton fab;

    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static final String EXTRA_ADDRESS = "address";

    private boolean isBtConnected;
    private BluetoothSocket btSocket;
    private BluetoothAdapter myBluetooth;
    private String address;
    private String mMode;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                addAppliance();
            }
        });

        fab.hide();

        setupViewPager();

        checkDeviceSetup();

//        this.startActivity(new Intent(this, TestActivity.class));


    }

    private void setupViewPager() {

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);

                if(tab.getPosition() == 1){
                    fab.show();
                }else{
                    fab.hide();
                }
            }
        });
    }

    private void checkDeviceSetup() {
        if(TextUtils.isEmpty(PrefUtil.getInstance(this).getConnectedDevice()) && TextUtils.isEmpty(PrefUtil.getInstance(this).getConnectedIp())){
            this.startActivityForResult(new Intent(this, ConfigureActivity.class), RC_CONFIGURE);
        }else{
            initBluetooth();
        }
    }

    private void initBluetooth() {

        address = PrefUtil.getInstance(this).getConnectedDevice();//getArguments().getString(EXTRA_ADDRESS, null); //receive the address of the bluetooth device


        if (TextUtils.isEmpty(address)) {
            address = PrefUtil.getInstance(this).getConnectedIp();
            if (TextUtils.isEmpty(address)) {
//                msg("Not connected");
            } else {
                mMode = "wifi";
            }
        } else {
            mMode = "bluetooth";
            new ConnectBT().execute(); //Call the class to connect
        }
    }


    private void Disconnect() {
        if (btSocket != null) //If the btSocket is busy
        {
            try {
                btSocket.close(); //close connection
            } catch (Throwable e) {
//                msg("Error");
            }
        }
//        finish(); //return to the first layout

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Disconnect();
    }

    private void addAppliance() {
        this.startActivityForResult(new Intent(this, AddApplianceActivity.class), REQUEST_PAIR_DEVICE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_PAIR_DEVICE:
                if(resultCode == RESULT_OK){
                    setupViewPager();
                }
                break;
            case RC_CONFIGURE:
                if(resultCode == RESULT_OK){
                    initBluetooth();
                }
                break;
            default:
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final int APPLIANCE_LOADER = 1;
        private PieChart mChart;
        protected String[] mMonths = new String[] {
                "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
        };

        protected String[] mParties = new String[] {
                "Light 1", "TV", "Bed Room AC", "Others", "Party E", "Party F", "Party G", "Party H",
                "Party I", "Party J", "Party K", "Party L", "Party M", "Party N", "Party O", "Party P",
                "Party Q", "Party R", "Party S", "Party T", "Party U", "Party V", "Party W", "Party X",
                "Party Y", "Party Z"
        };

        protected long[] mValues;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_home, container, false);
            /*TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));*/
            mChart = (PieChart) rootView.findViewById(R.id.chart);


            mChart.setUsePercentValues(true);
            mChart.setDescription("");
            mChart.setExtraOffsets(5, 10, 5, 5);

            mChart.setDragDecelerationFrictionCoef(0.95f);

            mChart.setDrawHoleEnabled(true);
            mChart.setHoleColor(Color.WHITE);

            mChart.setTransparentCircleColor(Color.WHITE);
            mChart.setTransparentCircleAlpha(110);

            mChart.setHoleRadius(58f);
            mChart.setTransparentCircleRadius(61f);

            mChart.setDrawCenterText(true);

            mChart.setRotationAngle(0);
            // enable rotation of the chart by touch
            mChart.setRotationEnabled(true);
            mChart.setHighlightPerTapEnabled(true);

            // mChart.setUnit(" €");
            // mChart.setDrawUnitsInChart(true);

            // add a selection listener
//            mChart.setOnChartValueSelectedListener(this);

//            setData(3, 100);

            mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
            // mChart.spin(2000, 0, 360);

            Legend l = mChart.getLegend();
            l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
            l.setXEntrySpace(7f);
            l.setYEntrySpace(0f);
            l.setYOffset(0f);


            setup();
            return rootView;
        }

        private void setup() {
            loadData();
        }

        private void loadData() {
            getLoaderManager().initLoader(APPLIANCE_LOADER, null, this);
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

            mParties = new String[data.getCount()];
            mValues = new long[data.getCount()];

            int i = 0;
            long total = 0;
            data.moveToPosition(-1);
            while(data.moveToNext()){
                mParties[i] = data.getString(data.getColumnIndexOrThrow(DBContract.Appliance.COLUMN_NAME));
                mValues[i] = data.getLong(data.getColumnIndexOrThrow(DBContract.Appliance.COLUMN_OPERATED_TIME));
                total += mValues[i];
                i++;
            }

//            data.close();
//            appliancesAdapter.changeCursor(data);

            if(mParties.length > 0 && total > 0) {
                setData(mParties.length, total);
            }else {
                mChart.clear();
            }

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
//            appliancesAdapter.swapCursor(null).close();

        }


        private void setData(int count, float range) {

            float mult = range;

            ArrayList<Entry> yVals1 = new ArrayList<Entry>();

            // IMPORTANT: In a PieChart, no values (Entry) should have the same
            // xIndex (even if from different DataSets), since no values can be
            // drawn above each other.
            for (int i = 0; i < count; i++) {
                yVals1.add(new Entry((float) mValues[i], i));
            }

            ArrayList<String> xVals = new ArrayList<String>();

            for (int i = 0; i < count; i++)
                xVals.add(mParties[i % mParties.length]);

            PieDataSet dataSet = new PieDataSet(yVals1, "Power usage");
            dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(5f);

            // add a lot of colors

            ArrayList<Integer> colors = new ArrayList<Integer>();

            for (int c : ColorTemplate.VORDIPLOM_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.JOYFUL_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.COLORFUL_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.LIBERTY_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.PASTEL_COLORS)
                colors.add(c);

            colors.add(ColorTemplate.getHoloBlue());

            dataSet.setColors(colors);
            //dataSet.setSelectionShift(0f);

            PieData data = new PieData(xVals, dataSet);
            data.setValueFormatter(new PercentFormatter());
            data.setValueTextSize(11f);
            data.setValueTextColor(Color.BLACK);
//            data.setValueTypeface(tf);
            mChart.setData(data);

            // undo all highlights
            mChart.highlightValues(null);

            mChart.invalidate();
        }
    }



    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position){
                case 0:
                    return PlaceholderFragment.newInstance(0);
                case 1:
                    return new AppliancesFragment();
                case 2:
                    return new ShortcutsFragment();
            }

            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "OVERVIEW";
                case 1:
                    return "APPLIANCES";
                case 2:
                    return "MODES";
            }
            return null;
        }
    }


    public void sendCommand(String id) {


        if (mMode.equals("bluetooth") && btSocket != null) {
            try {
                btSocket.getOutputStream().write(id.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
//                msg("Failed to communicate" + e.toString());
            }
        } else if (mMode.equals("wifi")) {
            sendWifi(id);
        }
    }

    private void sendWifi(String id) {
        new DownloadWebpageTask().execute("http://" + address + "?pin=" + id);
    }

    // Given a URL, establishes an HttpUrlConnection and retrieves
// the web page content as a InputStream, which it returns as
// a string.
    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
//            Log.d(DEBUG_TAG, "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }


    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
//            textView.setText(result);
        }
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected


        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(HomeActivity.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            } catch (IOException e) {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
//                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
//                getActivity().finish();
            } else {
//                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }
}
