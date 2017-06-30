package com.client.gp.sharksclientapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.client.gp.sharksclientapplication.myclasses.AppConstants;
import com.client.gp.sharksclientapplication.myclasses.MyURL;
import com.client.gp.sharksclientapplication.myclasses.Passenger;
import com.client.gp.sharksclientapplication.myservices.FemaleService;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    Passenger p;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



//        startService(new Intent(MyApplication.getAppContext(), FemaleService.class));//only start when start trip //start service
//        stopService(new Intent(MyApplication.getAppContext(), FemaleService.class));//only start when start trip //start service


        try {
            p = MyApplication.getLoggedPassenger();

            if(p!=null) {
                passengerReopen(p);
            }
            else {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }



        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    void passengerReopen(Passenger p){
        if(p!=null) {
            String appstate = MyApplication.getAppState();
            if(appstate.equals("requestsent")){
//                    startActivity(new Intent(MainActivity.this, WaitActivity.class));
//                    finish();
                startActivity(new Intent(MainActivity.this, HomeActivity.class));
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
    }

}
