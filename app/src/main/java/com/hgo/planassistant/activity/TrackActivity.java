package com.hgo.planassistant.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.google.android.material.snackbar.Snackbar;
import com.hgo.planassistant.App;
import com.hgo.planassistant.R;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.expressions.Expression;
import com.mapbox.mapboxsdk.style.layers.HeatmapLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import org.geotools.geojson.geom.GeometryJSON;

import java.io.InputStream;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.mapbox.mapboxsdk.style.expressions.Expression.heatmapDensity;
import static com.mapbox.mapboxsdk.style.expressions.Expression.interpolate;
import static com.mapbox.mapboxsdk.style.expressions.Expression.linear;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.expressions.Expression.rgb;
import static com.mapbox.mapboxsdk.style.expressions.Expression.rgba;
import static com.mapbox.mapboxsdk.style.expressions.Expression.zoom;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.heatmapColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.heatmapIntensity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.heatmapOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.heatmapRadius;

public class TrackActivity extends BaseActivity implements View.OnClickListener{

    private   String HEATMAP_SOURCE_ID = "HEATMAP_SOURCE_ID";
    private   String HEATMAP_LAYER_ID = "HEATMAP_LAYER_ID";
    private Expression[] listOfHeatmapColors;
    private Expression[] listOfHeatmapRadiusStops;
    private Float[] listOfHeatmapIntensityStops;
    private MapView mapView;
    private MapboxMap mapboxmap;
    private Style map_style;
    private int index;

    private Calendar start_time;
    private Calendar end_time;

