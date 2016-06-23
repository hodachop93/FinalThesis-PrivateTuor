package com.hodachop93.hohoda.adapter;

import android.content.Context;
import android.content.pm.FeatureInfo;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.common.AppReferences;
import com.hodachop93.hohoda.model.Conversation;
import com.hodachop93.hohoda.model.Message;
import com.hodachop93.hohoda.utils.DateUtils;
import com.hodachop93.hohoda.utils.Utils;
import com.hodachop93.hohoda.view.CircleImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by hopho on 04/05/2016.
 */
public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<Message> mData;
    private String mUserId;
    private String mUserAvatarUrl;
    private String mPartnerAvatarUrl;
    private RecyclerView mRecyclerView;

    private final static int SELF = 0;
    private final static int OTHER = 1;
    private static final int PROGRESS = 2;

    private int visibleThreshold = 10;
    private int lastVisibleItem, totalItemCount;
    private boolean loading, mLoadMoreEnabled = false;

    private LoadMoreListener mLoadMoreListener;


    public MessageAdapter(Context mContext, List<Message> data, String userId, RecyclerView recyclerView) {
        this.mContext = mContext;
        this.mData = data;
        this.mUserId = userId;
        this.mRecyclerView = recyclerView;
        mRecyclerView.setAdapter(this);

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();


            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            if (!mLoadMoreEnabled || isEmpty()) {
                                return;
                            }

                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager
                                    .findLastVisibleItemPosition();
                            if (!loading
                                    && totalItemCount <= (lastVisibleItem + visibleThreshold)) {

                                setLoadMore(true);
                                loading = true;

                                if (mLoadMoreListener != null) {
                                    mLoadMoreListener.onLoadMore();
                                }
                            }
                        }
                    });
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        // view type is to identify where to render the chat message
        // left or right
        if (viewType == SELF) {
            // self message
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_self, parent, false);
            return new ViewHolder(itemView);
        } else if (viewType == OTHER) {
            // others message
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_other, parent, false);
            return new ViewHolder(itemView);
        } else {
            View progressView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_progress, parent, false);
            return new ProgressViewHolder(progressView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ProgressViewHolder) {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        } else {
            Message message = mData.get(position);

            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.tvMessage.setText(message.getContent());


            if (position == 0) {
                //First position
                viewHolder.tvTimeStamp.setText(DateUtils.formatDateMessage(message.getCreatedAt()));
                viewHolder.tvTimeStamp.setVisibility(View.VISIBLE);
                if (getItemCount() == 1) {
                    visibleAvatar(viewHolder, message);
                } else if (getItemViewType(position) != getItemViewType(position + 1)) {
                    visibleAvatar(viewHolder, message);
                } else {
                    invisibleAvatar(viewHolder);
                }
            } else {
                if (getItemViewType(position) != getItemViewType(position - 1)) {
                    viewHolder.tvTimeStamp.setText(DateUtils.formatDateMessage(message.getCreatedAt()));
                    viewHolder.tvTimeStamp.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.tvTimeStamp.setVisibility(View.GONE);
                }

                if ((position < mData.size() - 1) && (getItemViewType(position) != getItemViewType(position + 1))) {
                    visibleAvatar(viewHolder, message);
                } else {
                    invisibleAvatar(viewHolder);
                }
            }

            if (position == mData.size() - 1) {
                visibleAvatar(viewHolder, message);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mData.get(position) == null) {
            return PROGRESS;
        } else {
            Message message = mData.get(position);
            if (message.getUserId().equals(AppReferences.getUserID())) {
                return SELF;
            }
            return OTHER;
        }
    }

    private void visibleAvatar(ViewHolder viewHolder, Message message) {
        Picasso.with(mContext).load(Utils.convertUrlIfUsingLocalhost(message.getUserId().equals(mUserId) ? mUserAvatarUrl : mPartnerAvatarUrl))
                .placeholder(ContextCompat.getDrawable(mContext, R.drawable.ic_user_default))
                .into(viewHolder.imvAvatar);
        viewHolder.imvAvatar.setVisibility(View.VISIBLE);
    }

    private void invisibleAvatar(ViewHolder viewHolder) {
        viewHolder.imvAvatar.setVisibility(View.INVISIBLE);
    }

    public void addData(int location, Message item) {
        mData.add(location, item);
        this.notifyItemInserted(location);
        this.notifyItemChanged(location + 1);
        mRecyclerView.scrollToPosition(location);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public boolean isEmpty() {
        return mData == null || mData.isEmpty();
    }

    public Message getItem(int position) {
        if (mData == null || position < 0) return null;
        return mData.get(position);
    }

    public void setConversation(Conversation conversation) {
        if (conversation.getMemberOne().equals(mUserId)) {
            mUserAvatarUrl = conversation.getPersonOne().getAvatarUrl();
            mPartnerAvatarUrl = conversation.getPersonTwo().getAvatarUrl();
        } else {
            mUserAvatarUrl = conversation.getPersonTwo().getAvatarUrl();
            mPartnerAvatarUrl = conversation.getPersonOne().getAvatarUrl();
        }
    }

    public void finishLoadMore() {
        if (loading) {
            loading = false;
            setLoadMore(false);
        }
    }

    /**
     * Enabled or disabled load-more function
     *
     * @param enabled true to enabled load-more
     *                false to disabled
     */
    public void setLoadMoreEnabled(boolean enabled) {
        this.mLoadMoreEnabled = enabled;
    }

    /**
     * Get the last item
     *
     * @return The last item
     */
    public Message getLastItem() {
        if (isEmpty())
            return null;

        if (loading)
            return getItem(getItemCount() - 2);
        return getItem(getItemCount() - 1);
    }

    private void setLoadMore(boolean loadMore) {

        if (loadMore) {
            if (mData != null && getItem(getItemCount() - 1) != null) {
                mData.add(null);
                notifyItemInserted(getItemCount());
            }
        } else {
            if (!isEmpty() && getItem(getItemCount() - 1) == null) {
                mData.remove(getItemCount() - 1);
                notifyItemRemoved(getItemCount());
            }
        }
    }

    public void setData(List<Message> data, boolean notify) {
        this.mData = data;
        if (notify)
            this.notifyItemRangeInserted(0, mData.size());
    }

    public void clear() {
        int itemCount = getItemCount();
        if (mData != null) {
            mData.clear();
            notifyItemRangeRemoved(0, itemCount);
        }
    }

    public void addData(List<Message> newData) {

        int lastPosition = getItemCount();

        if (mData == null)
            mData = newData;
        else
            mData.addAll(newData);

        notifyItemRangeInserted(lastPosition, getItemCount());
    }

    /**
     * Set callback when load-more
     *
     * @param loadMoreListener
     */
    public void setLoadMoreListener(LoadMoreListener loadMoreListener) {
        this.mLoadMoreListener = loadMoreListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.imv_avatar)
        CircleImageView imvAvatar;
        @Bind(R.id.message)
        TextView tvMessage;
        @Bind(R.id.timestamp)
        TextView tvTimeStamp;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class ProgressViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public ProgressViewHolder(View itemView) {
            super(itemView);

            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
        }
    }

    public interface LoadMoreListener {
        void onLoadMore();
    }
}
