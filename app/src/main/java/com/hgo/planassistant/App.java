package com.hgo.planassistant;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;

import com.avos.avoscloud.AVOSCloud;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;

import com.hgo.planassistant.service.TencentLocationService;
import java.lang.reflect.Array;
import java.util.concurrent.Executor;

/**
 * Created by zhangxiao on 2019/4/2
 */
public class App extends Application {

    private Handler mHandler;
    private Executor mExecutor;
    private static Context context;
    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mHandler = new Handler();
        mExecutor = AsyncTask.THREAD_POOL_EXECUTOR;

        initServer();
    }

    public void runOnUi(Runnable runnable) {
        if (mHandler != null) {
            mHandler.post(runnable);
        } else {
            mHandler = new Handler(Looper.getMainLooper());
            mHandler.post(runnable);
        }
    }

    public void runOnBackground(Runnable runnable) {
        if (mExecutor != null) {
            mExecutor.execute(runnable);
        } else {
            mExecutor = AsyncTask.THREAD_POOL_EXECUTOR;
            mExecutor.execute(runnable);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        context = base;
    }

    public static Context getContext() {
        return context;
    }

    public static App getApplication() {
        return instance;
    }

    private void initServer(){
        //获取一个 SharedPreferences对象
        //第一个参数：指定文件的名字，只会续写不会覆盖
        //第二个参数：MODE_PRIVATE只有当前应用程序可以续写
        //MODE_MULTI_PROCESS 允许多个进程访问同一个SharedPrecferences
        SharedPreferences SP_setting = App.getApplication().getSharedPreferences("setting",MODE_PRIVATE);
        String Server = SP_setting.getString("pref_list_system_server","international"); // 检测当前设置的服务器类型

        if(Server.equals("cn-north")){
            // leancloud cn init
            AVOSCloud.initialize(this,"eR1JFxB61gInL1GhmaURGdAx-gzGzoHsz","1nddY6z37rpVV2OzxXuWPdSI");
        }else if(Server.equals("international")){
            // leancloud international init
            AVOSCloud.initialize(this,"dRmA0kDOgX827gAlEM4JnX5Y-MdYXbMMI","HU07vgGnTDbGIl9faMgxhgzp");
        }else{
            // coming soon
        }

        // 正式发布前去除
        AVOSCloud.setDebugLogEnabled(false);


        //Bugly false为调试模式
//        CrashReport.initCrashReport(getApplicationContext(), "fb19b7ed69", true);


        // init baidu map
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);


    }
}