    private Button BT_save,BT_quare,BT_theme;
    private TextView TV_start_calendar, TV_start_time,TV_stop_calendar,TV_stop_time,TV_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //设置accessToken
        Mapbox.getInstance(App.getContext(), getString(R.string.mapbox_access_token));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        Toolbar toolbar = findViewById(R.id.toolbar_track);
        setToolbar(toolbar);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));

        initView(savedInstanceState);
    }

    void initView(Bundle savedInstanceState){


        mapView = (MapView) findViewById(R.id.card_track_mapView);
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
        start_time.add(Calendar.HOUR_OF_DAY, -12); //讲起始时间推算为当前时间前１２小时

        refresh();
        //Log.i("TrackActivity",start_time.get(Calendar.YEAR) + "-" + start_time.get(Calendar.MONTH) + "-" + start_time.get(Calendar.DATE));
        //Log.i("TrackActivity",start_time.get(Calendar.HOUR_OF_DAY) + ":" + start_time.get(Calendar.MINUTE));

        index = 0;
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                mapboxmap = mapboxMap;
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        // Map is set up and the style has loaded. Now you can add data or make other map adjustments
                        map_style = style;
                        CameraPosition cameraPositionForFragmentMap = new CameraPosition.Builder()
                                .target(new LatLng(34.833774, 113.537698))
                                .zoom(11.047)
                                .build();
                        mapboxMap.animateCamera(
                                CameraUpdateFactory.newCameraPosition(cameraPositionForFragmentMap), 2600);
                        AVQuery<AVObject> query = new AVQuery<>("trajectory");
                        query.whereEqualTo("UserId", AVUser.getCurrentUser().getObjectId());
                        query.whereGreaterThan("time",start_time.getTime());
                        query.whereLessThan("time",end_time.getTime());
                        query.whereLessThan("precision",50);
                        query.selectKeys(Arrays.asList("point", "time", "precision"));
                        query.limit(1000);
                        query.findInBackground(new FindCallback<AVObject>() {
                            @Override
                            public void done(List<AVObject> list, AVException e) {
                                Log.i("TrackActivity","共查询到：" + list.size() + "条数据。");
                                Toast.makeText(App.getContext(),"共查询到：" + list.size() + "条数据。",Toast.LENGTH_LONG).show();

                                TV_info.setText("开始时间:"+DateFormat.getDateTimeInstance().format(start_time.getTime())+"\n"+
                                        "结束时间: " + DateFormat.getDateTimeInstance().format(end_time.getTime())+"\n"+
                                        "数据总数: "+ list.size());

                                map_style.removeSource(HEATMAP_SOURCE_ID);
                                map_style.addSource(new GeoJsonSource(HEATMAP_SOURCE_ID,
                                        FeatureCollection.fromFeatures(genetateGeoStringFromAvobject(list))));
//                        for (AVObject obj: list){
////                            AVObject point = obj.getAVObject("point");
//                            AVGeoPoint geopoint = obj.getAVGeoPoint("point");
////                            Log.i("TrackActivity",geopoint.toString());
//                            mapboxMap.addMarker(new MarkerOptions()
//                                    .position(new LatLng(geopoint.getLatitude(),geopoint.getLongitude())));
//
//                        }
                            }
                        });
                        initHeatmapColors();
                        initHeatmapRadiusStops();
                        initHeatmapIntensityStops();
                        addHeatmapLayer(style);
                    }
                });
            }
        });

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.card_activity_track_info_button_quare:
                mapfresh();
                break;
            case R.id.card_activity_track_info_button_save:
                break;
            case R.id.card_activity_track_info_button_theme:
                index++;
                if (index == listOfHeatmapColors.length - 1) {
                    index = 0;
                }
                if (map_style.getLayer(HEATMAP_LAYER_ID) != null) {
                    map_style.getLayer(HEATMAP_LAYER_ID).setProperties(
                            heatmapColor(listOfHeatmapColors[index]),
                            heatmapRadius(listOfHeatmapRadiusStops[index]),
                            heatmapIntensity(listOfHeatmapIntensityStops[index])
                    );
                }
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
        }
    }
    private void mapfresh(){
        mapboxmap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                // Map is set up and the style has loaded. Now you can add data or make other map adjustments
                map_style = style;
                CameraPosition cameraPositionForFragmentMap = new CameraPosition.Builder()
                        .target(new LatLng(34.833774, 113.537698))
                        .zoom(11.047)
                        .build();
                mapboxmap.animateCamera(
                        CameraUpdateFactory.newCameraPosition(cameraPositionForFragmentMap), 2600);
                AVQuery<AVObject> query = new AVQuery<>("trajectory");
                query.whereEqualTo("UserId", AVUser.getCurrentUser().getObjectId());
                query.whereGreaterThan("time",start_time.getTime());
                query.whereLessThan("time",end_time.getTime());
                query.whereLessThan("precision",50);
                query.selectKeys(Arrays.asList("point", "time", "precision"));
                query.limit(1000);
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        Log.i("TrackActivity","共查询到：" + list.size() + "条数据。");
                        Toast.makeText(App.getContext(),"共查询到：" + list.size() + "条数据。",Toast.LENGTH_LONG).show();

                        TV_info.setText("开始时间:"+DateFormat.getDateTimeInstance().format(start_time.getTime())+"\n"+
                                "结束时间: " + DateFormat.getDateTimeInstance().format(end_time.getTime())+"\n"+
                                "数据总数: "+ list.size());

                        map_style.removeSource(HEATMAP_SOURCE_ID);
                        map_style.addSource(new GeoJsonSource(HEATMAP_SOURCE_ID,
                                FeatureCollection.fromFeatures(genetateGeoStringFromAvobject(list))));
//                        for (AVObject obj: list){
////                            AVObject point = obj.getAVObject("point");
//                            AVGeoPoint geopoint = obj.getAVGeoPoint("point");
////                            Log.i("TrackActivity",geopoint.toString());
//                            mapboxMap.addMarker(new MarkerOptions()
//                                    .position(new LatLng(geopoint.getLatitude(),geopoint.getLongitude())));
//
//                        }
                    }
                });
                initHeatmapColors();
                initHeatmapRadiusStops();
                initHeatmapIntensityStops();
                addHeatmapLayer(style);
            }
        });
