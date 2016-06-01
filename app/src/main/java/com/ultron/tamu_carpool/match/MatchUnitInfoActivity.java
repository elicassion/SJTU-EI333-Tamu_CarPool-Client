package com.ultron.tamu_carpool.match;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
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

import java.util.Date;

public class MatchUnitInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView textView;
    private Button matchConfirm;
    private String mDetail;
    private User user;
    private User target;
    private String mTime;
    private String mStartName;
    private String mDestName;
    private int mSelfQueryNumber;
    private int targetQueryNumber;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, 0xFF009688);
        setContentView(R.layout.activity_match_unit_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_order_unit_info_toolbar);
        toolbar.setTitle("详细信息");
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

    public void initView() {
        textView = (TextView)findViewById(R.id.match_unit_info_text);
        matchConfirm = (Button)findViewById(R.id.match_confirm);
        matchConfirm.setOnClickListener(this);
        Intent faIntent = getIntent();
        mDetail = faIntent.getStringExtra("detail");
        user = (User)faIntent.getSerializableExtra("user");
        mTime = faIntent.getStringExtra("time");
        mStartName = faIntent.getStringExtra("start");
        mDestName = faIntent.getStringExtra("destination");
        mSelfQueryNumber = faIntent.getIntExtra("self_query_number", 0);
        //TODO：区分车主乘客
        try {
            JSONObject jUser = new JSONObject(mDetail);
            String id = jUser.getString("id");
            if (user.userType == 1) target = new User(id, 2);
            else target = new User(id, 1);
            double reputation = jUser.getDouble("reputation");
            int finishedOrderNumber = jUser.getInt("finished_order_number");
            targetQueryNumber = jUser.getInt("match_query_number");
            String startName = jUser.getString("start_name");
            String destName = jUser.getString("dest_name");

            String onTheView = "";
            onTheView = onTheView + "ID: " + id + "\n";
            onTheView = onTheView + "从" + startName + "去往" + destName + "\n";
            onTheView = onTheView + "评价: " + String.format("%.2f", reputation) + "\n";
            onTheView = onTheView + "已完成订单数: " + Integer.toString(finishedOrderNumber) + "\n";

            if (target.getUserType() == 2) {
                JSONObject carInfo = jUser.getJSONObject("car_info");
                String numberPlate = carInfo.getString("number_plate");
                String carColor = carInfo.getString("car_color");
                String insurance = carInfo.getString("insurance");
                int maxPsg = carInfo.getInt("max_psg");
                int curPsg = carInfo.getInt("cur_psg");
                onTheView = onTheView + "车牌号: " + numberPlate + "\n";
                onTheView = onTheView + "车辆颜色: " + carColor + "\n";
                onTheView = onTheView + "保险情况: " + insurance + "\n";
                onTheView = onTheView + "最多可载: " + Integer.toString(maxPsg) + "人\n";
                onTheView = onTheView + "还可以载: " + Integer.toString(maxPsg - curPsg) + "人\n";
            }
            textView.setText(onTheView);

        }catch(Exception e){throw new RuntimeException(e);}

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.match_confirm:
                setResult(1);
                InteractUtil interactUtil = new InteractUtil();
                interactUtil.matchConfirm(user, target, mSelfQueryNumber, targetQueryNumber);
                finish();
        }
    }
}
