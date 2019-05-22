package com.hgo.planassistant.activity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.hgo.planassistant.R;

public class LoginActivity extends BaseActivity  implements View.OnClickListener{

    private CheckBox checkBox_showcode;
    private Button button_signin, button_signout, button_skip;
    private EditText et_account,et_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
        button_skip = findViewById(R.id.button_login_skip);
        et_account = findViewById(R.id.et_card_login_account);
        et_password = findViewById(R.id.et_card_login_password);

        checkBox_showcode.setOnClickListener(this);
        button_signin.setOnClickListener(this);
        button_signout.setOnClickListener(this);
        button_skip.setOnClickListener(this);
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
            case R.id.button_login_skip:
                Intent skip_intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(skip_intent);
                finish();
                break;
        }
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
