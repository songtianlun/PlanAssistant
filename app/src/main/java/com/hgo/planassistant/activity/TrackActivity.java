package com.hgo.planassistant.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.HeatmapTileProvider;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.TileOverlayOptions;
import com.amap.api.maps.utils.SpatialRelationUtil;
import com.amap.api.maps.utils.overlay.SmoothMoveMarker;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DistanceItem;
import com.amap.api.services.route.DistanceResult;
import com.amap.api.services.route.DistanceSearch;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.snackbar.Snackbar;
import com.hgo.planassistant.App;
import com.hgo.planassistant.Constant;
import com.hgo.planassistant.R;
import com.hgo.planassistant.datamodel.AVObjectListDataParcelableSend;
import com.hgo.planassistant.tools.PathSmoothTool;


import org.geotools.geojson.geom.GeometryJSON;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class TrackActivity extends BaseActivity implements View.OnClickListener{

    public static int QueryMaxNum = 5000; //数据查询条数上限
    private int PrecisionLessThen = 500; // 轨迹精度查询最高限制
    private   String HEATMAP_SOURCE_ID = "HEATMAP_SOURCE_ID";
    private   String HEATMAP_LAYER_ID = "HEATMAP_LAYER_ID";
    private Float[] listOfHeatmapIntensityStops;
//    private MapView mapView;
//    private MapboxMap mapboxmap;
//    private Style map_style;
//    private int index;
    private MapView aMapView = null;
    private AMap amap = null;


    private Calendar start_time;
    private Calendar end_time;

    private Button BT_save,BT_quare,BT_theme;
    private TextView TV_start_calendar, TV_start_time,TV_stop_calendar,TV_stop_time,TV_info;

    private List<AVObject> now_list = null;

    private Context track_context;

    private BarChart chart;

    //MarkerQuare
    public int range_point_num_max = 0; //范围查询时的最大点个数，中间变量

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        Toolbar toolbar = findViewById(R.id.toolbar_track);
        setToolbar(toolbar);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));

//        SharedPreferences SP_setting = App.getApplication().getSharedPreferences("setting",MODE_PRIVATE);
        PrecisionLessThen = Integer.parseInt(App.getApplication().getSharedPreferences("setting",MODE_PRIVATE).getString("settings_location_query_precision","300")); // 查询轨迹精度限制

        initView(savedInstanceState);
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
//        MobclickAgent.onResume(this); // umeng+ 统计 //AUTO页面采集模式下不调用

        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        aMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPause(this);  // umeng+ 统计 //AUTO页面采集模式下不调用
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        aMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        aMapView.onSaveInstanceState(outState);
    }

    void initView(Bundle savedInstanceState){


        chart = findViewById(R.id.card_activity_track_bar_chart_chart);
        aMapView = findViewById(R.id.card_track_amapView);
//        mapView = (MapView) findViewById(R.id.card_track_mapView);
        BT_save = findViewById(R.id.card_activity_track_info_button_save);
        BT_quare = findViewById(R.id.card_activity_track_info_button_quare);
        BT_theme = findViewById(R.id.card_activity_track_info_button_theme);
        TV_start_calendar = findViewById(R.id.card_activity_track_info_start_calendar);
        TV_stop_calendar = findViewById(R.id.card_activity_track_info_end_calendar);
        TV_start_time = findViewById(R.id.card_activity_track_info_start_time);
        TV_stop_time = findViewById(R.id.ccard_activity_track_info_end_calendar_time);
        TV_info = findViewById(R.id.card_activity_track_info_info_description);

        BT_theme.setOnClickListener(this);
        BT_quare.setOnClickListener(this);
        BT_save.setOnClickListener(this);
        TV_info.setOnClickListener(this);
        TV_stop_time.setOnClickListener(this);
        TV_start_time.setOnClickListener(this);
        TV_stop_calendar.setOnClickListener(this);
        TV_start_calendar.setOnClickListener(this);

        start_time = Calendar.getInstance();
        end_time = Calendar.getInstance();
        start_time.add(Calendar.HOUR_OF_DAY, -6); //讲起始时间推算为当前时间前n小时

        track_context = this;

        refresh();
        //Log.i("TrackActivity",start_time.get(Calendar.YEAR) + "-" + start_time.get(Calendar.MONTH) + "-" + start_time.get(Calendar.DATE));
        //Log.i("TrackActivity",start_time.get(Calendar.HOUR_OF_DAY) + ":" + start_time.get(Calendar.MINUTE));

        // 高德地图可视化
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        aMapView.onCreate(savedInstanceState);
        //初始化地图控制器对象
        if (amap == null) {
            amap = aMapView.getMap();
        }

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

        AVQuery<AVObject> query = new AVQuery<>("trajectory");
        // 启动查询缓存
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.setMaxCacheAge(24 * 3600 * 1000); //设置缓存为一天，单位毫秒
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
                if(count>QueryMaxNum){
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
                                        initChart(now_list);
                                        List<LatLng> latLngList = GenetateLatLngListFromAvobject(now_list,false);
                                        List<LatLng> latLngList_PathSmooth = GenetateLatLngListFromAvobject(now_list,true);
                                        distanceStatistics(now_list);
                                        // 构建热力图 HeatmapTileProvider
                                        HeatmapTileProvider.Builder builder = new HeatmapTileProvider.Builder();
                                        builder.data(latLngList); // 设置热力图绘制的数据
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
                                                addAll(latLngList_PathSmooth).width(10).color(Color.argb(255, 1, 1, 1)));

                                    }
                                }
                            }
                        });
                    }
                }


            }
        });
