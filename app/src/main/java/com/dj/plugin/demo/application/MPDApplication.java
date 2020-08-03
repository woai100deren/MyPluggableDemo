package com.dj.plugin.demo.application;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.dj.plugin.plugincorelib.manager.PluginAMSHookManager;
import com.dj.plugin.plugincorelib.manager.PluginAssetsManager;
import com.dj.plugin.plugincorelib.manager.PluginManager;
import java.lang.reflect.InvocationTargetException;

public class MPDApplication extends Application {
    private Resources mResources;
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        try {
            PluginAssetsManager.extractAssets(base,"plugin1-debug.apk");
            mResources = PluginManager.hook(base,"plugin1-debug.apk");

//            Log.e("dj",">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+mResources.getResourceName(1896480796));
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | NoSuchFieldException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Resources getResources() {
        if(mResources == null) {
            return super.getResources();
        }else {
            return mResources;
        }
    }
}
