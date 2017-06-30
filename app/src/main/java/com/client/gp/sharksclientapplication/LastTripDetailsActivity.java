package com.client.gp.sharksclientapplication;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.client.gp.sharksclientapplication.myclasses.AppConstants;
import com.client.gp.sharksclientapplication.myclasses.Driver;
import com.client.gp.sharksclientapplication.myclasses.MyURL;
import com.client.gp.sharksclientapplication.myclasses.Passenger;
import com.client.gp.sharksclientapplication.myclasses.Trip;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LastTripDetailsActivity extends AppCompatActivity {

    public static int selectedTripID;
//    Trip t1;

    ImageView mapimg;
    TextView passengername, pickuploctxt, dropoffloctxt, starttxt, endtxt, distancetxt, durationtxt, costtxt, tripratetxt;

    String test;
    //    ArrayList<Double> pathwayLats = new ArrayList<>();t
//    ArrayList<Double> pathwayLngs = new ArrayList<>();//lmlk
    ProgressDialog progress;

    ArrayList<Location> pathwayLocs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_trip_details);
        setTitle("Last trip");
        mapimg = (ImageView) findViewById(R.id.mapimg);
        passengername = (TextView) findViewById(R.id.passengername);
        pickuploctxt = (TextView) findViewById(R.id.pickuploctxt);
        dropoffloctxt = (TextView) findViewById(R.id.dropoffloctxt);
        starttxt = (TextView) findViewById(R.id.starttxt);
        endtxt = (TextView) findViewById(R.id.endtxt);
        distancetxt = (TextView) findViewById(R.id.distancetxt);
        durationtxt = (TextView) findViewById(R.id.durationtxt);
        costtxt = (TextView) findViewById(R.id.costtxt);
        tripratetxt = (TextView) findViewById(R.id.tripratetxt);


