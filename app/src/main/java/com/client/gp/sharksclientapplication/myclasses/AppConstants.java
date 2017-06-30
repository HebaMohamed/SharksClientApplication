package com.client.gp.sharksclientapplication.myclasses;

/**
 * Created by dell on 2/7/2017.
 */
public class AppConstants {

    public static final String PROPERTY_REG_ID = "regid"; //to save gcm regiistration id
    public static final String PROPERTY_APP_STATE = "appstate"; //to save appstate

    public static final String SENDER_ID = "385290206028";//gcm Project number

    public static final String PUB_PUBLISH_KEY = "pub-c-b04f5dff-3f09-4dc6-8b4e-58034b4b85bb";
    public static final String PUB_SUBSCRIBE_KEY = "sub-c-a92c9e70-e683-11e6-b3b8-0619f8945a4f";


    public static final String CHANNEL_NOTIFY = "passenger1";
    public static final String CHANNEL_PartnersLocation = "locschannel";//"LocsChannel";
    public static final String CHANNEL_WARNING = "warningchannel";

//    public static final String BROADCAST_COUNTER_ACTION = "com.sharks.gp.sharkspassengerapplication.updateprogress";
    public static final String BROADCAST_MSG_ACTION = "com.client.gp.sharksclientapplication.updatetalk";

    public static final String BROADCAST_TRIP_ACCEPT_ACTION = "com.client.gp.sharksclientapplication.tripaccepted";
    public static final String BROADCAST_TRIP_START_ACTION = "com.client.gp.sharksclientapplication.tripstarted";
    public static final String BROADCAST_TRIP_END_ACTION = "com.client.gp.sharksclientapplication.tripended";

    public static final String MAP_API_KEY = "AIzaSyBMkIegihYnGDWqYZukBz2eo_InQOh-XEI";

    public static final String FIREBASE_DB = "https://sharksmapandroid-158200.firebaseio.com/";
    public static final String FIRE_VEHICLES = "vehicles";
    public static final String FIRE_TRIPS= "trips";
    public static final String FIRE_PASSENGER= "passenger";




}
