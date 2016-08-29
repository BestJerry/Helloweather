package com.example.helloweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.helloweather.model.City;
import com.example.helloweather.model.County;
import com.example.helloweather.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jerry on 16-8-28.
 */
public class HelloWeatherDB {

    public static final String DB_NAME = "hello_weather";//数据库名

    public static final int VERSION = 1;//数据库版本

    private static HelloWeatherDB helloWeatherDB;
    private SQLiteDatabase database;

    //将构造方法私有化
    private HelloWeatherDB(Context context) {
        HelloWeatherOpenHelper dbhelper = new HelloWeatherOpenHelper(context, DB_NAME, null, VERSION);
        database = dbhelper.getWritableDatabase();

    }

    //获取helloweather的实例
    public synchronized static HelloWeatherDB getInstance(Context context) {
        if (helloWeatherDB == null) {
            helloWeatherDB = new HelloWeatherDB(context);

        }
        return helloWeatherDB;

    }

    //将province实例存储到数据库
    public void saveProvince(Province province) {
        if (province != null) {
            ContentValues values = new ContentValues();
            values.put("province_name", province.getprovincename());
            values.put("province_code", province.getProvincecode());
            database.insert("Province", null, values);
        }
    }

    //从数据库读取全国所有的省份信息
    public List<Province> loadprovinces() {
        List<Province> list = new ArrayList<Province>();
        Cursor cursor = database.query("Province", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvincename(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvincecode(cursor.getString(cursor.getColumnIndex("province_code")));
                list.add(province);
            } while (cursor.moveToNext());

        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    //将city实例存储到数据库
    public void savecity(City city) {
        if (city != null) {
            ContentValues values = new ContentValues();
            values.put("city_name", city.getCityname());
            values.put("city_code", city.getCitycode());
            values.put("province_id", city.getProvinceid());
            database.insert("City", null, values);

        }
    }

    //从数据库读取某省下所有的城市信息
    public List<City> loadcities(int provinceid) {
        List<City> list = new ArrayList<City>();
        Cursor cursor = database.query("City", null, "province_id = ?", new String[]{String.valueOf(provinceid)},
                null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityname(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCitycode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvinceid(provinceid);
                list.add(city);
            } while (cursor.moveToNext());

        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    //将county实例存储到数据库
    public void savecounty(County county) {
        if (county != null) {
            ContentValues values = new ContentValues();
            values.put("county_name", county.getCountyname());
            values.put("county_code", county.getCountycode());
            values.put("city_id", county.getCityid());
            database.insert("County", null, values);
        }
    }

    //从数据库读取某城市下所有的县信息
    public List<County> loadcounties(int cityid) {
        List<County> list = new ArrayList<County>();
        Cursor cursor = database.query("County", null, "city_id=?", new String[]{String.valueOf(cityid)}, null,
                null, null);
        if (cursor.moveToFirst()) {
            do {
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountycode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCountyname(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCityid(cityid);
                list.add(county);
            } while (cursor.moveToNext());

        }
        if (cursor != null) {
            cursor.close();
        }
        return list;

    }

}


