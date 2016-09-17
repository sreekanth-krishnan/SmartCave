package com.smartcave.app;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * A simple {@link BaseFragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BluetoothConfigureFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BluetoothConfigureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BluetoothConfigureFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int REQUEST_ENABLE_BT = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private List<BluetoothDevice> devices = new ArrayList<>();
    private DeviceAdapter devicesAdapter;

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
//                mArrayAdapter.add(device.getName() + "\n" + device.getAddress());

                devices.add(device);

                devicesAdapter.notifyDataSetChanged();
            }
        }
    };
    private int mSelectedPosition = -1;
    private View mPairButton;

    public BluetoothConfigureFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BluetoothConfigureFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BluetoothConfigureFragment newInstance(String param1, String param2) {
        BluetoothConfigureFragment fragment = new BluetoothConfigureFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        this.setContentView(R.layout.fragment_bluetooth_configure);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setup();

        listDevices();
    }

    private void listDevices() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth

            Toast.makeText(getContext(), "Device does not support Bluetooth", Toast.LENGTH_LONG).show();
            getActivity().finish();
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return;
        }

        // If it reaches here bluetooth is available and enabled

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        /*if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }*/

        devices.addAll(pairedDevices);

        devicesAdapter.notifyDataSetChanged();



// Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getContext().registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy

        mBluetoothAdapter.startDiscovery();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            getContext().unregisterReceiver(mReceiver);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if(resultCode == ConfigureActivity.RESULT_OK){
                    listDevices();
                }else{
                    getActivity().finish();
                }
                break;
            default:
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setup() {
        mRecyclerView = (RecyclerView) this.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);

        devicesAdapter = new DeviceAdapter();
        mRecyclerView.setAdapter(devicesAdapter);

        mPairButton = this.findViewById(R.id.button_pair);
        mPairButton.setVisibility(View.GONE);
        mPairButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrefUtil.getInstance(getContext()).setConnectedDevice(devices.get(mSelectedPosition).getAddress());
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder>{
        @Override
        public DeviceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_single_choice, parent, false);
            ViewHolder viewHolder = new ViewHolder(itemView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(DeviceAdapter.ViewHolder holder, int position) {
            holder.bind(devices.get(position), position);
        }

        @Override
        public int getItemCount() {
            return devices.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{

            private final TextView mDeviceNameView;
            private CheckedTextView mItemView;

            public ViewHolder(View itemView) {
                super(itemView);
                mItemView = (CheckedTextView) itemView;

                mDeviceNameView = (TextView) itemView.findViewById(android.R.id.text1);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onItemClicked(getAdapterPosition());
                    }
                });
            }

            public void bind(BluetoothDevice bluetoothDevice, int position) {
                mDeviceNameView.setText(bluetoothDevice.getName());
                mItemView.setChecked(position == mSelectedPosition);
            }
        }
    }

    private void onItemClicked(int position) {
        //TODO maintain state
        mSelectedPosition = position;
        devicesAdapter.notifyDataSetChanged();

        mPairButton.setVisibility(View.VISIBLE);
    }
}
