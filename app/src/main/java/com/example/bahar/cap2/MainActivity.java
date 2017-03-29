package com.example.bahar.cap2;

import java.util.Set;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    int minteger = 21;

    ConnectionCallback connectionCallback = new ConnectionCallback() {
        public void onConnectionStateChanged(int state) {
            Log.i(TAG, "Connection state changed " + state + ".");
            switch (state) {
                case ConnectionCallback.SCANNING_DEVICES :
                    setStatusText("Scanning");
                    break;
                case ConnectionCallback.CONNECTING_TO_DEVICE :
                    setStatusText("Connecting");
                    break;
                case ConnectionCallback.DISCOVERING_SERVICES :
                    setStatusText("Disc Services");
                    break;
                case ConnectionCallback.DISCONNECTED :
                    setStatusText("Disconnected");
                    connect();
                    break;
                case ConnectionCallback.CONNECTED :
                    setStatusText("Connected");
                    break;
            }
        }
        public void faliureState(int state) {
            Log.i(TAG, "Failure state occured.");
        }

        @Override
        public void readSetTemperature(final byte setTemperature) {
            super.readSetTemperature(setTemperature);

            handler.post(new Runnable() {
                @Override
                public void run() {
                    display(setTemperature);
                }
            });
        }
    };

    private BluetoothConnector bluetoothConnector;
    private boolean permissionGranted = false;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bluetoothConnector = new BluetoothConnector(this, connectionCallback);

        handler = new Handler(this.getMainLooper());
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.i(TAG, "Resuming activity.");
        connect();
    }

    private void connect() {
        if(!permissionGranted) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        } else {
            bluetoothConnector.connectToController(this);
        }
    }

    public void increaseInteger(View view) {
        minteger = minteger + 1;
        display(minteger);
        bluetoothConnector.setTemperature((byte)minteger);
    }
    public void decreaseInteger(View view) {
        minteger = minteger - 1;
        display(minteger);
        bluetoothConnector.setTemperature((byte)minteger);
    }

    private void display(int number) {
        TextView displayInteger = (TextView) findViewById(
                R.id.integer_number);
        displayInteger.setText("" + number);
    }

    private void setStatusText(final String str) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                TextView textView = (TextView) findViewById(
                        R.id.textView);
                textView.setText(str);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        if(requestCode == 0)
        {
            permissionGranted = true;
            Log.i(TAG, "Permission granted.");
            bluetoothConnector.connectToController(this);
        }
    }
}
