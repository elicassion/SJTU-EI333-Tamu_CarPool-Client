package com.ultron.tamu_carpool.order;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.ultron.tamu_carpool.R;
import com.ultron.tamu_carpool.util.ToastUtil;

import org.json.JSONObject;

public class OrderMainActivity extends AppCompatActivity {




    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements View.OnClickListener{
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private String[] mUnMatched = {"bertlps", "archer", "jarvisxx", "elicassion"};
        private String[] mHasMatched = {"sb1", "sb2", "sb3"};
        private String[] mHasDone = {"London", "Manila", "Iceland"};

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
                View rootView = inflater.inflate(R.layout.fragment_order_main, container, false);
                ViewGroup detailsView = (ViewGroup) rootView.findViewById (R.id.order_detail_select);
                switch (getArguments().getInt(ARG_SECTION_NUMBER)){
                    case 1:
                        for (int i = 0; i < mUnMatched.length; ++i){
                            LinearLayout linearLayout = newLinearLayout(i);
                            final TextView mTextView = (TextView) linearLayout.findViewById(R.id.order_detail_text);
                            mTextView.setText(mUnMatched[i]);
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                            detailsView.addView(linearLayout, layoutParams);
                        }
                        break;
                    case 2:
                        for (int i = 0; i < mHasMatched.length; ++i){
                            LinearLayout linearLayout = newLinearLayout(i);
                            final TextView mTextView = (TextView) linearLayout.findViewById(R.id.order_detail_text);
                            mTextView.setText(mHasMatched[i]);
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                            detailsView.addView(linearLayout, layoutParams);
                        }
                        break;
                    case 3:
                        for (int i = 0; i < mHasDone.length; ++i){
                            LinearLayout linearLayout = newLinearLayout(i);
                            final TextView mTextView = (TextView) linearLayout.findViewById(R.id.order_detail_text);
                            mTextView.setText(mHasDone[i]);
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                            detailsView.addView(linearLayout, layoutParams);
                        }
                        break;
                }
//            View rootView = inflater.inflate(R.layout.fragment_order_main, container, false);
//            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER))+"\n"+getString(R.string.large_text));
            return rootView;
        }

        @Override
        public void onClick(View v) {
            showMoreInfo(getArguments().getInt(ARG_SECTION_NUMBER),(int)v.getTag());
        }


        public LinearLayout newLinearLayout(int i){
            LinearLayout linearLayout = new LinearLayout(getActivity());
            View.inflate(getActivity(), R.layout.content_order_detail, linearLayout);
            linearLayout.setClickable(true);
            linearLayout.setFocusable(true);
            final View view = linearLayout.getChildAt(0);
            view.setTag(i+1);
            view.setOnClickListener(this);
            return linearLayout;
        }

        public void showMoreInfo(int orderType, int tag) {
            //TODO: link to orderunitinfo activity
            ToastUtil.show(getActivity(), "你点我"+"老"+Integer.toString(orderType)+"的"+Integer.toString(tag)+"儿子");
            Intent intent = new Intent(getActivity(), OrderUnitInfoActivity.class);
            intent.putExtra("order_type", orderType);
            String orderInfo = null;
            switch (orderType){
                case 1: orderInfo = mUnMatched[tag-1];break;
                case 2: orderInfo = mHasMatched[tag-1];break;
                case 3: orderInfo = mHasDone[tag-1];break;
            }
            intent.putExtra("order_info", orderInfo);
            startActivity(intent);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "未匹配";
                case 1:
                    return "正在进行";
                case 2:
                    return "已完成";
            }
            return null;
        }
    }
}
