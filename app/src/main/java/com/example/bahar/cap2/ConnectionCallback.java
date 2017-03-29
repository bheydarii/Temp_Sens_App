package com.example.bahar.cap2;

/**
 * Created by Declan Easton on 2017-03-21.
 */

public class ConnectionCallback {
    public final static int SCANNING_DEVICES = 0;
    public final static int CONNECTING_TO_DEVICE = 1;
    public final static int DISCOVERING_SERVICES = 2;
    public final static int CONNECTED = 3;
    public final static int DISCONNECTED = 4;

    public static int BLE_NOT_SUPPORTED = 5;

    public void onConnectionStateChanged(int state) {

    }

    public void faliureState(int state) {

    }

    public void readSetTemperature(byte setTemperature) {

    }
}
