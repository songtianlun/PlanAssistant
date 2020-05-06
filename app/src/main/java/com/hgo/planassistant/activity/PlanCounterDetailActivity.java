package com.hgo.planassistant.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hgo.planassistant.App;
import com.hgo.planassistant.R;
import com.umeng.analytics.MobclickAgent;
import com.warkiz.widget.IndicatorSeekBar;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PlanCounterDetailActivity extends AppCompatActivity {

    RelativeLayout rela_round_big;
    TextView tv_card_pc_detail_title;
    TextView tv_card_pc_detail_description;
    TextView tv_card_pc_detail_finish;
    TextView tv_card_pc_detail_log_info;
    Button bt_card_pc_detail_button_finish, bt_card_pc_detail_button_restore, bt_card_pc_detail_button_delete;

    Context pc_detail_activity;
    AVObject nowObject;
    AVObject Last_Log;
    String ObjectId;

    private Calendar Last_PCD_time;
    private Calendar now_time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_counter_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        pc_detail_activity = this;

        ObjectId = getIntent().getStringExtra("objectid");
        Log.i("PCDA","当前id: " + getIntent().getStringExtra("objectid"));
//        nowObject = getIntent().get("object");

        Last_PCD_time = Calendar.getInstance();


        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        MobclickAgent.onResume(this); // umeng+ 统计//AUTO页面采集模式下不调用
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPause(this);  // umeng+ 统计 //AUTO页面采集模式下不调用
    }

    private void initData(){

        // 方法一
        AVQuery<AVObject> query = new AVQuery<>("PlanCounter");
        query.getInBackground(ObjectId, new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                // object 就是 id 为 558e20cbe4b060308e3eb36c 的 Todo 对象实例
                // objectId 为空时不会报错，
                // 可以通过检验 avObject.getObjectId 方法是否返回空字符串判断其存在性
                if(e==null){
                    nowObject = avObject;
                    initView();
                }else{
                    Log.i("PCDetailActivity","error："+ e.toString());
//                    Toast.makeText(App.getContext(),"error："+ e.toString(),Toast.LENGTH_LONG).show();
                    new AlertDialog.Builder(pc_detail_activity)
                            .setMessage("当前计数器不存在,请刷新.")
                            .setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    PlanCounterDetailActivity.this.finish();
                                }
                            })
                            .show();
                }

            }
        });
        // 方法二
//        if(nowObject!=null){
//            initView();
//        }

    }

    private void initView() {

        rela_round_big = findViewById(R.id.pc_detailrela_round_big);
        tv_card_pc_detail_title = findViewById(R.id.card_pc_detail_title);
        tv_card_pc_detail_description = findViewById(R.id.card_pc_detail_description);
        tv_card_pc_detail_finish = findViewById(R.id.card_pc_detail_finish);
        tv_card_pc_detail_log_info = findViewById(R.id.card_pc_detail_log_info);
        bt_card_pc_detail_button_finish  = findViewById(R.id.card_pc_detail_button_finish);
        bt_card_pc_detail_button_restore = findViewById(R.id.card_pc_detail_button_restore);
        bt_card_pc_detail_button_delete = findViewById(R.id.card_pc_detail_button_delete);

        int nowcounter = (int)nowObject.get("NowCounter");
        int aimscounter = (int)nowObject.get("AimsCounter");
        int percentage = nowcounter*100 / aimscounter;

        if(percentage>80){
            rela_round_big.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.google_green)));
        }else if(percentage>60){
            rela_round_big.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.google_yellow)));
        }else{
            rela_round_big.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.google_red)));
        }
        refresh();
        initListener();
    }

    private void initListener(){
        tv_card_pc_detail_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_title = new EditText(pc_detail_activity);
                et_title.setText(nowObject.get("title").toString());
                new AlertDialog.Builder(pc_detail_activity)
                        .setMessage("修改计划标题")
                        .setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                Log.i("PersonInfoActivity",et_change.getText().toString());
                                nowObject.put("title", et_title.getText().toString());
                                nowObject.saveInBackground();
//                                refresh();
                            }
                        })
                        .setView(et_title)
                        .show();
            }
        });

        tv_card_pc_detail_description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_description = new EditText(pc_detail_activity);
                et_description.setText(nowObject.get("description").toString());
                new AlertDialog.Builder(pc_detail_activity)
                        .setMessage("修改计划描述")
                        .setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                Log.i("PersonInfoActivity",et_change.getText().toString());
                                nowObject.put("description", et_description.getText().toString());
                                nowObject.saveInBackground();
