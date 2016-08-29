package com.example.helloweather.util;

import android.text.TextUtils;

import com.example.helloweather.db.HelloWeatherDB;
import com.example.helloweather.model.City;
import com.example.helloweather.model.County;
import com.example.helloweather.model.Province;

/**
 * Created by jerry on 16-8-29.
 */
public class Utility {
    //解析和处理服务器返回的升级数据
    public synchronized static boolean handleprovincesresponse(HelloWeatherDB helloWeatherDB, String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] allprovinces = response.split(",");
            if (allprovinces != null && allprovinces.length > 0) {
                for (String p : allprovinces) {
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvincecode(array[0]);
                    province.setProvincename(array[1]);
                    //将解析出来的数据存储到Province表
                    helloWeatherDB.saveProvince(province);
                }
                return true;
            }

        }
        return false;
    }

    //解析和处理服务器返回的市级数据
    public static boolean handlecitiesresponse(HelloWeatherDB helloWeatherDB, String response, int provinceid) {
        if (!TextUtils.isEmpty(response)) {
            String[] allcities = response.split(",");
            if (allcities != null && allcities.length > 0) {
                for (String c : allcities) {
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCitycode(array[0]);
                    city.setCityname(array[1]);
                    city.setProvinceid(provinceid);
                    //将解析出来的数据存储到city表
                    helloWeatherDB.savecity(city);
                }
                return true;
            }
        }
        return false;
    }

    //解析和处理服务器返回的县级数据
    public static boolean handlecountiesresponse(HelloWeatherDB helloWeatherDB, String response, int cityid) {
        if (!TextUtils.isEmpty(response)) {
            String[] allcounties = response.split(",");
            if (allcounties != null && allcounties.length > 0) {
                for (String c : allcounties) {
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountycode(array[0]);
                    county.setCountyname(array[1]);
                    county.setCityid(cityid);
                    //将解析出来的数据存储到county类
                    helloWeatherDB.savecounty(county);

                }
                return true;
            }
        }
        return false;
    }

}
