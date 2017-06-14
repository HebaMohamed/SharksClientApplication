package com.client.gp.sharksclientapplication;

import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.client.gp.sharksclientapplication.myclasses.AppConstants;
import com.client.gp.sharksclientapplication.myclasses.Driver;
import com.client.gp.sharksclientapplication.myclasses.LatLngInterpolator;
import com.client.gp.sharksclientapplication.myclasses.MyURL;
import com.client.gp.sharksclientapplication.myclasses.Vehicle;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecommendedMapActivity extends AppCompatActivity implements OnMapReadyCallback {


    private GoogleMap mMap;

    ArrayList<Marker> markers = new ArrayList<>();

    public static ArrayList<Driver> drivers = new ArrayList<>();

    Dialog infodialog;

    public static int requestedvid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommended_map);

        getSupportActionBar().hide();

        getneardrivers(PickupMapActivity.lat,PickupMapActivity.lng);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        for (int i = 0; i < drivers.size(); i++) {
            BitmapDescriptor icn;
            if(drivers.get(i).avgtxt.equals("Excellent"))
                icn = BitmapDescriptorFactory.fromResource(R.drawable.happy1pin);
            else if(drivers.get(i).equals("Very Good"))
                icn = BitmapDescriptorFactory.fromResource(R.drawable.happy2pin);
            else if(drivers.get(i).equals("Good"))
                icn = BitmapDescriptorFactory.fromResource(R.drawable.smile3pin);
            else
                icn = BitmapDescriptorFactory.fromResource(R.drawable.confused4pin);
            //add init marker
            markers.add(mMap.addMarker(new MarkerOptions()
                    .title("Shark Driver Location")
                    .snippet(String.valueOf(drivers.get(i).vehicle.id))
                    .position(new LatLng(0,0))
                    .icon(icn)));
        }




        init_infodialog();

        //listen & get initial value
        MyApplication.myFirebaseRef.child(AppConstants.FIRE_VEHICLES).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    try {
                        int vid = Integer.parseInt(postSnapshot.getKey());
                        double lat = postSnapshot.child("lat").getValue(Double.class);
                        double lng = postSnapshot.child("lng").getValue(Double.class);
                        LatLng ll;

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

//
//
                    } catch (NullPointerException e) {
                        e.printStackTrace();//3shn momkn fl test yb2a fy node msh feha lat msln
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });
    }

    TextView dnametxt,dvmodeltxt,ddisttxt;
    CircleImageView dimg;
    ImageView avgimg;
    Button requestbtn;
    void init_infodialog() {
        //info dialog
        infodialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        infodialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        infodialog.setCancelable(false);
        infodialog.setContentView(R.layout.sharklayout);
        dnametxt = (TextView) infodialog.findViewById(R.id.dnametxt);
        dvmodeltxt = (TextView) infodialog.findViewById(R.id.dvmodeltxt);
        ddisttxt = (TextView) infodialog.findViewById(R.id.ddisttxt);
        requestbtn = (Button) infodialog.findViewById(R.id.requestbtn);
        Button mcbtn = (Button) infodialog.findViewById(R.id.mcbtn);
        dimg = (CircleImageView) infodialog.findViewById(R.id.pmekimg);
//        imageView21 = (ImageView) infodialog.findViewById(R.id.imageView21);
        avgimg = (ImageView) infodialog.findViewById(R.id.avgimg);

        mcbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infodialog.hide();
            }
        });
        requestbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infodialog.hide();
