package ru.sergioleko.usboncemore;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(" ");
    }
    public void checkUSB (View view){
        UsbManager usbmanager = null;
        usbmanager = (UsbManager) getSystemService(Context.USB_SERVICE);
        UsbAccessory [] listik = usbmanager.getAccessoryList();

        if (listik != null) {
            Toast toast = Toast.makeText(getApplicationContext(), "no device", Toast.LENGTH_LONG);
            toast.show();
            //Intent checkUSBintent = new Intent(this, USBinfo.class);
            //startActivity(checkUSBintent);
        }
        else {
            Intent errorPageIntent = new Intent(this, ErrorWindow.class);
            startActivity(errorPageIntent);
            //TextView exceptionText = (TextView) findViewById(R.id.exceptText);
            //exceptionText.setVisibility(View.VISIBLE);
            //exceptionText.setText("No USB device connected");

        }
    }
}
