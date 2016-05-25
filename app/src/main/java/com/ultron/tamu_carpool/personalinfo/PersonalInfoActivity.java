package com.ultron.tamu_carpool.personalinfo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.ultron.tamu_carpool.R;

public class PersonalInfoActivity extends AppCompatActivity {
    private String mUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        Intent intent = getIntent();
        mUserId = intent.getStringExtra("id");
        TextView textview = new TextView(this);
        textview.setText(mUserId);
        setContentView(textview);
    }
}
