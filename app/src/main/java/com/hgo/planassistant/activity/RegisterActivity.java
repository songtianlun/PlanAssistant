package com.hgo.planassistant.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.hgo.planassistant.R;
import com.umeng.analytics.MobclickAgent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.leancloud.AVUser;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class RegisterActivity extends BaseActivity implements View.OnClickListener{

    private EditText et_username, et_password, et_confirm, et_email;
    private Button button_finish, button_back, button_skip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();
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

    private void initView(){
        Toolbar toolbar = findViewById(R.id.toolbar_register);
        setToolbar(toolbar);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_about_card_show);
        ScrollView scroll_about = findViewById(R.id.scroll_register);
        scroll_about.startAnimation(animation);

        et_username = findViewById(R.id.et_card_register_account);
        et_password = findViewById(R.id.et_card_register_password);
        et_confirm = findViewById(R.id.et_card_register_confirm);
        et_email = findViewById(R.id.et_card_register_email);

        button_finish = findViewById(R.id.button_register_finish);
        button_back = findViewById(R.id.button_register_back);
        button_skip = findViewById(R.id.button_register_skip);

        button_finish.setOnClickListener(this);
        button_back.setOnClickListener(this);
        button_skip.setOnClickListener(this);
    }
    @Override // back button
    public void onBackPressed()
    {
        // super.onBackPressed();
        // 注释掉这行,back键不退出activity
        // Log.i(LOG_TAG, "onBackPressed");
        Intent skip_intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(skip_intent);
        finish();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_register_finish:
                attemptRegister();
                break;
            case R.id.button_register_back:
                Intent back_intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(back_intent);
                finish();
                break;
            case R.id.button_register_skip:
                Intent skip_intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(skip_intent);
                finish();
                break;
        }
    }

    private void attemptRegister() {
        et_username.setError(null);
        et_password.setError(null);
        et_confirm.setError(null);
        et_email.setError(null);

        String username = et_username.getText().toString();
        String password = et_password.getText().toString();
        String confirm = et_confirm.getText().toString();
        String email = et_email.getText().toString();

        boolean cancel = false;
        View focusView = null;

        //short
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            et_password.setError(getString(R.string.error_invalid_password));
            focusView = et_password;
            cancel = true;
        }

        //inconsistant
        if(!password.equals(confirm)){
            et_confirm.setError(getString(R.string.error_inconsistant_password));
            focusView = et_confirm;
            cancel = true;

        }
        //username empty
        if (TextUtils.isEmpty(username)) {
            et_username.setError(getString(R.string.error_field_required));
            focusView = et_username;
            cancel = true;
        }
        //email
        if (TextUtils.isEmpty(email) || !isEmail(email)){
            et_email.setError(getString(R.string.error_invalid_email));
            focusView = et_email;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
//            showProgress(true);

            AVUser user = new AVUser();// 新建 AVUser 对象实例
            user.setUsername(username);// 设置用户名
            user.setPassword(password);// 设置密码
            user.setEmail(email);//设置邮箱
            user.put("introduction","I'm" + username);
            user.put("birth","1997年10月29日");
            user.put("sex","保密");

            user.put("nickname",username);
            user.signUpInBackground().subscribe(new Observer<AVUser>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(AVUser avUser) {
                    // 注册成功，把用户对象赋值给当前用户 AVUser.getCurrentUser()
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    RegisterActivity.this.finish();
                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onComplete() {

                }
            });

        }
    }
    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
    public static boolean isEmail(String email){
        if (null==email || "".equals(email)) return false;
        //Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
        Pattern p =  Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");//复杂匹配
        Matcher m = p.matcher(email);
        return m.matches();
    }
}
