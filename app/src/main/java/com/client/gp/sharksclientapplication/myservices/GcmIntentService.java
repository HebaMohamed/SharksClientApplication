package com.client.gp.sharksclientapplication.myservices;

import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.client.gp.sharksclientapplication.MainActivity;
import com.client.gp.sharksclientapplication.R;
import com.client.gp.sharksclientapplication.TalkActivity;
import com.client.gp.sharksclientapplication.myclasses.Driver;
import com.client.gp.sharksclientapplication.myclasses.Vehicle;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.client.gp.sharksclientapplication .MyApplication;
import com.client.gp.sharksclientapplication.myclasses.AppConstants;

import org.json.JSONException;

/**
 * Created by dell on 1/31/2017.
 */
public class GcmIntentService extends IntentService {


//    private static final int NOTIFICATION_ID = 1;////

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty() && GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
//            try {
//                String str = extras.getString("GCMSays");
//                sendNotification("Received: " + str);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
                String type = extras.getString("type"); //managerinstruction or triprequest
                if(type.equals("requestresponse")){
                    goRequestResponse(Integer.parseInt(extras.getString("tripid")),Integer.parseInt(extras.getString("driverid")));
                }
                else if(type.equals("tripstarted")){
                    goTripStarted(Double.parseDouble(extras.getString("destlat")),Double.parseDouble(extras.getString("destlng")));
                }
                else if(type.equals("tripended")){
                    goTripEnded();
                }
                else if(type.equals("passengertalk")){
                    goDriverrTalk(extras.getString("msg"), extras.getString("msgflag"));
                }


        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }


    public void goRequestResponse(int tripid, int driverid) {
        try {
            //testttt send request to get driver details associated with vehicle details
            Driver d = new Driver(1,"Ahmed","","");
            d.vehicle=new Vehicle(1,"hyundai verna","Red","123ABC");

            //save request for activity use
            MyApplication.setArrivingState(d);

            sendNotification("be ready! and tap for more details", "Trip Accepted !", new MainActivity(),1);

            //send broad cast to wait
            Intent intent = new Intent(AppConstants.BROADCAST_TRIP_ACCEPT_ACTION);
            sendBroadcast(intent);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void goTripStarted(double destlat, double destlng){
        MyApplication.setInTripState(destlat, destlng);
        //send broad cast to start
        Intent intent = new Intent(AppConstants.BROADCAST_TRIP_START_ACTION);
        sendBroadcast(intent);
    }

    void goTripEnded(){
        MyApplication.setEndTripState();
        //send broad cast to end
        Intent intent = new Intent(AppConstants.BROADCAST_TRIP_END_ACTION);
        sendBroadcast(intent);
    }

    public void goDriverrTalk(String msg, String msgFlag){ // sendBroadcastMessage
        try {
            Intent intent = new Intent(AppConstants.BROADCAST_MSG_ACTION);
            intent.putExtra("msg", msg);
            intent.putExtra("msgflag", msgFlag);
            sendBroadcast(intent);
            //send notification
            sendNotification(msg, "Driver Message!", new TalkActivity(),2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void sendNotification(String msg, String header, Activity activity, int notificationID) throws JSONException {

        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, activity.getClass()), 0);//TripRequestActivity.class

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.slogo)
                        .setContentTitle(header)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        mBuilder.setContentIntent(contentIntent);

        //added voice
//        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        mBuilder.setSound(alarmSound);
        //end added voice

        mNotificationManager.notify(notificationID, mBuilder.build());//NOTIFICATION_ID

    }

}