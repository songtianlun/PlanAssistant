package com.hgo.planassistant.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.hgo.planassistant.App;
import com.hgo.planassistant.R;
import com.hgo.planassistant.adapter.LiveLineRecyclerViewAdapter;
import com.hgo.planassistant.adapter.PlanCounterRecyclerViewAdapter;
import com.hgo.planassistant.util.AppUtils;
import com.hgo.planassistant.view.ItemTouchHelperCallback;
import com.umeng.analytics.MobclickAgent;
import com.warkiz.widget.IndicatorSeekBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.leancloud.AVObject;
import cn.leancloud.AVQuery;
import cn.leancloud.AVUser;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class PlanCounterActivity extends BaseActivity {

    private SwipeRefreshLayout pc_swipeRefreshLayout;
    private RecyclerView pc_RecyclerView;
    private ExtendedFloatingActionButton pc_fab;

    private PlanCounterRecyclerViewAdapter pc_adapter;
    private int color = 0;
    private List<AVObject> pc_data;
    private AVObject insertData;
    private boolean loading;
    private int loadTimes;
    private Context PlanCounteractivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_counter);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));

        Toolbar toolbar = findViewById(R.id.toolbar_plan_counter_view);
        setToolbar(toolbar);

        PlanCounteractivity = this;

        initData();


    }

    @Override
    protected void onResume() {
        super.onResume();
//        MobclickAgent.onResume(this); // umeng+ 统计 //AUTO页面采集模式下不调用
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPause(this);  // umeng+ 统计 //AUTO页面采集模式下不调用
    }

    private void initData() {
        pc_data = new ArrayList<>();
        AVQuery<AVObject> query = new AVQuery<>("PlanCounter");
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);// 启动查询缓存
        query.setMaxCacheAge(24 * 3600 * 1000); //设置为一天，单位毫秒
        query.whereEqualTo("UserId", AVUser.getCurrentUser().getObjectId());
        query.limit(1000);
        query.addAscendingOrder("done"); //按是否完成，升序,先false，后true
        query.addDescendingOrder("updatedAt");// 按时间，降序排列
        query.findInBackground().subscribe(new Observer<List<AVObject>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<AVObject> avObjects) {
                if(avObjects!=null){
                    Log.i("LiveLIneActivity","共查询到：" + avObjects.size() + "条数据。");
                    pc_data = avObjects;
                }
                initView();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
//        data = new ArrayList<>();
//        for (int i = 1; i <= 20; i++) {
//            data.add(i + "");
//        }
        loadTimes = 0;
    }

    @SuppressLint("WrongConstant")
    private void initView() {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        pc_fab = findViewById(R.id.efab_recycler_pc_view);
        pc_RecyclerView = findViewById(R.id.recycler_pc_view_recycler_view);

        if (AppUtils.getScreenWidthDp(this) >= 1200) {
            final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
            pc_RecyclerView.setLayoutManager(gridLayoutManager);
        } else if (AppUtils.getScreenWidthDp(this) >= 800) {
            final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
            pc_RecyclerView.setLayoutManager(gridLayoutManager);
        } else {
            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            pc_RecyclerView.setLayoutManager(linearLayoutManager);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        pc_RecyclerView.setLayoutManager(layoutManager);

        //适配器，为RecyclerView提供数据
        pc_adapter = new PlanCounterRecyclerViewAdapter(pc_data, PlanCounteractivity);
        pc_RecyclerView.setAdapter(pc_adapter);

        pc_fab.setOnClickListener(view -> {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) pc_RecyclerView.getLayoutManager();

            Log.i("MainActivity","按钮点击");
            //底部Dialog
            BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_bottom_plan_counter, null);
            Button finish = dialogView.findViewById(R.id.btn_dialog_bottom_sheet_finish);
            Button ok = dialogView.findViewById(R.id.btn_dialog_bottom_sheet_ok);

            EditText Tv_dialog_title = dialogView.findViewById(R.id.dialog_button_plan_counter_title);
            EditText Tv_dialog_description = dialogView.findViewById(R.id.dialog_button_plan_counter_description);
            IndicatorSeekBar seekBar = dialogView.findViewById(R.id.dialog_plan_counter_finish);//seekbar控件

            Tv_dialog_description.setText("计划简述");

            mBottomSheetDialog.setContentView(dialogView);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int finish = seekBar.getProgress();
                    String description = Tv_dialog_description.getText().toString();
                    String title = Tv_dialog_title.getText().toString();
                    //存到云
                    AVObject PlanCounter = new AVObject("PlanCounter");
                    PlanCounter.put("UserId", AVUser.getCurrentUser().getObjectId());
                    PlanCounter.put("AimsCounter",finish);
                    PlanCounter.put("description",description);
                    PlanCounter.put("title",title);
                    PlanCounter.put("NowCounter",0);
                    PlanCounter.saveInBackground().subscribe(new Observer<AVObject>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(AVObject avObject) {
                            Snackbar.make(view, getString(R.string.succefully), Snackbar.LENGTH_LONG)
                                    .setAction(getString(R.string.main_snack_bar_action), view -> {
                                    }).show();
                            insertData = PlanCounter;
                            pc_adapter.addItem(insertData);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(App.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });

                    mBottomSheetDialog.dismiss();
                }
            });

            mBottomSheetDialog.show();

        });

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(pc_adapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(pc_RecyclerView);

        //下拉加载更多
        pc_swipeRefreshLayout = findViewById(R.id.swipe_refresh_pc_layout_recycler_view);
        pc_swipeRefreshLayout.setColorSchemeResources(R.color.google_blue, R.color.google_green, R.color.google_red, R.color.google_yellow);
        pc_swipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
            if (color > 4) {
                color = 0;
            }
            loadMoreData();
//            swipeRefreshLayout.setRefreshing(false);//加载成功后再消失
        }, 20));

        pc_RecyclerView.addOnScrollListener(scrollListener);
    }

    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (dy > 0) {
                pc_fab.shrink();
            } else {
                pc_fab.extend();
            }

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (!loading && linearLayoutManager.getItemCount() == (linearLayoutManager.findLastVisibleItemPosition() + 1)) {
                loadMoreData();
                loading = true;
            }
        }
    };

    private void loadMoreData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                pc_data.clear();//清除数据
                pc_data = new ArrayList<>();
                AVQuery<AVObject> query = new AVQuery<>("PlanCounter");
                query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);// 启动查询缓存
                query.setMaxCacheAge(24 * 3600 * 1000); //设置为一天，单位毫秒
                query.addAscendingOrder("done"); //按是否完成，升序,先false，后true
                query.addDescendingOrder("updatedAt");// 按时间，降序排列
                query.whereEqualTo("UserId", AVUser.getCurrentUser().getObjectId());
                query.limit(1000);
                query.findInBackground().subscribe(new Observer<List<AVObject>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<AVObject> avObjects) {
                        Log.i("LiveLIneActivity","共查询到：" + avObjects.size() + "条数据。");
                        pc_data = avObjects;
                        pc_adapter=new PlanCounterRecyclerViewAdapter(pc_data,PlanCounteractivity);
                        pc_RecyclerView.setAdapter(pc_adapter);
                        pc_swipeRefreshLayout.setRefreshing(false);//加载成功后再消失
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });


            }
        }, 1500);
    }
}
