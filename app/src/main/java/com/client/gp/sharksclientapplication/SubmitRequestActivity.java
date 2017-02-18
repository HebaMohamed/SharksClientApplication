package com.client.gp.sharksclientapplication;

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
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.client.gp.sharksclientapplication.myclasses.Trip;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
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

    Location pickup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_request);
//        getSupportActionBar().hide();
        setTitle("Submit Request");

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
                    double cost = 44.5;
                    costtxt.setText(cost+"$");

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
                    int tripid=1;
                    MyApplication.setSentState(pickup,tripid);
                    startActivity(new Intent(SubmitRequestActivity.this, WaitActivity.class));
                    finish();
                }
            });



        } catch (Exception e) {
            e.printStackTrace();
        }


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




}
