package ru.sergioleko.usboncemore;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class shower extends AppCompatActivity {
    boolean onoff = false;
    boolean offon = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shower);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setTitle(" ");

        /*Calendar calender = Calendar.getInstance();
        SimpleDateFormat dformat = new SimpleDateFormat("yyyy_MM_dd");
        String date = dformat.format(calender.getTime());
        SimpleDateFormat tformat = new SimpleDateFormat("HH:mm:ss");
        String time = tformat.format(calender.getTime());

        String exStorage = Environment.getExternalStorageDirectory().getAbsolutePath();
        String myDir = "linkoslogs" + File.separator + date;
        String fileName = date + "_log.txt";*/

        //if ((Environment.getExternalStorageDirectory().getFreeSpace() / 1024 / 1024) > 20) {
        ToggleButton tb = findViewById(R.id.toggleButton);
        tb.setChecked(false);
        TextView tw = findViewById(R.id.logstatus);
        tw.setText(R.string.log_off_button);
        TextView as = findViewById(R.id.autostatus);
        as.setText(R.string.autorefreshoff);
        startReader();


    }


    public void startReader() {
        UsbManager usbmanager = (UsbManager) getSystemService(Context.USB_SERVICE);


        if (usbmanager.getDeviceList().size() > 0) {
            HashMap<String, UsbDevice> deviceList = usbmanager.getDeviceList();
            byte[] bytes = new byte[64];
            List<Float> dataList = new ArrayList<Float>();

            CheckBox su3 = (CheckBox) findViewById(R.id.checkBox3);
            CheckBox su5 = (CheckBox) findViewById(R.id.checkBox);
            CheckBox thrm = (CheckBox) findViewById(R.id.checkBox2);


            Set<String> keys = deviceList.keySet();
            Object[] keysArr = keys.toArray();

            String lfkey = keysArr[0].toString();
            UsbDevice device = deviceList.get(lfkey);


            //assert device != null;
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
            String[] names = {"Тип 2,5/0,95/1,8В, °C", "+2.5V", "+1.8V", "+1.2V", "+1V", "Т FPGA, °C", "Т контроллера 2, °C", "+5V", "+1.8V", "+0.95", "+3.3V", "+1.5V", "+12V", "Тип 3,3/1,5В, °C", "Т контроллера 1, °C"};
            float[] maxValues = new float[]{60.0f, 2.51f, 1.91f, 1.31f, 1.11f, 40.0f, 40.0f, 5.11f, 1.91f, 1.11f, 3.41f, 1.61f, 12.11f, 40.0f, 40.0f};
            float[] minvalues = new float[]{0.0f, 2.29f, 1.69f, 1.09f, 0.89f, 0.0f, 0.0f, 4.89f, 1.69f, 0.89f, 3.19f, 1.39f, 1.9f, 0.0f, 0.0f};

            for (int i = 0; i < bytes.length - 4; i += 4) {
                int floatBits = bytes[i] & 0xFF |
                        (bytes[i + 1] & 0xFF) << 8 |
                        (bytes[i + 2] & 0xFF) << 16 |
                        (bytes[i + 3] & 0xFF) << 24;
                float Data = Float.intBitsToFloat(floatBits);
                dataList.add(Data);


            }
            for (int c = 0; c < dataList.size(); c++) {
                String textviewid = "textView" + c;
                int resID = getResources().getIdentifier(textviewid, "id", getPackageName());
                TextView output = findViewById(resID);
                String outputText = String.valueOf(names[c]) + "\t = \t" + String.valueOf(dataList.get(c));
                output.setText(outputText);
                if ((dataList.get(c) > maxValues[c] || dataList.get(c) < minvalues[c])) {
                    output.setTextColor(Color.parseColor("#ffff4444"));
                } else {
                    output.setTextColor(Color.parseColor("#ff669900"));
                }

            }
            if ((bytes[bytes.length - 4] & 0x01) != 0) {
                thrm.setChecked(true);
                thrm.setTextColor(Color.RED);
            } else {
                thrm.setChecked(false);
                thrm.setTextColor(Color.GREEN);
            }
            if ((bytes[bytes.length - 4] & 0x04) != 0) {
                su3.setChecked(true);
                su3.setTextColor(Color.RED);
            } else {
                su3.setChecked(false);
                su3.setTextColor(Color.GREEN);
            }
            if ((bytes[bytes.length - 4] & 0x08) != 0) {
                su5.setChecked(true);
                su5.setTextColor(Color.RED);
            } else {
                su5.setChecked(false);
                su5.setTextColor(Color.GREEN);
            }


            createLog(dataList);
        }
        else {

            Intent errorPageIntent = new Intent(this, ErrorWindow.class);
            startActivity(errorPageIntent);
            finish();
        }
    }

    public void refresher(View view) {
        startReader();
    }

    public void autorefresher(View view) {

        final Handler switchHandler = new Handler();
        checkOnOff();
        //UsbManager usbmanager = (UsbManager) getSystemService(Context.USB_SERVICE);
        final UsbManager usbmanager = (UsbManager) getSystemService(Context.USB_SERVICE);
        if (usbmanager.getDeviceList().size() > 0) {

            final Runnable runnable = new Runnable() {

                public void run() {
                    if (onoff) {
                        if (usbmanager.getDeviceList().size() > 0){
                        startReader();
                        switchHandler.postDelayed(this, 1000);}
                        else {
                            switchHandler.removeCallbacks(this);
                            Intent errorPageIntent = new Intent(shower.this, ErrorWindow.class);
                            startActivity(errorPageIntent);
                            finish();
                        }
                    } else {
                        switchHandler.removeCallbacks(this);
                    }
                }
            };
            switchHandler.removeCallbacks(runnable);
            switchHandler.postDelayed(runnable, 1000);
        }
             else {

                Intent errorPageIntent = new Intent(this, ErrorWindow.class);
                startActivity(errorPageIntent);
                finish();

            }





    }

    public boolean checkOnOff() {
        TextView switchShower = findViewById(R.id.autostatus);
        String stringy = getString(R.string.autorefreshoff);


            if (switchShower.getText().toString().equals(stringy)) {
                onoff = true;
                switchShower.setText(R.string.autorefreshon);
            } else {
                onoff = false;
                switchShower.setText(R.string.autorefreshoff);
            }

return onoff;
    }

    public  void getoffon (View view){
        logOnOff();
    }

    public boolean logOnOff () {
        TextView tw = findViewById(R.id.logstatus);
        String stringy = getString(R.string.log_off_button);
        if (tw.getText().toString().equals(stringy)){
            offon = true;
            tw.setText(R.string.log_on_button);
        }
        else {
            offon = false;
            tw.setText(R.string.log_off_button);
        }
        return offon;
    }


    public void createLog(List<Float> dataList) {

       // ToggleButton tb = findViewById(R.id.toggleButton);
        //TextView ls = findViewById(R.id.logstatus);
        if (offon) {
          //  ls.setText(R.string.log_on_button);
            Calendar calender = Calendar.getInstance();
            SimpleDateFormat dformat = new SimpleDateFormat("yyyy_MM_dd");
            String date = dformat.format(calender.getTime());
            SimpleDateFormat tformat = new SimpleDateFormat("HH:mm:ss");
            String time = tformat.format(calender.getTime());

            String exStorage = Environment.getExternalStorageDirectory().getAbsolutePath();
            String myDir = "linkoslogs" + File.separator + date;
            String fileName = date + "_log.txt";
            if ((Environment.getExternalStorageDirectory().getFreeSpace() / 1024 / 1024) > 20) {

                File myDiry = new File(exStorage + File.separator + myDir);
                if (!myDiry.exists()) {
                    myDiry.mkdirs();
                }

                File file = new File(exStorage + File.separator + myDir, fileName);
                if (!file.exists()) {
                    FileOutputStream fos = null;
                    String value = "Time; \t DA1°C; \t +2.5V; \t +1.8V; \t +1.2V; \t 1V; \t DA6°C; \t ?; \t +5V; \t +1.8V; \t +0.95; \t +3.3V; \t +1.5V; \t +12V; \t DA2°C; \t DA7°C; \t +5V; \n\r";
                    try {
                        fos = new FileOutputStream(file, true);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (fos != null) {
                        try {
                            fos.write(value.getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            fos.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(shower.this, getString(R.string.notenoughmemorytext), Toast.LENGTH_SHORT).show();
                    }
                } else {


                    FileOutputStream fos = null;
                    String value = "\n\r" + time;
                    for (int i = 0; i < dataList.size(); i++) {
                        value += String.valueOf(dataList.get(i)) + ";\t";
                    }
                    value += "\n \r";

                    try {
                        fos = new FileOutputStream(file, true);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    if (fos != null) {
                        try {
                            fos.write(value.getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            fos.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(shower.this, getString(R.string.notenoughmemorytext), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        }
        else {
           // ls.setText(R.string.log_off_button);
        }
    }

}


