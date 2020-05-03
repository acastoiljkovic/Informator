package com.informator.profile_fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.informator.R;
import com.informator.StartActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class AddFriendsBluetoothFragment extends Fragment {


    ImageView iwBluetoothState;
    TextView tvBluetoothState;
    LinearLayout state;
    LinearLayout search;
    ListView listDevices;


    //Dodato za bluetooth

    private BluetoothAdapter mBTAdapter;
    private Set<BluetoothDevice> mPairedDevices;
    private ArrayAdapter<String> mBTArrayAdapter;
    private Handler mHandler; // prima notifikacije
    private ConnectedThread mConnectedThread; // da se napravi klasa preko koje ce da se prenose podaci
    private BluetoothSocket mBTSocket = null; // put izmedju 2 klijenta
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private final static int REQUEST_ENABLE_BT = 10;
    private final static int MESSAGE_READ = 11;
    private final static int CONNECTING_STATUS = 12;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_friends_bluetooth,container,false);

        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBTAdapter == null){
            Toast.makeText(getContext(),"This device does not support bluetooth !",Toast.LENGTH_SHORT).show();
            ((StartActivity) getActivity()).setFragment(R.id.profile);
        }
        mBTArrayAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1);

        listDevices = view.findViewById(R.id.bluetooth_active_devices);
        listDevices.setAdapter(mBTArrayAdapter);
        listDevices.setOnItemClickListener(mDeviceClickListener);

        iwBluetoothState = view.findViewById(R.id.image_add_friends_bluetooth_state);
        tvBluetoothState = view.findViewById(R.id.text_add_friends_bluetooth_state);

        if(mBTAdapter.isEnabled()){
            iwBluetoothState.setImageResource(R.drawable.ic_bluetooth_green_24dp);
            tvBluetoothState.setText("Enabled");
            tvBluetoothState.setTextColor(getResources().getColor(R.color.color_green));
        }
        else{
            iwBluetoothState.setImageResource(R.drawable.ic_bluetooth_red_24dp);
            tvBluetoothState.setText("Disabled");
            tvBluetoothState.setTextColor(getResources().getColor(R.color.color_red));
        }

        state = view.findViewById(R.id.group_bluetooth_state);
        state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mBTAdapter.isEnabled()){
                    bluetoothOn(v);
                }
                else{
                    bluetoothOff(v);
                }
            }
        });

        search = view.findViewById(R.id.group_bluetooth_search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ako zatreba
//                listPairedDevices(v);
                discover(v);
            }
        });

        // za bluetooth
        mHandler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what == MESSAGE_READ){
                    String readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getContext(),"Message : "+readMessage,Toast.LENGTH_SHORT).show();
                }

                if(msg.what == CONNECTING_STATUS){
                    if(msg.arg1 == 1)
                        Toast.makeText(getContext(),"Connected to Device: " + (String)(msg.obj),Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getContext(),"Connection failed",Toast.LENGTH_SHORT).show();
                }
            }
        };



        return  view;
    }


    private void bluetoothOn(View view){
        if(!mBTAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            Toast.makeText(getContext(),"Bluetooth turned on",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getContext(),"Bluetooth is already on",Toast.LENGTH_SHORT).show();
        }
    }

    private void bluetoothOff(View view){
        mBTAdapter.disable();
        iwBluetoothState.setImageResource(R.drawable.ic_bluetooth_red_24dp);
        tvBluetoothState.setText("Disabled");
        tvBluetoothState.setTextColor(getResources().getColor(R.color.color_red));
        Toast.makeText(getContext(),"Bluetooth turned off",Toast.LENGTH_SHORT).show();
    }

    private void discover(View view){
        if(mBTAdapter.isDiscovering()){
            mBTAdapter.cancelDiscovery();
            Toast.makeText(getContext(),"Discovery stopped",Toast.LENGTH_SHORT).show();
        }
        else{
            if(mBTAdapter.isEnabled()) {
                mBTArrayAdapter.clear();
                mBTAdapter.startDiscovery();
                Toast.makeText(getContext(), "Discovery started", Toast.LENGTH_SHORT).show();
                getActivity().registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            }
            else{
                Toast.makeText(getContext(), "Bluetooth is disabled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onStop() {
        try {
            // probaj da odregistrujes kao je nesto registrovano
            getActivity().unregisterReceiver(blReceiver);
        }
        catch (Exception e){

        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        try {
            // probaj da odregistrujes kao je nesto registrovano
            getActivity().unregisterReceiver(blReceiver);
        }
        catch (Exception e){

        }
        super.onDestroy();
    }

    final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                mBTArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    // ako zatreba
    private void listPairedDevices(View view){
        mPairedDevices = mBTAdapter.getBondedDevices();
        if(mBTAdapter.isEnabled()) {
            for (BluetoothDevice device : mPairedDevices)
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());

            Toast.makeText(getContext(), "Show Paired Devices", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(getContext(), "Bluetooth is disabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_ENABLE_BT){
            if(resultCode == RESULT_OK){
                iwBluetoothState.setImageResource(R.drawable.ic_bluetooth_green_24dp);
                tvBluetoothState.setText("Enabled");
                tvBluetoothState.setTextColor(getResources().getColor(R.color.color_green));
                Toast.makeText(getContext(),"Bluetooth enabled",Toast.LENGTH_SHORT).show();
            }
            else {
                iwBluetoothState.setImageResource(R.drawable.ic_bluetooth_red_24dp);
                tvBluetoothState.setText("Disabled");
                tvBluetoothState.setTextColor(getResources().getColor(R.color.color_red));
                Toast.makeText(getContext(),"Bluetooth disabled",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

            if(!mBTAdapter.isEnabled()) {
                Toast.makeText(getContext(), "Bluetooth is disabled", Toast.LENGTH_SHORT).show();
                return;
            }

            String info = ((TextView) v).getText().toString();
            final String address = info.substring(info.length() - 17);
            final String name = info.substring(0,info.length() - 17);

//            Toast.makeText(getContext(), "Address : "+address + "\nName :"+name, Toast.LENGTH_SHORT).show();


            new Thread()
            {
                public void run() {
                    boolean fail = false;

                    BluetoothDevice device = mBTAdapter.getRemoteDevice(address);

                    try {
                        mBTSocket = device.createRfcommSocketToServiceRecord(BTMODULEUUID);
                    } catch (Exception e) {
                        fail = true;
                        Toast.makeText(getContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                    }

                    try {
                        mBTSocket.connect();
                    } catch (Exception e) {
                        try {
                            fail = true;
                            mBTSocket.close();
                            mHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                                    .sendToTarget();
                        } catch (Exception e2) {
                            Toast.makeText(getContext(), "Socket connection failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if(fail == false) {
                        mConnectedThread = new ConnectedThread(mBTSocket);
                        mConnectedThread.start();

                        mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name)
                                .sendToTarget();
                    }
                }
            }.start();
        }
    };

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;


            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {

                    bytes = mmInStream.available();
                    if(bytes != 0) {
                        SystemClock.sleep(100);
                        bytes = mmInStream.available();
                        bytes = mmInStream.read(buffer, 0, bytes);
                        mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                                .sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                    break;
                }
            }
        }


        public void write(String input) {
            byte[] bytes = input.getBytes();
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
}
