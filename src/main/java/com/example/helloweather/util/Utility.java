package com.example.helloweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.example.helloweather.db.HelloWeatherDB;
import com.example.helloweather.model.City;
import com.example.helloweather.model.County;
import com.example.helloweather.model.Province;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Locale;

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

    //解析服务器返回的JSON数据，并将解析出的数据存储到本地
    public static void handleweatherresponse(Context context, String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherinfo = jsonObject.getJSONObject("weatherinfo");
            String cityname = weatherinfo.getString("city");
            String weathercode = weatherinfo.getString("cityid");
            String temp1 = weatherinfo.getString("temp1");
            String temp2 = weatherinfo.getString("temp2");
            String weatherdesp = weatherinfo.getString("weather");
            String publishtime = weatherinfo.getString("ptime");
            saveweatherinfo(context, cityname, weathercode, temp1, temp2, weatherdesp, publishtime);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //将服务器返回的所有天气信息存储到sharedpreferences文件中

    public static void saveweatherinfo(Context context, String cityname, String weathercode, String temp1,
                                       String temp2, String weatherdesp, String publishtime) {
        SimpleDateFormat sdf = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        }
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityname);
        editor.putString("weather_code", weathercode);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weather_desp", weatherdesp);
        editor.putString("publish_time", publishtime);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            editor.putString("current_date", sdf.format(new Date()));
        }
        editor.commit();
    }

}
