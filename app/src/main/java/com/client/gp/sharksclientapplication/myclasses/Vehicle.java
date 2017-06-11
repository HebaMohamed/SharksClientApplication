package com.client.gp.sharksclientapplication.myclasses;

/**
 * Created by dell on 2/7/2017.
 */
public class Vehicle {
    public int id;
    public String model;
    public String color;
    public String plate_number;

    public double distance;

    public Vehicle(int id)
    {
        this.id=id;
    }

    public Vehicle(int id, String model, String color, String plate_number)
    {
        this.id=id;
        this.model=model;
        this.color=color;
        this.plate_number=plate_number;

    }
}
