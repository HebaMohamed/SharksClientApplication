package com.client.gp.sharksclientapplication;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.client.gp.sharksclientapplication.myclasses.AppConstants;
import com.client.gp.sharksclientapplication.myclasses.Driver;
import com.client.gp.sharksclientapplication.myclasses.Passenger;
import com.client.gp.sharksclientapplication.myclasses.Trip;
import com.client.gp.sharksclientapplication.myclasses.Vehicle;
import com.client.gp.sharksclientapplication.myservices.MyFirebaseInstanceIDService;
import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.iid.FirebaseInstanceId;


import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by dell on 2/17/2017.
 */

public class MyApplication  extends android.support.multidex.MultiDexApplication{
    private static final String TAG = "MyApplication";//android.app.Application {
    private static MyApplication mInstance;
    public static SharedPreferences prefs;
    private static Context mycontext;
//    public static Pubnub pubnub;


    private static String regId;
//    private static GoogleCloudMessaging gcm;

    public static Firebase myFirebaseRef;

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        mInstance = this;
        MyApplication.mycontext=getApplicationContext();//
        prefs = PreferenceManager.getDefaultSharedPreferences(this);//("mypref", Context.MODE_PRIVATE);

//        pubnub = new Pubnub( AppConstants.PUB_PUBLISH_KEY, AppConstants.PUB_SUBSCRIBE_KEY);

//        register();//for gcm services

        //for firebase
        Firebase.setAndroidContext(getApplicationContext());
        if(myFirebaseRef==null)
            myFirebaseRef = new Firebase(AppConstants.FIREBASE_DB);

        try {
            FirebaseApp.getInstance();
        } catch (IllegalStateException ex) {
            FirebaseApp.initializeApp(mycontext, FirebaseOptions.fromResource(mycontext));
        }

        String firebasetoken = FirebaseInstanceId.getInstance().getToken();
        int did = MyApplication.getLoggedPassengerID();
        if(did!=0)
            MyFirebaseInstanceIDService.sendRegistrationToServer(firebasetoken,did);
        if(firebasetoken!=null)
            Log.d("token: ",firebasetoken);
    }
    public static Context getAppContext() {
        return MyApplication.mycontext;
    }
    public static synchronized MyApplication getInstance() {
        return mInstance;
    }


    ///////////////////////////////////////////////////////////////////////////////login methods
//    private void register() {
//        if (checkPlayServices()) {
//            gcm = GoogleCloudMessaging.getInstance(this);
//            try {
//                regId = getRegistrationId(getApplicationContext());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            if (regId.isEmpty()) {
//                registerInBackground();
//            } else {
////                Toast.makeText(getAppContext(), "Registration ID already exists:" + regId, Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Log.e(TAG, "No valid Google Play Services APK found.");
//        }
//    }
//    private String getRegistrationId(Context context) throws Exception {
//        String registrationId = prefs.getString(AppConstants.PROPERTY_REG_ID, "");
//        if (registrationId.isEmpty()) {
//            return "";
//        }
//        return registrationId;
//    }
//    private void sendRegistrationId(String regId) {
//        pubnub.enablePushNotificationsOnChannel(
//                AppConstants.CHANNEL_NOTIFY,
//                regId);
//    }
//    private void registerInBackground() {
//        new AsyncTask() {
//            @Override
//            protected String doInBackground(Object[] params) {
//                String msg;
//                try {
//                    if (gcm == null) {
//                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
//                    }
//                    regId = gcm.register(AppConstants.SENDER_ID);
//                    msg = "Device registered, registration ID: " + regId;
//
//                    sendRegistrationId(regId);
//
//                    storeRegistrationId(getApplicationContext(), regId);
//                    Log.i(TAG, msg);
//                } catch (Exception ex) {
//                    msg = "Error :" + ex.getMessage();
//                    Log.e(TAG, msg);
//                }
//                return msg;
//            }
//        }.execute(null, null, null);
//    }
//    private void storeRegistrationId(Context context, String regId) throws Exception {
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putString(AppConstants.PROPERTY_REG_ID, regId);
//        editor.apply();
//    }
//    private boolean checkPlayServices() {
//        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
//        if (resultCode != ConnectionResult.SUCCESS) {
//            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
//                //GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
//            } else {
//                Log.e(TAG, "This device is not supported.");
//                //finish();
//            }
//            return false;
//        }
//        return true;
//    }

    ///////////////////////////////////////////////////////////////////////////////logout methods
