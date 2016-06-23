package com.hodachop93.hohoda.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.adapter.BaseLoadMoreAdapter;
import com.hodachop93.hohoda.adapter.ProfileReviewAdapter;
import com.hodachop93.hohoda.api.profile.ProfileApi;
import com.hodachop93.hohoda.eventbus.ProfileEventBus;
import com.hodachop93.hohoda.exception.ErrorManager;
import com.hodachop93.hohoda.model.HohodaResponse;
import com.hodachop93.hohoda.model.Profile;
import com.hodachop93.hohoda.model.Review;
import com.hodachop93.hohoda.view.DividerItemDecoration;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileReviewsFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.tv_no_result)
    TextView tvNoResultFound;
    @Bind(R.id.progress_bar)
    ProgressBar progressBar;

    private ProfileReviewAdapter mAdapter;
    private boolean mIsFirstLoad;
    private String mUserId;

    private Callback<HohodaResponse<List<Review>>> mCallback = new Callback<HohodaResponse<List<Review>>>() {
        @Override
        public void onResponse(Call<HohodaResponse<List<Review>>> call, Response<HohodaResponse<List<Review>>> response) {
            if (mAdapter==null||swipeRefreshLayout==null || progressBar==null){
                return;
            }
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
        public void onFailure(Call<HohodaResponse<List<Review>>> call, Throwable t) {
            if (mAdapter==null||swipeRefreshLayout==null || progressBar==null){
                return;
            }
            swipeRefreshLayout.setRefreshing(false);
            mAdapter.finishLoadMore();
            if (progressBar.getVisibility() == View.VISIBLE) {
                progressBar.setVisibility(View.GONE);
            }

            ErrorManager.handleNetworkException(getActivity());
        }
    };


    public ProfileReviewsFragment() {
    }

    public static ProfileReviewsFragment newInstance() {
        return new ProfileReviewsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_item, container, false);
        ButterKnife.bind(this, view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        mAdapter = new ProfileReviewAdapter(null, recyclerView);
        mAdapter.setLoadMoreEnabled(true);
        mAdapter.setLoadMoreListener(new BaseLoadMoreAdapter.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        makeReviewRequest(false);
                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        ProfileEventBus.getInstance().register(this);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onUpdateProfileEventBus(ProfileEventBus.UpdateProfileEvent event) {
        Profile profile = event.getProfile();
        mUserId = profile.getUserID();
        makeReviewRequest(true);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onUpdatePersonProfileEventBus(ProfileEventBus.UpdatePersonProfileEvent event) {
        Profile profile = event.getProfile();
        mUserId = profile.getUserID();
        makeReviewRequest(true);
/*        if (ProfileEventBus.getInstance().isRegistered(this)){
            ProfileEventBus.getInstance().unregister(this);
        }*/
    }

    @Override
    public void onStop() {
        if (ProfileEventBus.getInstance().isRegistered(this)){
            ProfileEventBus.getInstance().unregister(this);
        }
        super.onStop();
    }


    private void makeReviewRequest(boolean reload) {
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

        Call<HohodaResponse<List<Review>>> call = ProfileApi.getInstance().getProfileReview(mUserId, timeNext);
        call.enqueue(mCallback);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onRefresh() {
        makeReviewRequest(true);
    }

    private void processReturnedData(List<Review> data) {
        if (mIsFirstLoad) {
            if (data.size() == 0) {
                tvNoResultFound.setVisibility(View.VISIBLE);
            } else {
                tvNoResultFound.setVisibility(View.GONE);
                mAdapter.clear(true);
                mAdapter.setData(data, true);
            }
        } else {
            mAdapter.addData(data);
            if (data.isEmpty()) {
                mAdapter.setLoadMoreEnabled(false);
            }
        }
    }
}
