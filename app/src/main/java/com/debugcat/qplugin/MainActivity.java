package com.debugcat.qplugin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView mTvLoadJar;

    private TextView mTvLoadPlugin;

    private TextView mTvLoadApk;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mTvLoadJar = findViewById(R.id.tv_load_jar);
        mTvLoadPlugin = findViewById(R.id.tv_load_plugin);
        mTvLoadApk = findViewById(R.id.tv_load_apk);
        mTvLoadJar.setOnClickListener(this);
        mTvLoadPlugin.setOnClickListener(this);
        mTvLoadApk.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_load_jar:
                loadJar();
                break;
            case R.id.tv_load_plugin:
                loadApk();
                break;
            case R.id.tv_load_apk:
                startApk();
                return;
        }
    }

    /**
     * 加载指定位置jar包，测试ClassLoader
     *
     * java级别的jar包，URLClassLoader是不可用的，这个demo里面
     */
    private void loadApk(){
//
        final File optimizedDexOutputPath = new File(Environment.getExternalStorageDirectory()
                + File.separator + "plugin.apk");

        // 4.1以后不能够将optimizedDirectory设置到sd卡目录， 否则抛出异常.
        File optimizedDirectoryFile = getDir("dex", 0) ;

        // 4.1以后不能够将optimizedDirectory设置到sd卡目录， 否则抛出异常.
        DexClassLoader classLoader = new DexClassLoader(optimizedDexOutputPath.getAbsolutePath(), optimizedDirectoryFile.getAbsolutePath(),
                null, getClassLoader());


        try {
            Class cl = classLoader.loadClass("com.debugcat.pluginapk.MainActivity");
            Constructor constructor = cl.getConstructor(new Class[] {});
            Object activity = constructor.newInstance(new Object[] {});
            Method showToast = cl.getMethod("showToast", Context.class);
            showToast.setAccessible(true);
            showToast.invoke(activity, this);
        } catch (Throwable e) {
            e.printStackTrace();
            Toast.makeText(this, "异常" + e.toString(), Toast.LENGTH_LONG).show();
            Log.e("loadApk", e.toString());
        }
    }


    public void startApk() {
        try {
            Class cl = PluginHook.mNowClassLoader.loadClass("com.debugcat.pluginapk.MainActivity");
            Intent intent = new Intent(this, cl);
            //这种方式为通过在宿主AndroidManifest.xml中预埋activity实现
//            startActivity(intent);
            //这种方式为通过欺骗android系统的activity存在性校验的方式实现
            PluginHook.startActivity(this, intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * 理解加载jar的方式，如何加载出一个界面
     */
    private void loadJar() {
        final File optimizedDexOutputPath = new File(Environment.getExternalStorageDirectory()
                + File.separator + "dex.jar");

        // 4.1以后不能够将optimizedDirectory设置到sd卡目录， 否则抛出异常.
        DexClassLoader classLoader = new DexClassLoader(optimizedDexOutputPath.getAbsolutePath(), getFilesDir().getAbsolutePath(),
                null, getClassLoader());


        try {
            Class cl = classLoader.loadClass("com.debugcat.plugindemo.HelloQPlugin");
            Constructor constructor = cl.getConstructor(new Class[] {});
            Object plugin = constructor.newInstance(new Object[] {});
            Method showToast = cl.getMethod("showToast", Context.class);
            showToast.setAccessible(true);
            showToast.invoke(plugin, this);

            Method getHello = cl.getMethod("getHello");
            getHello.setAccessible(true);
        } catch (Throwable e) {
            e.printStackTrace();
            Toast.makeText(this, "异常" + e.toString(), Toast.LENGTH_LONG).show();
            Log.e("loadJar", e.toString());
        }

    }
}