//
//        query.findInBackground(new FindCallback<AVObject>() {
//            @Override
//            public void done(List<AVObject> list, AVException e) {
//                if(list!=null&&list.size()>0){
//
//                    initChart(list);
//                    TV_info.setText("开始时间:"+DateFormat.getDateTimeInstance().format(start_time.getTime())+"\n"+
//                            "结束时间: " + DateFormat.getDateTimeInstance().format(end_time.getTime())+"\n"+
//                            "数据总数: "+ list.size());
//                    // 构建热力图 HeatmapTileProvider
//                    HeatmapTileProvider.Builder builder = new HeatmapTileProvider.Builder();
//                    builder.data(Arrays.asList(GenetateLatLngArratFromAvobject(list))); // 设置热力图绘制的数据
//                    // 构造热力图对象
//                    HeatmapTileProvider heatmapTileProvider = builder.build();
//                    // 初始化 TileOverlayOptions
//                    TileOverlayOptions tileOverlayOptions = new TileOverlayOptions();
//                    tileOverlayOptions.tileProvider(heatmapTileProvider); // 设置瓦片图层的提供者
//                    // 向地图上添加 TileOverlayOptions 类对象
//                    amap.addTileOverlay(tileOverlayOptions);
//
//                    // 全幅显示
//                    com.amap.api.maps.model.LatLngBounds bounds = getLatLngBounds(now_list);
//                    amap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
//
//                    // 显示轨迹线
//                    Polyline polyline =amap.addPolyline(new PolylineOptions().
//                            addAll(Arrays.asList(GenetateLatLngArratFromAvobject(list))).width(10).color(Color.argb(255, 1, 1, 1)));
//
//                }
//            }
//        });

//        index = 0;
//        mapView.onCreate(savedInstanceState);
//        mapView.getMapAsync(new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(@NonNull MapboxMap mapboxMap) {
//                mapboxmap = mapboxMap;
//                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
//                    @Override
//                    public void onStyleLoaded(@NonNull Style style) {
//                        // Map is set up and the style has loaded. Now you can add data or make other map adjustments
//                        map_style = style;
//                        CameraPosition cameraPositionForFragmentMap = new CameraPosition.Builder()
//                                .target(new LatLng(34.833774, 113.537698))
//                                .zoom(11.047)
//                                .build();
////                        mapboxMap.animateCamera(
////                                CameraUpdateFactory.newCameraPosition(cameraPositionForFragmentMap), 2600);
//
////                        AVQuery<AVObject> query = new AVQuery<>("trajectory");
////                        // 启动查询缓存
////                        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
////                        query.setMaxCacheAge(24 * 3600 * 1000); //设置为一天，单位毫秒
////                        query.whereEqualTo("UserId", AVUser.getCurrentUser().getObjectId());
////                        query.whereGreaterThan("time",start_time.getTime());
////                        query.whereLessThan("time",end_time.getTime());
////                        query.whereLessThan("precision",50);
////                        query.selectKeys(Arrays.asList("point", "time", "precision"));
////                        query.limit(1000);
////                        query.findInBackground(new FindCallback<AVObject>() {
////                            @Override
////                            public void done(List<AVObject> list, AVException e) {
////                                if(list!=null){
////                                Log.i("TrackActivity","共查询到：" + list.size() + "条数据。");
////                                Toast.makeText(App.getContext(),"共查询到：" + list.size() + "条数据。",Toast.LENGTH_LONG).show();
////                                now_list = list;//暂时存储当前查询结果
////                                TV_info.setText("开始时间:"+DateFormat.getDateTimeInstance().format(start_time.getTime())+"\n"+
////                                        "结束时间: " + DateFormat.getDateTimeInstance().format(end_time.getTime())+"\n"+
////                                        "数据总数: "+ list.size());
////
////                                map_style.removeSource(HEATMAP_SOURCE_ID);
////                                map_style.addSource(new GeoJsonSource(HEATMAP_SOURCE_ID,
////                                        FeatureCollection.fromFeatures(genetateGeoStringFromAvobject(list))));
////                                CreateLineLayer(genetatePointsFromAvobject(list));//创建线
////                            }
////                            }
////                        });
////                        initHeatmapColors();
////                        initHeatmapRadiusStops();
////                        initHeatmapIntensityStops();
////                        addHeatmapLayer(style);
//                    }
//                });
//            }
//        });

    }

    private void distanceStatistics(List<AVObject> list){
        float sumDistance = 0;
        for (int i=0;i<list.size();i++){
            if(i!=0){
                if(list.get(i).getInt("precision")>=30){
                    // 删除条目
                    list.remove(i);
                    i--;
                }else{
                    AVGeoPoint geopoint1 = list.get(i-1).getAVGeoPoint("point");
                    AVGeoPoint geopoint2 = list.get(i).getAVGeoPoint("point");
                    LatLng latLng1 = new com.amap.api.maps.model.LatLng(geopoint1.getLatitude(), geopoint1.getLongitude());
                    LatLng latLng2 = new com.amap.api.maps.model.LatLng(geopoint2.getLatitude(), geopoint2.getLongitude());
                    sumDistance += AMapUtils.calculateLineDistance(latLng1,latLng2);
                }
            }else{
                if(list.get(i).getInt("precision")>=30){
                    // 删除条目，重新计数
                    list.remove(i);
                    i=0;
                }
            }
        }
        String text = "开始时间:"+DateFormat.getDateTimeInstance().format(start_time.getTime())+"\n"+
                "结束时间: " + DateFormat.getDateTimeInstance().format(end_time.getTime())+"\n"+
                "总长度: "+ sumDistance + "米" + "\n"+
                "数据总数: "+ now_list.size();
        TV_info.setText(text);
    }

    private void initChart(List<AVObject> list){
        chart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(true); //绘制标签


        ValueFormatter valueFormatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int value_int = (int)value;
                switch (value_int){
                    case 0: return "1~30";
                    case 1: return "30~60";
                    case 2: return "60~100";
                    case 3: return "100~200";
                    case 4: return "200~300";
                    case 5: return ">300";
                    default: return "";
                }
            }
        };
        xAxis.setValueFormatter(valueFormatter);//设置自定义格式，在绘制之前动态调整x的值。


        chart.getAxisLeft().setDrawGridLines(false);

        // add a nice and smooth animation
        chart.animateY(1500);

        chart.getLegend().setEnabled(false);


        // 设置数据
        ArrayList<BarEntry> values = new ArrayList<>(5);
        int[] PrecisionSum = new int[6]; //初始化为默认值,int型为0

        for (AVObject obj: list){
            int Precision = obj.getInt("precision");
//            Log.i("TrackActivity","处理精度："+Precision);
            if(Precision>1 && Precision<=30)
                PrecisionSum[0]++;
            else if(Precision>30 && Precision<=60)
                PrecisionSum[1]++;
            else if(Precision>60 && Precision<=100)
                PrecisionSum[2]++;
            else if(Precision>100 && Precision<=200)
                PrecisionSum[3]++;
            else if(Precision>200 && Precision<=300)
                PrecisionSum[4]++;
            else
                PrecisionSum[5]++;
        }


        for(int i=0;i<6;i++){
            values.add(new BarEntry(i,PrecisionSum[i]));
        }

        BarDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(values, "Data Set");
            set1.setColors(ColorTemplate.VORDIPLOM_COLORS);
            set1.setDrawValues(false);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

