package com.hgo.planassistant;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;


import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;

import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;

import org.litepal.LitePal;

import java.util.concurrent.Executor;

import cn.leancloud.AVOSCloud;

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
        initUM();

        // 初始化 LitePal
        LitePal.initialize(this);
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

    /**
     * 初始化 友盟+统计
     */

    private void initUM(){
        // 在此处调用基础组件包提供的初始化函数 相应信息可在应用管理 -> 应用信息 中找到 http://message.umeng.com/list/apps
        // 参数一：当前上下文context；
        // 参数二：应用申请的Appkey（需替换）；
        // 参数三：渠道名称；
        // 参数四：设备类型，必须参数，传参数为UMConfigure.DEVICE_TYPE_PHONE则表示手机；传参数为UMConfigure.DEVICE_TYPE_BOX则表示盒子；默认为手机；
        // 参数五：Push推送业务的secret 填充Umeng Message Secret对应信息（需替换）
        UMConfigure.init(this, "5e368c604ca357e87b00002c", "default", UMConfigure.DEVICE_TYPE_PHONE, "b5aa2d965aef24d48204785e3bcbe281");
        /**
         * 设置组件化的Log开关
         * 参数: boolean 默认为false，如需查看LOG设置为true
         */
        UMConfigure.setLogEnabled(true);
        // 选用AUTO页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);

        //获取消息推送代理示例
        PushAgent mPushAgent = PushAgent.getInstance(this);
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
                Log.i("APP","注册成功：deviceToken：-------->  " + deviceToken);
            }
            @Override
            public void onFailure(String s, String s1) {
                Log.e("APP","注册失败：-------->  " + "s:" + s + ",s1:" + s1);
            }
        });
    }

    private void initServer(){
        //获取一个 SharedPreferences对象
        //第一个参数：指定文件的名字，只会续写不会覆盖
        //第二个参数：MODE_PRIVATE只有当前应用程序可以续写
        //MODE_MULTI_PROCESS 允许多个进程访问同一个SharedPrecferences
        SharedPreferences SP_setting = App.getApplication().getSharedPreferences("setting",MODE_PRIVATE);
        String Server = SP_setting.getString("pref_list_system_server","cn-north-2"); // 检测当前设置的服务器类型
        Log.d("App","读取到后端信息："+ Server);
        if(Server.equals("cn-north-1")){
            // leancloud cn init
//            AVOSCloud.initialize(this,"eR1JFxB61gInL1GhmaURGdAx-gzGzoHsz","1nddY6z37rpVV2OzxXuWPdSI");
            AVOSCloud.initialize(this, "eR1JFxB61gInL1GhmaURGdAx-gzGzoHsz", "1nddY6z37rpVV2OzxXuWPdSI", "https://paapi.leancloud.frytea.com");
            Log.d("App","当前后端：华北一区");
        }else if(Server.equals("international")){
            // leancloud international init
//            AVOSCloud.initialize(this,"dRmA0kDOgX827gAlEM4JnX5Y-MdYXbMMI","HU07vgGnTDbGIl9faMgxhgzp");
            AVOSCloud.initialize(this, "dRmA0kDOgX827gAlEM4JnX5Y-MdYXbMMI", "HU07vgGnTDbGIl9faMgxhgzp");
            Log.d("App","当前后端：国际区");
        }else if(Server.equals("cn-north-2")){
//            AVOSCloud.initialize(this,"qk9hVb8Gh93X5LB0tNdR4j1e-gzGzoHsz","IK2b5Y150czy6g3g6cKpbCEg");
            AVOSCloud.initialize(this, "qk9hVb8Gh93X5LB0tNdR4j1e-gzGzoHsz", "IK2b5Y150czy6g3g6cKpbCEg", "https://paapi.leancloud.frytea.com");
            Log.d("App","当前后端：华北二区");
        }else if(Server.equals("cn-north-3")) {
//            AVOSCloud.initialize(this, "ByRMx627tHHJX9RUBCVeT1jT-gzGzoHsz", "2Rr1nQcDheb42EOrCOjsk61e");
            AVOSCloud.initialize(this, "ByRMx627tHHJX9RUBCVeT1jT-gzGzoHsz", "2Rr1nQcDheb42EOrCOjsk61e", "https://paapi.leancloud.frytea.com");
            Log.d("App","当前后端：华北三区");
        }
        else{
            // coming soon
            AVOSCloud.initialize(this, "qk9hVb8Gh93X5LB0tNdR4j1e-gzGzoHsz", "IK2b5Y150czy6g3g6cKpbCEg", "https://paapi.leancloud.frytea.com");
            Log.d("App","当前后端数据异常，切为默认区：华北二区");
        }



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