//        pathwayLats.add(30.123177); //testttt get last trip by id
//        t1 = new Trip(selectedTripID);
//        t1.destination.setLatitude(30.123177);
//        t1.destination.setLongitude(31.009540);
//        t1.pickup.setLatitude(30.223177);
//        t1.pickup.setLongitude(31.209540);
//        t1.start_Date=new Date();
//        t1.end_Date=new Date();
//        t1.p=new Passenger(1);
//        t1.rating=5;
//        t1.price=112;
//        t1.p.fullName="bla name";
//
////        pathwayLngs.add(31.009540);
////        pathwayLats.add(30.133177);
////        pathwayLngs.add(31.019540);///////////////3dly l method
////        pathwayLats.add(30.143177);
////        pathwayLngs.add(31.029540);
//        Location l1 = new Location("");
//        l1.setLatitude(30.13244589999999);
//        l1.setLongitude(31.0177915);
//        Location l2 = new Location("");
//        l2.setLatitude(30.1328793);
//        l2.setLongitude(31.2034381);
//
//        pathwayLocs.add(l1);
//        pathwayLocs.add(l2);

        //end testtts

        setuploading();
        getlasttripdetails();
    }

    void setuploading(){
        progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setMessage("Loading. Please wait...");
        progress.setIndeterminate(true);
        progress.setCanceledOnTouchOutside(false);
    }


    void getlasttripdetails() {
        progress.show();
        StringRequest sr = new StringRequest(Request.Method.GET, MyURL.getlasttrip+"/"+selectedTripID, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    String msg = obj.getString("msg");
                    int success = Integer.parseInt(obj.getString("success"));

                    if (success == 1) {
                        JSONObject trip=obj.getJSONObject("trip");
                        JSONArray pathway=trip.getJSONArray("pathway");
//                        JSONObject pickupobj = pathway.getJSONObject(0);
//                        JSONObject destinationobj=pathway.getJSONObject(pathway.length()-1);

                        Trip t =new Trip(selectedTripID);
//                        t.start_Date= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(trip.getString("start"));
//                        t.end_Date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(trip.getString("end"));;
                        t.start_Date= new Date(trip.getLong("start"));//fr.parse(fr.format(new Date(object.getLong("start"))));//.parse bytl3 date lkn format bytl3 string
                        t.end_Date = new Date(trip.getLong("end"));//new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(object.getString("end"));;
                        t.price = new Double(trip.getDouble("price"));
                        t.comment=new String(trip.getString("comment"));
                        t.rating = new Double(trip.getDouble("ratting"));
                        t.d = new Driver(trip.getInt("driver_id"));
                        t.d.name = trip.getString("driver_name");
////                        t.d =new Driver(trip.getInt("driver_id"));
//                        t.destination.setLatitude(destinationobj.getDouble("lat"));
//                        t.destination.setLongitude(destinationobj.getDouble("lng"));
//                        t.pickup.setLatitude(pickupobj.getDouble("lat"));
//                        t.pickup.setLongitude(pickupobj.getDouble("lng"));

                        if(pathway.length()>0) {
                            JSONObject pickupobj = pathway.getJSONObject(0);
                            JSONObject destinationobj;
                            if(pathway.length()>1) {
                                destinationobj = pathway.getJSONObject(pathway.length() - 1);

                                //set map img
                                for (int j = 0; j<pathway.length(); j++){
                                    Location tl = new Location("");
                                    tl.setLatitude(pathway.getJSONObject(j).getDouble("lat"));
                                    tl.setLongitude(pathway.getJSONObject(j).getDouble("lng"));
                                    pathwayLocs.add(tl);
                                }
                                String locurl = getpathwaymap();
                                URL url = new URL(locurl);
                                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                mapimg.setImageBitmap(bmp);
                            }
                            else
                                destinationobj=pickupobj;


                            t.destination.setLatitude(destinationobj.getDouble("lat"));
                            t.destination.setLongitude(destinationobj.getDouble("lng"));
                            t.pickup.setLatitude(pickupobj.getDouble("lat"));
                            t.pickup.setLongitude(pickupobj.getDouble("lng"));
                            t.pickupAddress=MyApplication.getLocationAddress(t.destination);


                            //to display map on the same location
                            pathwayLocs.add(t.pickup);
                            String locurl = getpathwaymap();
                            URL url = new URL(locurl);
                            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                            mapimg.setImageBitmap(bmp);


                        }
                        else
                            t.pickupAddress="Not Moved";

                        passengername.setText(t.d.name);
                        pickuploctxt.setText(MyApplication.getLocationAddress(t.pickup));
                        dropoffloctxt.setText(MyApplication.getLocationAddress(t.destination));
                        starttxt.setText(t.getStartdate());
                        endtxt.setText(t.getEnddate());
//                        tripratetxt.setText(t.rating + " Stars");
                        costtxt.setText(t.price + " $");
                        distancetxt.setText((int)getDistance() + "Km");
                        durationtxt.setText(getDuration(t));

                        String scount = "";
                        for (int s=0; s<t.rating; s++)
                            scount+="★";
                        tripratetxt.setText(scount);


                    } else {
                        Toast.makeText(LastTripDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                    progress.hide();


                } catch (Exception e) {
                    e.printStackTrace();
                    progress.hide();
                    Toast.makeText(LastTripDetailsActivity.this, "Something went wrong! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progress.hide();
                Toast.makeText(LastTripDetailsActivity.this, "Something went wrong!" + error.getMessage(), Toast.LENGTH_SHORT).show();
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





    String getpathwaymap(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String staticmapurl = "https://maps.googleapis.com/maps/api/staticmap?";
        try {

            int centersize = pathwayLocs.size()/2;
            String latcenter = String.valueOf(pathwayLocs.get(centersize).getLatitude());
            String lngcenter = String.valueOf(pathwayLocs.get(centersize).getLongitude());

            staticmapurl += "center="+latcenter+","+lngcenter+"&size=200x110&path=color:0x0000ff|weight:5";//&zoom=14

            for (int i = 0; i < pathwayLocs.size(); i++) {
                String lat = String.valueOf(pathwayLocs.get(i).getLatitude());
                String lng = String.valueOf(pathwayLocs.get(i).getLongitude());
                staticmapurl+="|"+lat+","+lng;
            }
            staticmapurl+="&key="+ AppConstants.MAP_API_KEY;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return staticmapurl;
    }

    double getDistance(){
        double d = 0;
        for (int i = 0; i < pathwayLocs.size()-1; i++) {//-1 u know
            double dist = pathwayLocs.get(i).distanceTo(pathwayLocs.get(i+1));
            d+=dist;
        }
        return d/1000;//km
    }

    String getDuration(Trip t1){
        long diff =  t1.end_Date.getTime() - t1.start_Date.getTime();
        int numOfDays = (int) (diff / (1000 * 60 * 60 * 24));
        int hours = (int) (diff / (1000 * 60 * 60));
        int minutes = (int) (diff / (1000 * 60));
        int seconds = (int) (diff / (1000));
        String duration = hours+":"+minutes+":"+seconds;
        return duration;
    }
}