//            ArrayList<String> xVals = new ArrayList<String>();
//            xVals.add("1.Q"); xVals.add("2.Q"); xVals.add("3.Q"); xVals.add("4.Q");

            BarData data = new BarData(dataSets);
            chart.setData(data);
            chart.setFitBars(true);
        }

        chart.invalidate();

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.card_activity_track_info_button_quare:
                mapfresh();
                break;
            case R.id.card_activity_track_info_button_save:
                //以当前时间命名地图
                String start_time_string = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS")).format(start_time.getTime());
                String end_time_string = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS")).format(end_time.getTime());
                SaveToMymap(now_list,start_time_string+" 至 "+end_time_string + "轨迹点");
                break;
            case R.id.card_activity_track_info_button_theme:
                Intent intent = new Intent();
//                AVObjectListDataParcelableSend avObjectListDataParcelableSend = new AVObjectListDataParcelableSend(now_list);
//                Bundle bundle = new Bundle();
                //Parcelable 序列化
//                bundle.putParcelable("TrackList", avObjectListDataParcelableSend);
//                intent.putExtras(bundle);
                Date start_time_date = start_time.getTime(); // 从一个 Calendar 对象中获取 Date 对象
                Date end_time_date = end_time.getTime(); // 从一个 Calendar 对象中获取 Date 对象
                long start_time_long = start_time_date.getTime();
                long end_time_long = end_time_date.getTime();
                com.hgo.planassistant.tools.DateFormat dateFormat = new com.hgo.planassistant.tools.DateFormat(start_time);
                intent.putExtra("start_time", start_time_long);
                intent.putExtra("end_time", end_time_long);
                Log.i("TrackDetailMapActivity","发送开始时间："+ dateFormat.GetDetailDescription());
                Log.i("TrackDetailMapActivity","发送结束时间："+ dateFormat.GetDetailDescription(end_time));
                intent.setClass(this, TrackDetailMapActivity.class);
                startActivity(intent);
//                index++;
//                if (index == listOfHeatmapColors.length - 1) {
//                    index = 0;
//                }
//                if (map_style.getLayer(HEATMAP_LAYER_ID) != null) {
//                    map_style.getLayer(HEATMAP_LAYER_ID).setProperties(
//                            heatmapColor(listOfHeatmapColors[index]),
//                            heatmapRadius(listOfHeatmapRadiusStops[index]),
//                            heatmapIntensity(listOfHeatmapIntensityStops[index])
//                    );
//                }
                break;
            case R.id.card_activity_track_info_start_calendar:
                DatePickerDialog start_datePickerDialog = new DatePickerDialog(this, (view1, year, monthOfYear, dayOfMonth) -> {
                    start_time.set(Calendar.YEAR,year);
                    start_time.set(Calendar.MONTH,monthOfYear);
                    start_time.set(Calendar.DATE,dayOfMonth);
//                    monthOfYear++;//月份＋１
                    Log.i("TrackActivity",start_time.get(Calendar.YEAR) + "-" + (start_time.get(Calendar.MONTH)+1) + "-" + start_time.get(Calendar.DATE));
                    TV_start_calendar.setText(year+"年"+(monthOfYear+1)+"月"+dayOfMonth+"日");
                    refresh();
                    }, start_time.get(Calendar.YEAR), start_time.get(Calendar.MONTH), start_time.get(Calendar.DAY_OF_MONTH));
                start_datePickerDialog.show();
                break;
            case R.id.card_activity_track_info_start_time:
                TimePickerDialog start_timePickerDialog = new TimePickerDialog(this,(view1, hour, minute) -> {
                    start_time.set(Calendar.HOUR_OF_DAY,hour);
                    start_time.set(Calendar.MINUTE,minute);
                    Log.i("TrackActivity",start_time.get(Calendar.HOUR_OF_DAY) + ":" + start_time.get(Calendar.MINUTE));
                    TV_start_time.setText(hour+" 时 "+minute +"分");
                    refresh();
                }, start_time.get(Calendar.HOUR_OF_DAY), start_time.get(Calendar.MINUTE),true);
                start_timePickerDialog.show();
                break;
            case R.id.card_activity_track_info_end_calendar:
                DatePickerDialog end_datePickerDialog = new DatePickerDialog(this, (view1, year, monthOfYear, dayOfMonth) -> {
                    end_time.set(Calendar.YEAR,year);
                    end_time.set(Calendar.MONTH,monthOfYear);
                    end_time.set(Calendar.DATE,dayOfMonth);
//                    monthOfYear++;//月份＋１
                    Log.i("TrackActivity",end_time.get(Calendar.YEAR) + "-" + (end_time.get(Calendar.MONTH)+1) + "-" + end_time.get(Calendar.DATE));
                    TV_stop_calendar.setText(year+"年"+(monthOfYear+1)+"月"+dayOfMonth+"日");
                    refresh();
                }, end_time.get(Calendar.YEAR), end_time.get(Calendar.MONTH), end_time.get(Calendar.DAY_OF_MONTH));
                end_datePickerDialog.show();
                break;
            case R.id.ccard_activity_track_info_end_calendar_time:
                TimePickerDialog end_timePickerDialog = new TimePickerDialog(this,(view1, hour, minute) -> {
                    end_time.set(Calendar.HOUR_OF_DAY,hour);
                    end_time.set(Calendar.MINUTE,minute);
                    Log.i("TrackActivity",end_time.get(Calendar.HOUR_OF_DAY) + ":" + end_time.get(Calendar.MINUTE));
                    TV_stop_time.setText(hour+" 时 "+minute +"分");
                    refresh();
                }, end_time.get(Calendar.HOUR_OF_DAY), end_time.get(Calendar.MINUTE),true);
                end_timePickerDialog.show();
                break;
            case R.id.card_track_amapView:
