package ru.sergioleko.usboncemore;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ErrorWindow extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_window);
        setTitle(" ");

    }
    public void restart (View view) {
        Intent retryIntent = new Intent(this, MainActivity.class);
        startActivity(retryIntent);
    }
}
