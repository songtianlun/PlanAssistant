package com.hgo.planassistant.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import com.avos.avoscloud.AVObject;
import com.hgo.planassistant.R;
import com.hgo.planassistant.datamodel.AVObjectsParcelable;

import java.util.ArrayList;
import java.util.List;

public class SchedulePlanningActivity extends BaseActivity {

    private List<AVObject> task_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_planning);

        Toolbar toolbar = findViewById(R.id.toolbar_toolbar_activity_schedule_planning);
        setToolbar(toolbar);

        LoadData();

    }

    private void LoadData(){
        task_list = new ArrayList<>();
        AVObjectsParcelable avObjectsParcelable = (AVObjectsParcelable)getIntent().getParcelableExtra("track_list");
        if(avObjectsParcelable.getSend_list()!=null){
            task_list.addAll(avObjectsParcelable.getSend_list());
        }
    }
}
