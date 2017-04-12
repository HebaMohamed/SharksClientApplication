package com.client.gp.sharksclientapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.client.gp.sharksclientapplication.myclasses.Driver;
import com.client.gp.sharksclientapplication.myclasses.MyURL;
import com.client.gp.sharksclientapplication.myclasses.Trip;
import com.client.gp.sharksclientapplication.myclasses.Vehicle;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.database.DatabaseError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SubmitRequestActivity extends AppCompatActivity {

    ImageView mapimg;
    TextView pickuploctxt, distancetxt, durationtxt, costtxt;
    PlaceAutocompleteFragment place_autocomplete_fragment;
    EditText adddetailstxt;
    Button submitbtn;

    Location selectedLoc;
    int distinM, durationinS;
    double costinM;

    Location pickup;

    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_request);
//        getSupportActionBar().hide();
        setTitle("Submit Request");
        setuploading();

        mapimg=(ImageView)findViewById(R.id.mapimg);
        pickuploctxt=(TextView) findViewById(R.id.pickuploctxt);
        distancetxt=(TextView) findViewById(R.id.distancetxt);
        durationtxt=(TextView) findViewById(R.id.durationtxt);
        costtxt=(TextView) findViewById(R.id.costtxt);
        place_autocomplete_fragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        adddetailstxt=(EditText) findViewById(R.id.adddetailstxt);
        submitbtn=(Button) findViewById(R.id.submitbtn);

        pickup=new Location("");
        pickup.setLatitude(PickupMapActivity.lat);
        pickup.setLongitude(PickupMapActivity.lng);


        try {
            selectedLoc = new Location("");
            selectedLoc.setLatitude(pickup.getLatitude());
            selectedLoc.setLongitude(pickup.getLongitude());

            getLocImg(selectedLoc);
            pickuploctxt.setText(PickupMapActivity.approxAddress);

            place_autocomplete_fragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(com.google.android.gms.location.places.Place place) {
                    Location destination = new Location("");
                    destination.setLatitude(place.getLatLng().latitude);
                    destination.setLongitude(place.getLatLng().longitude);
                    getDistanceDuration(selectedLoc, destination);
                    //testttt get estimated cost by distinM and durationinS
//                    double cost = 44.5;
//                    costtxt.setText(cost+"$");

                }
                @Override
                public void onError(Status status) { // Handle the error
                    Toast.makeText(SubmitRequestActivity.this, "Something went wrong! please try again ," + status, Toast.LENGTH_SHORT).show();
                }
            });


            submitbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //test send trip request get tripid and wait for notification to change state
//                    int tripid=1;
//                    MyApplication.setSentState(pickup,tripid);
//
//
//                    startActivity(new Intent(SubmitRequestActivity.this, WaitActivity.class));
//                    finish();
                    sentflag=0;
                    getTheNearest(selectedLoc);
