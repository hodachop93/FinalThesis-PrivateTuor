package com.hodachop93.hohoda.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;

import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.fragment.BrowseCourseInterestFragment;
import com.hodachop93.hohoda.fragment.BrowseCourseLatestFragment;
import com.hodachop93.hohoda.fragment.BrowseCourseLocalFragment;


/**
 * Created by hopho on 20/04/2016.
 */
public class BrowseCoursePagerAdapter extends FragmentPagerAdapter {
    private Context mContext;

    public BrowseCoursePagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // My interest
                return BrowseCourseInterestFragment.newInstance();

            case 1: // Latest course
                return BrowseCourseLatestFragment.newInstance();

            case 2: // Local course
                return BrowseCourseLocalFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0: // My interest
                return mContext.getString(R.string.page_title_my_interest);

            case 1: // Latest course
                return mContext.getString(R.string.page_title_latest_course);

            case 2: // Local course
                return mContext.getString(R.string.page_title_local_course);

            default:
                return null;
        }
    }
}
