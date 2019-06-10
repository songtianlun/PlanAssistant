package com.hgo.planassistant.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import com.fengmap.android.FMMapSDK;
import com.fengmap.android.map.FMMap;
import com.fengmap.android.map.FMMapView;
import com.hgo.planassistant.R;

public class IndoorActivity extends AppCompatActivity {

    FMMap mFMMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FMMapSDK.init(this);//初始化蜂鸟云SDK
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indoor);

        Toolbar toolbar = findViewById(R.id.toolbar_indoor);
        setSupportActionBar(toolbar);

        FMMapView mapView = (FMMapView) findViewById(R.id.indoor_mapview);
        mFMMap = mapView.getFMMap();       //获取地图操作对象

        String bid = "10380";             //地图id
        mFMMap.openMapById(bid, true);          //打开地图
    }
    @Override
    public void onBackPressed() {
        if (mFMMap != null) {
            mFMMap.onDestroy();
        }
        super.onBackPressed();
    }
}
