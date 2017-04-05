package com.example.bahar.cap2;

import android.Manifest;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup.LayoutParams;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.CheckBox;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    int minteger = 21;
    private View popupView;
    private TextView popupStatusTextView;
    private PopupWindow popupWindow;

    ConnectionCallback connectionCallback = new ConnectionCallback() {
        public void onConnectionStateChanged(int state) {
            Log.i(TAG, "Connection state changed " + state + ".");
            switch (state) {
                case ConnectionCallback.SCANNING_DEVICES :
                    //addition for spinner
                    //setContentView(R.layout.activity_main);
                    //end of addition for spinner
                    TextView displayInteger = (TextView) findViewById(
                            R.id.integer_number);
                    popupWindow.showAtLocation(displayInteger, Gravity.NO_GRAVITY, 0, 0);
                    setStatusText("Scanning");
                    break;
                case ConnectionCallback.CONNECTING_TO_DEVICE :
                    setStatusText("Connecting");
                    break;
                case ConnectionCallback.DISCOVERING_SERVICES :
                    setStatusText("Discovering Services");
                    break;
                case ConnectionCallback.DISCONNECTED :
                    setStatusText("Disconnected");
                    connect();
                    break;
                case ConnectionCallback.CONNECTED :
                    setStatusText("Connected");
                    //RadioGroup group = (RadioGroup)findViewById(R.id.radioGroup);
                    //group.check(R.id.none);
                    //RadioButton noneButton = (RadioButton)findViewById(R.id.none);
                    //noneButton.setChecked(true);
                    //Dismiss popup window once connected
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onRadioButtonClicked(findViewById(R.id.none));
                            popupWindow.dismiss();
                        }
                    });
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

    private IBluetoothConnector bluetoothConnector;
    private boolean permissionGranted = false;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
//        RadioGroup group = (RadioGroup)findViewById(R.id.radioGroup);
//        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
//                onRadioButtonClicked(findViewById(checkedId));
//            }
//        });
        bluetoothConnector = new BluetoothConnector(this);

        handler = new Handler(this.getMainLooper());

        LayoutInflater layoutInflater =
                (LayoutInflater)getBaseContext()
                        .getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = layoutInflater.inflate(R.layout.popup, null);
        popupWindow = new PopupWindow(
                popupView, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        popupStatusTextView=(TextView) popupView.findViewById(R.id.popUpStatusText);
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
            bluetoothConnector.connectToController(this, connectionCallback);
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
                TextView textView = (TextView)findViewById(R.id.textView);
                textView.setText(str);
                popupStatusTextView.setText(str);
            }
        });
    }

    //Radio Button and checkbox code
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.hot:
                if (checked)
                {
                    bluetoothConnector.forceMode(BluetoothConnector.HEAT_MODE);
                }
                    break;
            case R.id.cold:
                if (checked)
                {
                    bluetoothConnector.forceMode(BluetoothConnector.COOL_MODE);
                }
                    break;
            case R.id.none:
                if (checked) {
                    bluetoothConnector.forceMode(BluetoothConnector.AUTO_MODE);
                }
                    break;
        }
    }
    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        switch (view.getId()) {
            case R.id.fan:
                if (checked)
                    bluetoothConnector.forceFan(true);
                else
                    bluetoothConnector.forceFan(false);
                break;
        }
    }
    // end of radio button and checkbox code

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        if(requestCode == 0)
        {
            permissionGranted = true;
            Log.i(TAG, "Permission granted.");
            bluetoothConnector.connectToController(this, connectionCallback);
        }
    }
}
