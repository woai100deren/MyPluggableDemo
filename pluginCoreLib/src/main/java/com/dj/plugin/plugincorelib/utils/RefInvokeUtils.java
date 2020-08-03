package com.dj.plugin.plugincorelib.utils;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class RefInvokeUtils {

    //无参
    public static Object createObject(String className) {
        Class[] pareTypes = new Class[]{};
        Object[] pareValues = new Object[]{};

        try {
            Class r = Class.forName(className);
            return createObject(r, pareTypes, pareValues);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    //无参
    public static Object createObject(Class clazz) {
        Class[] pareType = new Class[]{};
        Object[] pareValues = new Object[]{};

        return createObject(clazz, pareType, pareValues);
    }

    //一个参数
    public static Object createObject(String className, Class pareTyple, Object pareVaule) {
        Class[] pareType = new Class[]{ pareTyple };
        Object[] pareValues = new Object[]{ pareVaule };

        try {
            Class r = Class.forName(className);
            return createObject(r, pareType, pareValues);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    //一个参数
    public static Object createObject(Class clazz, Class pareTyple, Object pareVaule) {
        Class[] pareType = new Class[]{ pareTyple };
        Object[] pareValues = new Object[]{ pareVaule };

        return createObject(clazz, pareType, pareValues);
    }

    //多个参数
    public static Object createObject(String className, Class[] pareType, Object[] pareValues) {
        try {
            Class r = Class.forName(className);
            return createObject(r, pareType, pareValues);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    //多个参数
    public static Object createObject(Class clazz, Class[] pareType, Object[] pareValues) {
        try {
            Constructor constructor = clazz.getDeclaredConstructor(pareType);
            constructor.setAccessible(true);
            return constructor.newInstance(pareValues);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }



    //多个参数
    public static Object invokeInstanceMethod(Object obj, String methodName, Class[] pareType, Object[] pareValues) {
        if (obj == null)
            return null;

        try {
            //调用一个private方法
            Method method = obj.getClass().getDeclaredMethod(methodName, pareType); //在指定类中获取指定的方法
            method.setAccessible(true);
            return method.invoke(obj, pareValues);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    //一个参数
    public static Object invokeInstanceMethod(Object obj, String methodName, Class pareTyple, Object pareVaule) {
        Class[] pareType = {pareTyple};
        Object[] pareValues = {pareVaule};

        return invokeInstanceMethod(obj, methodName, pareType, pareValues);
    }

    //无参
    public static Object invokeInstanceMethod(Object obj, String methodName) {
        Class[] pareType = new Class[]{};
        Object[] pareValues = new Object[]{};

        return invokeInstanceMethod(obj, methodName, pareType, pareValues);
    }




    //无参
    public static Object invokeStaticMethod(String className, String method_name) {
        Class[] pareType = new Class[]{};
        Object[] pareValues = new Object[]{};

        return invokeStaticMethod(className, method_name, pareType, pareValues);
    }

    //一个参数
    public static Object invokeStaticMethod(String className, String method_name, Class pareTyple, Object pareVaule) {
        Class[] pareType = new Class[]{pareTyple};
        Object[] pareValues = new Object[]{pareVaule};

        return invokeStaticMethod(className, method_name, pareType, pareValues);
    }

    //多个参数
    public static Object invokeStaticMethod(String className, String method_name, Class[] pareType, Object[] pareValues) {
        try {
            Class obj_class = Class.forName(className);
            return invokeStaticMethod(obj_class, method_name, pareType, pareValues);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    //无参
    public static Object invokeStaticMethod(Class clazz, String method_name) {
        Class[] pareType = new Class[]{};
        Object[] pareValues = new Object[]{};

        return invokeStaticMethod(clazz, method_name, pareType, pareValues);
    }

    //一个参数
    public static Object invokeStaticMethod(Class clazz, String method_name, Class classType, Object pareVaule) {
        Class[] classTypes = new Class[]{classType};
        Object[] pareValues = new Object[]{pareVaule};

        return invokeStaticMethod(clazz, method_name, classTypes, pareValues);
    }

    //多个参数
    public static Object invokeStaticMethod(Class clazz, String method_name, Class[] pareType, Object[] pareValues) {
        try {
            Method method = clazz.getDeclaredMethod(method_name, pareType);
            method.setAccessible(true);
            return method.invoke(null, pareValues);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }



    //简写版本
    public static Object getFieldObject(Object obj, String filedName) {
        return getFieldObject(obj.getClass(), obj, filedName);
    }

    public static Object getFieldObject(String className, Object obj, String filedName) {
        try {
            Class obj_class = Class.forName(className);
            return getFieldObject(obj_class, obj, filedName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getFieldObject(Class clazz, Object obj, String filedName) {
        try {
            Field field = clazz.getDeclaredField(filedName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    //简写版本
    public static void setFieldObject(Object obj, String filedName, Object filedVaule) {
        setFieldObject(obj.getClass(), obj, filedName, filedVaule);
    }

    public static void setFieldObject(Class clazz, Object obj, String filedName, Object filedVaule) {
        try {
            Field field = clazz.getDeclaredField(filedName);
            field.setAccessible(true);
            field.set(obj, filedVaule);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setFieldObject(String className, Object obj, String filedName, Object filedVaule) {
        try {
            Class obj_class = Class.forName(className);
            setFieldObject(obj_class, obj, filedName, filedVaule);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }




    public static Object getStaticFieldObject(String className, String filedName) {
        return getFieldObject(className, null, filedName);
    }

    public static Object getStaticFieldObject(Class clazz, String filedName) {
        return getFieldObject(clazz, null, filedName);
    }

    public static void setStaticFieldObject(String classname, String filedName, Object filedVaule) {
        setFieldObject(classname, null, filedName, filedVaule);
    }

    public static void setStaticFieldObject(Class clazz, String filedName, Object filedVaule) {
        setFieldObject(clazz, null, filedName, filedVaule);
    }
}
