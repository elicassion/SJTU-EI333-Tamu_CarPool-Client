package com.ultron.tamu_carpool.confirm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ultron.tamu_carpool.R;
import com.ultron.tamu_carpool.usr.User;
import com.ultron.tamu_carpool.util.InteractUtil;

public class ConfirmedMatchActivity extends AppCompatActivity implements View.OnClickListener{
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmed_match);
        Intent faIntent = getIntent();
        user = (User) faIntent.getSerializableExtra("user");
        Button button = (Button) findViewById(R.id.know_confirm);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.know_confirm:
                InteractUtil interactUtil = new InteractUtil();
                interactUtil.setKnowed(user, true);
                finish();
        }
    }
}
