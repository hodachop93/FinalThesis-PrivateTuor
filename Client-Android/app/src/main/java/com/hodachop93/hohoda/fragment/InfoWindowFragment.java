package com.hodachop93.hohoda.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.model.Profile;
import com.hodachop93.hohoda.utils.ClickGuard;
import com.hodachop93.hohoda.view.CircleImageView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by hodachop93 on 29/01/2016.
 */
public class InfoWindowFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.imv_avatar)
    CircleImageView imvAvatar;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.rating_bar)
    RatingBar ratingBar;
    @Bind(R.id.tv_address)
    TextView tvAddress;
    @Bind(R.id.tv_current_page)
    TextView tvCurrentPage;

    private Profile mTutor;

    public InfoWindowFragment() {
    }

    public static InfoWindowFragment newInstance(Profile tutor, int current, int total) {
        InfoWindowFragment fragment = new InfoWindowFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("TUTOR", tutor);
        bundle.putInt("CURRENT", current);
        bundle.putInt("TOTAL", total);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info_window, container, false);
        ButterKnife.bind(this, view);
        view.setOnClickListener(this);

        Bundle bundle = getArguments();
        mTutor = bundle.getParcelable("TUTOR");
        int current = bundle.getInt("CURRENT") + 1;
        int total = bundle.getInt("TOTAL");

        imvAvatar.setImageDrawable(mTutor.getAvatar());
        tvName.setText(mTutor.getName());
        ratingBar.setRating(mTutor.getRateAverage());
        tvCurrentPage.setText(current + "/" + total);
        tvAddress.setText(mTutor.getAddress());

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @Override
    public void onClick(View v) {
        //TODO Open profile detail activity
    }
}
