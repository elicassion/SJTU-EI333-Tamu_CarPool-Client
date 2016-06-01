package com.ultron.tamu_carpool.comment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ultron.tamu_carpool.R;
import com.ultron.tamu_carpool.StatusBarCompat;
import com.ultron.tamu_carpool.usr.User;
import com.ultron.tamu_carpool.util.InteractUtil;
import com.ultron.tamu_carpool.util.ToastUtil;

import org.json.JSONObject;

public class CommentActivity extends AppCompatActivity
        implements View.OnClickListener, RatingBar.OnRatingBarChangeListener{
    private TextView mCommentText;
    private Button mSubmit;
    private Context mContext;
    private String mOrderInfo;
    private User user;
    private RatingBar mRatingBar;
    private double mRepu = 0;
    private int orderNumber;
    private String commentContent = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            StatusBarCompat.compat(this, 0xFF80CBC4);

        setContentView(R.layout.activity_comment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_comment_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mCommentText = (EditText) findViewById(R.id.comment_text);
        mSubmit = (Button) findViewById(R.id.submit_button);
        mSubmit.setOnClickListener(this);
        mContext = getApplicationContext();
        mRatingBar = (RatingBar) findViewById(R.id.comment_rating_bar);
        mRatingBar.setOnRatingBarChangeListener(this);
        Intent faIntent = getIntent();
        mOrderInfo = faIntent.getStringExtra("order_info");
        user = (User) faIntent.getSerializableExtra("user");
        try {
            JSONObject jOrderInfo = new JSONObject(mOrderInfo);
            orderNumber = jOrderInfo.getInt("order_number");
        }catch(Exception e){throw new RuntimeException(e);}

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submit_button:
                commentContent = mCommentText.getText().toString();
                InteractUtil interactUtil = new InteractUtil();
                interactUtil.addComment(user, orderNumber, commentContent, mRepu);
                ToastUtil.show(mContext, "评价成功");
                finish();
                break;
        }
    }


    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        mRepu = rating;
    }
}
