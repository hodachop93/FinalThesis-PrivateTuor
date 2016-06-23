package com.hodachop93.hohoda.api.course;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hopho on 25/04/2016.
 */
public class LocalCourseRequestBody {
    private String address;
    @SerializedName("time_next")
    private long timeNext;

    public void setAddress(String address) {
        this.address = address;
    }

    public void setTimeNext(long timeNext) {
        this.timeNext = timeNext;
    }
}
