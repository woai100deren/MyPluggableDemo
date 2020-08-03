package com.dj.plugin.plugincorelib.manager;

import android.content.Context;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * 合并class类到宿主app中（主要用于热修复，插件化中一般用不上）
 */
public class PluginMergeClassManager {
    /**
     * 从插件apk文件中，得到dexElements，将dexElements合并到宿主apk中
     * @param context 上下文
     * @param classFileName 要合并的apk文件或者dex文件
     */
    public static void mergeClass(Context context, String classFileName) {
        try {
            //private final DexPathList pathList;
            Class<?> baseDexClassLoaderClass = Class.forName("dalvik.system.BaseDexClassLoader");
            Field pathListField = baseDexClassLoaderClass.getDeclaredField("pathList");
            pathListField.setAccessible(true);
            //private Element[] dexElements;
            Class<?> dexPathListClass = Class.forName("dalvik.system.DexPathList");
            Field dexElementsField = dexPathListClass.getDeclaredField("dexElements");
            dexElementsField.setAccessible(true);

            PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();
            Object hostPathList = pathListField.get(pathClassLoader);
            Object[] hostDexElements = (Object[]) dexElementsField.get(hostPathList);

            DexClassLoader dexClassLoader = new DexClassLoader(
                    PluginAssetsManager.getFileStreamPath(context,classFileName),
                    context.getCacheDir().getAbsolutePath(),
                    null,
                    context.getClassLoader()
            );
            Object pluginPathList = pathListField.get(dexClassLoader);
            Object[] pluginDexElements = (Object[]) dexElementsField.get(pluginPathList);

            Object[] dexElements = (Object[]) Array.newInstance(
                    pluginDexElements.getClass().getComponentType(),
                    hostDexElements.length + pluginDexElements.length
            );
            System.arraycopy(
                    hostDexElements,
                    0, dexElements,
                    0,
                    hostDexElements.length
            );
            System.arraycopy(
                    pluginDexElements,
                    0,
                    dexElements,
                    hostDexElements.length,
                    pluginDexElements.length
            );

            dexElementsField.set(hostPathList, dexElements);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
