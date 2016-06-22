package com.ultron.tamu_carpool.personalinfo;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.ultron.tamu_carpool.R;
import com.ultron.tamu_carpool.usr.User;
import com.ultron.tamu_carpool.util.InteractUtil;
import com.ultron.tamu_carpool.util.ToastUtil;

public class CompleteActivity extends AppCompatActivity
    implements View.OnClickListener{

    private User mUser;
    private LinearLayout mCompleteForm;
    private EditText mIdNumberView;
    private EditText mDriveIdNumberView;
    private EditText mNumberPlateView;
    private EditText mCarTypeView;
    private EditText mMaxPsgView;
    private Button mCompleteSubmitButton;

    private String mIdNumber = "";
    private String mDriveIdNumber = "";
    private String mCarType = "";
    private int mMaxPsg = 1;
    private String mNumberPlate = "";

    private Context mContext;

    private CompleteTask mCompleteTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete);
        mContext = getBaseContext();
        initView();
    }

    public void initView(){
        Intent faIntent = getIntent();
        mUser = (User)faIntent.getSerializableExtra("user");
        mCompleteSubmitButton = (Button) findViewById(R.id.complete_submit);
        mCompleteSubmitButton.setOnClickListener(this);
        mIdNumberView = (EditText) findViewById(R.id.complete_idnumber);
        if (mUser.getUserType() == 2){
            mDriveIdNumberView = (EditText) findViewById(R.id.complete_drive_idnumber);
            mNumberPlateView = (EditText) findViewById(R.id.complete_number_plate);
            mCarTypeView = (EditText) findViewById(R.id.complete_cartype);
            mMaxPsgView = (EditText) findViewById(R.id.complete_maxpsg);

            mDriveIdNumberView.setVisibility(View.VISIBLE);
            mNumberPlateView.setVisibility(View.VISIBLE);
            mCarTypeView.setVisibility(View.VISIBLE);
            mMaxPsgView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.complete_submit:
                goForComplete();
        }
    }

    public void goForComplete(){
        mIdNumber = mIdNumberView.getText().toString();
        if (mIdNumber == null){
            mIdNumberView.setError("身份证号不能为空");
            return;
        }
        else{
            if (mIdNumber.length() != 18){
                mIdNumberView.setError("身份证号格式有误");
                return;
            }
        }
        mDriveIdNumber = mDriveIdNumberView.getText().toString();
        if (mDriveIdNumber == null){
            mDriveIdNumberView.setError("驾驶证号不能为空");
            return;
        }
        mCarType = mCarTypeView.getText().toString();
        if (mCarType == null){
            mCarTypeView.setError("车型不能为空");
            return;
        }
        String mMaxPsgString = mMaxPsgView.getText().toString();
        if (mMaxPsgString == null){
            mMaxPsgView.setError("最大载客数不能为空");
            return;
        }
        mNumberPlate = mNumberPlateView.getText().toString();
        if (mNumberPlate == null){
            mNumberPlateView.setError("车牌号不能为空");
            return;
        }
        mCompleteTask = new CompleteTask(mUser, mIdNumber, mDriveIdNumber, mCarType, mMaxPsg, mNumberPlate);
        mCompleteTask.execute((Void) null);
    }

    public class CompleteTask extends AsyncTask<Void, Void, Boolean> {

        private User user;
        private String idNumber;
        private String driveIdNumber;
        private String carType;
        private int maxPsg;
        private String numberPlate;

        CompleteTask(User usr, String id, String did, String ct, int mpsg, String np) {
            user = usr;
            idNumber = id;
            driveIdNumber = did;
            carType = ct;
            maxPsg = mpsg;
            numberPlate = np;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean flag = true;
            try {
                InteractUtil interactUtil = new InteractUtil();
                interactUtil.completeInfo(user, idNumber, driveIdNumber, carType, maxPsg, numberPlate);
            }catch(Exception e){flag = false;}
            return flag;
        }

        @Override
        protected void onPostExecute(final Boolean flag) {
            mCompleteTask = null;
            ToastUtil.show(mContext, "修改成功");
            finish();
        }

        @Override
        protected void onCancelled() {
            mCompleteTask = null;
        }

    }
}
