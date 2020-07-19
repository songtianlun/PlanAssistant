package com.hgo.planassistant.fragement;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.hgo.planassistant.App;
import com.hgo.planassistant.R;
import com.hgo.planassistant.tools.SuggestionPopup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.leancloud.AVObject;
import cn.leancloud.AVQuery;
import cn.leancloud.AVUser;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static android.content.Context.MODE_PRIVATE;

public class TaskFragment extends Fragment implements View.OnClickListener{



    private int Pages = -1;
    private int sumPages = -1;
    private int MaxQuery = 100;
    private List<AVObject> now_list;
    private List<AVObject> urgent_important_list;
    private List<AVObject> noturgent_important_list;
    private List<AVObject> urgent_unimportant_list;
    private List<AVObject> noturgent_unimportant_list;

    private MaterialCardView card_task_tips, card_task_urgent_important, card_task_noturgent_important, card_task_urgent_unimportant,card_task_noturgent_unimportant;
    private TextView card_task_urgent_important_textview, card_task_noturgent_important_textview, card_task_urgent_unimportant_textview, card_task_noturgent_unimportant_textview;

    private NestedScrollView TasknestedScrollView;
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
        TasknestedScrollView = (NestedScrollView) inflater.inflate(R.layout.fragment_task, container, false);

        card_task_tips = TasknestedScrollView.findViewById(R.id.card_task_tips);
        card_task_urgent_important = TasknestedScrollView.findViewById(R.id.card_task_urgent_important);
        card_task_noturgent_important = TasknestedScrollView.findViewById(R.id.card_task_noturgent_important);
        card_task_urgent_unimportant = TasknestedScrollView.findViewById(R.id.card_task_urgent_unimportant);
        card_task_noturgent_unimportant = TasknestedScrollView.findViewById(R.id.card_task_noturgent_unimportant);
        card_task_urgent_important_textview = TasknestedScrollView.findViewById(R.id.card_task_urgent_important_textview);
        card_task_noturgent_important_textview = TasknestedScrollView.findViewById(R.id.card_task_noturgent_important_textview);
        card_task_urgent_unimportant_textview = TasknestedScrollView.findViewById(R.id.card_task_urgent_unimportant_textview);
        card_task_noturgent_unimportant_textview = TasknestedScrollView.findViewById(R.id.card_task_noturgent_unimportant_textview);

