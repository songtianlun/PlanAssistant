package com.hgo.planassistant.fragement;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.hgo.planassistant.R;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PlanFragment extends Fragment {

    private NestedScrollView PlannestedScrollView;
    private TextView tv_card_fragement_plan_vigor_score,tv_card_fragement_plan_vigor_suggest;

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        PlannestedScrollView = (NestedScrollView) inflater.inflate(R.layout.fragment_plan, container, false);
//
        tv_card_fragement_plan_vigor_score = PlannestedScrollView.findViewById(R.id.tv_card_fragement_plan_vigor_score);
        tv_card_fragement_plan_vigor_suggest = PlannestedScrollView.findViewById(R.id.tv_card_fragement_plan_vigor_suggest);
        return PlannestedScrollView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Calendar now = Calendar.getInstance();

        LoadVigorData(now.get(Calendar.HOUR_OF_DAY));



    }
    // TODO: Rename method, update argument and hook method into UI event


    @Override
    public void onDetach() {
        super.onDetach();
    }

    //读取指定时间的精力值
    private void LoadVigorData(int hour){
//        int hour_score = -1;
        int[] chareData = new int[24];
        Arrays.fill(chareData,0);//数组的批量赋值。

        AVQuery<AVObject> query = new AVQuery<>("liveline");
        query.whereEqualTo("UserId", AVUser.getCurrentUser().getObjectId());
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);// 启动查询缓存
        query.setMaxCacheAge(24 * 3600 * 1000); //设置为一天，单位毫秒
        query.limit(1000);
        query.orderByDescending("createdAt");// 按时间，降序排列
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if(list!=null){
                Log.i("LiveLIneActivity","共查询到：" + list.size() + "条数据。");
                for (AVObject obj: list){
                    Calendar livetime = Calendar.getInstance();
                    livetime.setTime((Date)obj.get("livetime"));//获取时间
                    int hour = livetime.get(Calendar.HOUR_OF_DAY);
                    int score = (int)obj.get("score");
                    if(chareData[hour]==0){
                        chareData[hour] = score;
                    }else{
                        chareData[hour] = (score+chareData[hour])/2;
                    }
                }
                int hour_score = chareData[hour];
                if(hour_score==0){
                    hour_score=50;
                }
                Log.i("PlanFragement",hour + "的精力分数"+ hour_score +"分");
                String score_str = hour_score + "分";
                tv_card_fragement_plan_vigor_score.setText(score_str);
                tv_card_fragement_plan_vigor_suggest.setText(LoadSuggest(hour_score));
//                LoadLinechart(chareData);
                }
            }
        });
    }

    private String LoadSuggest(int score){
        String suggest="";
        if(score>90){
            suggest = "当前精力充沛，建议进行今天的主要日程！";
        }else if(score>70){
            suggest = "您当前的精力状况较好，建议进行较重要的日程！";
        }else if(score>60){
            suggest = "勉勉强强，建议稍事休息，灵活安排日程！";
        }else {
            suggest = "昏昏欲睡，您当前精力较差，建议休眠补充精力！";
        }

        return suggest;
    }
}
