package com.client.gp.sharksclientapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.client.gp.sharksclientapplication.myclasses.Passenger;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            Passenger p = MyApplication.getLoggedPassenger();
            if(p!=null)
                startActivity(new Intent(MainActivity.this, HomeActivity.class));
            else
                startActivity(new Intent(MainActivity.this, LoginActivity.class));

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
