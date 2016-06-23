package com.hodachop93.hohoda.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.SearchView;

import com.hodachop93.hohoda.eventbus.LocationEventBus;
import com.hodachop93.hohoda.fragment.MapViewFragment;
import com.hodachop93.hohoda.utils.HashtagUtils;
import com.hodachop93.hohoda.utils.NotificationUtils;
import com.hodachop93.hohoda.utils.ProfileUtils;

public class HohodaActivity extends BaseNavigationDrawerActivity implements SearchView.OnQueryTextListener {

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, HohodaActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotificationUtils.saveDeviceToken();
        HashtagUtils.fetchPopularHashtag();
        switchToDashboardFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ProfileUtils.fetchProfile();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MapViewFragment.REQUEST_GOOGLE_PLAY_SERVICES) {
            if (resultCode == RESULT_OK) {
                LocationEventBus.getInstance().post(new LocationEventBus.Event());
            }
        }
    }
}