//                Intent intent = new Intent();
//                intent.setClass(this, TrackDetailMapActivity.class);
//                startActivity(intent);
                break;
        }
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
    private void mapfresh(){

        amap.clear();

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
                if(count>QueryMaxNum){
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
                                        initChart(now_list);
                                        List<LatLng> latLngList = GenetateLatLngListFromAvobject(now_list,false);
                                        List<LatLng> latLngList_PathSmooth = GenetateLatLngListFromAvobject(now_list,true);
                                        distanceStatistics(now_list);

                                        // 构建热力图 HeatmapTileProvider
                                        HeatmapTileProvider.Builder builder = new HeatmapTileProvider.Builder();
                                        builder.data(latLngList); // 设置热力图绘制的数据
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
                                                addAll(latLngList_PathSmooth).width(10).color(Color.argb(255, 1, 1, 1)));

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
//                    now_list = list;//暂时存储当前查询结果
//                    TV_info.setText("开始时间:"+DateFormat.getDateTimeInstance().format(start_time.getTime())+"\n"+
//                            "结束时间: " + DateFormat.getDateTimeInstance().format(end_time.getTime())+"\n"+
//                            "数据总数: "+ list.size());
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
//                    // 显示轨迹线
//                    Polyline polyline =amap.addPolyline(new PolylineOptions().
//                            addAll(Arrays.asList(GenetateLatLngArratFromAvobject(list))).width(20).color(Color.argb(255, 1, 1, 1)));
//
//                    // 全幅显示
//                    com.amap.api.maps.model.LatLngBounds bounds = getLatLngBounds(now_list);
//                    amap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
//
//
//                }
//            }
//        });

//        mapboxmap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
//            @Override
//            public void onStyleLoaded(@NonNull Style style) {
//                // Map is set up and the style has loaded. Now you can add data or make other map adjustments
//                map_style = style;
//                CameraPosition cameraPositionForFragmentMap = new CameraPosition.Builder()
//                        .target(new LatLng(34.833774, 113.537698))
//                        .zoom(11.047)
//                        .build();
//                mapboxmap.animateCamera(
//                        CameraUpdateFactory.newCameraPosition(cameraPositionForFragmentMap), 2600);
//                AVQuery<AVObject> query = new AVQuery<>("trajectory");
//                query.whereEqualTo("UserId", AVUser.getCurrentUser().getObjectId());
//                query.whereGreaterThan("time",start_time.getTime());
//                query.whereLessThan("time",end_time.getTime());
//                query.whereLessThan("precision",50);
//                query.selectKeys(Arrays.asList("point", "time", "precision"));
//                query.limit(1000);
//                query.findInBackground(new FindCallback<AVObject>() {
//                    @Override
//                    public void done(List<AVObject> list, AVException e) {
//                        Log.i("TrackActivity","共查询到：" + list.size() + "条数据。");
//                        Toast.makeText(App.getContext(),"共查询到：" + list.size() + "条数据。",Toast.LENGTH_LONG).show();
//
//                        now_list = list;//暂时存储当前查询结果
//
//                        TV_info.setText("开始时间:"+DateFormat.getDateTimeInstance().format(start_time.getTime())+"\n"+
//                                "结束时间: " + DateFormat.getDateTimeInstance().format(end_time.getTime())+"\n"+
//                                "数据总数: "+ list.size());
//
//                        map_style.removeSource(HEATMAP_SOURCE_ID);
//                        map_style.addSource(new GeoJsonSource(HEATMAP_SOURCE_ID,
//                                FeatureCollection.fromFeatures(genetateGeoStringFromAvobject(list))));
//                        CreateLineLayer(genetatePointsFromAvobject(list));//创建线
////                        for (AVObject obj: list){
//////                            AVObject point = obj.getAVObject("point");
////                            AVGeoPoint geopoint = obj.getAVGeoPoint("point");
//////                            Log.i("TrackActivity",geopoint.toString());
////                            mapboxMap.addMarker(new MarkerOptions()
////                                    .position(new LatLng(geopoint.getLatitude(),geopoint.getLongitude())));
////
////                        }
//                    }
//                });
//                initHeatmapColors();
//                initHeatmapRadiusStops();
//                initHeatmapIntensityStops();
//                addHeatmapLayer(style);
//            }
//        });

    }


    // 计算方位角pab
    private double CaculateAzimuth(double lat_a, double lng_a, double lat_b, double lng_b) {
        double d = 0;
        lat_a=lat_a*Math.PI/180;
        lng_a=lng_a*Math.PI/180;
        lat_b=lat_b*Math.PI/180;
        lng_b=lng_b*Math.PI/180;

        d=Math.sin(lat_a)*Math.sin(lat_b)+Math.cos(lat_a)*Math.cos(lat_b)*Math.cos(lng_b-lng_a);
        d=Math.sqrt(1-d*d);
        d=Math.cos(lat_b)*Math.sin(lng_b-lng_a)/d;
        d=Math.asin(d)*180/Math.PI;
//     d = Math.round(d*10000);
        return d;
    }


