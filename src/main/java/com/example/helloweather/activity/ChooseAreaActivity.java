package com.example.helloweather.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.helloweather.R;
import com.example.helloweather.db.HelloWeatherDB;
import com.example.helloweather.model.City;
import com.example.helloweather.model.County;
import com.example.helloweather.model.Province;
import com.example.helloweather.util.HttpUtil;
import com.example.helloweather.util.Httpcallbacklistener;
import com.example.helloweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jerry on 16-8-29.
 */
public class ChooseAreaActivity extends AppCompatActivity {

    //是否从weatheractivity中跳转过来
    private boolean isfromweatheractivity;
    private static final String TAG = "ChooseAreaActivity";

    public static final int level_province = 0;
    public static final int level_city = 1;
    public static final int level_county = 2;

    private ProgressDialog progressDialog;
    private TextView titletext;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private HelloWeatherDB helloWeatherDB;
    private List<String> datalist = new ArrayList<String>();

    //省列表
    private List<Province> provinceList;
    //市列表
    private List<City> cityList;
    //县列表
    private List<County> countyList;
    //选中的省份
    private Province selectedprovince;
    //选中的城市
    private City selectedcity;
    //当前选中的级别
    private int currentlevel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        isfromweatheractivity = getIntent().getBooleanExtra("from_weather_activity", false);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //已经选择了城市且不是从weatheractivity跳转过来的，才会直接跳转到weatheractivity
        if (preferences.getBoolean("city_selected", false) && !isfromweatheractivity) {
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        listView = (ListView) findViewById(R.id.list_view);
        titletext = (TextView) findViewById(R.id.title_text);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datalist);
        listView.setAdapter(adapter);
        helloWeatherDB = HelloWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
                if (currentlevel == level_province) {
                    selectedprovince = provinceList.get(index);
                    querycities();
                } else if (currentlevel == level_city) {
                    selectedcity = cityList.get(index);
                    querycounties();
                } else if (currentlevel == level_county) {
                    String countycode = countyList.get(index).getCountycode();
                    Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
                    intent.putExtra("county_code", countycode);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryprovinces();
    }

    //查询全国所有省，优先从数据库查询，如果没有查询到再去服务器上查询
    private void queryprovinces()

    {
        provinceList = helloWeatherDB.loadprovinces();
        if (provinceList.size() > 0) {
            datalist.clear();
            for (Province province : provinceList) {
                datalist.add(province.getprovincename());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titletext.setText("中国");
            currentlevel = level_province;
        } else {
            queryfromserver(null, "province");
        }
    }

    //查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询
    private void querycities() {
        cityList = helloWeatherDB.loadcities(selectedprovince.getId());
        if (cityList.size() > 0) {
            datalist.clear();
            for (City city : cityList) {
                datalist.add(city.getCityname());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titletext.setText(selectedprovince.getprovincename());
            currentlevel = level_city;
        } else {
            queryfromserver(selectedprovince.getProvincecode(), "city");

        }

    }

    private void querycounties() {
        countyList = helloWeatherDB.loadcounties(selectedcity.getId());
        if (countyList.size() > 0) {
            datalist.clear();
            for (County county : countyList) {
                datalist.add(county.getCountyname());

            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titletext.setText(selectedcity.getCityname());
            currentlevel = level_county;
        } else {
            queryfromserver(selectedcity.getCitycode(), "county");
        }

    }

    //根据传入的代号和类型从服务器上查询省市县数据
    private void queryfromserver(final String code, final String type) {
        String address;
        if (!TextUtils.isEmpty(code)) {
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";

        } else {
            address = "http://www.weather.com.cn/data/list3/city.xml";

        }
        showprogressdialog();
        HttpUtil.sendhttprequest(address, new Httpcallbacklistener() {
            @Override
            public void onfinish(String response) {
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleprovincesresponse(helloWeatherDB, response);

                } else if ("city".equals(type)) {
                    result = Utility.handlecitiesresponse(helloWeatherDB, response, selectedprovince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handlecountiesresponse(helloWeatherDB, response, selectedcity.getId());

                }
                if (result) {
                    //通过runonuithread（）方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeprogressdialog();
                            if ("province".equals(type)) {
                                queryprovinces();
                            } else if ("city".equals(type)) {
                                querycities();
                            } else if ("county".equals(type)) {
                                querycounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onerror(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeprogressdialog();
                        Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    //显示进度对话框
    private void showprogressdialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }

    //关闭进度对话框
    private void closeprogressdialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    //捕获back键，根据当前的级别来判断，此时应该返回省列表，市列表，还是直接推出
    @Override
    public void onBackPressed() {
        if (currentlevel == level_province) {
            finish();
        } else if (currentlevel == level_city) {
            queryprovinces();
        } else {
            if (isfromweatheractivity) {
                Intent intent = new Intent(this, WeatherActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }
}