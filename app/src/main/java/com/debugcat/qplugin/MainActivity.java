package com.debugcat.qplugin;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView mTvLoadJar;

    private TextView mTvLoadPlugin;

    private TextView mTvText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvLoadJar = findViewById(R.id.tv_load_jar);
        mTvLoadPlugin = findViewById(R.id.tv_load_plugin);
        mTvText = findViewById(R.id.tv_text);
        mTvLoadJar.setOnClickListener(this);
        mTvLoadPlugin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_load_jar:
                loadJar();
                break;
            case R.id.tv_load_plugin:
                break;
        }
    }

    /**
     * 加载指定位置jar包，测试ClassLoader
     */
    private void loadJar(){
//        String libPath = Environment.getExternalStorageDirectory()+ File.separator + "dex.jar"; // 要动态加载的jar
//        String tmpPath = ;
//        /**
//         * 进行动态加载，利用java的反射调用com.test.dynamic.MyClass的方法
//         */
//        DexClassLoader classLoader = new DexClassLoader(libPath, tmpPath, null, getClassLoader());
//


        final File optimizedDexOutputPath = new File(Environment.getExternalStorageDirectory()
                + File.separator + "dex.jar");



        //jar的包名和主工程的包名一致时可以用下面的代码   特点：方便简单  缺点：具有包名一致的限制
        /*BaseDexClassLoader cl = new BaseDexClassLoader(optimizedDexOutputPath.getAbsolutePath(),
                this.getFilesDir(),null, this.getClass().getClassLoader());
        Class libProviderClazz = null;
        try {
            // 载入JarLoader类， 并且通过反射构建JarLoader对象， 然后调用sayHi方法
            libProviderClazz = cl.loadClass("fota.adups.myapplication.JarLoader");
            ILoader loader = (ILoader) libProviderClazz.newInstance();
            Toast.makeText(MainActivity.this, loader.sayHi(), Toast.LENGTH_SHORT).show();
        } catch (Exception exception) {
            // Handle exception gracefully here.
            exception.printStackTrace();
        }*/


        //jar的包名和主工程的可以不一致，通用性强，可以适用于动态加载apk
        // 4.1以后不能够将optimizedDirectory设置到sd卡目录， 否则抛出异常.
        DexClassLoader classLoader = new DexClassLoader(optimizedDexOutputPath.getAbsolutePath(), getFilesDir().getAbsolutePath(),
                null, getClassLoader());


//        final File optimizedDexOutputPath = new File(Environment.getExternalStorageDirectory().toString()
//                + File.separator + "dex.jar");
//
//        BaseDexClassLoader classLoader = new BaseDexClassLoader(Environment.getExternalStorageDirectory().toString(),
//                new File("/data/app/com.debugcat.qplugin-2/plugin.dex"), optimizedDexOutputPath.getAbsolutePath(), getClassLoader());


        try {
            Class cl = classLoader.loadClass("com.debugcat.javajar.HelloQPlugin");
            Constructor constructor = cl.getConstructor(new Class[] {});
            Object plugin = constructor.newInstance(new Object[] {});
            Method method = cl.getMethod("showToast");
            method.setAccessible(true);
            String result = (String) method.invoke(plugin);
            if (!TextUtils.isEmpty(result)) {
                mTvText.setText(result);
            } else {
                Toast.makeText(this, "空的", Toast.LENGTH_LONG).show();
            }
        } catch (Throwable e) {
            e.printStackTrace();
            Toast.makeText(this, "异常" + e.toString(), Toast.LENGTH_LONG).show();
            Log.e("loadJar", e.toString());
        }

        //dong
    }
}
