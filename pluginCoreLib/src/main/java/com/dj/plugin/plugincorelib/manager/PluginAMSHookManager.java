package com.dj.plugin.plugincorelib.manager;

import android.app.Instrumentation;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;

import com.dj.plugin.plugincorelib.instrumentation.InstrumentationProxy;
import com.dj.plugin.plugincorelib.mock.MockActivityManagerClass;
import com.dj.plugin.plugincorelib.mock.MockActivityThreadClass;
import com.dj.plugin.plugincorelib.utils.RefInvokeUtils;

import java.lang.reflect.Proxy;

public class PluginAMSHookManager {
    /**
     * Hook AMS
     * 主要完成的操作是:把真正要启动的Activity临时替换为在AndroidManifest.xml中声明的替身Activity,进而骗过AMS
     */
    public static void hookAMS(Context context) throws ClassNotFoundException {
        //获取AMN的gDefault单例gDefault，gDefault是final静态的
        Object gDefault = null;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //获取ActivityTaskManager的单例IActivityTaskManagerSingleton，IActivityTaskManagerSingleton是final静态的
            gDefault = RefInvokeUtils.getStaticFieldObject("android.app.ActivityTaskManager", "IActivityTaskManagerSingleton");
        } else
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //获取ActivityManager的单例IActivityManagerSingleton，IActivityManagerSingleton是final静态的
            gDefault = RefInvokeUtils.getStaticFieldObject("android.app.ActivityManager", "IActivityManagerSingleton");
        }else if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            //获取ActivityManagerNative的单例gDefault，gDefault静态的
            gDefault = RefInvokeUtils.getStaticFieldObject("android.app.ActivityManagerNative", "gDefault");
        }

        // gDefault是一个 android.util.Singleton<T>对象; 我们取出这个单例里面的mInstance字段
        Object mInstance = RefInvokeUtils.getFieldObject("android.util.Singleton", gDefault, "mInstance");

        Class<?> classB2Interface = null;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            classB2Interface = Class.forName("android.app.IActivityTaskManager");
        } else {
            classB2Interface = Class.forName("android.app.IActivityManager");
        }
        Object proxy = Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class<?>[] { classB2Interface },
                new MockActivityManagerClass(context,mInstance));

        //把gDefault的mInstance字段，修改为proxy
        RefInvokeUtils.setFieldObject("android.util.Singleton", gDefault, "mInstance", proxy);
    }

    /**
     * 由于之前我们用替身欺骗了AMS; 现在我们要换回我们真正需要启动的Activity
     * 不然就真的启动替身了, 狸猫换太子...
     * 到最终要启动Activity的时候,会交给ActivityThread 的一个内部类叫做 H 来完成
     * H 会完成这个消息转发; 最终调用它的callback
     */
    public static void hookActivityThread() {
        // 先获取到当前的ActivityThread对象
        Object currentActivityThread = RefInvokeUtils.getStaticFieldObject("android.app.ActivityThread", "sCurrentActivityThread");
        // 由于ActivityThread一个进程只有一个,我们获取这个对象的mH
        Handler mH = (Handler) RefInvokeUtils.getFieldObject(currentActivityThread, "mH");
        //把Handler的mCallback字段，替换为new MockClass2(mH)
        RefInvokeUtils.setFieldObject(Handler.class,
                mH, "mCallback", new MockActivityThreadClass(mH));

        //以下是替换Activity中的resources资源，以便插件加载资源时可以用插件自己的resources
        Instrumentation instrumentation= (Instrumentation)RefInvokeUtils.getFieldObject(currentActivityThread, "mInstrumentation");
        InstrumentationProxy proxy=new InstrumentationProxy(instrumentation);
        RefInvokeUtils.setFieldObject(currentActivityThread,"mInstrumentation",proxy);

    }
}
