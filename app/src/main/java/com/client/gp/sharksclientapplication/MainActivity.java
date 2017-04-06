package com.client.gp.sharksclientapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.client.gp.sharksclientapplication.myclasses.MyURL;
import com.client.gp.sharksclientapplication.myclasses.Passenger;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





        try {
            Passenger p = MyApplication.getLoggedPassenger();
            if(p!=null) {
                String appstate = MyApplication.getAppState();
                if(appstate.equals("requestsent")){
                    startActivity(new Intent(MainActivity.this, WaitActivity.class));
                    finish();
                } else if(appstate.equals("arriving")){
                    startActivity(new Intent(MainActivity.this, ArrivingActivity.class));
                    finish();
                } else if(appstate.equals("intrip")){
                    startActivity(new Intent(MainActivity.this, InTripActivity.class));
                    finish();
                } else if(appstate.equals("tripended")){
                    startActivity(new Intent(MainActivity.this, DoneTripActivity.class));
                    finish();
                }else{
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                    finish();
                }


            }
            else {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
