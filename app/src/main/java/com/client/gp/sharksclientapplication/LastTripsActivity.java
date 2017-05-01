package com.client.gp.sharksclientapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.client.gp.sharksclientapplication.myclasses.Driver;
import com.client.gp.sharksclientapplication.myclasses.MyURL;
import com.client.gp.sharksclientapplication.myclasses.Passenger;
import com.client.gp.sharksclientapplication.myclasses.Trip;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LastTripsActivity extends AppCompatActivity {

    ArrayList<Trip> trips = new ArrayList<>();
    ListView tripslv;
    ArrayAdapter adapter;

    int currentPassengerid;

    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_trips);
        setTitle("Last Trips");
//        Trip t1 = new Trip(1);
//        t1.destination.setLatitude(30.123177);
//        t1.destination.setLongitude(31.009540);
//        t1.start_Date=new Date();
//        t1.end_Date=new Date();
//        Trip t2 = new Trip(1);
//        t2.destination.setLatitude(31.123177);
//        t2.destination.setLongitude(31.009540);
//        t2.start_Date=new Date();
//        t2.end_Date=new Date();
//
//        trips.add(t1);
//        trips.add(t2);

        tripslv=(ListView)findViewById(R.id.tripslv);

//        setadapter(trips,tripslv);

        currentPassengerid = MyApplication.getLoggedPassengerID();
        setuploading();
        getlasttrips();

    }
    void setuploading(){
        progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setMessage("Loading. Please wait...");
        progress.setIndeterminate(true);
        progress.setCanceledOnTouchOutside(false);
    }


    //fill lv
    void setadapter(final ArrayList<Trip> a, ListView lv){
        lv.setAdapter(null);
        adapter =new ArrayAdapter(LastTripsActivity.this, R.layout.lasttriplayout, android.R.id.text1, a)
        {
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater) MyApplication.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.lasttriplayout, parent, false);

                TextView disttxt = (TextView) view.findViewById(R.id.disttxt);
                String addr = null;

                addr = a.get(position).pickupAddress; //MyApplication.getLocationAddress(a.get(position).destination);

                TextView starttxt = (TextView) view.findViewById(R.id.starttxt);
                disttxt.setText(addr);
                starttxt.setText(a.get(position).getStartdate());

                final int pos= position;

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LastTripDetailsActivity.selectedTripID = a.get(pos).trip_ID;
                        startActivity(new Intent(LastTripsActivity.this, LastTripDetailsActivity.class));
                    }
                });

                return view;
            }
        };
        lv.setAdapter(adapter);
    }
    void getlasttrips() {
        progress.show();
        StringRequest sr = new StringRequest(Request.Method.GET, MyURL.getlasttrips+"/"+currentPassengerid, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    String msg = obj.getString("msg");
                    int success = Integer.parseInt(obj.getString("success"));

                    if (success == 1) {

                        JSONArray lasttrips=obj.getJSONArray("trips");
                        for (int i=0;i<lasttrips.length();i++)
                        {
                            JSONObject object =lasttrips.getJSONObject(i);
                            JSONArray pathway=object.getJSONArray("pathway");

                            Trip t1 = new Trip(object.getInt("trip_id"));
                            SimpleDateFormat fr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                            t1.start_Date= new Date(object.getLong("start"));//fr.parse(fr.format(new Date(object.getLong("start"))));//.parse bytl3 date lkn format bytl3 string
                            t1.end_Date = new Date(object.getLong("end"));//new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(object.getString("end"));;
                            t1.price = new Double(object.getDouble("price "));
                            t1.comment = new String(object.getString("comment"));
                            t1.rating = new Double(object.getDouble("ratting"));
                            t1.d = new Driver(object.getInt("driver_id"));
                            if(pathway.length()>0) {
                                JSONObject pickupobj = pathway.getJSONObject(0);
                                JSONObject destinationobj;
                                if(pathway.length()>1)
                                    destinationobj = pathway.getJSONObject(pathway.length() - 1);
                                else
                                    destinationobj=pickupobj;


                                t1.destination.setLatitude(destinationobj.getDouble("lat"));
                                t1.destination.setLongitude(destinationobj.getDouble("lng"));
                                t1.pickup.setLatitude(pickupobj.getDouble("lat"));
                                t1.pickup.setLongitude(pickupobj.getDouble("lng"));
                                t1.pickupAddress=MyApplication.getLocationAddress(t1.destination);
                            }
                            else
                                t1.pickupAddress="Not Moved";



                            trips.add(t1);


                        }

                        setadapter(trips, tripslv);

                    } else {
                        Toast.makeText(LastTripsActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                    progress.hide();

                } catch (Exception e) {
                    e.printStackTrace();
                    progress.hide();
                    Toast.makeText(LastTripsActivity.this, "Something went wrong! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progress.hide();
                Toast.makeText(LastTripsActivity.this, "Something went wrong!" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return String.format("application/x-www-form-urlencoded; charset=utf-8");
            }



            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
//                params.put("Content-Type","application/x-www-form-urlencoded");
//                params.put("Content-Type","application/json");
                return params;
            }
        };

        sr.setRetryPolicy(new DefaultRetryPolicy(100000, 10, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add the request to the queue
        Volley.newRequestQueue(this).add(sr);

    }

}

