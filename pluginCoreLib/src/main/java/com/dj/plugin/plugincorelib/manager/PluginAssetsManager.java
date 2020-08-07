package com.dj.plugin.plugincorelib.manager;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.os.Build;
import android.util.Log;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ZipUtils;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

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

            extractAssetsSo(context,sourceName);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeSilently(is);
            closeSilently(fos);
        }
    }

    /**
     * 拷贝一份apk文件，并重命名为zip后缀
     * @param context
     * @param sourceName
     */
    private static void extractAssetsSo(Context context, String sourceName) {
        AssetManager am = context.getAssets();
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = am.open(sourceName);
            File extractFile = context.getFileStreamPath(sourceName.replace("apk","zip"));
            fos = new FileOutputStream(extractFile);
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = is.read(buffer)) > 0) {
                fos.write(buffer, 0, count);
            }
            fos.flush();

            unzipSoFiles(context,sourceName.replace("apk","zip"));
        } catch (Exception e) {
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
     * 解压apk文件，提取so文件，判断CPU集，拷贝对应的so到每个插件指定加载so的文件夹中
     * @param context
     * @param sourceName
     * @throws Exception
     */
    private static void unzipSoFiles(Context context,String sourceName) throws Exception {
        String unZipPath = context.getFilesDir().getAbsolutePath()+File.separator+sourceName.replace(".zip","");
        ZipUtils.unzipFile(getFileStreamPath(context,sourceName),unZipPath);
        Log.e("dj",">>>>>>>>>>>>>>>>当前手机cpu指令集："+Build.CPU_ABI);
        List<File> files;
        String soPath ;
        if("arm64-v8a".equals(Build.CPU_ABI)){
            soPath = unZipPath+File.separator+"lib"+File.separator+"arm64-v8a";
        }else if("armeabi-v7a".equals(Build.CPU_ABI)){
            soPath = unZipPath+File.separator+"lib"+File.separator+"armeabi-v7a";
        }else if("armeabi".equals(Build.CPU_ABI)){
            soPath = unZipPath+File.separator+"lib"+File.separator+"armeabi";
        }else if("x86".equals(Build.CPU_ABI)){
            soPath = unZipPath+File.separator+"lib"+File.separator+"x86";
        }else{
            soPath = unZipPath+File.separator+"lib"+File.separator+"x86_64";
        }
        files = FileUtils.listFilesInDir(soPath);
        ApplicationInfo applicationInfo = PluginLoadedApkClassLoaderHookManager.generateApplicationInfo(getFileStreamPathFile(context,sourceName));
        for (File file:files){
            FileUtils.copy(file.getAbsolutePath(),getPluginLibDir(context,applicationInfo.packageName).getAbsoluteFile()+File.separator+file.getName());
        }

        FileUtils.delete(unZipPath);
        FileUtils.delete(context.getFileStreamPath(sourceName.replace("apk","zip")));
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
