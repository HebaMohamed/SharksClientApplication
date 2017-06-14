package com.client.gp.sharksclientapplication.myclasses;

/**
 * Created by dell on 2/7/2017.
 */
public class Passenger {

    public int id;
    public String password;
    public String fullName;
    public String gender;
    public int age;
    public int phone;
    public String relative_phone;
    public String language;
    public String email;

    public Passenger(int id){
        this.id=id;
    }
    public Passenger(int id,String pass,String name,String gender,int age,int phone,String R_phone,String language,String email)
    {
        this.id=id;
        this.password=pass;
        this.fullName=name;
        this.gender=gender;
        this.age=age;
        this.phone=phone;
        this.relative_phone=R_phone;
        this.language=language;
        this.email=email;


    }


}
