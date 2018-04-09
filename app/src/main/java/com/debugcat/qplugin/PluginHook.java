package com.debugcat.qplugin;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

/**
 * Hook拦截走插件
 *
 * Created by qiwangming on 2018/4/9.
 */

public class PluginHook {

    public static final String REAL_ACTIVITY = "realActivity";


    public static final String PLUGIN_ACTIVITY_FOR_STANDARD = "com.debugcat.qplugin.EmptyActivity";


    public static ClassLoader mNowClassLoader;

    public static volatile Context mBaseContext;                        //原始的application中的BaseContext，不能是其他的，否则会内存泄漏

    //hook Instrumentation, hook Classloader
    public static void hook(Application application) {

        mBaseContext = application.getBaseContext();

        final File optimizedDexOutputPath = new File(Environment.getExternalStorageDirectory()
                + File.separator + "plugin.apk");

        // 4.1以后不能够将optimizedDirectory设置到sd卡目录， 否则抛出异常.
        File optimizedDirectoryFile = application.getDir("dex", 0) ;

        // 4.1以后不能够将optimizedDirectory设置到sd卡目录， 否则抛出异常.
        mNowClassLoader = new DexClassLoader(optimizedDexOutputPath.getAbsolutePath(), optimizedDirectoryFile.getAbsolutePath(),
                null, application.getClassLoader());

        //更改系统的Instrumentation对象，以便创建插件的activity
        Object mMainThread = getField(application.getBaseContext(), "mMainThread");
        setField(mMainThread, "mInstrumentation", new PluginInstrumentation());

        setField(getField(mBaseContext, "mPackageInfo"), "mClassLoader", mNowClassLoader);
        Thread.currentThread().setContextClassLoader(mNowClassLoader);
    }



    public static void startActivity(Activity activity, Intent intent) {
        ComponentName componentName = intent.getComponent();
        intent.setClassName(componentName.getPackageName(), PLUGIN_ACTIVITY_FOR_STANDARD);
        intent.putExtra(REAL_ACTIVITY, componentName.getClassName());
        activity.startActivity(intent);
    }

    public static void startActivity(Intent intent) {
        ComponentName componentName = intent.getComponent();
        intent.setClassName(componentName.getPackageName(), PLUGIN_ACTIVITY_FOR_STANDARD);
        intent.putExtra(REAL_ACTIVITY, componentName.getClassName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mBaseContext.startActivity(intent);
    }

    /**
     * 反射的方式设置某个类的成员变量的值
     *
     * @param paramClass  类对象
     * @param paramString 域的名称
     * @param newClass    新的对象
     */
    public static void setField(Object paramClass, String paramString,
                                Object newClass) {
        if (paramClass == null || TextUtils.isEmpty(paramString)) return;
        Field field = null;
        Class cl = paramClass.getClass();
        for (; field == null && cl != null; ) {
            try {
                field = cl.getDeclaredField(paramString);
                if (field != null) {
                    field.setAccessible(true);
                }
            } catch (Throwable ignored) {

            }
            if (field == null) {
                cl = cl.getSuperclass();
            }
        }
        if (field != null) {
            try {
                field.set(paramClass, newClass);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            System.err.print(paramString + " is not found in " + paramClass.getClass().getName());
        }
    }

    /**
     * 设置paramClass中所有名称为paramString的成员变量的值为newClass
     * @param paramClass  类对象
     * @param paramString 域的名称
     * @param newClass    新的对象
     */
    public static void setFieldAllClass(Object paramClass, String paramString,
                                        Object newClass) {
        if (paramClass == null || TextUtils.isEmpty(paramString)) return;
        Field field;
        Class cl = paramClass.getClass();
        for (; cl != null; ) {
            try {
                field = cl.getDeclaredField(paramString);
                if (field != null) {
                    field.setAccessible(true);
                    field.set(paramClass, newClass);
                }
            } catch (Throwable e) {

            }
            cl = cl.getSuperclass();
        }

        return;
    }

    /**
     * 反射的方式获取某个类的方法
     *
     * @param cl             类的class
     * @param name           方法名称
     * @param parameterTypes 方法对应的输入参数类型
     * @return 方法
     */
    public static Method getMethod(Class cl, String name, Class... parameterTypes) {
        Method method = null;
        for (; method == null && cl != null; ) {
            try {
                method = cl.getDeclaredMethod(name, parameterTypes);
                if (method != null) {
                    method.setAccessible(true);
                }
            } catch (Exception ignored) {

            }
            if (method == null) {
                cl = cl.getSuperclass();
            }
        }
        return method;
    }

    /**
     * 反射的方式获取某个类的某个成员变量值
     *
     * @param paramClass  类对象
     * @param paramString field的名字
     * @return field对应的值
     */
    public static Object getField(Object paramClass, String paramString) {
        if (paramClass == null) return null;
        Field field = null;
        Object object = null;
        Class cl = paramClass.getClass();
        for (; field == null && cl != null; ) {
            try {
                field = cl.getDeclaredField(paramString);
                if (field != null) {
                    field.setAccessible(true);
                }
            } catch (Exception ignored) {

            }
            if (field == null) {
                cl = cl.getSuperclass();
            }
        }
        try {
            if (field != null)
                object = field.get(paramClass);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return object;
    }
    //end========================反射相关方法========================
}
