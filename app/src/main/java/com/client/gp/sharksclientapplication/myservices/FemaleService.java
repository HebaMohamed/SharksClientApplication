package com.client.gp.sharksclientapplication.myservices;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.client.gp.sharksclientapplication.FemaleActivity;
import com.client.gp.sharksclientapplication.InTripActivity;
import com.client.gp.sharksclientapplication.MyApplication;
import com.client.gp.sharksclientapplication.myclasses.Trip;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationRequest;

/**
 * Created by dell on 4/26/2017.
 */

public class FemaleService  extends Service implements SensorListener {
    private static final String TAG = "FemaleService";
    //    public static final String ACTION = "com.sharks.gp.sharkspassengerapplication.myservices.LocationService";
    //    private long startTimeInMilliSeconds = 0L;
    SensorManager sensorMgr;

    private static final int SHAKE_THRESHOLD = 1000;//800

    float x, y, z;
    float last_x, last_y, last_z;
    long lastUpdate;
    //    private Connection conn;
    public static int count = 0;
    Trip t;

    private boolean isServiceRunning = false;
    @Override
    public void onCreate() {
        super.onCreate();

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isServiceRunning = true;

        sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorMgr.registerListener(this,
                SensorManager.SENSOR_ACCELEROMETER,
                SensorManager.SENSOR_DELAY_GAME);

        t = MyApplication.getPickupTrip();


        return Service.START_STICKY;
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorMgr.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(int sensor, float[] floats) {
        if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            // only allow one update every 100ms.
            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                x = floats[SensorManager.DATA_X];
                y = floats[SensorManager.DATA_Y];
                z = floats[SensorManager.DATA_Z];

                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;
//                counttxt.setText(String.valueOf(speed));

                Log.d("sensor", "normal speed reading: " + speed);


                if (speed > SHAKE_THRESHOLD) {
                    Log.d("sensor", "shake detected w/ speed: " + speed+"count = "+count);
//                    sensortxt.setText("Motion");
//                    sensortxt.setTextColor(Color.RED);

                    count++;
                    if (count > 5) {
//                        Toast.makeText(MainActivity.this, "There is any problem ?!", Toast.LENGTH_SHORT).show();
                        //send alarm to server
                        try {
//                            Statement st = conn.createStatement();
//                            st.execute("INSERT INTO sensor (passengerid,speed) VALUES (" + passengerid + "," + speed + ");");
//                            sendPubWarning();

                            long ts = System.currentTimeMillis();
                            MyApplication.myFirebaseRef.child("warning").child("femalesaftey").child(String.valueOf(ts)).child("tid").setValue(String.valueOf(t.trip_ID));
                            MyApplication.myFirebaseRef.child("warning").child("femalesaftey").child(String.valueOf(ts)).child("lat").setValue(String.valueOf(FemaleActivity.lat));
                            MyApplication.myFirebaseRef.child("warning").child("femalesaftey").child(String.valueOf(ts)).child("lng").setValue(String.valueOf(FemaleActivity.lng));
                            MyApplication.myFirebaseRef.child("warning").child("femalesaftey").child(String.valueOf(ts)).child("vid").setValue(MyApplication.getCurrentDriver().vehicle.id);
                            MyApplication.myFirebaseRef.child("warning").child("femalesaftey").child(String.valueOf(ts)).child("status").setValue("new");

                            MyApplication.myFirebaseRef.child("notifications").child("femalewarning").setValue("NEW");

                            startHelpActivity();



                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        count = 0;
                    }
                }else {
//                    sensortxt.setText("No Motion");
//                    sensortxt.setTextColor(Color.GREEN);
                }
                last_x = x;
                last_y = y;
                last_z = z;

//                xtxt.setText(String.valueOf(x));
//                ytxt.setText(String.valueOf(y));
//                ztxt.setText(String.valueOf(z));


            }

        }

    }



    void startHelpActivity()
    {
        Intent intent = new Intent("android.intent.category.LAUNCHER");
        intent.setClassName("com.client.gp.sharksclientapplication", "com.client.gp.sharksclientapplication.FemaleActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onAccuracyChanged(int i, int i1) {

    }
}