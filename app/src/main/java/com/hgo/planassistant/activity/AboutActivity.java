package com.hgo.planassistant.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.hgo.planassistant.App;
import com.hgo.planassistant.Constant;
import com.hgo.planassistant.DataSource;
import com.hgo.planassistant.R;
import com.hgo.planassistant.util.AppUtils;
import com.sunfusheng.FirUpdater;
import com.umeng.analytics.MobclickAgent;

public class AboutActivity extends BaseActivity implements View.OnClickListener {

    private Context this_context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        this_context = this;
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        Toolbar toolbar = findViewById(R.id.toolbar_about);
        setToolbar(toolbar);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        initView();
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

    public void initView() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_about_card_show);
        ScrollView scroll_about = findViewById(R.id.scroll_about);
        scroll_about.startAnimation(animation);

//        TextView tv_card_about_2_shop = findViewById(R.id.tv_card_about_2_shop);
        TextView tv_card_about_2_email = findViewById(R.id.tv_card_about_2_email);
        TextView tv_card_about_2_git_hub = findViewById(R.id.tv_card_about_2_git_hub);
        TextView tv_card_about_2_website = findViewById(R.id.tv_card_about_2_website);
        TextView tv_card_about_source_licenses = findViewById(R.id.tv_card_about_source_licenses);
        RelativeLayout card_about_1_version = findViewById(R.id.card_about_1_version);
//        tv_card_about_2_shop.setOnClickListener(this);
        tv_card_about_2_email.setOnClickListener(this);
        tv_card_about_2_git_hub.setOnClickListener(this);
        tv_card_about_2_website.setOnClickListener(this);
        tv_card_about_source_licenses.setOnClickListener(this);
        card_about_1_version.setOnClickListener(this);


//        FloatingActionButton fab = findViewById(R.id.fab_about_share);
//        fab.setOnClickListener(this);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(300);
        alphaAnimation.setStartOffset(600);

        TextView tv_about_version = findViewById(R.id.tv_about_version);
        tv_about_version.setText(AppUtils.getVersionName(this));
        tv_about_version.startAnimation(alphaAnimation);

        // 检查更新
        FirUpdater.getInstance(this)
                .apiToken(DataSource.fir_im_API_TOKEN)
                .appId(DataSource.fir_im_FIR_UPDATER_APP_ID)
                .apkPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/")
                .checkVersion();

        Log.i("AboutActivity","Check Update.");
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
//            case R.id.tv_card_about_2_shop:
//                intent.setData(Uri.parse(Constant.APP_URL));
//                intent.setAction(Intent.ACTION_VIEW);
//                startActivity(intent);
//                break;

            case R.id.tv_card_about_2_email:
                intent.setAction(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse(Constant.EMAIL));
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.about_email_intent));
                //intent.putExtra(Intent.EXTRA_TEXT, "Hi,");
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(AboutActivity.this, getString(R.string.about_not_found_email), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.tv_card_about_source_licenses:
                final Dialog dialog = new Dialog(this, R.style.DialogFullscreenWithTitle);
                dialog.setTitle(getString(R.string.about_source_licenses));
                dialog.setContentView(R.layout.dialog_source_licenses);
                WebView webView = dialog.findViewById(R.id.web_source_licenses);
                webView.loadUrl("file:///android_asset/open_source_license.html");
                MaterialButton btn_source_licenses_close = dialog.findViewById(R.id.btn_source_licenses_close);
                btn_source_licenses_close.setOnClickListener(v -> dialog.dismiss());
                dialog.show();
                break;
            case R.id.tv_card_about_2_git_hub:
                intent.setData(Uri.parse(Constant.GIT_HUB));
                intent.setAction(Intent.ACTION_VIEW);
                startActivity(intent);
                break;

            case R.id.tv_card_about_2_website:
                intent.setData(Uri.parse(Constant.MY_WEBSITE));
                intent.setAction(Intent.ACTION_VIEW);
                startActivity(intent);
                break;
            case R.id.catd_about_1:
//                startActivity(new Intent(this, AppLogActivity.class));
                break;
            case R.id.card_about_1_version:
                startActivity(new Intent(this, AppLogActivity.class));
                FirUpdater.getInstance(this_context)
                        .apiToken(DataSource.fir_im_API_TOKEN)
                        .appId(DataSource.fir_im_FIR_UPDATER_APP_ID)
                        .apkPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/")
                        .checkVersion();
                Log.i("AboutActivity","Check Update.");
                break;

            case R.id.fab_about_share:
                startActivity(new Intent(AboutActivity.this, AppLogActivity.class));
//                intent.setAction(Intent.ACTION_SEND);
//                intent.putExtra(Intent.EXTRA_TEXT, Constant.SHARE_CONTENT);
//                intent.setType("text/plain");
//                startActivity(Intent.createChooser(intent, getString(R.string.share_with)));
                FirUpdater.getInstance(this_context)
                          .apiToken(DataSource.fir_im_API_TOKEN)
                          .appId(DataSource.fir_im_FIR_UPDATER_APP_ID)
                          .apkPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/")
                          .checkVersion();

                Log.i("AboutActivity","Check Update.");
                break;
        }
    }



}
