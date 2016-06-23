package com.hodachop93.hohoda.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.hodachop93.hohoda.R;

import java.util.List;

public abstract class BaseLoadMoreAdapter<T> extends RecyclerView.Adapter {

    public static final int VIEW_ITEM = 0;
    public static final int VIEW_PROGRESS = 1;

    protected List<T> data;
    private int visibleThreshold = 1;
    private int lastVisibleItem, totalItemCount;
    private boolean loading, mLoadMoreEnabled = false;

    private LoadMoreListener mLoadMoreListener;

    /**
     * Constructor for LoadMoreAdapter
     *
     * @param data         data of adapter
     * @param recyclerView recyclerView to be attached
     */
    public BaseLoadMoreAdapter(List<T> data, RecyclerView recyclerView) {
        this.data = data;

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

                                if (mLoadMoreListener !=null){
                                    mLoadMoreListener.onLoadMore();
                                }
                            }
                        }
                    });
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            return onCreateViewHolder(parent);
        } else {
            View progressView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_progress, parent, false);
            return new ProgressViewHolder(progressView);
        }
    }

    public abstract RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent);

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ProgressViewHolder) {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        } else {
            onBindViewHolder(holder, getItem(position));
        }
    }

    /**
     * Called when bind view holder
     *
     * @param holder item view holder
     * @param item   data of item
     */
    public abstract void onBindViewHolder(RecyclerView.ViewHolder holder, T item);

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    /**
     * @return get List of data in adapter
     */
    public
    @Nullable
    List<T> getData() {
        return data;
    }

    /**
     * Set data for adapter
     *
     * @param data data to set
     */
    public void setData(List<T> data) {
        this.data = data;
    }

    public void setData(List<T> data, boolean notify){
        setData(data);
        if (notify)
            this.notifyItemRangeInserted(0, this.data.size());
    }

    /**
     * Clear all data, not notify data set changed
     */
    public void clear() {
        if (data != null) {
            data.clear();
        }
    }

    /**
     * Clear the adapter data
     *
     * @param notify true if need to notify data set changed
     */
    public void clear(boolean notify) {

        int itemCount = getItemCount();
        clear();

        if (notify) {
            notifyItemRangeRemoved(0, itemCount);
        }
    }

    /**
     * Add data to adapter (new data or additional data) and notify item range inserted
     * @param newData The new data
     */
    public void addData(List<T> newData) {

        int lastPosition = getItemCount();

        if (data == null)
            data = newData;
        else
            data.addAll(newData);

        notifyItemRangeInserted(lastPosition, getItemCount());
    }

    @Nullable
    public T getItem(int position) {
        if (data == null || position < 0) return null;
        return data.get(position);
    }

    public boolean isEmpty() {
        return data == null || data.isEmpty();
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position) != null ? VIEW_ITEM : VIEW_PROGRESS;
    }

    /**
     * Get the last item
     * @return The last item
     */
    public T getLastItem() {

        if (isEmpty())
            return null;

        if (loading)
            return getItem(getItemCount() - 2);
        return getItem(getItemCount() - 1);
    }

    private void setLoadMore(boolean loadMore) {

        if (loadMore) {
            if (data != null && getItem(getItemCount() - 1) != null) {
                data.add(null);
                notifyItemInserted(getItemCount());
            }
        } else {
            if (!isEmpty() && getItem(getItemCount() - 1) == null) {
                data.remove(getItemCount() - 1);
                notifyItemRemoved(getItemCount());
            }
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
     * Call when load-more data finished
     */
    public void finishLoadMore() {
        if (loading) {
            loading = false;
            setLoadMore(false);
        }
    }

    /**
     * Remove an item at a particular position
     * @param position
     */
    public void removeItem(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * Set callback when load-more
     *
     * @param loadMoreListener
     */
    public void setLoadMoreListener(LoadMoreListener loadMoreListener) {
        this.mLoadMoreListener = loadMoreListener;
    }


    public static class ProgressViewHolder extends RecyclerView.ViewHolder {

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