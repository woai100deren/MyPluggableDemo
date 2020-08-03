package com.dj.plugin.plugincorelib.manager;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 处理加载assets目录下插件的管理类
 */
public class PluginAssetsManager {
    private static File sBaseDir;
    /**
     * 把Assets里面的文件复制到 /data/data/files 目录下
     * @param context 上下文
     * @param sourceName 文件名称
     */
    public static void extractAssets(Context context, String sourceName) {
        AssetManager am = context.getAssets();
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = am.open(sourceName);
            File extractFile = context.getFileStreamPath(sourceName);
            fos = new FileOutputStream(extractFile);
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = is.read(buffer)) > 0) {
                fos.write(buffer, 0, count);
            }
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeSilently(is);
            closeSilently(fos);
        }
    }

    /**
     * 关闭操作流
     * @param closeable
     */
    private static void closeSilently(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取插件文件拷贝到/data/data/files 目录下之后的文件路径
     * @param context
     * @param fileName
     * @return
     */
    public static String getFileStreamPath(Context context,String fileName){
        try {
            return context.getFileStreamPath(fileName).getAbsolutePath();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取插件文件拷贝到/data/data/files 目录下之后的文件File
     * @param context
     * @param fileName
     * @return
     */
    public static File getFileStreamPathFile(Context context,String fileName){
        try {
            return context.getFileStreamPath(fileName);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 待加载插件经过opt优化之后存放odex得路径
     */
    public static File getPluginOptDexDir(Context context,String packageName) {
        return enforceDirExists(new File(getPluginBaseDir(context,packageName), "odex"));
    }

    /**
     * 需要加载得插件得基本目录 /data/data/<package>/files/plugin/
      */
    private static File getPluginBaseDir(Context context,String packageName) {
        if (sBaseDir == null) {
            sBaseDir = context.getFileStreamPath("plugin");
            enforceDirExists(sBaseDir);
        }
        return enforceDirExists(new File(sBaseDir, packageName));
    }

    /**
     * 创建文件
     * @param sBaseDir
     * @return
     */
    private static synchronized File enforceDirExists(File sBaseDir) {
        if (!sBaseDir.exists()) {
            boolean ret = sBaseDir.mkdir();
            if (!ret) {
                throw new RuntimeException("create dir " + sBaseDir + "failed");
            }
        }
        return sBaseDir;
    }

    /**
     * 插件得lib库路径
     */
    public static File getPluginLibDir(Context context,String packageName) {
        return enforceDirExists(new File(getPluginBaseDir(context,packageName), "lib"));
    }
}
