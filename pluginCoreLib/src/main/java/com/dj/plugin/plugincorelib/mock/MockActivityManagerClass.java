package com.dj.plugin.plugincorelib.mock;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dj.plugin.plugincorelib.PluginStubActivity;
import com.dj.plugin.plugincorelib.constant.PluginConstant;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 动态代理系统IActivityManager，通过判断执行的方法来处理对应的操作
 */
public class MockActivityManagerClass implements InvocationHandler {
    private static final String TAG = MockActivityManagerClass.class.getName();
    private Object mBaseObject;
    private Context context;
    public MockActivityManagerClass(Context context,Object base) {
        mBaseObject = base;
        this.context = context;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 替身Activity的包名, 也就是我们自己的包名
        String stubPackage = context.getPackageName();
        if ("startActivity".equals(method.getName())) {
            // 只拦截这个方法
            // 替换参数, 任你所为;甚至替换原始Activity启动别的Activity偷梁换柱

            // 找到参数里面的第一个Intent 对象
            Intent raw;
            int index = 0;

            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Intent) {
                    index = i;
                    break;
                }
            }
            raw = (Intent) args[index];
            Intent newIntent = new Intent();
            // 这里我们把启动的Activity临时替换为 StubActivity
            ComponentName componentName = new ComponentName(stubPackage, PluginStubActivity.class.getName());
            newIntent.setComponent(componentName);
            // 把我们原始要启动的TargetActivity先存起来
            newIntent.putExtra(PluginConstant.EXTRA_TARGET_INTENT, raw);
            // 替换掉Intent, 达到欺骗AMS的目的
            args[index] = newIntent;
            return method.invoke(mBaseObject, args);
        }
//        else if ("startService".equals(method.getName())) {
//            // 只拦截这个方法
//            // 替换参数, 任你所为;甚至替换原始StubService启动别的Service偷梁换柱
//
//            // 找到参数里面的第一个Intent 对象
//            int index = 0;
//            for (int i = 0; i < args.length; i++) {
//                if (args[i] instanceof Intent) {
//                    index = i;
//                    break;
//                }
//            }
//
//            //get StubService form DynamicApplication.pluginServices
//            Intent rawIntent = (Intent) args[index];
//            String rawServiceName = rawIntent.getComponent().getClassName();
//
//            String stubServiceName = DynamicApplication.pluginServices.get(rawServiceName);
//
//            // replace Plugin Service of StubService
//            ComponentName componentName = new ComponentName(stubPackage, stubServiceName);
//            Intent newIntent = new Intent();
//            newIntent.setComponent(componentName);
//
//            // Replace Intent, cheat AMS
//            args[index] = newIntent;
//
//            Log.d(TAG, "hook success");
//            return method.invoke(mBaseObject, args);
//        } else if ("stopService".equals(method.getName())) {
//            // 只拦截这个方法
//            // 替换参数, 任你所为;甚至替换原始StubService启动别的Service偷梁换柱
//
//            // 找到参数里面的第一个Intent 对象
//            int index = 0;
//            for (int i = 0; i < args.length; i++) {
//                if (args[i] instanceof Intent) {
//                    index = i;
//                    break;
//                }
//            }
//
//            //get StubService form DynamicApplication.pluginServices
//            Intent rawIntent = (Intent) args[index];
//            String rawServiceName = rawIntent.getComponent().getClassName();
//            String stubServiceName = DynamicApplication.pluginServices.get(rawServiceName);
//
//            // replace Plugin Service of StubService
//            ComponentName componentName = new ComponentName(stubPackage, stubServiceName);
//            Intent newIntent = new Intent();
//            newIntent.setComponent(componentName);
//
//            // Replace Intent, cheat AMS
//            args[index] = newIntent;
//
//            Log.d(TAG, "hook success");
//            return method.invoke(mBaseObject, args);
//        }else if("bindService".equals(method.getName())){
//            //找到参数里面的第一个Intent对象
//            // 找到参数里面的第一个Intent 对象
//            int index = 0;
//            for (int i = 0; i < args.length; i++) {
//                if (args[i] instanceof Intent) {
//                    index = i;
//                    break;
//                }
//            }
//            Intent rawIntent = (Intent) args[index];
//            String rawServiceName = rawIntent.getComponent().getClassName();
//            String stubServiceName = DynamicApplication.pluginServices.get(rawServiceName);
//            ComponentName componentName = new ComponentName(stubPackage, stubServiceName);
//            Intent newIntent = new Intent();
//            newIntent.setComponent(componentName);
//            args[index] = newIntent;
//
//            Log.d(TAG, "hook success");
//            return method.invoke(mBaseObject, args);
//        }

        return method.invoke(mBaseObject, args);
    }
}
