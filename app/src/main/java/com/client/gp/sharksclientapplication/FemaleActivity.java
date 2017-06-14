package com.client.gp.sharksclientapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;

import com.client.gp.sharksclientapplication.myclasses.Passenger;

public class FemaleActivity extends AppCompatActivity {

    Button dangerbtn,smsbtn,callbtn;

    public static double lat,lng;

    Passenger p;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_female);

        dangerbtn = (Button)findViewById(R.id.dangerbtn);
        smsbtn = (Button)findViewById(R.id.smsbtn);
        callbtn = (Button)findViewById(R.id.callbtn);

        p = MyApplication.getLoggedPassenger();


        smsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = "Its a female safety warning!, my location is lat:"+lat+" ,lng:"+lng;
                SmsManager smsManager =     SmsManager.getDefault();
                smsManager.sendTextMessage(p.relative_phone, null, "Message", null, null);
            }
        });

        dangerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}