//    public List<com.amap.api.maps.model.LatLng> optimizePoints(List<com.amap.api.maps.model.LatLng> inPoint) {
//        int size = inPoint.size();
//        List<com.amap.api.maps.model.LatLng> outPoint;
//
//        int i;
//        if (size < 5) {
//            return inPoint;
//        } else {
//            // Latitude
//            inPoint.get(0).latitude = ((3.0 * inPoint.get(0).latitude + 2.0 * inPoint.get(1).latitude + inPoint.get(2).latitude - inPoint.get(4).latitude) / 5.0);
//            inPoint.get(0).l
//            inPoint.get(1)
//                    .setLat((4.0 * inPoint.get(0).getLat() + 3.0
//                            * inPoint.get(1).getLat() + 2
//                            * inPoint.get(2).getLat() + inPoint.get(3).getLat()) / 10.0);
//
//            inPoint.get(size - 2).setLat(
//                    (4.0 * inPoint.get(size - 1).getLat() + 3.0
//                            * inPoint.get(size - 2).getLat() + 2
//                            * inPoint.get(size - 3).getLat() + inPoint.get(
//                            size - 4).getLat()) / 10.0);
//            inPoint.get(size - 1).setLat(
//                    (3.0 * inPoint.get(size - 1).getLat() + 2.0
//                            * inPoint.get(size - 2).getLat()
//                            + inPoint.get(size - 3).getLat() - inPoint.get(
//                            size - 5).getLat()) / 5.0);
//
//            // Longitude
//            inPoint.get(0)
//                    .setLng((3.0 * inPoint.get(0).getLng() + 2.0
//                            * inPoint.get(1).getLng() + inPoint.get(2).getLng() - inPoint
//                            .get(4).getLng()) / 5.0);
//            inPoint.get(1)
//                    .setLng((4.0 * inPoint.get(0).getLng() + 3.0
//                            * inPoint.get(1).getLng() + 2
//                            * inPoint.get(2).getLng() + inPoint.get(3).getLng()) / 10.0);
//
//            inPoint.get(size - 2).setLng(
//                    (4.0 * inPoint.get(size - 1).getLng() + 3.0
//                            * inPoint.get(size - 2).getLng() + 2
//                            * inPoint.get(size - 3).getLng() + inPoint.get(
//                            size - 4).getLng()) / 10.0);
//            inPoint.get(size - 1).setLng(
//                    (3.0 * inPoint.get(size - 1).getLng() + 2.0
//                            * inPoint.get(size - 2).getLng()
//                            + inPoint.get(size - 3).getLng() - inPoint.get(
//                            size - 5).getLng()) / 5.0);
//        }
//        return inPoint;
//    }

    private void refresh(){
        TV_start_calendar.setText(start_time.get(Calendar.YEAR)+"年"+(start_time.get(Calendar.MONTH)+1)+"月"+start_time.get(Calendar.DATE)+"日");
        TV_start_time.setText(start_time.get(Calendar.HOUR_OF_DAY)+" 时 "+start_time.get(Calendar.MINUTE) +"分");
        TV_stop_calendar.setText(end_time.get(Calendar.YEAR)+"年"+(end_time.get(Calendar.MONTH)+1)+"月"+end_time.get(Calendar.DATE)+"日");
        TV_stop_time.setText(end_time.get(Calendar.HOUR_OF_DAY)+" 时 "+end_time.get(Calendar.MINUTE) +"分");
    }
//    private Feature[] genetateGeoStringFromAvobject(List<AVObject> list){
//        if(list!=null){
//        Feature[] features = new Feature[list.size()];
//        int i=0;
//
//        LatLngBounds.Builder latLngBoundsBuilder = new LatLngBounds.Builder();
////        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
//        for (AVObject obj: list){
//            AVGeoPoint geopoint = obj.getAVGeoPoint("point");
//            features[i] = Feature.fromGeometry(Point.fromLngLat(
//                    geopoint.getLongitude(),
//                    geopoint.getLatitude()));
//            latLngBoundsBuilder.include(new LatLng(geopoint.getLatitude(),geopoint.getLongitude()));
//            i++;
//        }
//        if(list.size()>2){
//            LatLngBounds latLngBounds = latLngBoundsBuilder.build();//创建边界
////            mapboxmap.easeCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 50), 5000);//全幅显示
//        }
//
//            return features;
//        }
//        return null;
//    }
//    private ArrayList<Point> genetatePointsFromAvobject(List<AVObject> list){
//        ArrayList<Point> routeCoordinates = new ArrayList<Point>();
//
//        for (AVObject obj: list){
//            AVGeoPoint geopoint = obj.getAVGeoPoint("point");
//            routeCoordinates.add(Point.fromLngLat(geopoint.getLongitude(), geopoint.getLatitude()));
//        }
//        Log.i("TrackActivity","为生成线读取到"+routeCoordinates.size()+"条数据");
//        return routeCoordinates;
//    }
    private List<com.amap.api.maps.model.LatLng> GenetateLatLngListFromAvobject(List<AVObject> list, boolean isPathSmooth){
        int sum = list.size();
        List<com.amap.api.maps.model.LatLng> latlngs = new ArrayList<>(sum);

        int i=0;
        for (AVObject obj: list){
            AVGeoPoint geopoint = obj.getAVGeoPoint("point");
            double x = geopoint.getLatitude();
            double y = geopoint.getLongitude();
            latlngs.add(new com.amap.api.maps.model.LatLng(x, y));

            String coordinate = obj.getString("geo_coordinate");
//            Log.i("TrackActivity","当前数据坐标："+coordinate + "数据信息:" + obj);
            if(coordinate.equals(Constant.GPS)){
                // 将WGS-84坐标转换为高德坐标
                CoordinateConverter converter  = new CoordinateConverter(track_context);
                // CoordType.GPS 待转换坐标类型
                converter.from(CoordinateConverter.CoordType.GPS);
                // sourceLatLng待转换坐标点 LatLng类型
                converter.coord(latlngs.get(i));
                // 执行转换操作
                latlngs.set(i,converter.convert());
            }
            i++;
        }

        if(isPathSmooth){
            // 平滑处理
            PathSmoothTool mpathSmoothTool = new PathSmoothTool();
            //设置平滑处理的等级
            mpathSmoothTool.setIntensity(4);;
            return mpathSmoothTool.pathOptimize(latlngs);
        }else{
            return latlngs;
        }


    }

