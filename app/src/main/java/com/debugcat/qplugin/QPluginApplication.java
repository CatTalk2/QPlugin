package com.debugcat.qplugin;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

/**
 * Created by qiwangming on 2018/4/9.
 */

public class QPluginApplication extends Application{

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PluginHook.hook(this);
    }


    @Override
    public Resources getResources() {
        return PluginHook.mNowResources != null ? PluginHook.mNowResources : super.getResources();
    }


    @Override
    public AssetManager getAssets() {
        return PluginHook.mNowAssetManager != null ? PluginHook.mNowAssetManager : super.getAssets();
    }

}
