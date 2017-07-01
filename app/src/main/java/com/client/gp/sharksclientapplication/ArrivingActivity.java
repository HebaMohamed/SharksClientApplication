package com.client.gp.sharksclientapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.TextView;

import com.client.gp.sharksclientapplication.myclasses.AppConstants;
import com.client.gp.sharksclientapplication.myclasses.Driver;
import com.client.gp.sharksclientapplication.myclasses.LatLngInterpolator;
import com.client.gp.sharksclientapplication.myclasses.Passenger;
import com.client.gp.sharksclientapplication.myclasses.Trip;
import com.client.gp.sharksclientapplication.myservices.FemaleService;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import at.markushi.ui.CircleButton;

public class ArrivingActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    TextView dnametxt, vmodeltxt, vnumtxt;

    Driver tripDriver;
    Trip currentTrip;
    Passenger passenger;
    private Marker drivermarker;

    CircleButton cancelbtn;

    LatLng lll;
    int x = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arriving);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        dnametxt=(TextView)findViewById(R.id.dnametxt);
        vmodeltxt=(TextView)findViewById(R.id.vmodeltxt);
        vnumtxt=(TextView)findViewById(R.id.vnumtxt);

        cancelbtn=(CircleButton)findViewById(R.id.cancelbtn);

        tripDriver=MyApplication.getCurrentDriver();
        passenger=MyApplication.getLoggedPassenger();



        dnametxt.setText(tripDriver.name);
        vmodeltxt.setText(tripDriver.vehicle.model);
        vnumtxt.setText(tripDriver.vehicle.plate_number);


        currentTrip=MyApplication.getPickupTrip();

        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.myFirebaseRef.child("trips").child(String.valueOf(currentTrip.trip_ID)).child("status").setValue("canceled");
                MyApplication.setReadyState();
                startActivity(new Intent(ArrivingActivity.this, HomeActivity.class));
                finish();
            }
        });

//        /////recive trip start
//        IntentFilter filter = new IntentFilter(AppConstants.BROADCAST_TRIP_START_ACTION);
//        BroadcastReceiver receiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                startActivity(new Intent(ArrivingActivity.this, InTripActivity.class));
//                finish();
//            }
//        };
//        registerReceiver(receiver, filter);

        //sendTestStarted();//testttttttt


        //listen & get initial value
        MyApplication.myFirebaseRef.child(AppConstants.FIRE_TRIPS).child(String.valueOf(currentTrip.trip_ID)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String status = dataSnapshot.child("status").getValue(String.class);

                if(status.equals("started")&&(x==0)) {

                    try {

                        double destlat = dataSnapshot.child("destlat").getValue(Double.class);
                        double destlng = dataSnapshot.child("destlng").getValue(Double.class);

                        //save request for activity use
                        MyApplication.setInTripState(destlat, destlng);

                        x=1;//y3ny 2ra klo 3shn ynfz mra w7da bs
                        startActivity(new Intent(ArrivingActivity.this, InTripActivity.class));
                        finish();
                    }
                    catch ( NullPointerException ne){
                        ne.printStackTrace();
                    }
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng ll = new LatLng(currentTrip.pickup.getLatitude(), currentTrip.pickup.getLongitude());
        mMap.addMarker(new MarkerOptions()
                .position(ll)
                .title("Pickup Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.passmarker)));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(ll));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        //init driver marker mn 8er position
        drivermarker =  mMap.addMarker(new MarkerOptions()
                .position(ll)
                .title("My Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.smallblueshark)));



        //listen & get initial value
        MyApplication.myFirebaseRef.child(AppConstants.FIRE_VEHICLES).child(String.valueOf(tripDriver.vehicle.id)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                double lat = dataSnapshot.child("Latitude").getValue(Double.class);
                double lng = dataSnapshot.child("Longitude").getValue(Double.class);

                lll = new LatLng(lat, lng);
                animateMarkerToGB(drivermarker,lll);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });

    }



    static void animateMarkerToGB(final Marker marker, final LatLng finalPosition) {
        final LatLng startPosition = marker.getPosition();

        ///
//        initGMaps(mypos,finalPosition,selectedtransportModeFlag);
        ///
        final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.Spherical();
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 3000;

        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                v = interpolator.getInterpolation(t);

                marker.setPosition(latLngInterpolator.interpolate(v, startPosition, finalPosition));

                // Repeat till progress is complete.
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    ///////////////////////////////////////////////////////////////////
//    void sendTestStarted(){
//
//        JSONObject jso = new JSONObject();
//        try {
//            jso.put("type", "tripstarted");
//            jso.put("destlat", 30.123177);
//            jso.put("destlng", 31.009540);
//            MyApplication.sendNotification(jso);
//
//        } catch (JSONException e) { e.printStackTrace(); }
//    }
    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }


}
