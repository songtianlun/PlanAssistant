package com.hgo.planassistant.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.MenuItem;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.RequestPasswordResetCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.hgo.planassistant.App;
import com.hgo.planassistant.R;
import com.hgo.planassistant.Tencent_Location;
import com.hgo.planassistant.adapter.FragmentAdapter;
import com.hgo.planassistant.fragement.HomeFragment;
import com.hgo.planassistant.fragement.PlanFragment;
import com.hgo.planassistant.fragement.RecordFragment;
import com.hgo.planassistant.service.TencentLocationService;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener,TencentLocationListener {

    private DrawerLayout drawer;
    private FloatingActionButton fab;
    private FloatingActionButton bt_feedback;

    private NavigationView navigationView;
    private View headview;
    private TextView tv_nickname, tv_introduction;
    private Context mainactivity_context;


    // 要申请的权限
    private String[] permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE
    };
    private AlertDialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView(); // 初始化View
        initnavi_view(); // 初始化NaviView
        initPermission(); // 动态获取权限
        loadsetting(); // 载入设置
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainactivity_context = this;

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        headview = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        // navigationView.setItemIconTintList(null);
        View headerView = navigationView.getHeaderView(0);
        LinearLayout nav_header = headerView.findViewById(R.id.nav_header);
        nav_header.setOnClickListener(this);

        fab = findViewById(R.id.fab_main);
        fab.setOnClickListener(this);

