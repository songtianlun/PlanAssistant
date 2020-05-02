package com.hgo.planassistant.activity;

import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.HeatmapTileProvider;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.TileOverlayOptions;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.hgo.planassistant.App;
import com.hgo.planassistant.Constant;
import com.hgo.planassistant.R;

import java.util.Arrays;
import java.util.List;

public class DetailPMapActivity extends BaseActivity {

//    private MapView mapView;
    private MapView amapview;
    private AVObject mapObject;
    private Context nowActContext;
    private Bundle nowBundle;
    private Toolbar toolbar;
    private AMap amap;
//    private Style mapstyle;
//    private int style_index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pmap);
        nowActContext = this;
        nowBundle = savedInstanceState;
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        MobclickAgent.onResume(this); // umeng+ 统计 //AUTO页面采集模式下不调用
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPause(this);  // umeng+ 统计 //AUTO页面采集模式下不调用
    }

    private void initData(){
        //通过传入的objetid，从云端获取avobject
        String mapsObjectId = getIntent().getStringExtra("pmapObjectId");
        AVObject avObject = AVObject.createWithoutData("personalmap", mapsObjectId);
        avObject.fetchInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
//                String mapname = avObject.getString("title");// 读取 title
                if (avObject != null) {
                    mapObject = avObject;
                    initView();
                    loadMap();
                }else{
                    Toast.makeText(nowActContext,"拉取数据失败!",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void initView(){
        amapview = findViewById(R.id.activity_dpmap_amapView);
        toolbar = findViewById(R.id.toolbar_detailpmap);
        setToolbar(toolbar);
        toolbar.setTitle(mapObject.get("name").toString());

        amapview.onCreate(nowBundle); // 此方法须覆写，虚拟机需要在很多情况下保存地图绘制的当前状态。

        if (amap == null) {
            amap = amapview.getMap();
        }
    }
    private void loadMap(){
        // 显示定位小蓝点
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();
        //初始化定位蓝点样式类
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW);//只定位一次。
        // myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        // 连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        amap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
//aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
        amap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。

        AVQuery<AVObject> query = new AVQuery<>("mappoint");
        // 启动查询缓存
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.setMaxCacheAge(24 * 3600 * 1000); //设置为一天，单位毫秒
        query.whereEqualTo("MapId", mapObject.getObjectId());//获取地图id
        query.limit(1000);

        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                Log.i("TrackActivity","共查询到：" + list.size() + "条数据。");
                Toast.makeText(App.getContext(),"共查询到：" + list.size() + "条数据。",Toast.LENGTH_LONG).show();
                // 构建热力图 HeatmapTileProvider
                HeatmapTileProvider.Builder builder = new HeatmapTileProvider.Builder();
                builder.data(Arrays.asList(GenetateLatLngArratFromAvobject(list))); // 设置热力图绘制的数据
                // 构造热力图对象
                HeatmapTileProvider heatmapTileProvider = builder.build();
                // 初始化 TileOverlayOptions
                TileOverlayOptions tileOverlayOptions = new TileOverlayOptions();
                tileOverlayOptions.tileProvider(heatmapTileProvider); // 设置瓦片图层的提供者
                // 向地图上添加 TileOverlayOptions 类对象
                amap.addTileOverlay(tileOverlayOptions);

                // 全幅显示
                com.amap.api.maps.model.LatLngBounds bounds = getLatLngBounds(list);
                amap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));

                // 显示轨迹线
                Polyline polyline =amap.addPolyline(new PolylineOptions().
                        addAll(Arrays.asList(GenetateLatLngArratFromAvobject(list))).width(10).color(Color.argb(255, 1, 1, 1)));
            }
        });


    }
    private com.amap.api.maps.model.LatLng[] GenetateLatLngArratFromAvobject(List<AVObject> list){
        int sum = list.size();
        com.amap.api.maps.model.LatLng[] latlngs = new com.amap.api.maps.model.LatLng[sum];

        int i=0;
        for (AVObject obj: list){
            AVGeoPoint geopoint = obj.getAVGeoPoint("point");
            double x = geopoint.getLatitude();
            double y = geopoint.getLongitude();
            latlngs[i] = new com.amap.api.maps.model.LatLng(x, y);

            String coordinate = obj.getString("geo_coordinate");
//            Log.i("TrackActivity","当前数据坐标："+coordinate + "数据信息:" + obj);
            if(coordinate.equals(Constant.GPS)){
                // 将WGS-84坐标转换为高德坐标
                CoordinateConverter converter  = new CoordinateConverter(this);
                // CoordType.GPS 待转换坐标类型
                converter.from(CoordinateConverter.CoordType.GPS);
                // sourceLatLng待转换坐标点 LatLng类型
                converter.coord(latlngs[i]);
                // 执行转换操作
                latlngs[i] = converter.convert();
            }
            i++;
        }

        return latlngs;
    }

    //根据自定义内容获取缩放bounds
    private com.amap.api.maps.model.LatLngBounds getLatLngBounds(List<AVObject> Geolist) {
        com.amap.api.maps.model.LatLngBounds.Builder b = com.amap.api.maps.model.LatLngBounds.builder();

        for (AVObject obj: Geolist){
            AVGeoPoint geopoint = obj.getAVGeoPoint("point");
            double x = geopoint.getLatitude();
            double y = geopoint.getLongitude();
            b.include(new com.amap.api.maps.model.LatLng(x, y));
        }

        return b.build();
    }

}
