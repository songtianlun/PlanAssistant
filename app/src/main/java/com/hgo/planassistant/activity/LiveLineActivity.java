package com.hgo.planassistant.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.hgo.planassistant.App;
import com.hgo.planassistant.R;
import com.hgo.planassistant.adapter.LiveLineRecyclerViewAdapter;
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

public class LiveLineActivity extends BaseActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private ExtendedFloatingActionButton efab;
    private LiveLineRecyclerViewAdapter adapter;
    private int color = 0;
    private List<AVObject> live_data;
    private AVObject insertData;
    private boolean loading;
    private int loadTimes;
    private Context livelineactivity;

    private Calendar liveline_time;
    private Calendar now_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_line);

        Toolbar toolbar = findViewById(R.id.toolbar_liveline_view);
        setToolbar(toolbar);

        livelineactivity=this;

        liveline_time = Calendar.getInstance();
        now_time = Calendar.getInstance();

        initData();
//        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        MobclickAgent.onResume(this); // umeng+ 统计  //AUTO页面采集模式下不调用
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPause(this);  // umeng+ 统计 //AUTO页面采集模式下不调用
    }

    private void initData() {
        live_data = new ArrayList<>();
        AVQuery<AVObject> query = new AVQuery<>("liveline");
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);// 启动查询缓存
        query.setMaxCacheAge(24 * 3600 * 1000); //设置为一天，单位毫秒
        query.whereEqualTo("UserId", AVUser.getCurrentUser().getObjectId());
        query.limit(1000);
        query.orderByDescending("createdAt");// 按时间，降序排列
        query.findInBackground().subscribe(new Observer<List<AVObject>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<AVObject> avObjects) {
                if(avObjects!=null){
                    Log.i("LiveLIneActivity","共查询到：" + avObjects.size() + "条数据。");
                    live_data = avObjects;
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
        efab = findViewById(R.id.efab_recycler_view);
        mRecyclerView = findViewById(R.id.recycler_view_recycler_view);

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
        adapter = new LiveLineRecyclerViewAdapter(live_data, livelineactivity);
        mRecyclerView.setAdapter(adapter);
//        adapter.addHeader();
//        adapter.setItems(live_data);
//        adapter.addFooter();


        efab.setOnClickListener(view -> {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();

            //格式化当前时间
            String now_time_string = (new SimpleDateFormat("HH:mm")).format(liveline_time.getTime()); //获取当前时间并格式化

            BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_bottom_liveline_score, null);
            Button btn_dialog_bottom_sheet_ok = dialogView.findViewById(R.id.btn_dialog_bottom_sheet_ok);
            Button btn_dialog_bottom_sheet_cancel = dialogView.findViewById(R.id.btn_dialog_bottom_sheet_cancel);
            TextView TV_dialog_button_liveline_score_remark = dialogView.findViewById(R.id.dialog_button_liveline_score_remark);
            IndicatorSeekBar seekBar = dialogView.findViewById(R.id.dialog_button_liveline_score_score);//seekbar控件
            TextView TV_time = dialogView.findViewById(R.id.dialog_button_liveline_score_time);

            TV_time.setText(now_time_string);
            TV_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TimePickerDialog livetime_timePickerDialog = new TimePickerDialog(livelineactivity,(view1, hour, minute) -> {
                        liveline_time.set(Calendar.HOUR_OF_DAY,hour);
                        liveline_time.set(Calendar.MINUTE,minute);
                        Log.i("TrackActivity",liveline_time.get(Calendar.HOUR_OF_DAY) + ":" + liveline_time.get(Calendar.MINUTE));
                        TV_time.setText(hour+" 时 "+minute +"分");
                    }, liveline_time.get(Calendar.HOUR_OF_DAY), liveline_time.get(Calendar.MINUTE),true);
                    livetime_timePickerDialog.show();
                }
            });
            TV_dialog_button_liveline_score_remark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(livelineactivity,v);
                    popupMenu.getMenuInflater().inflate(R.menu.liveline_popup_menu_main, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            TV_dialog_button_liveline_score_remark.setText(item.getTitle());
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });
//            //监听seekbar变化部分
//            seekBar.setOnSeekChangeListener(new OnSeekChangeListener() {
//                @Override
//                public void onSeeking(SeekParams seekParams) {
//                    Log.i("LiveLIneActivity", String.valueOf(seekParams.progressFloat));
//                }
//
//                @Override
//                public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
//                }
//
//                @Override
//                public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
//                }
//            });
            mBottomSheetDialog.setContentView(dialogView);
            btn_dialog_bottom_sheet_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int score = seekBar.getProgress();
                    Date now_date = liveline_time.getTime();//获取精力曲线时间

                    //存到云
                    AVObject liveline = new AVObject("liveline");
                    liveline.put("UserId",AVUser.getCurrentUser().getObjectId());
                    liveline.put("livetime",now_date);
                    liveline.put("score",score);
                    liveline.put("remarks",TV_dialog_button_liveline_score_remark.getText());
                    liveline.saveInBackground().subscribe(new Observer<AVObject>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(AVObject avObject) {
                            Snackbar.make(view, getString(R.string.succefully), Snackbar.LENGTH_LONG)
                                    .setAction(getString(R.string.main_snack_bar_action), view -> {
                                    }).show();
                            insertData = liveline;
                            adapter.addItem(insertData);
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
                    Log.i("LiveLineActivity", String.valueOf(seekBar.getProgress()));
                }
            });

            btn_dialog_bottom_sheet_cancel.setOnClickListener(v -> mBottomSheetDialog.dismiss());
            mBottomSheetDialog.show();

        });

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(adapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        //下拉加载更多
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout_recycler_view);
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

    private void loadMoreData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                live_data.clear();//清除数据
                live_data = new ArrayList<>();
                AVQuery<AVObject> query = new AVQuery<>("liveline");
                query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);// 启动查询缓存
                query.setMaxCacheAge(24 * 3600 * 1000); //设置为一天，单位毫秒
                query.whereEqualTo("UserId", AVUser.getCurrentUser().getObjectId());
                query.limit(1000);
                query.orderByDescending("createdAt");// 按时间，降序排列
                query.findInBackground().subscribe(new Observer<List<AVObject>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<AVObject> avObjects) {
                        Log.i("LiveLIneActivity","共查询到：" + avObjects.size() + "条数据。");
                        live_data = avObjects;
                        adapter=new LiveLineRecyclerViewAdapter(live_data,livelineactivity);
                        mRecyclerView.setAdapter(adapter);
                        swipeRefreshLayout.setRefreshing(false);//加载成功后再消失
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
