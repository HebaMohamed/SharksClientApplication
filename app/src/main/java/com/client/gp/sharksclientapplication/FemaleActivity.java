package com.client.gp.sharksclientapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class FemaleActivity extends AppCompatActivity {

    Button dangerbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_female);

         dangerbtn = (Button)findViewById(R.id.dangerbtn);


    }
}
