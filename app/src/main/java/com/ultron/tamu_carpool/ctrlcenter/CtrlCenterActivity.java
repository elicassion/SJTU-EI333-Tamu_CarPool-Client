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
    private LinearLayout body;

    //TODO: modify them to self-indicated names
    private LinearLayout mTabCarpool;
    private LinearLayout mTabOrder;
    private LinearLayout mTabInfo;

    private ImageButton carButton;
    private ImageButton odrButton;
    private ImageButton infButton;

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
                View v = getLocalActivityManager().startActivity("carButton",
                        new Intent(CtrlCenterActivity.this, SearchActivity.class)).getDecorView();

                carButton.setImageResource(R.drawable.tab_weixin_pressed);
                body.addView(v);
                break;
            case 1:
                body.removeAllViews();
                body.addView(getLocalActivityManager().startActivity("odrButton",
                        new Intent(CtrlCenterActivity.this, OrderMainActivity.class))
                        .getDecorView());
                odrButton.setImageResource(R.drawable.tab_find_frd_pressed);
                break;
            case 2:
                body.removeAllViews();
                body.addView(getLocalActivityManager().startActivity("infButton",
                        new Intent(CtrlCenterActivity.this, PersonalInfoActivity.class))
                        .getDecorView());
                infButton.setImageResource(R.drawable.tab_address_pressed);
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
        carButton.setImageResource(R.drawable.tab_weixin_normal);
        odrButton.setImageResource(R.drawable.tab_find_frd_normal);
        infButton.setImageResource(R.drawable.tab_address_normal);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        System.exit(0);
    }
}
