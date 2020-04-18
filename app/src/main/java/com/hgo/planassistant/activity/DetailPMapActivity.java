package com.hgo.planassistant.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.hgo.planassistant.App;
import com.hgo.planassistant.R;
import com.hgo.planassistant.custom.HeatMap;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.expressions.Expression;
import com.mapbox.mapboxsdk.style.layers.HeatmapLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.umeng.analytics.MobclickAgent;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.List;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.heatmapColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.heatmapIntensity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.heatmapOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.heatmapRadius;

public class DetailPMapActivity extends BaseActivity {

    private   String HEATMAP_SOURCE_ID = "HEATMAP_SOURCE_ID";
    private   String HEATMAP_LAYER_ID = "HEATMAP_LAYER_ID";
    private Expression[] listOfHeatmapColors;
    private Expression[] listOfHeatmapRadiusStops;
    private Float[] listOfHeatmapIntensityStops;

    private MapView mapView;
    private AVObject mapObject;
    private Context nowActContext;
    private Bundle nowBundle;
    private Toolbar toolbar;
    private Style mapstyle;
    private int style_index;

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
                String mapname = avObject.getString("title");// 读取 title
                if (avObject != null) {
                    mapObject = avObject;
                    style_index = (int)mapObject.get("mapstyle_index");
                    initView();
                }else{
                    Toast.makeText(nowActContext,"拉取数据失败!",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void initView(){

        mapView = findViewById(R.id.activity_dpmap_mapView);
        toolbar = findViewById(R.id.toolbar_detailpmap);
        setToolbar(toolbar);
        toolbar.setTitle(mapObject.get("name").toString());


        mapView.onCreate(nowBundle);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {

//                mapboxMap.addMarker(new MarkerOptions()
//                        .position(new LatLng(34.833774, 113.537698))
//                        .title("Eiffel Tower"));
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        // Map is set up and the style has loaded. Now you can add data or make other map adjustments
                        mapstyle = style;
                        loadMap();
                    }
                });
            }
        });


    }
    private void loadMap(){
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
                mapstyle.removeSource(HEATMAP_SOURCE_ID);
                mapstyle.addSource(new GeoJsonSource(HEATMAP_SOURCE_ID,
                        FeatureCollection.fromFeatures(genetateGeoStringFromAvobject(list))));
            }
        });

        listOfHeatmapColors = HeatMap.initHeatmapColors(listOfHeatmapColors);
        listOfHeatmapRadiusStops = HeatMap.initHeatmapRadiusStops(listOfHeatmapRadiusStops);
        listOfHeatmapIntensityStops = HeatMap.initHeatmapIntensityStops(listOfHeatmapIntensityStops);
        addHeatmapLayer(mapstyle);
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

    public void addHeatmapLayer(@NonNull Style loadedMapStyle) {
        // Create the heatmap layer
        HeatmapLayer layer = new HeatmapLayer(HEATMAP_LAYER_ID, HEATMAP_SOURCE_ID);

        // Heatmap layer disappears at whatever zoom level is set as the maximum
        layer.setMaxZoom(18);

        layer.setProperties(
                // Color ramp for heatmap.  Domain is 0 (low) to 1 (high).
                // Begin color ramp at 0-stop with a 0-transparency color to create a blur-like effect.
                heatmapColor(listOfHeatmapColors[style_index]),

                // Increase the heatmap color weight weight by zoom level
                // heatmap-intensity is a multiplier on top of heatmap-weight
                heatmapIntensity(listOfHeatmapIntensityStops[style_index]),

                // Adjust the heatmap radius by zoom level
                heatmapRadius(listOfHeatmapRadiusStops[style_index]
                ),

                heatmapOpacity(1f)
        );

        // Add the heatmap layer to the map and above the "water-label" layer
        loadedMapStyle.addLayerAbove(layer, "waterway-label");
    }
}
