package com.hgo.planassistant.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.hgo.planassistant.App;
import com.hgo.planassistant.R;
import com.hgo.planassistant.adapter.TaskRecyclerViewAdapter;
import com.hgo.planassistant.tools.DateFormat;
import com.hgo.planassistant.util.AppUtils;
import com.hgo.planassistant.util.CalendarReminderUtils;
import com.hgo.planassistant.view.ItemTouchHelperCallback;
import com.warkiz.widget.IndicatorSeekBar;
import com.zzhoujay.richtext.RichText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TaskActivity extends BaseActivity implements View.OnClickListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private ExtendedFloatingActionButton efab;
    private TaskRecyclerViewAdapter adapter;
    private int color = 0;
    private List<AVObject> now_list;
    private AVObject insertData;
    private boolean loading;
    private int loadTimes;
    private Context mContext;
//    private Calendar task_start_time;
//    private Calendar task_end_time;
//
//    private int Pages = -1;
//    private int sumPages = -1;
    static public int MaxQuery = 1000;
    private String task_type = "all";

    private BottomSheetDialog mBottomSheetDialog;

//    private AppCompatImageButton ib_marker;
//    private TextView TV_start_time;
//    private TextView TV_start_date;
//    private TextView TV_end_time;
//    private TextView TV_end_date;
//    private TextInputEditText edit_name;
//    private TextInputEditText edit_location;
//    private IndicatorSeekBar seekBar_importance;
//    private TextView TV_description;
//    private AppCompatSpinner spinner_remind;
//    private AppCompatSpinner spinner_cycle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        Toolbar toolbar = findViewById(R.id.toolbar_task);
        setToolbar(toolbar);

        mContext = this;
