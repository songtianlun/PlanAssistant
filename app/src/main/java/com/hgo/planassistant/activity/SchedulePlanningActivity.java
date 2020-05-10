package com.hgo.planassistant.activity;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.hgo.planassistant.App;
import com.hgo.planassistant.R;
import com.hgo.planassistant.timeline.RecyclerAdapter;
import com.hgo.planassistant.timeline.StepSTL;
import com.hgo.planassistant.timeline.TaskItem;
import com.hgo.planassistant.timeline.TimeItem;
import com.hgo.planassistant.tools.DateFormat;
import com.orient.me.widget.rv.itemdocration.timeline.SingleTimeLineDecoration;
import com.orient.me.widget.rv.itemdocration.timeline.TimeLine;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.hgo.planassistant.App.getContext;

public class SchedulePlanningActivity extends BaseActivity implements RouteSearch.OnRouteSearchListener {

    SharedPreferences SP_temporary;
    SharedPreferences.Editor SP_temporary_editor;
    private List<AVObject> task_list;
    private RecyclerView mRecyclerView;
    private RecyclerAdapter<TaskItem> mAdapter;
    private AMap amap;
    private MapView aMapview;
//    private RouteSearch aRouteSearch;
//    private WalkRouteResult aWalkRouteResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_planning);

        Toolbar toolbar = findViewById(R.id.toolbar_toolbar_activity_schedule_planning);
        setToolbar(toolbar);

