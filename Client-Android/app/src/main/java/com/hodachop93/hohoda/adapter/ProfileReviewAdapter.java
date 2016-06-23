package com.hodachop93.hohoda.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.model.Review;
import com.hodachop93.hohoda.utils.DateUtils;
import com.hodachop93.hohoda.view.CircleImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProfileReviewAdapter extends BaseLoadMoreAdapter<Review> {

    private Context mContext;

    public ProfileReviewAdapter(List<Review> data, RecyclerView recyclerView) {
        super(data, recyclerView);

        recyclerView.setAdapter(this);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_review_item, parent, false);
        this.mContext = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final Review review) {
        if (!(holder instanceof ViewHolder)) {
            throw new ClassCastException();
        }
        ViewHolder viewHolder = (ViewHolder) holder;

        Picasso.with(mContext).load(review.getReviewer().getAvatarUrl())
                .placeholder(ContextCompat.getDrawable(mContext, R.drawable.ic_user_default))
                .into(viewHolder.imvAvatar);
        viewHolder.tvName.setText(review.getReviewer().getName());
        viewHolder.tvComment.setText(review.getComment());
        viewHolder.tvTimeAgo.setText(DateUtils.getTimeAgo(review.getCreatedAt()));
        viewHolder.ratingBar.setRating(review.getRate());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.imv_avatar)
        CircleImageView imvAvatar;
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_time_ago)
        TextView tvTimeAgo;
        @Bind(R.id.rating_bar)
        RatingBar ratingBar;
        @Bind(R.id.tv_comment)
        TextView tvComment;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
