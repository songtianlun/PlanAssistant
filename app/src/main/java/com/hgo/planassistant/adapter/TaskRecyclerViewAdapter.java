package com.hgo.planassistant.adapter;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.RecyclerView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.SaveCallback;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.hgo.planassistant.App;
import com.hgo.planassistant.R;
import com.hgo.planassistant.util.CalendarReminderUtils;
import com.hgo.planassistant.view.onMoveAndSwipedListener;
import com.warkiz.widget.IndicatorSeekBar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * Created by zhang on 2016.08.07.
 */
public class TaskRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements onMoveAndSwipedListener,View.OnClickListener {

    private Context context;
    private List<AVObject> mItems;

    private int color = 0;
    private View parentView;

    private Calendar task_start_time = Calendar.getInstance();
    private Calendar task_end_time = Calendar.getInstance();

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
//    private AppCompatSpinner spinner_cycle;

    private int Position = -1;

    public TaskRecyclerViewAdapter(List<AVObject> list, Context context) {
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
        return new RecyclerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task_recycler_view, parent, false));
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

            recyclerViewHolder.rela_round.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.google_blue)));

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

//            int averagescore = (mItems.get(position).getInt("thinking") + mItems.get(position).getInt("determination"))/2;

//            recyclerViewHolder.tv_title.setText(mItems.get(position).get("score").toString());

//            if(averagescore>80){
//                recyclerViewHolder.rela_round.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.google_green)));
//            }else if(averagescore>60){
//                recyclerViewHolder.rela_round.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.google_yellow)));
//            }else{
//                recyclerViewHolder.rela_round.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.google_red)));
//            }

            com.hgo.planassistant.tools.DateFormat dateFormat = new com.hgo.planassistant.tools.DateFormat();
            Calendar date = Calendar.getInstance();
            if(mItems.get(position).getDate("end_time")!=null){
                date.setTime(mItems.get(position).getDate("end_time"));
                recyclerViewHolder.tv_time.setText("截止时间" + dateFormat.GetDetailDescription(date));
            }else{
                recyclerViewHolder.tv_time.setText("");
            }

            recyclerViewHolder.tv_title.setText(mItems.get(position).getString("task_name"));
            recyclerViewHolder.tv_description.setText(mItems.get(position).getString("task_description"));

            if(mItems.get(position).getBoolean("done")){
                //删除线
//                 recyclerViewHolder.tv_title.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                recyclerViewHolder.tv_title.setText(mItems.get(position).getString("task_name")+"（已完成）");
            }
