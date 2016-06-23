package com.hodachop93.hohoda.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.activity.ChatActivity;
import com.hodachop93.hohoda.adapter.BaseLoadMoreAdapter;
import com.hodachop93.hohoda.adapter.ConversationAdapter;
import com.hodachop93.hohoda.api.chat.ChatApi;
import com.hodachop93.hohoda.common.AppReferences;
import com.hodachop93.hohoda.exception.ErrorManager;
import com.hodachop93.hohoda.model.Conversation;
import com.hodachop93.hohoda.model.HohodaResponse;
import com.hodachop93.hohoda.view.DividerItemDecoration;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ConversationListFragment extends BaseFragment implements ConversationAdapter.OnConversationClickListener {

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.progress_bar)
    ProgressBar progressBar;
    @Bind(R.id.tv_no_result)
    TextView tvNoResultFound;

    private ConversationAdapter mAdapter;
    private boolean mIsFirstLoad;
    private Callback<HohodaResponse<List<Conversation>>> mCallback = new Callback<HohodaResponse<List<Conversation>>>() {
        @Override
        public void onResponse(Call<HohodaResponse<List<Conversation>>> call, Response<HohodaResponse<List<Conversation>>> response) {
            mAdapter.finishLoadMore();
            swipeRefreshLayout.setRefreshing(false);
            if (progressBar.getVisibility() == View.VISIBLE) {
                progressBar.setVisibility(View.GONE);
            }

            if (response.isSuccess()) {
                String status = response.body().getStatus();
                if ("0".equals(status)) {
                    processReturnedData(response.body().getBody());
                } else {
                    ErrorManager.handleApplicationException(getActivity(), response);
                }
            } else {
                ErrorManager.handleErroneousException(getActivity(), response);
            }
        }

        @Override
        public void onFailure(Call<HohodaResponse<List<Conversation>>> call, Throwable t) {
            swipeRefreshLayout.setRefreshing(false);
            mAdapter.finishLoadMore();
            if (progressBar.getVisibility() == View.VISIBLE) {
                progressBar.setVisibility(View.GONE);
            }

            ErrorManager.handleNetworkException(getActivity());
        }
    };

    private void processReturnedData(List<Conversation> data) {
        if (mIsFirstLoad) {
            mAdapter.clear(true);
            if (data.size() == 0) {
                tvNoResultFound.setVisibility(View.VISIBLE);
            } else {
                tvNoResultFound.setVisibility(View.GONE);
                mAdapter.setData(data, true);
            }
        } else {
            mAdapter.addData(data);
        }
        if (data.isEmpty()) {
            mAdapter.setLoadMoreEnabled(false);
        }
    }

    public ConversationListFragment() {
    }

    @SuppressWarnings("unused")
    public static ConversationListFragment newInstance() {
        ConversationListFragment fragment = new ConversationListFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_item, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        mAdapter = new ConversationAdapter(null, recyclerView);
        mAdapter.setOnConversationClickListener(this);
        mAdapter.setLoadMoreEnabled(true);
        mAdapter.setLoadMoreListener(new BaseLoadMoreAdapter.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        makeGetConversationStudentRequest(false);
                    }
                });
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.setLoadMoreEnabled(true);
                Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        makeGetConversationStudentRequest(true);
                    }
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        makeGetConversationStudentRequest(true);
    }

    /**
     * Get course that user is a totor
     *
     * @param reload true if reload or first load
     */
    private void makeGetConversationStudentRequest(boolean reload) {
        long timeNext;
        mIsFirstLoad = reload;

        if (reload) {
            // reload == true: first load or refreshing
            timeNext = System.currentTimeMillis();
        } else {
            // load more
            timeNext = mAdapter.getLastItem().getCreatedAt();
        }

        if (mAdapter.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
        }


        Call<HohodaResponse<List<Conversation>>> call = ChatApi.getInstance().getAllConversations(timeNext);
        call.enqueue(mCallback);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onConversationClick(Conversation conversation) {
        String toUserId;
        if (conversation.getMemberOne().equals(AppReferences.getUserID())) {
            toUserId = conversation.getPersonTwo().getUserID();
        } else {
            toUserId = conversation.getPersonOne().getUserID();
        }
        Intent intent = ChatActivity.getIntent(getActivity(), toUserId);
        startActivity(intent);
    }
}
