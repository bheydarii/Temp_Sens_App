package com.example.bahar.cap2;

import java.util.Set;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    int minteger = 21;

    ConnectionCallback connectionCallback = new ConnectionCallback() {
        public void onConnected() {

        }
        public void onConnectionFailed() {

        }
    };
    private BluetoothConnector bluetoothConnector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bluetoothConnector = new BluetoothConnector(connectionCallback);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        bluetoothConnector.connectToController(this);
    }

    public void increaseInteger(View view) {
        minteger = minteger + 1;
        display(minteger);
    }
    public void decreaseInteger(View view) {
        minteger = minteger - 1;
        display(minteger);
    }

    public void bluetooth (View view) {
        Intent i = new Intent(MainActivity.this,devicePairing.class);
        startActivity(i);
    }

    private void display(int number) {
        TextView displayInteger = (TextView) findViewById(
                R.id.integer_number);
        displayInteger.setText("" + number);
    }
}
