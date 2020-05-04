package com.hgo.planassistant.fragement;

import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreference;


import com.google.android.material.snackbar.Snackbar;
import com.gyf.cactus.Cactus;
import com.gyf.cactus.callback.CactusCallback;
import com.hgo.planassistant.App;
import com.hgo.planassistant.R;
import com.hgo.planassistant.activity.MainActivity;
import com.hgo.planassistant.activity.PlanCounterDetailActivity;
import com.hgo.planassistant.service.DataCaptureService;
import com.hgo.planassistant.service.TencentLocationService;
import com.hgo.planassistant.tools.DateFormat;

import java.util.Calendar;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener,
        Preference.OnPreferenceClickListener {

    private Context setting_context;
    private ListPreference pref_list_location_ali_type;
    private SwitchPreference pref_location_tencent_usegps;
    private SwitchPreference pref_location_tencent_indoor;
    private SeekBarPreference pref_list_personal_step_target;
    private SeekBarPreference pref_seek_personal_urgent_day;
    private Preference pref_personal_start_sleep;
    private Preference pref_personal_end_sleep;
    private SharedPreferences SP_setting;
    private SharedPreferences.Editor SP_edit;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences_settings);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref_list_location_ali_type = findPreference("pref_list_location_ali_type");
        pref_location_tencent_usegps = findPreference("pref_location_tencent_usegps");
        pref_location_tencent_indoor = findPreference("pref_location_tencent_indoor");
        pref_list_personal_step_target = findPreference("pref_list_personal_step_target");
        pref_seek_personal_urgent_day = findPreference("pref_seek_personal_urgent_day");
        pref_personal_start_sleep = findPreference("pref_personal_start_sleep");
        pref_personal_end_sleep = findPreference("pref_personal_end_sleep");

        setting_context = getActivity();

        SP_setting = getActivity().getSharedPreferences("setting",App.getContext().MODE_PRIVATE);
        SP_edit = SP_setting.edit();
        loadsetting();
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference.getKey() != null) {
            switch (preference.getKey()) {
                case "pref_location_switch":
//                    Snackbar.make(getListView(), "点击", Snackbar.LENGTH_SHORT).show();
                    break;
                case "pref_location_background_switch":
                    break;
                case "pref_list_location_type":
                    break;
                case "pref_list_location_time":
                    break;
            }
            return true;
        }
        return super.onPreferenceTreeClick(preference);
    }
    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey() != null) {
            switch(preference.getKey()){
                case "pref_personal_start_sleep":
                    // 睡眠时间设置
                    DateFormat dateFormat_start = new DateFormat();
                    Calendar start_sleep = Calendar.getInstance();
                    start_sleep.setTimeInMillis(SP_setting.getLong("pref_personal_start_sleep", Calendar.getInstance().getTime().getTime()));
                    Log.d("SettingsFragement","设置开始睡眠时刻：" + dateFormat_start.GetDetailDescription(start_sleep));
                    TimePickerDialog start_timePickerDialog = new TimePickerDialog(setting_context,(view1, hour, minute) -> {
                        start_sleep.set(Calendar.HOUR_OF_DAY,hour);
                        start_sleep.set(Calendar.MINUTE,minute);
                        SP_edit.putLong("pref_personal_start_sleep",start_sleep.getTime().getTime());
                        SP_edit.commit();
                        pref_personal_start_sleep.setSummary(dateFormat_start.GetHourAndMinuteDetailDescription(start_sleep));
                    }, start_sleep.get(Calendar.HOUR_OF_DAY), start_sleep.get(Calendar.MINUTE),true);
                    start_timePickerDialog.show();
                    break;
                case "pref_personal_end_sleep":
                    DateFormat dateFormat_end = new DateFormat();
                    Calendar end_sleep = Calendar.getInstance();
                    end_sleep.setTimeInMillis(SP_setting.getLong("pref_personal_end_sleep",end_sleep.getTime().getTime()));
                    TimePickerDialog end_timePickerDialog = new TimePickerDialog(setting_context,(view1, hour, minute) -> {
                        end_sleep.set(Calendar.HOUR_OF_DAY,hour);
                        end_sleep.set(Calendar.MINUTE,minute);
                        SP_edit.putLong("pref_personal_end_sleep",end_sleep.getTime().getTime());
                        SP_edit.commit();
                        pref_personal_end_sleep.setSummary(dateFormat_end.GetHourAndMinuteDetailDescription(end_sleep));
                    }, end_sleep.get(Calendar.HOUR_OF_DAY), end_sleep.get(Calendar.MINUTE),true);
                    end_timePickerDialog.show();
                    break;
            }
        }
        return false;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getKey()){
            case "pref_location_switch":
//                Snackbar.make(getListView(), newValue.toString(), Snackbar.LENGTH_SHORT).show();
                SP_edit.putBoolean("pref_location_switch",(Boolean) newValue);
                break;
//            case "pref_personal_start_sleep":
//                // 睡眠时间设置
//                DateFormat dateFormat_start = new DateFormat();
//                Calendar start_sleep = Calendar.getInstance();
//                start_sleep.setTimeInMillis(SP_setting.getLong("pref_personal_start_sleep", start_sleep.getTime().getTime()));
//                break;
//            case "pref_personal_end_sleep":
//                DateFormat dateFormat_end = new DateFormat();
//                Calendar end_sleep = Calendar.getInstance();
//                end_sleep.setTimeInMillis(SP_setting.getLong("pref_personal_end_sleep",end_sleep.getTime().getTime()));
//                break;
            case "pref_list_personal_step_target":
                int trunc = Integer.parseInt(newValue.toString()) / 100; //百位截断
                pref_list_personal_step_target.setSummary("当前运动目标：每日" + trunc*100 + "步");
                pref_list_personal_step_target.setValue(trunc*100);
                SP_edit.putInt("pref_personal_step_target",trunc*100);
                SP_edit.commit();
                break;
            case "pref_seek_personal_urgent_day":
                pref_seek_personal_urgent_day.setSummary("剩余" + Integer.parseInt(newValue.toString()) + "天结束判为紧急事件，用于日程分类。");
                pref_seek_personal_urgent_day.setValue(Integer.parseInt(newValue.toString()));
                SP_edit.putInt("pref_seek_personal_urgent_day",Integer.parseInt(newValue.toString()));
                SP_edit.commit();
                break;
            case "pref_list_location_query_precision":
                SP_edit.putString("settings_location_query_precision",newValue.toString());
                SP_edit.commit();
                Log.i("SettingsFragement","切换精度限制为："+newValue.toString());
                break;
            case "pref_list_location_server":
                if(SP_setting.getString("settings_location_server","Amap").equals(newValue.toString())){
                    Log.i("SettingsFragement","设置未改变");
                }else{
                    SP_edit.putString("settings_location_server",newValue.toString());
                    Log.i("SettingsFragement","切换位置服务提供者为："+newValue.toString());
                    if(newValue.toString().equals("Amap")){
                        pref_list_location_ali_type.setEnabled(true);
                        pref_location_tencent_usegps.setEnabled(false);
                        pref_location_tencent_indoor.setEnabled(false);
                    }else {
                        pref_list_location_ali_type.setEnabled(false);
                        pref_location_tencent_usegps.setEnabled(true);
                        pref_location_tencent_indoor.setEnabled(true);
                    }
                    SP_edit.commit();
                    restartService();
                    Log.i("SettingsFragement","数据获取服务已重启！");
                }
                break;
            case "pref_location_background_switch":
//                Snackbar.make(getListView(), newValue.toString(), Snackbar.LENGTH_SHORT).show();
                if(SP_setting.getBoolean("pref_location_background_switch",false)==Boolean.valueOf(newValue.toString())){
                    Log.i("SettingsFragement","设置未改变");
                }else{
                    Log.i("SettingsFragement","后台轨迹记录设置项改变为："+newValue.toString());
                    SP_edit.putBoolean("pref_location_background_switch",Boolean.valueOf(newValue.toString()));
//                Log.i("SettingsFragement",newValue.toString());
                    SP_edit.commit();
                    restartService();
                }
//                Log.i("SettingsFragement",String.valueOf(SP_setting.getBoolean("pref_location_background_switch",false)));
//                if((Boolean) newValue){
//                    Intent startIntent = new Intent(getActivity(), DataCaptureService.class);
//                    getActivity().startService(startIntent);
//                    //利用PendingIntent的getService接口，得到封装后的PendingIntent
//                    //第二个参数，是区分PendingIntent来源的请求代码
//                    //第四个参数，是决定如何创建PendingIntent的标志位
//                    PendingIntent pendingIntent = PendingIntent.getService(getActivity(), 0, startIntent, 0);
//
//                    // Cactus 应用保活
//                    Cactus.getInstance()
//                            .hideNotification(true)
//                            .isDebug(true)
//                            .setPendingIntent(pendingIntent)
//                            .addCallback(new CactusCallback() {
//                                @Override
//                                public void doWork(int i) {
//                                    Log.i("SettingsFragement","Cactus 应用保活已启动");
//                                }
//                                @Override
//                                public void onStop() {
//                                    Log.i("SettingsFragement","Cactus 应用保活已停止");
//                                }
//                            })
//                            .register(getActivity());
//
//                }else{
//                    Intent stopIntent = new Intent(getActivity(), DataCaptureService.class);
//                    getActivity().stopService(stopIntent);
//                    Cactus.getInstance().unregister(getActivity());
//                    Log.i("SettingsFragement","停止 Cactus 应用保活");
//                }
                break;
            case "pref_location_usegps":
                SP_edit.putBoolean("pref_location_usegps",(Boolean) newValue);
                SP_edit.commit();
                break;
            case "pref_location_indoor":
                SP_edit.putBoolean("pref_location_indoor",(Boolean) newValue);
                SP_edit.commit();
                break;
            case "pref_list_location_type":
//                Snackbar.make(getListView(), newValue.toString(), Snackbar.LENGTH_SHORT).show();
//                preference.setSummary(newValue.toString());
                if(SP_setting.getString("pref_list_location_type","Battery_Saving").equals(newValue.toString())){
                    Log.i("SettingsFragement","设置未改变");
                }else{
                    SP_edit.putString("pref_list_location_type",newValue.toString());
                    SP_edit.commit();
                    restartService();
                }
                break;
            case "pref_list_location_time":
//                Snackbar.make(getListView(), newValue.toString(), Snackbar.LENGTH_SHORT).show();
//                preference.setSummary(newValue.toString());
                if(SP_setting.getString("pref_list_location_time","4000").equals(newValue.toString())){
                    Log.i("SettingsFragement","设置未改变");
                }else{
                    SP_edit.putString("pref_list_location_time",newValue.toString());
                    restartService();
                    SP_edit.commit();
                }
                break;
            case "pref_list_system_server":
                SP_edit.putString("pref_list_system_server",newValue.toString());
                SP_edit.commit();

//                if(newValue.toString().equals("cn-north")){
//                    new AlertDialog.Builder(getContext())
//                            .setMessage("2019年10月1日后将停止“中国 - 华北”节点，请及时切换国际节点！ \n 注：若过期未切换会导致程序无法启动，此时请重新安装最新版软件解决！")
//                            .setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//
//                                }
//                            })
//                            .show();
//                }

                String oldvalue = PreferenceManager.getDefaultSharedPreferences(App.getContext()).getString("pref_list_system_server", "cn-north");
                Log.i("SettingFragement","Server Old Value:" + oldvalue);
                Log.i("SettingFragement","Server new Value:" + newValue.toString());
                if(!oldvalue.equals(newValue.toString())){
                    new AlertDialog.Builder(getContext())
                            .setMessage("重启程序后修改生效，是否重启?")
                            .setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    System.exit(0);
                                }
                            })
                            .show();
                }
        }
        // 修改状态时修改summary显示
        String stringValue = newValue.toString();
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);
            preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
        } else {
//            preference.setSummary(stringValue);
        }
        return true;
    }

    private void loadsetting(){
        //获取一个 SharedPreferences对象
        //第一个参数：指定文件的名字，只会续写不会覆盖
        //第二个参数：MODE_PRIVATE只有当前应用程序可以续写
        SharedPreferences SP_setting = App.getContext().getSharedPreferences("setting",App.getContext().MODE_MULTI_PROCESS);
//        Boolean FirsrUse = SP_setting.getBoolean("PlanAssistant_FirstUse",false); // 软件是否首次使用
//
//        SharedPreferences.Editor SP_editor = SP_setting.edit();
//
//        Log.i("MainActivity","本次是第 " + SP_setting.getInt("PlanAssistant_Frequency",1) + " 次启动.");
//
//        if(FirsrUse){
//            // 首次使用，初始化设置
//            //向其中添加数据，是什么数据类型就put什么，前面是键，后面是数据
//
//            SP_editor.putBoolean("PlanAssistant_FirstUse",false); // 清除首次使用标记
//            SP_editor.putInt("PlanAssistant_Frequency",1); // 设置软件使用次数为1
//
//            //定位相关设置
//            SP_editor.putString("pref_list_location_type","Battery_Saving"); // 定位模式
//            SP_editor.putBoolean("pref_location_switch",true); // 定位服务
//            SP_editor.putBoolean("pref_location_background_switch",false); // 后台定位服务
//            SP_editor.putString("pref_list_location_type","Battery_Saving"); // 定位模式
//            SP_editor.putInt("pref_list_location_time",4000); // 定位间隔
//
//            // 调用apply方法将添加的数据提交，从而完成存储的动作
//            SP_editor.apply();
//            Log.i("MainActivity","首次启动，初始化设置项。");
//        }else{
//            // 非第一次使用
//            int frequency = SP_setting.getInt("PlanAssistant_Frequency",1);
//            Log.i("MainActivity","非首次启动，本次是第 " + frequency + " 次启动.");
//
//            findPreference("pref_location_switch").setDefaultValue(SP_setting.getBoolean("pref_location_switch",true));
//            findPreference("pref_location_background_switch").setDefaultValue(SP_setting.getBoolean("pref_location_background_switch",false));
//            findPreference("pref_list_location_type").setDefaultValue(SP_setting.getString("pref_list_location_type","Battery_Saving"));
//            findPreference("pref_list_location_time").setDefaultValue(SP_setting.getInt("pref_list_location_time",4000));
//        }
//        SP_editor.commit();// 提交

//        findPreference("pref_location_switch").setDefaultValue(SP_setting.getBoolean("pref_location_switch",true));


        // 多位置服务提供者专项设置
        if(SP_setting.getString("pref_list_location_server","Amap").equals("Amap")){
            pref_list_location_ali_type.setEnabled(true);
            pref_location_tencent_usegps.setEnabled(false);
            pref_location_tencent_indoor.setEnabled(false);
        }else {
            pref_list_location_ali_type.setEnabled(false);
            pref_location_tencent_usegps.setEnabled(true);
            pref_location_tencent_indoor.setEnabled(true);
        }

        //设置默认值
        findPreference("pref_list_location_server").setDefaultValue(SP_setting.getString("pref_list_location_server","Amap"));
        findPreference("pref_list_location_query_precision").setDefaultValue(SP_setting.getString("settings_location_query_precision","300"));
        findPreference("pref_location_background_switch").setDefaultValue(SP_setting.getBoolean("pref_location_background_switch",false));
        findPreference("pref_list_location_ali_type").setDefaultValue(SP_setting.getString("pref_list_location_ali_type","Battery_Saving"));
        findPreference("pref_list_system_server").setDefaultValue(SP_setting.getString("pref_list_system_server","international"));
        findPreference("pref_list_location_time").setDefaultValue(SP_setting.getString("pref_list_location_time","4000"));
        findPreference("pref_list_personal_step_target").setDefaultValue(SP_setting.getInt("pref_personal_step_target",4000));
        findPreference("pref_seek_personal_urgent_day").setDefaultValue(SP_setting.getInt("pref_seek_personal_urgent_day",10));

        // 绑定点击事件
        findPreference("pref_personal_start_sleep").setOnPreferenceClickListener(this);
        findPreference("pref_personal_end_sleep").setOnPreferenceClickListener(this);

        // 绑定改变事件
//        findPreference("pref_location_switch").setOnPreferenceChangeListener(this);
        findPreference("pref_personal_start_sleep").setOnPreferenceChangeListener(this);
        findPreference("pref_personal_end_sleep").setOnPreferenceChangeListener(this);
        findPreference("pref_list_location_server").setOnPreferenceChangeListener(this);
        findPreference("pref_list_location_query_precision").setOnPreferenceChangeListener(this);
        findPreference("pref_location_background_switch").setOnPreferenceChangeListener(this);
        findPreference("pref_list_location_ali_type").setOnPreferenceChangeListener(this);
        findPreference("pref_list_location_time").setOnPreferenceChangeListener(this);
        findPreference("pref_location_tencent_indoor").setOnPreferenceChangeListener(this);
        findPreference("pref_location_tencent_usegps").setOnPreferenceChangeListener(this);
        findPreference("pref_list_personal_step_target").setOnPreferenceChangeListener(this);
        findPreference("pref_list_system_server").setOnPreferenceChangeListener(this);
        findPreference("pref_seek_personal_urgent_day").setOnPreferenceChangeListener(this);

        //设置描述，根据默认值设置描述
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        onPreferenceChange(findPreference("pref_list_location_server"), preferences.getString("pref_list_location_server", ""));
        onPreferenceChange(findPreference("pref_list_location_query_precision"), preferences.getString("pref_list_location_query_precision", ""));
        onPreferenceChange(findPreference("pref_list_location_ali_type"), preferences.getString("pref_list_location_ali_type", "Battery_Saving"));
        onPreferenceChange(findPreference("pref_list_location_time"), preferences.getString("pref_list_location_time", ""));
        onPreferenceChange(findPreference("pref_list_system_server"), preferences.getString("pref_list_system_server", "cn-north"));
        onPreferenceChange(findPreference("pref_list_personal_step_target"), (preferences.getInt("pref_list_personal_step_target",4000)));
        onPreferenceChange(findPreference("pref_seek_personal_urgent_day"), preferences.getInt("pref_seek_personal_urgent_day",10));

        // 睡眠时间设置
        DateFormat dateFormat = new DateFormat();
        Calendar start_sleep = Calendar.getInstance();
        Calendar end_sleep = Calendar.getInstance();
        start_sleep.set(2020,5,1,23,0);
        end_sleep.set(2020,5,2,7,0);

        start_sleep.setTimeInMillis(SP_setting.getLong("pref_personal_start_sleep", start_sleep.getTime().getTime()));
        end_sleep.setTimeInMillis(SP_setting.getLong("pref_personal_end_sleep",end_sleep.getTime().getTime()));

        // 设置默认描述
        findPreference("pref_personal_start_sleep").setSummary(dateFormat.GetHourAndMinuteDetailDescription(start_sleep));
        findPreference("pref_personal_end_sleep").setSummary(dateFormat.GetHourAndMinuteDetailDescription(end_sleep));
    }

    public void StartService(){
        Log.i("MainActivity","启动后台服务。");
        Intent startIntent = new Intent(getActivity(), DataCaptureService.class);
        getActivity().startService(startIntent);

        //利用PendingIntent的getService接口，得到封装后的PendingIntent
        //第二个参数，是区分PendingIntent来源的请求代码
        //第四个参数，是决定如何创建PendingIntent的标志位
        PendingIntent pendingIntent = PendingIntent.getService(getActivity(), 0, startIntent, 0);

        // Cactus 应用保活
        Cactus.getInstance()
                .setLargeIcon(R.mipmap.ic_launcher)
                .setSmallIcon(R.mipmap.ic_launcher)
                .hideNotification(false)
                .isDebug(true)
                .setPendingIntent(pendingIntent)
                .addCallback(new CactusCallback() {
                    @Override
                    public void doWork(int i) {
                        Log.i("SettingsFragement","Cactus 应用保活已启动");
                    }

                    @Override
                    public void onStop() {
                        Log.i("SettingsFragement","Cactus 应用保活已停止");
                    }
                })
                .register(getActivity());
    }
    public void stopService(){
        Intent stopIntent = new Intent(getActivity(), DataCaptureService.class);
        getActivity().stopService(stopIntent);
        Cactus.getInstance().unregister(getActivity());
    }
    public void restartService(){
//        Intent restartIntent = new Intent(getActivity(), DataCaptureService.class);
//        getActivity().startService(restartIntent);
//        getActivity().stopService(restartIntent);
//        Cactus.getInstance().restart(getActivity());
        stopService();
        StartService();
    }
}
