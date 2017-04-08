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
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.TextView;

import com.client.gp.sharksclientapplication.myclasses.AppConstants;
import com.client.gp.sharksclientapplication.myclasses.Driver;
import com.client.gp.sharksclientapplication.myclasses.LatLngInterpolator;
import com.client.gp.sharksclientapplication.myclasses.Trip;
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

public class ArrivingActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    TextView dnametxt, vmodeltxt, vnumtxt;

    Driver tripDriver;
    Trip currentTrip;
    private Marker drivermarker;

    LatLng lll;


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

        tripDriver=MyApplication.getCurrentDriver();

        dnametxt.setText(tripDriver.name);
        vmodeltxt.setText(tripDriver.vehicle.model);
        vnumtxt.setText(tripDriver.vehicle.plate_number);


        currentTrip=MyApplication.getPickupTrip();

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

                if(status.equals("started")) {

                    double destlat = dataSnapshot.child("destlat").getValue(Double.class);
                    double destlng = dataSnapshot.child("destlng").getValue(Double.class);

                    //save request for activity use
                    MyApplication.setInTripState(destlat, destlng);
                    startActivity(new Intent(ArrivingActivity.this, InTripActivity.class));
                    finish();
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

                double lat = dataSnapshot.child("lat").getValue(Double.class);
                double lng = dataSnapshot.child("lng").getValue(Double.class);

                lll = new LatLng(lat, lng);
                animateMarkerToGB(drivermarker,lll);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });

//        //listen to my vehicles moves
//        try {
//            MyApplication.pubnub.subscribe(AppConstants.CHANNEL_PartnersLocation, new Callback() {
//                        @Override
//                        public void connectCallback(String channel, Object message) {
////                            pubnub.publish("my_channel", "Hello from the PubNub Java SDK", new Callback() {});
//                        }
//
//                        @Override
//                        public void disconnectCallback(String channel, Object message) {
//                            System.out.println("SUBSCRIBE : DISCONNECT on channel:" + channel + " : " + message.getClass() + " : " + message.toString());
//                        }
//
//                        public void reconnectCallback(String channel, Object message) {
//                            System.out.println("SUBSCRIBE : RECONNECT on channel:" + channel + " : " + message.getClass() + " : " + message.toString());
//                        }
//
//                        @Override
//                        public void successCallback(String channel, Object message) { //l msg bttst2bl hna
//                            System.out.println("SUBSCRIBE : " + channel + " : " + message.getClass() + " : " + message.toString());
//
//                            try {
//                                JSONObject obj = (JSONObject) message;
//                                int id = obj.getInt("id");
//                                if (id == tripDriver.id) {//get location for my vehicle
//                                    Double lat = obj.getDouble("lat");
//                                    Double lng = obj.getDouble("lng");
//                                    final LatLng ll = new LatLng(lat, lng);
//
//                                    runOnUiThread(new Runnable() { // l runnable d 3shn err IllegalStateException 3shn d async
//                                        @Override
//                                        public void run() {
//                                            // Your code to run in GUI thread here
//                                            drivermarker.setPosition(ll);
//                                        }
//                                    });
//
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        @Override
//                        public void errorCallback(String channel, PubnubError error) {
//                            System.out.println("SUBSCRIBE : ERROR on channel " + channel + " : " + error.toString());
//                        }
//                    }
//            );
//        } catch (PubnubException e) {
//            System.out.println(e.toString());
//        }

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
