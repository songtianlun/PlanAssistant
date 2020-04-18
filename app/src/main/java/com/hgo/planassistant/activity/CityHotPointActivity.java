package com.hgo.planassistant.activity;

import android.os.Bundle;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import com.hgo.planassistant.R;
import com.umeng.analytics.MobclickAgent;

public class CityHotPointActivity extends BaseActivity {
    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_hot_point);
        Toolbar toolbar = findViewById(R.id.toolbar_city_hotpoint);
        setToolbar(toolbar);


        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView_city_hotpoint);
        mBaiduMap = mMapView.getMap();


        //开启交通图
        mBaiduMap.setTrafficEnabled(true);

        //开启热力图
        mBaiduMap.setBaiduHeatMapEnabled(true);

//        FloatingActionButton fab = findViewById(R.id.fab_city_hotpoint);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();

//        MobclickAgent.onResume(this); // umeng+ 统计 //AUTO页面采集模式下不调用
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();

//        MobclickAgent.onPause(this);  // umeng+ 统计 //AUTO页面采集模式下不调用
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

}
