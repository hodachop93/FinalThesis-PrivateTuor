package com.hodachop93.hohoda.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hodachop93.hohoda.HohodaApplication;
import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.common.AppReferences;
import com.hodachop93.hohoda.model.Notification;
import com.hodachop93.hohoda.model.Profile;
import com.hodachop93.hohoda.utils.DateUtils;
import com.hodachop93.hohoda.utils.Utils;
import com.hodachop93.hohoda.view.CircleImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NotificationAdapter extends BaseLoadMoreAdapter<Notification> {

    private OnNotificationClickListener mListener;
    private Context mContext;

    public NotificationAdapter(List<Notification> data, RecyclerView recyclerView) {
        super(data, recyclerView);
        mContext = recyclerView.getContext();
        recyclerView.setAdapter(this);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_notification_item, parent, false);
        this.mContext = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final Notification notification) {
        if (!(holder instanceof ViewHolder)) {
            throw new ClassCastException();
        }
        ViewHolder viewHolder = (ViewHolder) holder;

        bindData(viewHolder, notification);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onNotificationClick(notification);
                }
            }
        });
    }

    private void bindData(ViewHolder viewHolder, Notification notification) {
        Picasso.with(mContext).load(notification.getProfile().getAvatarUrl())
                .placeholder(ContextCompat.getDrawable(mContext, R.drawable.ic_user_default))
                .into(viewHolder.imvAvatar);
        viewHolder.tvName.setText(notification.getProfile().getName());
        viewHolder.tvNotificationContent.setText(getNotification(notification));
        viewHolder.tvTime.setText(DateUtils.formatDateMessage(notification.getCreatedAt()));
        if (notification.getStatus() == Notification.STATUS_UNREAD) {
            viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.bg_notification_unread));
        } else {
            viewHolder.itemView.setBackgroundColor(Color.WHITE);
        }
    }

    private SpannableStringBuilder getNotification(Notification notification) {
        SpannableStringBuilder result = new SpannableStringBuilder("");

        switch (notification.getNotiType()) {

            case Notification.NOTI_TYPE_POST_COURSE:
                result.append(mContext.getString(R.string.noti_content_post_course));
                result.append(createSpannableString(notification.getCourse().getTitle()));
                break;
            case Notification.NOTI_TYPE_JOIN_COURSE:
                result.append(mContext.getString(R.string.noti_content_join_course));
                result.append(createSpannableString(notification.getCourse().getTitle()));
                break;
        }

        return result;
    }

    public void notifyItemChanged(Notification notification) {
        int index = data.indexOf(notification);
        notifyItemChanged(index);
    }

    public SpannableStringBuilder createSpannableString(String text) {
        SpannableStringBuilder spannableString = new SpannableStringBuilder(" " + text);
        ForegroundColorSpan span = new ForegroundColorSpan(ContextCompat.getColor(HohodaApplication.getInstance(), R.color.colorTextLink));
        spannableString.setSpan(span, 1, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }

    public void setOnNotificationClickListener(OnNotificationClickListener listener) {
        mListener = listener;
    }

    public interface OnNotificationClickListener {
        void onNotificationClick(Notification notification);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.imv_avatar)
        CircleImageView imvAvatar;
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_time)
        TextView tvTime;
        @Bind(R.id.tv_notification_content)
        TextView tvNotificationContent;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
