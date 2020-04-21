package com.hgo.planassistant.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
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
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.hgo.planassistant.App;
import com.hgo.planassistant.Constant;
import com.hgo.planassistant.R;
import com.hgo.planassistant.datamodel.AVObjectListDataParcelableSend;
import com.hgo.planassistant.tools.DateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TrackDetailMapActivity extends BaseActivity {

    private MapView aMapView = null;
    private AMap amap;
    private List<AVObject> now_list = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_detail_map);

        Toolbar toolbar = findViewById(R.id.toolbar_track_detail_map);
        setToolbar(toolbar);
        toolbar.setTitle("地图详情");

        //获取地图控件引用
        aMapView = (MapView) findViewById(R.id.activity_track_detail_mapview);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        aMapView.onCreate(savedInstanceState); // 此方法须覆写，虚拟机需要在很多情况下保存地图绘制的当前状态。

        if (amap == null) {
            amap = aMapView.getMap();
        }

        Intent intent = getIntent();
        long now_long = Calendar.getInstance().getTime().getTime();
        long start_time_long = intent.getLongExtra("start_time",now_long);
        long end_time_long = intent.getLongExtra("end_time",now_long);
        Calendar start_time = Calendar.getInstance();
        start_time.setTime(new Date(start_time_long));
        Calendar end_time = Calendar.getInstance();
        end_time.setTime(new Date(end_time_long));

        DateFormat dateFormat = new DateFormat(start_time);
        Log.i("TrackDetailMapActivity","收到开始时间："+ dateFormat.GetDetailDescription());
        Log.i("TrackDetailMapActivity","收到结束时间："+ dateFormat.GetDetailDescription(end_time));

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

        int PrecisionLessThen = Integer.parseInt(App.getApplication().getSharedPreferences("setting",MODE_PRIVATE).getString("settings_location_query_precision","300")); // 查询轨迹精度限制
        AVQuery<AVObject> query = new AVQuery<>("trajectory");
        // 启动查询缓存
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.setMaxCacheAge(24 * 3600 * 1000); //设置为一天，单位毫秒
        query.whereEqualTo("UserId", AVUser.getCurrentUser().getObjectId());
        query.whereGreaterThan("time",start_time.getTime());
        query.whereLessThan("time",end_time.getTime());
        query.whereGreaterThan("precision",1);
        query.whereLessThan("precision",PrecisionLessThen);
        query.selectKeys(Arrays.asList("point", "time", "precision","geo_coordinate"));
        query.limit(1000);
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int count, AVException e) {
                if(count>TrackActivity.QueryMaxNum){
                    Toast.makeText(App.getContext(),"查询数据过大无法获取，请检查起止时间！共查询到：" + count + "条数据。",Toast.LENGTH_LONG).show();
                }else{
                    Log.i("TrackActivity","共查询到：" + count + "条数据。");
                    Toast.makeText(App.getContext(),"共查询到：" + count + "条数据。",Toast.LENGTH_LONG).show();
                    now_list = new ArrayList<>(count+1);
                    int querynum = count/1000 + 1;
                    Log.i("TrackActivity","查询次数："+querynum);
                    for(int i=0;i<querynum;i++){
                        Log.i("TrackActivity","第"+i+"次查询");
                        int skip = i*1000;
                        query.skip(skip);
                        query.findInBackground(new FindCallback<AVObject>() {
                            @Override
                            public void done(List<AVObject> avObjects, AVException avException) {
                                if(avObjects!=null&&avObjects.size()>0) {
                                    now_list.addAll(avObjects);
                                    Log.i("TrackActivity","分页查询获取到的数据条数："+avObjects.size()+"，数据总条数"+now_list.size());
                                    if(now_list.size()==count){
                                        // 构建热力图 HeatmapTileProvider
                                        HeatmapTileProvider.Builder builder = new HeatmapTileProvider.Builder();
                                        builder.data(Arrays.asList(GenetateLatLngArratFromAvobject(now_list))); // 设置热力图绘制的数据
                                        // 构造热力图对象
                                        HeatmapTileProvider heatmapTileProvider = builder.build();
                                        // 初始化 TileOverlayOptions
                                        TileOverlayOptions tileOverlayOptions = new TileOverlayOptions();
                                        tileOverlayOptions.tileProvider(heatmapTileProvider); // 设置瓦片图层的提供者
                                        // 向地图上添加 TileOverlayOptions 类对象
                                        amap.addTileOverlay(tileOverlayOptions);

                                        // 全幅显示
                                        com.amap.api.maps.model.LatLngBounds bounds = getLatLngBounds(now_list);
                                        amap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));

                                        // 显示轨迹线
                                        Polyline polyline =amap.addPolyline(new PolylineOptions().
                                                addAll(Arrays.asList(GenetateLatLngArratFromAvobject(now_list))).width(10).color(Color.argb(255, 1, 1, 1)));

                                    }
                                }
                            }
                        });
                    }
                }


            }
        });