//        task_start_time = Calendar.getInstance();
//        task_end_time = Calendar.getInstance();
//        task_end_time.add(Calendar.HOUR_OF_DAY,1);
        initDate();

        RichText.initCacheDir(this);
        RichText.debugMode = true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refreshData(task_type);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_task, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Intent intent = new Intent();
        switch (item.getItemId()) {
            case R.id.menu_home_task_all:
                task_type = "all";
//                refreshData(task_type);
                filterDataAndLoad(now_list,task_type);
                break;
            case R.id.menu_home_task_today:
                task_type = "today";
//                refreshData(task_type);
                filterDataAndLoad(now_list,task_type);
                break;
            case R.id.menu_home_task_today_planning:
                task_type = "today_planning";
//                refreshData(task_type);
                filterDataAndLoad(now_list,task_type);
                break;
            case R.id.menu_home_task_late:
                task_type = "late";
//                refreshData(task_type);
                filterDataAndLoad(now_list,task_type);
                break;
            case R.id.menu_home_task_undo:
                task_type = "undo";
//                refreshData(task_type);
                filterDataAndLoad(now_list,task_type);
                break;
            case R.id.menu_home_task_do:
                task_type = "do";
//                refreshData(task_type);
                filterDataAndLoad(now_list,task_type);
                break;
            case R.id.menu_home_task_urgent_importance:
                task_type = "urgent_importance";
//                refreshData(task_type);
                filterDataAndLoad(now_list,task_type);
                break;
            case R.id.menu_home_task_noturgent_importance:
                task_type = "noturgent_importance";
//                refreshData(task_type);
                filterDataAndLoad(now_list,task_type);
                break;
            case R.id.menu_home_task_urgent_unimportance:
                task_type = "urgent_unimportance";
//                refreshData(task_type);
                filterDataAndLoad(now_list,task_type);
                break;
            case R.id.menu_home_task_noturgent_unimportance:
                task_type = "noturgent_unimportance";
//                refreshData(task_type);
                filterDataAndLoad(now_list,task_type);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initDate(){
//        Pages = 0;// 页数从0开始
        task_type = "all";
        now_list = new ArrayList<>();
        AVQuery<AVObject> query = new AVQuery<>("Task");
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);// 启动查询缓存
        query.setMaxCacheAge(24 * 3600 * 1000); //设置为一天，单位毫秒
        query.whereEqualTo("UserId", AVUser.getCurrentUser().getObjectId());
        query.limit(MaxQuery);
        query.orderByAscending("done"); //按是否完成，升序,先false，后true
        query.addDescendingOrder("task_importance"); // 按照重要程序降序
        query.addAscendingOrder("start_time"); //按开始时间升序
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int count, AVException e) {
                if(count>=MaxQuery){
                    Toast.makeText(App.getContext(), "您的任务总数超过系统限制，仅显示前1000条，如需查询所有数据请联系软件作者！", Toast.LENGTH_SHORT).show();
                }
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if(list!=null){
                            Log.i("LiveLIneActivity","共查询到：" + list.size() + "条数据。");
                            now_list.addAll(list);
                        }
                        initView(list);
                    }
                });
            }
        });

        loadTimes = 0;
    }

    private void initView(List<AVObject> list){

        efab = findViewById(R.id.efab_task_add);
        mRecyclerView = findViewById(R.id.recycler_view_task);

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
        adapter = new TaskRecyclerViewAdapter(list, mContext);
        mRecyclerView.setAdapter(adapter);
        efab.setOnClickListener(view -> {
            startActivity(new Intent(TaskActivity.this, TaskAddActivity.class));
//            //底部Dialog
//            mBottomSheetDialog = new BottomSheetDialog(this);
//            View dialogView = getLayoutInflater().inflate(R.layout.dialog_bottom_task, null);
//            Button btn_ok = dialogView.findViewById(R.id.btn_dialog_bottom_task_ok);
//            Button btn_cancel = dialogView.findViewById(R.id.btn_dialog_bottom_task_cancel);
//            edit_name = dialogView.findViewById(R.id.dialog_bottom_task_name);
//            edit_location = dialogView.findViewById(R.id.dialog_bottom_task_location);
//            seekBar_importance = dialogView.findViewById(R.id.dialog_bottom_task_importance);
//            TV_description = dialogView.findViewById(R.id.dialog_bottom_task_description);
//            TV_start_time = dialogView.findViewById(R.id.dialog_bottom_task_start_time);
//            TV_start_date = dialogView.findViewById(R.id.dialog_bottom_task_start_time_date);
//            TV_end_time = dialogView.findViewById(R.id.dialog_bottom_task_end_time);
//            TV_end_date = dialogView.findViewById(R.id.dialog_bottom_task_end_time_date);
////            MaterialButton clean_start = dialogView.findViewById(R.id.dialog_bottom_task_start_time_clean);
////            MaterialButton clean_end = dialogView.findViewById(R.id.dialog_bottom_task_end_time_clean);
//            spinner_remind = dialogView.findViewById(R.id.dialog_bottom_task_remind);
////            spinner_cycle = dialogView.findViewById(R.id.dialog_bottom_task_cycle);
//            ib_marker = dialogView.findViewById(R.id.dialog_bottom_task_location_marker);
//
//            btn_ok.setOnClickListener(this);
//            btn_cancel.setOnClickListener(this);
//            TV_start_time.setOnClickListener(this);
//            TV_start_date.setOnClickListener(this);
//            TV_end_time.setOnClickListener(this);
//            TV_end_date.setOnClickListener(this);
////            clean_start.setOnClickListener(this);
////            clean_end.setOnClickListener(this);
//            ib_marker.setOnClickListener(this);
//
//            TV_start_date.setText(task_start_time.get(Calendar.YEAR)+"年"+(task_start_time.get(Calendar.MONTH)+1)+"月"+task_start_time.get(Calendar.DATE)+"日");
//            TV_start_time.setText(task_start_time.get(Calendar.HOUR_OF_DAY)+" 时 "+task_start_time.get(Calendar.MINUTE) +"分");
//            TV_end_date.setText(task_end_time.get(Calendar.YEAR)+"年"+(task_end_time.get(Calendar.MONTH)+1)+"月"+task_end_time.get(Calendar.DATE)+"日");
//            TV_end_time.setText(task_end_time.get(Calendar.HOUR_OF_DAY)+" 时 "+task_end_time.get(Calendar.MINUTE) +"分");
//
//            mBottomSheetDialog.setContentView(dialogView);
//            mBottomSheetDialog.show();
        });

//        efab.setOnClickListener(view -> {
//            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
//
//            //格式化当前时间
//            String now_time_string = (new SimpleDateFormat("HH:mm")).format(Calendar.getInstance().getTime()); //获取当前时间并格式化
//
//            //底部Dialog
//            BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this);
//            View dialogView = getLayoutInflater().inflate(R.layout.dialog_bottom_energy_evaluation, null);
//            Button btn_ok = dialogView.findViewById(R.id.btn_dialog_bottom_energy_evaluation_determination_ok);
//            Button btn_cancel = dialogView.findViewById(R.id.btn_dialog_bottom_energy_evaluation_determination_cancel);
//            IndicatorSeekBar seekBar_thinking = dialogView.findViewById(R.id.dialog_bottom_energy_evaluation_thinking);//seekbar控件
//            IndicatorSeekBar seekBar_determination = dialogView.findViewById(R.id.dialog_bottom_energy_evaluation_determination);
//            TextView TV_time = dialogView.findViewById(R.id.dialog_bottom_energy_evaluation_time);
//
//            TV_time.setText(now_time_string);
//            TV_time.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    TimePickerDialog livetime_timePickerDialog = new TimePickerDialog(mContext,(view1, hour, minute) -> {
//                        evaluation_time.set(Calendar.HOUR_OF_DAY,hour);
//                        evaluation_time.set(Calendar.MINUTE,minute);
//                        Log.i("TrackActivity",evaluation_time.get(Calendar.HOUR_OF_DAY) + ":" + evaluation_time.get(Calendar.MINUTE));
//                        TV_time.setText(hour+" 时 "+minute +"分");
//                    }, evaluation_time.get(Calendar.HOUR_OF_DAY), evaluation_time.get(Calendar.MINUTE),true);
//                    livetime_timePickerDialog.show();
//                }
//            });
//            mBottomSheetDialog.setContentView(dialogView);
//            btn_ok.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int score_thinking = seekBar_thinking.getProgress();
//                    int score_determination = seekBar_determination.getProgress();
//                    //存到云
//                    AVObject energyEvaluation = new AVObject("EnergyEvaluation");
//                    energyEvaluation.put("UserId",AVUser.getCurrentUser().getObjectId());
//                    energyEvaluation.put("time",evaluation_time.getTime());
//                    energyEvaluation.put("thinking",score_thinking);
//                    energyEvaluation.put("determination",score_determination);
//                    energyEvaluation.saveInBackground(new SaveCallback() {
//                        @Override
//                        public void done(AVException e) {
//                            if (e == null) {
//                                //成功
//                                Toast.makeText(App.getContext(), "保存成功！", Toast.LENGTH_SHORT).show();
//                                loadMoreData();
////                                adapter.addItem(linearLayoutManager.findFirstVisibleItemPosition() + 1, insertData);
//                            } else {
//                                // 失败的原因可能有多种，常见的是用户名已经存在。
////                        showProgress(false);
//                                Toast.makeText(App.getContext(), "保存失败，原因：" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//
//                    mBottomSheetDialog.dismiss();
//                }
//            });
//
//            btn_cancel.setOnClickListener(v -> mBottomSheetDialog.dismiss());
//            mBottomSheetDialog.show();
//
//        });

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(adapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        //下拉加载更多
        swipeRefreshLayout = findViewById(R.id.swipe_task_recycler_view);
        swipeRefreshLayout.setColorSchemeResources(R.color.google_blue, R.color.google_green, R.color.google_red, R.color.google_yellow);
        swipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
            if (color > 4) {
                color = 0;
            }
            refreshData(task_type);
//            swipeRefreshLayout.setRefreshing(false);//加载成功后再消失
        }, 20));

        mRecyclerView.addOnScrollListener(scrollListener);
    }

    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (dy > 0) {
                efab.shrink();
            } else {
                efab.extend();
            }

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (!loading && linearLayoutManager.getItemCount() == (linearLayoutManager.findLastVisibleItemPosition() + 1)) {
                refreshData(task_type);
                loading = true;
            }
        }
    };

    private void refreshData(String type){

        int urgent_day = App.getApplication().getSharedPreferences("setting",MODE_PRIVATE).getInt("pref_seek_personal_urgent_day",10);
        Calendar forward7day_calendar = Calendar.getInstance();
        forward7day_calendar.add(Calendar.DATE,urgent_day);
        Date forward7day = forward7day_calendar.getTime();

        now_list.clear();//清除数据
        now_list = new ArrayList<>();
        AVQuery<AVObject> query = new AVQuery<>("Task");
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);// 启动查询缓存
        query.setMaxCacheAge(24 * 3600 * 1000); //设置为一天，单位毫秒
        query.whereEqualTo("UserId", AVUser.getCurrentUser().getObjectId());

        // 已弃用，通过筛选条件分类任务
