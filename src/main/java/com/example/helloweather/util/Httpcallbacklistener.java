package com.example.helloweather.util;

/**
 * Created by jerry on 16-8-29.
 */
public interface Httpcallbacklistener {

    void onfinish(String response);

    void onerror(Exception e);
}
