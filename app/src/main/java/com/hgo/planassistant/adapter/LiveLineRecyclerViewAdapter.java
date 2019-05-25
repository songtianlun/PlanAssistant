package com.hgo.planassistant.adapter;

import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.Context;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CloudQueryCallback;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.SaveCallback;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.hgo.planassistant.App;
import com.hgo.planassistant.R;
import com.hgo.planassistant.activity.LiveLineActivity;
import com.hgo.planassistant.view.onMoveAndSwipedListener;
import com.warkiz.widget.IndicatorSeekBar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by zhang on 2016.08.07.
 */
public class LiveLineRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements onMoveAndSwipedListener {

    private Context context;
    private List<AVObject> mItems;

    private int color = 0;
    private View parentView;

    private final int TYPE_NORMAL = 1;
    private final int TYPE_FOOTER = 2;
    private final int TYPE_HEADER = 3;
    private final String FOOTER = "footer";
    private final String HEADER = "header";

    public LiveLineRecyclerViewAdapter(List<AVObject> list,Context context) {
        this.context = context;
        this.mItems = list;
    }


    public void Updatelist(List<AVObject> list){
        this.mItems = list;
        notifyDataSetChanged();
    }
//
//    public void setItems(List<AVObject> data) {
//        this.mItems.addAll(data);
//        notifyDataSetChanged();
//    }
//
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
//    public void changeItem(int position, AVObject insertData) {
//        mItems.remove(position);
//        notifyItemChanged(position,insertData);
//    }
//
//    public void addItems(List<AVObject> data) {
////        mItems.add(HEADER);
//        mItems.addAll(data);
//        notifyItemInserted(mItems.size() - 1);
//    }
//
//    public void addHeader() {
////        this.mItems.add(HEADER);
//        notifyItemInserted(mItems.size() - 1);
//    }
//
//    public void addFooter() {
////        mItems.add(FOOTER);
//        notifyItemInserted(mItems.size() - 1);
//    }
//
//    public void removeFooter() {
//        mItems.remove(mItems.size() - 1);
//        notifyItemRemoved(mItems.size());
//    }
//
//    public void setColor(int color) {
//        this.color = color;
//        notifyDataSetChanged();
//    }


    //为每个Item inflater出一个View
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        parentView = parent;
//        if (viewType == TYPE_NORMAL) {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_liveline_recycler_view, parent, false);
//            return new RecyclerViewHolder(view);
//        } else if (viewType == TYPE_FOOTER) {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_liveline_recycler_footer, parent, false);
//            return new FooterViewHolder(view);
//        } else {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_liveline_recycler_header, parent, false);
//            return new HeaderViewHolder(view);
//        }
        return new RecyclerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_liveline_recycler_view, parent, false));
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

//            if (color == 1) {
//                recyclerViewHolder.rela_round.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.google_blue)));
//            } else if (color == 2) {
//                recyclerViewHolder.rela_round.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.google_green)));
//            } else if (color == 3) {
//                recyclerViewHolder.rela_round.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.google_yellow)));
//            } else if (color == 4) {
//                recyclerViewHolder.rela_round.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.google_red)));
//            } else {
//                recyclerViewHolder.rela_round.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.gray)));
//            }

            recyclerViewHolder.rela_round.startAnimation(aa);

            int score = (int)mItems.get(position).get("score");

            recyclerViewHolder.tv_title.setText(mItems.get(position).get("score").toString());

            if(score>80){
                recyclerViewHolder.rela_round.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.google_green)));
            }else if(score>60){
                recyclerViewHolder.rela_round.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.google_yellow)));
            }else{
                recyclerViewHolder.rela_round.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.google_red)));
            }

//            Date DT_livetime = (Date)mItems.get(position).get("livetime");
//            Date DT_createAt = (Date)mItems.get(position).get("createdAt");
//            Calendar livetime = Calendar.getInstance().setTime(mItems.get(position).get("livetime"));
            Calendar C_livetime = Calendar.getInstance();
            C_livetime.setTime((Date)mItems.get(position).get("livetime"));
//            Calendar C_createAt = Calendar.getInstance();
//            C_createAt.setTime((Date)mItems.get(position).get("createdAt"));

            recyclerViewHolder.tv_time.setText(C_livetime.get(Calendar.HOUR_OF_DAY)+"时 --" + "当前状态："+mItems.get(position).get("remarks").toString());
            recyclerViewHolder.tv_date.setText("记录时间："+DateFormat.getDateTimeInstance().format((Date)mItems.get(position).get("createdAt")));
