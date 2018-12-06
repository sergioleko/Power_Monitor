package ru.sergioleko.usboncemore;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

  private final int MY_PERMISSION_REQUESTS_WRITE_EXTERNAL_STORAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(" ");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_REQUESTS_WRITE_EXTERNAL_STORAGE);

        }


    }

    public void checkUSB (View view) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
        }
        else {
            v.vibrate(50);
        }
        UsbManager usbmanager = (UsbManager) getSystemService(Context.USB_SERVICE);
        if (usbmanager.getDeviceList().size() > 0) {
            //Toast.makeText(MainActivity.this, String.valueOf(usbmanager.getDeviceList().size()), Toast.LENGTH_LONG).show();


            //usbmanager.getAccessoryList();
            HashMap<String, UsbDevice> deviceList = usbmanager.getDeviceList();

            Set<String> keys = deviceList.keySet();
            Object[] keysArr = keys.toArray();
            String lfkey = keysArr[0].toString();


            UsbDevice device = deviceList.get(lfkey);

            //UsbAccessory [] listik = usbmanager.getAccessoryList();

            //if (deviceList != null) {


            Intent checkUSBintent = new Intent(this, USBinfo.class);
            startActivity(checkUSBintent);


        } else {

            Intent errorPageIntent = new Intent(this, ErrorWindow.class);
            startActivity(errorPageIntent);


        }

    }
    public void onRequestPermissionsResult(
            int requestCode,
            String permissions[],
            int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUESTS_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, R.string.permissiongranted, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.permissiondenied), Toast.LENGTH_SHORT).show();
                }
        }
    }



    public void startErroeActivity(){
        Intent errorPageIntent = new Intent(this, ErrorWindow.class);
        startActivity(errorPageIntent);
    }



}
