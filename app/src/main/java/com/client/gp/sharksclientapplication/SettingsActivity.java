package com.client.gp.sharksclientapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.client.gp.sharksclientapplication.myclasses.Passenger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import at.markushi.ui.CircleButton;
import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    CircleImageView circleImageView;
    EditText nametxt,emailtxt,passtxt,phtxt,relatedtxt;
    TextView fullnametxt;
    Button outbtn,savebtn;
    ProgressDialog progress;
    Passenger p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setTitle("Edit");
        setuploading();

        p = MyApplication.getLoggedPassenger();

        circleImageView = (CircleImageView)findViewById(R.id.circleImageView);
        fullnametxt = (TextView)findViewById(R.id.fullnametxt);
        nametxt = (EditText)findViewById(R.id.nametxt);
        emailtxt = (EditText)findViewById(R.id.emailtxt);
        passtxt = (EditText)findViewById(R.id.passtxt);
        phtxt = (EditText)findViewById(R.id.phtxt);
        relatedtxt = (EditText)findViewById(R.id.relatedtxt);
        outbtn = (Button)findViewById(R.id.outbtn);
        savebtn = (Button)findViewById(R.id.savebtn);

        fullnametxt.setText(p.fullName);
        nametxt.setText(p.fullName);
        emailtxt.setText(p.email);
        passtxt.setText("");
        phtxt.setText(p.phone);
        relatedtxt.setText(p.relative_phone);


        outbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.storelogout();
                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
//                System.exit(0);//close app
            }
        });


        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendedit(nametxt.getText().toString(),emailtxt.getText().toString(),passtxt.getText().toString(),phtxt.getText().toString(),relatedtxt.getText().toString());
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

    void sendedit(final String name, final String email, final String pass, final String ph, final String relativeph){
        JSONObject toobj = new JSONObject();
        try {
            toobj.put("pid",p.id);
            toobj.put("name",name);
            toobj.put("email",email);
            toobj.put("pass",pass);
            toobj.put("ph",ph);
            toobj.put("relativeph",relativeph);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = toobj.toString();

        progress.show();
        StringRequest sr = new StringRequest(Request.Method.POST, MyURL.editpassenger , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    String msg = obj.getString("msg");
                    int success = Integer.parseInt(obj.getString("success"));

                    progress.hide();
                    if(success==1){
                        Toast.makeText(SettingsActivity.this, msg, Toast.LENGTH_LONG).show();

                        p.fullName=name;
                        p.email=email;
                        p.password=pass;
                        p.phone=ph;
                        p.relative_phone=relativeph;

                        MyApplication.storeLogin(p);
                        finish();
                    }
                    else {
                        Toast.makeText(SettingsActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    progress.hide();
                    Toast.makeText(SettingsActivity.this, "Something went wrong! "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progress.hide();
                Toast.makeText(SettingsActivity.this, "Something went wrong!"+error.getMessage(), Toast.LENGTH_SHORT).show();
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
                return String.format("application/json; charset=utf-8");
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
