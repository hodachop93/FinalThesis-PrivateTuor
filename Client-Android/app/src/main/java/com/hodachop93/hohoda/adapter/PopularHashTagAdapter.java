package com.hodachop93.hohoda.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.model.HashTag;
import com.hodachop93.hohoda.view.CircleImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PopularHashTagAdapter extends RecyclerView.Adapter<PopularHashTagAdapter.ViewHolder> {
    private List<HashTag> mHashTagList;
    private Context mContext;
    private OnClickPopularHashTagListener mListener;

    public PopularHashTagAdapter(List<HashTag> hashTags, Context context) {
        this.mHashTagList = hashTags;
        this.mContext = context;
    }

    @Override
    public PopularHashTagAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_hash_tag, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PopularHashTagAdapter.ViewHolder holder, int position) {
        final HashTag hashTag = mHashTagList.get(position);

        Picasso.with(mContext).load(hashTag.getBackground()).into(holder.cimvAvatar);
        holder.tvTitle.setText(hashTag.getName());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onClickPopularHashTag(hashTag);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (mHashTagList == null) ? 0 : mHashTagList.size();
    }

    public interface OnClickPopularHashTagListener {
        void onClickPopularHashTag(@Nullable HashTag hashTag);
    }

    public void setListener(OnClickPopularHashTagListener listener) {
        this.mListener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.card_view)
        CardView cardView;
        @Bind(R.id.cimv_avatar)
        CircleImageView cimvAvatar;
        @Bind(R.id.tv_title)
        TextView tvTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
