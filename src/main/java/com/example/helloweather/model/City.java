package com.example.helloweather.model;

/**
 * Created by jerry on 16-8-28.
 */
public class City {

    private int id;
    private String cityname;
    private String citycode;
    private int provinceid;

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;

    }

    public String getCityname(){
        return cityname;

    }
    public void setCityname(String cityname){
        this.cityname = cityname;
    }

    public String getCitycode(){
        return  citycode;
    }
    public void setCitycode(String citycode){
        this.citycode = citycode;
    }

    public int getProvinceid(){
        return provinceid;
    }

    public void setProvinceid(int provinceid){
        this.provinceid = provinceid;
    }
}
