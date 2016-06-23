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
import com.hodachop93.hohoda.activity.PersonProfileActivity;
import com.hodachop93.hohoda.adapter.BaseLoadMoreAdapter;
import com.hodachop93.hohoda.adapter.BrowseTutorAdapter;
import com.hodachop93.hohoda.api.profile.TutorRequestBody;
import com.hodachop93.hohoda.api.profile.ProfileApi;
import com.hodachop93.hohoda.exception.ErrorManager;
import com.hodachop93.hohoda.model.HohodaResponse;
import com.hodachop93.hohoda.model.Profile;
import com.hodachop93.hohoda.utils.ClickGuard;
import com.hodachop93.hohoda.utils.ProfileUtils;
import com.hodachop93.hohoda.view.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BrowseTutorLocalFragment extends BaseFragment implements BrowseTutorAdapter.OnProfileClickListener {

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.progress_bar)
    ProgressBar progressBar;
    @Bind(R.id.tv_no_result)
    TextView tvNoResultFound;

    private BrowseTutorAdapter mAdapter;
    private boolean mIsFirstLoad = false;
    private long mLastClickTime = 0;

    private Callback<HohodaResponse<List<Profile>>> mCallback = new Callback<HohodaResponse<List<Profile>>>() {
        @Override
        public void onResponse(Call<HohodaResponse<List<Profile>>> call, Response<HohodaResponse<List<Profile>>> response) {
            swipeRefreshLayout.setRefreshing(false);
            mAdapter.finishLoadMore();
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
        public void onFailure(Call<HohodaResponse<List<Profile>>> call, Throwable t) {
            swipeRefreshLayout.setRefreshing(false);
            mAdapter.finishLoadMore();
            if (progressBar.getVisibility() == View.VISIBLE) {
                progressBar.setVisibility(View.GONE);
            }

            ErrorManager.handleNetworkException(getActivity());
        }
    };

    private void processReturnedData(List<Profile> data) {
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

    public BrowseTutorLocalFragment() {
    }

    @SuppressWarnings("unused")
    public static BrowseTutorLocalFragment newInstance() {
        BrowseTutorLocalFragment fragment = new BrowseTutorLocalFragment();
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

        mAdapter = new BrowseTutorAdapter(null, recyclerView);
        mAdapter.setOnProfileClickListener(this);
        mAdapter.setLoadMoreEnabled(true);
        mAdapter.setLoadMoreListener(new BaseLoadMoreAdapter.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        makeGetLocalTutor(false);
                    }
                });
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (ProfileUtils.getCurrentProfile().getAddress() != null &&
                                !ProfileUtils.getCurrentProfile().getAddress().isEmpty()) {
                            makeGetLocalTutor(true);
                        }

                    }
                });
            }
        });
        if (ProfileUtils.getCurrentProfile()!= null && ProfileUtils.getCurrentProfile().getAddress() != null &&
                !ProfileUtils.getCurrentProfile().getAddress().isEmpty()) {
            makeGetLocalTutor(true);
            tvNoResultFound.setVisibility(View.GONE);
            tvNoResultFound.setText(getString(R.string.no_results_found));
        } else {
            tvNoResultFound.setVisibility(View.VISIBLE);
            tvNoResultFound.setText(getString(R.string.local_tutor_not_available));
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * Get tutor by user's interest
     *
     * @param reload true if reload or first load
     */
    private void makeGetLocalTutor(boolean reload) {
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

        TutorRequestBody body = new TutorRequestBody();
        body.setTimeNext(timeNext);

        Call<HohodaResponse<List<Profile>>> call = ProfileApi.getInstance().getLocalTutor(body);
        call.enqueue(mCallback);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @Override
    public void onProfileClick(Profile profile) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastClickTime < ClickGuard.DEFAULT_WATCH_PERIOD_MILLIS) {
            mLastClickTime = currentTime;
            return;
        }
        mLastClickTime = currentTime;

        Intent intent = PersonProfileActivity.getIntent(getActivity(), profile.getUserID());
        startActivity(intent);
    }

    @Override
    public void onMessageClick(Profile profile) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastClickTime < ClickGuard.DEFAULT_WATCH_PERIOD_MILLIS) {
            mLastClickTime = currentTime;
            return;
        }
        mLastClickTime = currentTime;

        Intent intent = ChatActivity.getIntent(getActivity(), profile.getUserID());
        startActivity(intent);
    }

}