//        query.findInBackground(new FindCallback<AVObject>() {
//            @Override
//            public void done(List<AVObject> list, AVException e) {
//                if(list!=null&&list.size()>0){
//                    Log.i("TrackActivity","共查询到：" + list.size() + "条数据。");
//                    Toast.makeText(App.getContext(),"共查询到：" + list.size() + "条数据。",Toast.LENGTH_LONG).show();
//                    // 构建热力图 HeatmapTileProvider
//                    HeatmapTileProvider.Builder builder = new HeatmapTileProvider.Builder();
//                    builder.data(Arrays.asList(GenetateLatLngArratFromAvobject(list))); // 设置热力图绘制的数据
////                            .gradient(ALT_HEATMAP_GRADIENT); // 设置热力图渐变，有默认值 DEFAULT_GRADIENT，可不设置该接口
//                    // Gradient 的设置可见参考手册
//                    // 构造热力图对象
//                    HeatmapTileProvider heatmapTileProvider = builder.build();
//                    // 初始化 TileOverlayOptions
//                    TileOverlayOptions tileOverlayOptions = new TileOverlayOptions();
//                    tileOverlayOptions.tileProvider(heatmapTileProvider); // 设置瓦片图层的提供者
//                    // 向地图上添加 TileOverlayOptions 类对象
//                    amap.addTileOverlay(tileOverlayOptions);
//
//                    // 全幅显示
//                    com.amap.api.maps.model.LatLngBounds bounds = getLatLngBounds(list);
//                    amap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
//
//                    // 显示轨迹线
//                    Polyline polyline =amap.addPolyline(new PolylineOptions().
//                            addAll(Arrays.asList(GenetateLatLngArratFromAvobject(list))).width(10).color(Color.argb(255, 1, 1, 1)));
//
////                    List<com.amap.api.maps.model.LatLng> points = GenetateLatLngListFromAvobject(now_list);
////                    amap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
////
////                    SmoothMoveMarker smoothMarker = new SmoothMoveMarker(amap);
////                    // 设置滑动的图标
////                    smoothMarker.setDescriptor(BitmapDescriptorFactory.fromResource(R.drawable.walk));
////
////                    com.amap.api.maps.model.LatLng drivePoint = points.get(0);
////                    Pair<Integer, com.amap.api.maps.model.LatLng> pair = SpatialRelationUtil.calShortestDistancePoint(points, drivePoint);
////                    points.set(pair.first, drivePoint);
////                    List<com.amap.api.maps.model.LatLng> subList = points.subList(pair.first, points.size());
////                    // 设置滑动的轨迹左边点
////                    smoothMarker.setPoints(subList);
////                    // 设置滑动的总时间
////                    smoothMarker.setTotalDuration(40);
////                    // 开始滑动
////                    smoothMarker.startSmoothMove();
//                }
//            }
//        });

//        Bundle extras = intent.getExtras();
        //Parcelable 反序列化
//        AVObjectListDataParcelableSend avObjectListDataParcelableSend = extras.getParcelable("TrackList");
//        List<AVObject> TrackList = avObjectListDataParcelableSend.getAvObjectList();
//        Log.i("TrackDetailMapActivity","接收到数据总数："+TrackList.size());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        aMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        aMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        aMapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        aMapView.onSaveInstanceState(outState);
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
