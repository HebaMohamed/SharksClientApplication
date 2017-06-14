package com.client.gp.sharksclientapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.client.gp.sharksclientapplication.myclasses.Driver;
import com.client.gp.sharksclientapplication.myclasses.Passenger;

import at.markushi.ui.CircleButton;
import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    CircleImageView circleImageView;
    EditText nametxt,emailtxt,passtxt,phtxt,relatedtxt;
    TextView fullnametxt;
    Button outbtn,savebtn;

    Passenger p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        p = MyApplication.getLoggedPassenger();

        circleImageView = (CircleImageView)findViewById(R.id.circleImageView);
        fullnametxt = (TextView)findViewById(R.id.fullnametxt);
        nametxt = (EditText)findViewById(R.id.nametxt);
        emailtxt = (EditText)findViewById(R.id.emailtxt);
        passtxt = (EditText)findViewById(R.id.passtxt);
        phtxt = (EditText)findViewById(R.id.phtxt);
        relatedtxt = (EditText)findViewById(R.id.relatedtxt);
        outbtn = (Button)findViewById(R.id.outbtn);
        savebtn = (Button)findViewById(R.id.savebtn);

        fullnametxt.setText(p.fullName);
        nametxt.setText(p.fullName);
        emailtxt.setText(p.email);
        passtxt.setText("");
        phtxt.setText(p.phone);
        relatedtxt.setText(p.relative_phone);


        outbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.storelogout();
                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
            }
        });


        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
}
