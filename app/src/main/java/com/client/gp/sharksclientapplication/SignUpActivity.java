package com.client.gp.sharksclientapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
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
import com.client.gp.sharksclientapplication.myclasses.Passenger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    EditText usernametxt,passtxt,passconftxt,emailtxt,phonetxt,reltedphonetxt,agetxt;
    RadioButton malebtn,femalebtn;
    Button signupbtn;
    Spinner languagesspinner;

    Passenger p;

    ArrayList<String> languages = new ArrayList<>();
    ArrayList<String> languagesCodes = new ArrayList<>();
    int selectedIndex;

    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setTitle("SignUp");

        usernametxt=(EditText)findViewById(R.id.usernametxt);
        passtxt=(EditText)findViewById(R.id.passtxt);
        passconftxt=(EditText)findViewById(R.id.passconftxt);
        emailtxt=(EditText)findViewById(R.id.emailtxt);
        phonetxt=(EditText)findViewById(R.id.phonetxt);
        reltedphonetxt=(EditText)findViewById(R.id.reltedphonetxt);
        agetxt=(EditText)findViewById(R.id.agetxt);
        malebtn=(RadioButton) findViewById(R.id.malebtn);
        femalebtn=(RadioButton) findViewById(R.id.femalebtn);
        signupbtn=(Button)findViewById(R.id.signupbtn);
        languagesspinner = (Spinner)findViewById(R.id.languagesspinner);


        setlanguages();

        setuploading();


        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(passtxt.getText().toString().equals(passconftxt.getText().toString()))){
                    Toast.makeText(SignUpActivity.this, "Please Check Password Confirmation", Toast.LENGTH_SHORT).show();
                }
                else{
                    String gender = "";
                    if(femalebtn.isChecked())
                        gender = "Female";
                    else if(femalebtn.isChecked())
                        gender = "Male";

                    String pass = passtxt.getText().toString();
                    String name = usernametxt.getText().toString();
                    String email = emailtxt.getText().toString();
                    int phone = Integer.parseInt(phonetxt.getText().toString());
                    int relatedphone = Integer.parseInt(reltedphonetxt.getText().toString());
                    int age = Integer.parseInt(agetxt.getText().toString());

                    String languagecode = languagesCodes.get(selectedIndex);

                    p = new Passenger(0,pass,name,gender,age,phone,relatedphone,languagecode,email);

                    sendsignup();
                }
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

    private void setlanguages() {
        //get languages
        for (Locale locale : Locale.getAvailableLocales()) {
            languages.add(locale.getDisplayName());
            languagesCodes.add(locale.getLanguage());
            if(locale.getDisplayName().equals("English")){//take as default
                selectedIndex=languages.size()-1;
            }
        }


        //spinning part
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, languages);
        languagesspinner.setAdapter(adapter);

        languagesspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position!=0)
                    selectedIndex=position;//3shn l default
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        languagesspinner.setSelection(selectedIndex);//as default
    }

    void sendsignup(){
        JSONObject toobj = new JSONObject();
        try {
            toobj.put("name",p.fullName);
            toobj.put("email",p.email);
            toobj.put("password",p.password);
            toobj.put("phone",p.phone);
            toobj.put("relatedphone",p.relative_phones);
            toobj.put("gender",p.gender);
            toobj.put("age",p.age);
            toobj.put("language",p.language);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = toobj.toString();

        progress.show();
        StringRequest sr = new StringRequest(Request.Method.POST, MyURL.login , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    String msg = obj.getString("msg");
                    int success = Integer.parseInt(obj.getString("success"));

                    progress.hide();
                    if(success==1){

                        int insertedid =obj.getInt("insertedid");
                        p.id=insertedid;

                        Toast.makeText(SignUpActivity.this, "Welcome "+p.fullName, Toast.LENGTH_LONG).show();

                        MyApplication.storeLogin(p);
                        startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                        finish();
                    }
                    else {
                        Toast.makeText(SignUpActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    progress.hide();
                    Toast.makeText(SignUpActivity.this, "Something went wrong! "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progress.hide();
                Toast.makeText(SignUpActivity.this, "Something went wrong!"+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
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
