package com.hodachop93.hohoda.fragment;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.activity.HohodaActivity;
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
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {
    @Bind(R.id.edt_user_name)
    EditText edtUsername;
    @Bind(R.id.edt_password)
    EditText edtPassword;
    @Bind(R.id.btn_sign_in)
    Button btnSignIn;
    @Bind(R.id.tv_forgot_password)
    TextView tvForgotPassword;
    @Bind(R.id.tv_sign_up)
    TextView tvSignUp;

    private ProgressDialog progressDialog;

    private Callback<HohodaResponse<User>> mCallbackSignIn = new Callback<HohodaResponse<User>>() {
        @Override
        public void onResponse(Call<HohodaResponse<User>> call, Response<HohodaResponse<User>> response) {
            if (progressDialog != null)
                progressDialog.dismiss();

            if (response.isSuccess()) {
                String status = response.body().getStatus();
                User user = response.body().getBody();
                if ("0".equals(status)) {
                    getActivity().finish();
                    AppReferences.setUserID(user.getUserID());
                    AppReferences.setUserTokenID(user.getTokenId());
                    Intent intent = HohodaActivity.getIntent(getActivity());
                    startActivity(intent);
                } else {
                    ErrorManager.handleApplicationException(getActivity(), response);
                }
            } else {
                ErrorManager.handleErroneousException(getActivity(), response);
            }
        }

        @Override
        public void onFailure(Call<HohodaResponse<User>> call, Throwable t) {
            if (progressDialog != null)
                progressDialog.dismiss();

            ErrorManager.handleNetworkException(getActivity());
        }
    };

    public SignInFragment() {
        // Required empty public constructor
    }

    public static SignInFragment newInstance() {
        return new SignInFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        ButterKnife.bind(this, view);
        ClickGuard.guard(btnSignIn);
        ClickGuard.guard(tvSignUp);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.btn_sign_in)
    public void signIn() {
        progressDialog = ProgressDialog.show(getActivity(), null, getString(R.string.signing_in));

        SignInRequestBody body = new SignInRequestBody();
        body.setAccountId(edtUsername.getText().toString().trim());
        body.setPassword(edtPassword.getText().toString());

        Call<HohodaResponse<User>> call = UserManagementApi.getInstance().signIn(body);
        call.enqueue(mCallbackSignIn);
    }


    @OnClick(R.id.tv_sign_up)
    public void signUp() {
        //TODO switch to sign up screen
    }

}
