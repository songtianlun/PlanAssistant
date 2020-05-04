package com.hgo.planassistant.fragement;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
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
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.material.snackbar.Snackbar;
import com.hgo.planassistant.App;
import com.hgo.planassistant.Constant;
import com.hgo.planassistant.R;
import com.hgo.planassistant.activity.MainActivity;
import com.hgo.planassistant.activity.StepCounterActivity;
import com.hgo.planassistant.activity.TrackActivity;
import com.hgo.planassistant.custom.MyMarkerView;
import com.hgo.planassistant.tools.DateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static android.content.Context.MODE_MULTI_PROCESS;
import static android.content.Context.MODE_PRIVATE;


public class HomeFragment extends Fragment implements View.OnClickListener, View.OnTouchListener,
        SeekBar.OnSeekBarChangeListener ,OnChartValueSelectedListener {
    // liveLinechart
//    private LineChart chart;
//    private Button loadlinechart,savetogallary,bt_start_location;
//    private CardView card__home_liveline, card__home_location, card__home_plan;
    NestedScrollView nestedScrollView;
    private static final int PERMISSION_STORAGE = 0;

    private com.google.android.material.card.MaterialCardView card_home_map;
    private com.google.android.material.card.MaterialCardView card_home_step_counter;
    private com.google.android.material.card.MaterialCardView card_home_suggest_task;
    private TextView card_home_suggest_task_textview;
//    private TextView tv_card_home_location_station,tv_card_home_location_date;
//    private TextView card_home_location_station_location,card_home_location_data;
    private Calendar now_calendar;
    private com.amap.api.maps.MapView aMapView = null;
    private AMap amap = null;
    private List<AVObject> now_list = null;

    private PieChart DayPieChart;

    SharedPreferences SP_setting = App.getContext().getSharedPreferences("setting",MODE_PRIVATE);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        nestedScrollView = (NestedScrollView) inflater.inflate(R.layout.fragment_home, container, false);
//        loadlinechart = nestedScrollView.findViewById(R.id.loadlinechart);
//        savetogallary = nestedScrollView.findViewById(R.id.savetogallary);
//        card__home_liveline = nestedScrollView.findViewById(R.id.card_home_liveline);
//        card__home_location = nestedScrollView.findViewById(R.id.card_home_location);
//        card__home_plan = nestedScrollView.findViewById(R.id.card_home_plan);
        aMapView = nestedScrollView.findViewById(R.id.card_home_amapView);
        card_home_map = nestedScrollView.findViewById(R.id.card_home_map);
        card_home_step_counter = nestedScrollView.findViewById(R.id.card_home_step_counter);
        DayPieChart = nestedScrollView.findViewById(R.id.card_home_step_counter_piechart);
        card_home_suggest_task = nestedScrollView.findViewById(R.id.card_home_suggest_task);
        card_home_suggest_task_textview = nestedScrollView.findViewById(R.id.card_home_suggest_task_textview);

//        tv_card_home_location_station = nestedScrollView.findViewById(R.id.card_home_location_station);
//        tv_card_home_location_date = nestedScrollView.findViewById(R.id.tv_card_home_location_date);
//        card_home_location_station_location = nestedScrollView.findViewById(R.id.card_home_location_station_location);
//        card_home_location_data = nestedScrollView.findViewById(R.id.card_home_location_data);
//        Initchart(nestedScrollView);

        now_calendar = Calendar.getInstance();//获取当前时间

        return nestedScrollView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String date_str = now_calendar.get(Calendar.YEAR) + "年" + (now_calendar.get(Calendar.MONTH)+1) + "月" + now_calendar.get(Calendar.DATE) + "日";

//        loadlinechart.setOnClickListener(v -> {
//            Log.i("HomeFragement","clickLoadChart");
//            Snackbar.make(v, "加载图表", Snackbar.LENGTH_SHORT).show();
//        });

//        savetogallary.setOnClickListener(this);
//        loadlinechart.setOnClickListener(this);
//        card__home_liveline.setOnTouchListener(this);
//        card__home_location.setOnTouchListener(this);
//        card__home_plan.setOnTouchListener(this);
        card_home_map.setOnClickListener(this);
        card_home_step_counter.setOnClickListener(this);
        card_home_suggest_task.setOnClickListener(this);

//        bt_start_location.setOnClickListener(this);

//        if(SP_setting.getBoolean("pref_location_background_switch",false)){
//            tv_card_home_location_station.setText("已开启");
//        }else{
//            tv_card_home_location_station.setText("未开启");
//        }
//        tv_card_home_location_date.setText(date_str);
//
//        Log.i("HomeFragement",LocadLocationState().toString());
//        if(LocadLocationState()){
//            card_home_location_station_location.setText("高精度");
//        }else{
//            card_home_location_station_location.setText("节能");
//        }
//        LoadDataTital(-6);


        LoadSuggestTask();
//        LoadLinechartData();//从数据库中读取经理数据,完毕后加载图表
        initMap(savedInstanceState);
        initDayPieChart();
        setDayPieChartData();

    }

    @Override
    public void onClick(View v) {
        Log.i("HomeFragement","click");
        switch (v.getId()) {
//            case R.id.loadlinechart:
//                Log.i("HomeFragement","clickLoadChart");
//                Snackbar.make(v, "加载图表", Snackbar.LENGTH_SHORT).show();
//                LoadLinechartData();//从数据库中读取经理数据,完毕后加载图表
//                break;
            case R.id.card_home_map:
                Log.i("HomeFragement","click card_home_map");
                startActivity(new Intent(getActivity(), TrackActivity.class));
                break;
            case R.id.card_home_step_counter:
                startActivity(new Intent(getActivity(), StepCounterActivity.class));
                break;
            case R.id.card_home_suggest_task:
                LoadSuggestTask();
                break;
//            case R.id.savetogallary:
//                chartsavetogallary();
//                break;
//            case R.id.bt_card_home_map_location:
//                break;
            default:
                Log.i("HomeFragement","HomeFragementClick!");
                break;
        }

    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ObjectAnimator downAnimator = ObjectAnimator.ofFloat(v, "translationZ", 16);
                downAnimator.setDuration(200);
                downAnimator.setInterpolator(new DecelerateInterpolator());
                downAnimator.start();
                Log.i("HomeFrragement","HomeFragement OnTouch！");
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                ObjectAnimator upAnimator = ObjectAnimator.ofFloat(v, "translationZ", 0);
                upAnimator.setDuration(200);
                upAnimator.setInterpolator(new AccelerateInterpolator());
                upAnimator.start();
                break;
        }
        return false;
    }

    private void initDayPieChart(){
        DayPieChart.setUsePercentValues(true);
        DayPieChart.getDescription().setEnabled(false);
        DayPieChart.setExtraOffsets(5, 10, 5, 5);

        DayPieChart.setDragDecelerationFrictionCoef(0.95f);

//        chart.setCenterTextTypeface(tfLight);
//        chart.setCenterText(generateCenterSpannableText());

        DayPieChart.setDrawHoleEnabled(true);
        DayPieChart.setHoleColor(Color.WHITE);

        DayPieChart.setTransparentCircleColor(Color.WHITE);
        DayPieChart.setTransparentCircleAlpha(110);

        DayPieChart.setHoleRadius(58f);
        DayPieChart.setTransparentCircleRadius(61f);

        DayPieChart.setDrawCenterText(true);

        DayPieChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        DayPieChart.setRotationEnabled(true);
        DayPieChart.setHighlightPerTapEnabled(true);

        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        // add a selection listener
        DayPieChart.setOnChartValueSelectedListener(this);

        DayPieChart.animateY(1400, Easing.EaseInOutQuad);

        Legend l = DayPieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        DayPieChart.setEntryLabelColor(Color.WHITE);
//        chart.setEntryLabelTypeface(tfRegular);
        DayPieChart.setEntryLabelTextSize(12f);

        DayPieChart.setUsePercentValues(false);            //使用百分比显示
    }

    private void setDayPieChartData() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        com.hgo.planassistant.tools.DateFormat dateFormat = new DateFormat();
        Calendar NowHour = dateFormat.FilterHourAndMinuteAndSecond(Calendar.getInstance());
        Log.i("StepCounterActivity","当前日期："+dateFormat.GetDetailDescription(NowHour));

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.

        AVQuery<AVObject> query = new AVQuery<>("stepcounter");
        query.whereEqualTo("UserId", AVUser.getCurrentUser().getObjectId());
        Calendar quaretime = Calendar.getInstance();
        quaretime.setTime(NowHour.getTime());
        query.whereGreaterThan("time",quaretime.getTime());
        quaretime.add(Calendar.HOUR_OF_DAY,24);
        Log.i("StepCounterActivity","增加24小时后时间："+dateFormat.GetDetailDescription(quaretime));
        query.whereLessThan("time",quaretime.getTime());
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException avException) {
                int sumStep = 0;
                Log.i("StepCounterActivity","查询到数据总数："+avObjects.size());
                sumStep = getSumStep(avObjects);
                int StepTarget = App.getApplication().getSharedPreferences("setting",MODE_PRIVATE).getInt("pref_personal_step_target",4000); // 查询轨迹精度限制
                Log.i("StepCounterActivity","目标步数："+StepTarget + " 当前已完成："+sumStep);
                if(StepTarget > sumStep){
                    entries.add(new PieEntry((StepTarget - sumStep),"剩余："+ (StepTarget - sumStep) + "步")); // 步数目标
                }else{
                    entries.add(new PieEntry((sumStep - StepTarget),"超额完成："+ (sumStep - StepTarget) + "步")); // 步数目标
                }
                entries.add(new PieEntry(sumStep,"已完成：" + sumStep + "步")); // 已完成

                PieDataSet dataSet = new PieDataSet(entries, "Election Results");

                dataSet.setDrawIcons(false);

                dataSet.setSliceSpace(3f);
                dataSet.setIconsOffset(new MPPointF(0, 40));
                dataSet.setSelectionShift(5f);

//                //最终数据 PieData
//                PieData pieData = new PieData(entries);
//                pieData.setDrawValues(true);            //设置是否显示数据实体(百分比，true:以下属性才有意义)
//                pieData.setValueTextColor(Color.BLUE);  //设置所有DataSet内数据实体（百分比）的文本颜色
//                pieData.setValueTextSize(12f);          //设置所有DataSet内数据实体（百分比）的文本字体大小
////                pieData.setValueTypeface(mTfLight);     //设置所有DataSet内数据实体（百分比）的文本字体样式
//                pieData.setValueFormatter(new PercentFormatter());//设置所有DataSet内数据实体（百分比）的文本字体格式
//                chart.setData(pieData);
//                chart.highlightValues(null);
//                chart.invalidate();                    //将图表重绘以显示设置的属性和数据


                // add a lot of colors

                ArrayList<Integer> colors = new ArrayList<>();

                for (int c : ColorTemplate.VORDIPLOM_COLORS)
                    colors.add(c);

                for (int c : ColorTemplate.JOYFUL_COLORS)
                    colors.add(c);

                for (int c : ColorTemplate.COLORFUL_COLORS)
                    colors.add(c);

                for (int c : ColorTemplate.LIBERTY_COLORS)
                    colors.add(c);

                for (int c : ColorTemplate.PASTEL_COLORS)
                    colors.add(c);

                colors.add(ColorTemplate.getHoloBlue());

                dataSet.setColors(colors);
                dataSet.setSelectionShift(0f);

                PieData data = new PieData(dataSet);
                data.setValueFormatter(new PercentFormatter(DayPieChart));
                data.setValueTextSize(11f);
                data.setValueTextColor(Color.WHITE);
//        data.setValueTypeface(tfLight);
                DayPieChart.setData(data);

                // undo all highlights
                DayPieChart.highlightValues(null);

                DayPieChart.invalidate();
            }
        });


    }


    private void initMap(Bundle savedInstanceState){

        Calendar start_time = Calendar.getInstance();
        Calendar end_time = Calendar.getInstance();
        start_time.add(Calendar.HOUR_OF_DAY, -6); //讲起始时间推算为当前时间前n小时


        int PrecisionLessThen = Integer.parseInt(App.getApplication().getSharedPreferences("setting",MODE_PRIVATE).getString("settings_location_query_precision","300")); // 查询轨迹精度限制

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
                if(count>TrackActivity.QueryMaxNum){
                    Toast.makeText(App.getContext(),"查询数据过大无法获取，请检查起止时间！共查询到：" + count + "条数据。",Toast.LENGTH_LONG).show();
                }else{
                    Log.i("TrackActivity","共查询到：" + count + "条数据。");
//                    Toast.makeText(App.getContext(),"共查询到：" + count + "条数据。",Toast.LENGTH_LONG).show();
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
    }

//    private void Initchart(NestedScrollView nestedScrollView){
//        chart = nestedScrollView.findViewById(R.id.livelinechart);
//        {   // // Chart Style // //
//
//            // background color
//            chart.setBackgroundColor(Color.WHITE);
//
//            // disable description text
//            chart.getDescription().setEnabled(false);
//
//            // enable touch gestures
//            chart.setTouchEnabled(true);
//
//            // set listeners
//            chart.setOnChartValueSelectedListener(this);
//            chart.setDrawGridBackground(false);
//
//            // create marker to display box when values are selected
//            MyMarkerView mv = new MyMarkerView(App.getContext(), R.layout.custom_marker_view);
//
//            // Set the marker to the chart
//            mv.setChartView(chart);
//            chart.setMarker(mv);
//
//            // enable scaling and dragging
//            chart.setDragEnabled(true);
//            chart.setScaleEnabled(true);
//            // chart.setScaleXEnabled(true);
//            // chart.setScaleYEnabled(true);
//
//            // force pinch zoom along both axis
//            chart.setPinchZoom(true);
//        }
//
//        XAxis xAxis;
//        {   // // X-Axis Style // //
//            xAxis = chart.getXAxis();
//
//            // vertical grid lines
//            xAxis.enableGridDashedLine(10f, 10f, 0f);
//
//            // axis range
//            xAxis.setAxisMaximum(24f);
//            xAxis.setAxisMinimum(0f);
//        }
//
//        YAxis yAxis;
//        {   // // Y-Axis Style // //
//            yAxis = chart.getAxisLeft();
//
//            // disable dual axis (only use LEFT axis)
//            chart.getAxisRight().setEnabled(false);
//
//            // horizontal grid lines
//            yAxis.enableGridDashedLine(10f, 10f, 0f);
//
//            // axis range
//            yAxis.setAxisMaximum(110f);
//            yAxis.setAxisMinimum(-10f);
//        }
//
//
//        {   // // Create Limit Lines // //
//            LimitLine llXAxis = new LimitLine(9f, "Index 10");
//            llXAxis.setLineWidth(4f);
//            llXAxis.enableDashedLine(10f, 10f, 0f);
//            llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
//            llXAxis.setTextSize(10f);
//            //llXAxis.setTypeface(tfRegular);
//
//            LimitLine ll1 = new LimitLine(100f, "Upper Limit");
//            ll1.setLineWidth(2f);
//            ll1.enableDashedLine(10f, 10f, 0f);
//            ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
//            ll1.setTextSize(6f);
//            //ll1.setTypeface(tfRegular);
//
//            LimitLine ll2 = new LimitLine(0f, "Lower Limit");
//            ll2.setLineWidth(2f);
//            ll2.enableDashedLine(10f, 10f, 0f);
//            ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
//            ll2.setTextSize(6f);
//            //ll2.setTypeface(tfRegular);
//
//            // draw limit lines behind data instead of on top
//            yAxis.setDrawLimitLinesBehindData(true);
//            xAxis.setDrawLimitLinesBehindData(true);
//
//            // add limit lines
//            yAxis.addLimitLine(ll1);
//            yAxis.addLimitLine(ll2);
//            //xAxis.addLimitLine(llXAxis);
//        }
//    }
//    private int[] LoadLinechartData(){
//        int[] chareData = new int[24];
//        Arrays.fill(chareData,0);//数组的批量赋值。
//
//        AVQuery<AVObject> query = new AVQuery<>("liveline");
//        query.whereEqualTo("UserId", AVUser.getCurrentUser().getObjectId());
//        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);// 启动查询缓存
//        query.setMaxCacheAge(24 * 3600 * 1000); //设置为一天，单位毫秒
//        query.limit(1000);
//        query.orderByDescending("createdAt");// 按时间，降序排列
//        query.findInBackground(new FindCallback<AVObject>() {
//            @Override
//            public void done(List<AVObject> list, AVException e) {
//                if(list!=null){
//                Log.i("LiveLIneActivity","共查询到：" + list.size() + "条数据。");
//                for (AVObject obj: list){
//                    Calendar livetime = Calendar.getInstance();
//                    livetime.setTime((Date)obj.get("livetime"));//获取时间
//                    int hour = livetime.get(Calendar.HOUR_OF_DAY);
//                    int score = (int)obj.get("score");
////                    Log.i("HomeFragement",hour+"时刻的精力值为："+score);
//                    if(chareData[hour]==0){
//                        chareData[hour] = score;
//                    }else{
//                        chareData[hour] = (score+chareData[hour])/2;
//                    }
////                    Log.i("HomeFragement",hour+"时刻的精力值修改为："+chareData[hour]);
//                }
//                LoadLinechart(chareData);
//                }
//
//            }
//        });
//        return chareData;
//    }
//    private void LoadLinechart(int[] chartdata){
//        Log.i("HomeFragement","Load liveline chart");
//
//
//        //1.设置x轴和y轴的点
//        List<Entry> entries = new ArrayList<>();
//        for (int i = 0; i < 24; i++){
//
////            entries.add(new Entry(i,new Random().nextInt(100)));
//            entries.add(new Entry(i,chartdata[i]));
//        }
//
//        LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset
//
//
//        //3.chart设置数据
//        LineData lineData = new LineData(dataSet);
//        chart.setData(lineData);
//        chart.invalidate(); // refresh
//
//        List<ILineDataSet> sets = chart.getData()
//                .getDataSets();
//        for (ILineDataSet iSet : sets) {
//
//            // 绘制
//            LineDataSet set = (LineDataSet) iSet;
//            if (set.isDrawFilledEnabled())
//                set.setDrawFilled(false);
//            else
//                set.setDrawFilled(true);
//
//            //平滑
//            set.setMode(set.getMode() == LineDataSet.Mode.CUBIC_BEZIER
//                    ? LineDataSet.Mode.LINEAR
//                    :  LineDataSet.Mode.CUBIC_BEZIER);
//        }
//        chart.invalidate();
//
//        //动画
//        chart.animateXY(2000, 2000);
//
//    }
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
                CoordinateConverter converter  = new CoordinateConverter(getContext());
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
    private int getSumStep(List<AVObject> in){
        int sumStep = 0;
        Log.i("StepCounterActivity","获取总步数输入数据总数："+in.size());
        DateFormat dateFormat = new DateFormat();
        for(int i=0;i<in.size();i++){
            if(in.get(i).getInt("count")>=0){
                Log.i("StepCounterActivity",dateFormat.GetDetailDescription(in.get(i).getDate("time"))+"时刻步数: "+in.get(i).getInt("count"));
                sumStep+=in.get(i).getInt("count");
            }
        }
        return sumStep;
    }
//    private void LoadLinechart(){
//        Log.i("HomeFragement","Load liveline chart");
//
//
//        //1.设置x轴和y轴的点
//        List<Entry> entries = new ArrayList<>();
//        for (int i = 0; i < 24; i++){
//
//            entries.add(new Entry(i,new Random().nextInt(100)));
//        }
//
//        LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset
//
//
//        //3.chart设置数据
//        LineData lineData = new LineData(dataSet);
//        chart.setData(lineData);
//        chart.invalidate(); // refresh
//
//        List<ILineDataSet> sets = chart.getData()
//                .getDataSets();
//        for (ILineDataSet iSet : sets) {
//
//            // 绘制
//            LineDataSet set = (LineDataSet) iSet;
//            if (set.isDrawFilledEnabled())
//                set.setDrawFilled(false);
//            else
//                set.setDrawFilled(true);
//
//            //平滑
//            set.setMode(set.getMode() == LineDataSet.Mode.CUBIC_BEZIER
//                    ? LineDataSet.Mode.LINEAR
//                    :  LineDataSet.Mode.CUBIC_BEZIER);
//        }
//        chart.invalidate();
//
//        //动画
//        chart.animateXY(2000, 2000);
//
//    }
//    private void chartsavetogallary(){
//        Log.i("HomeFragement","Chart Save to gallary!");
//        if (ContextCompat.checkSelfPermission(App.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//            if (chart.saveToGallery("规划助手—精力曲线" + "_" + System.currentTimeMillis(), 70))
//                Toast.makeText(App.getContext(), "Saving SUCCESSFUL!",
//                        Toast.LENGTH_SHORT).show();
//            else
//                Toast.makeText(App.getContext(), "Saving FAILED!", Toast.LENGTH_SHORT)
//                        .show();
//        } else {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                Activity activity = this.getActivity();
//                Snackbar.make(chart, "Write permission is required to save image to gallery", Snackbar.LENGTH_INDEFINITE)
//                        .setAction(android.R.string.ok, new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_STORAGE);
//                            }
//                        }).show();
//            } else {
//                Toast.makeText(App.getContext(), "Permission Required!", Toast.LENGTH_SHORT)
//                        .show();
//                ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_STORAGE);
//            }
//        }
//    }

    private void LoadSuggestTask(){
        AVQuery<AVObject> query = new AVQuery<>("Task");
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);// 启动查询缓存
        query.setMaxCacheAge(24 * 3600 * 1000); //设置为一天，单位毫秒
        query.whereEqualTo("UserId", AVUser.getCurrentUser().getObjectId());
        query.whereNotEqualTo("done", true);
        query.limit(10); // 根据重要性和截止时间筛选前十的事件
        query.orderByDescending("task_importance"); // 按重要性降序
        query.addAscendingOrder("end_time"); // 按结束时间升序
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int count, AVException e) {
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if(list!=null){
                            Log.i("HomeFragment","共查询到：" + list.size() + "条数据。");
                            String string = new String();
                            for(int j=0;j<list.size();j++){
                                string += ("<p>" + " &#8226; " +  list.get(j).getString("task_name") + "</p>");
                            }
                            // Html.fromHtml可以将Html代码转换成对应的text
                            card_home_suggest_task_textview.setText(Html.fromHtml(string));
                        }else{
                            card_home_suggest_task_textview.setText("当前无日程，请根据需要添加。");
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    private Boolean LocadLocationState(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(App.getContext());

//        Log.i("HomeFragement",preferences.getString("pref_list_location_time", ""));
        //return preferences.getString("pref_list_location_type", "");
        return preferences.getBoolean("pref_location_usegps", false);


    }

//    private void LoadDataTital(int hour){
//        Calendar start_time;
//        Calendar end_time;
//        start_time = Calendar.getInstance();
//        end_time = Calendar.getInstance();
//        start_time.add(Calendar.HOUR_OF_DAY, hour); //讲起始时间推算为当前时间前n小时
//
//        AVQuery<AVObject> query = new AVQuery<>("trajectory");
//        // 启动查询缓存
//        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
//        query.setMaxCacheAge(24 * 3600 * 1000); //设置为一天，单位毫秒
//        query.whereEqualTo("UserId", AVUser.getCurrentUser().getObjectId());
//        query.whereGreaterThan("time",start_time.getTime());
//        query.whereLessThan("time",end_time.getTime());
//        query.whereLessThan("precision",50);
//        query.selectKeys(Arrays.asList("point", "time", "precision"));
//        query.limit(1000);
//        query.countInBackground(new CountCallback() {
//            @Override
//            public void done(int i, AVException e) {
//                if (e == null) {
//                    // 查询成功，输出计数
//                    Log.d("HomeFragement", hour+"小时内共记录了" + i + "条轨迹记录。");
////                    card_home_location_data.setText(i+"条");
//                } else {
//                    // 查询失败
//                }
//            }
//        });
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
//    // 生成的同时全幅显示
//    private ArrayList<Point> genetatePointsFromAvobject(List<AVObject> list, MapboxMap mapboxmap){
//        ArrayList<Point> routeCoordinates = new ArrayList<Point>();
//        LatLngBounds.Builder latLngBoundsBuilder = new LatLngBounds.Builder();
//
//        for (AVObject obj: list){
//            AVGeoPoint geopoint = obj.getAVGeoPoint("point");
//            routeCoordinates.add(Point.fromLngLat(geopoint.getLongitude(), geopoint.getLatitude()));
//            latLngBoundsBuilder.include(new LatLng(geopoint.getLatitude(),geopoint.getLongitude()));
//        }
//        if(list.size()>2){
//            LatLngBounds latLngBounds = latLngBoundsBuilder.build();//创建边界
//            mapboxmap.easeCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 50), 5000);//全幅显示
//        }
//        Log.i("TrackActivity","为生成线读取到"+routeCoordinates.size()+"条数据");
//        return routeCoordinates;
//    }

}
