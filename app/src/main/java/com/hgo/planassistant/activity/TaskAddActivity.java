package com.hgo.planassistant.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.google.android.material.textfield.TextInputEditText;
import com.hgo.planassistant.App;
import com.hgo.planassistant.R;
import com.hgo.planassistant.util.CalendarReminderUtils;
import com.warkiz.widget.IndicatorSeekBar;

import java.util.Calendar;

public class TaskAddActivity extends BaseActivity implements View.OnClickListener{

    private Context mContext;
    private Calendar task_start_time;
    private Calendar task_end_time;

    private AppCompatImageButton ib_marker;
    private TextView TV_start_time;
    private TextView TV_start_date;
    private TextView TV_end_time;
    private TextView TV_end_date;
    private TextInputEditText edit_name;
    private TextInputEditText edit_location;
    private IndicatorSeekBar seekBar_importance;
    private TextView TV_description;
    private AppCompatSpinner spinner_remind;
    private Button btn_ok;
    private Button btn_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_add);

        Toolbar toolbar = findViewById(R.id.toolbar_task_add);
        setToolbar(toolbar);

        mContext = this;
        task_start_time = Calendar.getInstance();
        task_end_time = Calendar.getInstance();
        task_end_time.add(Calendar.HOUR_OF_DAY,1);

        initView();

    }

    private void initView(){
        btn_ok = findViewById(R.id.btn_activity_task_add_ok);
        btn_cancel = findViewById(R.id.btn_activity_task_add_cancel);
        edit_name = findViewById(R.id.activity_task_add_name);
        edit_location = findViewById(R.id.activity_task_add_location);
        seekBar_importance = findViewById(R.id.activity_task_add_importance);
        TV_description = findViewById(R.id.activity_task_add_description);
        TV_start_time = findViewById(R.id.activity_task_add_start_time);
        TV_start_date = findViewById(R.id.activity_task_add_start_time_date);
        TV_end_time = findViewById(R.id.activity_task_add_end_time);
        TV_end_date = findViewById(R.id.activity_task_add_end_time_date);
        spinner_remind = findViewById(R.id.activity_task_add_remind);
//            spinner_cycle = dialogView.findViewById(R.id.activity_task_add_cycle);
        ib_marker = findViewById(R.id.activity_task_add_location_marker);

        btn_ok.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        TV_start_time.setOnClickListener(this);
        TV_start_date.setOnClickListener(this);
        TV_end_time.setOnClickListener(this);
        TV_end_date.setOnClickListener(this);
//            clean_start.setOnClickListener(this);
//            clean_end.setOnClickListener(this);
        ib_marker.setOnClickListener(this);

        TV_start_date.setText(task_start_time.get(Calendar.YEAR)+"年"+(task_start_time.get(Calendar.MONTH)+1)+"月"+task_start_time.get(Calendar.DATE)+"日");
        TV_start_time.setText(task_start_time.get(Calendar.HOUR_OF_DAY)+" 时 "+task_start_time.get(Calendar.MINUTE) +"分");
        TV_end_date.setText(task_end_time.get(Calendar.YEAR)+"年"+(task_end_time.get(Calendar.MONTH)+1)+"月"+task_end_time.get(Calendar.DATE)+"日");
        TV_end_time.setText(task_end_time.get(Calendar.HOUR_OF_DAY)+" 时 "+task_end_time.get(Calendar.MINUTE) +"分");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_activity_task_add_ok:
                if(edit_name.length()==0){
                    Toast.makeText(App.getContext(), "任务名称不能为空！", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(task_start_time.getTime().getTime()>task_end_time.getTime().getTime()){
                        Toast.makeText(App.getContext(), "结束时间不能小于开始时间，请修改，您的任务未保存。", Toast.LENGTH_SHORT).show();
                    }else{
                        finish();

                        AVObject new_task = new AVObject("Task");
                        new_task.put("UserId", AVUser.getCurrentUser().getObjectId());// 设置用户ID
                        new_task.put("task_name",edit_name.getText());
                        new_task.put("task_importance",seekBar_importance.getProgress());
                        if(task_start_time!=null)
                            new_task.put("start_time",task_start_time.getTime());
                        if(task_end_time!=null)
                            new_task.put("end_time",task_end_time.getTime());
                        if(TV_description.length()!=0)
                            new_task.put("task_description",TV_description.getText());
                        if(edit_location.length()!=0)
                            new_task.put("task_location",edit_location.getText());
                        new_task.put("task_remind",spinner_remind.getSelectedItemId());
//                        new_task.put("task_cycle",spinner_cycle.getSelectedItemId());
                        new_task.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    //成功
                                    Toast.makeText(App.getContext(), "保存成功！", Toast.LENGTH_SHORT).show();

                                    CalendarReminderUtils calendarReminderUtils = new CalendarReminderUtils();
                                    switch ((int)spinner_remind.getSelectedItemId()){
                                        case 0:
                                            calendarReminderUtils.addCalendarEvent(mContext,edit_name.getText().toString(),TV_description.getText().toString(),edit_location.getText().toString(),task_start_time.getTime().getTime(),task_end_time.getTime().getTime());
                                            break;
                                        case 1:
                                            calendarReminderUtils.addCalendarEvent(mContext,edit_name.getText().toString(),TV_description.getText().toString(),edit_location.getText().toString(),task_start_time.getTime().getTime(),task_end_time.getTime().getTime(),0);
                                            break;
                                        case 2:
                                            calendarReminderUtils.addCalendarEvent(mContext,edit_name.getText().toString(),TV_description.getText().toString(),edit_location.getText().toString(),task_start_time.getTime().getTime(),task_end_time.getTime().getTime(),15);
                                            break;
                                        case 3:
                                            calendarReminderUtils.addCalendarEvent(mContext,edit_name.getText().toString(),TV_description.getText().toString(),edit_location.getText().toString(),task_start_time.getTime().getTime(),task_end_time.getTime().getTime(),60);
                                            break;
                                        case 4:
                                            calendarReminderUtils.addCalendarEvent(mContext,edit_name.getText().toString(),TV_description.getText().toString(),edit_location.getText().toString(),task_start_time.getTime().getTime(),task_end_time.getTime().getTime(), 24*60);
                                            break;
                                        default:
                                            break;

                                    }
//                                adapter.addItem(linearLayoutManager.findFirstVisibleItemPosition() + 1, insertData);
                                } else {
                                    // 失败的原因可能有多种，常见的是用户名已经存在。
//                        showProgress(false);
                                    Toast.makeText(App.getContext(), "保存失败，原因：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
                break;
            case R.id.btn_activity_task_add_cancel:
                finish();
                break;
//            case R.id.activity_task_add_start_time_clean:
//                TV_start_date.setText("null");
//                TV_start_time.setText("null");
//                task_start_time = null;
//                break;
//            case R.id.activity_task_add_end_time_clean:
//                TV_end_date.setText("null");
//                TV_end_time.setText("null");
//                task_end_time = null;
//                break;
            case R.id.activity_task_add_start_time_date:
                if(task_start_time!=null){
                    DatePickerDialog start_datePickerDialog = new DatePickerDialog(this, (view1, year, monthOfYear, dayOfMonth) -> {
                        task_start_time.set(Calendar.YEAR,year);
                        task_start_time.set(Calendar.MONTH,monthOfYear);
                        task_start_time.set(Calendar.DATE,dayOfMonth);

                        task_end_time.setTime(task_start_time.getTime());
                        task_end_time.add(Calendar.DAY_OF_MONTH,1);
                        TV_end_date.setText(task_end_time.get(Calendar.YEAR)+"年"+(task_end_time.get(Calendar.MONTH)+1)+"月"+task_end_time.get(Calendar.DAY_OF_MONTH)+"日");

                        TV_start_date.setText(year+"年"+(monthOfYear+1)+"月"+dayOfMonth+"日");

                    }, task_start_time.get(Calendar.YEAR), task_start_time.get(Calendar.MONTH), task_start_time.get(Calendar.DAY_OF_MONTH));
                    start_datePickerDialog.show();
                }else{
                    Calendar now = Calendar.getInstance();
                    DatePickerDialog start_datePickerDialog = new DatePickerDialog(this, (view1, year, monthOfYear, dayOfMonth) -> {
                        task_start_time.set(Calendar.YEAR,year);
                        task_start_time.set(Calendar.MONTH,monthOfYear);
                        task_start_time.set(Calendar.DATE,dayOfMonth);

                        task_end_time.setTime(task_start_time.getTime());
                        task_end_time.add(Calendar.DAY_OF_MONTH,1);
                        TV_end_date.setText(task_end_time.get(Calendar.YEAR)+"年"+(task_end_time.get(Calendar.MONTH)+1)+"月"+task_end_time.get(Calendar.DAY_OF_MONTH)+"日");

                        TV_start_date.setText(year+"年"+(monthOfYear+1)+"月"+dayOfMonth+"日");
                    }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                    start_datePickerDialog.show();
                }
                break;
            case R.id.activity_task_add_start_time:
                if(task_start_time!=null){
                    TimePickerDialog start_timePickerDialog = new TimePickerDialog(this,(view1, hour, minute) -> {
                        task_start_time.set(Calendar.HOUR_OF_DAY,hour);
                        task_start_time.set(Calendar.MINUTE,minute);
                        TV_start_time.setText(hour+" 时 "+minute +"分");
                    }, task_start_time.get(Calendar.HOUR_OF_DAY), task_start_time.get(Calendar.MINUTE),true);
                    start_timePickerDialog.show();
                }else{
                    Calendar now = Calendar.getInstance();
                    TimePickerDialog start_timePickerDialog = new TimePickerDialog(this,(view1, hour, minute) -> {
                        task_start_time.set(Calendar.HOUR_OF_DAY,hour);
                        task_start_time.set(Calendar.MINUTE,minute);
                        TV_start_time.setText(hour+" 时 "+minute +"分");
                    }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE),true);
                    start_timePickerDialog.show();
                }
                break;
            case R.id.activity_task_add_end_time_date:
                if(task_end_time!=null){

                    DatePickerDialog start_datePickerDialog = new DatePickerDialog(this, (view1, year, monthOfYear, dayOfMonth) -> {
                        task_end_time.set(Calendar.YEAR,year);
                        task_end_time.set(Calendar.MONTH,monthOfYear);
                        task_end_time.set(Calendar.DATE,dayOfMonth);
                        TV_end_date.setText(year+"年"+(monthOfYear+1)+"月"+dayOfMonth+"日");
//                        if(year<task_start_time.get(Calendar.YEAR) || monthOfYear<task_start_time.get(Calendar.MONTH) || dayOfMonth<task_start_time.get(Calendar.DAY_OF_MONTH)){
//                            Toast.makeText(App.getContext(), "结束时间不能小于开始时间！您的修改不会保存。", Toast.LENGTH_SHORT).show();
//                        }else{
//                            task_end_time.set(Calendar.YEAR,year);
//                            task_end_time.set(Calendar.MONTH,monthOfYear);
//                            task_end_time.set(Calendar.DATE,dayOfMonth);
//                            TV_end_date.setText(year+"年"+(monthOfYear+1)+"月"+dayOfMonth+"日");
//                        }
                    }, task_end_time.get(Calendar.YEAR), task_end_time.get(Calendar.MONTH), task_end_time.get(Calendar.DAY_OF_MONTH));
                    start_datePickerDialog.show();
                }else{
                    Calendar now = Calendar.getInstance();
                    DatePickerDialog start_datePickerDialog = new DatePickerDialog(this, (view1, year, monthOfYear, dayOfMonth) -> {
                        task_end_time.set(Calendar.YEAR,year);
                        task_end_time.set(Calendar.MONTH,monthOfYear);
                        task_end_time.set(Calendar.DATE,dayOfMonth);
                        TV_end_date.setText(year+"年"+(monthOfYear+1)+"月"+dayOfMonth+"日");
                    }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                    start_datePickerDialog.show();
                }
                break;
            case R.id.activity_task_add_end_time:
                if(task_end_time!=null){
                    TimePickerDialog start_timePickerDialog = new TimePickerDialog(this,(view1, hour, minute) -> {
                        task_end_time.set(Calendar.HOUR_OF_DAY,hour);
                        task_end_time.set(Calendar.MINUTE,minute);
                        TV_end_time.setText(hour+" 时 "+minute +"分");
                    }, task_end_time.get(Calendar.HOUR_OF_DAY), task_end_time.get(Calendar.MINUTE),true);
                    start_timePickerDialog.show();
                }else{
                    Calendar now = Calendar.getInstance();
                    TimePickerDialog start_timePickerDialog = new TimePickerDialog(this,(view1, hour, minute) -> {
                        task_end_time.set(Calendar.HOUR_OF_DAY,hour);
                        task_end_time.set(Calendar.MINUTE,minute);
                        TV_end_time.setText(hour+" 时 "+minute +"分");
                    }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE),true);
                    start_timePickerDialog.show();
                }
                break;
            case R.id.activity_task_add_location_marker:
                Log.i("TaskActivity","选择位置");
                View dialogView = getLayoutInflater().inflate(R.layout.activity_task_add, null);
                break;
            default:
                break;
        }
    }
}
