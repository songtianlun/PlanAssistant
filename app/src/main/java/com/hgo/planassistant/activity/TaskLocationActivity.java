package com.hgo.planassistant.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;

import com.hgo.planassistant.R;

public class TaskLocationActivity extends BaseActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_location);

        Toolbar toolbar = findViewById(R.id.toolbar_task_add);
        setToolbar(toolbar);

        mContext = this;
    }
}
