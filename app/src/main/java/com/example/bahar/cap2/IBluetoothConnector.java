package com.example.bahar.cap2;

import android.app.Activity;

/**
 * Created by Declan Easton on 2017-04-04.
 */

interface IBluetoothConnector {
    boolean connectToController(Activity activity, ConnectionCallback connectionCallback);

    void setTemperature(byte temperature);

    void readTemperature();

    void forceFan(boolean force);

    void forceMode(int mode);
}
