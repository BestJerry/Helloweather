package com.example.helloweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.helloweather.service.AutoUpdateService;

/**
 * Created by jerry on 16-8-31.
 */
public class AutoUpdateReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i =new Intent(context, AutoUpdateService.class);
        context.startActivity(i);
    }
}