//            Log.i("LLRVAdapter","当前数据:"+mItems.get(position).get("score").toString());

            //项目点击事件
            recyclerViewHolder.mView.setOnClickListener(view -> {

                Position = position;


                //底部Dialog
                mBottomSheetDialog = new BottomSheetDialog(context);
                LayoutInflater localinflater =  (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View dialogView = localinflater.inflate(R.layout.dialog_bottom_task_edit, null);
                Button btn_ok = dialogView.findViewById(R.id.btn_dialog_bottom_task_edit_ok);
                Button btn_cancel = dialogView.findViewById(R.id.btn_dialog_bottom_task_edit_cancel);
                Button btn_delete = dialogView.findViewById(R.id.btn_dialog_bottom_task_edit_delete);
                Button btn_done = dialogView.findViewById(R.id.btn_dialog_bottom_task_edit_done);
                edit_name = dialogView.findViewById(R.id.dialog_bottom_task_edit_name);
                edit_location = dialogView.findViewById(R.id.dialog_bottom_task_edit_location);
                seekBar_importance = dialogView.findViewById(R.id.dialog_bottom_task_edit_importance);
                TV_description = dialogView.findViewById(R.id.dialog_bottom_task_edit_description);
                TV_start_time = dialogView.findViewById(R.id.dialog_bottom_task_edit_start_time);
                TV_start_date = dialogView.findViewById(R.id.dialog_bottom_task_edit_start_time_date);
                TV_end_time = dialogView.findViewById(R.id.dialog_bottom_task_edit_end_time);
                TV_end_date = dialogView.findViewById(R.id.dialog_bottom_task_edit_end_time_date);
//                MaterialButton clean_start = dialogView.findViewById(R.id.dialog_bottom_task_edit_start_time_clean);
//                MaterialButton clean_end = dialogView.findViewById(R.id.dialog_bottom_task_edit_end_time_clean);
                spinner_remind = dialogView.findViewById(R.id.dialog_bottom_task_edit_remind);
//                spinner_cycle = dialogView.findViewById(R.id.dialog_bottom_task_edit_cycle);


                btn_ok.setOnClickListener(this);
                btn_cancel.setOnClickListener(this);
                btn_delete.setOnClickListener(this);
                btn_done.setOnClickListener(this);
                TV_start_time.setOnClickListener(this);
                TV_start_date.setOnClickListener(this);
                TV_end_time.setOnClickListener(this);
                TV_end_date.setOnClickListener(this);
//                clean_start.setOnClickListener(this);
//                clean_end.setOnClickListener(this);

                if(mItems.get(position).getDate("start_time")!=null){
                    task_start_time.setTime(mItems.get(position).getDate("start_time"));
                    TV_start_date.setText(task_start_time.get(Calendar.YEAR)+"年"+(task_start_time.get(Calendar.MONTH)+1)+"月"+task_start_time.get(Calendar.DATE)+"日");
                    TV_start_time.setText(task_start_time.get(Calendar.HOUR_OF_DAY)+" 时 "+task_start_time.get(Calendar.MINUTE) +"分");
                }
                if(mItems.get(position).getDate("end_time")!=null){
                    task_end_time.setTime(mItems.get(position).getDate("end_time"));
                    TV_end_date.setText(task_end_time.get(Calendar.YEAR)+"年"+(task_end_time.get(Calendar.MONTH)+1)+"月"+task_end_time.get(Calendar.DATE)+"日");
                    TV_end_time.setText(task_end_time.get(Calendar.HOUR_OF_DAY)+" 时 "+task_end_time.get(Calendar.MINUTE) +"分");
                }

                if(mItems.get(position).getString("task_location")!=null)
                    edit_location.setText(mItems.get(position).getString("task_location"));
                if(mItems.get(position).getString("task_description")!=null)
                    TV_description.setText(mItems.get(position).getString("task_description"));

                edit_name.setText(mItems.get(position).getString("task_name"));
                seekBar_importance.setProgress(mItems.get(position).getInt("task_importance"));
                spinner_remind.setSelection(mItems.get(position).getInt("task_remind"));
//                spinner_cycle.setSelection(mItems.get(position).getInt("task_cycle"));

                mBottomSheetDialog.setContentView(dialogView);
                mBottomSheetDialog.show();

//                //底部Dialog
//                BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(context);
//                LayoutInflater localinflater =  (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                View dialogView = localinflater.inflate(R.layout.dialog_bottom_energy_evaluation_edit, null);
//                Button btn_ok = dialogView.findViewById(R.id.btn_dialog_bottom_energy_evaluation_determination_edit_ok);
//                Button btn_cancel = dialogView.findViewById(R.id.btn_dialog_bottom_energy_evaluation_determination_edit_cancel);
//                Button btn_delete = dialogView.findViewById(R.id.btn_dialog_bottom_energy_evaluation_determination_edit_delete);
//                IndicatorSeekBar seekBar_thinking = dialogView.findViewById(R.id.dialog_bottom_energy_evaluation_thinking_edit);//seekbar控件
//                IndicatorSeekBar seekBar_determination = dialogView.findViewById(R.id.dialog_bottom_energy_evaluation_determination_edit);
//                TextView TV_time = dialogView.findViewById(R.id.dialog_bottom_energy_evaluation_edit_time);
//
//                Calendar evaluation_time = Calendar.getInstance();
//                evaluation_time.setTime(mItems.get(position).getDate("time"));
//                seekBar_thinking.setProgress(mItems.get(position).getInt("thinking"));
//                seekBar_determination.setProgress(mItems.get(position).getInt("determination"));
//                String time_string = (new SimpleDateFormat("HH:mm")).format(evaluation_time.getTime()); //获取当前时间并格式化
//                TV_time.setText(time_string);
//                TV_time.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        TimePickerDialog livetime_timePickerDialog = new TimePickerDialog(context,(view1, hour, minute) -> {
//                            evaluation_time.set(Calendar.HOUR_OF_DAY,hour);
//                            evaluation_time.set(Calendar.MINUTE,minute);
//                            Log.i("TrackActivity",evaluation_time.get(Calendar.HOUR_OF_DAY) + ":" + evaluation_time.get(Calendar.MINUTE));
//                            TV_time.setText(hour+" 时 "+minute +"分");
//                        }, evaluation_time.get(Calendar.HOUR_OF_DAY), evaluation_time.get(Calendar.MINUTE),true);
//                        livetime_timePickerDialog.show();
//                    }
//                });
//                mBottomSheetDialog.setContentView(dialogView);
//                btn_ok.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        int score_thinking = seekBar_thinking.getProgress();
//                        int score_determination = seekBar_determination.getProgress();
//                        //存到云
//                        AVObject energyEvaluation = AVObject.createWithoutData("EnergyEvaluation",mItems.get(position).getObjectId());
//                        energyEvaluation.put("UserId",AVUser.getCurrentUser().getObjectId());
//                        energyEvaluation.put("time",evaluation_time.getTime());
//                        energyEvaluation.put("thinking",score_thinking);
//                        energyEvaluation.put("determination",score_determination);
//                        energyEvaluation.saveInBackground(new SaveCallback() {
//                            @Override
//                            public void done(AVException e) {
//                                if (e == null) {
//                                    //成功
//                                    Toast.makeText(App.getContext(), "保存成功！（下拉刷新查看）", Toast.LENGTH_SHORT).show();
////                                adapter.addItem(linearLayoutManager.findFirstVisibleItemPosition() + 1, insertData);
//                                } else {
//                                    // 失败的原因可能有多种，常见的是用户名已经存在。
////                        showProgress(false);
//                                    Toast.makeText(App.getContext(), "保存失败，原因：" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//
//                        mBottomSheetDialog.dismiss();
//                    }
//                });
//
//                btn_cancel.setOnClickListener(v -> mBottomSheetDialog.dismiss());
//                btn_delete.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        AVObject delete = AVObject.createWithoutData("EnergyEvaluation",mItems.get(position).getObjectId());
//                        delete.deleteInBackground(new DeleteCallback() {
//                            @Override
//                            public void done(AVException e) {
//                                if (e == null) {
//                                    //成功
//                                    Toast.makeText(App.getContext(), "删除成功！（下拉刷新查看）", Toast.LENGTH_SHORT).show();
////                                adapter.addItem(linearLayoutManager.findFirstVisibleItemPosition() + 1, insertData);
//                                } else {
//                                    // 失败的原因可能有多种，常见的是用户名已经存在。
////                        showProgress(false);
//                                    Toast.makeText(App.getContext(), "失败，原因：" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//                        mBottomSheetDialog.dismiss();
//                    }
//                });
//                mBottomSheetDialog.show();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_dialog_bottom_task_edit_ok:
                if(edit_name.length()==0){
                    Toast.makeText(App.getContext(), "任务名称不能为空！", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(task_start_time.getTime().getTime()>task_end_time.getTime().getTime()){
                        Toast.makeText(App.getContext(), "结束时间不能小于开始时间，请修改，您的修改未保存。", Toast.LENGTH_SHORT).show();
                    }else{
                        mBottomSheetDialog.dismiss();
                        CalendarReminderUtils calendarReminderUtils = new CalendarReminderUtils();
                        calendarReminderUtils.deleteCalendarEvent(context,mItems.get(Position).getString("task_name"));
                        switch ((int)spinner_remind.getSelectedItemId()){
                            case 0:
                                calendarReminderUtils.addCalendarEvent(context,edit_name.getText().toString(),TV_description.getText().toString(),edit_location.getText().toString(),task_start_time.getTime().getTime(),task_end_time.getTime().getTime());
                                break;
                            case 1:
                                calendarReminderUtils.addCalendarEvent(context,edit_name.getText().toString(),TV_description.getText().toString(),edit_location.getText().toString(),task_start_time.getTime().getTime(),task_end_time.getTime().getTime(),0);
                                break;
                            case 2:
                                calendarReminderUtils.addCalendarEvent(context,edit_name.getText().toString(),TV_description.getText().toString(),edit_location.getText().toString(),task_start_time.getTime().getTime(),task_end_time.getTime().getTime(),15);
                                break;
                            case 3:
                                calendarReminderUtils.addCalendarEvent(context,edit_name.getText().toString(),TV_description.getText().toString(),edit_location.getText().toString(),task_start_time.getTime().getTime(),task_end_time.getTime().getTime(),60);
                                break;
                            case 4:
                                calendarReminderUtils.addCalendarEvent(context,edit_name.getText().toString(),TV_description.getText().toString(),edit_location.getText().toString(),task_start_time.getTime().getTime(),task_end_time.getTime().getTime(), 24*60);
                                break;
                            default:
                                break;

                        }

                        AVObject new_task = AVObject.createWithoutData("Task",mItems.get(Position).getObjectId());
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
//                    new_task.put("task_cycle",spinner_cycle.getSelectedItemId());
                        new_task.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    //成功
                                    Toast.makeText(App.getContext(), "保存成功！", Toast.LENGTH_SHORT).show();
//                                adapter.addItem(linearLayoutManager.findFirstVisibleItemPosition() + 1, insertData);
                                } else {
                                    // 失败的原因可能有多种，常见的是用户名已经存在。
//                        showProgress(false);
                                    Toast.makeText(App.getContext(), "保存失败，原因：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
                break;
            case R.id.btn_dialog_bottom_task_edit_cancel:
                mBottomSheetDialog.dismiss();
                break;
            case R.id.btn_dialog_bottom_task_edit_delete:

                CalendarReminderUtils calendarReminderUtils = new CalendarReminderUtils();
                calendarReminderUtils.deleteCalendarEvent(context,mItems.get(Position).getString("task_name"));

                mBottomSheetDialog.dismiss();
                AVObject delete_task = AVObject.createWithoutData("Task",mItems.get(Position).getObjectId());
                delete_task.deleteInBackground(new DeleteCallback() {
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
                break;
            case R.id.btn_dialog_bottom_task_edit_done:
                mBottomSheetDialog.dismiss();
                AVObject done_task = AVObject.createWithoutData("Task",mItems.get(Position).getObjectId());
                Log.d("TaskRecyclerViewAdapter","任务完成按钮，任务状态："+done_task.getBoolean("done"));
                if(mItems.get(Position).getBoolean("done")){
                    // 任务已完成，设为未完成
                    done_task.put("done",false);
                    Log.d("TaskRecyclerViewAdapter","任务完成按钮，任务已完成，设为未完成！");
                }else{
                    done_task.put("done",true);
                    done_task.put("done_time", Calendar.getInstance().getTime());
                }

                done_task.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            //成功
                            Toast.makeText(App.getContext(), "任务已完成！（下拉刷新查看）", Toast.LENGTH_SHORT).show();
//                                adapter.addItem(linearLayoutManager.findFirstVisibleItemPosition() + 1, insertData);
                        } else {
                            // 失败的原因可能有多种，常见的是用户名已经存在。
//                        showProgress(false);
                            Toast.makeText(App.getContext(), "失败，原因：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
//            case R.id.dialog_bottom_task_start_time_clean:
//                TV_start_date.setText("null");
//                TV_start_time.setText("null");
//                task_start_time = null;
//                break;
//            case R.id.dialog_bottom_task_end_time_clean:
//                TV_end_date.setText("null");
//                TV_end_time.setText("null");
//                task_end_time = null;
//                break;
            case R.id.dialog_bottom_task_edit_start_time_date:
                if(task_start_time!=null){
                    DatePickerDialog start_datePickerDialog = new DatePickerDialog(context, (view1, year, monthOfYear, dayOfMonth) -> {
                        task_start_time.set(Calendar.YEAR,year);
                        task_start_time.set(Calendar.MONTH,monthOfYear);
                        task_start_time.set(Calendar.DATE,dayOfMonth);
                        TV_start_date.setText(year+"年"+(monthOfYear+1)+"月"+dayOfMonth+"日");
                    }, task_start_time.get(Calendar.YEAR), task_start_time.get(Calendar.MONTH), task_start_time.get(Calendar.DAY_OF_MONTH));
                    start_datePickerDialog.show();
                }else{
                    Calendar now = Calendar.getInstance();
                    DatePickerDialog start_datePickerDialog = new DatePickerDialog(context, (view1, year, monthOfYear, dayOfMonth) -> {
                        task_start_time.set(Calendar.YEAR,year);
                        task_start_time.set(Calendar.MONTH,monthOfYear);
                        task_start_time.set(Calendar.DATE,dayOfMonth);
                        TV_start_date.setText(year+"年"+(monthOfYear+1)+"月"+dayOfMonth+"日");
                    }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                    start_datePickerDialog.show();
                }
                break;
            case R.id.dialog_bottom_task_edit_start_time:
                if(task_start_time!=null){
                    TimePickerDialog start_timePickerDialog = new TimePickerDialog(context,(view1, hour, minute) -> {
                        task_start_time.set(Calendar.HOUR_OF_DAY,hour);
                        task_start_time.set(Calendar.MINUTE,minute);
                        TV_start_time.setText(hour+" 时 "+minute +"分");
                    }, task_start_time.get(Calendar.HOUR_OF_DAY), task_start_time.get(Calendar.MINUTE),true);
                    start_timePickerDialog.show();
                }else{
                    Calendar now = Calendar.getInstance();
                    TimePickerDialog start_timePickerDialog = new TimePickerDialog(context,(view1, hour, minute) -> {
                        task_start_time.set(Calendar.HOUR_OF_DAY,hour);
                        task_start_time.set(Calendar.MINUTE,minute);
                        TV_start_time.setText(hour+" 时 "+minute +"分");
                    }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE),true);
                    start_timePickerDialog.show();
                }
                break;
            case R.id.dialog_bottom_task_edit_end_time_date:
                if(task_end_time!=null){
                    DatePickerDialog start_datePickerDialog = new DatePickerDialog(context, (view1, year, monthOfYear, dayOfMonth) -> {
                        task_end_time.set(Calendar.YEAR,year);
                        task_end_time.set(Calendar.MONTH,monthOfYear);
                        task_end_time.set(Calendar.DATE,dayOfMonth);
                        TV_end_date.setText(year+"年"+(monthOfYear+1)+"月"+dayOfMonth+"日");
                    }, task_end_time.get(Calendar.YEAR), task_end_time.get(Calendar.MONTH), task_end_time.get(Calendar.DAY_OF_MONTH));
                    start_datePickerDialog.show();
                }else{
                    Calendar now = Calendar.getInstance();
                    DatePickerDialog start_datePickerDialog = new DatePickerDialog(context, (view1, year, monthOfYear, dayOfMonth) -> {
                        task_end_time.set(Calendar.YEAR,year);
                        task_end_time.set(Calendar.MONTH,monthOfYear);
                        task_end_time.set(Calendar.DATE,dayOfMonth);
                        TV_end_date.setText(year+"年"+(monthOfYear+1)+"月"+dayOfMonth+"日");
                    }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                    start_datePickerDialog.show();
                }
                break;
            case R.id.dialog_bottom_task_edit_end_time:
                if(task_end_time!=null){
                    TimePickerDialog start_timePickerDialog = new TimePickerDialog(context,(view1, hour, minute) -> {
                        task_end_time.set(Calendar.HOUR_OF_DAY,hour);
                        task_end_time.set(Calendar.MINUTE,minute);
                        TV_end_time.setText(hour+" 时 "+minute +"分");
                    }, task_end_time.get(Calendar.HOUR_OF_DAY), task_end_time.get(Calendar.MINUTE),true);
                    start_timePickerDialog.show();
                }else{
                    Calendar now = Calendar.getInstance();
                    TimePickerDialog start_timePickerDialog = new TimePickerDialog(context,(view1, hour, minute) -> {
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


    private class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private RelativeLayout rela_round;
        private TextView tv_title, tv_time, tv_description;

        private RecyclerViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            rela_round = itemView.findViewById(R.id.card_item_task_recycler_view_rela_round);
            tv_title = itemView.findViewById(R.id.tv_card_item_task_recycler_view_title);
            tv_time = itemView.findViewById(R.id.tv_card_item_task_recycler_view_time);
            tv_description = itemView.findViewById(R.id.tv_card_item_task_recycler_view_description);
        }
    }

}
