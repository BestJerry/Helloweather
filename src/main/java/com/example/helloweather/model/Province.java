package com.example.helloweather.model;

/**
 * Created by jerry on 16-8-28.
 */
public class Province  {
    private int id;
    private String provincename;
    private  String provincecode;

    public int getId(){
        return id;

    }

    public void setId(int id){
        this.id = id;
    }

    public String getprovincename(){
        return provincename;
    }

    public void setProvincename(String provincename){
        this.provincename = provincename;
    }

    public String getProvincecode(){
        return provincecode;
    }

    public void setProvincecode(String provincecode){
        this.provincecode = provincecode;
    }
}
