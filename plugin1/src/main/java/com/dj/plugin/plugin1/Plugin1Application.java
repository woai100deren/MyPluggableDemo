package com.dj.plugin.plugin1;

import android.app.Application;
import android.util.Log;

public class Plugin1Application extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("dj",">>>>>>>>>>>>>>>>>>>>>>>>>>>>Plugin1Application");
    }
}
