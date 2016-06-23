package com.hodachop93.hohoda.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.model.Candidate;
import com.hodachop93.hohoda.utils.Utils;
import com.hodachop93.hohoda.view.CircleImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Hop Dac Ho on 08/05/2016.
 */
public class CandidateAdapter extends BaseLoadMoreAdapter<Candidate> {

    private Context mContext;
    private OnCandidateClickListener mListener;

    public CandidateAdapter(List<Candidate> data, RecyclerView recyclerView) {
        super(data, recyclerView);
        recyclerView.setAdapter(this);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dapter_browse_tutor_item, parent, false);
        this.mContext = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final Candidate candidate) {
        if (!(holder instanceof ViewHolder)) {
            throw new ClassCastException();
        }
        ViewHolder viewHolder = (ViewHolder) holder;

        Picasso.with(mContext).load(Utils.convertUrlIfUsingLocalhost(candidate.getProfile().getAvatarUrl()))
                .placeholder(ContextCompat.getDrawable(mContext, R.drawable.ic_user_default))
                .into(viewHolder.imvAvatar);
        viewHolder.tvName.setText(candidate.getProfile().getName());
        viewHolder.ratingBar.setRating(candidate.getProfile().getRateAverage());
        if (candidate.getProfile().getAddress() == null ||
                candidate.getProfile().getAddress().isEmpty()) {
            viewHolder.tvAddress.setVisibility(View.GONE);
        } else {
            viewHolder.tvAddress.setText(candidate.getProfile().getAddress());
            viewHolder.tvAddress.setVisibility(View.VISIBLE);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onProfileClick(candidate);
                }
            }
        });
        viewHolder.imvMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onMessageClick(candidate);
                }
            }
        });
    }

    public void setOnCandidateClickListener(OnCandidateClickListener listener) {
        mListener = listener;
    }

    public interface OnCandidateClickListener {
        void onProfileClick(Candidate candidate);

        void onMessageClick(Candidate candidate);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.imv_avatar)
        CircleImageView imvAvatar;
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.rating_bar)
        RatingBar ratingBar;
        @Bind(R.id.tv_address)
        TextView tvAddress;
        @Bind(R.id.imv_message)
        ImageView imvMessage;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
