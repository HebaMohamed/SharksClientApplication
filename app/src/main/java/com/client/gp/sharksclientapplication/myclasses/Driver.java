package com.client.gp.sharksclientapplication.myclasses;

import java.util.ArrayList;

/**
 * Created by dell on 2/7/2017.
 */
public class Driver {

    public int id;
    public String name;
    public String email;
    public String password;
    public String image;//byte array string

    public int avg;
    public String avgtxt;

    public Driver(int id) {
        this.id = id;
    }

    public Driver(int id, String name, String password, String image) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.image = image;
    }


    public Vehicle vehicle;
    public String vehicle_datetime;

    public ArrayList<Double> restrictedLats = new ArrayList<Double>();
    public ArrayList<Double> restrictedLngs = new ArrayList<Double>();

}