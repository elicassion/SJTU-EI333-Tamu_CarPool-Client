package com.ultron.tamu_carpool.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.style.LeadingMarginSpan;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ultron.tamu_carpool.R;
import com.ultron.tamu_carpool.comment.CommentActivity;
import com.ultron.tamu_carpool.util.ToastUtil;

public class OrderUnitInfoActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView mUnitDetail;
    private Button mUnitGoBtn;
    private LinearLayout mUnitGo;
    private int mOrderType;
    private String mOrderInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_unit_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initView();

    }

    public void initView() {
        mUnitDetail = (TextView) findViewById(R.id.order_unit_info_detail);
        mUnitGoBtn = (Button) findViewById(R.id.order_unit_go_btn);
        mUnitGo = (LinearLayout) findViewById(R.id.order_unit_go);
        mUnitGoBtn.setOnClickListener(this);

        getInfoFromExtra();

        mUnitDetail.setText(mOrderInfo);
        switch (mOrderType){
            case 1:
                mUnitGoBtn.setText("去匹配");
                break;
            case 2:
                mUnitGoBtn.setText("确认到达");
                break;
            case 3:
                mUnitGoBtn.setText("去评价");
                break;
        }

    }

    public void getInfoFromExtra() {
        Intent intent = getIntent();
        mOrderType = intent.getIntExtra("order_type", 0);
        mOrderInfo = intent.getStringExtra("order_info");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.order_unit_go_btn:
                //TODO: link to activity
                switch (mOrderType){
                    case 1:
                        ToastUtil.show(this, "现在的我已经飙到极限了！！");
                        break;
                    case 2:
                        ToastUtil.show(this, "我的大刀已饥渴难耐！");
                        break;
                    case 3:
                        ToastUtil.show(this, "人在塔在！！！");
                        Intent intent = new Intent(OrderUnitInfoActivity.this, CommentActivity.class);
                        break;
                }

        }
    }
}
