package com.example.bahar.cap2;

import java.util.List;
import java.util.Set;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class devicePairing extends Activity {

    // textview for connection status
    TextView textConnectionStatus;
    ListView pairedListView;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mScanner;
    private boolean mScanning;
    private Handler mHandler;

    private static final long SCAN_PERIOD = 10000;

    // Member fields
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;

    private ScanCallback mScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    BluetoothDevice device = result.getDevice();
                    mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_pairing);

        textConnectionStatus = (TextView) findViewById(R.id.connecting);
        textConnectionStatus.setTextSize(40);

        // Initialize array adapter for paired devices
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

        // Find and set up the ListView for paired devices
        pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);

        mHandler = new Handler();

    }

    @Override
    public void onResume()
    {
        super.onResume();
        //It is best to check BT status at onResume in case something has changed while app was paused etc
        checkBTState();

        mPairedDevicesArrayAdapter.clear();// clears the array so items aren't duplicated when resuming from onPause

        textConnectionStatus.setText(" "); //makes the textview blank

        scanLeDevice(true);
    }

    //method to check if the device has Bluetooth and if it is on.
    //Prompts the user to turn it on if it is off
    private void checkBTState()
    {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE Not Supported", Toast.LENGTH_SHORT).show();
            finish();
        }
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mScanner = mBluetoothAdapter.getBluetoothLeScanner();
        // Check device has Bluetooth and that it is turned on
        if(mBluetoothAdapter == null) {
            Toast.makeText(getBaseContext(), "Device does not support Bluetooth", Toast.LENGTH_SHORT).show();
            finish();
        } else if (!mBluetoothAdapter.isEnabled()) {
            //Prompt user to turn on Bluetooth
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
            Intent enableLocationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(enableLocationIntent, 1);
        }
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mScanner.stopScan(mScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mScanner.startScan(mScanCallback);
        } else {
            mScanning = false;
            mScanner.stopScan(mScanCallback);
        }
    }

//    public void temperature (View view) {
//        Intent intent2 = new Intent(devicePairing.this,MainActivity.class);
//        startActivity(intent2);
//    }
}
