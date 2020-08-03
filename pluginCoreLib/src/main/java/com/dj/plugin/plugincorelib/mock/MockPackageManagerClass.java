package com.dj.plugin.plugincorelib.mock;

import android.content.pm.PackageInfo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 对系统IPackageManager的动态代理，处理getPackageInfo方法的检查，不让系统返回null
 */
public class MockPackageManagerClass implements InvocationHandler {
    private Object mBase;
    public MockPackageManagerClass(Object base) {
        mBase = base;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("getPackageInfo")) {
            return new PackageInfo();
        }
        return method.invoke(mBase, args);
    }
}
