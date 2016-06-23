package com.hodachop93.hohoda.dialog;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.api.profile.ProfileApi;
import com.hodachop93.hohoda.eventbus.ProfileEventBus;
import com.hodachop93.hohoda.exception.ErrorManager;
import com.hodachop93.hohoda.model.HohodaResponse;
import com.hodachop93.hohoda.model.Profile;
import com.hodachop93.hohoda.utils.MessageUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ReviewDialog extends DialogFragment {

    @Bind(R.id.btn_cancel)
    Button btnCancel;
    @Bind(R.id.btn_submit)
    Button btnSubmit;
    @Bind(R.id.edt_comments)
    EditText edtComment;
    @Bind(R.id.rating_bar)
    RatingBar ratingBar;

    ProgressDialog progressDialog;
    private String mToUserId;

    private Callback<HohodaResponse<Object>> mCallbackReview = new Callback<HohodaResponse<Object>>() {
        @Override
        public void onResponse(Call<HohodaResponse<Object>> call, Response<HohodaResponse<Object>> response) {
            progressDialog.dismiss();

            if (response.isSuccess()) {
                String status = response.body().getStatus();
                if ("0".equals(status)) {
                    Toast.makeText(getActivity(), R.string.tks_for_review, Toast.LENGTH_SHORT).show();
                    ProfileEventBus.getInstance().post(new ProfileEventBus.ReloadProfileEvent());
                    dismiss();
                } else {
                    ErrorManager.handleApplicationException(getActivity(), response);
                }
            } else {
                ErrorManager.handleErroneousException(getActivity(), response);
            }
        }

        @Override
        public void onFailure(Call<HohodaResponse<Object>> call, Throwable t) {
            progressDialog.dismiss();
            ErrorManager.handleNetworkException(getActivity());
        }
    };

    private static final int MIN_COMMENT_LENGTH = 1;

    public ReviewDialog() {
    }

    public static ReviewDialog newInstance(String toUserId) {
        Bundle bundle = new Bundle();
        bundle.putString("TO_USER_ID", toUserId);
        ReviewDialog dialog = new ReviewDialog();
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mToUserId = getArguments().getString("TO_USER_ID");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View v = inflater.inflate(R.layout.dialog_review, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @OnClick(R.id.btn_submit)
    public void submit() {
        submitReview();
    }

    @OnClick(R.id.btn_cancel)
    public void cancel() {
        dismiss();
    }

    private void submitReview() {
        String comment = edtComment.getText().toString().trim();

        if (comment.length() < MIN_COMMENT_LENGTH) {
            MessageUtils.showInformationMessage(getActivity(), R.string.label_error, R.string.comment_not_be_empty);
        } else {
            makeReviewRequest();
        }
    }

    private void makeReviewRequest() {
        progressDialog = ProgressDialog.show(getActivity(), null, getString(R.string.loading));

        Call<HohodaResponse<Object>> call = ProfileApi.getInstance().addReview(mToUserId, ratingBar.getRating(), edtComment.getText().toString().trim());
        call.enqueue(mCallbackReview);
    }
}
