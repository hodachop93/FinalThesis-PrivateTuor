package com.hodachop93.hohoda.activity;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.eventbus.PhotoIntentEventBus;
import com.hodachop93.hohoda.eventbus.ProfileEventBus;
import com.hodachop93.hohoda.fragment.ProfileReviewsFragment;
import com.hodachop93.hohoda.fragment.ProfileSummaryFragment;
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

public class UserProfileActivity extends BaseActivity {
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

    ProgressDialog progressDialog;

    private boolean mEditting = false;
    private long mLastClickTime = 0;
    private boolean mIsAvatarEdited;
    private Bitmap mAvatar;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, UserProfileActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ho Dac Hop");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewPager.setAdapter(new UserProfilePagerAdapter());
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
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(UserProfileActivity.this,
                            R.color.colorPrimary)));
                    tabLayout.setBackground(new ColorDrawable(ContextCompat.getColor(UserProfileActivity.this,
                            R.color.colorPrimary)));
                    appBarLayout.setBackground(new ColorDrawable(ContextCompat.getColor(UserProfileActivity.this,
                            R.color.colorPrimary)));
                    profileInfo.setAlpha(0);
                    tabLayout.setSelectedTabIndicatorColor(Color.WHITE);
                } else {
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    appBarLayout.setBackground(ContextCompat.getDrawable(UserProfileActivity.this, R.drawable.bg_default_user));
                    profileInfo.setAlpha(1);
                    tabLayout.setBackground(new ColorDrawable(Color.TRANSPARENT));
                    tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(UserProfileActivity.this,
                            R.color.colorPrimary));
                }

            }
        });
        makeGetUserProfileRequest();
    }



    private void makeGetUserProfileRequest() {
        progressDialog = ProgressDialog.show(this, null, getString(R.string.loading));
        ProfileUtils.fetchProfile();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
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
            case R.id.action_edit:
                mEditting = !mEditting;
                turnOnOffEditProfile(item);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void turnOnOffEditProfile(MenuItem item) {
        if (mEditting) {
            item.setIcon(R.drawable.ic_menu_save);
            enableEditProfile();
        } else {
            item.setIcon(R.drawable.ic_menu_edit);
            disableEditProfile();
        }
    }

    private void disableEditProfile() {
        ProfileEventBus.getInstance().post(new ProfileEventBus.EditProfileEvent(false, mAvatar));
    }

    private void enableEditProfile() {
        ProfileEventBus.getInstance().post(new ProfileEventBus.EditProfileEvent(true, null));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsAvatarEdited = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        ProfileEventBus.getInstance().register(this);
        PhotoIntentEventBus.getInstance().register(this);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onUpdateProfileEventBus(ProfileEventBus.UpdateProfileEvent event) {
        progressDialog.dismiss();
        Profile profile = event.getProfile();
        if (profile != null) {
            fillData(profile);
        }
    }

    @SuppressWarnings("unused")
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onPhotoIntentEventBus(PhotoIntentEventBus.Event event) {
        mAvatar = event.getPhoto();
        imvAvatar.setImageBitmap(mAvatar);
        mIsAvatarEdited = true;
        PhotoIntentEventBus.getInstance().removeStickyEvent(event);
    }

    @Override
    protected void onStop() {
        ProfileEventBus.getInstance().unregister(this);
        PhotoIntentEventBus.getInstance().unregister(this);
        super.onStop();
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

    @SuppressWarnings("unused")
    @OnClick(R.id.imv_avatar)
    public void changeAvatar() {
        if (mEditting) {
            Intent intent = new Intent(this, AddPhotoActivity.class);
            startActivity(intent);
        }
    }

    private class UserProfilePagerAdapter extends FragmentPagerAdapter {

        public UserProfilePagerAdapter() {
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
