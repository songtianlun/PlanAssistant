package com.hgo.planassistant.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.hgo.planassistant.App;
import com.hgo.planassistant.R;
import com.hgo.planassistant.tools.DateFormat;
import com.umeng.commonsdk.debug.D;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StepCounterActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener,
        OnChartValueSelectedListener {

    private PieChart DayPieChart;
    private BarChart DayBarChart;
    private BarChart SevenDayBarChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_counter);

        Toolbar toolbar = findViewById(R.id.toolbar_step_counter);
        setToolbar(toolbar);
        toolbar.setTitle("计步器");

        DateFormat dateFormat = new DateFormat();
        initView();
        initDayPieChart();
        setDayPieChartData();

        Calendar quare_end_time = Calendar.getInstance();
        Calendar quare_start_time = Calendar.getInstance();
        quare_end_time.setTime(dateFormat.FilterHourAndMinuteAndSecond(Calendar.getInstance()).getTime());
        quare_end_time.add(Calendar.DATE,1);
        quare_start_time.setTime(quare_end_time.getTime());
        quare_start_time.add(Calendar.DATE,-7);
        initSevenDayBarChart(quare_start_time.getTime(),quare_end_time.getTime(),7);
    }

    private void initView(){
        DayPieChart = findViewById(R.id.card_activity_step_counter_todaystep_counter_piechart);
        DayBarChart = findViewById(R.id.card_activity_step_counter_day_trend_barchart);
        SevenDayBarChart = findViewById(R.id.card_activity_step_counter_sevenday_trend_barchart);
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
        DateFormat dateFormat = new DateFormat();
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
                if(avObjects!=null){
                    int sumStep = 0;
                    Log.i("StepCounterActivity","查询到数据总数："+avObjects.size());
                    initDayBarChart(avObjects);
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
            }
        });


    }

    private void initDayBarChart(List<AVObject> in){
        DayBarChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        DayBarChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        DayBarChart.setPinchZoom(false);

        DayBarChart.setDrawBarShadow(false);
        DayBarChart.setDrawGridBackground(false);

        XAxis xAxis = DayBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(true); //绘制标签


        ValueFormatter valueFormatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int value_int = (int)value;
                return value_int + "时";
            }
        };
        xAxis.setValueFormatter(valueFormatter);//设置自定义格式，在绘制之前动态调整x的值。


        DayBarChart.getAxisLeft().setDrawGridLines(false);

        // add a nice and smooth animation
        DayBarChart.animateY(1500);

        DayBarChart.getLegend().setEnabled(false);


        // 设置数据
        ArrayList<BarEntry> values = new ArrayList<>(24);
        int[] PrecisionSum = new int[6]; //初始化为默认值,int型为0

        for (AVObject obj: in){
            int StepCounter = obj.getInt("count");
            Calendar time = Calendar.getInstance();
            time.setTime(obj.getDate("time"));
            values.add(new BarEntry(time.get(Calendar.HOUR_OF_DAY),StepCounter));
        }

        BarDataSet set1;

        if (DayBarChart.getData() != null &&
                DayBarChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) DayBarChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            DayBarChart.getData().notifyDataChanged();
            DayBarChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(values, "Data Set");
            set1.setColors(ColorTemplate.VORDIPLOM_COLORS);
            set1.setDrawValues(false);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

