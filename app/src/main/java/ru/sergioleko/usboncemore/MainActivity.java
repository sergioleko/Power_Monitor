package ru.sergioleko.usboncemore;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Linkos Power Monitor");
    }
    public void checkUSB (View view){
        UsbManager usbmanager = (UsbManager) getSystemService(Context.USB_SERVICE);
        if (usbmanager != null) {
            Intent checkUSBintent = new Intent(this, USBinfo.class);
            startActivity(checkUSBintent);
        }
        else {
            TextView exceptionText = (TextView) findViewById(R.id.exceptText);
            exceptionText.setVisibility(View.VISIBLE);
            exceptionText.setText("No USB device connected");

        }
    }
}
