package com.dj.plugin.plugincorelib.manager;

import android.content.Context;
import android.content.res.Resources;

import java.lang.reflect.InvocationTargetException;

public class PluginManager {
    public static Resources hook(Context context, String apkName) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, NoSuchFieldException, InvocationTargetException {
        PluginResourceMergeManager.mergePluginResources(context,PluginAssetsManager.getFileStreamPathFile(context,apkName));
        PluginLoadedApkClassLoaderHookManager.hookLoadedApkInActivityThread(context,PluginAssetsManager.getFileStreamPathFile(context,apkName));
        PluginAMSHookManager.hookAMS(context);
        PluginAMSHookManager.hookActivityThread();

        //合并class类到宿主app中（主要用于热修复，插件化中一般用不上）
//        PluginMergeClassManager.mergeClass(context,apkName);

        return PluginResourceMergeManager.mResources;
    }
}
