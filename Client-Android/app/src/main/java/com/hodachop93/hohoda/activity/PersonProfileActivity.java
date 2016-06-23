package com.hodachop93.hohoda.activity;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.api.profile.GetProfileRequestBody;
import com.hodachop93.hohoda.api.profile.ProfileApi;
import com.hodachop93.hohoda.dialog.ReviewDialog;
import com.hodachop93.hohoda.eventbus.PhotoIntentEventBus;
import com.hodachop93.hohoda.eventbus.ProfileEventBus;
import com.hodachop93.hohoda.exception.ErrorManager;
import com.hodachop93.hohoda.fragment.ProfileReviewsFragment;
import com.hodachop93.hohoda.fragment.ProfileSummaryFragment;
import com.hodachop93.hohoda.model.HohodaResponse;
import com.hodachop93.hohoda.model.Profile;
import com.hodachop93.hohoda.utils.ClickGuard;
import com.hodachop93.hohoda.utils.ProfileUtils;
import com.hodachop93.hohoda.utils.Utils;
import com.hodachop93.hohoda.view.CircleImageView;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonProfileActivity extends BaseActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.imv_avatar)
    CircleImageView imvAvatar;
    @Bind(R.id.appbar)
    AppBarLayout appBarLayout;
    @Bind(R.id.view_pager)
    ViewPager viewPager;
    @Bind(R.id.tab_layout)
    TabLayout tabLayout;
    @Bind(R.id.profile_info)
    FrameLayout profileInfo;
    @Bind(R.id.tv_rate_average)
    TextView tvRateAvergage;
    @Bind(R.id.rating_bar)
    RatingBar ratingBar;
    @Bind(R.id.btn_review)
    Button btnReview;

    ProgressDialog progressDialog;

    private long mLastClickTime = 0;

    private Callback<HohodaResponse<Profile>> mCallbackGetPersonProfile = new Callback<HohodaResponse<Profile>>() {
        @Override
        public void onResponse(Call<HohodaResponse<Profile>> call, Response<HohodaResponse<Profile>> response) {
            progressDialog.dismiss();

            if (response.isSuccess()) {
                String status = response.body().getStatus();
                if ("0".equals(status)) {
                    fillData(response.body().getBody());
                    ProfileEventBus.getInstance().post(new ProfileEventBus.UpdatePersonProfileEvent(response.body().getBody()));
                } else {
                    ErrorManager.handleApplicationException(PersonProfileActivity.this, response);
                }
            } else {
                ErrorManager.handleErroneousException(PersonProfileActivity.this, response);
            }
        }

        @Override
        public void onFailure(Call<HohodaResponse<Profile>> call, Throwable t) {
            progressDialog.dismiss();
            ErrorManager.handleNetworkException(PersonProfileActivity.this);
        }
    };
    private String mUserId;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, PersonProfileActivity.class);
        return intent;
    }

    public static Intent getIntent(Context context, String userId) {
        Intent intent = new Intent(context, PersonProfileActivity.class);
        intent.putExtra("USER_ID", userId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ButterKnife.bind(this);
        btnReview.setVisibility(View.VISIBLE);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ho Dac Hop");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewPager.setAdapter(new PersonProfilePagerAdapter());
        tabLayout.setupWithViewPager(viewPager);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }

                float alpha = (float) (scrollRange + verticalOffset) / scrollRange;
                if (alpha <= 0.3) {
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(PersonProfileActivity.this,
                            R.color.colorPrimary)));
                    tabLayout.setBackground(new ColorDrawable(ContextCompat.getColor(PersonProfileActivity.this,
                            R.color.colorPrimary)));
                    appBarLayout.setBackground(new ColorDrawable(ContextCompat.getColor(PersonProfileActivity.this,
                            R.color.colorPrimary)));
                    profileInfo.setAlpha(0);
                    tabLayout.setSelectedTabIndicatorColor(Color.WHITE);
                } else {
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    appBarLayout.setBackground(ContextCompat.getDrawable(PersonProfileActivity.this, R.drawable.bg_default_user));
                    profileInfo.setAlpha(1);
                    tabLayout.setBackground(new ColorDrawable(Color.TRANSPARENT));
                    tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(PersonProfileActivity.this,
                            R.color.colorPrimary));
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUserId = getIntent().getStringExtra("USER_ID");
        makeGetPersonProfileRequest(mUserId);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ProfileEventBus.getInstance().register(this);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onReloadProfile(ProfileEventBus.ReloadProfileEvent event) {
        makeGetPersonProfileRequest(mUserId);
    }

    @Override
    protected void onStop() {
        ProfileEventBus.getInstance().unregister(this);
        super.onStop();
    }

    private void makeGetPersonProfileRequest(String userId) {
        progressDialog = ProgressDialog.show(this, null, getString(R.string.loading));
        GetProfileRequestBody body = new GetProfileRequestBody();
        body.setUserId(userId);
        Call<HohodaResponse<Profile>> call = ProfileApi.getInstance().getUserProfile(body);
        call.enqueue(mCallbackGetPersonProfile);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_person_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastClickTime < ClickGuard.DEFAULT_WATCH_PERIOD_MILLIS) {
            mLastClickTime = currentTime;
            return super.onOptionsItemSelected(item);
        }
        mLastClickTime = currentTime;

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_chat:
                finish();
                Intent intent = ChatActivity.getIntent(this, mUserId);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btn_review)
    public void addReview() {
        ReviewDialog dialog = ReviewDialog.newInstance(mUserId);
        dialog.show(getFragmentManager(), null);
    }

    public void setRateAverage(Profile profile) {
        String result = "";
        result += profile.getRateAverage() + " - ";
        result += profile.getTotalReview() + " " + (profile.getTotalReview() > 1 ? "rates" : "rate");
        tvRateAvergage.setText(result);
    }

    private void fillData(Profile profile) {
        toolbar.setTitle(profile.getName());
        Picasso.with(this).load(Utils.convertUrlIfUsingLocalhost(profile.getAvatarUrl()))
                .placeholder(ContextCompat.getDrawable(this, R.drawable.ic_user_default))
                .error(ContextCompat.getDrawable(this, R.drawable.ic_user_default))
                .into(imvAvatar);
        setRateAverage(profile);
        ratingBar.setRating(profile.getRateAverage());
    }

    private class PersonProfilePagerAdapter extends FragmentPagerAdapter {

        public PersonProfilePagerAdapter() {
            super(getFragmentManager());
        }

        @Override
        public Fragment getItem(int position) {
            return position == 0 ? ProfileSummaryFragment.newInstance() : ProfileReviewsFragment.newInstance();
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return position == 0 ? getString(R.string.page_title_tab_summary) : getString(R.string.page_title_tab_reviews);
        }
    }
}
