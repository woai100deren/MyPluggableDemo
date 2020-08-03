package com.dj.plugin.plugincorelib.manager;

import com.dj.plugin.plugincorelib.mock.MockPackageManagerClass;
import com.dj.plugin.plugincorelib.utils.RefInvokeUtils;

import java.lang.reflect.Proxy;

public class PluginPackageHookManager {
    /**
     * 这一步是因为 initializeJavaContextClassLoader 这个方法内部无意中检查了这个包是否在系统安装
     * @throws ClassNotFoundException
     */
    public static void hookPackageManager() throws ClassNotFoundException {
        // 这一步是因为 initializeJavaContextClassLoader 这个方法内部无意中检查了这个包是否在系统安装
        // 如果没有安装, 直接抛出异常, 这里需要临时Hook掉 PMS, 绕过这个检查.
        Object currentActivityThread = RefInvokeUtils.invokeStaticMethod("android.app.ActivityThread", "currentActivityThread");

        // 获取ActivityThread里面原始的 sPackageManager
        Object sPackageManager = RefInvokeUtils.getFieldObject(currentActivityThread, "sPackageManager");

        // 准备好代理对象, 用来替换原始的对象
        Class<?> iPackageManagerInterface = Class.forName("android.content.pm.IPackageManager");
        Object proxy = Proxy.newProxyInstance(iPackageManagerInterface.getClassLoader(),
                new Class<?>[] { iPackageManagerInterface },
                new MockPackageManagerClass(sPackageManager));

        // 1. 替换掉ActivityThread里面的 sPackageManager 字段
        RefInvokeUtils.setFieldObject(currentActivityThread, "sPackageManager", proxy);
    }
}
