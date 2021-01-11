package com.zcolin.zrecyclerdemo;

import android.app.Application;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler((thread, ex) -> ex.printStackTrace());
    }
}