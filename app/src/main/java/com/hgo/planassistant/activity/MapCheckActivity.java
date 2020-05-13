package com.hgo.planassistant.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.alibaba.fastjson.JSONObject;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hgo.planassistant.App;
import com.hgo.planassistant.R;
import com.hgo.planassistant.adapter.MapCheckRecyclerViewAdapter;
import com.hgo.planassistant.adapter.MyMapRecyclerViewAdapter;
import com.hgo.planassistant.timeline.RecyclerAdapter;
import com.hgo.planassistant.timeline.TaskItem;
import com.hgo.planassistant.util.AppUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MapCheckActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MapCheckRecyclerViewAdapter adapter;
    private Context context;
    private Bundle bundle;
    private List<AVObject> location_list;
    private boolean loading;
    private int loadTimes;
    private AMap amap;
    private MapView aMapview;

    SharedPreferences SP_temporary;
    SharedPreferences.Editor SP_temporary_editor;

    private ExtendedFloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_check);

        context=this;
        bundle=savedInstanceState;
        Toolbar toolbar = findViewById(R.id.toolbar_map_check_view);
        setToolbar(toolbar);

        initMap();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        MobclickAgent.onResume(this); // umeng+ 统计 //AUTO页面采集模式下不调用

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        String poi_item = SP_temporary.getString("location_poi_item","");
        Log.d("TaskAddActivity","收到序列化的poi："+poi_item);

        if(poi_item.length()>0){
            JSONObject jsonObject = JSONObject.parseObject(poi_item); //反序列化
            Log.d("TaskAddActivity","接收到poi数据"+jsonObject);

            AVObject track_record = new AVObject("MapCheck");
            track_record.put("UserId", AVUser.getCurrentUser().getObjectId());// 设置用户ID
//            track_record.put("time",new Date()); //设置时间戳
            track_record.put("snippet", jsonObject.getString("snippet"));
            track_record.put("title", jsonObject.getString("title"));
            track_record.put("cityName", jsonObject.getString("cityName"));
            track_record.put("poiId", jsonObject.getString("poiId"));
            track_record.put("typeDes", jsonObject.getString("typeDes"));
            track_record.put("direction", jsonObject.getString("direction"));
            track_record.put("adName", jsonObject.getString("adName"));
            track_record.put("provinceCode", jsonObject.getString("provinceCode"));
            track_record.put("postcode", jsonObject.getString("postcode"));
            track_record.put("typeCode", jsonObject.getString("typeCode"));
            track_record.put("businessArea", jsonObject.getString("businessArea"));
            track_record.put("provinceName", jsonObject.getString("provinceName"));
            track_record.put("shopID", jsonObject.getString("shopID"));
            track_record.put("cityCode", jsonObject.getString("cityCode"));
            JSONObject latlon_json = jsonObject.getJSONObject("latLonPoint");
            track_record.put("latLonPoint", new AVGeoPoint(latlon_json.getDouble("latitude"), latlon_json.getDouble("longitude")));
            track_record.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    initData();
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPause(this);  // umeng+ 统计 //AUTO页面采集模式下不调用
    }

    private void initMap(){
        aMapview = findViewById(R.id.activity_map_check_map);
        aMapview.onCreate(bundle); // 此方法须覆写，虚拟机需要在很多情况下保存地图绘制的当前状态。

        if (amap == null) {
            amap = aMapview.getMap();
        }
    }

    private void initView(){

        fab = findViewById(R.id.fab_activity_map_check_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                startActivity(new Intent(context, TaskLocationActivity.class));
            }
        });

        mRecyclerView = findViewById(R.id.activity_map_check_rv);

        if (AppUtils.getScreenWidthDp(this) >= 1200) {
            final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
            mRecyclerView.setLayoutManager(gridLayoutManager);
        } else if (AppUtils.getScreenWidthDp(this) >= 800) {
            final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
            mRecyclerView.setLayoutManager(gridLayoutManager);
        } else {
            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(linearLayoutManager);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        //适配器，为RecyclerView提供数据
        adapter = new MapCheckRecyclerViewAdapter(location_list, context);
        adapter.setBundle(bundle);
        mRecyclerView.setAdapter(adapter);
//        adapter.addHeader();
//        adapter.setItems(live_data);
//        adapter.addFooter();



//        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(adapter);
//        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
//        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        //下拉加载更多
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout_map_check_view);
        swipeRefreshLayout.setColorSchemeResources(R.color.google_blue, R.color.google_green, R.color.google_red, R.color.google_yellow);
        swipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {

            loadMoreData();
//            swipeRefreshLayout.setRefreshing(false);//加载成功后再消失
        }, 20));

        mRecyclerView.addOnScrollListener(scrollListener);
        SP_temporary = App.getApplication().getSharedPreferences("temporary",MODE_PRIVATE);
        SP_temporary_editor = SP_temporary.edit();
    }
    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (dy > 0) {
                fab.shrink();
            } else {
                fab.extend();
            }

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (!loading && linearLayoutManager.getItemCount() == (linearLayoutManager.findLastVisibleItemPosition() + 1)) {
                loadMoreData();
                loading = true;
            }
        }
    };
    private void initData() {
        location_list = new ArrayList<>();
        AVQuery<AVObject> query = new AVQuery<>("MapCheck");
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);// 启动查询缓存
        query.setMaxCacheAge(24 * 3600 * 1000); //设置为一天，单位毫秒
        query.whereEqualTo("UserId", AVUser.getCurrentUser().getObjectId());
        query.limit(1000);
        query.orderByDescending("createdAt");// 按时间，降序排列
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if(list!=null){
                    Log.d("MyMapActivity","共查询到：" + list.size() + "条数据。");
                    location_list.addAll(list);
                    LoadToMarker(list);
                }

                initView();
            }
        });
