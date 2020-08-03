package com.dj.plugin.plugincorelib.manager;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 插件资源合并管理类
 */
public class PluginResourceMergeManager {
    public static Resources mResources;
    public static void mergePluginResources(Context context,File optDexFile) throws IllegalAccessException, NoSuchFieldException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        // 创建一个新的 AssetManager 对象
        AssetManager newAssetManagerObj = AssetManager.class.newInstance();
        Method addAssetPath = AssetManager.class.getMethod("addAssetPath", String.class);
        // 塞入原来宿主的资源
        addAssetPath.invoke(newAssetManagerObj, context.getPackageResourcePath());
        addAssetPath.invoke(newAssetManagerObj, optDexFile.getAbsolutePath());

        // ----------------------------------------------

        // 创建一个新的 Resources 对象
        Resources newResourcesObj = new Resources(newAssetManagerObj,
                context.getResources().getDisplayMetrics(),
                context.getResources().getConfiguration());


//        Log.e("dj",">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+newResourcesObj.getResourceName(1896480796));

        // ----------------------------------------------

        // 获取 ContextImpl 中的 Resources 类型的 mResources 变量，并替换它的值为新的 Resources 对象
        Field resourcesField = context.getClass().getDeclaredField("mResources");
        resourcesField.setAccessible(true);
        resourcesField.set(context, newResourcesObj);

        // ----------------------------------------------

        // 获取 ContextImpl 中的 LoadedApk 类型的 mPackageInfo 变量
        Field packageInfoField = context.getClass().getDeclaredField("mPackageInfo");
        packageInfoField.setAccessible(true);
        Object packageInfoObj = packageInfoField.get(context);

        // 获取 mPackageInfo 变量对象中类的 Resources 类型的 mResources 变量，，并替换它的值为新的 Resources 对象
        // 注意：这是最主要的需要替换的，如果不需要支持插件运行时更新，只留这一个就可以了
        Field resourcesField2 = packageInfoObj.getClass().getDeclaredField("mResources");
        resourcesField2.setAccessible(true);
        resourcesField2.set(packageInfoObj, newResourcesObj);

        // ----------------------------------------------

        // 获取 ContextImpl 中的 Resources.Theme 类型的 mTheme 变量，并至空它
        // 注意：清理mTheme对象，否则通过inflate方式加载资源会报错, 如果是activity动态加载插件，则需要把activity的mTheme对象也设置为null
        Field themeField = context.getClass().getDeclaredField("mTheme");
        themeField.setAccessible(true);
        themeField.set(context, null);

        mResources = newResourcesObj;
    }

//    public static Resources mergePluginResources(Context context,File optDexFile) throws IllegalAccessException, NoSuchFieldException, InstantiationException, NoSuchMethodException, InvocationTargetException {
//        Class<AssetManager> clazz = AssetManager.class;
//        AssetManager assetManager = clazz.newInstance();
//        Method method = clazz.getMethod("addAssetPath", String.class);
//        method.invoke(assetManager, optDexFile.getAbsolutePath());
//        Resources newResourcesObj = new Resources(assetManager, context.getResources().getDisplayMetrics(), context.getResources().getConfiguration());
//
//        return newResourcesObj;
//    }
}
