package com.ultron.tamu_carpool.personalinfo;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.ultron.tamu_carpool.R;
import com.ultron.tamu_carpool.StatusBarCompat;
import com.ultron.tamu_carpool.usr.User;

import org.json.JSONObject;

import java.security.spec.ECField;

public class PersonalInfoActivity extends AppCompatActivity {
    private User user;
    private TextView mTextView;
    private String personalInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            StatusBarCompat.compat(this, 0xFF80CBC4);
        setContentView(R.layout.activity_personal_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_personal_info_toolbar);
        toolbar.setTitle("个人信息");
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        personalInfo = intent.getStringExtra("personal_info");
        mTextView = (TextView) findViewById(R.id.personal_info_text);
        try {
            JSONObject jPersonalInfo = new JSONObject(personalInfo);
            String name = jPersonalInfo.getString("name");
            String gender = jPersonalInfo.getString("gender");
            int age = jPersonalInfo.getInt("age");
            double reputation = jPersonalInfo.getDouble("reputation");
            int finishedOrderNumber = jPersonalInfo.getInt("finished_order_number");
            String text = "";
            text = text + name + "，您好！\n";
            //text = text + "性别: " + gender + "\n";
            //text = text + "年龄: " + Integer.toString(age) + "\n";
            text = text + "已完成订单数: " + Integer.toString(finishedOrderNumber) + "\n";
            text = text + "评价: " + String.format("%.2f", reputation) + "\n";
            if (user.getUserType() == 2) {
                String numberPlate = jPersonalInfo.getString("number_plate");
                String carType = jPersonalInfo.getString("car_type");
                int maxPassenger = jPersonalInfo.getInt("max_psg");
                text = text + "车辆信息: \n";
                text = text + "\t车牌号: " + numberPlate + "\n";
                text = text + "\t车型: " + carType + "\n";
                text = text + "\t最多可载: " + Integer.toString(maxPassenger) + "人\n";
            }
            mTextView.setText(text);
        }catch (Exception e){throw new RuntimeException(e);}

    }
}
