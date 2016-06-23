package com.hodachop93.hohoda.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.activity.PostCourseActivity;
import com.hodachop93.hohoda.adapter.PopularHashTagAdapter;
import com.hodachop93.hohoda.api.hashtag.HashtagApi;
import com.hodachop93.hohoda.exception.ErrorManager;
import com.hodachop93.hohoda.model.HashTag;
import com.hodachop93.hohoda.model.HohodaResponse;
import com.hodachop93.hohoda.view.GridSpacingItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hopho on 19/04/2016.
 */
public class PostCourseFragment extends BaseFragment implements PopularHashTagAdapter.OnClickPopularHashTagListener {
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.progress_bar)
    ProgressBar progressBar;

    private List<HashTag> mPopularHashTagList;
    private PopularHashTagAdapter mPopularHashTagAdapter;

    private Callback<HohodaResponse<List<HashTag>>> mCallback = new Callback<HohodaResponse<List<HashTag>>>() {
        @Override
        public void onResponse(Call<HohodaResponse<List<HashTag>>> call, Response<HohodaResponse<List<HashTag>>> response) {
            progressBar.setVisibility(View.GONE);

            if (response.isSuccess()) {
                String status = response.body().getStatus();
                if ("0".equals(status)) {
                    mPopularHashTagList.addAll(response.body().getBody());
                    mPopularHashTagAdapter.notifyItemRangeInserted(0, mPopularHashTagAdapter.getItemCount());
                } else {
                    ErrorManager.handleApplicationException(getActivity(), response);
                }
            } else {
                ErrorManager.handleErroneousException(getActivity(), response);
            }
        }

        @Override
        public void onFailure(Call<HohodaResponse<List<HashTag>>> call, Throwable t) {
            progressBar.setVisibility(View.GONE);

            ErrorManager.handleNetworkException(getActivity());
        }
    };

    public PostCourseFragment() {
    }

    public static PostCourseFragment newInstance() {
        PostCourseFragment instance = new PostCourseFragment();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_course, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPopularHashTagList = new ArrayList<>();
        mPopularHashTagAdapter = new PopularHashTagAdapter(mPopularHashTagList, getActivity());
        mPopularHashTagAdapter.setListener(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(
                new GridSpacingItemDecoration(2,
                        getResources().getDimensionPixelOffset(R.dimen.popular_hashtag_spacing), true, 0));

        recyclerView.setAdapter(mPopularHashTagAdapter);

        Call<HohodaResponse<List<HashTag>>> call = HashtagApi.getInstance().getPopularHashtag();
        call.enqueue(mCallback);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClickPopularHashTag(HashTag hashTag) {
        Intent intent = PostCourseActivity.getIntent(getActivity(), hashTag);
        startActivity(intent);
    }
}
