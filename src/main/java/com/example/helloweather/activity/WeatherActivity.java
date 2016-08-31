package com.example.helloweather.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.helloweather.R;
import com.example.helloweather.service.AutoUpdateService;
import com.example.helloweather.util.HttpUtil;
import com.example.helloweather.util.Httpcallbacklistener;
import com.example.helloweather.util.Utility;

/**
 * Created by jerry on 16-8-30.
 */
public class WeatherActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout weatherinfolayout;

    //用于显示城市名
    private TextView citynametext;
    //用于显示发布时间
    private TextView publishtext;
    //用于显示天气描述信息
    private TextView weatherdesptext;
    //用于显示气温1
    private TextView temp1text;
    //用于显示气温2
    private TextView temp2text;
    //用于显示当前日期
    private TextView currentdatetext;

    //切换城市按钮
    private Button switchcity;
    //更新天气按钮
    private Button refreshweather;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
        //初始化各控件
        weatherinfolayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        citynametext = (TextView) findViewById(R.id.city_name);
        publishtext = (TextView) findViewById(R.id.publish_text);
        weatherdesptext = (TextView) findViewById(R.id.weather_desp);
        temp1text = (TextView) findViewById(R.id.temp1);
        temp2text = (TextView) findViewById(R.id.temp2);
        currentdatetext = (TextView) findViewById(R.id.current_date);
        String countycode = getIntent().getStringExtra("county_code");
        switchcity = (Button) findViewById(R.id.switch_city);
        refreshweather = (Button) findViewById(R.id.refresh_weather);
        switchcity.setOnClickListener(this);
        refreshweather.setOnClickListener(this);
        if (!TextUtils.isEmpty(countycode)) {
            //有县级代号时就去查询天气
            publishtext.setText("同步中...");
            weatherinfolayout.setVisibility(View.INVISIBLE);
            citynametext.setVisibility(View.INVISIBLE);
            queryweathercode(countycode);

        } else {
            //没有县级代号时就直接显示本地天气
            showweather();

        }
    }

    //查询县级代号所对应的天气代号
    private void queryweathercode(String countycode) {
        String address = "http://www.weather.com.cn/data/list3/city" + countycode + ".xml";
        queryfromserver(address, "countycode");

    }

    //查询天气代号所对应的天气
    private void queryweatherinfo(String weathercode) {
        String address = "http://www.weather.com.cn/data/cityinfo/" + weathercode + ".html";
        queryfromserver(address, "weathercode");

    }

    //根据传入的地址和类型去向服务器查询天气代号或者天气信息
    private void queryfromserver(final String address, final String type) {
        HttpUtil.sendhttprequest(address, new Httpcallbacklistener() {
            @Override
            public void onfinish(String response) {
                if ("countycode".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
                        //从服务器返回的数据解析出天气代号
                        String[] array = response.split("\\|");
                        if (array != null && array.length == 2) {
                            String weathercode = array[1];
                            queryweatherinfo(weathercode);
                        }

                    }
                } else if ("weathercode".equals(type)) {
                    //处理服务器返回的天气信息
                    Utility.handleweatherresponse(WeatherActivity.this, response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showweather();
                        }
                    });
                }
            }

            @Override
            public void onerror(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishtext.setText("同步失败");
                    }
                });
            }
        });
    }

    //从sharedpreferences文件中读取存储的天气信息，并显示到界面上
    private void showweather() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        citynametext.setText(preferences.getString("city_name", ""));
        temp1text.setText(preferences.getString("temp1", ""));
        temp2text.setText(preferences.getString("temp2", ""));
        weatherdesptext.setText(preferences.getString("weather_desp", ""));
        publishtext.setText("今天" + preferences.getString("publish_time", "") + "发布");
        currentdatetext.setText(preferences.getString("current_date", ""));
        weatherinfolayout.setVisibility(View.VISIBLE);
        citynametext.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.switch_city:
                Intent intent = new Intent(this, ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_weather:
                publishtext.setText("同步中...");
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                String weathercode = preferences.getString("weather_code", "");
                if (!TextUtils.isEmpty(weathercode)) {
                    queryweatherinfo(weathercode);
                }
                break;
            default:
                break;

        }
    }
}
