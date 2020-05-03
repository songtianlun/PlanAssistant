package com.hgo.planassistant.adapter;

import android.app.TimePickerDialog;
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
public class EnergyEvaluationRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements onMoveAndSwipedListener {

    private Context context;
    private List<AVObject> mItems;

    private int color = 0;
    private View parentView;

    public EnergyEvaluationRecyclerViewAdapter(List<AVObject> list, Context context) {
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
        return new RecyclerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_energy_evaluation_recycler_view, parent, false));
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

            int averagescore = (mItems.get(position).getInt("thinking") + mItems.get(position).getInt("determination"))/2;

//            recyclerViewHolder.tv_title.setText(mItems.get(position).get("score").toString());

            if(averagescore>80){
                recyclerViewHolder.rela_round.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.google_green)));
            }else if(averagescore>60){
                recyclerViewHolder.rela_round.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.google_yellow)));
            }else{
                recyclerViewHolder.rela_round.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.google_red)));
            }


            com.hgo.planassistant.tools.DateFormat dateFormat = new com.hgo.planassistant.tools.DateFormat();
            Calendar date = Calendar.getInstance();
            date.setTime(mItems.get(position).getDate("time"));
            recyclerViewHolder.tv_date.setText("时刻："+ date.get(Calendar.HOUR_OF_DAY) + "时" + date.get(Calendar.MINUTE) + "分");
            recyclerViewHolder.tv_thinking.setText("思维精力（专注程度）：" + mItems.get(position).getInt("thinking"));
            recyclerViewHolder.tv_time.setText("创建于" + dateFormat.GetDetailDescription(mItems.get(position).getDate("createdAt")));
            recyclerViewHolder.tv_determination.setText("意志精力（强度）：" + mItems.get(position).getInt("determination"));
