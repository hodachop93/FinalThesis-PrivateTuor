package com.hodachop93.hohoda.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.api.usermanagement.ActivationUserRequestBody;
import com.hodachop93.hohoda.api.usermanagement.UserManagementApi;
import com.hodachop93.hohoda.common.AppReferences;
import com.hodachop93.hohoda.exception.ErrorManager;
import com.hodachop93.hohoda.model.HohodaResponse;
import com.hodachop93.hohoda.model.User;
import com.hodachop93.hohoda.view.NumberKeyBoardView;
import com.hodachop93.hohoda.view.OTPCodeEditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyCodeActivity extends BaseActivity implements View.OnClickListener, NumberKeyBoardView.KeyboardViewListener {

    private ProgressDialog mProgressDialog;

    @Bind(R.id.edt_otp_code)
    OTPCodeEditText edtOtpCode;
    @Bind(R.id.keyboard)
    NumberKeyBoardView keyBoardView;
    @Bind(R.id.tv_resend_code)
    TextView tvResendCode;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private String userId;

    private Callback<HohodaResponse<User>> mCallbackActivateOtpCode = new Callback<HohodaResponse<User>>() {
        @Override
        public void onResponse(Call<HohodaResponse<User>> call, Response<HohodaResponse<User>> response) {
            if (mProgressDialog != null)
                mProgressDialog.dismiss();

            if (response.isSuccess()) {
                String status = response.body().getStatus();
                User user = response.body().getBody();
                if ("0".equals(status)) {
                    finish();
                    AppReferences.setUserID(user.getUserID());
                    AppReferences.setUserTokenID(user.getTokenId());
                    Intent intent = HohodaActivity.getIntent(VerifyCodeActivity.this);
                    startActivity(intent);
                } else {
                    ErrorManager.handleApplicationException(VerifyCodeActivity.this, response);
                }
            } else {
                ErrorManager.handleErroneousException(VerifyCodeActivity.this, response);
            }
        }

        @Override
        public void onFailure(Call<HohodaResponse<User>> call, Throwable t) {
            if (mProgressDialog != null)
                mProgressDialog.dismiss();

            ErrorManager.handleNetworkException(VerifyCodeActivity.this);
        }
    };

    public static Intent getIntent(Context context, String userId) {
        Intent intent = new Intent(context, VerifyCodeActivity.class);
        intent.putExtra("USER_ID", userId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("USER_ID")) {
            userId = intent.getStringExtra("USER_ID");
        }

        setContentView(R.layout.activity_verify_code);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        keyBoardView.setKeyboardViewListener(this);
        tvResendCode.setOnClickListener(this);

        if (savedInstanceState != null) {
            userId = savedInstanceState.getString("USER_ID");
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            navigateToLogin();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == tvResendCode) {
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (userId != null) {
            outState.putString("USER_ID", userId);
        }
    }


    private void validateOTP() {
        String otpCode = edtOtpCode.getEnteredCode();
        if (otpCode != null && otpCode.length() == 6) {
            makeValidateOTPRequest();
        }
    }

    private void makeValidateOTPRequest() {
        mProgressDialog = ProgressDialog.show(this, null, getString(R.string.validating_otp));

        ActivationUserRequestBody body = new ActivationUserRequestBody();
        body.setUserId(userId);
        body.setOtp(edtOtpCode.getEnteredCode());

        Call<HohodaResponse<User>> call = UserManagementApi.getInstance().activate(body);
        call.enqueue(mCallbackActivateOtpCode);
    }

    private void navigateToHome() {
        Intent intent = HohodaActivity.getIntent(this);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
    }

    private void navigateToLogin() {
        Intent intent = SignInActivity.getIntent(this);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    @Override
    public void onBackPressed() {
        navigateToLogin();
    }

    @Override
    public void onDigitHit(int digit) {
        edtOtpCode.append(digit);

        if (edtOtpCode.isFinishedInput()) {
            validateOTP();
        }
    }

    @Override
    public void onCloseKeyboard() {
        edtOtpCode.clear();
    }

    @Override
    public void onBackSpace() {
        edtOtpCode.popBackDigit();
    }
}