//        bt_feedback = findViewById(R.id.action_main_feedback);
//        bt_feedback.setOnClickListener(this);

        TabLayout mTabLayout = findViewById(R.id.tab_layout_main);
        ViewPager mViewPager = findViewById(R.id.view_pager_main);
        mViewPager.setOffscreenPageLimit(2);
        List<String> titles = new ArrayList<>();
        titles.add(getString(R.string.tab_title_main_1));
        titles.add(getString(R.string.tab_title_main_2));
        titles.add(getString(R.string.tab_title_main_3));
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(0)));
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(1)));
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(2)));
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new HomeFragment());
        fragments.add(new RecordFragment());
        fragments.add(new PlanFragment());
        FragmentAdapter mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), fragments, titles);
        mViewPager.setAdapter(mFragmentAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(pageChangeListener);


        tv_nickname = (TextView) headview.findViewById(R.id.nav_header_nickname);
        tv_introduction = (TextView) headview.findViewById(R.id.nav_header_introduction);

    }
    private void initnavi_view(){
        //检测是否已登录
        if (AVUser.getCurrentUser() != null) {

            //移除登录相关菜单
            navigationView.getMenu().removeGroup(R.id.nav_non_account);
//            Log.i("MainActivity","用户名: " + AVUser.getCurrentUser().getUsername());
//            Log.i("MainActivity","简介: " + AVUser.getCurrentUser().getString("introduction"));
            // 设置用户名和简介
            tv_nickname.setText(AVUser.getCurrentUser().get("nickname").toString());
            tv_introduction.setText(AVUser.getCurrentUser().get("introduction").toString());
        }else{
            //移除账户相关菜单
            navigationView.getMenu().removeGroup(R.id.nav_account);
        }
    }

    private void initPermission(){
        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for(int j=0; j<permissions.length;j++){
                // 检查该权限是否已经获取
                int i = ContextCompat.checkSelfPermission(this, permissions[j]);
                // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                if (i != PackageManager.PERMISSION_GRANTED) {
                    // 如果没有授予该权限，就去提示用户请求
                    showDialogTipUserRequestPermission();
                }
            }

        }
    }
    // 提示用户该请求权限的弹出框
    private void showDialogTipUserRequestPermission() {

        new AlertDialog.Builder(this)
                .setTitle("有权限需要您授权")
                .setMessage("由于规划助手需要获取您的位置并存储在本地，为你存储个人信息；\n否则，您将无法正常使用规划助手")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startRequestPermission();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).show();
    }

    // 开始提交请求权限
    private void startRequestPermission() {
        ActivityCompat.requestPermissions(this, permissions, 321);
    }

    // 用户权限 申请 的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 321) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                    boolean b = shouldShowRequestPermissionRationale(permissions[0]);
                    if (!b) {
                        // 用户还是想用我的 APP 的
                        // 提示用户去应用设置界面手动开启权限
                        showDialogTipUserGoToAppSettting();
                    } else
                        finish();
                } else {
                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // 提示用户去应用设置界面手动开启权限

    private void showDialogTipUserGoToAppSettting() {

        dialog = new AlertDialog.Builder(this)
                .setTitle("存储权限不可用")
                .setMessage("请在-应用设置-权限-中，允许规划助手使用存储权限来保存用户数据")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 跳转到应用设置界面
                        goToAppSetting();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).show();
    }

    // 跳转到当前应用的设置界面
    private void goToAppSetting() {
        Intent intent = new Intent();

        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);

        startActivityForResult(intent, 123);
    }

    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 检查该权限是否已经获取
                int i = ContextCompat.checkSelfPermission(this, permissions[0]);
                // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                if (i != PackageManager.PERMISSION_GRANTED) {
                    // 提示用户应该去应用设置界面手动开启权限
                    showDialogTipUserGoToAppSettting();
                } else {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            //Log.i("MainActitvity","MainActivity Page Change！");
        }

        @Override
        public void onPageSelected(int position) {

            if (position == 2) {
                fab.show();
            } else {
                fab.hide();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            //Log.i("MainActitvity","MainActivity PageScrollStateChanged！");
        }
    };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Intent intent = new Intent();
        switch (item.getItemId()) {
            case R.id.action_main_about:
//                Intent aboutIntent = new Intent(this, AboutActivity.class);
//                startActivity(aboutIntent);
                intent.setClass(this, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.action_main_settings:
                intent.setClass(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_main_feedback:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()){
            case R.id.nav_menu_login:
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                MainActivity.this.finish();
                break;
            case R.id.nav_changepassword:
                AVUser.requestPasswordResetInBackground(AVUser.getCurrentUser().getEmail(), new RequestPasswordResetCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
//                            Toast.makeText(MainActivity.this, "重置密码邮箱已发送，请检查您的邮箱！", Toast.LENGTH_SHORT).show();
//                            Snackbar.make(getWindow().getDecorView().findViewById(R.id.nav_changepassword), getString(R.string.main_snack_bar), Snackbar.LENGTH_LONG)
//                                    .setAction(getString(R.string.main_snack_bar_action), view -> {
//                                    }).show();
                            new AlertDialog.Builder(mainactivity_context)
                                    .setMessage("重置密码邮件已发送至您的邮箱，请注意查收！")
                                    .setPositiveButton(getString(R.string.dialog_ok), null)
                                    .show();
                        } else {
                            e.printStackTrace();
                            Log.i("MainActivity",e.toString());
                            new AlertDialog.Builder(mainactivity_context)
                                    .setMessage("出现错误，错误原因为: "+ e.toString())
                                    .setPositiveButton(getString(R.string.dialog_ok), null)
                                    .show();
                        }
                    }
                });
                break;
            case R.id.nav_info:
                startActivity(new Intent(MainActivity.this, PersonInfoActivity.class));
                break;
            case R.id.nav_logout:
                AVUser.getCurrentUser().logOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                MainActivity.this.finish();
                break;
            case R.id.nav_menu_track:
                startActivity(new Intent(MainActivity.this, TrackActivity.class));
                break;
            case R.id.nav_menu_mymap:
                startActivity(new Intent(MainActivity.this, MyMapActivity.class));
                break;
            case R.id.nav_menu_liveline:
                startActivity(new Intent(MainActivity.this, LiveLineActivity.class));
                break;
            case R.id.nav_menu_plan:
                startActivity(new Intent(MainActivity.this, PlanActivity.class));
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_main:
                Snackbar.make(v, getString(R.string.main_snack_bar), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.main_snack_bar_action), view -> {
                        }).show();
                break;
            default:
                Log.i("MainActitvity","MainActivity Click！");
                break;
        }
    }

    private void loadsetting(){
        //获取一个 SharedPreferences对象
        //第一个参数：指定文件的名字，只会续写不会覆盖
        //第二个参数：MODE_PRIVATE只有当前应用程序可以续写
        //MODE_MULTI_PROCESS 允许多个进程访问同一个SharedPrecferences
        SharedPreferences SP_setting = App.getApplication().getSharedPreferences("setting",MODE_PRIVATE);
        Boolean FirsrUse = SP_setting.getBoolean("PlanAssistant_FirstUse",false); // 软件是否首次使用
        Boolean Background = SP_setting.getBoolean("pref_location_background_switch",false); //检测服务是否启动

        SharedPreferences.Editor SP_editor = SP_setting.edit();

//        Log.i("MainActivity","本次是第 " + SP_setting.getInt("PlanAssistant_Frequency",1) + " 次启动.");

//        Log.i("MainActivity",String.valueOf(Background));
        // 初始化服务状态
//           SP_setting.getBoolean("pref_location_background_switch",false)


        if(FirsrUse){
            // 首次使用，初始化设置
            //向其中添加数据，是什么数据类型就put什么，前面是键，后面是数据

            SP_editor.putBoolean("PlanAssistant_FirstUse",false); // 清除首次使用标记
            SP_editor.putInt("PlanAssistant_Frequency",1); // 设置软件使用次数为1

            //定位相关设置
            SP_editor.putBoolean("pref_location_switch",true); // 定位服务
            SP_editor.putBoolean("pref_location_background_switch",false); // 后台定位服务
            SP_editor.putString("pref_list_location_type","Battery_Saving"); // 定位模式
            SP_editor.putInt("pref_list_location_time",4000); // 定位间隔
            SP_editor.putBoolean("pref_location_usegps",false); // 是否使用GPS
            SP_editor.putBoolean("pref_location_indoor",false); // 是否室内定位

            // 调用apply方法将添加的数据提交，从而完成存储的动作
            SP_editor.commit();// 提交
            Log.i("MainActivity","首次启动，初始化设置项。");
        }else{
            // 非第一次使用
            int frequency = SP_setting.getInt("PlanAssistant_Frequency",1);
            Log.i("MainActivity","非首次启动，本次是第 " + frequency + " 次启动.");
            frequency++;
            SP_editor.putInt("PlanAssistant_Frequency",frequency);// 启动次数+1
        }
        if(Background){
            Log.i("MainActivity","允许后台启动，正在启动服务。");
            //如果设置启动服务，则激活服务
            Intent startIntent = new Intent(this, TencentLocationService.class);
            startService(startIntent);
        }
//        SP_editor.putInt("pref_list_location_time",4000); // 定位间隔
        SP_editor.commit();// 提交

    }

    @Override
    public void onLocationChanged(TencentLocation tencentLocation, int i, String s) {
        String msg = null;
        if (i == TencentLocation.ERROR_OK) {
            // 定位成功
            StringBuilder sb = new StringBuilder();
            sb.append("(纬度=").append(tencentLocation.getLatitude()).append(",经度=")
                    .append(tencentLocation.getLongitude()).append(",精度=")
                    .append(tencentLocation.getAccuracy()).append("), 来源=")
                    .append(tencentLocation.getProvider()).append(", 地址=")
                    // 注意, 根据国家相关法规, wgs84坐标下无法提供地址信息
                    .append("{84坐标下不提供地址!}");
            msg = sb.toString();
            Toast.makeText(App.getContext(),msg,Toast.LENGTH_LONG).show();
        } else {
            // 定位失败
            msg = "定位失败: " + s;
        }
        Log.i("MainActivity",msg);
    }

    @Override
    public void onStatusUpdate(String s, int i, String s1) {

    }
}
