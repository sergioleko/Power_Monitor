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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shower);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //boolean isCheck = false;
        setTitle(" ");
        //final Button switchShower = findViewById(R.id.autorefreshbutton);
        //switchShower.setText(R.string.autorefreshon);
        // final Switch switchShower = findViewById(R.id.autorefreshSwitch);
        /*switchShower.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final Switch switchShower = findViewById(R.id.autorefreshSwitch);
                final Handler switchHandler = new Handler();
                final int delay = 1000;

                final Runnable runnable = new Runnable() {

                    public void run() {
                        if (switchShower.isChecked()){
                            startReader();
                            switchHandler.postDelayed(this, 1000);
                        }
                        else {
                            switchHandler.removeCallbacks(this);
                        }
                    }
                };
                return false;
            }


        });*/

        //Toast toast = Toast.makeText(getApplicationContext(),privateDir.toString(), Toast.LENGTH_SHORT);
        //toast.show();
        Calendar calender = Calendar.getInstance();
        SimpleDateFormat dformat = new SimpleDateFormat("yyyy_MM_dd");
        String date = dformat.format(calender.getTime());
        SimpleDateFormat tformat = new SimpleDateFormat("HH:mm:ss");
        String time = tformat.format(calender.getTime());
        //Toast toast = Toast.makeText(getApplicationContext(),date, Toast.LENGTH_SHORT);
        //toast.show();
        String exStorage = Environment.getExternalStorageDirectory().getAbsolutePath();
        String myDir = "linkoslogs" + File.separator + date;
        String fileName = date + "_log.txt";
        File myDiry = new File(exStorage + File.separator + myDir);
        if (!myDiry.exists()){
            myDiry.mkdirs();
        }
        File file = new File(exStorage + File.separator + myDir, fileName);
        FileOutputStream fos = null;
        String value = "New logging session \n\r Time; \t DA1°C; \t +2.5V; \t +1.8V; \t +1.2V; \t 1V; \t DA6°C; \t ?; \t +5V; \t +1.8V; \t +0.95; \t +3.3V; \t +1.5V; \t +12V; \t DA2°C; \t DA7°C; \t +5V; \n\r";
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
        }
        startReader();


    }


    public void startReader() {
        UsbManager usbmanager = (UsbManager) getSystemService(Context.USB_SERVICE);
        //UsbAccessory[] listik = usbmanager.getAccessoryList();
        usbmanager.getAccessoryList();
        HashMap<String, UsbDevice> deviceList = usbmanager.getDeviceList();
        if (deviceList != null) {
            byte[] bytes = new byte[64];
            List<Float> dataList = new ArrayList<Float>();
            // TextView text = findViewById(R.id.showerText);
            CheckBox su3 = (CheckBox) findViewById(R.id.checkBox3);
            CheckBox su5 = (CheckBox) findViewById(R.id.checkBox);
            CheckBox thrm = (CheckBox) findViewById(R.id.checkBox2);
            //Button refreshButton = findViewById(R.id.refreshButtonShower);
            //UsbManager usbmanager = (UsbManager) getSystemService(Context.USB_SERVICE);

                //HashMap<String, UsbDevice> deviceList = usbmanager.getDeviceList();
                Set<String> keys = deviceList.keySet();
                Object[] keysArr = keys.toArray();
               // assert keysArr != null;
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

                    //SSS += /*names[i / 4] + "\t=\t" + */String.valueOf(Data) + "\n";
                }
                for (int c = 0; c < dataList.size(); c++) {
                    String textviewid = "textView" + c;
                    int resID = getResources().getIdentifier(textviewid, "id", getPackageName());
                    TextView output = findViewById(resID/*R.id.textView0*/);
                    String outputText = String.valueOf(names[c]) + "\t = \t" + String.valueOf(dataList.get(c));
                    output.setText(outputText);
                    if ((dataList.get(c) > maxValues[c] || dataList.get(c) < minvalues[c])) {
                        output.setTextColor(Color.parseColor("#ffff4444"));
                    } else {
                        output.setTextColor(Color.parseColor("#ff669900"));
                    }
                    //TextView outputy = findViewById(R.id.textView0);
                    //outputy.setText(String.valueOf(names.length) + String.valueOf(dataList.size()));
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

                //text.setText(String.valueOf(dataList.size()));

        //return dataList;
        createLog(dataList);
        }

    else {
            Intent errorPageIntent = new Intent(this, ErrorWindow.class);
            startActivity(errorPageIntent);}
            //return null;
    }

    public void refresher(View view) {
        startReader();
    }

    public void autorefresher(View view) {
        //final Switch switchShower = findViewById(R.id.autorefreshSwitch);



            /*Toast toast = Toast.makeText(getApplicationContext(), stringy, Toast.LENGTH_LONG);
            toast.show();*/
        final Handler switchHandler = new Handler();
        checkOnOff ();
        final Runnable runnable = new Runnable() {

            public void run() {
                if (onoff){
                    startReader();
                    //createLog(startReader());
                    switchHandler.postDelayed(this, 1000);
                }
                else {
                    switchHandler.removeCallbacks(this);
                }
            }
        };
        switchHandler.removeCallbacks(runnable);
        switchHandler.postDelayed(runnable,1000);
       /* switchHandler.postDelayed(new Runnable(){
                  public void run() {
                      startReader();
                      switchHandler.postDelayed(this, delay);

                  }
        }, delay);*/

        }
        public boolean checkOnOff (){
        Button switchShower = findViewById(R.id.autorefreshbutton);
        String stringy = getString(R.string.autorefreshon);

        if (switchShower.getText().toString().equals(stringy)) {
            onoff = true;
            switchShower.setText(R.string.autorefreshoff);
        }
        else {
            onoff = false;
            switchShower.setText(R.string.autorefreshon);
        }
        return onoff;
    }



    public void createLog (List<Float> dataList){

        //File privateDir = new File(getExternalFilesDir(null).getAbsolutePath());

        //Toast toast = Toast.makeText(getApplicationContext(),privateDir.toString(), Toast.LENGTH_SHORT);
        //toast.show();
        Calendar calender = Calendar.getInstance();
        SimpleDateFormat dformat = new SimpleDateFormat("yyyy_MM_dd");
        String date = dformat.format(calender.getTime());
        SimpleDateFormat tformat = new SimpleDateFormat("HH:mm:ss");
        String time = tformat.format(calender.getTime());
        //Toast toast = Toast.makeText(getApplicationContext(),date, Toast.LENGTH_SHORT);
        //toast.show();
        String exStorage = Environment.getExternalStorageDirectory().getAbsolutePath();
        String myDir = "linkoslogs" + File.separator + date;
        String fileName = date + "_log.txt";

        File file = new File(exStorage + File.separator + myDir, fileName);
        FileOutputStream fos = null;
        String value = time;
        for (int i = 0; i < dataList.size(); i++){
            value += String.valueOf(dataList.get(i)) + ";\t";
        }
        value += "\n \r";

        try {
            fos = new FileOutputStream(file, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //OutputStreamWriter osw = new OutputStreamWriter(fos);
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
        }

    }

}


