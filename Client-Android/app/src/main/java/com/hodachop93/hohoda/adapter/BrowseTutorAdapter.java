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
import com.hodachop93.hohoda.model.Profile;
import com.hodachop93.hohoda.utils.Utils;
import com.hodachop93.hohoda.view.CircleImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BrowseTutorAdapter extends BaseLoadMoreAdapter<Profile> {

    private OnProfileClickListener mListener;
    private Context mContext;

    public BrowseTutorAdapter(List<Profile> data, RecyclerView recyclerView) {
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final Profile profile) {
        if (!(holder instanceof ViewHolder)) {
            throw new ClassCastException();
        }
        final ViewHolder viewHolder = (ViewHolder) holder;

        Picasso.with(mContext).load(Utils.convertUrlIfUsingLocalhost(profile.getAvatarUrl()))
                .placeholder(ContextCompat.getDrawable(mContext, R.drawable.ic_user_default))
                .into(viewHolder.imvAvatar, new Callback() {
                    @Override
                    public void onSuccess() {
                        profile.setAvatar(viewHolder.imvAvatar.getDrawable());
                    }

                    @Override
                    public void onError() {
                        profile.setAvatar(ContextCompat.getDrawable(mContext, R.drawable.ic_user_default));
                    }
                });
        viewHolder.tvName.setText(profile.getName());
        viewHolder.ratingBar.setRating(profile.getRateAverage());
        if (profile.getAddress() == null || profile.getAddress().isEmpty()) {
            viewHolder.tvAddress.setVisibility(View.GONE);
        } else {
            viewHolder.tvAddress.setText(profile.getAddress());
            viewHolder.tvAddress.setVisibility(View.VISIBLE);
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onProfileClick(profile);
                }
            }
        });
        viewHolder.imvMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onMessageClick(profile);
                }
            }
        });
    }

    public void setOnProfileClickListener(OnProfileClickListener listener) {
        mListener = listener;
    }

    public interface OnProfileClickListener {
        void onProfileClick(Profile profile);

        void onMessageClick(Profile profile);
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
