package com.ultron.tamu_carpool.match;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TimeUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DriveRouteResult;
import com.ultron.tamu_carpool.R;
import com.ultron.tamu_carpool.ctrlcenter.CtrlCenterActivity;
import com.ultron.tamu_carpool.usr.User;
import com.ultron.tamu_carpool.util.InteractUtil;
import com.ultron.tamu_carpool.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.sql.Time;
import java.util.List;

public class MatchDetailActivity extends AppCompatActivity implements View.OnClickListener{

    private User user;
    private DriveRouteResult mDriveRouteResult;
    private String mDestName;
    private String mStartName;
    private LatLonPoint mStartPoint;
    private LatLonPoint mEndPoint;
    private TextView mRouteInfo;
    private Context mContext;

    private String mMatchResult;
    private JSONArray mJResult;
    private ViewGroup mDetailsView;
    private Toolbar toolbar;

    private int mUserType;
    private int mPoolType;
    private String mTime;
    private int mSelfQueryNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());



        //TODO:辣鸡button 可以脑洞改
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        mDetailsView = (ViewGroup)findViewById(R.id.match_detail_select);
        mRouteInfo = (TextView) findViewById(R.id.route_info);

        getDriveRouteResultFromSearch();
        beginMatch();
    }

    public void getDriveRouteResultFromSearch()
    {
        Intent intent = getIntent();
        mDriveRouteResult = intent.getParcelableExtra("drive_route_result");
        if (mDriveRouteResult == null)
            Log.e("driverouteresult: ", "null");
        mDestName = intent.getStringExtra("dest_name");
        mStartName = intent.getStringExtra("start_name");
        mEndPoint = intent.getParcelableExtra("dest_point");
        mStartPoint = intent.getParcelableExtra("start_point");
        mPoolType = intent.getIntExtra("pool_type", 1);
        mTime = intent.getStringExtra("time");
        user = (User)intent.getSerializableExtra("user");
        mMatchResult = intent.getStringExtra("match_result");
        Log.e("matchdetail activity", mMatchResult);


        mRouteInfo.setText("从:"+mStartName+"\n去往: " + mDestName);

    }

    public void beginMatch()
    {
        //TODO: send messeges

        //TODO: receive messeges
        //This is DUMMY
//        mMatchTask = new MatchTask(mDriveRouteResult, mPoolType, mTime);
//        mMatchTask.execute((Void) null);

        //Log.e("match result: ", mMatchResult);
        //接受所有数据 直接用接收并存储的本地数据初始化unitinfo
        //在本页面每项只取一瓢
        if (mMatchResult != null) {
            try {
                JSONObject jMatchResult = new JSONObject(mMatchResult);
                mJResult = jMatchResult.getJSONArray("users");
                mSelfQueryNumber = jMatchResult.getInt("self_query_number");
                //TODO:区分乘客 车主 显示信息
                if (user.getUserType() == 1 || user.getUserType() == 2) {//匹配者乘客
                    for (int i = 0; i < mJResult.length(); ++i) {
                        JSONObject jUser = (JSONObject) mJResult.opt(i);
                        //Log.e("juser: ", jUser.toString());
                        String userDetail = "";
                        String phoneNumber = jUser.getString("id");
                        double reputationStars = jUser.getDouble("reputation");
                        int orderNumber = jUser.getInt("order_number");
                        userDetail = "电话: " + phoneNumber + "\n" + "评价: " + Double.toString(reputationStars) + "星\n"
                                        +"已成功拼车: " + Integer.toString(orderNumber) +"次" +"\n";
                        //Log.e("userDetail", userDetail);

                        final LinearLayout linearLayout = new LinearLayout(this);
                        View.inflate(this, R.layout.content_match_detail, linearLayout);
                        final View view = linearLayout.getChildAt(0);
                        view.setTag(i + 1);
                        view.setOnClickListener(this);
                        linearLayout.setClickable(true);
                        linearLayout.setFocusable(true);

                        final TextView mTextView = (TextView) linearLayout.findViewById(R.id.match_detail_text);
                        mTextView.setText(userDetail);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        mDetailsView.addView(linearLayout, layoutParams);

                    }
                }
            } catch (JSONException ex) {
                ToastUtil.show(MatchDetailActivity.this, "NO Match Result!");
            }
        }

    }

    @Override
    public void onClick(View v) {
        showMoreInfo((int)v.getTag());
    }

    public void showMoreInfo(int selectedUserNumber){
        //TODO: link MatchUnitInfoActivity
        //ToastUtil.show(MatchDetailActivity.this, "你戳我老" + Integer.toString(selectedUserNumber) + "?!");
        Intent intentMatchUnitInfo = new Intent(MatchDetailActivity.this, MatchUnitInfoActivity.class);
        JSONObject jUser = (JSONObject)mJResult.opt(selectedUserNumber - 1);
        intentMatchUnitInfo.putExtra("detail",jUser.toString());
        intentMatchUnitInfo.putExtra("user", user);
        intentMatchUnitInfo.putExtra("time", mTime);
        intentMatchUnitInfo.putExtra("start", mStartName);
        intentMatchUnitInfo.putExtra("destination", mDestName);
        intentMatchUnitInfo.putExtra("self_query_number", mSelfQueryNumber);
        startActivityForResult(intentMatchUnitInfo, user.userType);// usertype
        //用startactivityforresult跳转matchunitinfo 如果 matchunitinfo点击了确认 则在其finish之前传回信息
        //然后清空界面 出现成功（？）
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1){
            //mDetailsView.removeAllViews();
            //TextView successText = new TextView(this);
            //successText.setText("已确认！请到订单页等待回复");
            //mDetailsView.addView(successText);
            setResult(1);
            finish();
        }
        else{
            setResult(0);
        }
    }



}
