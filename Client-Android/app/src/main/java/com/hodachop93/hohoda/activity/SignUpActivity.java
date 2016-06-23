package com.hodachop93.hohoda.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.api.usermanagement.RegisterRequestBody;
import com.hodachop93.hohoda.api.usermanagement.SignInRequestBody;
import com.hodachop93.hohoda.api.usermanagement.UserManagementApi;
import com.hodachop93.hohoda.common.AppReferences;
import com.hodachop93.hohoda.exception.ErrorManager;
import com.hodachop93.hohoda.model.HohodaResponse;
import com.hodachop93.hohoda.model.User;
import com.hodachop93.hohoda.utils.ClickGuard;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hop on 05/03/2016.
 */
public class SignUpActivity extends BaseActivity {
    @Bind(R.id.edt_user_name)
    EditText edtUsername;
    @Bind(R.id.edt_email)
    EditText edtEmail;
    @Bind(R.id.edt_full_name)
    EditText edtFullName;
    @Bind(R.id.edt_password)
    EditText edtPassword;
    @Bind(R.id.btn_sign_up)
    Button btnSignUp;
    @Bind(R.id.tv_link_signin)
    TextView tvLinkSignIn;

    private ProgressDialog progressDialog;

    private Callback<HohodaResponse<User>> mCallbackSignUp = new Callback<HohodaResponse<User>>() {
        @Override
        public void onResponse(Call<HohodaResponse<User>> call, Response<HohodaResponse<User>> response) {
            if (progressDialog != null)
                progressDialog.dismiss();

            if (response.isSuccess()) { //(status 200-299)
                String status = response.body().getStatus();
                User User = response.body().getBody();
                if ("0".equals(status)) {
                    // Real success
                    finish();
                    Intent intent = VerifyCodeActivity.getIntent(SignUpActivity.this, User.getUserID());
                    startActivity(intent);
                } else {
                    // Hohoda Error
                    ErrorManager.handleApplicationException(SignUpActivity.this, response);
                }
            } else { //erroneous (status 400-599)
                // Server error
                ErrorManager.handleErroneousException(SignUpActivity.this, response);
            }
        }

        @Override
        public void onFailure(Call<HohodaResponse<User>> call, Throwable t) {
            if (progressDialog != null)
                progressDialog.dismiss();
            //No internet connection or  could not get any response from server
            ErrorManager.handleNetworkException(SignUpActivity.this);
        }
    };


    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, SignUpActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        ClickGuard.guard(btnSignUp);
        ClickGuard.guard(tvLinkSignIn);
    }

    @OnClick(R.id.btn_sign_up)
    public void signUp() {
        progressDialog = ProgressDialog.show(this, null, getString(R.string.signing_up));

        RegisterRequestBody body = new RegisterRequestBody();
        body.setEmail(edtEmail.getText().toString().trim());
        body.setPassword(edtPassword.getText().toString());
        body.setUserName(edtUsername.getText().toString().trim());
        body.setFullName(edtFullName.getText().toString().trim());

        Call<HohodaResponse<User>> call = UserManagementApi.getInstance().register(body);
        call.enqueue(mCallbackSignUp);
    }

    @OnClick(R.id.tv_link_signin)
    public void moveToSignUpActivity() {
        Intent intent = SignInActivity.getIntent(this);
        startActivity(intent);
        finish();
    }
}
