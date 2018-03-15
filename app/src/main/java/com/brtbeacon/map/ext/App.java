package com.brtbeacon.map.ext;

import android.app.Application;

/**
 * 程序主Application
 * Created by Walkud on 2018/3/12 0012.
 */
public class App extends Application {

    private static App app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }

    public static App getInstance() {
        return app;
    }
}
