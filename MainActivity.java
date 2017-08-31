package com.jiahao.baidulocation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;

import java.util.List;

public class MainActivity extends AppCompatActivity {


    private BaiduLocationManager baiduLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        baiduLocationManager =  BaiduLocationManager.getInstance(getApplicationContext());
        baiduLocationManager.startBaiduLocation();
        baiduLocationManager.getmLocationBean(new BaiduLocationManager.onLocationListener() {
            @Override
            public void onSuccess(BaiduLocationManager.LocationBean locationBean) {
                Log.i("BaiduLocationApiDem", locationBean.toString());
                baiduLocationManager.stopBaiduLocation();
            }

            @Override
            public void onFile() {
                Log.i("BaiduLocationApiDem", "失败了");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        baiduLocationManager.stopBaiduLocation();
    }
}
