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
        //UsbAccessory[] listik = usbmanager.getAccessoryList();
        usbmanager.getAccessoryList();
        HashMap<String, UsbDevice> deviceList = usbmanager.getDeviceList();
        if (deviceList != null) {
            /*usbmanager.getAccessoryList();
            HashMap<String, UsbDevice> deviceList = usbmanager.getDeviceList();*/
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
                    text.setText("Linkos' Power Monitor is found");
                    Button connectionButton = findViewById(R.id.buttonConnect);
                    connectionButton.setVisibility(View.VISIBLE);
                } else {
                    text.setText("Vendor ID is: " + String.valueOf(vendorId) + ". Product ID is: " + String.valueOf(productId));
                }
            } else {
                text.setText("No such device");
            }
            //text.setText(deviceList.toString());
       /* int length = deviceList.size();
        String lengthstr = String.valueOf(length);
        text.setText(lengthstr);*/
    /*    boolean isthere = (deviceList.containsKey("mVendorId"));
        if (isthere){
            text.setText("yes");
        }
        else {
            text.setText("no");
        }*/
     /*   boolean isLinkos = (deviceList.containsValue("2385"));
        if (isLinkos==true){
            text.setText("it's Linkos");
        }
        else {
            text.setText("not Linkos");
        }*/
            //return device;
        } else {
            Intent errorPageIntent = new Intent(this, ErrorWindow.class);
            startActivity(errorPageIntent);
           /* TextView text = findViewById(R.id.magicInfo);
            text.setText("No device Connected");*/
        }
        //return null;
    }

    private static final String ACTION_USB_PERMISSION =
            "ru.sergioleko.usboncemore.USB_PERMISSION";

    public void connectingUSB(View view) {

        UsbManager usbmanager = (UsbManager) getSystemService(Context.USB_SERVICE);
        usbmanager.getAccessoryList();
        HashMap<String, UsbDevice> deviceList = usbmanager.getDeviceList();
        //UsbAccessory [] listik = usbmanager.getAccessoryList();
        if (deviceList != null) {
            //HashMap<String, UsbDevice> deviceList = usbmanager.getDeviceList();
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
            /*Button connectionButton = findViewById(R.id.buttonConnect);
            connectionButton.setVisibility(View.INVISIBLE);
            startReader();*/

        }
        else {
            Intent errorPageIntent = new Intent(this, ErrorWindow.class);
            startActivity(errorPageIntent);
        }
    }
        /*final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent) {
                TextView text = findViewById(R.id.magicInfo);
                String action = intent.getAction();
                if (ACTION_USB_PERMISSION.equals(action)) {
                    synchronized (this) {
                        UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            if (device != null) {

                                *//*Intent readDataIntent = new Intent(getApplicationContext(), readUsb.class);
                                startActivity(readDataIntent);*//*
                                text.setText("Permission Granted");
                                //call method to set up device communication
                            }
                        } else {
                            text.setText("Permission Denied");
                            //Log.d(TAG, "permission denied for device " + device);
                        }
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);*/
   // }

   /* private byte[] bytes = new byte[64];

    public void startReader() {
      //  TextView text = findViewById(R.id.magicInfo);
        UsbManager usbmanager = (UsbManager) getSystemService(Context.USB_SERVICE);
        if (usbmanager != null) {
            HashMap<String, UsbDevice> deviceList = usbmanager.getDeviceList();
            Set<String> keys = deviceList.keySet();
            Object[] keysArr = keys.toArray();
            String lfkey = keysArr[0].toString();
            UsbDevice device = deviceList.get(lfkey);


            assert device != null;
            UsbInterface intf = device.getInterface(0);
            UsbEndpoint endpoint = intf.getEndpoint(0);
            UsbDeviceConnection connection = usbmanager.openDevice(device);
            boolean forceClaim = true;
            connection.claimInterface(intf, forceClaim);
            int TIMEOUT = 0;
            connection.bulkTransfer(endpoint, bytes, bytes.length, TIMEOUT);
            connection.close();
            connection.releaseInterface(intf);
            String SSS = "Result:\n";
            String[] names = {"DA1°C", "+2.5V", "+1.8V", "+1.2V", "+1V", "DA6°C", "?", "+5V", "+1.8V", "+0.95", "+3.3V", "+1.5V", "+12V", "DA2°C", "DA7°C", "+5V"};
            for (int i = 0; i < bytes.length; i += 4) {
                int floatBits = bytes[i] & 0xFF |
                        (bytes[i + 1] & 0xFF) << 8 |
                        (bytes[i + 2] & 0xFF) << 16 |
                        (bytes[i + 3] & 0xFF) << 24;
                float Data = Float.intBitsToFloat(floatBits);
                SSS += names[i / 4] + "\t=\t" + String.valueOf(Data) + "\n";
            }*/
            //text.setText(SSS);
            /*Button refreshButton = findViewById(R.id.refreshbutton);
            refreshButton.setVisibility(View.VISIBLE);
            Button autorefreshButton = findViewById(R.id.autorefreshbutton);
            autorefreshButton.setVisibility(View.VISIBLE);*/
       // }

 /*   public void getDevice (){
        UsbManager usbmanager = (UsbManager) getSystemService(Context.USB_SERVICE);
        if (usbmanager != null){
            HashMap<String, UsbDevice> deviceList = usbmanager.getDeviceList();
            Set<String> keys = deviceList.keySet();
            Object[] keysArr = keys.toArray();
            String lfkey = keysArr[0].toString();
            UsbDevice device = deviceList.get(lfkey);
    }*/


    //}



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
                        text.setText("Permission Denied");
                        //Log.d(TAG, "permission denied for device " + device);
                    }
                }
            }
        }
    };
    public void startShowing (){
        Intent startshowingIntent = new Intent(this, shower.class);
        startActivity(startshowingIntent);
    }


    }




