package com.client.gp.sharksclientapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.client.gp.sharksclientapplication.myclasses.AppConstants;

import org.json.JSONException;
import org.json.JSONObject;

public class WaitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);
        getSupportActionBar().hide();

        /////recive trip request
        IntentFilter filter = new IntentFilter(AppConstants.BROADCAST_TRIP_ACCEPT_ACTION);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                startActivity(new Intent(WaitActivity.this, ArrivingActivity.class));
                finish();
            }
        };
        registerReceiver(receiver, filter);

        sendTestAcceptance();

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

    void sendTestStarted(){

        JSONObject jso = new JSONObject();
        try {
            jso.put("type", "tripstarted");
            jso.put("destlat", 30.123177);
            jso.put("destlng", 31.009540);
            MyApplication.sendNotification(jso);

        } catch (JSONException e) { e.printStackTrace(); }
    }



}