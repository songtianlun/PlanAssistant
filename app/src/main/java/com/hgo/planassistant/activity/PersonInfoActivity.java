package com.hgo.planassistant.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.RequestEmailVerifyCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.hgo.planassistant.App;
import com.hgo.planassistant.R;
import com.hgo.planassistant.util.AppUtils;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;

public class PersonInfoActivity extends BaseActivity implements View.OnClickListener{

    private TextView tv_nickname, tv_birth, tv_sex, tv_introduction, tv_username, tv_email, tv_emailverfied;
    private Button bt_finish,bt_refresh;
    private EditText et_nickname, et_introduction, et_email;
    private Context personinfo_context;
    private Calendar calendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_info);

        initView();
    }
    @Override
    protected void onResume() {
        super.onResume();
        Configuration configuration = getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    private void initView(){
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        Toolbar toolbar = findViewById(R.id.toolbar_personinfo);
        setToolbar(toolbar);

        ImageView image_scrolling_top = findViewById(R.id.image_person_info_top);
        Glide.with(this).load(R.drawable.material_design_3).apply(new RequestOptions().fitCenter()).into(image_scrolling_top);

        if (AppUtils.getScreenWidthDp(this) >= 600) {
            CollapsingToolbarLayout collapsing_toolbar_layout = findViewById(R.id.person_info_toolbar_layout);
            collapsing_toolbar_layout.setExpandedTitleTextColor(ColorStateList.valueOf(Color.TRANSPARENT));
        }

        personinfo_context = this;

        tv_nickname = findViewById(R.id.card_personalinfo_userinfo_nickname);
        tv_birth = findViewById(R.id.card_personalinfo_userinfo_birth);
        tv_sex = findViewById(R.id.card_personalinfo_userinfo_sex);
        tv_introduction = findViewById(R.id.card_personalinfo_introduction);
        tv_username = findViewById(R.id.card_personalinfo_userinfo_username);
        tv_email = findViewById(R.id.card_personalinfo_userinfo_email);
        tv_emailverfied = findViewById(R.id.card_personalinfo_userinfo_emailverfied);
        bt_finish = findViewById(R.id.button_personinfo_finish);
        bt_refresh = findViewById(R.id.button_personinfo_restore);

        calendar = Calendar.getInstance();

        tv_nickname.setText(AVUser.getCurrentUser().get("nickname").toString());

        if(AVUser.getCurrentUser().get("birth")!=null){
            tv_birth.setText(AVUser.getCurrentUser().get("birth").toString());
        }else{
            tv_birth.setText("未知");
        }

        tv_sex.setText(AVUser.getCurrentUser().get("sex").toString());
        tv_introduction.setText(AVUser.getCurrentUser().get("introduction").toString());
        tv_username.setText(AVUser.getCurrentUser().getUsername());
        tv_email.setText(AVUser.getCurrentUser().getEmail());
        tv_emailverfied.setText(AVUser.getCurrentUser().get("emailVerified").toString());

        tv_nickname.setOnClickListener(this);
        tv_sex.setOnClickListener(this);
        tv_birth.setOnClickListener(this);
        tv_introduction.setOnClickListener(this);
        tv_username.setOnClickListener(this);
        tv_email.setOnClickListener(this);
        tv_emailverfied.setOnClickListener(this);
        bt_finish.setOnClickListener(this);
        bt_refresh.setOnClickListener(this);

        et_nickname = new EditText(this);
        et_email = new EditText(this);
        et_introduction = new EditText(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.card_personalinfo_userinfo_nickname:
//                Log.i("PersonInfoActivity","点击昵称！");
                et_nickname.setText(AVUser.getCurrentUser().get("nickname").toString());
                new AlertDialog.Builder(personinfo_context)
                        .setMessage("修改昵称")
                        .setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                Log.i("PersonInfoActivity",et_change.getText().toString());
                                AVUser.getCurrentUser().put("nickname", et_nickname.getText().toString());
                                AVUser.getCurrentUser().saveInBackground();
                                refresh();
                            }
                        })
                        .setView(et_nickname)
                        .show();
                break;
            case R.id.card_personalinfo_userinfo_sex:
                String[] singleChoiceItems = getResources().getStringArray(R.array.dialog_choice_sex);
                int itemSelected = 0 ;
                for(int i=0; i<singleChoiceItems.length;i++){
                    if(singleChoiceItems[i].equals(tv_sex.getText())){
                        itemSelected = i;
                        break;
                    }
                }
                new AlertDialog.Builder(personinfo_context)
                        .setTitle("性别")
                        .setSingleChoiceItems(singleChoiceItems, itemSelected, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                AVUser.getCurrentUser().put("sex", singleChoiceItems[i]);
                                AVUser.getCurrentUser().saveInBackground();
                                refresh();
                            }
                        })
                        .setNegativeButton(getString(R.string.dialog_cancel), null)
                        .show();
                break;
            case R.id.card_personalinfo_userinfo_birth:
                DatePickerDialog datePickerDialog = new DatePickerDialog(personinfo_context, (view1, year, monthOfYear, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    String date = DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime());
                    AVUser.getCurrentUser().put("birth", date);
                    AVUser.getCurrentUser().saveInBackground();
                    refresh();
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                break;
            case R.id.card_personalinfo_introduction:
                et_introduction.setText(AVUser.getCurrentUser().get("introduction").toString());
                new AlertDialog.Builder(personinfo_context)
                        .setMessage("修改个人简介")
                        .setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                Log.i("PersonInfoActivity",et_change.getText().toString());
                                AVUser.getCurrentUser().put("introduction", et_introduction.getText().toString());
                                AVUser.getCurrentUser().saveInBackground();
                                refresh();
                            }
                        })
                        .setView(et_introduction)
                        .show();
                break;
            case R.id.card_personalinfo_userinfo_username:
                break;
            case R.id.card_personalinfo_userinfo_email:
                et_email.setText(AVUser.getCurrentUser().getEmail());
                new AlertDialog.Builder(personinfo_context)
                        .setMessage("修改邮箱")
                        .setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                Log.i("PersonInfoActivity",et_change.getText().toString());
                                if(!et_email.getText().toString().equals(AVUser.getCurrentUser().getEmail())){
                                    AVUser.getCurrentUser().setEmail(et_email.getText().toString());
                                    AVUser.getCurrentUser().saveInBackground();
                                    refresh();
                                    dialog.dismiss();
                                    new AlertDialog.Builder(personinfo_context)
                                            .setMessage("邮箱激活邮件已发送至您的邮箱，请检查您的邮箱激活新邮箱！")
                                            .setPositiveButton(getString(R.string.dialog_ok), null)
                                            .show();
                                }else{
                                    dialog.dismiss();
                                }
                            }
                        })
                        .setView(et_email)
                        .show();
                break;
            case R.id.card_personalinfo_userinfo_emailverfied:
                if(AVUser.getCurrentUser().get("emailVerified").equals(false)){
                    new AlertDialog.Builder(personinfo_context)
                            .setMessage("是否发送邮箱激活邮件到您的邮箱，以激活您的邮箱？")
                            .setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AVUser.requestEmailVerifyInBackground(AVUser.getCurrentUser().getEmail(), new RequestEmailVerifyCallback() {
                                        @Override
                                        public void done(AVException e) {
                                            if (e == null) {
                                                // 求重发验证邮件成功
                                                new AlertDialog.Builder(personinfo_context)
                                                        .setMessage("邮件发送成功！")
                                                        .setPositiveButton(getString(R.string.dialog_ok), null)
                                                        .show();
                                            }
                                        }
                                    });
                                }
                            })
                            .setNegativeButton(getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }else{
                    new AlertDialog.Builder(personinfo_context)
                            .setMessage("您的邮箱已激活，无需重复激活！")
                            .setPositiveButton(getString(R.string.dialog_ok), null)
                            .show();
                }

                break;
            case R.id.button_personinfo_restore:
                refresh();
                break;
            case R.id.button_personinfo_finish:
                PersonInfoActivity.this.finish();
                break;
        }
    }

    private void refresh(){

        AVUser.getCurrentUser().saveInBackground();
        tv_nickname.setText(AVUser.getCurrentUser().get("nickname").toString());

        if(AVUser.getCurrentUser().get("birth")!=null){
            tv_birth.setText(AVUser.getCurrentUser().get("birth").toString());
        }else{
            tv_birth.setText("未知");
        }

        tv_sex.setText(AVUser.getCurrentUser().get("sex").toString());
        tv_introduction.setText(AVUser.getCurrentUser().get("introduction").toString());
        tv_username.setText(AVUser.getCurrentUser().getUsername());
        tv_email.setText(AVUser.getCurrentUser().getEmail());
        tv_emailverfied.setText(AVUser.getCurrentUser().get("emailVerified").toString());
    }
}
