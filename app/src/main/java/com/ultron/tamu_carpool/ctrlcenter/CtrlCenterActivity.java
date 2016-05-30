package com.ultron.tamu_carpool.ctrlcenter;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.ultron.tamu_carpool.R;
import com.ultron.tamu_carpool.confirm.AskForConfirmActivity;
import com.ultron.tamu_carpool.confirm.ConfirmedMatchActivity;
import com.ultron.tamu_carpool.order.OrderMainActivity;
import com.ultron.tamu_carpool.personalinfo.PersonalInfoActivity;
import com.ultron.tamu_carpool.search.SearchActivity;
import com.ultron.tamu_carpool.usr.User;
import com.ultron.tamu_carpool.util.InteractUtil;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class CtrlCenterActivity extends ActivityGroup implements OnClickListener
{
    @SuppressWarnings("unused")
    private LinearLayout body;

    //TODO: modify them to self-indicated names
    private LinearLayout mTabCarpool;
    private LinearLayout mTabOrder;
    private LinearLayout mTabInfo;

    private ImageButton carButton;
    private ImageButton odrButton;
    private ImageButton infButton;
    private User user;

    private Timer mTimer;

    private int flag = 0; // 通过标记跳转不同的页面，显示不同的菜单项

    private String orderInfo;
    //private OrderTask mOrderTask;
    private String personalInfo;
    private PersonalInfoTask mPersonalInfoTask;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_ctrl_center);
        Intent faIntent = getIntent();
        user = (User) faIntent.getSerializableExtra("user");
        initView();
        initEvent();
        showView(flag);
        mTimer = new Timer(true);
        listenOnMatchConfirm();
    }

    public void listenOnMatchConfirm() {
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    InteractUtil interactUtil = new InteractUtil();
                    interactUtil.getMatchConfirm(user);
                    String backInfo = interactUtil.getMatchConfirm(user);
                    if (backInfo == null) return;
                    JSONObject jBackInfo = new JSONObject(backInfo);
                    int code = jBackInfo.getInt("code");
                    JSONObject jTarget = jBackInfo.getJSONObject("target");
                    JSONObject jMyQuery = jBackInfo.getJSONObject("my_query");
                    if (code == 1){//我的匹配得到了确认
                        Intent intentConfirmedMatch = new Intent(CtrlCenterActivity.this, ConfirmedMatchActivity.class);
                        intentConfirmedMatch.putExtra("target", jTarget.toString());
                        intentConfirmedMatch.putExtra("my_query", jMyQuery.toString());
                        intentConfirmedMatch.putExtra("user", user);
                        startActivity(intentConfirmedMatch);
                    }
                    else if (code == 2){//我有人匹配选择了我
                        Intent intentAskForConfirm = new Intent(CtrlCenterActivity.this, AskForConfirmActivity.class);
                        intentAskForConfirm.putExtra("target", jTarget.toString());
                        intentAskForConfirm.putExtra("my_query", jMyQuery.toString());
                        //intentAskForConfirm.putExtra("user", user);
                        startActivity(intentAskForConfirm);
                    }
                }catch(Exception e){throw new RuntimeException(e);}
            }
        },5000, 10000);
    }

    private void initEvent()
    {
        mTabCarpool.setOnClickListener(this);
        mTabOrder.setOnClickListener(this);
        mTabInfo.setOnClickListener(this);
    }

    /*
     * 初始化主界面
     */
    public void initView() {
        body = (LinearLayout) findViewById(R.id.body);

        mTabCarpool = (LinearLayout) findViewById(R.id.tab_carpool);
        mTabOrder = (LinearLayout) findViewById(R.id.tab_order);
        mTabInfo = (LinearLayout) findViewById(R.id.tab_info);


        carButton = (ImageButton) findViewById(R.id.tab_carpool_img);
        odrButton = (ImageButton) findViewById(R.id.tab_order_img);
        infButton = (ImageButton) findViewById(R.id.tab_info_img);

    }

    // 在主界面中显示其他界面
    public void showView(int flag) {
        switch (flag) {
            case 0:
                body.removeAllViews();
                Intent intentSearch = new Intent(CtrlCenterActivity.this, SearchActivity.class);
                intentSearch.putExtra("user", user);
                View v = getLocalActivityManager().startActivity("carButton", intentSearch
                        ).getDecorView();
                carButton.setImageResource(R.drawable.tab_carpool_pressed);
                body.addView(v);
                break;
            case 1:
                body.removeAllViews();
                Intent intentOrderMain = new Intent(CtrlCenterActivity.this, OrderMainActivity.class);
                intentOrderMain.putExtra("user", user);
                intentOrderMain.putExtra("order_info", orderInfo);
                body.addView(getLocalActivityManager().startActivity("odrButton",intentOrderMain
                ).getDecorView());
                odrButton.setImageResource(R.drawable.tab_order_pressed);
                //body.addView(v);
                break;
            case 2:
                body.removeAllViews();
                mPersonalInfoTask = new PersonalInfoTask();
                mPersonalInfoTask.execute((Void) null);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v)
    {
        resetImgs();
        switch (v.getId())
        {
            case R.id.tab_carpool:
                showView(0);
                break;
            case R.id.tab_order:
                showView(1);
                break;
            case R.id.tab_info:
                showView(2);
                break;
            default:
                break;
        }
    }

    private void resetImgs()
    {
        carButton.setImageResource(R.drawable.tab_carpool_normal);
        odrButton.setImageResource(R.drawable.tab_order_normal);
        infButton.setImageResource(R.drawable.tab_info_normal);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        System.exit(0);
    }


    public class PersonalInfoTask extends AsyncTask<Void, Void, Boolean> {
        PersonalInfoTask() {

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: link service to check id and password
            InteractUtil interactUtil = new InteractUtil();
            personalInfo = interactUtil.getPersonalInfo(user);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mPersonalInfoTask = null;
            if (success) {
                Intent intentPersonalInfo = new Intent(CtrlCenterActivity.this, PersonalInfoActivity.class);
                intentPersonalInfo.putExtra("user", user);
                intentPersonalInfo.putExtra("personal_info", personalInfo);
                View v = getLocalActivityManager().startActivity("infButton",intentPersonalInfo
                ).getDecorView();
                infButton.setImageResource(R.drawable.tab_info_pressed);
                body.addView(v);
            }
        }

        @Override
        protected void onCancelled() {
            mPersonalInfoTask = null;
        }

    }
}
