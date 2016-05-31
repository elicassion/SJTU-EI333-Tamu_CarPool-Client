package com.ultron.tamu_carpool.confirm;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ultron.tamu_carpool.R;
import com.ultron.tamu_carpool.usr.User;
import com.ultron.tamu_carpool.util.InteractUtil;

import org.json.JSONObject;

public class AskForConfirmActivity extends AppCompatActivity implements View.OnClickListener{
    private Button mYes;
    private Button mNo;
    private ConfirmTask mConfirmTask = null;
    private User user;
    private User target;
    private int selfQueryNumber;
    private int targetQueryNumber;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_for_confirm);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_ask_for_confirm_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initView();
    }

    public void initView(){
        textView = (TextView)findViewById(R.id.ask_for_confirm_text);
        mYes = (Button) findViewById(R.id.ask_for_confirm_btn_yes);
        mYes.setOnClickListener(this);
        mNo = (Button) findViewById(R.id.ask_for_confirm_btn_no);
        mNo.setOnClickListener(this);

        try {
            Intent faIntent = getIntent();
            user = (User) faIntent.getSerializableExtra("user");
            String targetInfo = faIntent.getStringExtra("target");
            String selfQueryInfo = faIntent.getStringExtra("my_query");
            JSONObject jTarget = new JSONObject(targetInfo);
            JSONObject jSelfQuery = new JSONObject(selfQueryInfo);
            target = new User(jTarget.getString("id"), jTarget.getInt("user_type"));
            targetQueryNumber = jTarget.getInt("query_number");
            selfQueryNumber = jSelfQuery.getInt("query_number");
            String text = "";
            String myStartName = jSelfQuery.getString("start_name");
            String myEndName = jSelfQuery.getString("dest_name");
            String myTime = jSelfQuery.getString("time");
            String tarStartName = jTarget.getString("start_name");
            String tarEndName = jTarget.getString("dest_name");
            String tarTime = jTarget.getString("time");
            double reputation = jTarget.getDouble("reputation");
            int finishedOrderNumber = jTarget.getInt("finished_order_number");
            text = text + "您的" + myTime + "从" + myStartName + "去往" + myEndName + "请求已匹配到，请确认：\n";
            text = text + "匹配人信息：\n";
            text = text + tarTime + "从" + tarStartName + "去往" + tarEndName + "\n";
            text = text + "联系电话: " + target.getID() + "\n";
            text = text + "评价: " + String.format("%.2f", reputation) + "\n";
            text = text + "已完成订单数: " + Integer.toString(finishedOrderNumber) + "\n";
            if (target.getUserType() == 2) {
                JSONObject carInfo = jTarget.getJSONObject("car_info");
                String numberPlate = carInfo.getString("number_plate");
                String carColor = carInfo.getString("car_color");
                String insurance = carInfo.getString("insurance");
                int maxPsg = carInfo.getInt("max_psg");
                int curPsg = carInfo.getInt("cur_psg");
                text = text + "车牌号: " + numberPlate + "\n";
                text = text + "车辆颜色: " + carColor + "\n";
                text = text + "保险情况: " + insurance + "\n";
                text = text + "最多可载: " + Integer.toString(maxPsg) + "人\n";
                text = text + "还可以载: " + Integer.toString(maxPsg - curPsg) + "人\n";
            }
            textView.setText(text);
        }catch(Exception e){throw new RuntimeException(e);}

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ask_for_confirm_btn_yes:
                mConfirmTask = new ConfirmTask();
                mConfirmTask.execute((Void) null);
                break;
            case R.id.ask_for_confirm_btn_no:
                finish();
                break;
        }
    }

    public class ConfirmTask extends AsyncTask<Void, Void, Boolean> {
        ConfirmTask() {

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            InteractUtil interactUtil = new InteractUtil();
            interactUtil.matchConfirm(user, target, selfQueryNumber, targetQueryNumber);
            return true;

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mConfirmTask = null;
            if (success) {
                finish();
            }
        }

        @Override
        protected void onCancelled() {
            mConfirmTask = null;
        }

    }
}