//    private static void unregister() {
//        new AsyncTask() {
//            @Override
//            protected Object doInBackground(Object[] params) {
//                try {
//                    if (gcm == null) {
//                        gcm = GoogleCloudMessaging.getInstance(MyApplication.getAppContext());
//                    }
//                    // Unregister from GCM
//                    gcm.unregister();
//                    // Remove Registration ID from memory
//                    removeRegistrationId(MyApplication.getAppContext());
//                    // Disable Push Notification
//                    pubnub.disablePushNotificationsOnChannel(AppConstants.CHANNEL_NOTIFY, regId);
//                } catch (Exception e) { e.printStackTrace(); }
//                return null;
//            }
//        }.execute(null, null, null);
//    }
//    private static void removeRegistrationId(Context context) throws Exception {
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.remove(AppConstants.PROPERTY_REG_ID);
//        editor.apply();
//    }



//    public static void sendNotification(JSONObject jso) {
//        PnGcmMessage gcmMessage = new PnGcmMessage();
//        gcmMessage.setData(jso);
//        PnMessage message = new PnMessage(
//                pubnub,
//                AppConstants.CHANNEL_NOTIFY,
//                callback,
//                gcmMessage);
//        try {
//            message.publish();
//        } catch (PubnubException e) {
//            e.printStackTrace();
//        }
//    }

//    public static void sendNotificationToChannel(JSONObject jso, String channel) {
//        PnGcmMessage gcmMessage = new PnGcmMessage();
//        gcmMessage.setData(jso);
//        PnMessage message = new PnMessage(
//                pubnub,
//                channel,
//                callback,
//                gcmMessage);
//        try {
//            message.publish();
//        } catch (PubnubException e) {
//            e.printStackTrace();
//        }
//    }

//    public static Callback callback = new Callback() {
//        @Override
//        public void successCallback(String channel, Object message) {
//            Log.i(TAG, "Success on Channel " + AppConstants.CHANNEL_NOTIFY  + " : " + message);
//        }
//        @Override
//        public void errorCallback(String channel, PubnubError error) {
//            Log.i(TAG, "Error On Channel " + AppConstants.CHANNEL_NOTIFY + " : " + error);
//        }
//    };


    //////////////////////////////////////////////////////////////////////////////////loc
    public static String getLocationAddress(Location loc) {
        try{
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(mycontext, Locale.getDefault());
            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String approxAddress = addresses.get(0).getAddressLine(0);
            return approxAddress;
        } catch (Exception e) {
            e.printStackTrace();
            return"Unknown Address";
        }
    }

    /////////////////////////////////////////////////////////////////////////////////app state
    public static String getAppState(){
        String appstate = prefs.getString(AppConstants.PROPERTY_APP_STATE, "");
        return appstate;
    }

    public static void setSentState(Location pickup, int tripid, Driver d, long senttimestamp) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(AppConstants.PROPERTY_APP_STATE, "requestsent");
        editor.putInt("tripid", tripid);
        editor.putString("pickuplat", String.valueOf(pickup.getLatitude()));
        editor.putString("pickuplng", String.valueOf(pickup.getLongitude()));

        editor.putInt("did", d.id);
        editor.putString("dname", d.name);
        editor.putString("dimg", d.image);
        editor.putInt("vid", d.vehicle.id);
        editor.putString("vmodel", d.vehicle.model);
        editor.putString("vcolor", d.vehicle.color);
        editor.putString("vplatenumber", d.vehicle.plate_number);

        editor.putLong("senttimestamp", senttimestamp);


        editor.apply();
    }
    public static void setArrivingState() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(AppConstants.PROPERTY_APP_STATE, "arriving");
