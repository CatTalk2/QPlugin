package com.debugcat.qplugin;

import android.app.Application;
import android.content.Context;

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
}
