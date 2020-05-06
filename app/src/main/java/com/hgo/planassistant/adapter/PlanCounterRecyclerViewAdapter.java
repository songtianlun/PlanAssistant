package com.hgo.planassistant.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.SaveCallback;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.hgo.planassistant.App;
import com.hgo.planassistant.R;
import com.hgo.planassistant.activity.PlanCounterDetailActivity;
import com.hgo.planassistant.view.onMoveAndSwipedListener;
import com.warkiz.widget.IndicatorSeekBar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by zhang on 2016.08.07.
 */
public class PlanCounterRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements onMoveAndSwipedListener {

    private Context context;
    private List<AVObject> mItems;

    private View parentView;

    public PlanCounterRecyclerViewAdapter(List<AVObject> list, Context context) {
        this.context = context;
        this.mItems = list;
    }


    public void Updatelist(List<AVObject> list){
        this.mItems = list;
        notifyDataSetChanged();
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

    //为每个Item inflater出一个View
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        parentView = parent;

        return new RecyclerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plan_counter_recycler_view, parent, false));
    }

    //适配渲染数据到View
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof RecyclerViewHolder) {
            final RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;

            Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim_recycler_item_show);
            recyclerViewHolder.mView.startAnimation(animation);

            AlphaAnimation aa1 = new AlphaAnimation(1.0f, 0.1f);
            aa1.setDuration(400);
            recyclerViewHolder.rela_round.startAnimation(aa1);

            AlphaAnimation aa = new AlphaAnimation(0.1f, 1.0f);
            aa.setDuration(400);

            recyclerViewHolder.rela_round.startAnimation(aa);

            int nowcounter = (int)mItems.get(position).get("NowCounter");
            int aimscounter = (int)mItems.get(position).get("AimsCounter");
            int percentage = nowcounter*100 / aimscounter;

            recyclerViewHolder.tv_title.setText(mItems.get(position).get("title").toString());
            if(mItems.get(position).getBoolean("done")){
                recyclerViewHolder.tv_title.setText(mItems.get(position).get("title").toString()+"（已完成）");
            }
            recyclerViewHolder.tv_description.setText(mItems.get(position).get("description").toString());

            if(percentage>80){
                recyclerViewHolder.rela_round.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.google_green)));
            }else if(percentage>60){
                recyclerViewHolder.rela_round.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.google_yellow)));
            }else{
                recyclerViewHolder.rela_round.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.google_red)));
            }


            String nowString = "当前进度: " + nowcounter + " / " + aimscounter;

            recyclerViewHolder.tv_now.setText(nowString);
            Log.i("LLRVAdapter","当前数据:"+mItems.get(position).get("NowCounter").toString());


//            项目点击事件
            recyclerViewHolder.mView.setOnClickListener(view -> {
                String objectID = String.valueOf(mItems.get(position).getObjectId());
                Log.i("LIRVAdapter",objectID);
                Intent intent = new Intent(context, PlanCounterDetailActivity.class);
                intent.putExtra("objectid", objectID);
                intent.putExtra("object",mItems.get(position));
                context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation
                        ((Activity) context, recyclerViewHolder.rela_round, "shareView").toBundle());
            });
        }
    }

    @Override
    public int getItemCount() {
        if(mItems!=null){
            return mItems.size();
        }else{
            return 0;
        }

    }


    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mItems, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(final int position) {
        mItems.remove(position);
        notifyItemRemoved(position);

        mItems.get(position).deleteInBackground(new DeleteCallback() {
            @Override
            public void done(AVException e) {
                if(e==null){
                    Snackbar.make(parentView, context.getString(R.string.item_swipe_dismissed), Snackbar.LENGTH_LONG)
                            .setAction(context.getString(R.string.item_swipe_undo), view -> {
                            }).show();
                }
            }
        });


    }


    private class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private RelativeLayout rela_round;
        private TextView tv_title, tv_now, tv_description;

        private RecyclerViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            rela_round = itemView.findViewById(R.id.rela_round);
            tv_title = itemView.findViewById(R.id.tv_recycler_item_title);
            tv_now = itemView.findViewById(R.id.tv_recycler_item_now);
            tv_description = itemView.findViewById(R.id.tv_recycler_item_end);
        }
    }


}
