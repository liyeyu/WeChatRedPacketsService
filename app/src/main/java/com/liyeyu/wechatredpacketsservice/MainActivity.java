package com.liyeyu.wechatredpacketsservice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        startService(new Intent(this,RedPacketsService.class));
        String name = "com.liyeyu.wechatredpacketsservice/.RedPacketsService";
        Log.i("enabled",SysUtils.enabled(this,name)+"");
        Log.i("checkStealFeature",SysUtils.checkStealFeature(this,name)+"");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
