package ru.sergioleko.usboncemore;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

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
    public void checkUSB (View view){
        UsbManager usbmanager;
        usbmanager = (UsbManager) getSystemService(Context.USB_SERVICE);
        usbmanager.getAccessoryList();
        HashMap<String, UsbDevice> deviceList = null;
        deviceList = usbmanager.getDeviceList();
       //UsbAccessory [] listik = usbmanager.getAccessoryList();

        if (deviceList != null) {
            //Toast toast = Toast.makeText(getApplicationContext(), deviceList.size(), Toast.LENGTH_LONG);
            //toast.show();

            Intent checkUSBintent = new Intent(this, USBinfo.class);
            startActivity(checkUSBintent);
        }
        else {
            Toast toast = Toast.makeText(getApplicationContext(), "no device", Toast.LENGTH_LONG);
            toast.show();
            //Intent errorPageIntent = new Intent(this, ErrorWindow.class);
            //startActivity(errorPageIntent);
            //TextView exceptionText = (TextView) findViewById(R.id.exceptText);
            //exceptionText.setVisibility(View.VISIBLE);
            //exceptionText.setText("No USB device connected");

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
                    Toast.makeText(MainActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

}
