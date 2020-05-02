package com.hgo.planassistant.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.hgo.planassistant.App;
import com.hgo.planassistant.R;
import com.hgo.planassistant.adapter.EnergyEvaluationRecyclerViewAdapter;
import com.hgo.planassistant.adapter.LiveLineRecyclerViewAdapter;
import com.hgo.planassistant.util.AppUtils;
import com.hgo.planassistant.view.ItemTouchHelperCallback;
import com.warkiz.widget.IndicatorSeekBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EnergyEvaluationActivity extends BaseActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private ExtendedFloatingActionButton efab;
    private EnergyEvaluationRecyclerViewAdapter adapter;
    private int color = 0;
    private List<AVObject> now_list;
    private AVObject insertData;
    private boolean loading;
    private int loadTimes;
    private Context mContext;
    private Calendar evaluation_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_energy_evaluation);

        Toolbar toolbar = findViewById(R.id.toolbar_energy_evaluation);
        setToolbar(toolbar);
        mContext = this;
        evaluation_time = Calendar.getInstance();
        initDate();

    }

    private void initDate(){
        now_list = new ArrayList<>();
        AVQuery<AVObject> query = new AVQuery<>("EnergyEvaluation");
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);// 启动查询缓存
        query.setMaxCacheAge(24 * 3600 * 1000); //设置为一天，单位毫秒
        query.whereEqualTo("UserId", AVUser.getCurrentUser().getObjectId());
        query.limit(1000);
        query.orderByDescending("createdAt");// 按时间，降序排列
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if(list!=null){
                    Log.i("LiveLIneActivity","共查询到：" + list.size() + "条数据。");
                    now_list.addAll(list);
                }
                initView();
            }
        });
        loadTimes = 0;
    }

    private void initView(){

        efab = findViewById(R.id.efab_energy_evaluation_add);
        mRecyclerView = findViewById(R.id.recycler_view_energy_evaluation);

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
        adapter = new EnergyEvaluationRecyclerViewAdapter(now_list, mContext);
        mRecyclerView.setAdapter(adapter);

        efab.setOnClickListener(view -> {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();

            //格式化当前时间
            String now_time_string = (new SimpleDateFormat("HH:mm")).format(Calendar.getInstance().getTime()); //获取当前时间并格式化

            //底部Dialog
            BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_bottom_energy_evaluation, null);
            Button btn_ok = dialogView.findViewById(R.id.btn_dialog_bottom_energy_evaluation_determination_ok);
            Button btn_cancel = dialogView.findViewById(R.id.btn_dialog_bottom_energy_evaluation_determination_cancel);
            IndicatorSeekBar seekBar_thinking = dialogView.findViewById(R.id.dialog_bottom_energy_evaluation_thinking);//seekbar控件
            IndicatorSeekBar seekBar_determination = dialogView.findViewById(R.id.dialog_bottom_energy_evaluation_determination);
            TextView TV_time = dialogView.findViewById(R.id.dialog_bottom_energy_evaluation_time);

            TV_time.setText(now_time_string);
            TV_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TimePickerDialog livetime_timePickerDialog = new TimePickerDialog(mContext,(view1, hour, minute) -> {
                        evaluation_time.set(Calendar.HOUR_OF_DAY,hour);
                        evaluation_time.set(Calendar.MINUTE,minute);
                        Log.i("TrackActivity",evaluation_time.get(Calendar.HOUR_OF_DAY) + ":" + evaluation_time.get(Calendar.MINUTE));
                        TV_time.setText(hour+" 时 "+minute +"分");
                    }, evaluation_time.get(Calendar.HOUR_OF_DAY), evaluation_time.get(Calendar.MINUTE),true);
                    livetime_timePickerDialog.show();
                }
            });
            mBottomSheetDialog.setContentView(dialogView);
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int score_thinking = seekBar_thinking.getProgress();
                    int score_determination = seekBar_determination.getProgress();
                    //存到云
                    AVObject energyEvaluation = new AVObject("EnergyEvaluation");
                    energyEvaluation.put("UserId",AVUser.getCurrentUser().getObjectId());
                    energyEvaluation.put("time",evaluation_time.getTime());
                    energyEvaluation.put("thinking",score_thinking);
                    energyEvaluation.put("determination",score_determination);
                    energyEvaluation.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                //成功
                                Toast.makeText(App.getContext(), "保存成功！", Toast.LENGTH_SHORT).show();
                                loadMoreData();
//                                adapter.addItem(linearLayoutManager.findFirstVisibleItemPosition() + 1, insertData);
                            } else {
                                // 失败的原因可能有多种，常见的是用户名已经存在。
//                        showProgress(false);
                                Toast.makeText(App.getContext(), "保存失败，原因：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    mBottomSheetDialog.dismiss();
                }
            });

            btn_cancel.setOnClickListener(v -> mBottomSheetDialog.dismiss());
            mBottomSheetDialog.show();

        });

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(adapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        //下拉加载更多
        swipeRefreshLayout = findViewById(R.id.swipe_energy_ecaluation_recycler_view);
        swipeRefreshLayout.setColorSchemeResources(R.color.google_blue, R.color.google_green, R.color.google_red, R.color.google_yellow);
        swipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
            if (color > 4) {
                color = 0;
            }
            loadMoreData();
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
                loadMoreData();
                loading = true;
            }
        }
    };

    private void loadMoreData(){
        now_list.clear();//清除数据
        now_list = new ArrayList<>();
        AVQuery<AVObject> query = new AVQuery<>("EnergyEvaluation");
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);// 启动查询缓存
        query.setMaxCacheAge(24 * 3600 * 1000); //设置为一天，单位毫秒
        query.whereEqualTo("UserId", AVUser.getCurrentUser().getObjectId());
        query.limit(1000);
        query.orderByDescending("createdAt");// 按时间，降序排列
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                Log.i("LiveLIneActivity","共查询到：" + list.size() + "条数据。");
                now_list.addAll(list);
                adapter=new EnergyEvaluationRecyclerViewAdapter(now_list,mContext);
                mRecyclerView.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);//加载成功后再消失
            }
        });
    }
}