        return TasknestedScrollView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        card_task_tips.setOnClickListener(this);
        card_task_urgent_important.setOnClickListener(this);
        card_task_noturgent_important.setOnClickListener(this);
        card_task_urgent_unimportant.setOnClickListener(this);
        card_task_noturgent_unimportant.setOnClickListener(this);

    }
    // TODO: Rename method, update argument and hook method into UI event


    @Override
    public void onStart() {
        super.onStart();
        LoadTaskData();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        SuggestionPopup suggestionPopup = new SuggestionPopup();
        switch (view.getId()){
            case R.id.card_task_tips:
                LoadTaskData();
                break;
            case R.id.card_task_urgent_important:
                suggestionPopup.GetUrgentImportantPopup(getContext());
                break;
            case R.id.card_task_noturgent_important:
                suggestionPopup.GetNotUrgentImportantPopup(getContext());
                break;
            case R.id.card_task_urgent_unimportant:
                suggestionPopup.GetUrgentUnimportantPopup(getContext());
                break;
            case R.id.card_task_noturgent_unimportant:
                suggestionPopup.GetNotUrgentUnimportantPopup(getContext());
                break;
            default:
                break;
        }
    }

    private void LoadTaskData(){
        now_list = new ArrayList<>();
        AVQuery<AVObject> query = new AVQuery<>("Task");
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);// 启动查询缓存
        query.setMaxCacheAge(24 * 3600 * 1000); //设置为一天，单位毫秒
        query.whereEqualTo("UserId", AVUser.getCurrentUser().getObjectId());
        query.whereNotEqualTo("done", true);
        query.whereGreaterThanOrEqualTo("end_time",Calendar.getInstance().getTime());// 结束时间大于当前时间
        query.limit(MaxQuery);
        query.orderByDescending("task_importance"); // 按重要性降序
        query.addAscendingOrder("start_time"); // 按开始时间升序
        query.countInBackground().subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {
                sumPages = integer/MaxQuery + 1;
                query.findInBackground().subscribe(new Observer<List<AVObject>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<AVObject> avObjects) {
                        if(avObjects!=null){
                            Log.i("TaskFragment","共查询到：" + avObjects.size() + "条数据。");
                            now_list.addAll(avObjects);
                            ClassificationTask(avObjects);
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

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void ClassificationTask(List<AVObject> all_task){
        int urgent_day = App.getApplication().getSharedPreferences("setting",MODE_PRIVATE).getInt("pref_seek_personal_urgent_day",10);

        urgent_important_list = new ArrayList<>();
        noturgent_important_list = new ArrayList<>();
        urgent_unimportant_list = new ArrayList<>();
        noturgent_unimportant_list = new ArrayList<>();
        for(int i=0;i<all_task.size();i++){
            if(all_task.get(i).getInt("task_importance")>5){
                // 重要
                Calendar forward7day_calendar = Calendar.getInstance();
                forward7day_calendar.add(Calendar.DATE,urgent_day);
                Date forward7day = forward7day_calendar.getTime();
                if(all_task.get(i).getDate("end_time")!=null&&all_task.get(i).getDate("end_time").getTime()<forward7day.getTime()){
                    // 截止时间大于7天 判为紧急
                    // 重要且紧急
                    urgent_important_list.add(all_task.get(i));
                    LoadDateToView(urgent_important_list,card_task_urgent_important_textview);
                }else{
                    // 重要但不紧急
                    noturgent_important_list.add(all_task.get(i));
                    LoadDateToView(noturgent_important_list,card_task_noturgent_important_textview);
                }
            }
            else{
                // 不重要
                Calendar forward7day_calendar = Calendar.getInstance();
                forward7day_calendar.add(Calendar.DATE,urgent_day);
                Date forward7day = forward7day_calendar.getTime();
                if(all_task.get(i).getDate("end_time")!=null&&all_task.get(i).getDate("end_time").getTime()<forward7day.getTime()){
                    // 截止时间大于7天 判为紧急
                    // 不重要但紧急
                    urgent_unimportant_list.add(all_task.get(i));
                    LoadDateToView(urgent_unimportant_list,card_task_urgent_unimportant_textview);
                }else{
                    // 不紧急也不重要
                    noturgent_unimportant_list.add(all_task.get(i));
                    LoadDateToView(noturgent_unimportant_list,card_task_noturgent_unimportant_textview);
                }
            }
        }

    }
    private void LoadDateToView(List<AVObject> urgent_important_list, List<AVObject> noturgent_important_list, List<AVObject> urgent_unimportant_list, List<AVObject> noturgent_unimportant_list){

    }
    private void LoadDateToView(List<AVObject> list, TextView textView){
        String string = new String();
        for(int j=0;j<list.size();j++){
            string += ("<p>" + " &#8226; " +  list.get(j).getString("task_name") + "</p>");
        }
        // Html.fromHtml可以将Html代码转换成对应的text
        textView.setText(Html.fromHtml(string));
    }
    private void syncToSystemCalendar(){

    }



    //读取指定时间的精力值
//    private void LoadVigorData(int hour){
////        int hour_score = -1;
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
//                    if(chareData[hour]==0){
//                        chareData[hour] = score;
//                    }else{
//                        chareData[hour] = (score+chareData[hour])/2;
//                    }
//                }
//                int hour_score = chareData[hour];
//                if(hour_score==0){
//                    hour_score=50;
//                }
//                Log.i("PlanFragement",hour + "的精力分数"+ hour_score +"分");
//                String score_str = hour_score + "分";
//                tv_card_fragement_plan_vigor_score.setText(score_str);
//                tv_card_fragement_plan_vigor_suggest.setText(LoadSuggest(hour_score));
////                LoadLinechart(chareData);
//                }
//            }
//        });
//    }

//    private String LoadSuggest(int score){
//        String suggest="";
//        if(score>90){
//            suggest = "当前精力充沛，建议进行今天的主要日程！";
//        }else if(score>70){
//            suggest = "您当前的精力状况较好，建议进行较重要的日程！";
//        }else if(score>60){
//            suggest = "勉勉强强，建议稍事休息，灵活安排日程！";
//        }else {
//            suggest = "昏昏欲睡，您当前精力较差，建议休眠补充精力！";
//        }
//
//        return suggest;
//    }
}
