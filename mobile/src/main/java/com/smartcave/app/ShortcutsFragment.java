package com.smartcave.app;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

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
public class ShortcutsFragment extends BaseFragment implements View.OnClickListener {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.fragment_shortcuts);

    }

    // fast way to call Toast
    private void msg(String s) {
        Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setup();

    }

    private void setup() {
        this.findViewById(R.id.button_sleep_wake).setOnClickListener(this);
        this.findViewById(R.id.button_party_mode).setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_party_mode:
                sendCommand("p");
                break;
            case R.id.button_sleep_wake:
                sendCommand("s");
                break;
        }
    }

    private void sendCommand(String p) {
        ((HomeActivity)getActivity()).sendCommand(p);
    }


}
