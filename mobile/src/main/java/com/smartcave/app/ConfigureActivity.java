package com.smartcave.app;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ConfigureActivity extends AppCompatActivity implements BluetoothConfigureFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure);

        new AlertDialog.Builder(this).setItems(R.array.conf, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        getSupportFragmentManager().beginTransaction().replace(
                                R.id.fragment_container, WifiConfigureFragment.newInstance(null, null)
                        ).commit();
                        break;
                    case 1://delete
                        getSupportFragmentManager().beginTransaction().replace(
                                R.id.fragment_container, BluetoothConfigureFragment.newInstance(null, null)
                        ).commit();
                        break;
                }
            }
        }).setTitle("Configure using").show();


    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
