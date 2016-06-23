package com.hodachop93.hohoda.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;

import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.fragment.BrowseTutorByInterestFragment;
import com.hodachop93.hohoda.fragment.BrowseTutorLocalFragment;


/**
 * Created by hopho on 20/04/2016.
 */
public class BrowseTutorPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;

    public BrowseTutorPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // My interest
                return BrowseTutorByInterestFragment.newInstance();

            case 1: // Local course
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
        switch (position) {
            case 0: // My interest
                return mContext.getString(R.string.page_title_my_interest);

            case 1: // Local course
                return mContext.getString(R.string.page_title_local_course);

            default:
                return null;
        }
    }
}
