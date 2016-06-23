package com.hodachop93.hohoda.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;

import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.fragment.MyCourseJoinedFragment;
import com.hodachop93.hohoda.fragment.MyCoursePostedFragment;

/**
 * Created by Hop on 16/04/2016.
 */
public class MyCoursePagerAdapter extends FragmentPagerAdapter {
    private Context mContext;

    public MyCoursePagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // Tutor
                return MyCoursePostedFragment.newInstance();

            case 1: // Student
                return MyCourseJoinedFragment.newInstance();
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
            case 0: // Posted
                return mContext.getString(R.string.my_course_posted);

            case 1: // Joined
                return mContext.getString(R.string.my_course_joined);
            default:
                return null;
        }
    }
}
