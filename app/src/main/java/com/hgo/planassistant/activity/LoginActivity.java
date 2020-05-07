package com.hgo.planassistant.activity;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SaveCallback;
import com.hgo.planassistant.App;
import com.hgo.planassistant.R;
import com.umeng.analytics.MobclickAgent;

public class LoginActivity extends BaseActivity  implements View.OnClickListener{

    private CheckBox checkBox_showcode;
    private Button button_signin, button_signout;
//    private Button button_skip;
    private EditText et_account,et_password;
    private AppCompatSpinner spinner_server;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = this;

        //检测是否已登录
        if (AVUser.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            LoginActivity.this.finish();
        }

        initView();

        // 测试 SDK 是否正常工作的代码
//        AVObject testObject = new AVObject("TestObject");
//        testObject.put("words","Hello World!");
//        testObject.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(AVException e) {
//                if(e == null){
//                    Log.d("saved","success!");
//                }
//            }
//        });
    }

    public void initView(){
        Toolbar toolbar = findViewById(R.id.toolbar_login);
        setToolbar(toolbar);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_about_card_show);
        ScrollView scroll_about = findViewById(R.id.scroll_login);
        scroll_about.startAnimation(animation);

        checkBox_showcode = findViewById(R.id.checkbox_login_showcode);
        button_signin = findViewById(R.id.button_login_login);
        button_signout = findViewById(R.id.button_login_signout);
//        button_skip = findViewById(R.id.button_login_skip);
        et_account = findViewById(R.id.et_card_login_account);
        et_password = findViewById(R.id.et_card_login_password);
        spinner_server = findViewById(R.id.card_login_server);

        checkBox_showcode.setOnClickListener(this);
        button_signin.setOnClickListener(this);
        button_signout.setOnClickListener(this);
//        button_skip.setOnClickListener(this);

        SharedPreferences SP_setting = getApplication().getSharedPreferences("setting",App.getContext().MODE_PRIVATE);
        SharedPreferences.Editor SP_edit = SP_setting.edit();

        String oldvalue = SP_setting.getString("pref_list_system_server", "cn-north-2");
        Log.d("LoginActivity","当前后端："+oldvalue);
        if(oldvalue.equals("cn-north-1")){
            spinner_server.setSelection(0,true);
        }else if(oldvalue.equals("cn-north-2")){
            spinner_server.setSelection(1,true);
        }else if(oldvalue.equals("cn-north-3")){
            spinner_server.setSelection(2,true);
        }else if(oldvalue.equals("international")){
            spinner_server.setSelection(3,true);
        }else{
            spinner_server.setSelection(0,true);
        }

        spinner_server.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String oldvalue = App.getApplication().getSharedPreferences("setting",MODE_PRIVATE).getString("pref_list_system_server","cn-north-1");
//                String oldvalue = PreferenceManager.getDefaultSharedPreferences(App.getContext()).getString("pref_list_system_server", "cn-north");
                Log.d("LoginActivity","Server Old Value:" + oldvalue);
                Log.d("LoginActivity","Server new Value:" + spinner_server.getSelectedItem().toString());
                if(!oldvalue.equals(spinner_server.getSelectedItem().toString())){

                    new AlertDialog.Builder(context)
                            .setMessage("重启程序后修改生效，是否重启?")
                            .setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
//                                    System.exit(0);

                                    SP_edit.putString("pref_list_system_server",spinner_server.getSelectedItem().toString());
                                    SP_edit.commit();
                                    Log.d("LoginActivity","修改后后端："+SP_setting.getString("pref_list_system_server","cn-north-1"));
                                    final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }
    @Override // back button
    public void onBackPressed()
    {
        // super.onBackPressed();
        // 注释掉这行,back键不退出activity
        // Log.i(LOG_TAG, "onBackPressed");
        Intent skip_intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(skip_intent);
        finish();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.checkbox_login_showcode:
                if (checkBox_showcode.isChecked()){
                    et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    Log.i("LoginActivity","显示密码！");
                }else{
                    et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                break;
            case R.id.button_login_login:
                attemptLogin();
                break;
            case R.id.button_login_signout:
                Intent register_intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(register_intent);
                finish();
                break;
//            case R.id.button_login_skip:
//                Intent skip_intent = new Intent(LoginActivity.this, MainActivity.class);
//                startActivity(skip_intent);
//                finish();
//                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        MobclickAgent.onResume(this); // umeng+ 统计 //AUTO页面采集模式下不调用
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPause(this);  // umeng+ 统计 //AUTO页面采集模式下不调用
    }

    private void attemptLogin() {
        et_account.setError(null);
        et_password.setError(null);

        final String username = et_account.getText().toString();
        final String password = et_password.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            et_password.setError(getString(R.string.error_invalid_password));
            focusView = et_password;
            cancel = true;
        }

        if (TextUtils.isEmpty(username)) {
            et_account.setError(getString(R.string.error_field_required));
            focusView = et_account;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
//            showProgress(true);

            AVUser.logInInBackground(username, password, new LogInCallback<AVUser>() {
                @Override
                public void done(AVUser avUser, AVException e) {
                    if (e == null) {
                        LoginActivity.this.finish();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    } else {
//                        showProgress(false);
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
}
