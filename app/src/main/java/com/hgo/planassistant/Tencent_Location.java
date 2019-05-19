package com.hgo.planassistant;


import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;

public class Tencent_Location extends Activity implements TencentLocationListener{
    private TencentLocationManager mLocationManager;

    public Tencent_Location(Context context){
        mLocationManager = TencentLocationManager.getInstance(context);

        /* 保证调整坐标系前已停止定位 */
        mLocationManager.removeUpdates(this);
        // 设置 wgs84 坐标系
        mLocationManager
                .setCoordinateType(TencentLocationManager.COORDINATE_TYPE_WGS84);

//        start();
        Log.i("Location","开始定位！！");
    }

    public Tencent_Location(Context context, TencentLocationListener locationListener){
        mLocationManager = TencentLocationManager.getInstance(context);

        /* 保证调整坐标系前已停止定位 */
        mLocationManager.removeUpdates(locationListener);
        // 设置 wgs84 坐标系
        mLocationManager
                .setCoordinateType(TencentLocationManager.COORDINATE_TYPE_WGS84);

//        start();
        Log.i("Location","开始定位！！");
    }


    public void start(){

        Log.i("Location","请求定位！");
        // 创建定位请求
        TencentLocationRequest request = TencentLocationRequest.create();

        // 修改定位请求参数, 定位周期 3000 ms
        request.setInterval(3000);

        // 开始定位
        mLocationManager.requestLocationUpdates(request, this);
    }


    public void stop(){
        mLocationManager.removeUpdates(this);
    }


    public void start(TencentLocationListener locationListener){
        Log.i("Location","请求定位！");
        // 创建定位请求
        TencentLocationRequest request = TencentLocationRequest.create();

        // 修改定位请求参数, 定位周期 3000 ms
        request.setInterval(3000);

        // 开始定位
        mLocationManager.requestLocationUpdates(request, locationListener);
    }

    public void stop(TencentLocationListener locationListener){
        mLocationManager.removeUpdates(locationListener);
    }
    //析构函数
    public void finalize(){
        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(TencentLocation tencentLocation, int i, String s) {
        Log.i("MainActivity","位置改变回调！");
        String msg = null;
        if (i == TencentLocation.ERROR_OK) {
            // 定位成功
            StringBuilder sb = new StringBuilder();
            sb.append("(纬度=").append(tencentLocation.getLatitude()).append(",经度=")
                    .append(tencentLocation.getLongitude()).append(",精度=")
                    .append(tencentLocation.getAccuracy()).append("), 来源=")
                    .append(tencentLocation.getProvider()).append(", 地址=")
                    // 注意, 根据国家相关法规, wgs84坐标下无法提供地址信息
                    .append("{84坐标下不提供地址!}");
            msg = sb.toString();
            Log.i("lication",tencentLocation.getLatitude()+","+tencentLocation.getLongitude());
        } else {
            // 定位失败
            msg = "定位失败: " + s;
            Log.i("Location",msg);
        }
        Log.i("Location",msg);
    }

    @Override
    public void onStatusUpdate(String s, int i, String s1) {

    }
}
