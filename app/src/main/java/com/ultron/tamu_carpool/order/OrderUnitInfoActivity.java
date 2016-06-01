package com.ultron.tamu_carpool.order;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
import com.ultron.tamu_carpool.StatusBarCompat;
import com.ultron.tamu_carpool.comment.CommentActivity;
import com.ultron.tamu_carpool.match.MatchDetailActivity;
import com.ultron.tamu_carpool.match.MatchUnitInfoActivity;
import com.ultron.tamu_carpool.usr.User;
import com.ultron.tamu_carpool.util.InteractUtil;
import com.ultron.tamu_carpool.util.ToastUtil;

import org.json.JSONObject;

public class OrderUnitInfoActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView mUnitDetail;
    private Button mUnitGoBtn;
    private LinearLayout mUnitGo;
    private int mOrderType;
    private String mOrderInfo;
    private int mQueryNumber;
    private int mOrderNumber;
    private ReMatchTask mReMatchTask;
    private ConfirmArriveTask mConfirmArriveTask;
    private Context mContext;
    private User mUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, 0xFF009688);
        setContentView(R.layout.activity_order_unit_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_order_unit_info_toolbar);
        //setSupportActionBar(toolbar);
        toolbar.setTitle("订单详情");
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
        mUnitDetail = (TextView) findViewById(R.id.order_unit_info_detail);
        mUnitGoBtn = (Button) findViewById(R.id.order_unit_go_btn);
        mUnitGo = (LinearLayout) findViewById(R.id.order_unit_go);


        getInfoFromExtra();

        //mUnitDetail.setText(mOrderInfo);
        try {
            switch (mOrderType) {
                case 1:
                    mUnitGoBtn.setText("去匹配");
                    JSONObject jQuery = new JSONObject(mOrderInfo);
                    int poolType = jQuery.getInt("pool_type");
                    int queryNumber = jQuery.getInt("query_number");
                    String time = jQuery.getString("time");
                    String startName = jQuery.getString("start_name");
                    String destName = jQuery.getString("dest_name");
                    String text = "";
                    if (poolType == 1) text = text + "实时拼车:\n";
                    else text = text + "预约拼车:\n";
                    text = text + time + "\n\t从" + startName + "\n" + "去往" + destName;
                    mUnitGoBtn.setOnClickListener(this);
                    mQueryNumber = queryNumber;
                    mUnitDetail.setText(text);
                    break;
                case 2:
                    mUnitGoBtn.setText("确认到达");
                    JSONObject jOrder = new JSONObject(mOrderInfo);
                    //int poolType2 = jOrder.getInt("pool_type");
                    int orderNumber = jOrder.getInt("order_number");
                    JSONObject jCarOwner = jOrder.getJSONObject("carowner");
                    JSONObject jPassenger = jOrder.getJSONObject("passenger");
                    String cTime = jCarOwner.getString("time");
                    String cStartName = jCarOwner.getString("start_name");
                    String cDestName = jCarOwner.getString("dest_name");
                    String cId = jCarOwner.getString("id");
                    String pTime = jPassenger.getString("time");
                    String pStartName = jPassenger.getString("start_name");
                    String pDestName = jPassenger.getString("dest_name");
                    String pId = jPassenger.getString("id");
                    String text2 = "";
                    //if (poolType2 == 1) text2 = text2 + "实时拼车:\n";
                    //else text2 = text2 + "预约拼车:\n";
                    text2 = text2 + "车主: " + cId + "\n\t" + cTime + " 从 " + cStartName + "去往 " + cDestName + "\n";
                    text2 = text2 + "乘客: " + pId + "\n\t" + pTime + " 从 " + pStartName + "去往 " + pDestName + "\n";
                    mUnitGoBtn.setOnClickListener(this);
                    mOrderNumber = orderNumber;
                    mUnitDetail.setText(text2);
                    break;
                case 3:

                    JSONObject jOrder2 = new JSONObject(mOrderInfo);
                    int orderNumber2 = jOrder2.getInt("order_number");
                    //int poolType3 = jOrder2.getInt("pool_type");
                    JSONObject jCarOwner2 = jOrder2.getJSONObject("carowner");
                    JSONObject jPassenger2 = jOrder2.getJSONObject("passenger");
                    String cTime2 = jCarOwner2.getString("time");
                    String cStartName2 = jCarOwner2.getString("start_name");
                    String cDestName2 = jCarOwner2.getString("dest_name");
                    String cId2 = jCarOwner2.getString("id");
                    String pTime2 = jPassenger2.getString("time");
                    String pStartName2 = jPassenger2.getString("start_name");
                    String pDestName2 = jPassenger2.getString("dest_name");
                    String pId2 = jPassenger2.getString("id");
                    boolean ifCarComment = jCarOwner2.getBoolean("if_comment");
                    boolean ifPassengerComment = jPassenger2.getBoolean("if_comment");
                    String cComment = "还未评价";
                    String pComment = "还未评价";
                    double cReputation = -1;
                    double pReputation = -1;
                    if (ifCarComment) {
                        cComment = jCarOwner2.getString("comment");
                        cReputation = jCarOwner2.getDouble("reputation");
                    }
                    if (ifPassengerComment) {
                        pComment = jPassenger2.getString("comment");
                        pReputation = jPassenger2.getDouble("reputation");
                    }

                    //String comment = "";
                    //mUnitGoBtn.setOnClickListener(this);
                    String text3 = "";
                    //if (poolType3 == 1) text3 = text3 + "实时拼车:\n";
                    //else text3 = text3 + "预约拼车:\n";
                    text3 = text3 + "车主: " + cId2 + "\n\t" + cTime2 + " 从 " + cStartName2 + "去往 " + cDestName2 + "\n";
                    text3 = text3 + "乘客: " + pId2 + "\n\t" + pTime2 + " 从 " + pStartName2 + "去往 " + pDestName2 + "\n";
                    text3 = text3 + "\n乘客对车主的评价:\n\t";
                    text3 = text3 + cComment + "\n";
                    text3 = text3+ "评分:" + String.format("%.2f", cReputation) + "分\n";
                    text3 = text3 + "\n车主对乘客的评价:\n\t";
                    text3 = text3 + pComment + "\n";
                    text3 = text3+ "评分" + String.format("%.2f", pReputation) + "分\n";

                    //text3 = text3 + comment;
                    if (mUser.getUserType() == 1 ){
                        if (!ifPassengerComment) {
                            mUnitGoBtn.setText("去评价");
                            mUnitGoBtn.setOnClickListener(this);
                        }
                        else {
                            mUnitGoBtn.setText("已评价");
                        }
                    }
                    else{
                        if (!ifCarComment) {
                            mUnitGoBtn.setText("去评价");
                            mUnitGoBtn.setOnClickListener(this);
                        }
                        else {
                            mUnitGoBtn.setText("已评价");
                        }
                    }
                    mOrderNumber = orderNumber2;
                    mUnitDetail.setText(text3);
                    break;
            }
        }catch(Exception e){throw new RuntimeException(e);}
    }

    public void getInfoFromExtra() {
        Intent intent = getIntent();
        mOrderType = intent.getIntExtra("order_type", 0);
        mOrderInfo = intent.getStringExtra("order_info");
        mUser = (User) intent.getSerializableExtra("user");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.order_unit_go_btn:
                //TODO: link to activity
                switch (mOrderType){
                    case 1:
                        mReMatchTask = new ReMatchTask();
                        mReMatchTask.execute((Void) null);
                        break;
                    case 2:
                        mConfirmArriveTask = new ConfirmArriveTask();
                        mConfirmArriveTask.execute((Void) null);
                        break;
                    case 3:
                        //ToastUtil.show(this, "人在塔在！！！");
                        Intent intent = new Intent(OrderUnitInfoActivity.this, CommentActivity.class);
                        intent.putExtra("order_info", mOrderInfo);
                        intent.putExtra("user", mUser);
                        startActivity(intent);
                        break;
                }

        }
    }

    public class ReMatchTask extends AsyncTask<Void, Void, Boolean> {
        private int type;
        ReMatchTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                InteractUtil interactUtil = new InteractUtil();
                JSONObject jQuery = new JSONObject(mOrderInfo);
                int poolType = jQuery.getInt("pool_type");
                String time = jQuery.getString("time");
                String startName = jQuery.getString("start_name");
                String destName = jQuery.getString("dest_name");
                String id = jQuery.getString("id");
                int userType = jQuery.getInt("user_type");
                User user = new User(id, userType);
                String matchResult = interactUtil.reMatch(mQueryNumber);

                Intent intentMatchDetail = new Intent(OrderUnitInfoActivity.this, MatchDetailActivity.class);
                intentMatchDetail.putExtra("start_name", startName);
                intentMatchDetail.putExtra("dest_name", destName);
                intentMatchDetail.putExtra("pool_type", poolType);
                intentMatchDetail.putExtra("time", time);
                intentMatchDetail.putExtra("user", user);
                intentMatchDetail.putExtra("match_result", matchResult);
                startActivityForResult(intentMatchDetail, 2);

                return true;
            }catch(Exception e){throw new RuntimeException(e);}
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mReMatchTask = null;
            if (success) {
                finish();
            }
        }

        @Override
        protected void onCancelled() {
            mReMatchTask = null;
        }

    }

    public class ConfirmArriveTask extends AsyncTask<Void, Void, Boolean> {
        private int type;
        ConfirmArriveTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            InteractUtil interactUtil = new InteractUtil();
            interactUtil.confirmArrive(mOrderNumber);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mConfirmArriveTask = null;
            if (success) {
                confirmSetResult();
                finish();
            }
        }

        @Override
        protected void onCancelled() {
            mConfirmArriveTask = null;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == 1){
            ToastUtil.show(mContext, "请求成功！");
            setResult(1);
            finish();
        }
    }

    public void confirmSetResult(){
        setResult(2);
    }


}
