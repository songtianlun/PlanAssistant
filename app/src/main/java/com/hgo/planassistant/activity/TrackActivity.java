package com.hgo.planassistant.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.hgo.planassistant.App;
import com.hgo.planassistant.R;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import java.util.List;

public class TrackActivity extends AppCompatActivity {

    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //设置accessToken
        Mapbox.getInstance(App.getContext(), getString(R.string.mapbox_access_token));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        initView(savedInstanceState);
    }

    void initView(Bundle savedInstanceState){
        mapView = (MapView) findViewById(R.id.card_track_mapView);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {

                AVQuery<AVObject> query = new AVQuery<>("trajectory");
                query.whereEqualTo("UserId", AVUser.getCurrentUser().getObjectId());
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        for (AVObject obj: list){
//                            AVObject point = obj.getAVObject("point");
                            AVGeoPoint geopoint = obj.getAVGeoPoint("point");

                            Log.i("TrackActivity",geopoint.toString());
                            mapboxMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(geopoint.getLatitude(),geopoint.getLongitude())));
                            mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                                @Override
                                public void onStyleLoaded(@NonNull Style style) {

                                    // Map is set up and the style has loaded. Now you can add data or make other map adjustments

                                }
                            });
                        }

                    }
                });

                mapboxMap.addMarker(new MarkerOptions()
                        .position(new LatLng(34.833774, 113.537698))
                        .title("Eiffel Tower"));
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

                        // Map is set up and the style has loaded. Now you can add data or make other map adjustments

                    }
                });
            }
        });
    }
}
