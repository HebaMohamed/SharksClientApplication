package com.client.gp.sharksclientapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.client.gp.sharksclientapplication.myservices.FemaleService;

import at.markushi.ui.CircleButton;

public class HomeActivity extends AppCompatActivity {

    CircleButton gobtn;
    ImageButton historybtn,settingsbtn;
    TextView usernametxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();

        gobtn=(CircleButton)findViewById(R.id.gobtn);
        historybtn=(ImageButton)findViewById(R.id.historybtn);
        settingsbtn=(ImageButton)findViewById(R.id.settingsbtn);
        usernametxt=(TextView)findViewById(R.id.usernametxt);

        gobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, PickupMapActivity.class));
            }
        });

        historybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, LastTripsActivity.class));
            }
        });

        settingsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //MyApplication.storelogout();
//                stopService(new Intent(MyApplication.getAppContext(), FemaleService.class));//only start when start trip //start service

                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));


            }
        });

    }
    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }

}