//        switch(type){
//            case "today_planning":
//            case "today":
//                DateFormat dateFormat = new DateFormat();
//                Calendar today = dateFormat.FilterHourAndMinuteAndSecond(Calendar.getInstance());
//                Calendar tomorrow = dateFormat.FilterHourAndMinuteAndSecond(Calendar.getInstance());
//                tomorrow.add(Calendar.DAY_OF_MONTH,1);
//                query.whereGreaterThanOrEqualTo("start_time",today.getTime()); // 开始时间大于今天0时
//                query.whereLessThanOrEqualTo("start_time",tomorrow.getTime()); // 开始时间小于明天0时
//                query.whereEqualTo("done",false); //未完成事件
//                break;
//            case "all":
//                break;
//            case "late":
//                break;
//            case "undo":
//                query.whereEqualTo("done",false); //未完成事件
//                break;
//            case "do":
//                query.whereEqualTo("done",true); //已完成事件
//                break;
//            case "urgent_importance":
//                // 重要且紧急事件
//                query.whereLessThanOrEqualTo("end_time",forward7day); // 截止时间小于等于紧急
//                query.whereEqualTo("done",false); //未完成事件
//
//                query.whereGreaterThan("task_importance",5); // 重要程度大于5
//                break;
//            case "noturgent_importance":
//                // 重要但不紧急紧急事件
//                query.whereGreaterThan("end_time",forward7day); // 截止时间大于紧急
//                query.whereEqualTo("done",false); //未完成事件
//
//                query.whereGreaterThan("task_importance",5); // 重要程度大于5
//                break;
//            case "urgent_unimportance":
//                // 不重要但紧急事件
//                query.whereLessThanOrEqualTo("end_time",forward7day); // 截止时间小于等于紧急
//                query.whereEqualTo("done",false); //未完成事件
//
//                query.whereLessThanOrEqualTo("task_importance",5); // 重要程度小于等于5
//                break;
//            case "noturgent_unimportance":
//                // 不重要也不紧急事件
//                query.whereGreaterThan("end_time",forward7day); // 截止时间大于等于紧急
//                query.whereEqualTo("done",false); //未完成事件
//
//                query.whereLessThanOrEqualTo("task_importance",5); // 重要程度小于等于5
//                break;
//            default:
//                // 默认显示全部事件 仅按照是否完成排序
//                query.addAscendingOrder("done"); //按是否完成，升序,先false，后true
//                break;
//        }

        query.limit(MaxQuery);
        query.addAscendingOrder("done"); //按是否完成，升序,先false，后true
        query.addDescendingOrder("task_importance"); // 按照重要程序降序
        query.addAscendingOrder("start_time"); //按开始时间升序
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int count, AVException e) {
                if(count>=MaxQuery){
                    Toast.makeText(App.getContext(), "您的任务总数超过系统限制，仅显示前1000条，如需查询所有数据请联系软件作者！", Toast.LENGTH_SHORT).show();
                }
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        Log.i("LiveLIneActivity","共查询到：" + list.size() + "条数据。");
                        now_list.addAll(list);
                        filterDataAndLoad(list,type);
                        swipeRefreshLayout.setRefreshing(false);//加载成功后再消失

                    }
                });
            }
        });
    }

    private void filterDataAndLoad(List<AVObject> list, String type){
        // 获取今日开始结束时刻
        List<AVObject> filter = new ArrayList<>();
        DateFormat dateFormat = new DateFormat();
        Calendar today = dateFormat.FilterHourAndMinuteAndSecond(Calendar.getInstance());
        Calendar tomorrow = dateFormat.FilterHourAndMinuteAndSecond(Calendar.getInstance());
        tomorrow.add(Calendar.DAY_OF_MONTH,1);

        // 获取紧急日程判定依据及数据
        int urgent_day = App.getApplication().getSharedPreferences("setting",MODE_PRIVATE).getInt("pref_seek_personal_urgent_day",10);
        Calendar forward7day_calendar = Calendar.getInstance();
        forward7day_calendar.add(Calendar.DATE,urgent_day);
        Date forward7day = forward7day_calendar.getTime();
        for(AVObject obj: list){
            switch(type){
                case "today_planning":
                case "today":
                    // 未完成任务
                    if(!obj.getBoolean("done")){
                        // 开始时刻位于今日
                        if(obj.getDate("start_time").getTime()>today.getTime().getTime() && obj.getDate("start_time").getTime()<tomorrow.getTime().getTime()){
                            filter.add(obj);
                        }else if(obj.getDate("end_time").getTime()>today.getTime().getTime() && obj.getDate("end_time").getTime()<tomorrow.getTime().getTime()){
                            // 结束时刻位于今日
                            filter.add(obj);
                        }else if(obj.getDate("start_time").getTime()<today.getTime().getTime() && obj.getDate("end_time").getTime()>tomorrow.getTime().getTime()){
                            // 开始结束越过今天
                            filter.add(obj);
                        }
                    }
                    break;
                case "late":
                    // 已延误任务
                    if(!obj.getBoolean("done")){
                        // 结束时间小于今日开始
                        if(obj.getDate("end_time").getTime()<today.getTime().getTime()){
                            filter.add(obj);
                        }
                    }
                    break;
                case "undo":
                    //未完成事件
                    if(!obj.getBoolean("done")){
                        filter.add(obj);
                    }
                    break;
                case "do":
                    //已完成事件
                    if(obj.getBoolean("done")){
                        filter.add(obj);
                    }
                    break;
                case "urgent_importance":
                    // 重要且紧急事件
                    if(!obj.getBoolean("done")){
                        // 重要程度大于5
                        if(obj.getInt("task_importance")>5){
                            // 截止时间小于等于紧急
                            if(obj.getDate("end_time").getTime()<=forward7day.getTime()){
                                filter.add(obj);
                            }
                        }
                    }
                    break;
                case "noturgent_importance":
                    // 重要但不紧急紧急事件
                    if(!obj.getBoolean("done")){
                        // 重要程度大于5
                        if(obj.getInt("task_importance")>5){
                            // 截止时间大于紧急
                            if(obj.getDate("end_time").getTime()>forward7day.getTime()){
                                filter.add(obj);
                            }
                        }
                    }
                    break;
                case "urgent_unimportance":
                    // 不重要但紧急事件
                    if(!obj.getBoolean("done")){
                        // 重要程度小于等于5
                        if(obj.getInt("task_importance")<=5){
                            // 截止时间小于等于紧急
                            if(obj.getDate("end_time").getTime()<=forward7day.getTime()){
                                filter.add(obj);
                            }
                        }
                    }
                    break;
                case "noturgent_unimportance":
                    // 不重要也不紧急事件
                    if(!obj.getBoolean("done")){
                        // 重要程度小于等于5
                        if(obj.getInt("task_importance")<=5){
                            // 截止时间大于紧急
                            if(obj.getDate("end_time").getTime()>forward7day.getTime()){
                                filter.add(obj);
                            }
                        }
                    }
                    break;
                default:
                    // 默认显示全部事件 包括all
                    filter.add(obj);
                    break;
            }
        }
        adapter=new TaskRecyclerViewAdapter(filter,mContext);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
//            case R.id.btn_dialog_bottom_task_ok:
//                if(edit_name.length()==0){
//                    Toast.makeText(App.getContext(), "任务名称不能为空！", Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    if(task_start_time.getTime().getTime()>task_end_time.getTime().getTime()){
//                        Toast.makeText(App.getContext(), "结束时间不能小于开始时间，请修改，您的任务未保存。", Toast.LENGTH_SHORT).show();
//                    }else{
//                        mBottomSheetDialog.dismiss();
//
//                        AVObject new_task = new AVObject("Task");
//                        new_task.put("UserId", AVUser.getCurrentUser().getObjectId());// 设置用户ID
//                        new_task.put("task_name",edit_name.getText());
//                        new_task.put("task_importance",seekBar_importance.getProgress());
//                        if(task_start_time!=null)
//                            new_task.put("start_time",task_start_time.getTime());
//                        if(task_end_time!=null)
//                            new_task.put("end_time",task_end_time.getTime());
//                        if(TV_description.length()!=0)
//                            new_task.put("task_description",TV_description.getText());
//                        if(edit_location.length()!=0)
//                            new_task.put("task_location",edit_location.getText());
//                        new_task.put("task_remind",spinner_remind.getSelectedItemId());
////                        new_task.put("task_cycle",spinner_cycle.getSelectedItemId());
//                        new_task.saveInBackground(new SaveCallback() {
//                            @Override
//                            public void done(AVException e) {
//                                if (e == null) {
//                                    //成功
//                                    Toast.makeText(App.getContext(), "保存成功！", Toast.LENGTH_SHORT).show();
//
//                                    CalendarReminderUtils calendarReminderUtils = new CalendarReminderUtils();
//                                    switch ((int)spinner_remind.getSelectedItemId()){
//                                        case 0:
//                                            calendarReminderUtils.addCalendarEvent(mContext,edit_name.getText().toString(),TV_description.getText().toString(),edit_location.getText().toString(),task_start_time.getTime().getTime(),task_end_time.getTime().getTime());
//                                            break;
//                                        case 1:
//                                            calendarReminderUtils.addCalendarEvent(mContext,edit_name.getText().toString(),TV_description.getText().toString(),edit_location.getText().toString(),task_start_time.getTime().getTime(),task_end_time.getTime().getTime(),0);
//                                            break;
//                                        case 2:
//                                            calendarReminderUtils.addCalendarEvent(mContext,edit_name.getText().toString(),TV_description.getText().toString(),edit_location.getText().toString(),task_start_time.getTime().getTime(),task_end_time.getTime().getTime(),15);
//                                            break;
//                                        case 3:
//                                            calendarReminderUtils.addCalendarEvent(mContext,edit_name.getText().toString(),TV_description.getText().toString(),edit_location.getText().toString(),task_start_time.getTime().getTime(),task_end_time.getTime().getTime(),60);
//                                            break;
//                                        case 4:
//                                            calendarReminderUtils.addCalendarEvent(mContext,edit_name.getText().toString(),TV_description.getText().toString(),edit_location.getText().toString(),task_start_time.getTime().getTime(),task_end_time.getTime().getTime(), 24*60);
//                                            break;
//                                        default:
//                                            break;
//
//                                    }
//
//                                    refreshData(task_type);
////                                adapter.addItem(linearLayoutManager.findFirstVisibleItemPosition() + 1, insertData);
//                                } else {
//                                    // 失败的原因可能有多种，常见的是用户名已经存在。
////                        showProgress(false);
//                                    Toast.makeText(App.getContext(), "保存失败，原因：" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//                    }
//                }
//                break;
//            case R.id.btn_dialog_bottom_task_cancel:
//                mBottomSheetDialog.dismiss();
//                break;
////            case R.id.dialog_bottom_task_start_time_clean:
////                TV_start_date.setText("null");
////                TV_start_time.setText("null");
////                task_start_time = null;
////                break;
////            case R.id.dialog_bottom_task_end_time_clean:
////                TV_end_date.setText("null");
////                TV_end_time.setText("null");
////                task_end_time = null;
////                break;
//            case R.id.dialog_bottom_task_start_time_date:
//                if(task_start_time!=null){
//                    DatePickerDialog start_datePickerDialog = new DatePickerDialog(this, (view1, year, monthOfYear, dayOfMonth) -> {
//                        task_start_time.set(Calendar.YEAR,year);
//                        task_start_time.set(Calendar.MONTH,monthOfYear);
//                        task_start_time.set(Calendar.DATE,dayOfMonth);
//
//                        task_end_time.setTime(task_start_time.getTime());
//                        task_end_time.add(Calendar.DAY_OF_MONTH,1);
//                        TV_end_date.setText(task_end_time.get(Calendar.YEAR)+"年"+(task_end_time.get(Calendar.MONTH)+1)+"月"+task_end_time.get(Calendar.DAY_OF_MONTH)+"日");
//
//                        TV_start_date.setText(year+"年"+(monthOfYear+1)+"月"+dayOfMonth+"日");
//
//                    }, task_start_time.get(Calendar.YEAR), task_start_time.get(Calendar.MONTH), task_start_time.get(Calendar.DAY_OF_MONTH));
//                    start_datePickerDialog.show();
//                }else{
//                    Calendar now = Calendar.getInstance();
//                    DatePickerDialog start_datePickerDialog = new DatePickerDialog(this, (view1, year, monthOfYear, dayOfMonth) -> {
//                        task_start_time.set(Calendar.YEAR,year);
//                        task_start_time.set(Calendar.MONTH,monthOfYear);
//                        task_start_time.set(Calendar.DATE,dayOfMonth);
//
//                        task_end_time.setTime(task_start_time.getTime());
//                        task_end_time.add(Calendar.DAY_OF_MONTH,1);
//                        TV_end_date.setText(task_end_time.get(Calendar.YEAR)+"年"+(task_end_time.get(Calendar.MONTH)+1)+"月"+task_end_time.get(Calendar.DAY_OF_MONTH)+"日");
//
//                        TV_start_date.setText(year+"年"+(monthOfYear+1)+"月"+dayOfMonth+"日");
//                    }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
//                    start_datePickerDialog.show();
//                }
//                break;
//            case R.id.dialog_bottom_task_start_time:
//                if(task_start_time!=null){
//                    TimePickerDialog start_timePickerDialog = new TimePickerDialog(this,(view1, hour, minute) -> {
//                        task_start_time.set(Calendar.HOUR_OF_DAY,hour);
//                        task_start_time.set(Calendar.MINUTE,minute);
//                        TV_start_time.setText(hour+" 时 "+minute +"分");
//                    }, task_start_time.get(Calendar.HOUR_OF_DAY), task_start_time.get(Calendar.MINUTE),true);
//                    start_timePickerDialog.show();
//                }else{
//                    Calendar now = Calendar.getInstance();
//                    TimePickerDialog start_timePickerDialog = new TimePickerDialog(this,(view1, hour, minute) -> {
//                        task_start_time.set(Calendar.HOUR_OF_DAY,hour);
//                        task_start_time.set(Calendar.MINUTE,minute);
//                        TV_start_time.setText(hour+" 时 "+minute +"分");
//                    }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE),true);
//                    start_timePickerDialog.show();
//                }
//                break;
//            case R.id.dialog_bottom_task_end_time_date:
//                if(task_end_time!=null){
//
//                    DatePickerDialog start_datePickerDialog = new DatePickerDialog(this, (view1, year, monthOfYear, dayOfMonth) -> {
//                        task_end_time.set(Calendar.YEAR,year);
//                        task_end_time.set(Calendar.MONTH,monthOfYear);
//                        task_end_time.set(Calendar.DATE,dayOfMonth);
//                        TV_end_date.setText(year+"年"+(monthOfYear+1)+"月"+dayOfMonth+"日");
////                        if(year<task_start_time.get(Calendar.YEAR) || monthOfYear<task_start_time.get(Calendar.MONTH) || dayOfMonth<task_start_time.get(Calendar.DAY_OF_MONTH)){
////                            Toast.makeText(App.getContext(), "结束时间不能小于开始时间！您的修改不会保存。", Toast.LENGTH_SHORT).show();
////                        }else{
////                            task_end_time.set(Calendar.YEAR,year);
////                            task_end_time.set(Calendar.MONTH,monthOfYear);
////                            task_end_time.set(Calendar.DATE,dayOfMonth);
////                            TV_end_date.setText(year+"年"+(monthOfYear+1)+"月"+dayOfMonth+"日");
////                        }
//                    }, task_end_time.get(Calendar.YEAR), task_end_time.get(Calendar.MONTH), task_end_time.get(Calendar.DAY_OF_MONTH));
//                    start_datePickerDialog.show();
//                }else{
//                    Calendar now = Calendar.getInstance();
//                    DatePickerDialog start_datePickerDialog = new DatePickerDialog(this, (view1, year, monthOfYear, dayOfMonth) -> {
//                        task_end_time.set(Calendar.YEAR,year);
//                        task_end_time.set(Calendar.MONTH,monthOfYear);
//                        task_end_time.set(Calendar.DATE,dayOfMonth);
//                        TV_end_date.setText(year+"年"+(monthOfYear+1)+"月"+dayOfMonth+"日");
//                    }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
//                    start_datePickerDialog.show();
//                }
//                break;
//            case R.id.dialog_bottom_task_end_time:
//                if(task_end_time!=null){
//                    TimePickerDialog start_timePickerDialog = new TimePickerDialog(this,(view1, hour, minute) -> {
//                        task_end_time.set(Calendar.HOUR_OF_DAY,hour);
//                        task_end_time.set(Calendar.MINUTE,minute);
//                        TV_end_time.setText(hour+" 时 "+minute +"分");
//                    }, task_end_time.get(Calendar.HOUR_OF_DAY), task_end_time.get(Calendar.MINUTE),true);
//                    start_timePickerDialog.show();
//                }else{
//                    Calendar now = Calendar.getInstance();
//                    TimePickerDialog start_timePickerDialog = new TimePickerDialog(this,(view1, hour, minute) -> {
//                        task_end_time.set(Calendar.HOUR_OF_DAY,hour);
//                        task_end_time.set(Calendar.MINUTE,minute);
//                        TV_end_time.setText(hour+" 时 "+minute +"分");
//                    }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE),true);
//                    start_timePickerDialog.show();
//                }
//                break;
//            case R.id.dialog_bottom_task_location_marker:
//                Log.i("TaskActivity","选择位置");
//                View dialogView = getLayoutInflater().inflate(R.layout.dialog_bottom_task, null);
//                mBottomSheetDialog.setContentView(dialogView);
//                mBottomSheetDialog.show();
//                break;
            default:
                break;
        }
    }
}
