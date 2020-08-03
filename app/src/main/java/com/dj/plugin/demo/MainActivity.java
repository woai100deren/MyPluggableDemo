package com.dj.plugin.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{
            Class mClass = getClassLoader().loadClass("com.dj.plugin.plugin1.MainActivity");
            Log.e("dj",">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+mClass);
        }catch (Exception e){
            e.printStackTrace();
        }

        Log.e("dj",">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>宿主的resource:"+getResources());
    }

    public void jumpPlugin(View view) {
        Intent t = new Intent();
        t.setComponent(new ComponentName("com.dj.plugin.plugin1",
                "com.dj.plugin.plugin1.MainActivity"));
        startActivity(t);
    }
}