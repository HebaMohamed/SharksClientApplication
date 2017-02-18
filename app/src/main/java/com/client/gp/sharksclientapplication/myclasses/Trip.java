package com.client.gp.sharksclientapplication.myclasses;

import android.location.Location;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by dell on 2/7/2017.
 */
public class Trip {
    public int trip_ID;

    public Date start_Date;
    public Date end_Date;
    public double price;
    public String comment;
    public double rating;

    public Passenger p;
    public Driver d ;

    public Location pickup;
    public Location destination;
    public String details;
    public Long request_timestamp;//for request deletion


    public String staticmapurl;

    public Trip(int id)
    {
        trip_ID=id;
        pickup=new Location("");
        destination=new Location("");

    }
    public Trip(double lat, double lng, String details, int tripid, Long timestamp)// for trip state
    {
        pickup=new Location("");
        this.pickup.setLatitude(lat);
        this.pickup.setLongitude(lng);
        this.details=details;
        this.trip_ID=tripid;
        this.request_timestamp=timestamp/1000;
    }
    public Trip(int id,String s_date,String e_date,double price, String comment ,double rating)//for history
    {
        this.trip_ID=id;
        this.start_Date=todatetime(s_date);
        this.end_Date=todatetime(e_date);
        this.price=price;
        this.comment=comment;
        this.rating=rating;
    }

    public void setDestination(double lat, double lng) {
        destination = new Location("");
        destination.setLatitude(lat);
        destination.setLongitude(lng);
    }

    Date todatetime(String d){
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//2017-01-24 06:00:00.0
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(d);
            return date;
        } catch (ParseException ex) {
            Logger.getLogger(Trip.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public String getStartdate(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String s = formatter.format(start_Date);
        return s;
    }
    public String getEnddate(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String s = formatter.format(end_Date);
        return s;
    }

}