//            ArrayList<String> xVals = new ArrayList<String>();
//            xVals.add("1.Q"); xVals.add("2.Q"); xVals.add("3.Q"); xVals.add("4.Q");

            BarData data = new BarData(dataSets);
            DayBarChart.setData(data);
            DayBarChart.setFitBars(true);
        }

        DayBarChart.invalidate();
    }

    private void initSevenDayBarChart(Date start_time, Date end_time, int SumDay){
        DateFormat dateFormat = new DateFormat();
        Log.d("StepCounterActivity","查询开始时间："+dateFormat.GetDetailDescription(start_time)+", 结束时间：" + dateFormat.GetDetailDescription(end_time));
        SevenDayBarChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        SevenDayBarChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        SevenDayBarChart.setPinchZoom(false);

        SevenDayBarChart.setDrawBarShadow(false);
        SevenDayBarChart.setDrawGridBackground(false);

        XAxis xAxis = SevenDayBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(true); //绘制标签


        ValueFormatter valueFormatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int value_int = (int)value;
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(start_time);
                calendar.add(Calendar.DAY_OF_MONTH,value_int);
                return calendar.get(Calendar.DAY_OF_MONTH) + "日";
            }
        };
        xAxis.setValueFormatter(valueFormatter);//设置自定义格式，在绘制之前动态调整x的值。


        SevenDayBarChart.getAxisLeft().setDrawGridLines(false);

        // add a nice and smooth animation
        SevenDayBarChart.animateY(1500);

        SevenDayBarChart.getLegend().setEnabled(false);


        // 设置数据
        ArrayList<BarEntry> values = new ArrayList<>();

        AVQuery<AVObject> query = new AVQuery<>("stepcounter");
        query.whereEqualTo("UserId", AVUser.getCurrentUser().getObjectId());
        query.whereGreaterThan("time",start_time);
        query.whereLessThan("time",end_time);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException avException) {
                if(avObjects!=null){
                    Log.d("StepCounterActivity","多日步数趋势共查到数据条数:"+avObjects.size());
                    int[] StepSum = new int[SumDay+1]; //初始化为默认值,int型为0
                    Calendar start_calendat = Calendar.getInstance();
                    start_calendat.setTime(start_time);
                    for(int i=0;i<avObjects.size();i++){
                        Calendar getDay = Calendar.getInstance();
                        getDay.setTime(avObjects.get(i).getDate("time"));
                        Log.d("StepCounterActivity","处理数据时刻："+dateFormat.GetDetailDescription(getDay) + "索引："+getDay.get(Calendar.DATE));

                        // 将 开始时间到当前时间之间相差的天数 作为数组坐标
//                    StepSum[(dateFormat.FilterHourAndMinuteAndSecond(getDay).get(Calendar.DATE)) - dateFormat.FilterHourAndMinuteAndSecond(start_calendat).get(Calendar.DATE)] += avObjects.get(i).getInt("count");
                        // 计算毫秒差 作为数组坐标
                        StepSum[(int)Math.abs( ( dateFormat.FilterHourAndMinuteAndSecond(getDay).getTime().getTime() - dateFormat.FilterHourAndMinuteAndSecond(start_calendat).getTime().getTime())/86400000)] += avObjects.get(i).getInt("count");
                    }
                    for(int i=0;i<SumDay;i++){
                        // 直接获取日期+1，忽略月末异常，采用日期加一再取日期的方法解决
//                    values.add(new BarEntry((start_calendat.get(Calendar.DATE)+i),StepSum[i]));
                        Calendar nowday = Calendar.getInstance();
                        nowday.setTime(start_time);
                        nowday.add(Calendar.DATE,i);
                        values.add(new BarEntry(i,StepSum[i]));
                    }
                    BarDataSet set1;

                    if (SevenDayBarChart.getData() != null &&
                            SevenDayBarChart.getData().getDataSetCount() > 0) {
                        set1 = (BarDataSet) SevenDayBarChart.getData().getDataSetByIndex(0);
                        set1.setValues(values);
                        SevenDayBarChart.getData().notifyDataChanged();
                        SevenDayBarChart.notifyDataSetChanged();
                    } else {
                        set1 = new BarDataSet(values, "Data Set");
                        set1.setColors(ColorTemplate.VORDIPLOM_COLORS);
                        set1.setDrawValues(false);

                        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
                        dataSets.add(set1);

//            ArrayList<String> xVals = new ArrayList<String>();
//            xVals.add("1.Q"); xVals.add("2.Q"); xVals.add("3.Q"); xVals.add("4.Q");

                        BarData data = new BarData(dataSets);
                        SevenDayBarChart.setData(data);
                        SevenDayBarChart.setFitBars(true);
                    }

                    SevenDayBarChart.invalidate();
                }

            }
        });
    }

    private void setData(int count, float range) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (int i = 0; i < count ; i++) {
            entries.add(new PieEntry((float) ((Math.random() * range) + range / 5),
                    2));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Election Results");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

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
        //dataSet.setSelectionShift(0f);

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



    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

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
}
