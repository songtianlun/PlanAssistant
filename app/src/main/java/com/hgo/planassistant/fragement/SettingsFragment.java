package com.hgo.planassistant.fragement;

import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;


import com.google.android.material.snackbar.Snackbar;
import com.gyf.cactus.Cactus;
import com.gyf.cactus.callback.CactusCallback;
import com.hgo.planassistant.App;
import com.hgo.planassistant.R;
import com.hgo.planassistant.activity.PlanCounterDetailActivity;
import com.hgo.planassistant.service.TencentLocationService;

import static android.content.Context.MODE_PRIVATE;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    private Context setting_context;
    private ListPreference pref_list_location_ali_type;
    private SwitchPreference pref_location_tencent_usegps;
    private SwitchPreference pref_location_tencent_indoor;

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
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        SharedPreferences SP_setting = getActivity().getSharedPreferences("setting",App.getContext().MODE_PRIVATE);
        SharedPreferences.Editor SP_edit = SP_setting.edit();
        switch (preference.getKey()){
            case "pref_location_switch":
//                Snackbar.make(getListView(), newValue.toString(), Snackbar.LENGTH_SHORT).show();
                SP_edit.putBoolean("pref_location_switch",(Boolean) newValue);
                break;
            case "pref_list_location_query_precision":
                SP_edit.putString("settings_location_query_precision",newValue.toString());
                SP_edit.commit();
                Log.i("SettingsFragement","切换精度限制为："+newValue.toString());
                break;
            case "pref_list_location_server":
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
                break;
            case "pref_location_background_switch":
//                Snackbar.make(getListView(), newValue.toString(), Snackbar.LENGTH_SHORT).show();
                SP_edit.putBoolean("pref_location_background_switch",Boolean.valueOf(newValue.toString()));
//                Log.i("SettingsFragement",newValue.toString());
                SP_edit.commit();
//                Log.i("SettingsFragement",String.valueOf(SP_setting.getBoolean("pref_location_background_switch",false)));
                if((Boolean) newValue){
                    Intent startIntent = new Intent(getActivity(), TencentLocationService.class);
                    getActivity().startService(startIntent);


                    //利用PendingIntent的getService接口，得到封装后的PendingIntent
                    //第二个参数，是区分PendingIntent来源的请求代码
                    //第四个参数，是决定如何创建PendingIntent的标志位
                    PendingIntent pendingIntent = PendingIntent.getService(getActivity(), 0, startIntent, 0);

                    // Cactus 应用保活
                    Cactus.getInstance()
                            .hideNotification(true)
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

                }else{
                    Intent stopIntent = new Intent(getActivity(), TencentLocationService.class);
                    getActivity().stopService(stopIntent);
                    Cactus.getInstance().unregister(getActivity());
                    Log.i("SettingsFragement","停止 Cactus 应用保活");
                }
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
                SP_edit.putString("pref_list_location_type",newValue.toString());
                SP_edit.commit();
                break;
            case "pref_list_location_time":
//                Snackbar.make(getListView(), newValue.toString(), Snackbar.LENGTH_SHORT).show();
//                preference.setSummary(newValue.toString());
                SP_edit.putString("pref_list_location_time",newValue.toString());
                SP_edit.commit();
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

        // 绑定改变事件
//        findPreference("pref_location_switch").setOnPreferenceChangeListener(this);
        findPreference("pref_list_location_server").setOnPreferenceChangeListener(this);
        findPreference("pref_list_location_query_precision").setOnPreferenceChangeListener(this);
        findPreference("pref_location_background_switch").setOnPreferenceChangeListener(this);
        findPreference("pref_list_location_ali_type").setOnPreferenceChangeListener(this);
        findPreference("pref_list_location_time").setOnPreferenceChangeListener(this);
        findPreference("pref_location_tencent_indoor").setOnPreferenceChangeListener(this);
        findPreference("pref_location_tencent_usegps").setOnPreferenceChangeListener(this);

        findPreference("pref_list_system_server").setOnPreferenceChangeListener(this);

        //设置描述，根据默认值设置描述
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        onPreferenceChange(findPreference("pref_list_location_server"), preferences.getString("pref_list_location_server", ""));
        onPreferenceChange(findPreference("pref_list_location_query_precision"), preferences.getString("pref_list_location_query_precision", ""));
        onPreferenceChange(findPreference("pref_list_location_ali_type"), preferences.getString("pref_list_location_ali_type", "Battery_Saving"));
        onPreferenceChange(findPreference("pref_list_location_time"), preferences.getString("pref_list_location_time", ""));
        onPreferenceChange(findPreference("pref_list_system_server"), preferences.getString("pref_list_system_server", "cn-north"));

    }
}
