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
//    TextView textConnectionStatus;
//    ListView pairedListView;
//
//    private BluetoothAdapter mBtAdapter;
//    private ArrayAdapter<String> mPairedDevicesArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        textConnectionStatus = (TextView) findViewById(R.id.connecting);
//        textConnectionStatus.setTextSize(40);
//
//        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
//
//        pairedListView = (ListView) findViewById(R.id.paired_devices);
//        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
    }

//    @Override
//    public void onResume()
//    {
//        super.onResume();
//        checkBTState();
//        mPairedDevicesArrayAdapter.clear();
//        textConnectionStatus.setText(" ");
//
//        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
//
//        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
//
//        if (pairedDevices.size() > 0) {
//            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
//            for (BluetoothDevice device : pairedDevices) {
//                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
//            }
//        } else{
//            if (pairedDevices.size() == 0){
//                findViewById(R.id.infoText).setVisibility(View.VISIBLE);
//            }
//        }
//    }
//
//    private void checkBTState()
//    {
//        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (mBtAdapter==null) {
//            Toast.makeText(getBaseContext(),"Device does support Bluetooth", Toast.LENGTH_SHORT).show();
//            finish();
//        } else {
//            if (!mBtAdapter.isEnabled()) {
//                //Prompt user to turn on Bluetooth
//                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(enableBtIntent, 1);
//            }
//        }
//    }
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
