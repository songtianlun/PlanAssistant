package com.hgo.planassistant.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.hgo.planassistant.R;
import com.hgo.planassistant.model.Log;
import com.hgo.planassistant.tools.DateFormat;

import org.litepal.LitePal;

import java.util.List;

public class AppLogActivity extends BaseActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_log);

        Toolbar toolbar = findViewById(R.id.toolbar_app_log);
        setToolbar(toolbar);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));

        textView = findViewById(R.id.textview_activity_app_log);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());//滑动

        List<Log> allAlbum = LitePal.findAll(Log.class);
        DateFormat dateFormat = new DateFormat();
        for (int i=allAlbum.size()-1;i>=0;i--){
            textView.append(dateFormat.GetDetailDescription(allAlbum.get(i).getTime())+"/"+allAlbum.get(i).getLabel()+"/"+allAlbum.get(i).getLog()+"\n");
            // 自动滑动
            int offset=textView.getLineCount()*textView.getLineHeight();
            if(offset>textView.getHeight()){
                textView.scrollTo(0,offset-textView.getHeight());
            }
        }
    }
}
