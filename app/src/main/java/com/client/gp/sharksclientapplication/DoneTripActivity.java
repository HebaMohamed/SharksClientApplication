package com.client.gp.sharksclientapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
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
import com.client.gp.sharksclientapplication.myclasses.MyURL;
import com.client.gp.sharksclientapplication.myclasses.Trip;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class DoneTripActivity extends AppCompatActivity {

    TextView costtxt, stxt, dtxt;
    RatingBar ratingBar;
    EditText commentxt;
    Button submitbtn;
    ProgressDialog progress;

    TextView dnametxt;

    Trip currentTrip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done_trip);
        setTitle("Trip Feedback");

        dnametxt = (TextView)findViewById(R.id.dnametxt);
//        costtxt=(TextView)findViewById(R.id.costtxt);
//        stxt=(TextView)findViewById(R.id.stxt);
//        dtxt=(TextView)findViewById(R.id.dtxt);
        ratingBar=(RatingBar) findViewById(R.id.ratingBar);
        commentxt=(EditText) findViewById(R.id.commentxt);
        submitbtn=(Button)findViewById(R.id.submitbtn);
        setuploading();
        currentTrip=MyApplication.getPickupTrip();
        currentTrip.d=MyApplication.getCurrentDriver();

        MyApplication.setReadyState();

        dnametxt.setText("To Driver : "+currentTrip.d.name);

        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendfeedback(currentTrip.trip_ID,ratingBar.getNumStars(),commentxt.getText().toString());
            }
        });

    }
    void setuploading(){
        progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setMessage("Loading. Please wait...");
        progress.setIndeterminate(true);
        progress.setCanceledOnTouchOutside(false);
    }

    void sendfeedback(int tripid, int ratting, String comment){
        JSONObject toobj = new JSONObject();
        try {
            toobj.put("trip_id",tripid);
            toobj.put("ratting",ratting);
            toobj.put("comment",comment);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = toobj.toString();

        progress.show();
        StringRequest sr = new StringRequest(Request.Method.POST, MyURL.sendfeedback , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    String msg = obj.getString("msg");
                    int success = Integer.parseInt(obj.getString("success"));

                    progress.hide();
                    if(success==1){


                        Toast.makeText(DoneTripActivity.this, "Your Feedback is sent successfully!", Toast.LENGTH_LONG).show();

                        startActivity(new Intent(DoneTripActivity.this, HomeActivity.class));
                        finish();
                    }
                    else {
                        Toast.makeText(DoneTripActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    progress.hide();
                    Toast.makeText(DoneTripActivity.this, "Something went wrong! "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progress.hide();
                Toast.makeText(DoneTripActivity.this, "Something went wrong!"+error.getMessage(), Toast.LENGTH_SHORT).show();
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

        sr.setRetryPolicy(new DefaultRetryPolicy( 100000, 10, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add the request to the queue
        Volley.newRequestQueue(this).add(sr);

    }
}
