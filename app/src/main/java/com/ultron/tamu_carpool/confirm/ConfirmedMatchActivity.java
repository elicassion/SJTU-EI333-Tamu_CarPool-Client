package com.ultron.tamu_carpool.confirm;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ultron.tamu_carpool.R;
import com.ultron.tamu_carpool.StatusBarCompat;
import com.ultron.tamu_carpool.usr.User;
import com.ultron.tamu_carpool.util.InteractUtil;

import org.json.JSONObject;

public class ConfirmedMatchActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView textView;
    private Button button;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, 0xFF009688);
        setContentView(R.layout.activity_confirmed_match);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_confirmed_match_toolbar);
        toolbar.setTitle("拼车成功！");
        //setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mContext = this.getApplicationContext();
        initView();
    }

    public void initView(){
        Intent faIntent = getIntent();
        textView = (TextView)findViewById(R.id.confirmed_text);
        button = (Button) findViewById(R.id.know_confirm);
        button.setOnClickListener(this);

        try {
            String targetInfo = faIntent.getStringExtra("target");
            String selfQueryInfo = faIntent.getStringExtra("my_query");
            JSONObject jTarget = new JSONObject(targetInfo);
            JSONObject jSelfQuery = new JSONObject(selfQueryInfo);
            User target = new User(jTarget.getString("id"), jTarget.getInt("user_type"));
            String text = "";
            String myStartName = jSelfQuery.getString("start_name");
            String myEndName = jSelfQuery.getString("dest_name");
            String myTime = jSelfQuery.getString("time");
            text = text + "您的" + myTime + "从" + myStartName + "到" + myEndName + "\n";
            text = text + "向用户" + target.getID()+ "发出的匹配请求已被确认";
            textView.setText(text);
        }catch(Exception e){throw new RuntimeException(e);}
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.know_confirm:
                //InteractUtil interactUtil = new InteractUtil();
                //interactUtil.setKnowed(user, true);
                finish();
        }
    }
}
