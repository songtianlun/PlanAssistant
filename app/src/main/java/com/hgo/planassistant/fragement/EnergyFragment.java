package com.hgo.planassistant.fragement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.material.card.MaterialCardView;
import com.hgo.planassistant.App;
import com.hgo.planassistant.R;
import com.hgo.planassistant.activity.EnergyEvaluationActivity;
import com.hgo.planassistant.activity.MoodWhisperActivity;
import com.hgo.planassistant.activity.StepCounterActivity;
import com.hgo.planassistant.custom.MyMarkerView;
import com.hgo.planassistant.tools.SuggestionPopup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import cn.leancloud.AVObject;
import cn.leancloud.AVQuery;
import cn.leancloud.AVUser;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EnergyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EnergyFragment extends Fragment implements View.OnClickListener, OnChartValueSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private LineChart EnergyLineChart, PhysicalLineChart, EmotionLineChart, ThinkingLineChart, DeterminationLineChart;
    private MaterialCardView card_energy, card_energy_physical, card_energy_emotional, card_energy_thinking, card_energy_determination;
    private NestedScrollView nestedScrollView;

    SharedPreferences SP_setting = App.getContext().getSharedPreferences("setting",MODE_PRIVATE);

    private int[] physicalScore = new int[24];
    private int[] emotionalScore = new int[24];
    private int[] thinkingScore = new int[24];
    private int[] determinationScore = new int[24];

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EnergyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EnergyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EnergyFragment newInstance(String param1, String param2) {
        EnergyFragment fragment = new EnergyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        nestedScrollView = (NestedScrollView) inflater.inflate(R.layout.fragment_energy, container, false);
        EnergyLineChart = nestedScrollView.findViewById(R.id.card_energy_linechart);
        PhysicalLineChart = nestedScrollView.findViewById(R.id.card_energy_physical_linechart);
        EmotionLineChart = nestedScrollView.findViewById(R.id.card_energy_emotion_linechart);
        ThinkingLineChart = nestedScrollView.findViewById(R.id.card_energy_thinking_linechart);
        DeterminationLineChart = nestedScrollView.findViewById(R.id.card_energy_determination_linechart);
        card_energy = nestedScrollView.findViewById(R.id.card_energy);
        card_energy_physical = nestedScrollView.findViewById(R.id.card_energy_physical);
        card_energy_emotional = nestedScrollView.findViewById(R.id.card_energy_emotion);
        card_energy_thinking = nestedScrollView.findViewById(R.id.card_energy_thinking);
        card_energy_determination = nestedScrollView.findViewById(R.id.card_energy_determination);
        return nestedScrollView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        card_energy.setOnClickListener(this);
        card_energy_physical.setOnClickListener(this);
        card_energy_emotional.setOnClickListener(this);
        card_energy_thinking.setOnClickListener(this);
        card_energy_determination.setOnClickListener(this);

        initLineChart(EnergyLineChart);
        initLineChart(PhysicalLineChart);
        initLineChart(EmotionLineChart);
        initLineChart(ThinkingLineChart);
        initLineChart(DeterminationLineChart);
    }

    @Override
    public void onStart() {
        super.onStart();
        // 1. 初始化思维和意志，结束后联动情感
        // 2. 初始化情感，结束后联动体能
        // 3. 初始化体能，结束后联动精力趋势
        initThinkingAndDeterminationLineChart();
//        initEmotionLineChart();
//        initPhysicalLineChart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.card_energy:
                int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                int physical = physicalScore[hour];
                int emotional = emotionalScore[hour];
                int thinking = thinkingScore[hour];
                int determination = determinationScore[hour];

                SuggestionPopup suggestionPopup = new SuggestionPopup();
                if(physical<emotional&&physical<thinking&&physical<determination){
                    // 体能最小
                    suggestionPopup.RandomPhysicalPopup(getContext());
                }else if(emotional<physical&&emotional<thinking&&emotional<determination){
                    // 情感最小
                    suggestionPopup.RandomEmotionalPopup(getContext());
                }else if(thinking<physical&&thinking<emotional&&thinking<determination){
                    // 思维最小
                    suggestionPopup.RandomThinkingPopup(getContext());
                }else{
                    // 意志最小
                    suggestionPopup.RandomDeterminationPopup(getContext());
                }
                break;
            case R.id.card_energy_physical:
                startActivity(new Intent(getActivity(), StepCounterActivity.class));
                break;
            case R.id.card_energy_emotion:
                startActivity(new Intent(getActivity(), MoodWhisperActivity.class));
                break;
            case R.id.card_energy_thinking:
                startActivity(new Intent(getActivity(), EnergyEvaluationActivity.class));
                break;
            case R.id.card_energy_determination:
                startActivity(new Intent(getActivity(), EnergyEvaluationActivity.class));
                break;
            default:
                break;
        }
    }

    private void initLineChart(LineChart chart){
        {   // // Chart Style // //

            // background color
            chart.setBackgroundColor(Color.WHITE);

            // disable description text
            chart.getDescription().setEnabled(false);

            // enable touch gestures
            chart.setTouchEnabled(true);

            // set listeners
            chart.setOnChartValueSelectedListener(this);
            chart.setDrawGridBackground(false);

            // create marker to display box when values are selected
            MyMarkerView mv = new MyMarkerView(App.getContext(), R.layout.custom_marker_view);

            // Set the marker to the chart
            mv.setChartView(chart);
            chart.setMarker(mv);

            // enable scaling and dragging
            chart.setDragEnabled(true);
            chart.setScaleEnabled(true);
            // chart.setScaleXEnabled(true);
            // chart.setScaleYEnabled(true);

            // force pinch zoom along both axis
            chart.setPinchZoom(true);
        }

        XAxis xAxis;
        {   // // X-Axis Style // //
            xAxis = chart.getXAxis();

            // vertical grid lines
            xAxis.enableGridDashedLine(10f, 10f, 0f);

            // axis range
            xAxis.setAxisMaximum(24f);
            xAxis.setAxisMinimum(0f);

            ValueFormatter valueFormatter = new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    int value_int = (int)value;
                    return value_int + "时";
                }
            };
            xAxis.setValueFormatter(valueFormatter);//设置自定义格式，在绘制之前动态调整x的值。
        }

        YAxis yAxis;
        {   // // Y-Axis Style // //
            yAxis = chart.getAxisLeft();

            // disable dual axis (only use LEFT axis)
            chart.getAxisRight().setEnabled(false);

            // horizontal grid lines
            yAxis.enableGridDashedLine(10f, 10f, 0f);

            // axis range
            yAxis.setAxisMaximum(110f);
            yAxis.setAxisMinimum(-10f);
        }


        {   // // Create Limit Lines // //
            int limitx = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            LimitLine llXAxis = new LimitLine(limitx, "当前时刻");
            llXAxis.setLineWidth(2f);
            llXAxis.enableDashedLine(10f, 10f, 0f);
            llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            llXAxis.setTextSize(6f);
            //llXAxis.setTypeface(tfRegular);

            // 设置开始睡眠时间
            Calendar start_sleep = Calendar.getInstance();
            start_sleep.setTimeInMillis(SP_setting.getLong("pref_personal_start_sleep", Calendar.getInstance().getTime().getTime()));
            LimitLine start_sleep_XAxis = new LimitLine(start_sleep.get(Calendar.HOUR_OF_DAY), "开始时刻");
            start_sleep_XAxis.setLineWidth(2f);
            start_sleep_XAxis.setLineColor(Color.parseColor("#003366"));
            start_sleep_XAxis.enableDashedLine(10f, 10f, 0f);
            start_sleep_XAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            start_sleep_XAxis.setTextSize(6f);

            // 设置结束睡眠时间
            Calendar end_sleep = Calendar.getInstance();
            end_sleep.setTimeInMillis(SP_setting.getLong("pref_personal_end_sleep",end_sleep.getTime().getTime()));
            LimitLine stop_sleep_XAxis = new LimitLine(end_sleep.get(Calendar.HOUR_OF_DAY), "结束睡眠");
            stop_sleep_XAxis.setLineWidth(2f);
            stop_sleep_XAxis.setLineColor(Color.parseColor("#003366"));
            stop_sleep_XAxis.enableDashedLine(10f, 10f, 0f);
            stop_sleep_XAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            stop_sleep_XAxis.setTextSize(6f);

            LimitLine ll1 = new LimitLine(100f, "Upper Limit");
            ll1.setLineWidth(2f);
            ll1.enableDashedLine(10f, 10f, 0f);
            ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
            ll1.setTextSize(6f);
            //ll1.setTypeface(tfRegular);

            LimitLine ll2 = new LimitLine(0f, "Lower Limit");
            ll2.setLineWidth(2f);
            ll2.enableDashedLine(10f, 10f, 0f);
            ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            ll2.setTextSize(6f);
            //ll2.setTypeface(tfRegular);

            // draw limit lines behind data instead of on top
            yAxis.setDrawLimitLinesBehindData(true);
            xAxis.setDrawLimitLinesBehindData(true);

            // add limit lines
            yAxis.addLimitLine(ll1);
            yAxis.addLimitLine(ll2);
            xAxis.addLimitLine(llXAxis);
            xAxis.addLimitLine(start_sleep_XAxis);
            xAxis.addLimitLine(stop_sleep_XAxis);
        }
    }

    private void initThinkingAndDeterminationLineChart(){
        AVQuery<AVObject> query = new AVQuery<>("EnergyEvaluation");
        // 启动查询缓存
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.setMaxCacheAge(24 * 3600 * 1000); //设置为一天，单位毫秒
        query.whereEqualTo("UserId", AVUser.getCurrentUser().getObjectId());
        query.orderByDescending("updateAt"); //按修改时间降序排列
        query.limit(100);
        query.findInBackground().subscribe(new Observer<List<AVObject>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<AVObject> avObjects) {
                if (avObjects!=null){
                    // Thinking
                    int[] ThinkingScore = new int[24]; // 初始化一个24大小数组，用来存储24小时中各个时刻的精力评分均值

                    int[] Determination = new int[24]; // 同理
                    for(int i=0;i<avObjects.size();i++){
                        Calendar time = Calendar.getInstance();
                        time.setTime(avObjects.get(i).getDate("time"));
                        int hour = time.get(Calendar.HOUR_OF_DAY);
                        if(ThinkingScore[hour]!=0){
                            ThinkingScore[hour]+=avObjects.get(i).getInt("thinking");
                            ThinkingScore[hour] /= 2;
                        }else{
                            ThinkingScore[hour] = avObjects.get(i).getInt("thinking");
                        }
                        if(Determination[hour]!=0){
                            Determination[hour]+=avObjects.get(i).getInt("determination");
                            Determination[hour] /= 2;
                        }else{
                            Determination[hour] = avObjects.get(i).getInt("determination");
                        }
                    }
                    // 加入图表
                    LoadLineChartData(ThinkingLineChart,ThinkingScore);
                    LoadLineChartData(DeterminationLineChart,Determination);
                    // 传入思维和意志评分
                    initEmotionLineChart(ThinkingScore,Determination);
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }
    private void initEmotionLineChart(int[] thinking, int[] determination){
        AVQuery<AVObject> query = new AVQuery<>("MoodWhisper");
        // 启动查询缓存
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.setMaxCacheAge(24 * 3600 * 1000); //设置为一天，单位毫秒
        query.whereEqualTo("UserId", AVUser.getCurrentUser().getObjectId());
        query.orderByDescending("updateAt"); //按修改时间降序排列
        query.limit(100);
        query.findInBackground().subscribe(new Observer<List<AVObject>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<AVObject> avObjects) {
                int[] Score = new int[24]; // 初始化一个24大小数组，用来存储24小时中各个时刻的精力评分均值
                for(int i=0;i<avObjects.size();i++){
                    Calendar time = Calendar.getInstance();
                    time.setTime(avObjects.get(i).getDate("time"));
                    int hour = time.get(Calendar.HOUR_OF_DAY);
                    if(Score[hour]!=0){
                        Score[hour] += avObjects.get(i).getDouble("positive_prob")*100;
                        Score[hour] /= 2;
                    }else{
                        Score[hour] = (int)(avObjects.get(i).getDouble("positive_prob")*100);
                    }
                }
                LoadLineChartData(EmotionLineChart,Score);
                // 联动体能精力
                initPhysicalLineChart(Score,thinking,determination);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }
    private void initPhysicalLineChart(int[] emotional, int[] thinking, int[] determination){
        AVQuery<AVObject> query = new AVQuery<>("stepcounter");
        // 启动查询缓存
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.setMaxCacheAge(24 * 3600 * 1000); //设置为一天，单位毫秒
        query.whereEqualTo("UserId", AVUser.getCurrentUser().getObjectId());
        query.orderByDescending("updateAt"); //按修改时间降序排列
        query.limit(100);
        query.findInBackground().subscribe(new Observer<List<AVObject>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<AVObject> avObjects) {
                if(avObjects!=null){
                    int[] StepCounter = new int[24]; // 初始化一个24大小数组，用来存储24小时中各个时刻的精力评分均值
                    int sumStep = 0;
                    // 1.计算最近几天内产生的100条数据（大约三天）内24小时各个时刻的总步数
                    for(int i=0;i<avObjects.size();i++){
                        Calendar time = Calendar.getInstance();
                        time.setTime(avObjects.get(i).getDate("time"));
                        int hour = time.get(Calendar.HOUR_OF_DAY);
                        StepCounter[hour] += avObjects.get(i).getInt("count");
                        sumStep += avObjects.get(i).getInt("count");
                    }
                    Log.d("EnergyFragement","总步数：" + sumStep);
                    // 2. 根据各个小时的总步数在总步数中的占比计算百分制评分
                    int[] Score = new int[24];
//                for(int j=0;j<24;j++){
//                    Score[j] = StepCounter[j] * 100 / sumStep;
//                    Log.d("EnergyFragement",j+ "时刻" + "步数" + StepCounter[j]  +  ", 占比：" + Score[j]);
//                }
                    // 步数 超总步数的50% 为100，40% 90 30% 80 20% 70 10 % 60
                    for(int k=0;k<24;k++){
                        if(StepCounter[k]>(sumStep*0.2)){
                            Score[k] = 100;
                        }else if(StepCounter[k]>(sumStep*0.15)){
                            Score[k] = 90;
                        }else if(StepCounter[k]>(sumStep*0.10)){
                            Score[k] = 80;
                        }else if(StepCounter[k]>(sumStep*0.05)){
                            Score[k] = 70;
                        }else if(StepCounter[k]>(sumStep*0.02)){
                            Score[k] = 60;
                        }else if(StepCounter[k]>10){
                            Score[k] = 10;
                        }else{
                            Score[k] = 0;
                        }
//                    Log.d("EnergyFragement",k+ "时刻" + "步数" + StepCounter[k]  +  ", 评分：" + Score[k]);
                    }
                    LoadLineChartData(PhysicalLineChart,Score);
                    initEnergyLineChart(Score,emotional,thinking,determination);
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }
    private void LoadLineChartData(LineChart chart, int[] chartdata){
//        Log.i("EnergyFragement","Load liveline chart");
        //1.设置x轴和y轴的点
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < 24; i++){

//            entries.add(new Entry(i,new Random().nextInt(100)));
            entries.add(new Entry(i,chartdata[i]));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Score"); // add entries to dataset


        //3.chart设置数据
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate(); // refresh

        List<ILineDataSet> sets = chart.getData()
                .getDataSets();
        for (ILineDataSet iSet : sets) {

            // 绘制
            LineDataSet set = (LineDataSet) iSet;
            if (set.isDrawFilledEnabled())
                set.setDrawFilled(false);
            else
                set.setDrawFilled(true);

            //平滑
            set.setMode(set.getMode() == LineDataSet.Mode.CUBIC_BEZIER
                    ? LineDataSet.Mode.LINEAR
                    :  LineDataSet.Mode.CUBIC_BEZIER);
        }
        chart.invalidate();

        //动画
        chart.animateXY(2000, 2000);

    }

    private void initEnergyLineChart(int[] physical, int[] emotional, int[] thinking, int[] determination){

        int[] Score = new int[24];
        for(int i=0;i<24;i++){
            physicalScore[i] = physical[i];
            emotionalScore[i] = emotional[i];
            thinkingScore[i] = thinking[i];
            determinationScore[i] = determination[i];


            int ScoreSum = physical[i] + emotional[i] + thinking[i] + determination[i];
            if(physical[i]!=0){
                Score[i] += (physical[i] * (physical[i] * 100 / ScoreSum) / 100);
            }
            if(emotional[i]!=0){
                Score[i] += (emotional[i] * (emotional[i] * 100 / ScoreSum) / 100);
            }
            if(thinking[i]!=0){
                Score[i] += (thinking[i] * (thinking[i] * 100 / ScoreSum) / 100);
            }
            if(determination[i]!=0){
                Score[i] += (determination[i] * (determination[i] * 100 / ScoreSum) / 100);
            }
            Log.d("EnergyFragement","时刻" + i + "体能" + physical[i] +  "情感" +  emotional[i] + "思维" + thinking[i] + "意志" + determination[i] + "总计" + ScoreSum + "总评" + Score[i]);
        }
        LoadLineChartData(EnergyLineChart,Score);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
