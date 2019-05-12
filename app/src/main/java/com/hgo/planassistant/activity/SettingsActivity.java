package com.hgo.planassistant.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.hgo.planassistant.R;
import com.hgo.planassistant.fragement.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, new SettingsFragment())
                .commit();
    }
}
