package com.ultron.tamu_carpool.ctrlcenter;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.ultron.tamu_carpool.R;
import com.ultron.tamu_carpool.order.OrderMainActivity;
import com.ultron.tamu_carpool.personalinfo.PersonalInfoActivity;
import com.ultron.tamu_carpool.search.SearchActivity;
public class CtrlCenterActivity extends ActivityGroup implements OnClickListener
{
    @SuppressWarnings("unused")
    private String mUserId = "18217209315";
    private LinearLayout body;

    //TODO: modify them to self-indicated names
    private LinearLayout mTabWeixin;
    private LinearLayout mTabFrd;
    private LinearLayout mTabAddress;

    private ImageButton one;
    private ImageButton two;
    private ImageButton three;

    private int flag = 0; // 通过标记跳转不同的页面，显示不同的菜单项
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_ctrl_center);
        initView();
        initEvent();
        showView(flag);

    }

    private void initEvent()
    {
        mTabWeixin.setOnClickListener(this);
        mTabFrd.setOnClickListener(this);
        mTabAddress.setOnClickListener(this);
    }

    /*
     * 初始化主界面
     */
    public void initView() {
        body = (LinearLayout) findViewById(R.id.body);

        mTabWeixin = (LinearLayout) findViewById(R.id.id_tab_weixin);
        mTabFrd = (LinearLayout) findViewById(R.id.id_tab_frd);
        mTabAddress = (LinearLayout) findViewById(R.id.id_tab_address);


        one = (ImageButton) findViewById(R.id.id_tab_weixin_img);
        two = (ImageButton) findViewById(R.id.id_tab_frd_img);
        three = (ImageButton) findViewById(R.id.id_tab_address_img);

    }

    // 在主界面中显示其他界面
    public void showView(int flag) {
        switch (flag) {
            case 0:
                body.removeAllViews();
                Intent intentSearch = new Intent(CtrlCenterActivity.this, SearchActivity.class);
                intentSearch.putExtra("id", mUserId);
                View v = getLocalActivityManager().startActivity("one",
                        intentSearch).getDecorView();

                one.setImageResource(R.drawable.tab_weixin_pressed);
                body.addView(v);
                break;
            case 1:
                body.removeAllViews();
                Intent intentOrderMain = new Intent(CtrlCenterActivity.this, OrderMainActivity.class);
                intentOrderMain.putExtra("id", mUserId);
                body.addView(getLocalActivityManager().startActivity("two",
                        intentOrderMain)
                        .getDecorView());
                two.setImageResource(R.drawable.tab_find_frd_pressed);
                break;
            case 2:
                body.removeAllViews();
                Intent intentPersonalInfo = new Intent(CtrlCenterActivity.this, PersonalInfoActivity.class);
                intentPersonalInfo.putExtra("id", mUserId);
                body.addView(getLocalActivityManager().startActivity(
                        "three", intentPersonalInfo)
                        .getDecorView());
                three.setImageResource(R.drawable.tab_address_pressed);
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
            case R.id.id_tab_weixin:
                showView(0);
                break;
            case R.id.id_tab_frd:
                showView(1);
                break;
            case R.id.id_tab_address:
                showView(2);
                break;
            default:
                break;
        }
    }

    private void resetImgs()
    {
        one.setImageResource(R.drawable.tab_weixin_normal);
        two.setImageResource(R.drawable.tab_find_frd_normal);
        three.setImageResource(R.drawable.tab_address_normal);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        System.exit(0);
    }
}
