package com.debugcat.pluginapk;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("插件第一个Activity");
        //先不用资源
        FrameLayout root = new FrameLayout(this);
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        TextView textView = new TextView(this);
        textView.setText("跳转第二个插件Activity");
        textView.setTextSize(30);
        textView.setTextColor(Color.RED);
        root.addView(textView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ((FrameLayout.LayoutParams)textView.getLayoutParams()).gravity = Gravity.CENTER;
        setContentView(root);


        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(MainActivity.this, new Intent(MainActivity.this, PluginActivity.class));
            }
        });
        // setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }



    public static final String REAL_ACTIVITY = "realActivity";


    public static final String PLUGIN_ACTIVITY_FOR_STANDARD = "com.debugcat.qplugin.EmptyActivity";


    public static void startActivity(Activity activity, Intent intent) {
        ComponentName componentName = intent.getComponent();
        intent.setClassName(componentName.getPackageName(), PLUGIN_ACTIVITY_FOR_STANDARD);
        intent.putExtra(REAL_ACTIVITY, componentName.getClassName());
        activity.startActivity(intent);
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    public void showToast(Context context) {
        Toast.makeText(context, "插件APK弹出toast", Toast.LENGTH_SHORT).show();
    }
}
