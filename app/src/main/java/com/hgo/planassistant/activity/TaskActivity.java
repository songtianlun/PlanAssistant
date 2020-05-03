package com.hgo.planassistant.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.hgo.planassistant.App;
import com.hgo.planassistant.R;
import com.hgo.planassistant.adapter.TaskRecyclerViewAdapter;
import com.hgo.planassistant.util.AppUtils;
import com.hgo.planassistant.view.ItemTouchHelperCallback;
import com.warkiz.widget.IndicatorSeekBar;

import java.text.SimpleDateFormat;
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
    private Calendar task_start_time;
    private Calendar task_end_time;

    private int Pages = -1;
    private int sumPages = -1;
    private int MaxQuery = 100;

    private BottomSheetDialog mBottomSheetDialog;

    private TextView TV_start_time;
    private TextView TV_start_date;
    private TextView TV_end_time;
    private TextView TV_end_date;
    private TextInputEditText edit_name;
    private TextInputEditText edit_location;
    private IndicatorSeekBar seekBar_importance;
    private TextView TV_description;
    private AppCompatSpinner spinner_remind;
    private AppCompatSpinner spinner_cycle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        Toolbar toolbar = findViewById(R.id.toolbar_task);
        setToolbar(toolbar);

        mContext = this;
        task_start_time = Calendar.getInstance();
        task_end_time = Calendar.getInstance();
        task_end_time.add(Calendar.HOUR_OF_DAY,1);
        initDate();
    }

    private void initDate(){
        Pages = 0;// 页数从0开始
        now_list = new ArrayList<>();
        AVQuery<AVObject> query = new AVQuery<>("Task");
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);// 启动查询缓存
        query.setMaxCacheAge(24 * 3600 * 1000); //设置为一天，单位毫秒
        query.whereEqualTo("UserId", AVUser.getCurrentUser().getObjectId());
        query.limit(MaxQuery);
        query.orderByAscending("done"); //按是否完成，升序,先false，后true
        query.addDescendingOrder("start_time");
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int count, AVException e) {
                sumPages = count/100 + 1;
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
            //底部Dialog
            mBottomSheetDialog = new BottomSheetDialog(this);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_bottom_task, null);
            Button btn_ok = dialogView.findViewById(R.id.btn_dialog_bottom_task_ok);
            Button btn_cancel = dialogView.findViewById(R.id.btn_dialog_bottom_task_cancel);
            edit_name = dialogView.findViewById(R.id.dialog_bottom_task_name);
            edit_location = dialogView.findViewById(R.id.dialog_bottom_task_location);
            seekBar_importance = dialogView.findViewById(R.id.dialog_bottom_task_importance);
            TV_description = dialogView.findViewById(R.id.dialog_bottom_task_description);
            TV_start_time = dialogView.findViewById(R.id.dialog_bottom_task_start_time);
            TV_start_date = dialogView.findViewById(R.id.dialog_bottom_task_start_time_date);
            TV_end_time = dialogView.findViewById(R.id.dialog_bottom_task_end_time);
            TV_end_date = dialogView.findViewById(R.id.dialog_bottom_task_end_time_date);
            MaterialButton clean_start = dialogView.findViewById(R.id.dialog_bottom_task_start_time_clean);
            MaterialButton clean_end = dialogView.findViewById(R.id.dialog_bottom_task_end_time_clean);
            spinner_remind = dialogView.findViewById(R.id.dialog_bottom_task_remind);
            spinner_cycle = dialogView.findViewById(R.id.dialog_bottom_task_cycle);

            btn_ok.setOnClickListener(this);
            btn_cancel.setOnClickListener(this);
            TV_start_time.setOnClickListener(this);
            TV_start_date.setOnClickListener(this);
            TV_end_time.setOnClickListener(this);
            TV_end_date.setOnClickListener(this);
            clean_start.setOnClickListener(this);
            clean_end.setOnClickListener(this);

            TV_start_date.setText(task_start_time.get(Calendar.YEAR)+"年"+(task_start_time.get(Calendar.MONTH)+1)+"月"+task_start_time.get(Calendar.DATE)+"日");
            TV_start_time.setText(task_start_time.get(Calendar.HOUR_OF_DAY)+" 时 "+task_start_time.get(Calendar.MINUTE) +"分");
            TV_end_date.setText(task_end_time.get(Calendar.YEAR)+"年"+(task_end_time.get(Calendar.MONTH)+1)+"月"+task_end_time.get(Calendar.DATE)+"日");
            TV_end_time.setText(task_end_time.get(Calendar.HOUR_OF_DAY)+" 时 "+task_end_time.get(Calendar.MINUTE) +"分");

            mBottomSheetDialog.setContentView(dialogView);
            mBottomSheetDialog.show();
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
            LoadMoreData();
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
                refreshData();
                loading = true;
            }
        }
    };

    private void refreshData(){
        Pages = 0;// 页数从0开始
        now_list.clear();//清除数据
        now_list = new ArrayList<>();
        AVQuery<AVObject> query = new AVQuery<>("Task");
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);// 启动查询缓存
        query.setMaxCacheAge(24 * 3600 * 1000); //设置为一天，单位毫秒
        query.whereEqualTo("UserId", AVUser.getCurrentUser().getObjectId());
        query.limit(MaxQuery);
        query.orderByAscending("done"); //按是否完成，升序,先false，后true
        query.addDescendingOrder("start_time");
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int count, AVException e) {
                sumPages = count/100 + 1;
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        Log.i("LiveLIneActivity","共查询到：" + list.size() + "条数据。");
                        now_list.addAll(list);
                        adapter=new TaskRecyclerViewAdapter(list,mContext);
                        mRecyclerView.setAdapter(adapter);
                        swipeRefreshLayout.setRefreshing(false);//加载成功后再消失
                    }
                });
            }
        });
    }

    private void LoadMoreData(){
        if(Pages<(sumPages-1)){
            Pages++;
            AVQuery<AVObject> query = new AVQuery<>("Task");
            query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);// 启动查询缓存
            query.setMaxCacheAge(24 * 3600 * 1000); //设置为一天，单位毫秒
            query.whereEqualTo("UserId", AVUser.getCurrentUser().getObjectId());
            query.limit(MaxQuery);
            query.skip(Pages*100);
            query.orderByAscending("done"); //按是否完成，升序,先false，后true
            query.addDescendingOrder("start_time");
            query.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    Log.i("LiveLIneActivity","共查询到：" + list.size() + "条数据。");
                    now_list.addAll(list);
                    adapter=new TaskRecyclerViewAdapter(now_list,mContext);
                    mRecyclerView.setAdapter(adapter);
                    swipeRefreshLayout.setRefreshing(false);//加载成功后再消失
                }
            });
        }else{
            refreshData();
            Toast.makeText(App.getContext(), "已刷新！(没有更多日程！)", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_dialog_bottom_task_ok:
                if(edit_name.length()==0){
                    Toast.makeText(App.getContext(), "任务名称不能为空！", Toast.LENGTH_SHORT).show();
                }
                else{
                    mBottomSheetDialog.dismiss();
                    AVObject new_task = new AVObject("Task");
                    new_task.put("UserId", AVUser.getCurrentUser().getObjectId());// 设置用户ID
                    new_task.put("task_name",edit_name.getText());
                    new_task.put("task_importance",seekBar_importance.getProgress());
                    if(task_start_time!=null)
                        new_task.put("start_time",task_start_time.getTime());
                    if(task_end_time!=null)
                        new_task.put("end_time",task_end_time.getTime());
                    if(TV_description.length()!=0)
                        new_task.put("task_description",TV_description.getText());
                    if(edit_location.length()!=0)
                        new_task.put("task_location",edit_location.getText());
                    new_task.put("task_remind",spinner_remind.getSelectedItemId());
                    new_task.put("task_cycle",spinner_cycle.getSelectedItemId());
                    new_task.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                //成功
                                Toast.makeText(App.getContext(), "保存成功！", Toast.LENGTH_SHORT).show();
                                refreshData();
//                                adapter.addItem(linearLayoutManager.findFirstVisibleItemPosition() + 1, insertData);
                            } else {
                                // 失败的原因可能有多种，常见的是用户名已经存在。
//                        showProgress(false);
                                Toast.makeText(App.getContext(), "保存失败，原因：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                break;
            case R.id.btn_dialog_bottom_task_cancel:
                mBottomSheetDialog.dismiss();
                break;
            case R.id.dialog_bottom_task_start_time_clean:
                TV_start_date.setText("null");
                TV_start_time.setText("null");
                task_start_time = null;
                break;
            case R.id.dialog_bottom_task_end_time_clean:
                TV_end_date.setText("null");
                TV_end_time.setText("null");
                task_end_time = null;
                break;
            case R.id.dialog_bottom_task_start_time_date:
                if(task_start_time!=null){
                    DatePickerDialog start_datePickerDialog = new DatePickerDialog(this, (view1, year, monthOfYear, dayOfMonth) -> {
                        task_start_time.set(Calendar.YEAR,year);
                        task_start_time.set(Calendar.MONTH,monthOfYear);
                        task_start_time.set(Calendar.DATE,dayOfMonth);
                        TV_start_date.setText(year+"年"+(monthOfYear+1)+"月"+dayOfMonth+"日");
                    }, task_start_time.get(Calendar.YEAR), task_start_time.get(Calendar.MONTH), task_start_time.get(Calendar.DAY_OF_MONTH));
                    start_datePickerDialog.show();
                }else{
                    Calendar now = Calendar.getInstance();
                    DatePickerDialog start_datePickerDialog = new DatePickerDialog(this, (view1, year, monthOfYear, dayOfMonth) -> {
                        task_start_time.set(Calendar.YEAR,year);
                        task_start_time.set(Calendar.MONTH,monthOfYear);
                        task_start_time.set(Calendar.DATE,dayOfMonth);
                        TV_start_date.setText(year+"年"+(monthOfYear+1)+"月"+dayOfMonth+"日");
                    }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                    start_datePickerDialog.show();
                }
                break;
            case R.id.dialog_bottom_task_start_time:
                if(task_start_time!=null){
                    TimePickerDialog start_timePickerDialog = new TimePickerDialog(this,(view1, hour, minute) -> {
                        task_start_time.set(Calendar.HOUR_OF_DAY,hour);
                        task_start_time.set(Calendar.MINUTE,minute);
                        TV_start_time.setText(hour+" 时 "+minute +"分");
                    }, task_start_time.get(Calendar.HOUR_OF_DAY), task_start_time.get(Calendar.MINUTE),true);
                    start_timePickerDialog.show();
                }else{
                    Calendar now = Calendar.getInstance();
                    TimePickerDialog start_timePickerDialog = new TimePickerDialog(this,(view1, hour, minute) -> {
                        task_start_time.set(Calendar.HOUR_OF_DAY,hour);
                        task_start_time.set(Calendar.MINUTE,minute);
                        TV_start_time.setText(hour+" 时 "+minute +"分");
                    }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE),true);
                    start_timePickerDialog.show();
                }
                break;
            case R.id.dialog_bottom_task_end_time_date:
                if(task_end_time!=null){
                    DatePickerDialog start_datePickerDialog = new DatePickerDialog(this, (view1, year, monthOfYear, dayOfMonth) -> {
                        task_end_time.set(Calendar.YEAR,year);
                        task_end_time.set(Calendar.MONTH,monthOfYear);
                        task_end_time.set(Calendar.DATE,dayOfMonth);
                        TV_end_date.setText(year+"年"+(monthOfYear+1)+"月"+dayOfMonth+"日");
                    }, task_end_time.get(Calendar.YEAR), task_end_time.get(Calendar.MONTH), task_end_time.get(Calendar.DAY_OF_MONTH));
                    start_datePickerDialog.show();
                }else{
                    Calendar now = Calendar.getInstance();
                    DatePickerDialog start_datePickerDialog = new DatePickerDialog(this, (view1, year, monthOfYear, dayOfMonth) -> {
                        task_end_time.set(Calendar.YEAR,year);
                        task_end_time.set(Calendar.MONTH,monthOfYear);
                        task_end_time.set(Calendar.DATE,dayOfMonth);
                        TV_end_date.setText(year+"年"+(monthOfYear+1)+"月"+dayOfMonth+"日");
                    }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                    start_datePickerDialog.show();
                }
                break;
            case R.id.dialog_bottom_task_end_time:
                if(task_end_time!=null){
                    TimePickerDialog start_timePickerDialog = new TimePickerDialog(this,(view1, hour, minute) -> {
                        task_end_time.set(Calendar.HOUR_OF_DAY,hour);
                        task_end_time.set(Calendar.MINUTE,minute);
                        TV_end_time.setText(hour+" 时 "+minute +"分");
                    }, task_end_time.get(Calendar.HOUR_OF_DAY), task_end_time.get(Calendar.MINUTE),true);
                    start_timePickerDialog.show();
                }else{
                    Calendar now = Calendar.getInstance();
                    TimePickerDialog start_timePickerDialog = new TimePickerDialog(this,(view1, hour, minute) -> {
                        task_end_time.set(Calendar.HOUR_OF_DAY,hour);
                        task_end_time.set(Calendar.MINUTE,minute);
                        TV_end_time.setText(hour+" 时 "+minute +"分");
                    }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE),true);
                    start_timePickerDialog.show();
                }
                break;
            default:
                break;
        }
    }
}
