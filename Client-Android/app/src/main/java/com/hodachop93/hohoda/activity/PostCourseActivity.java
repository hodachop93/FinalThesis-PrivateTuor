package com.hodachop93.hohoda.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.adapter.PostCoursePagerAdapter;
import com.hodachop93.hohoda.api.course.CourseApi;
import com.hodachop93.hohoda.api.course.PostCourseRequestBody;
import com.hodachop93.hohoda.api.googleplace.GooglePlaceApi;
import com.hodachop93.hohoda.exception.ErrorManager;
import com.hodachop93.hohoda.fragment.PostCoursePageOne;
import com.hodachop93.hohoda.fragment.PostCoursePageTwo;
import com.hodachop93.hohoda.model.HashTag;
import com.hodachop93.hohoda.model.HohodaResponse;
import com.hodachop93.hohoda.view.DisabledSwipeViewPager;
import com.hodachop93.hohoda.view.ProcessStepView;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostCourseActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.process_step)
    ProcessStepView processStep;
    @Bind(R.id.view_pager)
    DisabledSwipeViewPager viewPager;

    private int mCurrentPagePosition = 0;
    private PostCoursePagerAdapter mAdapter;
    private ProgressDialog progressDialog;

    private Callback<HohodaResponse<Object>> mCallback = new Callback<HohodaResponse<Object>>() {
        @Override
        public void onResponse(Call<HohodaResponse<Object>> call, Response<HohodaResponse<Object>> response) {
            if (progressDialog != null)
                progressDialog.dismiss();

            if (response.isSuccess()) {
                String status = response.body().getStatus();
                if ("0".equals(status)) {
                    Toast.makeText(PostCourseActivity.this, getString(R.string.toast_post_course_success), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    ErrorManager.handleApplicationException(PostCourseActivity.this, response);
                }
            } else {
                ErrorManager.handleErroneousException(PostCourseActivity.this, response);
            }
        }

        @Override
        public void onFailure(Call<HohodaResponse<Object>> call, Throwable t) {
            if (progressDialog != null)
                progressDialog.dismiss();

            ErrorManager.handleNetworkException(PostCourseActivity.this);
        }
    };

    private Callback<JsonObject> mCallbackGooglePlaceId = new Callback<JsonObject>() {
        @Override
        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
            if (response.isSuccess()) {
                JsonObject location = response.body().getAsJsonObject("result").getAsJsonObject("geometry").getAsJsonObject("location");
                double lat = location.getAsJsonPrimitive("lat").getAsDouble();
                double lng = location.getAsJsonPrimitive("lng").getAsDouble();
                mAdapter.getGooglePlaceAddress().setPosition(new LatLng(lat, lng));
            }
            makePostCourseRequest();
        }

        @Override
        public void onFailure(Call<JsonObject> call, Throwable t) {
            makePostCourseRequest();
        }
    };


    public static Intent getIntent(Context context, HashTag hashTag) {
        Intent intent = new Intent(context, PostCourseActivity.class);
        intent.putExtra("HASH_TAG", hashTag);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_course);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        HashTag hashTag = getIntent().getParcelableExtra("HASH_TAG");

        mAdapter = new PostCoursePagerAdapter(getFragmentManager(), hashTag);
        viewPager.setAdapter(mAdapter);
        processStep.setOnDoneListener(new ProcessStepView.OnDoneListener() {
            @Override
            public void done() {
                //get lat lng from google place id
                if (mAdapter.getGooglePlaceAddress() != null)
                    makeGetLatLngFromGooglePlaceId(mAdapter.getGooglePlaceAddress().getPlaceId());
                else
                    makePostCourseRequest();
            }
        });
    }

    private void makeGetLatLngFromGooglePlaceId(String placeId) {
        progressDialog = ProgressDialog.show(this, null, getString(R.string.posting_mess));
        String key = this.getString(R.string.google_server_key);
        Call<JsonObject> call = GooglePlaceApi.getInstance().getLatLngFromGooglePlaceId(placeId, key);
        call.enqueue(mCallbackGooglePlaceId);
    }

    private void makePostCourseRequest() {

        PostCourseRequestBody body = new PostCourseRequestBody();
        body.setTitle(mAdapter.getTitle());
        body.setHashtags(mAdapter.getHashTags());
        body.setPrice(mAdapter.getPrice());
        body.setCourseType(mAdapter.getCourseType());
        body.setDuration(mAdapter.getDuration());
        body.setSchedule(mAdapter.getSchedule());
        body.setAddress(mAdapter.getAddress());
        body.setDescription(mAdapter.getDescription());

        if (mAdapter.getStartDate() != -1)
            body.setStartDate(mAdapter.getStartDate());

        if (mAdapter.getGooglePlaceAddress() != null && mAdapter.getGooglePlaceAddress().getPosition() != null) {
            body.setLat(mAdapter.getGooglePlaceAddress().getPosition().latitude);
            body.setLng(mAdapter.getGooglePlaceAddress().getPosition().longitude);
        }

        Call<HohodaResponse<Object>> call = CourseApi.getInstance().postCourse(body);
        call.enqueue(mCallback);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post_course, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                comeBackToPrevious();
                break;
            case R.id.action_next:
                moveToNextStep();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void comeBackToPrevious() {
        int currentStep = processStep.getCurrentStep();
        if (currentStep == 1) {
            finish();
        } else {
            processStep.backToPreviousStep();
            viewPager.setCurrentItem(currentStep - 2);
        }
    }

    private void moveToNextStep() {
        int currentStep = processStep.getCurrentStep();
        boolean canNext = false;
        switch (currentStep) {
            case 1:
                PostCoursePageOne pageOne = (PostCoursePageOne) mAdapter.getItem(0);
                canNext = pageOne.checkConditionToNext();
                break;
            case 2:
                PostCoursePageTwo pageTwo = (PostCoursePageTwo) mAdapter.getItem(1);
                canNext = pageTwo.checkConditionToNext();
                break;
            case 3:
                canNext = true;
                break;
        }
        if (canNext) {
            processStep.nextStep();
            viewPager.setCurrentItem(currentStep);
        }
    }
}
