package com.client.gp.sharksclientapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.client.gp.sharksclientapplication.myclasses.AppConstants;
import com.client.gp.sharksclientapplication.myclasses.LatLngInterpolator;
import com.client.gp.sharksclientapplication.myclasses.TalkMessage;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.markushi.ui.CircleButton;

public class InTripActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    TextView addresstxt, durationtxt, disttxt;
    CircleButton chatbtn, warningbtn;

    Trip currentTrip;
    private Marker drivermarker;

    int f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_trip);

        addresstxt=(TextView)findViewById(R.id.addresstxt);
        durationtxt=(TextView)findViewById(R.id.durationtxt);
        disttxt=(TextView)findViewById(R.id.disttxt);
        chatbtn=(CircleButton)findViewById(R.id.chatbtn);
        warningbtn=(CircleButton)findViewById(R.id.warningbtn);

        currentTrip=MyApplication.getPickupTrip();
        currentTrip.d=MyApplication.getCurrentDriver();

        chatbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InTripActivity.this, TalkActivity.class));
            }
        });
        warningbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //testttttttttttttt warning
//                try {
//                    JSONObject o = new JSONObject();
//                    o.put("pid", 1);
//                    o.put("reason", "Unreasonable Movement");
//                    o.put("timestamp", System.currentTimeMillis());
//                    MyApplication.pubnub.publish(AppConstants.CHANNEL_WARNING, o, new Callback() {});
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                startActivity(new Intent(InTripActivity.this, FemaleActivity.class));

            }
        });

        //testttt
//        sendTestEnded();

//        /////recive trip start
//        IntentFilter filter = new IntentFilter(AppConstants.BROADCAST_TRIP_END_ACTION);
//        BroadcastReceiver receiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                startActivity(new Intent(InTripActivity.this, DoneTripActivity.class));
//                finish();
//            }
//        };
//        registerReceiver(receiver, filter);

        f = 0;

        //listen & get initial value
        MyApplication.myFirebaseRef.child(AppConstants.FIRE_TRIPS).child(String.valueOf(currentTrip.trip_ID)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String status = dataSnapshot.child("status").getValue(String.class);
                String comment = dataSnapshot.child("comment").getValue(String.class);
                if(status.equals("ended")&&(f==0)){//&&comment.equals("")) {
                    f=1;
                    MyApplication.setEndTripState();
                    stopService(new Intent(MyApplication.getAppContext(), FemaleService.class));//only start when start trip //start service
                    startActivity(new Intent(InTripActivity.this, DoneTripActivity.class));
                    finish();
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

//    void sendTestEnded(){
//
//        JSONObject jso = new JSONObject();
//        try {
//            jso.put("type", "tripended");
//            MyApplication.sendNotification(jso);
//
//        } catch (JSONException e) { e.printStackTrace(); }
//    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng ll = new LatLng(currentTrip.destination.getLatitude(),currentTrip.destination.getLongitude());
        mMap.addMarker(new MarkerOptions().position(ll).title("Destination"));

        //init vehicle marker :D
        Location l = new Location("");//MyApplication.getLastKnownLocation();
        l.setLatitude(0);
        l.setLongitude(0);
        drivermarker =  mMap.addMarker(new MarkerOptions()
                .title("My Location")
                .position(new LatLng(l.getLatitude(),l.getLongitude()))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.smallblueshark)));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(ll));//first only
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        getDirections(l.getLatitude(), l.getLongitude());

        //listen & get initial value
        MyApplication.myFirebaseRef.child(AppConstants.FIRE_VEHICLES).child(String.valueOf(currentTrip.d.vehicle.id)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                double lat = dataSnapshot.child("lat").getValue(Double.class);
                double lng = dataSnapshot.child("lng").getValue(Double.class);

                animateMarkerToGB(drivermarker,new LatLng(lat, lng));
                getDirections(lat, lng);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });


        //listen to my vehicles moves
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
//                                if (id == currentTrip.d.id) {//get location for my vehicle
//                                    final Double lat = obj.getDouble("lat");
//                                    final Double lng = obj.getDouble("lng");
//                                    final LatLng ll = new LatLng(lat, lng);
//
//                                    runOnUiThread(new Runnable() { // l runnable d 3shn err IllegalStateException 3shn d async
//                                        @Override
//                                        public void run() {
//                                            // Your code to run in GUI thread here
//                                            drivermarker.setPosition(ll);
//                                            getDirections(lat, lng);
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




    void getDirections(double newLat, double newLng){

        String url = "https://maps.googleapis.com/maps/api/directions/json"+
                "?origin="+newLat+","+newLng+
                "&destination="+currentTrip.destination.getLatitude()+","+currentTrip.destination.getLongitude()+
                "&sensor=false&mode=driving&alternatives=true"+"&key="+ AppConstants.MAP_API_KEY;

        StringRequest sr = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    final JSONObject json = new JSONObject(response);
                    JSONArray routeArray = json.getJSONArray("routes");
                    JSONObject routes = routeArray.getJSONObject(0);//awl route

                    JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
                    String encodedString = overviewPolylines.getString("points");//string kber by3ml route

                    List<LatLng> list = PolyUtil.decode(encodedString);//to convert that string to list of lat lng

                    Polyline line = mMap.addPolyline(new PolylineOptions()
                            .addAll(list)
                            .width(12)
                            .color(Color.parseColor("#05b1fb"))
                            .geodesic(true)
                    );

                    //get distance and duration
                    JSONObject legs = routes.getJSONArray("legs").getJSONObject(0);
                    String distance = legs.getJSONObject("distance").getString("text");
                    String duration = legs.getJSONObject("duration").getString("text");

                    String endAddress = legs.getString("end_address");
                    addresstxt.setText(endAddress);

                    disttxt.setText(distance);
                    durationtxt.setText(duration);



                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
//                params.put("Content-Type", "application/x-www-form-urlencoded");
//                params.put("latitude", String.valueOf(lat));
//                params.put("longitude", String.valueOf(lng));
                return params;
            }
        };
        // Add the request to the queue
        Volley.newRequestQueue(MyApplication.getAppContext()).add(sr);
    }







}
