package com.hodachop93.hohoda.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.activity.CourseDetailActivity;
import com.hodachop93.hohoda.activity.PersonProfileActivity;
import com.hodachop93.hohoda.adapter.BrowseCourseAdapter;
import com.hodachop93.hohoda.api.course.CourseApi;
import com.hodachop93.hohoda.api.course.CourseRequestBody;
import com.hodachop93.hohoda.exception.ErrorManager;
import com.hodachop93.hohoda.model.Course;
import com.hodachop93.hohoda.model.HohodaResponse;
import com.hodachop93.hohoda.utils.ClickGuard;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;


public class BrowseCourseLatestFragment extends BaseFragment implements BrowseCourseAdapter.OnCourseClickListener {

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.sticky_lv)
    StickyListHeadersListView stickyListView;
    @Bind(R.id.progress_bar)
    ProgressBar progressBar;
    @Bind(R.id.tv_no_result)
    TextView tvNoResultFound;

    private BrowseCourseAdapter mAdapter;
    private List<Course> mCourseList;
    private int mPrevLast = 0;
    private boolean mIsFirstLoad = false;
    private long mLastClickTime = 0;


    private Callback<HohodaResponse<List<Course>>> mCallback = new Callback<HohodaResponse<List<Course>>>() {
        @Override
        public void onResponse(Call<HohodaResponse<List<Course>>> call, Response<HohodaResponse<List<Course>>> response) {
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
        public void onFailure(Call<HohodaResponse<List<Course>>> call, Throwable t) {
            swipeRefreshLayout.setRefreshing(false);
            if (progressBar.getVisibility() == View.VISIBLE) {
                progressBar.setVisibility(View.GONE);
            }

            ErrorManager.handleNetworkException(getActivity());
        }
    };

    private void processReturnedData(List<Course> data) {
        if (mIsFirstLoad) {
            mCourseList.clear();
            mCourseList.addAll(data);
        } else {
            mCourseList.addAll(data);
        }
        mAdapter.notifyDataSetChanged();
        tvNoResultFound.setVisibility(mCourseList.size() > 0 ? View.GONE : View.VISIBLE);
    }

    public BrowseCourseLatestFragment() {
    }

    @SuppressWarnings("unused")
    public static BrowseCourseLatestFragment newInstance() {
        BrowseCourseLatestFragment fragment = new BrowseCourseLatestFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_item_sticky_header, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCourseList = new ArrayList<>();
        mAdapter = new BrowseCourseAdapter(mCourseList, getActivity());
        mAdapter.setOnCourseClickListener(this);
        stickyListView.setAdapter(mAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        makeGetCourseByInterest(true);
                    }
                });
            }
        });

        stickyListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                final int lastItem = firstVisibleItem + visibleItemCount;
                if (lastItem == totalItemCount) {
                    if (mPrevLast != lastItem) {
                        //Loading more data if possible
                        mPrevLast = lastItem;
                        makeGetCourseByInterest(false);
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        makeGetCourseByInterest(true);

    }

    /**
     * Get course by user/s interest
     *
     * @param reload true if reload or first load
     */
    private void makeGetCourseByInterest(boolean reload) {
        long timeNext;
        mIsFirstLoad = reload;

        if (reload) {
            // reload == true: first load or refreshing
            timeNext = System.currentTimeMillis();
        } else {
            // load more
            if (mCourseList.size() == 0)
                return;
            timeNext = mCourseList.get(mCourseList.size() - 1).getCreatedAt();
        }

        if (mAdapter.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
        }

        CourseRequestBody body = new CourseRequestBody();
        body.setTimeNext(timeNext);

        Call<HohodaResponse<List<Course>>> call = CourseApi.getInstance().getLatestCourse(body);
        call.enqueue(mCallback);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @Override
    public void onCourseClick(Course course) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastClickTime < ClickGuard.DEFAULT_WATCH_PERIOD_MILLIS) {
            mLastClickTime = currentTime;
            return;
        }
        mLastClickTime = currentTime;

        Intent intent = CourseDetailActivity.getIntent(getActivity(), course.getId());
        startActivity(intent);
    }

    @Override
    public void onProfileClick(String userId) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastClickTime < ClickGuard.DEFAULT_WATCH_PERIOD_MILLIS) {
            mLastClickTime = currentTime;
            return;
        }
        mLastClickTime = currentTime;

        Intent intent = PersonProfileActivity.getIntent(getActivity(), userId);
        startActivity(intent);
    }
}
