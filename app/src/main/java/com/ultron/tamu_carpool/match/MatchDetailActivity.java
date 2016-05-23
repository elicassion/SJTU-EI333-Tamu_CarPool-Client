package com.ultron.tamu_carpool.match;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.services.route.DriveRouteResult;
import com.ultron.tamu_carpool.R;
import com.ultron.tamu_carpool.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.List;

public class MatchDetailActivity extends AppCompatActivity implements View.OnClickListener{

    private DriveRouteResult mDriveRouteResult;
    private String mDestName;
    private TextView mRouteInfo;

    private String mMatchResult;
    private JSONArray mJResult;
    private ViewGroup mDetailsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());

        //TODO:Add time option:realtime or appointment

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

        getDriveRouteResultFromSearch();
        beginMatch();
    }

    public void getDriveRouteResultFromSearch()
    {
        Intent intent = getIntent();
        mDriveRouteResult = (DriveRouteResult) intent.getSerializableExtra("drive_route_result");
        mDestName = intent.getStringExtra("destination");
        mRouteInfo = (TextView) findViewById(R.id.route_info);
        mRouteInfo.setText("去往: " + mDestName);

    }

    public void beginMatch()
    {
        //TODO: send messeges

        //TODO: receive messeges
        //This is DUMMY
        mMatchResult = "{" +
                       "    \"users\" : "+
                                         "["+
                                         "{"+
                                         "  \"phone\" : \"18217209315\","+
                                         "  \"repu\" : 5"+
                                         "},"+
                                         "{"+
                                         "  \"phone\" : \"11111111111\","+
                                         "  \"repu\" : 1"+
                                         "},"+
                                        "{"+
                                        "  \"phone\" : \"22222222222\","+
                                        "  \"repu\" : 2"+
                                        "},"+
                                        "{"+
                                        "  \"phone\" : \"33333333333\","+
                                        "  \"repu\" : 3"+
                                        "},"+
                                        "{"+
                                        "  \"phone\" : \"44444444444\","+
                                        "  \"repu\" : 4"+
                                        "},"+
                                        "{"+
                                        "  \"phone\" : \"00000000000\","+
                                        "  \"repu\" : 0"+
                                        "}"+
                                        "]"+
                       "}";


        try {
            mJResult = new JSONObject(mMatchResult).getJSONArray("users");
            for (int i = 0; i < mJResult.length(); ++i){
                JSONObject jUser = (JSONObject)mJResult.opt(i);
                String userDetail = "";
                String phoneNumber = jUser.getString("phone");
                int reputationStars = jUser.getInt("repu");
                userDetail = "电话: " + phoneNumber + "\n" + "评价: " + Integer.toString(reputationStars) + "星";
                final LinearLayout linearLayout = new LinearLayout(this);
                View.inflate(this, R.layout.content_match_detail, linearLayout);
                final View view = linearLayout.getChildAt(0);
                view.setTag(i+1);
                view.setOnClickListener(this);

                final TextView mTextView = (TextView) linearLayout.findViewById(R.id.match_detail_text);
                mTextView.setText(userDetail);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                mDetailsView.addView(linearLayout, layoutParams);

            }
        } catch (JSONException ex){
            ToastUtil.show(MatchDetailActivity.this, "NO Match Result!");
        }

    }

    @Override
    public void onClick(View v) {
        showMoreInfo((int)v.getTag());
    }

    public void showMoreInfo(int selectedUserNumber){
        //TODO: link MatchUnitInfoActivity
        ToastUtil.show(MatchDetailActivity.this, "你戳我老" + Integer.toString(selectedUserNumber) + "?!");
    }
}
