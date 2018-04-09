package com.debugcat.pluginapk;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

public class PluginResourceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("插件第三个Activity");
        //先不用资源
        final FrameLayout root = new FrameLayout(this);
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        TextView textView = new TextView(this);
        textView.setText("点击切换背景哦");
        textView.setTextSize(30);
        textView.setTextColor(Color.RED);
        root.addView(textView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ((FrameLayout.LayoutParams)textView.getLayoutParams()).gravity = Gravity.CENTER;
        setContentView(root);


        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                root.setBackgroundDrawable(getResources().getDrawable(R.drawable.img_bg));
            }
        });
    }


}
