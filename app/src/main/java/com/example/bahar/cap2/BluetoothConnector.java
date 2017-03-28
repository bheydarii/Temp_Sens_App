package com.example.bahar.cap2;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

import static android.content.ContentValues.TAG;

/**
 * Created by Declan Easton on 2017-03-21.
 */

public class BluetoothConnector {

    private static final long SCAN_PERIOD = 10000;
    private static final UUID ptacServiceUUID = UUID.fromString("0000FFF1-0000-1000-8000-00805F9B34FB");
    private static final UUID ptacSetTemperatureUUID = UUID.fromString("0000FFF1-0000-1000-8000-00805F9B34FB");
    private static final UUID ptacForceFanUUID = UUID.fromString("0000FFF2-0000-1000-8000-00805F9B34FB");
    private static final UUID ptacForceHeatUUID = UUID.fromString("0000FFF3-0000-1000-8000-00805F9B34FB");
    private static final UUID ptacForceCoolUUID = UUID.fromString("0000FFF4-0000-1000-8000-00805F9B34FB");

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {

                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                connectionCallback.onConnectionStateChanged(ConnectionCallback.DISCOVERING_SERVICES);
                boolean success = mBluetoothGatt.discoverServices();
                Log.i(TAG, "Attempting to start service discovery:" + success);

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                connectionCallback.onConnectionStateChanged(ConnectionCallback.DISCONNECTED);
                Log.i(TAG, "Disconnected from GATT server.");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                connectionCallback.onConnectionStateChanged(ConnectionCallback.CONNECTED);
                ptacService = mBluetoothGatt.getService(ptacServiceUUID);
                if (ptacService == null) {
                    Log.e(TAG, "service not found!");
                }
                Log.i(TAG, "Successfully discovered GATT services.");
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            Log.i(TAG, "Read a characteristic.");
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            Log.i(TAG, "Characteristic changed.");

        }
    };

    private ScanCallback mScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    BluetoothDevice device = result.getDevice();
                    String name = device.getName();
                    if(name == "PTAC Control") {
                        connectionCallback.onConnectionStateChanged(ConnectionCallback.CONNECTING_TO_DEVICE);
                        mBluetoothGatt = device.connectGatt(context, false, mGattCallback);
                        mScanner.stopScan(mScanCallback);
                    }
                }

                @Override
                public void onBatchScanResults(List<ScanResult> results) {
                    super.onBatchScanResults(results);
                }
            };

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mScanner;
    private BluetoothDevice device;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattService ptacService;

    private Handler mHandler;
    private ConnectionCallback connectionCallback;
    private Context context;

    public BluetoothConnector(Context context, ConnectionCallback connectionCallback) {
        mHandler = new Handler();
        this.connectionCallback = connectionCallback;
        this.context = context;
    }

    public boolean connectToController(Activity activity) {
        if(!checkBTState(activity)) {
            return false;
        }

        scanLeDevice(activity, true);
        return true;
    }

    public void setTemperature(byte temperature) {
        BluetoothGattCharacteristic setTemperatureChar = ptacService.getCharacteristic(ptacSetTemperatureUUID);
        byte[] value = new byte[1];
        value[0] = temperature;
        setTemperatureChar.setValue(value);

        mBluetoothGatt.writeCharacteristic((setTemperatureChar));
    }

    public void forceFan(boolean force) {
        BluetoothGattCharacteristic forceFanChar = ptacService.getCharacteristic(ptacForceFanUUID);
        byte[] value = new byte[1];
        value[0] = force ? (byte)1 : (byte)0;
        forceFanChar.setValue(value);

        mBluetoothGatt.writeCharacteristic((forceFanChar));
    }

    public void forceHeat(boolean force) {
        BluetoothGattCharacteristic forceHeatChar = ptacService.getCharacteristic(ptacForceHeatUUID);
        byte[] value = new byte[1];
        value[0] = force ? (byte)1 : (byte)0;
        forceHeatChar.setValue(value);

        mBluetoothGatt.writeCharacteristic((forceHeatChar));
    }

    public void forceCool(boolean force) {
        BluetoothGattCharacteristic forceCoolChar = ptacService.getCharacteristic(ptacForceCoolUUID);
        byte[] value = new byte[1];
        value[0] = force ? (byte)1 : (byte)0;
        forceCoolChar.setValue(value);

        mBluetoothGatt.writeCharacteristic((forceCoolChar));
    }

    //method to check if the device has Bluetooth and if it is on.
    //Prompts the user to turn it on if it is off
    private boolean checkBTState(Activity activity)
    {
        if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            connectionCallback.faliureState(ConnectionCallback.BLE_NOT_SUPPORTED);
            return false;
        }
        final android.bluetooth.BluetoothManager bluetoothManager =
                (android.bluetooth.BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mScanner = mBluetoothAdapter.getBluetoothLeScanner();
        // Check device has Bluetooth and that it is turned on
        if(mBluetoothAdapter == null) {
            connectionCallback.faliureState(ConnectionCallback.BLE_NOT_SUPPORTED);
            return false;
        } else if (!mBluetoothAdapter.isEnabled()) {
            //Prompt user to turn on Bluetooth
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, 1);
            Intent enableLocationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            activity.startActivityForResult(enableLocationIntent, 1);
        }
        return true;
    }

    private void scanLeDevice(Activity activity, final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanner.stopScan(mScanCallback);
                }
            }, SCAN_PERIOD);

            connectionCallback.onConnectionStateChanged(ConnectionCallback.SCANNING_DEVICES);
            mScanner.startScan(mScanCallback);
        } else {
            mScanner.stopScan(mScanCallback);
        }
    }
}
