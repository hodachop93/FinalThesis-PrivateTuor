package com.hodachop93.hohoda.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
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
import com.hodachop93.hohoda.activity.ChatActivity;
import com.hodachop93.hohoda.activity.PersonProfileActivity;
import com.hodachop93.hohoda.adapter.CandidateAdapter;
import com.hodachop93.hohoda.api.course.CourseApi;
import com.hodachop93.hohoda.eventbus.CourseDetailEventBus;
import com.hodachop93.hohoda.eventbus.UpdateCandidateEventBus;
import com.hodachop93.hohoda.exception.ErrorManager;
import com.hodachop93.hohoda.model.Candidate;
import com.hodachop93.hohoda.model.HohodaResponse;
import com.hodachop93.hohoda.utils.ClickGuard;
import com.hodachop93.hohoda.view.DividerItemDecoration;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hopho on 20/04/2016.
 */
public class CandidateListFragment extends Fragment implements CandidateAdapter.OnCandidateClickListener {
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.tv_no_result)
    TextView tvNoResultFound;
    @Bind(R.id.progress_bar)
    ProgressBar progressBar;

    private CandidateAdapter mAdapter;
    private String mCourseId;
    private long mLastClickTime = 0;

    private Callback<HohodaResponse<List<Candidate>>> mCallback = new Callback<HohodaResponse<List<Candidate>>>() {
        @Override
        public void onResponse(Call<HohodaResponse<List<Candidate>>> call, Response<HohodaResponse<List<Candidate>>> response) {
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
        public void onFailure(Call<HohodaResponse<List<Candidate>>> call, Throwable t) {
            mAdapter.finishLoadMore();
            if (progressBar.getVisibility() == View.VISIBLE) {
                progressBar.setVisibility(View.GONE);
            }

            ErrorManager.handleNetworkException(getActivity());
        }
    };


    public CandidateListFragment() {
    }

    public static CandidateListFragment newInstance() {
        return new CandidateListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_item, container, false);
        ButterKnife.bind(this, view);

        swipeRefreshLayout.setEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        mAdapter = new CandidateAdapter(null, recyclerView);
        mAdapter.setLoadMoreEnabled(false);
        mAdapter.setOnCandidateClickListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        CourseDetailEventBus.getInstance().register(this);
        UpdateCandidateEventBus.getInstance().register(this);
    }


    @Override
    public void onStop() {
        super.onStop();
        CourseDetailEventBus.getInstance().unregister(this);
        UpdateCandidateEventBus.getInstance().unregister(this);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onCourseDetailEventBus(CourseDetailEventBus.Event event) {
        mCourseId = event.getCourse().getId();
        makeGetCandidateListRequest();
    }

    @SuppressWarnings("unused")
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onUpdateCandidateEventBus(UpdateCandidateEventBus.Event event) {
        UpdateCandidateEventBus.getInstance().removeAllStickyEvents();
        makeGetCandidateListRequest();
    }


    private void makeGetCandidateListRequest() {
        if (mAdapter.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
        }

        Call<HohodaResponse<List<Candidate>>> call = CourseApi.getInstance().getCandidateList(mCourseId);
        call.enqueue(mCallback);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void processReturnedData(List<Candidate> data) {
        if (data.size() == 0) {
            tvNoResultFound.setVisibility(View.VISIBLE);
            mAdapter.clear(true);
        } else {
            tvNoResultFound.setVisibility(View.GONE);
            mAdapter.clear(true);
            mAdapter.setData(data, true);
        }
    }


    @Override
    public void onProfileClick(Candidate candidate) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastClickTime < ClickGuard.DEFAULT_WATCH_PERIOD_MILLIS) {
            mLastClickTime = currentTime;
            return;
        }
        mLastClickTime = currentTime;

        Intent intent = PersonProfileActivity.getIntent(getActivity(), candidate.getUserId());
        startActivity(intent);
    }

    @Override
    public void onMessageClick(Candidate candidate) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastClickTime < ClickGuard.DEFAULT_WATCH_PERIOD_MILLIS) {
            mLastClickTime = currentTime;
            return;
        }
        mLastClickTime = currentTime;
        Intent intent = ChatActivity.getIntent(getActivity(), candidate.getUserId());
        startActivity(intent);
    }
}
