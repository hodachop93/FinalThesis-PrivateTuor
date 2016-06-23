package com.hodachop93.hohoda.adapter;


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.hodachop93.hohoda.fragment.InfoWindowFragment;
import com.hodachop93.hohoda.model.Profile;

import java.util.List;


public class InfoWindowPagerAdapter extends FragmentStatePagerAdapter {

    private List<Profile> mTutors;

    public InfoWindowPagerAdapter(FragmentManager fm, List<Profile> tutors) {
        super(fm);
        mTutors = tutors;
    }

    @Override
    public Fragment getItem(int position) {
        return InfoWindowFragment.newInstance(mTutors.get(position), position, getCount());
    }

    @Override
    public int getCount() {
        return mTutors.size();
    }

    public void setData(List<Profile> tutors) {
        mTutors = tutors;
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
