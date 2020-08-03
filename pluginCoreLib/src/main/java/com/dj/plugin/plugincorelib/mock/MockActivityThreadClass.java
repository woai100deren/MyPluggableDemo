package com.dj.plugin.plugincorelib.mock;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.dj.plugin.plugincorelib.constant.PluginConstant;
import com.dj.plugin.plugincorelib.manager.PluginPackageHookManager;
import com.dj.plugin.plugincorelib.utils.RefInvokeUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.List;
/**
 * 动态代理系统ActivityThread，通过判断Handler的消息回调，进行相应操作
 */
public class MockActivityThreadClass implements Handler.Callback {
    private static final String TAG = MockActivityThreadClass.class.getName();
    private Handler mBase;

    public MockActivityThreadClass(Handler base) {
        mBase = base;
    }

    @Override
    public boolean handleMessage(Message msg) {
        Log.e(TAG,">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>1111111");
        //从android p开始，activity的启动流程有所变化。ActivityThread中，activity的生命周期，都是在EXECUTE_TRANSACTION回调中完成
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            switch (msg.what) {
                // ActivityThread里面 "CREATE_SERVICE" 这个字段的值是114
                // 本来使用反射的方式获取最好, 这里为了简便直接使用硬编码
                case 114:
                    handleCreateService(msg);
                    break;
                // ActivityThread里面 "EXECUTE_TRANSACTION" 这个字段的值是159
                // 本来使用反射的方式获取最好, 这里为了简便直接使用硬编码
                case 159:
                    try {
                        Object obj = msg.obj;
                        //获取ClientTransaction中的mActivityCallbacks集合
                        Class<?> clazz = Class.forName("android.app.servertransaction.ClientTransaction");
                        Field mActivityCallbacksFiled = clazz.getDeclaredField("mActivityCallbacks");
                        mActivityCallbacksFiled.setAccessible(true);
                        List list = (List) mActivityCallbacksFiled.get(obj);
                        if (list != null && list.size() > 0 && "android.app.servertransaction.LaunchActivityItem".equals(list.get(0).getClass().getName())) {
                            //得到集合中的LaunchActivityItem
                            Object o = list.get(0);
                            //获取LaunchActivityItem中的mIntent
                            Class<?> LaunchActivityItemClazz = Class.forName("android.app.servertransaction.LaunchActivityItem");
                            Field mIntentFiled = LaunchActivityItemClazz.getDeclaredField("mIntent");
                            mIntentFiled.setAccessible(true);
                            Intent intent = (Intent) mIntentFiled.get(o);
                            //得到我们设置的class 替换进去
                            if (intent.getParcelableExtra(PluginConstant.EXTRA_TARGET_INTENT) != null) {
                                Intent target = intent.getParcelableExtra(PluginConstant.EXTRA_TARGET_INTENT);
                                intent.setComponent(target.getComponent());


                                //修改packageName，这样缓存才能命中
                                ActivityInfo activityInfo = (ActivityInfo) RefInvokeUtils.getFieldObject(o, "mInfo");
                                activityInfo.applicationInfo.packageName = target.getPackage() == null ?
                                        target.getComponent().getPackageName() : target.getPackage();

                                try {
                                    PluginPackageHookManager.hookPackageManager();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, e.toString());
                    }
                    break;
            }
        } else {
            switch (msg.what) {
                // ActivityThread里面 "LAUNCH_ACTIVITY" 这个字段的值是100
                // 本来使用反射的方式获取最好, 这里为了简便直接使用硬编码
                case 100:
                    handleLaunchActivity(msg);
                    break;
                // ActivityThread里面 "CREATE_SERVICE" 这个字段的值是114
                // 本来使用反射的方式获取最好, 这里为了简便直接使用硬编码
                case 114:
                    handleCreateService(msg);
                    break;
            }
        }

        mBase.handleMessage(msg);
        return true;
    }

    /**
     * 劫持启动一个activity之后的操作
     * @param msg
     */
    private void handleLaunchActivity(Message msg) {
        // 这里简单起见,直接取出TargetActivity;
        Object obj = msg.obj;

        // 把替身恢复成真身
        Intent raw = (Intent) RefInvokeUtils.getFieldObject(obj, "intent");

        Intent target = raw.getParcelableExtra(PluginConstant.EXTRA_TARGET_INTENT);
        raw.setComponent(target.getComponent());

        //修改packageName，这样缓存才能命中
        ActivityInfo activityInfo = (ActivityInfo) RefInvokeUtils.getFieldObject(obj, "activityInfo");
        activityInfo.applicationInfo.packageName = target.getPackage() == null ?
                target.getComponent().getPackageName() : target.getPackage();
        try {
            PluginPackageHookManager.hookPackageManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 对service劫持之后的操作
     * @param msg
     */
    private void handleCreateService(Message msg) {
        // 这里简单起见,直接取出插件Service
//
//        Object obj = msg.obj;
//        ServiceInfo serviceInfo = (ServiceInfo) RefInvokeUtils.getFieldObject(obj, "info");
//
//        String realServiceName = null;
//
//        for (String key : DynamicApplication.pluginServices.keySet()) {
//            String value = DynamicApplication.pluginServices.get(key);
//            if(value.equals(serviceInfo.name)) {
//                realServiceName = key;
//                break;
//            }
//        }
//
//        serviceInfo.name = realServiceName;
    }
}