package com.hodachop93.hohoda.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.adapter.CourseDetailPagerAdapter;
import com.hodachop93.hohoda.api.course.CourseApi;
import com.hodachop93.hohoda.api.course.CourseDetailRequestBody;
import com.hodachop93.hohoda.api.course.JoinCourseRequestBody;
import com.hodachop93.hohoda.common.AppReferences;
import com.hodachop93.hohoda.eventbus.CourseDetailEventBus;
import com.hodachop93.hohoda.eventbus.UpdateCandidateEventBus;
import com.hodachop93.hohoda.exception.ErrorManager;
import com.hodachop93.hohoda.model.Course;
import com.hodachop93.hohoda.model.HohodaResponse;
import com.hodachop93.hohoda.utils.Utils;
import com.hodachop93.hohoda.view.CircleImageView;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseDetailActivity extends BaseActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.cimv_avatar)
    CircleImageView imvAvatar;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_course_status)
    TextView tvCourseStatus;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_hash_tags)
    TextView tvHashtags;
    @Bind(R.id.tab_layout)
    TabLayout tabLayout;
    @Bind(R.id.view_pager)
    ViewPager viewPager;
    @Bind(R.id.btn_join)
    Button btnJoin;

    private ProgressDialog progressDialog;
    private Course mCourse;

    private Callback<HohodaResponse<Course>> mCallbackGetCourseDetail = new Callback<HohodaResponse<Course>>() {
        @Override
        public void onResponse(Call<HohodaResponse<Course>> call, Response<HohodaResponse<Course>> response) {
            if (progressDialog != null)
                progressDialog.dismiss();

            if (response.isSuccess()) {
                String status = response.body().getStatus();
                if ("0".equals(status)) {
                    Course course = response.body().getBody();
                    mCourse = course;
                    fillData(course);
                    CourseDetailEventBus.getInstance().post(new CourseDetailEventBus.Event(course));
                } else {
                    ErrorManager.handleApplicationException(CourseDetailActivity.this, response);
                }
            } else {
                ErrorManager.handleErroneousException(CourseDetailActivity.this, response);
            }
        }

        @Override
        public void onFailure(Call<HohodaResponse<Course>> call, Throwable t) {
            if (progressDialog != null)
                progressDialog.dismiss();

            ErrorManager.handleNetworkException(CourseDetailActivity.this);
        }
    };

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, CourseDetailActivity.class);
        return intent;
    }

    public static Intent getIntent(Context context, String courseId) {
        Intent intent = new Intent(context, CourseDetailActivity.class);
        intent.putExtra("COURSE_ID", courseId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.course_detail_activity);

        viewPager.setAdapter(new CourseDetailPagerAdapter(this, getFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

        String courseId = this.getIntent().getStringExtra("COURSE_ID");
        makeCourseDetailRequest(courseId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fillData(Course course) {
        Picasso.with(this).load(course.getProfile().getAvatarUrl())
                .placeholder(ContextCompat.getDrawable(this, R.drawable.ic_user_default))
                .error(ContextCompat.getDrawable(this, R.drawable.ic_user_default))
                .into(imvAvatar);
        tvName.setText(course.getProfile().getName());
        tvCourseStatus.setText(course.getStatusString());
        Utils.changeBackgroundCourseStatus(this, tvCourseStatus, course.getStatusInt());
        tvTitle.setText(course.getTitle());
        Utils.fillHashtagList(tvHashtags, course.getHashtags(), ContextCompat.getColor(this, R.color.white));
        if (course.getOwnerId().equals(AppReferences.getUserID())) {
            if (course.getStatusInt() == Course.COURSE_OPENED) {
                btnJoin.setText(R.string.label_close_course);
            } else if (course.getStatusInt() == Course.COURSE_CLOSED) {
                btnJoin.setVisibility(View.GONE);
            }
        } else {
            if (course.isJoined()) {
                btnJoin.setText(getString(R.string.label_cancel));
            } else {
                btnJoin.setText(getString(R.string.label_join));
            }
        }

    }

    private void makeCourseDetailRequest(String courseId) {
        progressDialog = ProgressDialog.show(this, null, getString(R.string.loading));
        CourseDetailRequestBody body = new CourseDetailRequestBody();
        body.setCourseId(courseId);
        Call<HohodaResponse<Course>> call = CourseApi.getInstance().getCourseDetail(body);
        call.enqueue(mCallbackGetCourseDetail);
    }

    @SuppressWarnings("unused")
    @OnClick({R.id.cimv_avatar, R.id.tv_name})
    public void seePersonProfile() {
        if (mCourse != null) {
            Intent intent = PersonProfileActivity.getIntent(this, mCourse.getOwnerId());
            startActivity(intent);
        }
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.btn_join)
    public void onClickButton() {
        if (mCourse.getOwnerId().equals(AppReferences.getUserID())) {
            makeCloseCourseRequest();
        } else {
            if (mCourse.isJoined()) {
                // un-join
                makeUnJoinCourseRequest();
            } else {
                // join
                makeJoinCourseRequest();
            }
        }
    }

    private void makeCloseCourseRequest() {
        Callback<HohodaResponse<Object>> callback = new Callback<HohodaResponse<Object>>() {
            @Override
            public void onResponse(Call<HohodaResponse<Object>> call, Response<HohodaResponse<Object>> response) {
                progressDialog.dismiss();
                if (response.isSuccess()) {
                    String status = response.body().getStatus();
                    if ("0".equals(status)) {
                        mCourse.setCourseStatus(Course.COURSE_CLOSED);
                        tvCourseStatus.setText(mCourse.getStatusString());
                        Utils.changeBackgroundCourseStatus(CourseDetailActivity.this, tvCourseStatus, mCourse.getStatusInt());
                        btnJoin.setVisibility(View.GONE);
                        Toast.makeText(CourseDetailActivity.this, R.string.close_course_successfully, Toast.LENGTH_SHORT).show();
                    } else {
                        ErrorManager.handleApplicationException(CourseDetailActivity.this, response);
                    }
                } else {
                    ErrorManager.handleErroneousException(CourseDetailActivity.this, response);
                }
            }

            @Override
            public void onFailure(Call<HohodaResponse<Object>> call, Throwable t) {
                progressDialog.dismiss();
                ErrorManager.handleNetworkException(CourseDetailActivity.this);
            }
        };

        progressDialog.setMessage(getString(R.string.closing_course));
        progressDialog.show();

        Call<HohodaResponse<Object>> call = CourseApi.getInstance().closeCourse(mCourse.getId());
        call.enqueue(callback);
    }

    private void makeUnJoinCourseRequest() {
        Callback<HohodaResponse<Object>> callback = new Callback<HohodaResponse<Object>>() {
            @Override
            public void onResponse(Call<HohodaResponse<Object>> call, Response<HohodaResponse<Object>> response) {
                progressDialog.dismiss();
                if (response.isSuccess()) {
                    String status = response.body().getStatus();
                    if ("0".equals(status)) {
                        btnJoin.setText(R.string.label_join);
                        mCourse.setJoined(false);
                        Toast.makeText(CourseDetailActivity.this, R.string.unjoin_course_successfully, Toast.LENGTH_SHORT).show();
                        UpdateCandidateEventBus.getInstance().postSticky(new UpdateCandidateEventBus.Event());
                    } else {
                        ErrorManager.handleApplicationException(CourseDetailActivity.this, response);
                    }
                } else {
                    ErrorManager.handleErroneousException(CourseDetailActivity.this, response);
                }
            }

            @Override
            public void onFailure(Call<HohodaResponse<Object>> call, Throwable t) {
                progressDialog.dismiss();
                ErrorManager.handleNetworkException(CourseDetailActivity.this);
            }
        };

        progressDialog.setMessage(getString(R.string.unjoining_course));
        progressDialog.show();

        Call<HohodaResponse<Object>> call = CourseApi.getInstance().unJoinCourse(mCourse.getId());
        call.enqueue(callback);
    }

    private void makeJoinCourseRequest() {
        Callback<HohodaResponse<Object>> callback = new Callback<HohodaResponse<Object>>() {
            @Override
            public void onResponse(Call<HohodaResponse<Object>> call, Response<HohodaResponse<Object>> response) {
                progressDialog.dismiss();
                if (response.isSuccess()) {
                    String status = response.body().getStatus();
                    if ("0".equals(status)) {
                        btnJoin.setText(R.string.label_cancel);
                        mCourse.setJoined(true);
                        Toast.makeText(CourseDetailActivity.this, R.string.join_course_successfully, Toast.LENGTH_SHORT).show();
                        UpdateCandidateEventBus.getInstance().postSticky(new UpdateCandidateEventBus.Event());
                    } else {
                        ErrorManager.handleApplicationException(CourseDetailActivity.this, response);
                    }
                } else {
                    ErrorManager.handleErroneousException(CourseDetailActivity.this, response);
                }
            }

            @Override
            public void onFailure(Call<HohodaResponse<Object>> call, Throwable t) {
                progressDialog.dismiss();
                ErrorManager.handleNetworkException(CourseDetailActivity.this);
            }
        };

        progressDialog.setMessage(getString(R.string.label_joining));
        progressDialog.show();

        JoinCourseRequestBody body = new JoinCourseRequestBody();
        body.setCourseId(mCourse.getId());
        Call<HohodaResponse<Object>> call = CourseApi.getInstance().joinCourse(body);
        call.enqueue(callback);
    }
}