//                startActivity(new Intent(MeksMapActivity.this, MekDetailsActivity.class));
                startActivity(new Intent(RecommendedMapActivity.this, SubmitRequestActivity.class));
            }
        });


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                int selectedv = Integer.parseInt(marker.getSnippet());
                fill_infodialog(selectedv);
                marker.hideInfoWindow();

                //return false;
                return true;
            }
        });
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                marker.hideInfoWindow();
            }
        });

    }

    void fill_infodialog(int selectedvid){
        for (int i = 0; i < drivers.size(); i++) {
            int vid = drivers.get(i).vehicle.id;
            requestedvid=vid;

            if(selectedvid == vid){

                dnametxt.setText("Driver Name : "+drivers.get(i).name);
                dvmodeltxt.setText("Vehicle Model : "+drivers.get(i).vehicle.model);
                ddisttxt.setText(String.valueOf(((Double)(drivers.get(i).vehicle.distance/1000))+"Km"));
//                String locurl = MyURL.uploadedimagesurl + meks.get(index).img;
//                if(!meks.get(index).img.equals(""))
//                    Picasso.with(this).load(locurl).into(pmekimg);
//                else
//                    pmekimg.setImageResource(R.drawable.mecuser);

                if(drivers.get(i).avgtxt.equals("Excellent"))
                    avgimg.setImageResource(R.drawable.happy1);
                else if(drivers.get(i).avgtxt.equals("Very Good"))
                    avgimg.setImageResource(R.drawable.happy2);
                else if(drivers.get(i).avgtxt.equals("Good"))
                    avgimg.setImageResource(R.drawable.smile3);
                else
                    avgimg.setImageResource(R.drawable.confused4);

                LatLng position = markers.get(i).getPosition();//3shn byt2ado m3 b3d
                mMap.animateCamera(CameraUpdateFactory.newLatLng(position));

                infodialog.show();
            }
        }
    }

    void getneardrivers(double ilat, double ilng){
//        progress.show();
        JSONObject toobj = new JSONObject();
        try {
            toobj.put("ilat",ilat);
            toobj.put("ilng",ilng);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = toobj.toString();

        StringRequest sr = new StringRequest(Request.Method.POST, MyURL.getneardrivers , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    String msg = obj.getString("msg");
                    int success = Integer.parseInt(obj.getString("success"));

//                    progress.hide();
                    if(success==1){
                        JSONArray neardrivers = obj.getJSONArray("neardrivers");
                        for (int i = 0; i < neardrivers.length() ; i++) {
                            JSONObject sob = neardrivers.getJSONObject(i);
                            int did = sob.getInt("did");
                            int avg = sob.getInt("avg");
                            int vid = sob.getInt("vid");
                            String model = sob.getString("model");
                            String avgtxt = sob.getString("avgtxt");
                            String fullname = sob.getString("fullname");
                            double dist = sob.getDouble("dist");

                            Driver d = new Driver(did,fullname,"","");
                            d.avg=avg;
                            d.avgtxt=avgtxt;
                            d.vehicle = new Vehicle(vid);
                            d.vehicle.distance=dist;
                            d.vehicle.model=model;

                            drivers.add(d);



                        }


                        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                        mapFragment.getMapAsync(RecommendedMapActivity.this);

//                        setadapter(drivers,avglv);
//                        startActivity(new Intent(Rem.this, WaitActivity.class));
                        //finish();
                    }
                    else {
                        Toast.makeText(RecommendedMapActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
//                    progress.hide();
                    Toast.makeText(RecommendedMapActivity.this, "Something went wrong! "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
//                progress.hide();
                Toast.makeText(RecommendedMapActivity.this, "Something went wrong!"+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            //            @Override
//            protected Map<String,String> getParams(){
//                Map<String,String> params = new HashMap<String, String>();
//                params.put("driver_id", String.valueOf(d.id));
//                params.put("password",d.password);
//                return params;
//            }
            @Override
            public String getBodyContentType() {
                return String.format("application/x-www-form-urlencoded; charset=utf-8");
            }
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            requestBody, "utf-8");
                    return null;
                }
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
//                params.put("Content-Type","application/x-www-form-urlencoded");
//                params.put("Content-Type","application/json");
                return params;
            }
        };

        //sr.setRetryPolicy(new DefaultRetryPolicy( 100000, 10, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        int MY_SOCKET_TIMEOUT_MS = 120000;
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add the request to the queue
        Volley.newRequestQueue(this).add(sr);

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
