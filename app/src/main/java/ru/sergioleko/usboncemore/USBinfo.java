package ru.sergioleko.usboncemore;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class USBinfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usbinfo);
        setTitle(" ");

        checkingUSB();

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);



    }

    public void checkingUSB() {
        UsbManager usbmanager = (UsbManager) getSystemService(Context.USB_SERVICE);

        //usbmanager.getAccessoryList();
        HashMap<String, UsbDevice> deviceList = usbmanager.getDeviceList();
        if (usbmanager.getDeviceList().size() > 0) {

            TextView text = findViewById(R.id.magicInfo);
            Set<String> keys = deviceList.keySet();
            Object[] keysArr = keys.toArray();
            String lfkey = keysArr[0].toString();
            text.setText(lfkey);

            UsbDevice device = deviceList.get(lfkey);
            if (device != null) {
                int vendorId = device.getVendorId();
                int productId = device.getProductId();
                if (vendorId == /*2385*/1240 && productId == /*5734*/95) {
                    text.setText(getString(R.string.onpmdetected));
                    Button connectionButton = findViewById(R.id.buttonConnect);
                    connectionButton.setVisibility(View.VISIBLE);
                } else {
                    text.setText(getString(R.string.onuddetected) + "\n" + "Vendor ID is: " + String.valueOf(vendorId) + ". \nProduct ID is: " + String.valueOf(productId));
                }
            } else {
                text.setText("No such device");
            }


        } else {
            Intent errorPageIntent = new Intent(this, ErrorWindow.class);
            startActivity(errorPageIntent);
            finish();

        }

    }

    private static final String ACTION_USB_PERMISSION =
            "ru.sergioleko.usboncemore.USB_PERMISSION";

    public void connectingUSB(View view) {

        UsbManager usbmanager = (UsbManager) getSystemService(Context.USB_SERVICE);
        //usbmanager.getAccessoryList();


        if (usbmanager.getDeviceList().size() > 0) {
            HashMap<String, UsbDevice> deviceList = usbmanager.getDeviceList();
            Set<String> keys = deviceList.keySet();
            Object[] keysArr = keys.toArray();
            String lfkey = keysArr[0].toString();
            UsbDevice device = deviceList.get(lfkey);
            if (!usbmanager.hasPermission(device)) {
                PendingIntent mPermIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                usbmanager.requestPermission(device, mPermIntent);

            } else {
                startShowing();
            }


        }
        else {
            Intent errorPageIntent = new Intent(this, ErrorWindow.class);
            startActivity(errorPageIntent);
            finish();
        }
    }








    final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            TextView text = findViewById(R.id.magicInfo);
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {

                                /*Intent readDataIntent = new Intent(getApplicationContext(), readUsb.class);
                                startActivity(readDataIntent);*/
                            //text.setText("Permission Granted");
                           startShowing();
                            //startReader();
                            //call method to set up device communication
                        }
                    } else {
                        text.setText(getString(R.string.permissiondenied));
                        //Log.d(TAG, "permission denied for device " + device);
                    }
                }
            }
        }
    };
    public void startShowing (){
        Intent startshowingIntent = new Intent(this, shower.class);
        startActivity(startshowingIntent);
        finish();
    }

public void startErroeActivity(){
    Intent errorPageIntent = new Intent(this, ErrorWindow.class);
    startActivity(errorPageIntent);
    finish();
}
    }




