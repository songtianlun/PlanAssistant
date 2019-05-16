package com.hgo.planassistant.fragement;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
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
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.material.snackbar.Snackbar;
import com.hgo.planassistant.App;
import com.hgo.planassistant.R;
import com.hgo.planassistant.activity.MainActivity;
import com.hgo.planassistant.custom.MyMarkerView;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class HomeFragment extends Fragment implements View.OnClickListener, View.OnTouchListener,
        SeekBar.OnSeekBarChangeListener ,OnChartValueSelectedListener {
    // liveLinechart
    private LineChart chart;
    private Button loadlinechart,savetogallary;
    private CardView card__home_liveline, card__home_location, card__home_plan;
    NestedScrollView nestedScrollView;
    private static final int PERMISSION_STORAGE = 0;

    MapView mapView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //设置accessToken
        Mapbox.getInstance(App.getContext(), getString(R.string.mapbox_access_token));
        // Inflate the layout for this fragment
        nestedScrollView = (NestedScrollView) inflater.inflate(R.layout.fragment_home, container, false);
        loadlinechart = nestedScrollView.findViewById(R.id.loadlinechart);
        savetogallary = nestedScrollView.findViewById(R.id.savetogallary);
        card__home_liveline = nestedScrollView.findViewById(R.id.card_home_liveline);
        card__home_location = nestedScrollView.findViewById(R.id.card_home_location);
        card__home_plan = nestedScrollView.findViewById(R.id.card_home_plan);
        mapView = (MapView) nestedScrollView.findViewById(R.id.mapView);
        Initchart(nestedScrollView);

        return nestedScrollView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        loadlinechart.setOnClickListener(v -> {
//            Log.i("HomeFragement","clickLoadChart");
//            Snackbar.make(v, "加载图表", Snackbar.LENGTH_SHORT).show();
//        });

        savetogallary.setOnClickListener(this);
        loadlinechart.setOnClickListener(this);
        card__home_liveline.setOnTouchListener(this);
        card__home_location.setOnTouchListener(this);
        card__home_plan.setOnTouchListener(this);

        LoadLinechart();
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {

                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

                        // Map is set up and the style has loaded. Now you can add data or make other map adjustments

                    }
                });
            }
        });

    }

    @Override
    public void onClick(View v) {
        Log.i("HomeFragement","click");
        switch (v.getId()) {
            case R.id.loadlinechart:
                Log.i("HomeFragement","clickLoadChart");
                Snackbar.make(v, "加载图表", Snackbar.LENGTH_SHORT).show();
                LoadLinechart();
                break;
            case R.id.savetogallary:
                chartsavetogallary();
                break;
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





    private void Initchart(NestedScrollView nestedScrollView){
        chart = nestedScrollView.findViewById(R.id.livelinechart);
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
            LimitLine llXAxis = new LimitLine(9f, "Index 10");
            llXAxis.setLineWidth(4f);
            llXAxis.enableDashedLine(10f, 10f, 0f);
            llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            llXAxis.setTextSize(10f);
            //llXAxis.setTypeface(tfRegular);

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
            //xAxis.addLimitLine(llXAxis);
        }
    }
    private void LoadLinechart(){
        Log.i("HomeFragement","Load liveline chart");


        //1.设置x轴和y轴的点
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < 24; i++){

            entries.add(new Entry(i,new Random().nextInt(100)));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset


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
    private void chartsavetogallary(){
        Log.i("HomeFragement","Chart Save to gallary!");
        if (ContextCompat.checkSelfPermission(App.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            if (chart.saveToGallery("规划助手—精力曲线" + "_" + System.currentTimeMillis(), 70))
                Toast.makeText(App.getContext(), "Saving SUCCESSFUL!",
                        Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(App.getContext(), "Saving FAILED!", Toast.LENGTH_SHORT)
                        .show();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Activity activity = this.getActivity();
                Snackbar.make(chart, "Write permission is required to save image to gallery", Snackbar.LENGTH_INDEFINITE)
                        .setAction(android.R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_STORAGE);
                            }
                        }).show();
            } else {
                Toast.makeText(App.getContext(), "Permission Required!", Toast.LENGTH_SHORT)
                        .show();
                ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_STORAGE);
            }
        }
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
}
