package com.example.helloweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.example.helloweather.receiver.AutoUpdateReceiver;
import com.example.helloweather.util.HttpUtil;
import com.example.helloweather.util.Httpcallbacklistener;
import com.example.helloweather.util.Utility;

/**
 * Created by jerry on 16-8-31.
 */
public class AutoUpdateService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateweather();
            }
        }).start();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anhour = 8 * 60 * 60 * 1000;//这是8小时的毫秒数
        long triggerattime = SystemClock.elapsedRealtime() + anhour;
        Intent i = new Intent(this, AutoUpdateReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerattime, pi);
        return super.onStartCommand(intent, flags, startId);

    }

    //更新天气信息
    private void updateweather() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weathercode = preferences.getString("weather_code", "");
        String address = "http//:www.weather.com.cn/data/cityinfo/" + weathercode + ".html";
        HttpUtil.sendhttprequest(address, new Httpcallbacklistener() {
            @Override
            public void onfinish(String response) {
                Utility.handleweatherresponse(AutoUpdateService.this, response);
            }

            @Override
            public void onerror(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