//            Log.i("LLRVAdapter","当前数据:"+mItems.get(position).get("score").toString());


            //项目点击事件
            recyclerViewHolder.mView.setOnClickListener(view -> {
                //底部Dialog
                BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(context);
                LayoutInflater localinflater =  (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View dialogView = localinflater.inflate(R.layout.dialog_bottom_energy_evaluation_edit, null);
                Button btn_ok = dialogView.findViewById(R.id.btn_dialog_bottom_energy_evaluation_determination_edit_ok);
                Button btn_cancel = dialogView.findViewById(R.id.btn_dialog_bottom_energy_evaluation_determination_edit_cancel);
                Button btn_delete = dialogView.findViewById(R.id.btn_dialog_bottom_energy_evaluation_determination_edit_delete);
                IndicatorSeekBar seekBar_thinking = dialogView.findViewById(R.id.dialog_bottom_energy_evaluation_thinking_edit);//seekbar控件
                IndicatorSeekBar seekBar_determination = dialogView.findViewById(R.id.dialog_bottom_energy_evaluation_determination_edit);
                TextView TV_time = dialogView.findViewById(R.id.dialog_bottom_energy_evaluation_edit_time);

                Calendar evaluation_time = Calendar.getInstance();
                evaluation_time.setTime(mItems.get(position).getDate("time"));
                seekBar_thinking.setProgress(mItems.get(position).getInt("thinking"));
                seekBar_determination.setProgress(mItems.get(position).getInt("determination"));
                String time_string = (new SimpleDateFormat("HH:mm")).format(evaluation_time.getTime()); //获取当前时间并格式化
                TV_time.setText(time_string);
                TV_time.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TimePickerDialog livetime_timePickerDialog = new TimePickerDialog(context,(view1, hour, minute) -> {
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
                        AVObject energyEvaluation = AVObject.createWithoutData("EnergyEvaluation",mItems.get(position).getObjectId());
                        energyEvaluation.put("UserId",AVUser.getCurrentUser().getObjectId());
                        energyEvaluation.put("time",evaluation_time.getTime());
                        energyEvaluation.put("thinking",score_thinking);
                        energyEvaluation.put("determination",score_determination);
                        energyEvaluation.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    //成功
                                    Toast.makeText(App.getContext(), "保存成功！（下拉刷新查看）", Toast.LENGTH_SHORT).show();
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
                btn_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AVObject delete = AVObject.createWithoutData("EnergyEvaluation",mItems.get(position).getObjectId());
                        delete.deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    //成功
                                    Toast.makeText(App.getContext(), "删除成功！（下拉刷新查看）", Toast.LENGTH_SHORT).show();
//                                adapter.addItem(linearLayoutManager.findFirstVisibleItemPosition() + 1, insertData);
                                } else {
                                    // 失败的原因可能有多种，常见的是用户名已经存在。
//                        showProgress(false);
                                    Toast.makeText(App.getContext(), "失败，原因：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        mBottomSheetDialog.dismiss();
                    }
                });
                mBottomSheetDialog.show();
//                Log.i("LIRVAdapter",String.valueOf(mItems.get(position).getObjectId()));
//                Calendar livetime = Calendar.getInstance();
//                livetime.setTime((Date)mItems.get(position).get("livetime"));//获取时间
//                int liveline_Score = (int)mItems.get(position).get("score");//获取精力值
//                String remarks = mItems.get(position).get("remarks").toString();//获取备注
//
//                //底部Dialog
//                BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(context);
//                LayoutInflater localinflater =  (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                View dialogView = localinflater.inflate(R.layout.dialog_bottom_liveline_score_edit, null);
//                Button btn_dialog_bottom_sheet_delete = dialogView.findViewById(R.id.btn_dialog_bottom_sheet_edit_delete);
//                Button btn_dialog_bottom_sheet_ok = dialogView.findViewById(R.id.btn_dialog_bottom_sheet_edit_ok);
//                Button btn_dialog_bottom_sheet_cancel = dialogView.findViewById(R.id.btn_dialog_bottom_sheet_edit_cancel);
//                TextView TV_dialog_button_liveline_score_remark = dialogView.findViewById(R.id.dialog_button_liveline_score_edit_remark);
//                IndicatorSeekBar seekBar = dialogView.findViewById(R.id.dialog_button_liveline_score_edit_score);//seekbar控件
//                TextView TV_time = dialogView.findViewById(R.id.dialog_button_liveline_score_edit_time);
//
//
//                String now_time_string = (new SimpleDateFormat("HH:mm")).format(livetime.getTime()); //格式化liveline时间
//                TV_time.setText(now_time_string);
//                TV_dialog_button_liveline_score_remark.setText(remarks);
//                seekBar.setProgress(liveline_Score);
//
//                TV_time.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        TimePickerDialog livetime_timePickerDialog = new TimePickerDialog(context,(view1, hour, minute) -> {
//                            livetime.set(Calendar.HOUR_OF_DAY,hour);
//                            livetime.set(Calendar.MINUTE,minute);
//                            Log.i("TrackActivity",livetime.get(Calendar.HOUR_OF_DAY) + ":" + livetime.get(Calendar.MINUTE));
//                            TV_time.setText(hour+" 时 "+minute +"分");
//                        }, livetime.get(Calendar.HOUR_OF_DAY), livetime.get(Calendar.MINUTE),true);
//                        livetime_timePickerDialog.show();
//                    }
//                });
//
//                TV_dialog_button_liveline_score_remark.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        PopupMenu popupMenu = new PopupMenu(context,v);
//                        popupMenu.getMenuInflater().inflate(R.menu.liveline_popup_menu_main, popupMenu.getMenu());
//                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                            @Override
//                            public boolean onMenuItemClick(MenuItem item) {
//                                TV_dialog_button_liveline_score_remark.setText(item.getTitle());
//                                return false;
//                            }
//                        });
//                        popupMenu.show();
//                    }
//                });
//
//                btn_dialog_bottom_sheet_cancel.setOnClickListener(v -> mBottomSheetDialog.dismiss());
//
//                btn_dialog_bottom_sheet_ok.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        int score = seekBar.getProgress();
//                        Date now_date = livetime.getTime();//获取精力曲线时间
//
//                        //更新数据
////                        AVObject liveline = AVObject.createWithoutData("liveline",mItems.get(position).getObjectId());
//                        AVObject liveline = mItems.get(position);
//                        liveline.put("UserId", AVUser.getCurrentUser().getObjectId());
//                        liveline.put("livetime",now_date);
//                        liveline.put("score",score);
//                        liveline.put("remarks",TV_dialog_button_liveline_score_remark.getText());
//                        liveline.saveInBackground(new SaveCallback() {
//                            @Override
//                            public void done(AVException e) {
//                                if (e == null) {
//                                    //成功
//                                    Snackbar.make(view, context.getString(R.string.succefully), Snackbar.LENGTH_LONG)
//                                            .setAction(context.getString(R.string.main_snack_bar_action), view -> {
//                                            }).show();
////                                    changeItem(position,liveline);
//                                    Updatelist(mItems);
////                                    RemoveItem(position);
////                                    addItem(liveline);
////                                    Updatelist(mItems);
////                                adapter.addItem(linearLayoutManager.findFirstVisibleItemPosition() + 1, insertData);
//                                } else {
//                                    // 失败的原因可能有多种，常见的是用户名已经存在。
////                        showProgress(false);
//                                    Toast.makeText(App.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//
//                        mBottomSheetDialog.dismiss();
//                    }
//                });
//                btn_dialog_bottom_sheet_delete.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        mBottomSheetDialog.dismiss();
//                        AVObject del_obj = mItems.get(position);
//
//                        del_obj.deleteInBackground(new DeleteCallback() {
//                            @Override
//                            public void done(AVException e) {
//                                if(e==null){
//                                    Snackbar.make(view, context.getString(R.string.succefully), Snackbar.LENGTH_LONG)
//                                            .setAction(context.getString(R.string.main_snack_bar_action), view -> {
//                                            }).show();
//                                    RemoveItem(position);
//                                    Updatelist(mItems);
//                                }else{
//                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//                    }
//                });
//
//
//                mBottomSheetDialog.setContentView(dialogView);
//                mBottomSheetDialog.show();

//                Intent intent = new Intent(context, ShareViewActivity.class);
//                intent.putExtra("color", color);
//                context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation
//                        ((Activity) context, recyclerViewHolder.rela_round, "shareView").toBundle());
            });
        }
    }


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
        private TextView tv_thinking, tv_time, tv_determination,tv_date;

        private RecyclerViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            rela_round = itemView.findViewById(R.id.card_item_energy_evaluation_recycler_view_rela_round);
            tv_thinking = itemView.findViewById(R.id.tv_card_item_energy_evaluation_recycler_view_thinking);
            tv_time = itemView.findViewById(R.id.tv_card_item_energy_evaluation_recycler_view_time);
            tv_determination = itemView.findViewById(R.id.tv_card_item_energy_evaluation_recycler_view_determination);
            tv_date = itemView.findViewById(R.id.tv_card_item_energy_evaluation_recycler_view_date);
        }
    }

}