//                                refresh();
                            }
                        })
                        .setView(et_description)
                        .show();
            }
        });
        tv_card_pc_detail_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //底部Dialog
                BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(pc_detail_activity);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_pc_detail_items_finish_edit, null);
                Button btn_dialog_bottom_sheet_ok = dialogView.findViewById(R.id.dialog_pc_detail_items_finish_edit_ok);
                Button btn_dialog_bottom_sheet_cancel = dialogView.findViewById(R.id.dialog_pc_detail_items_finish_edit_cancel);
                IndicatorSeekBar seekBar = dialogView.findViewById(R.id.dialog_pc_detail_items_finish_edit_aims);//seekbar控件

                int progress = (int)nowObject.get("AimsCounter");
                seekBar.setProgress(progress);

                btn_dialog_bottom_sheet_cancel.setOnClickListener(view -> mBottomSheetDialog.dismiss());
                btn_dialog_bottom_sheet_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nowObject.put("AimsCounter", seekBar.getProgress());
                        nowObject.saveInBackground();
                        mBottomSheetDialog.dismiss();
                    }
                });
                mBottomSheetDialog.setContentView(dialogView);
                mBottomSheetDialog.show();

            }
        });
        tv_card_pc_detail_log_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        bt_card_pc_detail_button_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                PlanCounterDetailActivity.this.finish();
                int now_counter,aims_counter;
                now_counter = nowObject.getInt("NowCounter");
                aims_counter = nowObject.getInt("AimsCounter");

                if(TryToday()){
                    AVObject today_log = new AVObject("PlanCounterLog");// 构建对象
                    today_log.put("UserId", AVUser.getCurrentUser().getObjectId());// 设置用户ID
                    today_log.put("PCID", ObjectId);
                    today_log.put("NowCounter",now_counter+1);
                    today_log.put("AimsCounter",aims_counter);
//                    today_log.saveInBackground();// 保存到服务端

                    today_log.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                //成功
                                Snackbar.make(v, getString(R.string.succefully), Snackbar.LENGTH_LONG)
                                        .setAction(getString(R.string.main_snack_bar_action), view -> {
                                        }).show();
                                if(now_counter>=(aims_counter-1)){
                                    nowObject.put("done", true);
                                    Toast.makeText(App.getContext(), "恭喜您完成该百日计划！", Toast.LENGTH_SHORT).show();
                                }
                                nowObject.put("NowCounter",now_counter+1);
                                nowObject.saveInBackground();
                                refresh();

                            } else {
                                // 失败的原因可能有多种，常见的是用户名已经存在。
                                Toast.makeText(App.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }else{
                    Toast.makeText(App.getContext(),"今日已记录, 请明天继续",Toast.LENGTH_LONG).show();
                    refresh();
                }


            }
        });
        bt_card_pc_detail_button_restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
        bt_card_pc_detail_button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(pc_detail_activity)
                        .setMessage("确定删除当前计数器吗?")
                        .setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                nowObject.deleteInBackground();
                                PlanCounterDetailActivity.this.finish();
                            }
                        })
                        .show();
            }
        });
    }
    private void refresh(){

        AVQuery<AVObject> query = new AVQuery<>("PlanCounter");
        query.getInBackground(ObjectId, new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                // object 就是 id 为 558e20cbe4b060308e3eb36c 的 Todo 对象实例
                // objectId 为空时不会报错，
                // 可以通过检验 avObject.getObjectId 方法是否返回空字符串判断其存在性
                nowObject = avObject;

                // set round
                int nowcounter = (int)nowObject.get("NowCounter");
                int aimscounter = (int)nowObject.get("AimsCounter");
                int percentage = nowcounter*100 / aimscounter;
                String starttime = nowObject.get("createdAt").toString();

                if(percentage>80){
                    rela_round_big.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.google_green)));
                }else if(percentage>60){
                    rela_round_big.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.google_yellow)));
                }else{
                    rela_round_big.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.google_red)));
                }
                // set info
                tv_card_pc_detail_title.setText(nowObject.get("title").toString());
                tv_card_pc_detail_description.setText(nowObject.get("description").toString());
                tv_card_pc_detail_finish.setText(nowcounter + "/" + aimscounter + " \n" + "( 始于 "+ starttime + " )");

                AVQuery<AVObject> query_log = new AVQuery<>("PlanCounterLog");
                // 启动查询缓存
                query_log.whereEqualTo("PCID", ObjectId);
                query_log.limit(1000);
                query_log.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        Log.i("PCDetailActivity","共查询到：" + list.size() + "条数据。");
//                        Toast.makeText(App.getContext(),"共查询到：" + list.size() + "条数据。",Toast.LENGTH_LONG).show();
                        String PCDlog = "";
                        for (AVObject obj: list){
//                            AVObject point = obj.getAVObject("point");
                            int nowcounter = (int)obj.get("NowCounter");
                            int aimscounter = (int)obj.get("AimsCounter");
                            PCDlog += obj.get("createdAt").toString() + "记录:" + nowcounter + " / " + aimscounter + "\n";
                        }
                        tv_card_pc_detail_log_info.setText(PCDlog);
                    }
                });


            }
        });

        // 刷新最新的一条打卡记录
        AVQuery<AVObject> log_query = new AVQuery<>("PlanCounterLog");
        log_query.whereEqualTo("PCID",ObjectId);
        log_query.orderByDescending("createdAt");// 按时间，降序排列
        log_query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if (avObject!=null){
                    Last_Log = avObject;
                    refresh_finish_button();
                }else{
                    Last_Log = null;
                    refresh_finish_button();
                }
            }
        });


    }

    private void refresh_finish_button(){

        bt_card_pc_detail_button_finish.setEnabled(false);
        if(TryToday()){
            bt_card_pc_detail_button_finish.setEnabled(true);
        }

    }
    private boolean TryToday(){
        now_time = Calendar.getInstance();

        if (Last_Log != null) {
            Last_PCD_time.setTime(Last_Log.getCreatedAt());
            if(Last_PCD_time.get(Calendar.YEAR)==now_time.get(Calendar.YEAR)){
                if(Last_PCD_time.get(Calendar.MONTH)==now_time.get(Calendar.MONTH)){
                    if(Last_PCD_time.get(Calendar.DAY_OF_MONTH)==now_time.get(Calendar.DAY_OF_MONTH)){
                        //同一天
                        return false;
                    }
                }
            }else{
                return true;
            }
        }
        return true;
    }

}
