package com.ultron.tamu_carpool.order;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.SweepGradient;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.ultron.tamu_carpool.R;
import com.ultron.tamu_carpool.usr.User;
import com.ultron.tamu_carpool.util.InteractUtil;
import com.ultron.tamu_carpool.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OrderMainActivity extends AppCompatActivity
        implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener{

    private User user;
    private Toolbar toolbar;
    private Context mContext;
    private ViewGroup mDetailsView;
    private JSONObject jOrders;
    private JSONArray jUnMatched;
    private JSONArray jHasMatched;
    private JSONArray jHasDone;
    private int unMatchedNumber = 0;
    private int hasMatchedNumber = 0;
    private int hasDoneNumber = 0;
    private Map<Integer, Integer> orderNumberMap = new HashMap<Integer, Integer>();
    private Map<Integer, Integer> orderTypeMap = new HashMap<Integer, Integer>();
    private OrderTask mOrderTask = null;
    private String orderInfoString;
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_main);
        Intent faIntent = getIntent();
        user = (User)faIntent.getSerializableExtra("user");
        mContext = this.getApplicationContext();
        //orderInfoString = faIntent.getStringExtra("order_info");
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.order_main_swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        mDetailsView = (ViewGroup)findViewById(R.id.order_detail_select);
        mOrderTask = new OrderTask();
        mOrderTask.execute((Void) null);
        //Log.e("orderinforString", orderInfoString);
        //initView();
    }

    public void initView(String orderInfo){
        try{
            mDetailsView.removeAllViews();
            jOrders = new JSONObject(orderInfo);
            Log.e("jorderssss", jOrders.toString());
            unMatchedNumber = jOrders.getInt("unmatched_number");
            hasMatchedNumber = jOrders.getInt("hasmatched_number");
            hasDoneNumber = jOrders.getInt("hasdone_number");
            if (unMatchedNumber != 0)
                jUnMatched = jOrders.getJSONArray("unmatched");
            else
                jUnMatched = null;

            if (hasMatchedNumber != 0)
                jHasMatched = jOrders.getJSONArray("hasmatched");
            else
                jHasMatched = null;

            if (hasDoneNumber != 0)
                jHasDone = jOrders.getJSONArray("hasdone");
            else
                jHasDone = null;
            int allNumber = 0;
            if (jUnMatched != null) {
                for (int i = 0; i < jUnMatched.length(); ++i) {
                    ++allNumber;//++unMatchedNumber;
                    JSONObject jQuery = (JSONObject) jUnMatched.opt(i);
                    Log.e("jquery", jQuery.toString());
                    //String time = jQuery.getString("time");
                    //String startName = jQuery.getString("start_name");
                    String destName = jQuery.getString("dest_name");
                    int queryNumber = jQuery.getInt("query_number");
                    orderNumberMap.put(allNumber, queryNumber);
                    orderTypeMap.put(allNumber,1);
                    String text = "";
                    text = text + "去往" + destName + "\n";

                    final LinearLayout linearLayout = new LinearLayout(this);
                    View.inflate(this, R.layout.content_order_detail, linearLayout);
                    final View view = linearLayout.getChildAt(0);
                    view.setTag(allNumber);
                    view.setOnClickListener(this);
                    linearLayout.setClickable(true);
                    linearLayout.setFocusable(true);

                    final TextView mTextView = (TextView) linearLayout.findViewById(R.id.order_detail_text);
                    mTextView.setText(text);
                    final TextView orderTypeTextView = (TextView) linearLayout.findViewById(R.id.order_type_text);
                    orderTypeTextView.setText("未匹配");
                    orderTypeTextView.setTextColor(Color.rgb(255, 0, 0));
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    mDetailsView.addView(linearLayout, layoutParams);
                }
            }
            if (jHasMatched != null) {
                for (int i = 0; i < jHasMatched.length(); ++i) {
                    ++allNumber;//++hasMatchedNumber;
                    JSONObject jOrder = (JSONObject) jHasMatched.opt(i);
                    //Log.e("jorder", jOrder.toString());
                    int orderNumber = jOrder.getInt("order_number");
                    orderNumberMap.put(allNumber, orderNumber);
                    orderTypeMap.put(allNumber,2);
                    JSONObject jCarOwner = jOrder.getJSONObject("carowner");
                    JSONObject jPassenger = jOrder.getJSONObject("passenger");
                    //String cTime = jCarOwner.getString("time");
                    //String cStartName = jCarOwner.getString("start_name");
                    String cDestName = jCarOwner.getString("dest_name");
                    String cId = jCarOwner.getString("id");
                    //String pTime = jPassenger.getString("time");
                    //String pStartName = jPassenger.getString("start_name");
                    String pDestName = jPassenger.getString("dest_name");
                    String pId = jPassenger.getString("id");
                    String text = "";
                    text = text + "车主: " + cId + " " + "去往" + cDestName + "\n";
                    text = text + "乘客: " + pId + " " + "去往" + pDestName + "\n";

                    final LinearLayout linearLayout = new LinearLayout(this);
                    View.inflate(this, R.layout.content_order_detail, linearLayout);
                    final View view = linearLayout.getChildAt(0);
                    view.setTag(allNumber);
                    view.setOnClickListener(this);
                    linearLayout.setClickable(true);
                    linearLayout.setFocusable(true);

                    final TextView mTextView = (TextView) linearLayout.findViewById(R.id.order_detail_text);
                    mTextView.setText(text);
                    final TextView orderTypeTextView = (TextView) linearLayout.findViewById(R.id.order_type_text);
                    orderTypeTextView.setText("已匹配, 正在进行");
                    orderTypeTextView.setTextColor(Color.rgb(0, 0, 255));
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    mDetailsView.addView(linearLayout, layoutParams);
                }
            }
            if (jHasDone != null) {
                for (int i = 0; i < jHasDone.length(); ++i) {
                    ++allNumber;//++hasDoneNumber;
                    JSONObject jOrder = (JSONObject) jHasDone.opt(i);
                    Log.e("jorder", jOrder.toString());
                    int orderNumber = jOrder.getInt("order_number");
                    orderNumberMap.put(allNumber, orderNumber);
                    orderTypeMap.put(allNumber,3);
                    JSONObject jCarOwner = jOrder.getJSONObject("carowner");
                    JSONObject jPassenger = jOrder.getJSONObject("passenger");
                    //String cTime = jCarOwner.getString("time");
                    //String cStartName = jCarOwner.getString("start_name");
                    String cDestName = jCarOwner.getString("dest_name");
                    String cId = jCarOwner.getString("id");
                    //String pTime = jPassenger.getString("time");
                    //String pStartName = jPassenger.getString("start_name");
                    String pDestName = jPassenger.getString("dest_name");
                    String pId = jPassenger.getString("id");
                    String text = "";
                    text = text + "车主: " + cId + " " + "去往" + cDestName + "\n";
                    text = text + "乘客: " + pId + " " + "去往" + pDestName + "\n";

                    final LinearLayout linearLayout = new LinearLayout(this);
                    View.inflate(this, R.layout.content_order_detail, linearLayout);
                    final View view = linearLayout.getChildAt(0);
                    view.setTag(allNumber);
                    view.setOnClickListener(this);
                    linearLayout.setClickable(true);
                    linearLayout.setFocusable(true);

                    final TextView mTextView = (TextView) linearLayout.findViewById(R.id.order_detail_text);
                    mTextView.setText(text);
                    final TextView orderTypeTextView = (TextView) linearLayout.findViewById(R.id.order_type_text);
                    orderTypeTextView.setText("已完成");
                    orderTypeTextView.setTextColor(Color.rgb(0, 255, 0));
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    mDetailsView.addView(linearLayout, layoutParams);
                }
            }
            swipeRefreshLayout.setRefreshing(false);
        }catch(Exception e){throw new RuntimeException(e);}

    }


    @Override
    public void onClick(View v) {
        try {
            int viewNumber = Integer.parseInt(v.getTag().toString());
            int orderType = orderTypeMap.get(viewNumber);
            int orderNumber = orderNumberMap.get(viewNumber);
            int innerNumber;
            if (orderType == 1) {
                JSONObject jQuery = (JSONObject) jUnMatched.opt(viewNumber - 1);
                showMoreInfo(jQuery, 1);
            } else if (orderType == 2) {
                JSONObject jOrder = (JSONObject) jHasMatched.opt(viewNumber - unMatchedNumber - 1);
                showMoreInfo(jOrder, 2);
            } else {
                JSONObject jOrder = (JSONObject) jHasDone.opt(viewNumber - unMatchedNumber - hasMatchedNumber - 1);
                showMoreInfo(jOrder, 3);
            }
        }catch(Exception e){throw new RuntimeException(e);}
    }

    public void showMoreInfo(JSONObject jOrder, int orderType){
        Intent intent = new Intent(OrderMainActivity.this, OrderUnitInfoActivity.class);
        intent.putExtra("order_type", orderType);
        intent.putExtra("order_info", jOrder.toString());
        intent.putExtra("user", user);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        mOrderTask = new OrderTask();
        mOrderTask.execute((Void) null);
    }

    public class OrderTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            InteractUtil interactUtil = new InteractUtil();
            String orderInfo = interactUtil.getOrderInfo(user);
            return orderInfo;
        }

        @Override
        protected void onPostExecute(final String orderInfo) {
            mOrderTask = null;
            //orderInfoString = orderInfo;
            initView(orderInfo);
            //refreshUI(orderInfo);
        }

        @Override
        protected void onCancelled() {
            mOrderTask = null;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 2){
            ToastUtil.show(mContext, "确认成功，请及时评价!");
            //finish();
        }
    }
}
