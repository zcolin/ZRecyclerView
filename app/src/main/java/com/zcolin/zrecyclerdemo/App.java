/*
 * *********************************************************
 *   author   colin
 *   company  telchina
 *   email    wanglin2046@126.com
 *   date     18-1-9 下午2:46
 * ********************************************************
 */

package com.zcolin.zrecyclerdemo;

import android.app.Application;

public class App extends Application {

    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler((thread, ex) -> ex.printStackTrace());
    }
}