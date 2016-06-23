package com.hodachop93.hohoda.adapter;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;

import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.fragment.CandidateListFragment;
import com.hodachop93.hohoda.fragment.CourseDetailFragment;

public class CourseDetailPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;

    public CourseDetailPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return CourseDetailFragment.newInstance();
            case 1:
                return CandidateListFragment.newInstance();
            default:
                return null;
        }

    }

    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.course_detail_page_title);
            case 1:
                return mContext.getString(R.string.candidate_list_page_title);
            default:
                return null;
        }
    }
}

