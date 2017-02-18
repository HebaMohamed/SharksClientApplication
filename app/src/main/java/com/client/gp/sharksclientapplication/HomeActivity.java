package com.client.gp.sharksclientapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

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

    }
}
