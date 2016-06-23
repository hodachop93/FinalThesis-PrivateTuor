package com.hodachop93.hohoda.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.api.usermanagement.LoginSocialRequestBody;
import com.hodachop93.hohoda.api.usermanagement.SignInRequestBody;
import com.hodachop93.hohoda.api.usermanagement.UserManagementApi;
import com.hodachop93.hohoda.common.AppReferences;
import com.hodachop93.hohoda.exception.ErrorManager;
import com.hodachop93.hohoda.model.HohodaResponse;
import com.hodachop93.hohoda.model.User;
import com.hodachop93.hohoda.utils.ClickGuard;

import org.json.JSONObject;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hop on 05/03/2016.
 */
public class SignInActivity extends BaseActivity implements FacebookCallback<LoginResult> {
    @Bind(R.id.edt_user_name)
    EditText edtUsername;
    @Bind(R.id.edt_password)
    EditText edtPassword;
    @Bind(R.id.btn_login)
    Button btnLogin;
    @Bind(R.id.tv_link_signup)
    TextView tvLinkSignup;
    @Bind(R.id.login_button)
    LoginButton loginButton;

    private ProgressDialog progressDialog;
    private CallbackManager mFacebookCallbackManager;

    private Callback<HohodaResponse<User>> mCallbackSignIn = new Callback<HohodaResponse<User>>() {
        @Override
        public void onResponse(Call<HohodaResponse<User>> call, Response<HohodaResponse<User>> response) {
            if (progressDialog != null)
                progressDialog.dismiss();

            if (response.isSuccess()) {
                String status = response.body().getStatus();
                User user = response.body().getBody();
                if ("0".equals(status)) {
                    finish();
                    AppReferences.setUserID(user.getUserID());
                    AppReferences.setUserTokenID(user.getTokenId());
                    Intent intent = HohodaActivity.getIntent(SignInActivity.this);
                    startActivity(intent);
                } else {
                    ErrorManager.handleApplicationException(SignInActivity.this, response);
                }
            } else {
                ErrorManager.handleErroneousException(SignInActivity.this, response);
            }
        }

        @Override
        public void onFailure(Call<HohodaResponse<User>> call, Throwable t) {
            if (progressDialog != null)
                progressDialog.dismiss();

            ErrorManager.handleNetworkException(SignInActivity.this);
        }
    };

    private Callback<HohodaResponse<User>> mCallbackLoginSocial = new Callback<HohodaResponse<User>>() {
        @Override
        public void onResponse(Call<HohodaResponse<User>> call, Response<HohodaResponse<User>> response) {
            if (progressDialog != null)
                progressDialog.dismiss();

            if (response.isSuccess()) {
                String status = response.body().getStatus();
                User user = response.body().getBody();
                if ("0".equals(status)) {
                    finish();
                    AppReferences.setUserID(user.getUserID());
                    AppReferences.setUserTokenID(user.getTokenId());
                    Intent intent = HohodaActivity.getIntent(SignInActivity.this);
                    startActivity(intent);
                } else {
                    ErrorManager.handleApplicationException(SignInActivity.this, response);
                }
            } else {
                ErrorManager.handleErroneousException(SignInActivity.this, response);
            }
        }

        @Override
        public void onFailure(Call<HohodaResponse<User>> call, Throwable t) {
            if (progressDialog != null)
                progressDialog.dismiss();

            ErrorManager.handleNetworkException(SignInActivity.this);
            LoginManager.getInstance().logOut();
        }
    };

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, SignInActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
        ClickGuard.guard(btnLogin);
        ClickGuard.guard(tvLinkSignup);

        String[] loginPermissions = {"public_profile,email"};
        loginButton.setReadPermissions(Arrays.asList(loginPermissions));

        if (mFacebookCallbackManager == null)
            mFacebookCallbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(mFacebookCallbackManager, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mFacebookCallbackManager != null)
            mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.btn_login)
    public void login() {
        if (edtUsername.getText().toString().isEmpty()
                || edtPassword.getText().toString().isEmpty()) {
            return;
        }
        progressDialog = ProgressDialog.show(this, null, getString(R.string.signing_in));

        SignInRequestBody body = new SignInRequestBody();
        body.setAccountId(edtUsername.getText().toString().trim());
        body.setPassword(edtPassword.getText().toString());

        Call<HohodaResponse<User>> call = UserManagementApi.getInstance().signIn(body);
        call.enqueue(mCallbackSignIn);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.tv_link_signup)
    public void moveToSignUpActivity() {
        finish();
        Intent intent = SignUpActivity.getIntent(this);
        startActivity(intent);
    }

    /*START - Facebook callback*/
    @Override
    public void onSuccess(LoginResult loginResult) {
        System.out.println("SUCCESS");
        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(this, null, getString(R.string.login_with_facebook));
        } else {
            progressDialog.setMessage(getString(R.string.login_with_facebook));
            progressDialog.show();
        }

        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        System.out.println("fuck");
                        System.out.println(object);
                        progressDialog.dismiss();

                        if (response.getError() == null) {

                            makeLoginFacebookRequest(object);

                        } else {
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,gender,email,picture.type(large)");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onCancel() {
        System.out.println("CANCEL");
    }

    @Override
    public void onError(FacebookException error) {
        System.out.println("ERROR");
        System.out.println(error);
    }
    /*END - Facebook callback*/

    private void makeLoginFacebookRequest(JSONObject object) {

        String fbId = object.optString("id");
        String name = object.optString("name");
        String email = object.optString("email");
        String gender = object.optString("gender");

        String avatarUrl;

        try {
            avatarUrl = object.optJSONObject("picture").optJSONObject("data").optString("url");
        } catch (NullPointerException e) {
            avatarUrl = null;
        }

        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(this, null, getString(R.string.login_with_facebook));
        } else {
            progressDialog.setMessage(getString(R.string.login_with_facebook));
            progressDialog.show();
        }

        LoginSocialRequestBody body = new LoginSocialRequestBody();
        body.setSocialId(fbId);
        body.setAccountType(User.ACCOUNT_TYPE_FACEBOOK);
        body.setAvatarUrl(avatarUrl);
        body.setEmail(email);
        body.setName(name);
        body.setGender(gender);

        Call<HohodaResponse<User>> call = UserManagementApi.getInstance().loginSocialNetwork(body);
        call.enqueue(mCallbackLoginSocial);
    }
}
