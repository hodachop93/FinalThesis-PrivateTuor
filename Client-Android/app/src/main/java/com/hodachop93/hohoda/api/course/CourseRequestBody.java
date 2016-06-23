package com.hodachop93.hohoda.api.course;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Hop on 16/04/2016.
 */
public class CourseRequestBody {
	@SerializedName("time_next")
    private long timeNext;

    public void setTimeNext(long timeNext) {
        this.timeNext = timeNext;
    }
}
