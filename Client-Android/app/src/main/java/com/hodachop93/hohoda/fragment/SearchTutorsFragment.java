package com.hodachop93.hohoda.fragment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.activity.ChatActivity;
import com.hodachop93.hohoda.activity.PersonProfileActivity;
import com.hodachop93.hohoda.adapter.BaseLoadMoreAdapter;
import com.hodachop93.hohoda.adapter.BrowseTutorAdapter;
import com.hodachop93.hohoda.api.profile.ProfileApi;
import com.hodachop93.hohoda.eventbus.SearchEventBus;
import com.hodachop93.hohoda.exception.ErrorManager;
import com.hodachop93.hohoda.model.HohodaResponse;
import com.hodachop93.hohoda.model.Profile;
import com.hodachop93.hohoda.utils.ClickGuard;
import com.hodachop93.hohoda.view.DividerItemDecoration;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchTutorsFragment extends BaseFragment implements BrowseTutorAdapter.OnProfileClickListener,
        FragmentManager.OnBackStackChangedListener {

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.progress_bar)
    ProgressBar progressBar;
    @Bind(R.id.tv_no_result)
    TextView tvNoResultFound;
    @Bind(R.id.fab)
    FloatingActionButton fab;

    private BrowseTutorAdapter mAdapter;
    private boolean mIsFirstLoad = false;
    private long mLastClickTime = 0;
    private MapViewFragment mMapViewFragment;

    private List<String> mHashtags;

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

    public SearchTutorsFragment() {
    }

    @SuppressWarnings("unused")
    public static SearchTutorsFragment newInstance() {
        SearchTutorsFragment fragment = new SearchTutorsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_item_with_fab, container, false);
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
                        makeSearchTutorsByHashtags(false);
                    }
                });
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                mAdapter.setLoadMoreEnabled(true);
                Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        makeSearchTutorsByHashtags(true);
                    }
                });
            }
        });
        ClickGuard.guard(fab);
    }

    @Override
    public void onStart() {
        super.onStart();
        SearchEventBus.getInstance().register(this);
    }

    @SuppressWarnings("unused")
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onSearchEvent(SearchEventBus.Event event) {
        mHashtags = event.getHashtags();
        SearchEventBus.getInstance().removeAllStickyEvents();
        makeSearchTutorsByHashtags(true);
    }

    @Override
    public void onStop() {
        SearchEventBus.getInstance().unregister(this);
        super.onStop();
    }

    private void makeSearchTutorsByHashtags(boolean reload) {
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

        Call<HohodaResponse<List<Profile>>> call = ProfileApi.getInstance().searchTutorsByHashtags(mHashtags, timeNext);
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
        Intent intent = ChatActivity.getIntent(getActivity(), profile.getUserID());
        startActivity(intent);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.fab)
    public void onClickFab(View fab) {
        if (getChildFragmentManager().findFragmentById(R.id.fl_container) instanceof MapViewFragment) {
            getChildFragmentManager().popBackStack();
            recyclerView.setVisibility(View.VISIBLE);
            return;
        }

        recyclerView.setVisibility(View.GONE);

        mMapViewFragment = MapViewFragment.newInstance();

        mMapViewFragment.setCoveredView(fab);

/*        if (googlePlace != null)
            mMapViewFragment.setCenterPosition(googlePlace.getPosition());*/

        List<Profile> tutors = mAdapter.getData();

        if (tutors != null && tutors.size() > 0) {
            mMapViewFragment.setData(tutors);
            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fl_container, mMapViewFragment);
            fragmentTransaction.addToBackStack(mMapViewFragment.getClass().getSimpleName());
            fragmentTransaction.commit();
        } else {
            Toast.makeText(getActivity(), "No results found for map view", Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onBackStackChanged() {
        if (getChildFragmentManager().findFragmentById(R.id.fl_container) instanceof MapViewFragment) {
            fab.setImageResource(R.drawable.ic_list_white);
        } else {
            fab.setImageResource(R.drawable.ic_map_white);
            fab.setVisibility(View.VISIBLE);
        }
    }
}