//        editor.putInt("did", d.id);
//        editor.putString("dname", d.name);
//        editor.putString("dimg", d.image);
//        editor.putInt("vid", d.vehicle.id);
//        editor.putString("vmodel", d.vehicle.model);
//        editor.putString("vcolor", d.vehicle.color);
//        editor.putString("vplatenumber", d.vehicle.plate_number);
        editor.apply();
    }
    public static void setInTripState(double destlat, double destlng) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(AppConstants.PROPERTY_APP_STATE, "intrip");
        editor.putString("destlat", String.valueOf(destlat));
        editor.putString("destlng", String.valueOf(destlng));
        editor.apply();
    }
    public static void setEndTripState() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(AppConstants.PROPERTY_APP_STATE, "tripended");
        editor.apply();
    }
    public static void setReadyState() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(AppConstants.PROPERTY_APP_STATE, "ready");
        editor.apply();
    }
/////////
    public static Driver getCurrentDriver(){
        int did = prefs.getInt("did", 0);
        String dname = prefs.getString("dname", "");
        String dimg = prefs.getString("dimg", "");
        int vid = prefs.getInt("vid", 0);
        String vmodel = prefs.getString("vmodel", "");
        String vcolor = prefs.getString("vcolor", "");
        String vplatenumber = prefs.getString("vplatenumber", "");

        Driver d = new Driver(did,dname,"",dimg);
        d.vehicle=new Vehicle(vid,vmodel,vcolor,vplatenumber);

        return d;
    }

    public static Trip getPickupTrip(){
        int tripid = prefs.getInt("tripid", 0);
        double pickuplat = Double.parseDouble(prefs.getString("pickuplat", "0"));
        double pickuplng = Double.parseDouble(prefs.getString("pickuplng", "0"));

        double destlat = Double.parseDouble(prefs.getString("destlat", "0"));
        double destlng = Double.parseDouble(prefs.getString("destlng", "0"));

        Long senttimestamp = prefs.getLong("senttimestamp",0);

        Location loc = new Location("");
        loc.setLatitude(pickuplat);
        loc.setLongitude(pickuplng);

        Location des = new Location("");
        des.setLatitude(destlat);
        des.setLongitude(destlng);

        Trip t = new Trip(tripid);
        t.request_timestamp=senttimestamp;
        t.pickup=loc;
        t.destination=des;
        return t;
    }


    ///////////////////////////////////////////////////////////////////////////////////

    public static void removeNotifications(int notfication_id){
        NotificationManager notifManager= (NotificationManager) MyApplication.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
//        notifManager.cancelAll();
        notifManager.cancel(notfication_id);
    }

    ///////////////////////////////////////////////////////////////////////////////////////get last location
    public static Location getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(mycontext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mycontext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return null;
        LocationManager mLocationManager;
        mLocationManager = (LocationManager)mycontext.getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////login register
    public static void storeLogin(Passenger p) throws Exception {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(AppConstants.PROPERTY_APP_STATE, "ready");
        editor.putInt("pid", p.id);
        editor.putString("pgender", p.gender);
        editor.putInt("page", p.age);
        editor.putString("pname", p.fullName);
        editor.putString("pphone", p.phone);
        editor.putString("prelativephone", p.relative_phone);
        editor.putString("planguage", p.language);
        editor.putString("pemail", p.email);

        editor.apply();
    }
    public static Passenger getLoggedPassenger() {
        int pid = prefs.getInt("pid", 0);
        Passenger p;
        if(pid != 0) {//3shn lw mfish wla passenger logged
            String pgender = prefs.getString("pgender", "");
            int page = prefs.getInt("page", 0);
            String pname = prefs.getString("pname", "");
            String pphone = prefs.getString("pphone", "0");
            String prelativephone = prefs.getString("prelativephone", "0");
            String planguage = prefs.getString("planguage", "");
            String pemail = prefs.getString("pemail", "");

            p = new Passenger(pid,"",pname,pgender,page,pphone,prelativephone,planguage,pemail);
        }
        else
            p=null;
        return p;
    }
    public static int getLoggedPassengerID() {
        int did = prefs.getInt("pid", 0);
        return did;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////logout methods
    public static void storelogout(){
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        //unregister();//3shn l notifications

    }





}

