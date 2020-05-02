package com.hgo.planassistant.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.baidu.aip.nlp.AipNlp;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hgo.planassistant.R;
import com.hgo.planassistant.adapter.MoodWhisperRecyclerViewAdapter;
import com.hgo.planassistant.adapter.MyMapRecyclerViewAdapter;
import com.hgo.planassistant.model.EmotionalTrendResults;
import com.hgo.planassistant.model.EmotionalTrendResultsItems;
import com.hgo.planassistant.tools.BaiDuAiAuthService;
import com.hgo.planassistant.util.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.avos.avoscloud.LogUtil.log.show;

public class MoodWhisperActivity extends BaseActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private MoodWhisperRecyclerViewAdapter adapter;
    private List<AVObject> whisper_data;
    private Context MoodWhisperContext;
    private Bundle MW_bundle;
    private ExtendedFloatingActionButton AddWhisper;
    private boolean loading;
    private int loadTimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_whisper);

        MW_bundle = savedInstanceState; //在recycleview中使用的bundle
        MoodWhisperContext = this;

        Toolbar toolbar = findViewById(R.id.toolbar_mood_whisper);
        setToolbar(toolbar);

        initDate();
    }

    private void initView(){
        AddWhisper = findViewById(R.id.efab_mood_whisper_add);
        mRecyclerView = findViewById(R.id.recycler_view_mood_whisper_view);

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
        adapter = new MoodWhisperRecyclerViewAdapter(whisper_data, MoodWhisperContext);
        adapter.setBundle(MW_bundle);
        mRecyclerView.setAdapter(adapter);


        //下拉加载更多
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout_mood_whisper_view);
        swipeRefreshLayout.setColorSchemeResources(R.color.google_blue, R.color.google_green, R.color.google_red, R.color.google_yellow);
        swipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {

            loadMoreData();
//            swipeRefreshLayout.setRefreshing(false);//加载成功后再消失
        }, 20));

        mRecyclerView.addOnScrollListener(scrollListener);

        AddWhisper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et_whisper = new EditText(MoodWhisperContext);
                et_whisper.setHint("心情小语");
                new AlertDialog.Builder(MoodWhisperContext)
                        .setMessage("记录心情")
                        .setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                Log.i("PersonInfoActivity",et_change.getText().toString());
                                SaveEmotionalTrend(et_whisper.getText().toString());
                            }
                        })
                        .setView(et_whisper)
                        .show();
            }
        });
    }
    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (dy > 0) {
                AddWhisper.shrink();
            } else {
                AddWhisper.extend();
            }

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (!loading && linearLayoutManager.getItemCount() == (linearLayoutManager.findLastVisibleItemPosition() + 1)) {
                loadMoreData();
                loading = true;
            }
        }
    };

    private void initDate(){
        whisper_data = new ArrayList<>();
        AVQuery<AVObject> query = new AVQuery<>("MoodWhisper");
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
                    whisper_data.addAll(list);
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

                whisper_data.clear();//清除数据
                whisper_data = new ArrayList<>();
                AVQuery<AVObject> query = new AVQuery<>("MoodWhisper");
                query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);// 启动查询缓存
                query.setMaxCacheAge(24 * 3600 * 1000); //设置为一天，单位毫秒
                query.whereEqualTo("UserId", AVUser.getCurrentUser().getObjectId());
                query.limit(1000);
                query.orderByDescending("createdAt");// 按时间，降序排列
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        Log.i("LiveLIneActivity","共查询到：" + list.size() + "条数据。");
                        whisper_data.addAll(list);
//                        adapter.Updatelist(list);
                        adapter=new MoodWhisperRecyclerViewAdapter(whisper_data, MoodWhisperContext);
                        mRecyclerView.setAdapter(adapter);
                        swipeRefreshLayout.setRefreshing(false);//加载成功后再消失
                    }
                });

            }
        }, 1500);
    }

    private void SaveEmotionalTrend(String mood) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                AipNlp client = new AipNlp("19675043", "5yiaMUOYcceqEHUYZ4l41HZw", "hMvziR18hZO4nX5SkjertIsqu0H28zkN");

                // 传入可选参数调用接口
                HashMap<String, Object> options = new HashMap<String, Object>();

                // 情感倾向分析
                JSONObject res = client.sentimentClassify(mood, options);
                EmotionalTrendResults emotionalTrendResults = JSON.parseObject(res.toString(), EmotionalTrendResults.class);
                EmotionalTrendResultsItems emotionalTrendResultsItems = emotionalTrendResults.getItems().get(0);

                Log.d("MoodWhisperActivity", "获取情感趋势，result: "+res);

                AVObject MoodWhisper = new AVObject("MoodWhisper");
                MoodWhisper.put("UserId", AVUser.getCurrentUser().getObjectId());// 设置用户ID
                MoodWhisper.put("time", Calendar.getInstance().getTime()); //设置时间戳
                MoodWhisper.put("whisper", mood);
                MoodWhisper.put("positive_prob",emotionalTrendResultsItems.getPositive_prob());
                MoodWhisper.put("negative_prob",emotionalTrendResultsItems.getNegative_prob());
                MoodWhisper.put("confidence",emotionalTrendResultsItems.getConfidence());
                MoodWhisper.put("sentiment",emotionalTrendResultsItems.getSentiment()); //表示情感极性分类结果，0:负向，1:中性，2:正向

                MoodWhisper.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        loadMoreData();
                        if(emotionalTrendResultsItems.getSentiment()==0){
                            new AlertDialog.Builder(MoodWhisperContext)
                                    .setTitle("心情不好？试着获得正面情感吧！")
                                    .setMessage("所有能带来享受、满足和安全感的活动都能够激发正面情感。由于人们兴趣各异,这些活动可能是唱歌,园艺,跳舞,亲热,练瑜伽,读书,体育运动,参观博物馆,听音乐会,或者仅仅是在忙碌地社交之后静坐自省。因此我们建议您做一些令自己享受、满足或是能给自己带来安全感的活动来激发您的正向情感。")
                                    .setPositiveButton(getString(R.string.dialog_ok), null)
                                    .show();
                        }else{
                            Toast.makeText(MoodWhisperContext, "保存成功！",Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }
        }).start();


    }

}
