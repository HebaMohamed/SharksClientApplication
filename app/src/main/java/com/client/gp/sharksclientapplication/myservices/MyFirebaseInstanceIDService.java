package com.client.gp.sharksclientapplication.myservices;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.client.gp.sharksclientapplication.myclasses.AppConstants;
import com.client.gp.sharksclientapplication.myclasses.MyURL;
import com.firebase.client.Firebase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dell on 4/7/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    private Firebase myFirebaseRef;

    @Override
    public void onTokenRefresh() {

        if(myFirebaseRef==null)
            myFirebaseRef = new Firebase(AppConstants.FIREBASE_DB);

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.d(TAG, "Refreshed token: " + refreshedToken);
//        sendRegistrationToServer(refreshedToken);
    }
//
//    public static void sendRegistrationToServer(final String token) {
//
//        StringRequest sr = new StringRequest(Request.Method.POST, MyURL.refreshtoken , new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    JSONObject obj = new JSONObject(response);
//                    String msg = obj.getString("message");
//                    int success = Integer.parseInt(obj.getString("success"));
//
//                    Log.d("MyFirebaseInstanceIDS ",msg);
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    Log.d("SaveTokenResponse ", "onResponse: "+response);
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//            }
//        }){
//            @Override
//            protected Map<String,String> getParams(){
//                Map<String,String> params = new HashMap<String, String>();
////                params.put("id",MyApplication.getuserid());
//                params.put("token",token);
//                params.put("user","Client");
//                return params;
//            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String,String> params = new HashMap<String, String>();
//                params.put("Content-Type","application/x-www-form-urlencoded");
//                return params;
//            }
//        };
//
//
//        // Add the request to the queue
//        Volley.newRequestQueue(MyApplication.getAppContext()).add(sr);
//
//
//    }
}