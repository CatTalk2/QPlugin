package com.debugcat.qplugin;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import static com.debugcat.qplugin.PluginHook.REAL_ACTIVITY;
import static com.debugcat.qplugin.PluginHook.mNowClassLoader;

/**
 * Created by qiwangming on 2018/4/9.
 */

public class PluginInstrumentation extends Instrumentation {

    //hook住
    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if(bundle != null){
                bundle.setClassLoader(mNowClassLoader);
                if("com.debugcat.qplugin.EmptyActivity".equals(className)) {
                    String realActivity = className = intent.getStringExtra(REAL_ACTIVITY);
                    if (!TextUtils.isEmpty(realActivity)) {
                        //替换resources
                        PluginHook.mNowResources = PluginHook.mBaseResources;
                        PluginHook.mNowAssetManager = PluginHook.mBaseAssetManager;
                        return super.newActivity(cl, realActivity, intent);
                    }
                }
            }
        }
        PluginHook.mNowResources = null;
        PluginHook.mNowAssetManager = null;
        return super.newActivity(cl, className, intent);
    }
}