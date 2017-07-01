package com.client.gp.sharksclientapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.client.gp.sharksclientapplication.myclasses.AppConstants;
import com.client.gp.sharksclientapplication.myclasses.Passenger;
import com.client.gp.sharksclientapplication.myclasses.Trip;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class FemaleActivity extends AppCompatActivity {

    Button dangerbtn,smsbtn,callbtn;
    TextView helptxt;

    public static double lat,lng;


    Passenger p;
    Trip t;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_female);

        setTitle("Female Warning");

        dangerbtn = (Button)findViewById(R.id.dangerbtn);
        smsbtn = (Button)findViewById(R.id.smsbtn);
        callbtn = (Button)findViewById(R.id.callbtn);
        helptxt = (TextView)findViewById(R.id.helptxt);

        p = MyApplication.getLoggedPassenger();
        t = MyApplication.getPickupTrip();


        smsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = "Its a female safety warning!, my location is lat:"+lat+" ,lng:"+lng;
                Toast.makeText(FemaleActivity.this, "Your message is sent !, check your sms manager", Toast.LENGTH_LONG).show();
                SmsManager smsManager =     SmsManager.getDefault();
                smsManager.sendTextMessage(p.relative_phone, null, msg, null, null);
            }
        });

        dangerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAndShowAlertDialog();
            }
        });

        callbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + p.relative_phone));
                if (ActivityCompat.checkSelfPermission(FemaleActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(intent);
            }
        });


        //listen to help actions
        MyApplication.myFirebaseRef.child("warning").child("femalesaftey").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    int tid = postSnapshot.child("tid").getValue(int.class);
                    String status = postSnapshot.child("status").getValue(String.class);
                    if(tid==t.trip_ID && (!status.equals("ended"))){
                        helptxt.setText("Your help request is submitted, we'll take an action ASAP!");
                        dangerbtn.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });

    }
    private void createAndShowAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure to request help ?");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                sendHelp();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    void sendHelp(){
        long ts = System.currentTimeMillis();
        MyApplication.myFirebaseRef.child("warning").child("femalesaftey").child(String.valueOf(ts)).child("tid").setValue(String.valueOf(t.trip_ID));
        MyApplication.myFirebaseRef.child("warning").child("femalesaftey").child(String.valueOf(ts)).child("lat").setValue(String.valueOf(FemaleActivity.lat));
        MyApplication.myFirebaseRef.child("warning").child("femalesaftey").child(String.valueOf(ts)).child("lng").setValue(String.valueOf(FemaleActivity.lng));
        MyApplication.myFirebaseRef.child("warning").child("femalesaftey").child(String.valueOf(ts)).child("vid").setValue(MyApplication.getCurrentDriver().vehicle.id);
        MyApplication.myFirebaseRef.child("warning").child("femalesaftey").child(String.valueOf(ts)).child("status").setValue("new");

        MyApplication.myFirebaseRef.child("notifications").child("femalewarning").setValue("NEW");
    }

}