//            Log.i("LLRVAdapter","当前数据:"+mItems.get(position).get("score").toString());


            //项目点击事件
            recyclerViewHolder.mView.setOnClickListener(view -> {
                Log.i("LIRVAdapter",String.valueOf(mItems.get(position).getObjectId()));
                Calendar livetime = Calendar.getInstance();
                livetime.setTime((Date)mItems.get(position).get("livetime"));//获取时间
                int liveline_Score = (int)mItems.get(position).get("score");//获取精力值
                String remarks = mItems.get(position).get("remarks").toString();//获取备注

                //底部Dialog
                BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(context);
                LayoutInflater localinflater =  (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View dialogView = localinflater.inflate(R.layout.dialog_bottom_liveline_score_edit, null);
                Button btn_dialog_bottom_sheet_delete = dialogView.findViewById(R.id.btn_dialog_bottom_sheet_edit_delete);
                Button btn_dialog_bottom_sheet_ok = dialogView.findViewById(R.id.btn_dialog_bottom_sheet_edit_ok);
                Button btn_dialog_bottom_sheet_cancel = dialogView.findViewById(R.id.btn_dialog_bottom_sheet_edit_cancel);
                TextView TV_dialog_button_liveline_score_remark = dialogView.findViewById(R.id.dialog_button_liveline_score_edit_remark);
                IndicatorSeekBar seekBar = dialogView.findViewById(R.id.dialog_button_liveline_score_edit_score);//seekbar控件
                TextView TV_time = dialogView.findViewById(R.id.dialog_button_liveline_score_edit_time);


                String now_time_string = (new SimpleDateFormat("HH:mm")).format(livetime.getTime()); //格式化liveline时间
                TV_time.setText(now_time_string);
                TV_dialog_button_liveline_score_remark.setText(remarks);
                seekBar.setProgress(liveline_Score);

                TV_time.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TimePickerDialog livetime_timePickerDialog = new TimePickerDialog(context,(view1, hour, minute) -> {
                            livetime.set(Calendar.HOUR_OF_DAY,hour);
                            livetime.set(Calendar.MINUTE,minute);
                            Log.i("TrackActivity",livetime.get(Calendar.HOUR_OF_DAY) + ":" + livetime.get(Calendar.MINUTE));
                            TV_time.setText(hour+" 时 "+minute +"分");
                        }, livetime.get(Calendar.HOUR_OF_DAY), livetime.get(Calendar.MINUTE),true);
                        livetime_timePickerDialog.show();
                    }
                });

                TV_dialog_button_liveline_score_remark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(context,v);
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

                btn_dialog_bottom_sheet_cancel.setOnClickListener(v -> mBottomSheetDialog.dismiss());

                btn_dialog_bottom_sheet_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int score = seekBar.getProgress();
                        Date now_date = livetime.getTime();//获取精力曲线时间

                        //更新数据
//                        AVObject liveline = AVObject.createWithoutData("liveline",mItems.get(position).getObjectId());
                        AVObject liveline = mItems.get(position);
                        liveline.put("UserId", AVUser.getCurrentUser().getObjectId());
                        liveline.put("livetime",now_date);
                        liveline.put("score",score);
                        liveline.put("remarks",TV_dialog_button_liveline_score_remark.getText());
                        liveline.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    //成功
                                    Snackbar.make(view, context.getString(R.string.succefully), Snackbar.LENGTH_LONG)
                                            .setAction(context.getString(R.string.main_snack_bar_action), view -> {
                                            }).show();
//                                    changeItem(position,liveline);
                                    Updatelist(mItems);
//                                    RemoveItem(position);
//                                    addItem(liveline);
//                                    Updatelist(mItems);
//                                adapter.addItem(linearLayoutManager.findFirstVisibleItemPosition() + 1, insertData);
                                } else {
                                    // 失败的原因可能有多种，常见的是用户名已经存在。
//                        showProgress(false);
                                    Toast.makeText(App.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        mBottomSheetDialog.dismiss();
                    }
                });
                btn_dialog_bottom_sheet_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBottomSheetDialog.dismiss();
                        AVObject del_obj = mItems.get(position);

                        del_obj.deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(AVException e) {
                                if(e==null){
                                    Snackbar.make(view, context.getString(R.string.succefully), Snackbar.LENGTH_LONG)
                                            .setAction(context.getString(R.string.main_snack_bar_action), view -> {
                                            }).show();
                                    RemoveItem(position);
                                    Updatelist(mItems);
                                }else{
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });


                mBottomSheetDialog.setContentView(dialogView);
                mBottomSheetDialog.show();

//                Intent intent = new Intent(context, ShareViewActivity.class);
//                intent.putExtra("color", color);
//                context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation
//                        ((Activity) context, recyclerViewHolder.rela_round, "shareView").toBundle());
            });
        }
    }

//    @Override
//    public int getItemViewType(int position) {
//        String s = mItems.get(position);
//        switch (s) {
//            case HEADER:
//                return TYPE_HEADER;
//            case FOOTER:
//                return TYPE_FOOTER;
//            default:
//                return TYPE_NORMAL;
//        }
//    }

    @Override
    public int getItemCount() {
        return mItems.size();
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
        private TextView tv_title, tv_time, tv_date;

        private RecyclerViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            rela_round = itemView.findViewById(R.id.rela_round);
            tv_title = itemView.findViewById(R.id.tv_recycler_item_title);
            tv_time = itemView.findViewById(R.id.tv_recycler_item_time);
            tv_date = itemView.findViewById(R.id.tv_recycler_item_date);
        }
    }

//    private class FooterViewHolder extends RecyclerView.ViewHolder {
//        private ProgressBar progress_bar_load_more;
//
//        private FooterViewHolder(View itemView) {
//            super(itemView);
//            progress_bar_load_more = itemView.findViewById(R.id.progress_bar_load_more);
//        }
//    }
//
//    private class HeaderViewHolder extends RecyclerView.ViewHolder {
//        private TextView header_text;
//
//        private HeaderViewHolder(View itemView) {
//            super(itemView);
//            header_text = itemView.findViewById(R.id.header_text);
//        }
//    }

}
