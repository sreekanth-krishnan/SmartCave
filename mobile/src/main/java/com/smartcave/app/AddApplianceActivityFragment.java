package com.smartcave.app;

import android.content.ContentValues;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.smartcave.app.data.DBContract;
import com.smartcave.app.data.DBHelper;

/**
 * A placeholder fragment containing a simple view.
 */
public class AddApplianceActivityFragment extends BaseFragment {

    private EditText mApplianceNameView;
    private Spinner mApplianceTypeSpinner;
    private EditText mApplianceIdView;
    private EditText mApplianceRatingView;

    public AddApplianceActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.fragment_add_appliance);
        this.setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setup();
    }

    private void setup() {
        mApplianceNameView = (EditText) this.findViewById(R.id.text_appliance_name);
        mApplianceTypeSpinner = (Spinner) this.findViewById(R.id.spinner_appliance_type);
        mApplianceIdView = (EditText) this.findViewById(R.id.text_appliance_id);
        mApplianceRatingView = (EditText) this.findViewById(R.id.text_appliance_rating);

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.appliance_types,
                android.R.layout.simple_spinner_item);

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mApplianceTypeSpinner.setAdapter(arrayAdapter);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_add_appliance, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            addAppliance();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addAppliance() {
        String applianceName = mApplianceNameView.getText().toString();
        int applianceType = mApplianceTypeSpinner.getSelectedItemPosition();
        String applianceId = mApplianceIdView.getText().toString();
        String rating = mApplianceRatingView.getText().toString();

        if(TextUtils.isEmpty(applianceName)){
            mApplianceNameView.setError("Required");
            mApplianceNameView.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(applianceId)){
            mApplianceIdView.setError("Required");
            mApplianceIdView.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(rating)){
            mApplianceRatingView.setError("Required");
            mApplianceRatingView.requestFocus();
            return;
        }

        addApplianceToDb(applianceName, applianceType, applianceId, Long.parseLong(rating));

        getActivity().finish();
    }

    private void addApplianceToDb(String applianceName, int applianceType, String applianceId, long rating) {

        ContentValues values = new ContentValues();
        values.put(DBContract.Appliance.COLUMN_NAME, applianceName);
        values.put(DBContract.Appliance.COLUMN_TYPE, applianceType);
        values.put(DBContract.Appliance.COLUMN_ID, applianceId);
        values.put(DBContract.Appliance.COLUMN_POWER_RATING, rating);

        Uri newApplianceUri = getContext().getContentResolver().insert(
                DBContract.Appliance.CONTENT_URI,
                values
        );
    }
}