//    private List<com.amap.api.maps.model.LatLng> GenetateLatLngListFromAvobject(List<AVObject> list){
//        int sum = list.size();
//        List<com.amap.api.maps.model.LatLng> latlngs = new ArrayList<>(sum);
//
//        int i=0;
//        for (AVObject obj: list){
//            AVGeoPoint geopoint = obj.getAVGeoPoint("point");
//            double x = geopoint.getLatitude();
//            double y = geopoint.getLongitude();
//            latlngs.add(new com.amap.api.maps.model.LatLng(x, y));
//
//            String coordinate = obj.getString("geo_coordinate");
//            if(coordinate.equals(Constant.GPS)){
//                // 将WGS-84坐标转换为高德坐标
//                CoordinateConverter converter  = new CoordinateConverter(track_context);
//                // CoordType.GPS 待转换坐标类型
//                converter.from(CoordinateConverter.CoordType.GPS);
//                // sourceLatLng待转换坐标点 LatLng类型
//                converter.coord(latlngs.get(i));
//                // 执行转换操作
//                latlngs.set(i,converter.convert());
//            }
//
//            i++;
//        }
//
//        return latlngs;
//    }



//    private void CreateLineLayer(ArrayList<Point> routeCoordinates){
//        // Create the LineString from the list of coordinates and then make a GeoJSON
//        // FeatureCollection so we can add the line to our map as a layer.
//
//        // 尝试使用高德地图轨迹纠偏api进行纠偏
//        final String URL = "https://restapi.amap.com/v4/grasproad/driving" ;
////
////        try {
////            HttpPost request = new HttpPost(URL);                       // 提交路径
////            List<NameValuePair> params = new ArrayList<NameValuePair>();// 设置提交参数
////            params.add(new BasicNameValuePair("id", "100"));    // 设置id参数
////            params.add(new BasicNameValuePair("password", "111111"));// 设置password参数
////            request.setEntity(new UrlEncodedFormEntity(params,
////                    HTTP.UTF_8));                                       // 设置编码
////            HttpResponse response = new DefaultHttpClient()
////                    .execute(request);                                      // 接收回应
////            if (response.getStatusLine().getStatusCode() != 404) {      // 请求正常
////                flag = Boolean.parseBoolean(EntityUtils.toString(
////                        response.getEntity()).trim());                  // 接收返回的信息
////            }
////        } catch (Exception e) {
////            e.printStackTrace() ;
////            info.setText("WEB服务器连接失败。") ;
////        }
//
//
//        LineString lineString = LineString.fromLngLats(routeCoordinates);
//
//        FeatureCollection featureCollection =
//                FeatureCollection.fromFeatures(new Feature[]{Feature.fromGeometry(lineString)});
//
//        Source geoJsonSource = new GeoJsonSource("line-source", featureCollection);
//
//        map_style.addSource(geoJsonSource);
//
//        LineLayer lineLayer = new LineLayer("linelayer", "line-source");
//
//    // The layer properties for our line. This is where we make the line dotted, set the
//    // color, etc.
//        lineLayer.setProperties(
//                PropertyFactory.lineDasharray(new Float[]{0.01f, 2f}),
//                PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
//                PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
//                PropertyFactory.lineWidth(5f),
//                PropertyFactory.lineColor(Color.parseColor("#e55e5e"))
//        );
//
////        map_style.addLayerAbove(lineLayer, HEATMAP_LAYER_ID);
//        map_style.addLayer(lineLayer);
//
//    }
    private void SaveToMymap(List<AVObject> map_list, String map_name){
        // 第一步：创建空的个人地图
        // 第二步：将数据提交到当前的个人地图
        // 第三步：存储当前地图风格

        if(map_list.size()>1000){
            Toast.makeText(track_context,map_name + "数据点过多，请约束精度或时间将数据点数降至1000以下再试！",Toast.LENGTH_LONG).show();
        }else{
            // 构造方法传入的参数，对应的就是控制台中的 Class Name
            AVObject mymap = new AVObject("personalmap");
//        AVObject mappoint = new AVObject("mappoint");
            ArrayList<AVObject> mappoints = new ArrayList<AVObject>();

            // no.1
            mymap.put("name",map_name);//地图名称
            mymap.put("UserId",AVUser.getCurrentUser().getObjectId());//用户编号
            mymap.put("mapstyle_index",0);//风格编号
            mymap.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        // 存储成功
//                    Toast.makeText(track_context,map_name + "地图创建成功!",Toast.LENGTH_LONG).show();

                        for (AVObject mappoint : map_list) {
                            AVObject point = new AVObject("mappoint");
                            point.put("point",mappoint.getAVGeoPoint("point"));
                            point.put("altutude",mappoint.get("altitude"));
                            point.put("MapId",mymap.getObjectId());
                            mappoints.add(point);
                        }

                        AVObject.saveAllInBackground(mappoints, new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e != null) {
                                    // 出现错误
                                    mymap.deleteInBackground();
                                    Toast.makeText(track_context,"地图创建失败!\n 失败原因: "+e.toString(),Toast.LENGTH_LONG).show();
                                } else {
                                    // 保存成功
//                                Toast.makeText(track_context,"地图存储成功!",Toast.LENGTH_LONG).show();
                                }
                            }
                        });


                    } else {
                        // 失败的话，请检查网络环境以及 SDK 配置是否正确
                        Toast.makeText(track_context,map_name + "地图创建失败!\n 失败原因: "+e.toString(),Toast.LENGTH_LONG).show();
                    }
                }
            });

//        ArrayList<AVObject> save_mappoints = (ArrayList<AVObject>) map_list;




