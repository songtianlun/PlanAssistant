package com.hgo.planassistant.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.hgo.planassistant.App;
import com.hgo.planassistant.R;
import com.hgo.planassistant.activity.MainActivity;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;

import java.util.Calendar;
import java.util.Date;

import static android.app.PendingIntent.getActivity;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class TencentLocationService extends Service implements

        TencentLocationListener{

//    private int interval = 0; //定位时间间隔

    private SharedPreferences SP_setting;

    private TencentLocationManager mLocationManager;
    public TencentLocationService() {
    }

    //创建服务时调用
    @Override
    public void onCreate() {
        super.onCreate();
//        Log.d(TAG, "onCreate");
    }

    //服务执行的操作
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.d(TAG, "onStartCommand");

        // android 8.0后需要给notification一个channel one id
        String CHANNEL_ONE_ID = "com.primedu.cn";
        String CHANNEL_ONE_NAME = "Channel One";
        NotificationChannel notificationChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
        }

        // 在API11之后构建Notification的方式
    Notification.Builder builder = new Notification
            .Builder(this.getApplicationContext()) //获取一个Notification构造器
            .setChannelId(CHANNEL_ONE_ID);
    Intent nfIntent = new Intent(this, MainActivity.class);
    builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0)) // 设置PendingIntent
           .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher_round)) // 设置下拉列表中的图标(大图标)
           .setContentTitle("规划助手") // 设置下拉列表里的标题
           .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
           .setContentText("位置服务正在运行！") // 设置上下文内容
           .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间
    Notification notification = builder.build(); // 获取构建好的Notification
    startForeground(110, notification);// 开始前台服务

        mLocationManager = TencentLocationManager.getInstance(this);
        /* 保证调整坐标系前已停止定位 */
        mLocationManager.removeUpdates(null);
        // 设置 wgs84 坐标系
        mLocationManager
                .setCoordinateType(TencentLocationManager.COORDINATE_TYPE_WGS84);

        // 创建定位请求
        TencentLocationRequest request = TencentLocationRequest.create();
        // 修改定位请求参数, 定位周期 3000 ms
//        request.setInterval(3000);

        //读取设置信息初始化定位设置
        SP_setting = this.getSharedPreferences("setting",MODE_MULTI_PROCESS);
//        interval =  SP_setting.getInt("pref_list_location_time",4000);
        request.setAllowCache(true); //允许缓存
        request.setAllowGPS(SP_setting.getBoolean("pref_location_tencent_usegps",false));
        request.setIndoorLocationMode(SP_setting.getBoolean("pref_location_tencent_indoor",false));
        request.setInterval(Integer.parseInt(SP_setting.getString("pref_list_location_time","4000")));

        // 开始定位
        int error = mLocationManager.requestLocationUpdates(request, this);

        Log.i("TencentLocationService","注册位置监听服务器状态码:" + error);

        return super.onStartCommand(intent, flags, startId);

    }

    //销毁服务时调用
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if(mLocationManager!=null)
            mLocationManager.removeUpdates(this);
        stopForeground(true);// 停止前台服务--参数：表示是否移除之前的通知
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onLocationChanged(TencentLocation tencentLocation, int i, String s) {
        Log.i("TencentLocationService","位置改变回调！");
        String msg = null;
        if (i == TencentLocation.ERROR_OK) {
            Calendar now = Calendar.getInstance();
            String now_str;
            now_str = now.get(Calendar.YEAR)+"-";
            if((now.get(Calendar.MONTH)+1)<10){
                now_str+="0"+(now.get(Calendar.MONTH)+1)+"-";
            }else{
                now_str +=(now.get(Calendar.MONTH)+1)+"-";
            }
            if(now.get(Calendar.DATE)<10){
                now_str+="0"+now.get(Calendar.DATE)+"";
            }else{
                now_str+=now.get(Calendar.DATE)+"";
            }//格式化格式，保证2位
            // 定位成功
            StringBuilder sb = new StringBuilder();
            sb.append("(纬度=").append(tencentLocation.getLatitude()).append(",经度=")
                    .append(tencentLocation.getLongitude()).append(",精度=")
                    .append(tencentLocation.getAccuracy()).append("), 来源=")
                    .append(tencentLocation.getProvider()).append(", 地址=")
                    // 注意, 根据国家相关法规, wgs84坐标下无法提供地址信息
                    .append("{84坐标下不提供地址!}");
            msg = sb.toString();

            // 构造方法传入的参数，对应的就是控制台中的 Class Name
            AVGeoPoint point = new AVGeoPoint(tencentLocation.getLatitude(), tencentLocation.getLongitude()); //获取经纬度
            AVObject track_record = new AVObject("trajectory");
            track_record.put("UserId", AVUser.getCurrentUser().getObjectId());// 设置用户ID
            track_record.put("time",new Date()); //设置时间戳
            track_record.put("geo_coordinate","WGS-84");//坐标系
            track_record.put("point",point); //设置坐标
            track_record.put("altitude",tencentLocation.getAltitude()); //设置高程
            track_record.put("type",tencentLocation.getProvider()); //设置类型
            track_record.put("precision",tencentLocation.getAccuracy()); //精度
//            track_record.put("interval",SP_setting.getString("pref_list_location_time","4000")); //定位时间间隔
//            track_record.put("interval", 4000); //定位时间间隔
            track_record.put("createDate",now_str); //文本日期
//            track_record.saveInBackground();// 保存到服务端
            track_record.saveEventually();// 离线保存
        } else {
            // 定位失败
            msg = "定位失败: " + s;
        }
        Log.i("TencentLocationService",msg);
    }

    @Override
    public void onStatusUpdate(String s, int i, String s1) {

    }

    //
//    Date getDateWithDateString(String dateString) {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date date = dateFormat.parse(dateString);
//        return date;
//    }
}
