package com.hgo.planassistant.activity;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.hgo.planassistant.App;
import com.hgo.planassistant.R;
import com.hgo.planassistant.adapter.BookkeepingRecyclerViewAdapter;
import com.hgo.planassistant.adapter.MapCheckRecyclerViewAdapter;
import com.hgo.planassistant.tools.MoneyValueFilter;
import com.hgo.planassistant.util.AppUtils;
import com.warkiz.widget.IndicatorSeekBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BookKeepingActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private BookkeepingRecyclerViewAdapter adapter;
    private Context context;
    private Bundle bundle;
    private List<AVObject> location_list;
    private boolean loading;
    private int loadTimes;
    private ExtendedFloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_keeping);

        context=this;
        bundle=savedInstanceState;
        Toolbar toolbar = findViewById(R.id.toolbar_bookkeeping_view);
        setToolbar(toolbar);

        initData();
    }

    private void initView(){

        fab = findViewById(R.id.fab_activity_bookkeeping_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();

                //底部Dialog
                BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(context);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_bottom_bookkeeping, null);
                Button btn_ok = dialogView.findViewById(R.id.btn_dialog_bottom_bookkeeping_ok);
                Button btn_cancel = dialogView.findViewById(R.id.btn_dialog_bottom_bookkeeping_cancel);
                AppCompatSpinner spinner_revenue = dialogView.findViewById(R.id.dialog_bottom_bookkeeping_revenue);
                AppCompatSpinner spinner_type = dialogView.findViewById(R.id.dialog_bottom_bookkeeping_type);
                TextInputEditText edit_prince = dialogView.findViewById(R.id.dialog_bottom_bookkeeping_prince);
                TextInputEditText edit_title = dialogView.findViewById(R.id.dialog_bottom_bookkeeping_name_title);
                EditText edit_description = dialogView.findViewById(R.id.dialog_bottom_bookkeeping_description);
                //默认两位小数
                edit_prince.setFilters(new InputFilter[]{new MoneyValueFilter()});

                spinner_revenue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(spinner_revenue.getSelectedItem().toString().equals("收入")){
                            spinner_type.setEnabled(false);
                            if(edit_title.getText().length()<1){
                                edit_title.setText("收入");
                            }
                        }else{
                            spinner_type.setEnabled(true);
                            if(edit_title.getText().length()<1){
                                edit_title.setText("支出");
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(spinner_revenue.getSelectedItem().toString().equals("收入")){
                            spinner_type.setEnabled(false);
                            if(edit_title.getText().length()<1){
                                edit_title.setText("收入");
                            }
                        }else{
                            spinner_type.setEnabled(true);
                            if(edit_title.getText().length()<1){
                                edit_title.setText("支出");
                            }
                        }

                        if(edit_prince.getText().length()<1){
                            Toast.makeText(context,"请输入金额！",Toast.LENGTH_SHORT).show();
                        }else{
                            //存到云
                            AVObject bookkeeping = new AVObject("Bookkeeping");
                            bookkeeping.put("UserId",AVUser.getCurrentUser().getObjectId());
                            bookkeeping.put("time",new Date());
                            bookkeeping.put("prince",edit_prince.getText());
                            bookkeeping.put("title",edit_title.getText());
                            bookkeeping.put("description",edit_description.getText());
                            bookkeeping.put("revenue",spinner_revenue.getSelectedItem().toString());
                            bookkeeping.put("type",spinner_type.getSelectedItem().toString());

                            bookkeeping.saveInBackground(new SaveCallback() {
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
                    }
                });

                btn_cancel.setOnClickListener(v -> mBottomSheetDialog.dismiss());
                mBottomSheetDialog.setContentView(dialogView);
                mBottomSheetDialog.show();
            }
        });

        mRecyclerView = findViewById(R.id.activity_bookkeeping_rv);

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
        adapter = new BookkeepingRecyclerViewAdapter(location_list, context);
        adapter.setBundle(bundle);
        mRecyclerView.setAdapter(adapter);
//        adapter.addHeader();
//        adapter.setItems(live_data);
//        adapter.addFooter();



//        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(adapter);
//        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
//        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        //下拉加载更多
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout_bookkeeping_view);
        swipeRefreshLayout.setColorSchemeResources(R.color.google_blue, R.color.google_green, R.color.google_red, R.color.google_yellow);
        swipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {

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
                fab.shrink();
            } else {
                fab.extend();
            }

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (!loading && linearLayoutManager.getItemCount() == (linearLayoutManager.findLastVisibleItemPosition() + 1)) {
                loadMoreData();
                loading = true;
            }
        }
    };

    private void initData() {
        location_list = new ArrayList<>();
        AVQuery<AVObject> query = new AVQuery<>("Bookkeeping");
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);// 启动查询缓存
        query.setMaxCacheAge(24 * 3600 * 1000); //设置为一天，单位毫秒
        query.whereEqualTo("UserId", AVUser.getCurrentUser().getObjectId());
        query.limit(1000);
        query.orderByDescending("createdAt");// 按时间，降序排列
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if(list!=null){
                    Log.d("MyMapActivity","共查询到：" + list.size() + "条数据。");
                    location_list.addAll(list);
                }
                initView();
            }
        });
//        data = new ArrayList<>();
//        for (int i = 1; i <= 20; i++) {
//            data.add(i + "");
//        }
        loadTimes = 0;
    }
    private void loadMoreData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                location_list.clear();//清除数据
                location_list = new ArrayList<>();
                AVQuery<AVObject> query = new AVQuery<>("Bookkeeping");
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
                            location_list.addAll(list);
                            adapter=new BookkeepingRecyclerViewAdapter(location_list,context);
                            mRecyclerView.setAdapter(adapter);
                        }

                        swipeRefreshLayout.setRefreshing(false);//加载成功后再消失
                    }
                });
            }
        }, 1500);
    }
}
