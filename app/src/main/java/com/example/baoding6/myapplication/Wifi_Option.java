package com.example.baoding6.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Wifi_Option extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi__option);
    }

    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btn_WiFi_Edit:

                break;
            case R.id.btn_WiFi_Cancel:
                Intent intent = new Intent();
                intent.setClass(Wifi_Option.this, MainActivity.class);
                this.startActivity(intent);
                this.finish();
                break;
            default:
                break;
        }
    }

}