//        data = new ArrayList<>();
//        for (int i = 1; i <= 20; i++) {
//            data.add(i + "");
//        }
        loadTimes = 0;
    }
    private void loadMoreData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                location_list.clear();//清除数据
                location_list = new ArrayList<>();
                AVQuery<AVObject> query = new AVQuery<>("MapCheck");
                query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);// 启动查询缓存
                query.setMaxCacheAge(24 * 3600 * 1000); //设置为一天，单位毫秒
                query.whereEqualTo("UserId", AVUser.getCurrentUser().getObjectId());
                query.limit(1000);
                query.orderByDescending("createdAt");// 按时间，降序排列
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if(list!=null){
                            Log.i("LiveLIneActivity","共查询到：" + list.size() + "条数据。");
                            location_list.addAll(list);
                            adapter=new MapCheckRecyclerViewAdapter(location_list,context);
                            mRecyclerView.setAdapter(adapter);
                            LoadToMarker(list);
                        }

                        swipeRefreshLayout.setRefreshing(false);//加载成功后再消失
                    }
                });
            }
        }, 1500);
    }

    private void LoadToMarker(List<AVObject> task_list){
        // 全幅显示
        com.amap.api.maps.model.LatLngBounds bounds = getLatLngBounds(task_list);
        amap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));

        amap.clear();

        for(int i=0;i<task_list.size();i++){
            if(task_list.get(i).getAVGeoPoint("latLonPoint")!=null){
                AVGeoPoint avGeoPoint = task_list.get(i).getAVGeoPoint("latLonPoint");
                LatLng latLng = new LatLng(avGeoPoint.getLatitude(),avGeoPoint.getLongitude());
                final Marker marker = amap.addMarker(new MarkerOptions()
                        .position(latLng)
//                        .title(task_list.get(i).getString("task_name"))
                        .snippet(task_list.get(i).getString("title")));
            }
        }
    }
    //根据自定义内容获取缩放bounds
    private com.amap.api.maps.model.LatLngBounds getLatLngBounds(List<AVObject> Geolist) {
        com.amap.api.maps.model.LatLngBounds.Builder b = com.amap.api.maps.model.LatLngBounds.builder();

        for (AVObject obj: Geolist){
            AVGeoPoint geopoint = obj.getAVGeoPoint("latLonPoint");
            if(geopoint!=null){
                double x = geopoint.getLatitude();
                double y = geopoint.getLongitude();
                b.include(new com.amap.api.maps.model.LatLng(x, y));
            }
        }

        return b.build();
    }
}