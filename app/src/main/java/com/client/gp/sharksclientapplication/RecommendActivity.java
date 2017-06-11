package com.client.gp.sharksclientapplication;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.client.gp.sharksclientapplication.myclasses.Vehicle;
import com.google.android.gms.drive.Drive;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecommendActivity extends AppCompatActivity {

    ArrayList<Driver> drivers = new ArrayList<>();
    ArrayAdapter adapter;
    ListView avglv;

    static public int selectedvid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        setTitle("Drivers Recommendation");

        avglv = (ListView)findViewById(R.id.avglv);

//        Driver d1 = new Driver(1);
//        d1.name = "d111111111111";
//        d1.avgtxt="Good";
//        Driver d2 = new Driver(2);
//        d2.name = "d222222222222";
//        d2.avgtxt="Bad";
//
//        drivers.add(d1);
//        drivers.add(d2);



//        setadapter(drivers,avglv);

        getneardrivers(PickupMapActivity.lat,PickupMapActivity.lng);

    }



    void setadapter(final ArrayList<Driver> a, ListView lv){
        lv.setAdapter(null);
        adapter =new ArrayAdapter(RecommendActivity.this, R.layout.recommended_layout, android.R.id.text1, a)
        {
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater) MyApplication.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.recommended_layout, parent, false);

                TextView dnametxt = (TextView) view.findViewById(R.id.dnametxt);
                TextView davgtxt = (TextView) view.findViewById(R.id.davgtxt);
                TextView ddisttxt = (TextView) view.findViewById(R.id.ddisttxt);
                ImageView davgpic = (ImageView) view.findViewById(R.id.davgpic);

                dnametxt.setText(a.get(position).name);
                davgtxt.setText("Driving Behavior : "+a.get(position).avgtxt);
                ddisttxt.setText(((int) a.get(position).vehicle.distance/1000)+"Km");

                if(a.get(position).avgtxt.equals("Excellent"))
                    davgpic.setImageDrawable(getResources().getDrawable(R.drawable.happy1));
                else if(a.get(position).avgtxt.equals("Very Good"))
                    davgpic.setImageDrawable(getResources().getDrawable(R.drawable.happy2));
                else if(a.get(position).avgtxt.equals("Good"))
                    davgpic.setImageDrawable(getResources().getDrawable(R.drawable.smile3));
                else if(a.get(position).avgtxt.equals("Bad"))
                    davgpic.setImageDrawable(getResources().getDrawable(R.drawable.confused4));
                final int i = position;

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectedvid = a.get(i).vehicle.id;
                        startActivity(new Intent(RecommendActivity.this, SubmitRequestActivity.class));
                    }
                });

                return view;
            }
        };
        lv.setAdapter(adapter);
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
                            String avgtxt = sob.getString("avgtxt");
                            String fullname = sob.getString("fullname");
                            double dist = sob.getDouble("dist");

                            Driver d = new Driver(did,fullname,"","");
                            d.avg=avg;
                            d.avgtxt=avgtxt;
                            d.vehicle = new Vehicle(vid);
                            d.vehicle.distance=dist;

                            drivers.add(d);

                        }

                        setadapter(drivers,avglv);
//                        startActivity(new Intent(Rem.this, WaitActivity.class));
                        //finish();
                    }
                    else {
                        Toast.makeText(RecommendActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
//                    progress.hide();
                    Toast.makeText(RecommendActivity.this, "Something went wrong! "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
//                progress.hide();
                Toast.makeText(RecommendActivity.this, "Something went wrong!"+error.getMessage(), Toast.LENGTH_SHORT).show();
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


}
