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
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amap.api.services.route.DriveRouteResult;
import com.ultron.tamu_carpool.R;
import com.ultron.tamu_carpool.util.InteractUtil;
import com.ultron.tamu_carpool.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.sql.Time;
import java.util.List;

public class MatchDetailActivity extends AppCompatActivity implements View.OnClickListener{

    private DriveRouteResult mDriveRouteResult;
    private String mDestName;
    private TextView mRouteInfo;
    private ProgressBar mProgressView;
    private Context mContext;

    private String mMatchResult;
    private JSONArray mJResult;
    private ViewGroup mDetailsView;
    private Toolbar toolbar;

    private int mPoolType;
    private Time mTime;

    private MatchTask mMatchTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        mProgressView = (ProgressBar)findViewById(R.id.progress);
        mRouteInfo = (TextView) findViewById(R.id.route_info);

        getDriveRouteResultFromSearch();
        showProgress(true);
        beginMatch();
    }

    public void getDriveRouteResultFromSearch()
    {
        Intent intent = getIntent();
        mDriveRouteResult = (DriveRouteResult) intent.getSerializableExtra("drive_route_result");
        mDestName = intent.getStringExtra("destination");

        mRouteInfo.setText("去往: " + mDestName);

    }

    public void beginMatch()
    {
        //TODO: send messeges

        //TODO: receive messeges
        //This is DUMMY
        mMatchTask = new MatchTask(mDriveRouteResult, mPoolType, mTime);

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
                linearLayout.setClickable(true);
                linearLayout.setFocusable(true);

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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);


            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });


            mDetailsView.setVisibility(show ? View.GONE : View.VISIBLE);
            mDetailsView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mDetailsView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mDetailsView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class MatchTask extends AsyncTask<Void, Void, Boolean>{

        private DriveRouteResult mDriveRouteResult;
        private Time mTime;
        private int mPoolType;

        MatchTask(DriveRouteResult result, int poolType, Time time){
            mDriveRouteResult = result;
            mPoolType = poolType;
            mTime = time;
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            InteractUtil interactUtil = new InteractUtil();
            if (!interactUtil.socketSuccess){
                mMatchResult = null;
                return false;
            }
            mMatchResult = interactUtil.match(mDriveRouteResult, mPoolType, mTime);

            return true;
        }
        @Override
        protected void onPostExecute(final Boolean success) {
            mMatchTask = null;
            if (success) showProgress(false);
            else ToastUtil.show(mContext, "Connect Failed!");
        }

        @Override
        protected void onCancelled() {
            mMatchTask = null;
            showProgress(false);
        }
    }
}
