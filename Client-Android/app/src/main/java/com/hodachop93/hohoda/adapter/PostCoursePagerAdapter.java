package com.hodachop93.hohoda.adapter;


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.hodachop93.hohoda.fragment.PostCoursePageOne;
import com.hodachop93.hohoda.fragment.PostCoursePageThree;
import com.hodachop93.hohoda.fragment.PostCoursePageTwo;
import com.hodachop93.hohoda.model.GooglePlace;
import com.hodachop93.hohoda.model.HashTag;

import java.util.List;

/**
 * Created by Hop on 29/03/2016.
 */
public class PostCoursePagerAdapter extends FragmentStatePagerAdapter {
    private PostCoursePageOne pageOne;
    private PostCoursePageTwo pageTwo;
    private PostCoursePageThree pageThree;

    private HashTag mHashTag;

    public PostCoursePagerAdapter(FragmentManager fm, HashTag hashTag) {
        super(fm);
        this.mHashTag = hashTag;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (pageOne == null)
                    pageOne = PostCoursePageOne.newInstance(mHashTag);
                return pageOne;
            case 1:
                if (pageTwo == null)
                    pageTwo = PostCoursePageTwo.newInstance();
                return pageTwo;
            case 2:
                if (pageThree == null)
                    pageThree = PostCoursePageThree.newInstance();
                return pageThree;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    public String getTitle() {
        return pageOne.getTitle();
    }

    public List<String> getHashTags() {
        return pageOne.getHashTags();
    }

    public String getPrice() {
        return pageTwo.getPrice();
    }

    public int getCourseType() {
        return pageTwo.getCourseType();
    }

    public String getDuration() {
        return pageTwo.getDuration();
    }

    public long getStartDate() {
        return pageTwo.getStartDate();
    }

    public String getSchedule() {
        return pageTwo.getSchedule();
    }

    public String getAddress() {
        return pageThree.getAddress();
    }

    public String getDescription() {
        return pageThree.getDescription();
    }

    public GooglePlace getGooglePlaceAddress() {
        return pageThree.getGooglePlaceAddress();
    }
}