//                    sendsubmit(selectedLoc, adddetailstxt.getText().toString());
                }
            });



        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    int sentflag = 0;//3shn msh m3 kl change yb3t tany

    int min_id;
    double min_distance;
    void getTheNearest(final Location lc){
        min_id = 0;
        min_distance = 0;
        progress.show();


        MyApplication.myFirebaseRef.child("vehicles").addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    Location loc1 = new Location("");
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        try{
                            int vid = Integer.parseInt(postSnapshot.getKey());
                            double lat = postSnapshot.child("lat").getValue(Double.class);
                            double lng = postSnapshot.child("lng").getValue(Double.class);
                            int status = postSnapshot.child("status").getValue(Integer.class);

                            if(status==0){
                                loc1.setLatitude(lat);
                                loc1.setLongitude(lng);

                                double dist = loc1.distanceTo(lc);
                                if(min_id==0){//first time only
                                    min_id = vid;
                                    min_distance = dist;
                                }else{
                                    if(min_distance>dist){
                                        min_id = vid;
                                        min_distance = dist;
                                    }
                                }
                            }
                        }catch(NullPointerException ne){
                            ne.printStackTrace();
                        }catch(NumberFormatException ne){
                            ne.printStackTrace();
                        }
                    }
                    ////////////////////////////////////////////////////////////////////////////////

                    if(sentflag==0) {
                        sendsubmit(selectedLoc, adddetailstxt.getText().toString(),min_id);
                        sentflag=1;
                    }


                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
            @Override
            public void onCancelled(FirebaseError databaseError) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

        });
    }


    void getLocImg(Location loc) throws IOException {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String locurl = "http://maps.googleapis.com/maps/api/staticmap?zoom=17&size=360x180&markers=size:mid|color:red|" + loc.getLatitude() + "," + loc.getLongitude() + "&sensor=false";
        URL url = new URL(locurl);
        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        mapimg.setImageBitmap(bmp);
    }

    void getDistanceDuration(Location initloc, Location destloc){
        String url = "http://maps.google.com/maps/api/directions/json?origin=" + initloc.getLatitude() + "," + initloc.getLongitude()
                + "&destination=" + destloc.getLatitude() + "," + destloc.getLongitude() + "&sensor=false&units=metric";
        StringRequest sr = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    final JSONObject json = new JSONObject(response);
                    JSONArray routeArray = json.getJSONArray("routes");
                    JSONObject routes = routeArray.getJSONObject(0);

                    JSONArray newTempARr = routes.getJSONArray("legs");
                    JSONObject newDisTimeOb = newTempARr.getJSONObject(0);

                    JSONObject distOb = newDisTimeOb.getJSONObject("distance");
                    JSONObject timeOb = newDisTimeOb.getJSONObject("duration");

                    distancetxt.setText(distOb.getString("text"));
                    durationtxt.setText(timeOb.getString("text"));

                    distinM=distOb.getInt("value");
                    durationinS=timeOb.getInt("value");

                    costinM=(4/1000)*distinM;
                    costtxt.setText(costinM+"$");


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
                return params;
            }
        };
        // Add the request to the queue
        Volley.newRequestQueue(MyApplication.getAppContext()).add(sr);
    }

    void setuploading(){
        progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setMessage("Loading. Please wait...");
        progress.setIndeterminate(true);
        progress.setCanceledOnTouchOutside(false);
    }

    void sendsubmit(Location lc, final String details, int minvid){
//        progress.show();
        JSONObject toobj = new JSONObject();
        try {
            toobj.put("lat",lc.getLatitude());
            toobj.put("lng",lc.getLongitude());
            toobj.put("details",details);
            toobj.put("passengerid",MyApplication.getLoggedPassengerID());
            toobj.put("vid",minvid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = toobj.toString();

        StringRequest sr = new StringRequest(Request.Method.POST, MyURL.submitpickup , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    String msg = obj.getString("msg");
                    int success = Integer.parseInt(obj.getString("success"));

                    progress.hide();
                    if(success==1){
//                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();

                        JSONObject driver = obj.getJSONObject("driver");
                        int driver_id = driver.getInt("driver_id");
                        String fullname = driver.getString("fullname");

                        JSONObject vehicle = obj.getJSONObject("vehicle");
                        int vehicle_id = vehicle.getInt("vehicle_id");
                        String model = vehicle.getString("model");
                        String color = vehicle.getString("color");
                        String plate_number = vehicle.getString("plate_number");

                        Driver d = new Driver(driver_id,fullname,"","");
                        d.vehicle=new Vehicle(vehicle_id,model,color,plate_number);

                        int tripid = obj.getInt("tripid");

                        long nwtimestamp = System.currentTimeMillis();

                        MyApplication.setSentState(pickup,tripid,d,nwtimestamp);
//                        sendDriverNotification(driver_id,tripid,details);
                        startActivity(new Intent(SubmitRequestActivity.this, WaitActivity.class));
                        //finish();
                    }
                    else {
                        Toast.makeText(SubmitRequestActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    progress.hide();
                    Toast.makeText(SubmitRequestActivity.this, "Something went wrong! "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progress.hide();
                Toast.makeText(SubmitRequestActivity.this, "Something went wrong!"+error.getMessage(), Toast.LENGTH_SHORT).show();
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

//    void sendDriverNotification(int driverid, int tripid, String details){
//        JSONObject jso = new JSONObject();
//        try {
//            jso.put("type", "triprequest");
//            jso.put("tripid", tripid);
//            jso.put("lat", selectedLoc.getLatitude());
//            jso.put("lng", selectedLoc.getLongitude());
//            jso.put("details",details);
//            jso.put("timestamp", System.currentTimeMillis());
//            jso.put("passengerid", MyApplication.getLoggedPassengerID());
//
//
//            MyApplication.sendNotificationToChannel(jso,"driver"+driverid);
//
//        } catch (JSONException e) { e.printStackTrace(); }
//    }

}
