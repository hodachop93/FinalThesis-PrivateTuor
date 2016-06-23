package com.hodachop93.hohoda.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.common.AppReferences;
import com.hodachop93.hohoda.model.Conversation;
import com.hodachop93.hohoda.model.Profile;
import com.hodachop93.hohoda.utils.Utils;
import com.hodachop93.hohoda.view.CircleImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ConversationAdapter extends BaseLoadMoreAdapter<Conversation> {

    private OnConversationClickListener mListener;
    private Context mContext;

    public ConversationAdapter(List<Conversation> data, RecyclerView recyclerView) {
        super(data, recyclerView);
        mContext = recyclerView.getContext();
        recyclerView.setAdapter(this);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_conversation_item, parent, false);
        this.mContext = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final Conversation conversation) {
        if (!(holder instanceof ViewHolder)) {
            throw new ClassCastException();
        }
        ViewHolder viewHolder = (ViewHolder) holder;

        if (conversation.getMemberOne().equals(AppReferences.getUserID())) {
            bindData(viewHolder, conversation.getPersonTwo());
        } else {
            bindData(viewHolder, conversation.getPersonOne());
        }
        viewHolder.tvLastMessage.setText(conversation.getLastMessage());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onConversationClick(conversation);
                }
            }
        });
    }

    private void bindData(ViewHolder viewHolder, Profile partner) {
        Picasso.with(mContext).load(Utils.convertUrlIfUsingLocalhost(partner.getAvatarUrl()))
                .placeholder(ContextCompat.getDrawable(mContext, R.drawable.ic_user_default))
                .into(viewHolder.imvAvatar);
        viewHolder.tvName.setText(partner.getName());
    }

    public void setOnConversationClickListener(OnConversationClickListener listener) {
        mListener = listener;
    }

    public interface OnConversationClickListener {
        void onConversationClick(Conversation conversation);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.imv_avatar)
        CircleImageView imvAvatar;
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_last_message)
        TextView tvLastMessage;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
