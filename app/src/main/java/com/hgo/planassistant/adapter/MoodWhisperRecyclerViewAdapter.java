package com.hgo.planassistant.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.baidu.aip.nlp.AipNlp;
import com.hgo.planassistant.R;
import com.hgo.planassistant.activity.DetailPMapActivity;
import com.hgo.planassistant.model.EmotionalTrendResults;
import com.hgo.planassistant.model.EmotionalTrendResultsItems;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.leancloud.AVObject;
import cn.leancloud.types.AVNull;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MoodWhisperRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<AVObject> mItems;
    private Bundle bundle;

    public MoodWhisperRecyclerViewAdapter(List<AVObject> list, Context context) {
        this.mContext = context;
        this.mItems = list;
    }

    public void Updatelist(List<AVObject> list){
//        this.mItems.clear();
        this.mItems = list;
        notifyDataSetChanged();
    }

    public void setBundle(Bundle bundle){
        this.bundle = bundle;
    }

    public void addItem(int position, AVObject insertData) {
        mItems.add(position, insertData);
        notifyItemInserted(position);
    }
    public void addItem(AVObject insertData) {
        int position = 0;
        mItems.add(position, insertData);
        notifyItemInserted(position);
    }
    public void RemoveItem(int position){
        mItems.remove(position);
        notifyItemRemoved(position);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MoodWhisperRecyclerViewAdapter.RecyclerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mood_whisper_recycler_view, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof MoodWhisperRecyclerViewAdapter.RecyclerViewHolder) {
            final MoodWhisperRecyclerViewAdapter.RecyclerViewHolder recyclerViewHolder = (MoodWhisperRecyclerViewAdapter.RecyclerViewHolder) holder;
            //加载名称、备注
            com.hgo.planassistant.tools.DateFormat dateFormat = new com.hgo.planassistant.tools.DateFormat();
            recyclerViewHolder.TV_title.setText(mItems.get(position).getString("whisper"));
            recyclerViewHolder.TV_time.setText(dateFormat.GetDetailDescription(mItems.get(position).getDate("time")));

            //监听项目点击事件
            recyclerViewHolder.mItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText et_whisper = new EditText(mContext);
                    et_whisper.setHint("心情小语");
                    et_whisper.setText(mItems.get(position).getString("whisper"));
                    new AlertDialog.Builder(mContext)
                            .setMessage("修改心情")
                            .setNegativeButton(mContext.getString(R.string.dialog_delete), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    AVObject MoodWhisper = AVObject.createWithoutData("MoodWhisper",mItems.get(position).getObjectId());
                                    MoodWhisper.deleteInBackground().subscribe(new Observer<AVNull>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {

                                        }

                                        @Override
                                        public void onNext(AVNull avNull) {
                                            Toast.makeText(mContext, "删除成功！（下拉刷新）",Toast.LENGTH_LONG).show();
                                        }

                                        @Override
                                        public void onError(Throwable e) {

                                        }

                                        @Override
                                        public void onComplete() {

                                        }
                                    });
                                }
                            })
                            .setPositiveButton(mContext.getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
//                                Log.i("PersonInfoActivity",et_change.getText().toString());
                                    UpdateEmotionalTrend( et_whisper.getText().toString(),mItems.get(position).getObjectId());
                                }
                            })
                            .setView(et_whisper)
                            .show();
//                    Intent intent = new Intent(mContext, DetailPMapActivity.class);
//                    intent.putExtra("MoodWhisperObjectId", mItems.get(position).getObjectId());
//                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private TextView TV_title;
        private TextView TV_time;
        private CardView mItem;

        private RecyclerViewHolder(View itemView) {
            super(itemView);
            TV_title = (TextView) itemView.findViewById(R.id.tv_recycler_mood_whisper_title);
            TV_time = (TextView) itemView.findViewById(R.id.tv_recycler_mood_whisper_time);
            mItem = (CardView) itemView.findViewById(R.id.card_mood_whisper_recycler_view);
        }
    }

    private void UpdateEmotionalTrend(String mood, String ObjectID) {

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
                AVObject MoodWhisper = AVObject.createWithoutData("MoodWhisper", ObjectID);
                MoodWhisper.put("whisper", mood);
                MoodWhisper.put("positive_prob",emotionalTrendResultsItems.getPositive_prob());
                MoodWhisper.put("negative_prob",emotionalTrendResultsItems.getNegative_prob());
                MoodWhisper.put("confidence",emotionalTrendResultsItems.getConfidence());
                MoodWhisper.put("sentiment",emotionalTrendResultsItems.getSentiment());
                MoodWhisper.saveInBackground().subscribe(new Observer<AVObject>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(AVObject avObject) {
                        Toast.makeText(mContext, "更新成功！（下拉刷新查看）",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
            }
        }).start();
    }
}
