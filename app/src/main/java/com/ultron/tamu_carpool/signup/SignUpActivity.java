package com.ultron.tamu_carpool.signup;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ultron.tamu_carpool.R;
import com.ultron.tamu_carpool.util.InteractUtil;
import com.ultron.tamu_carpool.util.ToastUtil;

import org.apache.http.client.RequestDirector;

public class SignUpActivity extends AppCompatActivity
        implements View.OnClickListener, RadioGroup.OnCheckedChangeListener{

    private SignUpTask mSignUpTask = null;
    private AutoCompleteTextView mPhoneNumberView;
    private EditText mPasswordView;
    private EditText mPasswordViewAgain;
    private AutoCompleteTextView mLastnameView;
    private AutoCompleteTextView mFirstnameView;
    private View mSignUpView;
    private Button mSignUpButton;
    private RadioGroup mUserTypeRadioGroup;
    private RadioButton mPassengerBtn;
    private RadioButton mCarOwnerBtn;

    private String mPhoneNumber;
    private String mPassword;
    private int mUserType;
    private String mFirstname;
    private String mLastname;
    private String mPasswordAgain;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mContext = this.getApplicationContext();
        LinearLayout traceroute_rootview = (LinearLayout) findViewById(R.id.sign_up_traceroute_rootview);
        traceroute_rootview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        mSignUpView = findViewById(R.id.sign_up_form);
        mPhoneNumberView = (AutoCompleteTextView) findViewById(R.id.sign_up_phone_number);
        mPasswordView = (EditText) findViewById(R.id.sign_up_password);
        mPasswordViewAgain = (EditText) findViewById(R.id.sign_up_password_again);
        mFirstnameView = (AutoCompleteTextView) findViewById(R.id.sign_up_firstname);
        mLastnameView = (AutoCompleteTextView) findViewById(R.id.sign_up_lastname);
        mUserTypeRadioGroup = (RadioGroup)findViewById(R.id.user_type_select);
        mUserTypeRadioGroup.setOnCheckedChangeListener(this);
        mPassengerBtn = (RadioButton)findViewById(R.id.user_type_passenger);
        mCarOwnerBtn = (RadioButton)findViewById(R.id.user_type_carowner);
        mPassengerBtn.setChecked(true);
        mSignUpButton = (Button) findViewById(R.id.sign_up_submit);
        mSignUpButton.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.sign_up_submit:
                signUp();
                break;
        }
    }

    public void signUp(){
        if (mSignUpTask != null) {
            return;
        }
        mPhoneNumberView.setError(null);
        mPasswordView.setError(null);
        mPasswordViewAgain.setError(null);
        mFirstnameView.setError(null);
        mLastnameView.setError(null);

        String phoneNumber = mPhoneNumberView.getText().toString();
        String password = mPasswordView.getText().toString();
        String passwordAgain = mPasswordViewAgain.getText().toString();
        int userType = mUserType;
        String firstname = mFirstnameView.getText().toString();
        String lastname = mLastnameView.getText().toString();
        View focusView = null;
        boolean cancel = false;

        if (TextUtils.isEmpty(password)){
            mPasswordView.setError("密码不能为空");
            focusView = mPasswordView;
            cancel = true;
        }
        else if (!isPasswordValid(password)){
            mPasswordView.setError("密码要长于6位");
            focusView = mPasswordView;
            cancel = true;
        }
        else if (!password.equals(passwordAgain)){
            mPasswordViewAgain.setError("两次输入密码不一致");
            focusView = mPasswordViewAgain;
            cancel = true;
        }

        if (TextUtils.isEmpty(phoneNumber)){
            mPhoneNumberView.setError("手机号不能为空");
            focusView = mPhoneNumberView;
            cancel = true;
        }
        else if (!isPhoneNumberValid(phoneNumber)){
            mPhoneNumberView.setError("手机号必须为11位");
            focusView = mPhoneNumberView;
            cancel = true;

        }

        if (TextUtils.isEmpty(firstname)){
            mFirstnameView.setError("名不能为空");
            focusView = mFirstnameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(lastname)){
            mLastnameView.setError("姓不能为空");
            focusView = mLastnameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mSignUpTask = new SignUpTask(phoneNumber, password, userType, firstname, lastname);
            mSignUpTask.execute((Void) null);
        }

    }

    private boolean isPhoneNumberValid(String phone_number) {
        return phone_number.length() == 11;
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == mPassengerBtn.getId()){
            mUserType = 1;
        }
        else{
            mUserType = 2;
        }
    }

    public class SignUpTask extends AsyncTask<Void, Void, Integer> {

        private String phoneNumber;
        private String password;
        private int userType;
        private String firstName;
        private String lastName;

        SignUpTask(String pn, String pw, int ut, String ftn, String ltn) {
            phoneNumber= pn;
            password = pw;
            userType = ut;
            firstName = ftn;
            lastName = ltn;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            InteractUtil interactUtil = new InteractUtil();
            int code = interactUtil.signUp(phoneNumber, password, userType, firstName, lastName);
            return code;
        }

        @Override
        protected void onPostExecute(final Integer code) {
            mSignUpTask = null;
            switch (code){
                case 0://用户名已注册
                    mPhoneNumberView.setError("手机号已注册");
                    break;
                case 1://ok
                    Intent intent = getIntent();
                    setResult(1);
                    intent.putExtra("phone_number", mPhoneNumber);
                    intent.putExtra("password", mPassword);
                    ToastUtil.show(mContext, "注册成功，正在自动登录");
                    finish();
                    break;
            }
        }

        @Override
        protected void onCancelled() {
            mSignUpTask = null;
        }

    }
}
