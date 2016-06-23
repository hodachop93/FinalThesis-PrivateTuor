package com.hodachop93.hohoda.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;

import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.fragment.BrowseCourseLatestFragment;
import com.hodachop93.hohoda.fragment.BrowseTutorLocalFragment;

/**
 * Created by Hop Dac Ho on 23/05/2016.
 */
public class DashboardPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;

    public DashboardPagerAdapter( Context mContext, FragmentManager fm) {
        super(fm);
        this.mContext = mContext;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return BrowseCourseLatestFragment.newInstance();
            case 1:
                return BrowseTutorLocalFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return mContext.getString(R.string.page_title_latest_course_dashboard);
            case 1:
                return mContext.getString(R.string.page_title_local_tutor_dashboard);
            default:
                return null;
        }
    }
}