        initView(savedInstanceState);
        LoadData();
    }

    private void initView(Bundle savedInstanceState){
        aMapview = findViewById(R.id.activity_schedule_planning_map);
        aMapview.onCreate(savedInstanceState); // 此方法须覆写，虚拟机需要在很多情况下保存地图绘制的当前状态。

        if (amap == null) {
            amap = aMapview.getMap();
        }

//        aRouteSearch = new RouteSearch(this);
//        aRouteSearch.setRouteSearchListener(this);

        mRecyclerView = findViewById(R.id.activity_schedule_planning_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter = new RecyclerAdapter<TaskItem>() {
            @Override
            public ViewHolder<TaskItem> onCreateViewHolder(View root, int viewType) {
                return new TimeLineViewHolder(root);
            }

            @Override
            public int getItemLayout(TaskItem taskItem, int position) {
                return R.layout.item_schedule_planning_recycle_view;
            }
        });

//        List<TimeItem> timeItems = TimeItem.initStepInfo();
//        mAdapter.addAllData(timeItems);
//        TimeLine decoration = new SingleTimeLineDecoration.Builder(getContext(), timeItems)
//                .setTitle(Color.parseColor("#ffffff"), 20)
//                .setTitleStyle(SingleTimeLineDecoration.FLAG_TITLE_TYPE_TOP, 40)
//                .setLine(SingleTimeLineDecoration.FLAG_LINE_DIVIDE, 50, Color.parseColor("#8d9ca9"))
//                .setDot(SingleTimeLineDecoration.FLAG_DOT_DRAW)
//                .setSameTitleHide()
//                .build(StepSTL.class);
//        mRecyclerView.addItemDecoration(decoration);

    }

    private void LoadData(){
        SP_temporary = App.getApplication().getSharedPreferences("temporary",MODE_PRIVATE);
        SP_temporary_editor = SP_temporary.edit();
        task_list = new ArrayList<>();
        String poi_item = SP_temporary.getString("task_schedule_planning","");
        Log.d("SchedulePlanning","收到序列化的poi："+poi_item);

        if(poi_item.length()>0){
            JSONArray jsonArray = JSONArray.parseArray(poi_item); //反序列化
            Log.d("SchedulePlanning","接收到poi数据"+jsonArray);
            for(int i=0;i<jsonArray.size();i++){
                task_list.add((AVObject)jsonArray.get(i));
                Log.i("SchedulePlanning","逐个解析对象："+ jsonArray.get(i).toString());
            }
            LoadAVObjectToRecycleView(task_list);
            LocationPlanning(task_list);
        }else {
            Toast.makeText(getContext(),"您今日无待完成日程！",Toast.LENGTH_SHORT);
        }
    }
    private void LoadAVObjectToRecycleView(List<AVObject> avObjects){
        List<TaskItem> taskItems = new ArrayList<>();
        for(int i=0;i<avObjects.size();i++){
            AVObject object = avObjects.get(i);
            TaskItem taskItem = new TaskItem(object.getString("task_name"),"日程表",object.getString("task_description"),object.getDate("start_time"),Color.parseColor("#F57F17"), 0);
            Log.d("SchedulePlanning", "转换AVObject到TaskItems:"+"名称：" +taskItem.getName()+ "描述" + taskItem.getDetail() + "时间" + taskItem.getDate());
            taskItems.add(taskItem);
        }
        mAdapter.addAllData(taskItems);
        TimeLine decoration = new SingleTimeLineDecoration.Builder(getContext(), taskItems)
                .setTitle(Color.parseColor("#ffffff"), 20)
                .setTitleStyle(SingleTimeLineDecoration.FLAG_TITLE_TYPE_TOP, 40)
                .setLine(SingleTimeLineDecoration.FLAG_LINE_DIVIDE, 50, Color.parseColor("#8d9ca9"))
                .setDot(SingleTimeLineDecoration.FLAG_DOT_DRAW)
                .setSameTitleHide()
                .build(StepSTL.class);
        mRecyclerView.addItemDecoration(decoration);

//        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
//                mStartPoint, mEndPoint);
//        RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo);
//        aRouteSearch.calculateWalkRouteAsyn(query);//开始算路

//        List<TimeItem> timeItems = TimeItem.initStepInfo();
//        mAdapter.addAllData(timeItems);
//        TimeLine decoration = new SingleTimeLineDecoration.Builder(getContext(), timeItems)
//                .setTitle(Color.parseColor("#ffffff"), 20)
//                .setTitleStyle(SingleTimeLineDecoration.FLAG_TITLE_TYPE_TOP, 40)
//                .setLine(SingleTimeLineDecoration.FLAG_LINE_DIVIDE, 50, Color.parseColor("#8d9ca9"))
//                .setDot(SingleTimeLineDecoration.FLAG_DOT_DRAW)
//                .setSameTitleHide()
//                .build(StepSTL.class);
//        mRecyclerView.addItemDecoration(decoration);
    }
    private void LocationPlanning(List<AVObject> task_list){
        // 全幅显示
        com.amap.api.maps.model.LatLngBounds bounds = getLatLngBounds(task_list);
        amap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));

        for(int i=0;i<task_list.size();i++){
            if(task_list.get(i).getAVGeoPoint("task_point")!=null){
                AVGeoPoint avGeoPoint = task_list.get(i).getAVGeoPoint("task_point");
                LatLng latLng = new LatLng(avGeoPoint.getLatitude(),avGeoPoint.getLongitude());
                final Marker marker = amap.addMarker(new MarkerOptions()
                        .position(latLng)
//                        .title(task_list.get(i).getString("task_name"))
                        .snippet(task_list.get(i).getString("task_name")));
            }
        }
    }
    //根据自定义内容获取缩放bounds
    private com.amap.api.maps.model.LatLngBounds getLatLngBounds(List<AVObject> Geolist) {
        com.amap.api.maps.model.LatLngBounds.Builder b = com.amap.api.maps.model.LatLngBounds.builder();

        for (AVObject obj: Geolist){
            AVGeoPoint geopoint = obj.getAVGeoPoint("task_point");
            if(geopoint!=null){
                double x = geopoint.getLatitude();
                double y = geopoint.getLongitude();
                b.include(new com.amap.api.maps.model.LatLng(x, y));
            }
        }

        return b.build();
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }

    class TimeLineViewHolder extends RecyclerAdapter.ViewHolder<TaskItem> {

        @BindView(R.id.tv_schedule_planning_recycle_view_title)
        TextView mTitleTv;
        @BindView(R.id.tv_schedule_planning_recycle_view_content)
        TextView mContentTv;
        @BindView(R.id.tv_schedule_planning_recycle_view_time)
        TextView mDate;

        public TimeLineViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(TaskItem taskItem) {
            DateFormat dateFormat = new DateFormat();
            mTitleTv.setText(taskItem.getName());
            if(taskItem.getDetail()==null){
                mContentTv.setText("");
            }else{
                mContentTv.setText(taskItem.getDetail());
            }
            mDate.setText(dateFormat.GetDetailDescription(taskItem.getDate()));
        }
    }
}
