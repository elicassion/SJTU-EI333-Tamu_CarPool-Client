package com.ultron.tamu_carpool.confirm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ultron.tamu_carpool.R;
import com.ultron.tamu_carpool.usr.User;
import com.ultron.tamu_carpool.util.InteractUtil;

import org.json.JSONObject;

public class ConfirmedMatchActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView textView;
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmed_match);
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
            JSONObject jSelfQuery = new JSONObject();
            User target = new User(jTarget.getString("id"), jTarget.getInt("user_type"));
            String text = "";
            String myStartName = "start";
            String myEndName = "end";
            String myTime = "八点";
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
