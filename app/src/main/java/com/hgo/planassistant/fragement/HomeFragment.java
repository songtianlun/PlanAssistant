package com.hgo.planassistant.fragement;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.SeekBar;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link HomeFragment.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link HomeFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class HomeFragment extends Fragment implements View.OnClickListener, View.OnTouchListener
         {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

//    private OnFragmentInteractionListener mListener;

    // liveLinechart
    private LineChart chart;
    private Button loadlinechart;
    private CardView card__home_liveline, card__home_location, card__home_plan;

//    public HomeFragment() {
//        // Required empty public constructor
//    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        NestedScrollView nestedScrollView = (NestedScrollView) inflater.inflate(R.layout.fragment_home, container, false);

//        Initchart(nestedScrollView);

        chart = nestedScrollView.findViewById(R.id.livelinechart);
        loadlinechart = nestedScrollView.findViewById(R.id.loadlinechart);

        card__home_liveline = nestedScrollView.findViewById(R.id.card_home_liveline);
        card__home_location = nestedScrollView.findViewById(R.id.card_home_location);
        card__home_plan = nestedScrollView.findViewById(R.id.card_home_plan);

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        card__home_liveline.setOnTouchListener(this);
        card__home_location.setOnTouchListener(this);
        card__home_plan.setOnTouchListener(this);

        loadlinechart.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Log.i("HomeFragement","click");
        switch (view.getId()) {
            case R.id.loadlinechart:
                Log.i("HomeFragement","clickLoadChart");
                Snackbar.make(view, "加载图表", Snackbar.LENGTH_SHORT).show();
                LoadLinechart();
                break;
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }



    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ObjectAnimator downAnimator = ObjectAnimator.ofFloat(view, "translationZ", 16);
                downAnimator.setDuration(200);
                downAnimator.setInterpolator(new DecelerateInterpolator());
                downAnimator.start();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                ObjectAnimator upAnimator = ObjectAnimator.ofFloat(view, "translationZ", 0);
                upAnimator.setDuration(200);
                upAnimator.setInterpolator(new AccelerateInterpolator());
                upAnimator.start();
                break;
        }
        return false;
    }


    private void Initchart(NestedScrollView nestedScrollView){
        //initchart
        chart = nestedScrollView.findViewById(R.id.livelinechart);
        loadlinechart = nestedScrollView.findViewById(R.id.loadlinechart);

        card__home_liveline = nestedScrollView.findViewById(R.id.card_home_liveline);
        card__home_location = nestedScrollView.findViewById(R.id.card_home_location);
        card__home_plan = nestedScrollView.findViewById(R.id.card_home_plan);
    }
    private void LoadLinechart(){
        Log.i("HomeFragement","Load liveline chart");


        //1.设置x轴和y轴的点
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < 12; i++){
            entries.add(new Entry(i, 100));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset

        //3.chart设置数据
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate(); // refresh

    }
}
