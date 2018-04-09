package com.debugcat.plugindemo;


import android.content.Context;
import android.widget.Toast;

/**
 * Created by qiwangming on 2018/4/8.
 */

public class HelloQPlugin {
    public static void main(String[] args) {
        System.out.println("Hello QPlugin");
    }

    public String getHello() {
        return "Hello QPlugin";
    }

    public void showToast(Context context) {
        if (context == null) return;
        Toast.makeText(context, "Hello QPlugin jar弹出的toast", Toast.LENGTH_LONG).show();
    }
}
