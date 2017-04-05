package com.example.bahar.cap2;

import android.app.Activity;
import android.os.Handler;

/**
 * Created by Declan Easton on 2017-04-04.
 */

public class DummyBluetoothConnector implements IBluetoothConnector {

    ConnectionCallback connectionCallback;
    Runnable stateChanger = new Runnable() {
        @Override
        public void run() {
            connectionCallback.onConnectionStateChanged(state);
            state = state + 1;
            if(state < 4) {
                handler.postDelayed(stateChanger, 1000);
            }
        }
    };

    private Handler handler;
    private int state;

    public DummyBluetoothConnector() {
        state = ConnectionCallback.DISCONNECTED;
    }

    @Override
    public boolean connectToController(Activity activity, final ConnectionCallback connectionCallback) {
        if(state == ConnectionCallback.DISCONNECTED) {
            state = ConnectionCallback.SCANNING_DEVICES;
            this.connectionCallback = connectionCallback;
            handler = new Handler(activity.getMainLooper());
            handler.postDelayed(stateChanger, 1000);
        }
        return true;
    }

    @Override
    public void setTemperature(byte temperature) {

    }

    @Override
    public void readTemperature() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                connectionCallback.readSetTemperature((byte)24);
            }
        }, 500);
    }

    @Override
    public void forceFan(boolean force) {

    }

    @Override
    public void forceMode(int mode) {

    }
}