//        for (AVObject obj: map_list){
//            AVGeoPoint geopoint = obj.getAVGeoPoint("point");
//            geopoint.getLongitude();
//            geopoint.getLatitude();
//
//
//
//        }
        }
    }
    private void RagesQuare(List<AVObject> list){
        int i=0;
        double range_num = 0.2;

//        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
        Log.i("TrackActivity","开始分析时空点个数");
        for (AVObject obj: list){
            //查询指定范围内个数，并更新到字段ranges中
            AVGeoPoint geopoint = obj.getAVGeoPoint("point");
            AVQuery<AVObject> query = new AVQuery<>("trajectory");
            AVGeoPoint point = new AVGeoPoint(geopoint.getLatitude(), geopoint.getLongitude());
            query.limit(1000); //最多为1000
            query.whereGreaterThan("time",start_time.getTime());
            query.whereLessThan("time",end_time.getTime());
            query.whereWithinKilometers("point", point, range_num);//查询范围
            // 得到点总个数
            query.countInBackground(new CountCallback() {
                @Override
                public void done(int i, AVException e) {
                    if (e == null) {
                        // 查询成功，输出计数
//                        Log.d("TrackActivity", "该点"+ range_num +"范围内共有" + i + "个点.");
                        // 第一参数是 className,第二个参数是 objectId
                        AVObject point_range = AVObject.createWithoutData("trajectory", obj.getObjectId());
                        // 修改 content
                        point_range.put("ranges",i);
                        // 保存到云端
                        point_range.saveInBackground();
                    } else {
                        // 查询失败
                    }

                }
            });
        }
//        MarkerQuare();
    }



    private String loadGeoJsonFromAsset(String filename) {
        try {
            // Load GeoJSON file
            InputStream is = getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, "UTF-8");

        } catch (Exception exception) {
//            Timber.e("Exception loading GeoJSON: %s", exception.toString());
            Log.e("TrackActivity","Exception loading GeoJSON: %s"+  exception.toString());
            exception.printStackTrace();
            return null;
        }
    }
//    public void initHeatmapColors() {
//        listOfHeatmapColors = new Expression[] {
//                // 0
//                interpolate(
//                        linear(), heatmapDensity(),
//                        literal(0.01), rgba(0, 0, 0, 0.01),
//                        literal(0.25), rgba(224, 176, 63, 0.5),
//                        literal(0.5), rgb(247, 252, 84),
//                        literal(0.75), rgb(186, 59, 30),
//                        literal(0.9), rgb(255, 0, 0)
//                ),
//                // 1
//                interpolate(
//                        linear(), heatmapDensity(),
//                        literal(0.01), rgba(255, 255, 255, 0.4),
//                        literal(0.25), rgba(4, 179, 183, 1.0),
//                        literal(0.5), rgba(204, 211, 61, 1.0),
//                        literal(0.75), rgba(252, 167, 55, 1.0),
//                        literal(1), rgba(255, 78, 70, 1.0)
//                ),
//                // 2
//                interpolate(
//                        linear(), heatmapDensity(),
//                        literal(0.01), rgba(12, 182, 253, 0.0),
//                        literal(0.25), rgba(87, 17, 229, 0.5),
//                        literal(0.5), rgba(255, 0, 0, 1.0),
//                        literal(0.75), rgba(229, 134, 15, 0.5),
//                        literal(1), rgba(230, 255, 55, 0.6)
//                ),
//                // 3
//                interpolate(
//                        linear(), heatmapDensity(),
//                        literal(0.01), rgba(135, 255, 135, 0.2),
//                        literal(0.5), rgba(255, 99, 0, 0.5),
//                        literal(1), rgba(47, 21, 197, 0.2)
//                ),
//                // 4
//                interpolate(
//                        linear(), heatmapDensity(),
//                        literal(0.01), rgba(4, 0, 0, 0.2),
//                        literal(0.25), rgba(229, 12, 1, 1.0),
//                        literal(0.30), rgba(244, 114, 1, 1.0),
//                        literal(0.40), rgba(255, 205, 12, 1.0),
//                        literal(0.50), rgba(255, 229, 121, 1.0),
//                        literal(1), rgba(255, 253, 244, 1.0)
//                ),
//                // 5
//                interpolate(
//                        linear(), heatmapDensity(),
//                        literal(0.01), rgba(0, 0, 0, 0.01),
//                        literal(0.05), rgba(0, 0, 0, 0.05),
//                        literal(0.4), rgba(254, 142, 2, 0.7),
//                        literal(0.5), rgba(255, 165, 5, 0.8),
//                        literal(0.8), rgba(255, 187, 4, 0.9),
//                        literal(0.95), rgba(255, 228, 173, 0.8),
//                        literal(1), rgba(255, 253, 244, .8)
//                ),
//                //6
//                interpolate(
//                        linear(), heatmapDensity(),
//                        literal(0.01), rgba(0, 0, 0, 0.01),
//                        literal(0.3), rgba(82, 72, 151, 0.4),
//                        literal(0.4), rgba(138, 202, 160, 1.0),
//                        literal(0.5), rgba(246, 139, 76, 0.9),
//                        literal(0.9), rgba(252, 246, 182, 0.8),
//                        literal(1), rgba(255, 255, 255, 0.8)
//                ),
//
//                //7
//                interpolate(
//                        linear(), heatmapDensity(),
//                        literal(0.01), rgba(0, 0, 0, 0.01),
//                        literal(0.1), rgba(0, 2, 114, .1),
//                        literal(0.2), rgba(0, 6, 219, .15),
//                        literal(0.3), rgba(0, 74, 255, .2),
//                        literal(0.4), rgba(0, 202, 255, .25),
//                        literal(0.5), rgba(73, 255, 154, .3),
//                        literal(0.6), rgba(171, 255, 59, .35),
//                        literal(0.7), rgba(255, 197, 3, .4),
//                        literal(0.8), rgba(255, 82, 1, 0.7),
//                        literal(0.9), rgba(196, 0, 1, 0.8),
//                        literal(0.95), rgba(121, 0, 0, 0.8)
//                ),
//                // 8
//                interpolate(
//                        linear(), heatmapDensity(),
//                        literal(0.01), rgba(0, 0, 0, 0.01),
//                        literal(0.1), rgba(0, 2, 114, .1),
//                        literal(0.2), rgba(0, 6, 219, .15),
//                        literal(0.3), rgba(0, 74, 255, .2),
//                        literal(0.4), rgba(0, 202, 255, .25),
//                        literal(0.5), rgba(73, 255, 154, .3),
//                        literal(0.6), rgba(171, 255, 59, .35),
//                        literal(0.7), rgba(255, 197, 3, .4),
//                        literal(0.8), rgba(255, 82, 1, 0.7),
//                        literal(0.9), rgba(196, 0, 1, 0.8),
//                        literal(0.95), rgba(121, 0, 0, 0.8)
//                ),
//                // 9
//                interpolate(
//                        linear(), heatmapDensity(),
//                        literal(0.01), rgba(0, 0, 0, 0.01),
//                        literal(0.1), rgba(0, 2, 114, .1),
//                        literal(0.2), rgba(0, 6, 219, .15),
//                        literal(0.3), rgba(0, 74, 255, .2),
//                        literal(0.4), rgba(0, 202, 255, .25),
//                        literal(0.5), rgba(73, 255, 154, .3),
//                        literal(0.6), rgba(171, 255, 59, .35),
//                        literal(0.7), rgba(255, 197, 3, .4),
//                        literal(0.8), rgba(255, 82, 1, 0.7),
//                        literal(0.9), rgba(196, 0, 1, 0.8),
//                        literal(0.95), rgba(121, 0, 0, 0.8)
//                ),
//                // 10
//                interpolate(
//                        linear(), heatmapDensity(),
//                        literal(0.01), rgba(0, 0, 0, 0.01),
//                        literal(0.1), rgba(0, 2, 114, .1),
//                        literal(0.2), rgba(0, 6, 219, .15),
//                        literal(0.3), rgba(0, 74, 255, .2),
//                        literal(0.4), rgba(0, 202, 255, .25),
//                        literal(0.5), rgba(73, 255, 154, .3),
//                        literal(0.6), rgba(171, 255, 59, .35),
//                        literal(0.7), rgba(255, 197, 3, .4),
//                        literal(0.8), rgba(255, 82, 1, 0.7),
//                        literal(0.9), rgba(196, 0, 1, 0.8),
//                        literal(0.95), rgba(121, 0, 0, 0.8)
//                ),
//                // 11
//                interpolate(
//                        linear(), heatmapDensity(),
//                        literal(0.01), rgba(0, 0, 0, 0.25),
//                        literal(0.25), rgba(229, 12, 1, .7),
//                        literal(0.30), rgba(244, 114, 1, .7),
//                        literal(0.40), rgba(255, 205, 12, .7),
//                        literal(0.50), rgba(255, 229, 121, .8),
//                        literal(1), rgba(255, 253, 244, .8)
//                )
//        };
//    }

