package com.client.gp.sharksclientapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.client.gp.sharksclientapplication.myclasses.AppConstants;
import com.client.gp.sharksclientapplication.myclasses.Driver;
import com.client.gp.sharksclientapplication.myclasses.Trip;
import com.client.gp.sharksclientapplication.myclasses.Vehicle;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pubnub.api.Callback;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONException;
import org.json.JSONObject;

public class WaitActivity extends AppCompatActivity {

    Trip trip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);
        getSupportActionBar().hide();

        trip = MyApplication.getPickupTrip();

        /////recive trip request
//        IntentFilter filter = new IntentFilter(AppConstants.BROADCAST_TRIP_ACCEPT_ACTION);
//        BroadcastReceiver receiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                startActivity(new Intent(WaitActivity.this, ArrivingActivity.class));
//                finish();
//            }
//        };
//        registerReceiver(receiver, filter);

        //sendTestAcceptance();

        //listen & get initial value
        MyApplication.myFirebaseRef.child(AppConstants.FIRE_TRIPS).child(String.valueOf(trip.trip_ID)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String status = dataSnapshot.child("status").getValue(String.class);

                if(status.equals("approved")) {

                    //save request for activity use
                    MyApplication.setArrivingState();
                    startActivity(new Intent(WaitActivity.this, ArrivingActivity.class));
                    finish();
                }
                else if(status.equals("ignored")) {
                    Toast.makeText(WaitActivity.this, "The nearest driver ignored trip please try again", Toast.LENGTH_LONG).show();
                    finish();
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });

    }

    void sendTestAcceptance(){

        JSONObject jso = new JSONObject();
        try {
            jso.put("type", "requestresponse");
            jso.put("tripid", 1);
            jso.put("driverid", 1);
            MyApplication.sendNotification(jso);

        } catch (JSONException e) { e.printStackTrace(); }
    }





}