//        AVQuery<AVObject> query = new AVQuery<>("trajectory");
//        query.whereEqualTo("UserId", AVUser.getCurrentUser().getObjectId());
//        query.whereGreaterThan("time",start_time.getTime());
//        query.whereLessThan("time",end_time.getTime());
//        query.whereLessThan("precision",50);
//        query.selectKeys(Arrays.asList("point", "time", "precision"));
//        query.limit(1000);
//        query.findInBackground(new FindCallback<AVObject>() {
//            @Override
//            public void done(List<AVObject> list, AVException e) {
//                Log.i("TrackActivity","共查询到：" + list.size() + "条数据。");
//                Toast.makeText(App.getContext(),"共查询到：" + list.size() + "条数据。",Toast.LENGTH_LONG).show();
//
//                map_style.removeSource(HEATMAP_SOURCE_ID);
//                HEATMAP_SOURCE_ID += "_1";
//                map_style.addSource(new GeoJsonSource(HEATMAP_SOURCE_ID,
//                        FeatureCollection.fromFeatures(genetateGeoStringFromAvobject(list))));
////                        for (AVObject obj: list){
//////                            AVObject point = obj.getAVObject("point");
////                            AVGeoPoint geopoint = obj.getAVGeoPoint("point");
//////                            Log.i("TrackActivity",geopoint.toString());
////                            mapboxMap.addMarker(new MarkerOptions()
////                                    .position(new LatLng(geopoint.getLatitude(),geopoint.getLongitude())));
////
////                        }
//            }
//        });
//        mapView.refreshDrawableState();
    }
    private void refresh(){
        TV_start_calendar.setText(start_time.get(Calendar.YEAR)+"年"+(start_time.get(Calendar.MONTH)+1)+"月"+start_time.get(Calendar.DATE)+"日");
        TV_start_time.setText(start_time.get(Calendar.HOUR_OF_DAY)+" 时 "+start_time.get(Calendar.MINUTE) +"分");
        TV_stop_calendar.setText(end_time.get(Calendar.YEAR)+"年"+(end_time.get(Calendar.MONTH)+1)+"月"+end_time.get(Calendar.DATE)+"日");
        TV_stop_time.setText(end_time.get(Calendar.HOUR_OF_DAY)+" 时 "+end_time.get(Calendar.MINUTE) +"分");
    }
    private Feature[] genetateGeoStringFromAvobject(List<AVObject> list){
        Feature[] features = new Feature[list.size()];
        int i=0;
//        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
        for (AVObject obj: list){
            AVGeoPoint geopoint = obj.getAVGeoPoint("point");
            features[i] = Feature.fromGeometry(Point.fromLngLat(
                    geopoint.getLongitude(),
                    geopoint.getLatitude()));
            i++;
        }
        return features;
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
    private void initHeatmapColors() {
        listOfHeatmapColors = new Expression[] {
                // 0
                interpolate(
                        linear(), heatmapDensity(),
                        literal(0.01), rgba(0, 0, 0, 0.01),
                        literal(0.25), rgba(224, 176, 63, 0.5),
                        literal(0.5), rgb(247, 252, 84),
                        literal(0.75), rgb(186, 59, 30),
                        literal(0.9), rgb(255, 0, 0)
                ),
                // 1
                interpolate(
                        linear(), heatmapDensity(),
                        literal(0.01), rgba(255, 255, 255, 0.4),
                        literal(0.25), rgba(4, 179, 183, 1.0),
                        literal(0.5), rgba(204, 211, 61, 1.0),
                        literal(0.75), rgba(252, 167, 55, 1.0),
                        literal(1), rgba(255, 78, 70, 1.0)
                ),
                // 2
                interpolate(
                        linear(), heatmapDensity(),
                        literal(0.01), rgba(12, 182, 253, 0.0),
                        literal(0.25), rgba(87, 17, 229, 0.5),
                        literal(0.5), rgba(255, 0, 0, 1.0),
                        literal(0.75), rgba(229, 134, 15, 0.5),
                        literal(1), rgba(230, 255, 55, 0.6)
                ),
                // 3
                interpolate(
                        linear(), heatmapDensity(),
                        literal(0.01), rgba(135, 255, 135, 0.2),
                        literal(0.5), rgba(255, 99, 0, 0.5),
                        literal(1), rgba(47, 21, 197, 0.2)
                ),
                // 4
                interpolate(
                        linear(), heatmapDensity(),
                        literal(0.01), rgba(4, 0, 0, 0.2),
                        literal(0.25), rgba(229, 12, 1, 1.0),
                        literal(0.30), rgba(244, 114, 1, 1.0),
                        literal(0.40), rgba(255, 205, 12, 1.0),
                        literal(0.50), rgba(255, 229, 121, 1.0),
                        literal(1), rgba(255, 253, 244, 1.0)
                ),
                // 5
                interpolate(
                        linear(), heatmapDensity(),
                        literal(0.01), rgba(0, 0, 0, 0.01),
                        literal(0.05), rgba(0, 0, 0, 0.05),
                        literal(0.4), rgba(254, 142, 2, 0.7),
                        literal(0.5), rgba(255, 165, 5, 0.8),
                        literal(0.8), rgba(255, 187, 4, 0.9),
                        literal(0.95), rgba(255, 228, 173, 0.8),
                        literal(1), rgba(255, 253, 244, .8)
                ),
                //6
                interpolate(
                        linear(), heatmapDensity(),
                        literal(0.01), rgba(0, 0, 0, 0.01),
                        literal(0.3), rgba(82, 72, 151, 0.4),
                        literal(0.4), rgba(138, 202, 160, 1.0),
                        literal(0.5), rgba(246, 139, 76, 0.9),
                        literal(0.9), rgba(252, 246, 182, 0.8),
                        literal(1), rgba(255, 255, 255, 0.8)
                ),

                //7
                interpolate(
                        linear(), heatmapDensity(),
                        literal(0.01), rgba(0, 0, 0, 0.01),
                        literal(0.1), rgba(0, 2, 114, .1),
                        literal(0.2), rgba(0, 6, 219, .15),
                        literal(0.3), rgba(0, 74, 255, .2),
                        literal(0.4), rgba(0, 202, 255, .25),
                        literal(0.5), rgba(73, 255, 154, .3),
                        literal(0.6), rgba(171, 255, 59, .35),
                        literal(0.7), rgba(255, 197, 3, .4),
                        literal(0.8), rgba(255, 82, 1, 0.7),
                        literal(0.9), rgba(196, 0, 1, 0.8),
                        literal(0.95), rgba(121, 0, 0, 0.8)
                ),
                // 8
                interpolate(
                        linear(), heatmapDensity(),
                        literal(0.01), rgba(0, 0, 0, 0.01),
                        literal(0.1), rgba(0, 2, 114, .1),
                        literal(0.2), rgba(0, 6, 219, .15),
                        literal(0.3), rgba(0, 74, 255, .2),
                        literal(0.4), rgba(0, 202, 255, .25),
                        literal(0.5), rgba(73, 255, 154, .3),
                        literal(0.6), rgba(171, 255, 59, .35),
                        literal(0.7), rgba(255, 197, 3, .4),
                        literal(0.8), rgba(255, 82, 1, 0.7),
                        literal(0.9), rgba(196, 0, 1, 0.8),
                        literal(0.95), rgba(121, 0, 0, 0.8)
                ),
                // 9
                interpolate(
                        linear(), heatmapDensity(),
                        literal(0.01), rgba(0, 0, 0, 0.01),
                        literal(0.1), rgba(0, 2, 114, .1),
                        literal(0.2), rgba(0, 6, 219, .15),
                        literal(0.3), rgba(0, 74, 255, .2),
                        literal(0.4), rgba(0, 202, 255, .25),
                        literal(0.5), rgba(73, 255, 154, .3),
                        literal(0.6), rgba(171, 255, 59, .35),
                        literal(0.7), rgba(255, 197, 3, .4),
                        literal(0.8), rgba(255, 82, 1, 0.7),
                        literal(0.9), rgba(196, 0, 1, 0.8),
                        literal(0.95), rgba(121, 0, 0, 0.8)
                ),
                // 10
                interpolate(
                        linear(), heatmapDensity(),
                        literal(0.01), rgba(0, 0, 0, 0.01),
                        literal(0.1), rgba(0, 2, 114, .1),
                        literal(0.2), rgba(0, 6, 219, .15),
                        literal(0.3), rgba(0, 74, 255, .2),
                        literal(0.4), rgba(0, 202, 255, .25),
                        literal(0.5), rgba(73, 255, 154, .3),
                        literal(0.6), rgba(171, 255, 59, .35),
                        literal(0.7), rgba(255, 197, 3, .4),
                        literal(0.8), rgba(255, 82, 1, 0.7),
                        literal(0.9), rgba(196, 0, 1, 0.8),
                        literal(0.95), rgba(121, 0, 0, 0.8)
                ),
                // 11
                interpolate(
                        linear(), heatmapDensity(),
                        literal(0.01), rgba(0, 0, 0, 0.25),
                        literal(0.25), rgba(229, 12, 1, .7),
                        literal(0.30), rgba(244, 114, 1, .7),
                        literal(0.40), rgba(255, 205, 12, .7),
                        literal(0.50), rgba(255, 229, 121, .8),
                        literal(1), rgba(255, 253, 244, .8)
                )
        };
    }

    private void initHeatmapRadiusStops() {
        listOfHeatmapRadiusStops = new Expression[] {
                // 0
                interpolate(
                        linear(), zoom(),
                        literal(6), literal(50),
                        literal(20), literal(100)
                ),
                // 1
                interpolate(
                        linear(), zoom(),
                        literal(12), literal(70),
                        literal(20), literal(100)
                ),
                // 2
                interpolate(
                        linear(), zoom(),
                        literal(1), literal(7),
                        literal(5), literal(50)
                ),
                // 3
                interpolate(
                        linear(), zoom(),
                        literal(1), literal(7),
                        literal(5), literal(50)
                ),
                // 4
                interpolate(
                        linear(), zoom(),
                        literal(1), literal(7),
                        literal(5), literal(50)
                ),
                // 5
                interpolate(
                        linear(), zoom(),
                        literal(1), literal(7),
                        literal(15), literal(200)
                ),
                // 6
                interpolate(
                        linear(), zoom(),
                        literal(1), literal(10),
                        literal(8), literal(70)
                ),
                // 7
                interpolate(
                        linear(), zoom(),
                        literal(1), literal(10),
                        literal(8), literal(200)
                ),
                // 8
                interpolate(
                        linear(), zoom(),
                        literal(1), literal(10),
                        literal(8), literal(200)
                ),
                // 9
                interpolate(
                        linear(), zoom(),
                        literal(1), literal(10),
                        literal(8), literal(200)
                ),
                // 10
                interpolate(
                        linear(), zoom(),
                        literal(1), literal(10),
                        literal(8), literal(200)
                ),
                // 11
                interpolate(
                        linear(), zoom(),
                        literal(1), literal(10),
                        literal(8), literal(200)
                ),
        };
    }

    private void initHeatmapIntensityStops() {
        listOfHeatmapIntensityStops = new Float[] {
                // 0
                0.06f,
                // 1
                0.3f,
                // 2
                1f,
                // 3
                1f,
                // 4
                1f,
                // 5
                1f,
                // 6
                1.5f,
                // 7
                0.8f,
                // 8
                0.25f,
                // 9
                0.8f,
                // 10
                0.25f,
                // 11
                0.5f
        };
    }
    private void addHeatmapLayer(@NonNull Style loadedMapStyle) {
        // Create the heatmap layer
        HeatmapLayer layer = new HeatmapLayer(HEATMAP_LAYER_ID, HEATMAP_SOURCE_ID);

        // Heatmap layer disappears at whatever zoom level is set as the maximum
        layer.setMaxZoom(18);

        layer.setProperties(
                // Color ramp for heatmap.  Domain is 0 (low) to 1 (high).
                // Begin color ramp at 0-stop with a 0-transparency color to create a blur-like effect.
                heatmapColor(listOfHeatmapColors[index]),

                // Increase the heatmap color weight weight by zoom level
                // heatmap-intensity is a multiplier on top of heatmap-weight
                heatmapIntensity(listOfHeatmapIntensityStops[index]),

                // Adjust the heatmap radius by zoom level
                heatmapRadius(listOfHeatmapRadiusStops[index]
                ),

                heatmapOpacity(1f)
        );

        // Add the heatmap layer to the map and above the "water-label" layer
        loadedMapStyle.addLayerAbove(layer, "waterway-label");
    }


}
