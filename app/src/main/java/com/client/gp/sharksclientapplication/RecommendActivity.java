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

//    ArrayList<Driver> drivers = new ArrayList<>();
    ArrayAdapter adapter;
    ListView avglv;

//    static public int selectedvid;

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

//        getneardrivers(PickupMapActivity.lat,PickupMapActivity.lng);
        setadapter(RecommendedMapActivity.drivers,avglv);

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
                        RecommendedMapActivity.requestedvid = a.get(i).vehicle.id;
                        startActivity(new Intent(RecommendActivity.this, SubmitRequestActivity.class));
                    }
                });

                return view;
            }
        };
        lv.setAdapter(adapter);
    }



}
