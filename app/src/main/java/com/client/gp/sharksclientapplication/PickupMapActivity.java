package com.client.gp.sharksclientapplication;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.client.gp.sharksclientapplication.myclasses.AppConstants;
import com.client.gp.sharksclientapplication.myclasses.LatLngInterpolator;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PickupMapActivity extends FragmentActivity implements OnMapReadyCallback ,LocationListener {

    private GoogleMap mMap;
    TextView addrtxt;
    Button submitbtn2;//submitbtn
    public static double lat, lng;
    public static String approxAddress;

    ArrayList<Marker> markers = new ArrayList<>();
    Location loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        addrtxt = (TextView) findViewById(R.id.addrtxt);
//        submitbtn = (Button) findViewById(R.id.submitbtn);
        submitbtn2 = (Button) findViewById(R.id.submitbtn2);

//        submitbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(PickupMapActivity.this, SubmitRequestActivity.class));
//                finish();
//            }
//        });

        submitbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PickupMapActivity.this, RecommendedMapActivity.class));
                finish();
            }
        });

    }

    LatLng ll;//int id;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        buildGoogleApiClient();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        mMap.setMyLocationEnabled(true);

        //show my location
//        Location loc = MyApplication.getLastKnownLocation();
//        LatLng ll = new LatLng(loc.getLatitude(),loc.getLongitude());//for test onlyyy //ay location we hyt8yr lma ysm3
//        //move to this location
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(ll));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));



        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                addrtxt.setText("Loading");
            }
        });
        mMap.setOnCameraMoveCanceledListener(new GoogleMap.OnCameraMoveCanceledListener() {
            @Override
            public void onCameraMoveCanceled() {
            }
        });
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                Log.d("Locationnnn", "lat: "+mMap.getCameraPosition().target.latitude+" Lng: "+mMap.getCameraPosition().target.longitude);
                lat=mMap.getCameraPosition().target.latitude;
                lng=mMap.getCameraPosition().target.longitude;

                //get approx loc
                try {
                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(PickupMapActivity.this, Locale.getDefault());

                    addresses = geocoder.getFromLocation(mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    approxAddress = addresses.get(0).getAddressLine(0);
                    addrtxt.setText(approxAddress);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        int x = 0;

        //listen to all vehicles moves

//        String g = MyApplication.myFirebaseRef.child("trips").child("1").child("status").getKey();


        //listen & get initial value
        MyApplication.myFirebaseRef.child(AppConstants.FIRE_VEHICLES).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    try {
                        String vid = postSnapshot.getKey();
                        double lat = postSnapshot.child("lat").getValue(Double.class);
                        double lng = postSnapshot.child("lng").getValue(Double.class);

                        ll = new LatLng(lat, lng);
                        int f = 0;
                        for (int i = 0; i < markers.size(); i++) {
                            if (markers.get(i).getSnippet().toString().equals(String.valueOf(vid))) {
                                //markers.get(i).setPosition(ll);
                                // animation part
                                animateMarkerToGB(markers.get(i), ll);
                                f = 1;
                            }
                        }
                        if (f == 0) {
                            markers.add(mMap.addMarker(new MarkerOptions()
                                    .position(ll)
                                    .title("Shark Location")
                                    .snippet(String.valueOf(vid))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.smallblueshark))));
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();//3shn momkn fl test yb2a fy node msh feha lat msln
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });

        x=1;
//        try {
//            MyApplication.pubnub.subscribe(AppConstants.CHANNEL_PartnersLocation, new Callback() {
//                        @Override
//                        public void connectCallback(String channel, Object message) {
////                            pubnub.publish("my_channel", "Hello from the PubNub Java SDK", new Callback() {});
//                        }
//
//                        @Override
//                        public void disconnectCallback(String channel, Object message) {
//                            System.out.println("SUBSCRIBE : DISCONNECT on channel:" + channel
//                                    + " : " + message.getClass() + " : "
//                                    + message.toString());
//                        }
//
//                        public void reconnectCallback(String channel, Object message) {
//                            System.out.println("SUBSCRIBE : RECONNECT on channel:" + channel
//                                    + " : " + message.getClass() + " : "
//                                    + message.toString());
//                        }
//
//                        @Override
//                        public void successCallback(String channel, Object message) { //l msg bttst2bl hna
//                            System.out.println("SUBSCRIBE : " + channel + " : "
//                                    + message.getClass() + " : " + message.toString());
//
//                            try {
//                                JSONObject obj = (JSONObject) message;
//                                id = obj.getInt("did");
//                                Double lat = obj.getDouble("lat");
//                                Double lng = obj.getDouble("lng");
//                                ll = new LatLng(lat, lng);
//
//                                runOnUiThread(new Runnable() { // l runnable d 3shn err IllegalStateException 3shn d async
//                                    @Override
//                                    public void run() {
//                                        // Your code to run in GUI thread here
//
//                                        int f = 0;
//                                        for(int i=0; i<markers.size(); i++){
//                                            if(markers.get(i).getSnippet().toString().equals(String.valueOf(id))){
//                                                //markers.get(i).setPosition(ll);
//                                                //animation part
//                                                animateMarkerToGB(markers.get(i),ll);
//                                                f=1;
//                                            }
//                                        }
//                                        if(f==0) {
//                                            markers.add(mMap.addMarker(new MarkerOptions()
//                                                    .position(ll)
//                                                    .title("Shark Location")
//                                                    .snippet(String.valueOf(id))
//                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.smallblueshark))));
//                                        }
//                                    }
//                                });
//
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        @Override
//                        public void errorCallback(String channel, PubnubError error) {
//                            System.out.println("SUBSCRIBE : ERROR on channel " + channel
//                                    + " : " + error.toString());
//                        }
//                    }
//            );
//        } catch (PubnubException e) {
//            System.out.println(e.toString());
//        }



    }




    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    protected synchronized void buildGoogleApiClient() {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10 * 1000);
        mLocationRequest.setFastestInterval(1 * 1000);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        if (ActivityCompat.checkSelfPermission(PickupMapActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(PickupMapActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        //LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,GeoLocMapActivity.this );
                        loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                        if (loc != null) {
//            dLat = loc.getLatitude();
//            dLong = loc.getLongitude();
//            //Does this log?
//            Log.d(getClass().getSimpleName(), String.valueOf(dLat) + ", " + String.valueOf(dLong));

//                            mMap.clear();
//                            mMap.addMarker(new MarkerOptions().position(new LatLng(loc.getLatitude(), loc.getLongitude())).title("Assistant Location"));
//                            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(loc.getLatitude(), loc.getLongitude())));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()),14));



                        } else {
                            Toast.makeText(PickupMapActivity.this, "no location detected", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        mMap.setMyLocationEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected())
//            mGoogleApiClient.connect();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mGoogleApiClient != null)
            if (mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting()){
                mGoogleApiClient.disconnect();
                mGoogleApiClient.connect();
            } else if (!mGoogleApiClient.isConnected()){
                mGoogleApiClient.connect();
            }
//        Toast.makeText(GeoLocMapActivity.this, "Loc", Toast.LENGTH_SHORT).show();
        loc = location;

        LatLng ll = new LatLng(loc.getLatitude(),loc.getLongitude());//ay location we hyt8yr lma ysm3
        //move to this location
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ll));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));

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


}