//    public void initHeatmapRadiusStops() {
//        listOfHeatmapRadiusStops = new Expression[] {
//                // 0
//                interpolate(
//                        linear(), zoom(),
//                        literal(1), literal(30),
//                        literal(6), literal(60)
//                ),
////                interpolate(
////                        linear(), zoom(),
////                        literal(6), literal(50),
////                        literal(20), literal(100)
////                ),
//                // 1
//                interpolate(
//                        linear(), zoom(),
//                        literal(12), literal(70),
//                        literal(20), literal(100)
//                ),
//                // 2
//                interpolate(
//                        linear(), zoom(),
//                        literal(1), literal(7),
//                        literal(5), literal(50)
//                ),
//                // 3
//                interpolate(
//                        linear(), zoom(),
//                        literal(1), literal(7),
//                        literal(5), literal(50)
//                ),
//                // 4
//                interpolate(
//                        linear(), zoom(),
//                        literal(1), literal(7),
//                        literal(5), literal(50)
//                ),
//                // 5
//                interpolate(
//                        linear(), zoom(),
//                        literal(1), literal(7),
//                        literal(15), literal(200)
//                ),
//                // 6
//                interpolate(
//                        linear(), zoom(),
//                        literal(1), literal(10),
//                        literal(8), literal(70)
//                ),
//                // 7
//                interpolate(
//                        linear(), zoom(),
//                        literal(1), literal(10),
//                        literal(8), literal(200)
//                ),
//                // 8
//                interpolate(
//                        linear(), zoom(),
//                        literal(1), literal(10),
//                        literal(8), literal(200)
//                ),
//                // 9
//                interpolate(
//                        linear(), zoom(),
//                        literal(1), literal(10),
//                        literal(8), literal(200)
//                ),
//                // 10
//                interpolate(
//                        linear(), zoom(),
//                        literal(1), literal(10),
//                        literal(8), literal(200)
//                ),
//                // 11
//                interpolate(
//                        linear(), zoom(),
//                        literal(1), literal(10),
//                        literal(8), literal(200)
//                ),
//        };
//    }

//    public void initHeatmapIntensityStops() {
//        listOfHeatmapIntensityStops = new Float[] {
//                // 0
//                0.06f,
//                // 1
//                0.3f,
//                // 2
//                1f,
//                // 3
//                1f,
//                // 4
//                1f,
//                // 5
//                1f,
//                // 6
//                1.5f,
//                // 7
//                0.8f,
//                // 8
//                0.25f,
//                // 9
//                0.8f,
//                // 10
//                0.25f,
//                // 11
//                0.5f
//        };
//    }
//    public void addHeatmapLayer(@NonNull Style loadedMapStyle) {
//        // Create the heatmap layer
//        HeatmapLayer layer = new HeatmapLayer(HEATMAP_LAYER_ID, HEATMAP_SOURCE_ID);
//
//        // Heatmap layer disappears at whatever zoom level is set as the maximum
//        layer.setMaxZoom(18);
//
//        layer.setProperties(
//                // Color ramp for heatmap.  Domain is 0 (low) to 1 (high).
//                // Begin color ramp at 0-stop with a 0-transparency color to create a blur-like effect.
//                heatmapColor(listOfHeatmapColors[index]),
//
//                // Increase the heatmap color weight weight by zoom level
//                // heatmap-intensity is a multiplier on top of heatmap-weight
//                heatmapIntensity(listOfHeatmapIntensityStops[index]),
//
//                // Adjust the heatmap radius by zoom level
//                heatmapRadius(listOfHeatmapRadiusStops[index]
//                ),
//
//                heatmapOpacity(1f)
//        );
//
//        // Add the heatmap layer to the map and above the "water-label" layer
//        loadedMapStyle.addLayerAbove(layer, "waterway-label");
//    }


}
