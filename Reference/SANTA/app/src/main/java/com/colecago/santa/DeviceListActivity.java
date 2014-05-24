
package com.colecago.santa;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;

//derived from Google's BT Chat example
public class  DeviceListActivity extends Activity {
    // debugging
    private static final String TAG = "DeviceListActivity";

    private static final boolean D = true;

    // return intent extra
    public static String EXTRA_DEVICE_ADDRESS = "device_addres";

    // member fields
    private BluetoothAdapter mBtAdapter;

    private ArrayAdapter<String> mPairedDevicesArrayAdapter;

    private ArrayAdapter<String> mNewDevicesArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set up the window
        setContentView(R.layout.device_list);

        // set result cancelled in case user backs out
        setResult(Activity.RESULT_CANCELED);

        // initialize button to perform device discovery
        Button scanButton = (Button) findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                doDiscovery();
                v.setVisibility(View.GONE);
            }
        });

        // initialize array adapters. one for already paired, and one for new
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

        // find and set up listview for paired
        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        // find and set up listview for newly discovered devices
        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);

        // register for broadcast when device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // reigster for boardcasts when discovery is finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        // get local adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // if there are paired devices add to arrayadapter
        if (pairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            String noDevices = getResources().getText(R.string.none_paired).toString();
            mPairedDevicesArrayAdapter.add(noDevices);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // make sure we're not doing discovery
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }

        // unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
    }

    // start device discovery with btadapter
    private void doDiscovery() {
        if (D)
            Log.d(TAG, "doDiscovery()");

        // indicate scanning in the title
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);

        // turn on sub-title for new devices
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        // if we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        // request discover
        mBtAdapter.startDiscovery();
    }

    // the onclick listener
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // cancel discovery because its costly
            mBtAdapter.cancelDiscovery();

            // get the device mac which is last 17 chars in view
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            // create the result intent and include mac
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

            // set result and finish activity
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    // the broadcast receiver that listens for devices and changes title
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // when discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // get the device object from intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                // if its already paired, skip it because its on the list
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
                // when discovery finsihed, change the title
                else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    setProgressBarIndeterminateVisibility(false);
                    setTitle(R.string.select_device);
                    if (mNewDevicesArrayAdapter.getCount() == 0) {
                        String noDevices = getResources().getText(R.string.none_found).toString();
                        mNewDevicesArrayAdapter.add(noDevices);
                    }
                }
            }
        }

    };

}
