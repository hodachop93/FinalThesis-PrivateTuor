package com.hodachop93.hohoda.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.activity.VerifyCodeActivity;
import com.hodachop93.hohoda.api.usermanagement.RegisterRequestBody;
import com.hodachop93.hohoda.api.usermanagement.UserManagementApi;
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


public class SignUpFragment extends BaseFragment implements Callback<HohodaResponse<User>> {
    @Bind(R.id.edt_email)
    EditText edtEmail;
    @Bind(R.id.edt_user_name)
    EditText edtUsername;
    @Bind(R.id.edt_password)
    EditText edtPassword;
    @Bind(R.id.chk_terms_condition)
    CheckBox chkTermsCondition;
    @Bind(R.id.btn_create_account)
    Button btnCreateAccount;
    @Bind(R.id.line_email_field)
    View lineEmailField;
    @Bind(R.id.line_password_field)
    View linePasswordField;
    @Bind((R.id.line_user_name_field))
    View lineUserNameField;
    @Bind(R.id.asterisk_email)
    TextView asteriskEmail;
    @Bind(R.id.asterisk_password)
    TextView asteriskPassword;
    @Bind(R.id.asterisk_user_name)
    TextView asteriskUserName;

    private ProgressDialog mProgressDialog;

    public SignUpFragment() {
        // Required empty public constructor
    }

    public static SignUpFragment newInstance() {
        return new SignUpFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        ButterKnife.bind(this, view);
        ClickGuard.guard(btnCreateAccount);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.chk_terms_condition)
    public void checkTermsCondition() {
        if (chkTermsCondition.isChecked()) {
            btnCreateAccount.setEnabled(true);
        } else {
            btnCreateAccount.setEnabled(false);
        }
    }

    @OnClick(R.id.btn_create_account)
    public void createAccount() {
        String userName = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString();
        String email = edtEmail.getText().toString().trim();

        notifyMandatoryField(edtUsername, userName.isEmpty());
        notifyMandatoryField(edtPassword, password.isEmpty());
        notifyMandatoryField(edtEmail, email.isEmpty());

        if (!email.isEmpty() && !userName.isEmpty() && !password.isEmpty()) {
            makeRegisterRequest();
        }
    }

    private void makeRegisterRequest() {
        mProgressDialog = ProgressDialog.show(getActivity(), null, getString(R.string.registering));

        RegisterRequestBody body = new RegisterRequestBody();
        body.setEmail(edtEmail.getText().toString().trim());
        body.setPassword(edtPassword.getText().toString());
        body.setUserName(edtUsername.getText().toString().trim());

        Call<HohodaResponse<User>> call = UserManagementApi.getInstance().register(body);
        call.enqueue(this);
    }

    /**
     * Show red line and * sign when field is empty
     *
     * @param field The current field
     * @param show  true if field is empty
     */
    private void notifyMandatoryField(EditText field, boolean show) {
        if (field == edtEmail) {
            lineEmailField.setSelected(show);
            asteriskEmail.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        } else if (field == edtUsername) {
            lineUserNameField.setSelected(show);
            asteriskUserName.setVisibility(show ? View.VISIBLE : View.GONE);
        } else if (field == edtPassword) {
            linePasswordField.setSelected(show);
            asteriskPassword.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onResponse(Call<HohodaResponse<User>> call, Response<HohodaResponse<User>> response) {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();

        if (response.isSuccess()) { //(status 200-299)
            String status = response.body().getStatus();
            User user = response.body().getBody();
            if ("0".equals(status)) {
                // Real success
                getActivity().finish();
                Intent intent = VerifyCodeActivity.getIntent(getActivity(), user.getUserID());
                startActivity(intent);
            } else {
                // Hohoda Error
                ErrorManager.handleApplicationException(getActivity(), response);
            }
        } else { //erroneous (status 400-599)
            // Server error
            ErrorManager.handleErroneousException(getActivity(), response);
        }
    }

    @Override
    public void onFailure(Call<HohodaResponse<User>> call, Throwable t) {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
        //No internet connection or  could not get any response from server
        ErrorManager.handleNetworkException(getActivity());
    }
}
