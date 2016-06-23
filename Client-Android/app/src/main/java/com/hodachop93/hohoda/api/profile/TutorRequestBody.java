package com.hodachop93.hohoda.api.profile;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Hop Dac Ho on 01/05/2016.
 */
public class TutorRequestBody {
    @SerializedName("time_next")
    private long timeNext;

    public void setTimeNext(long timeNext) {
        this.timeNext = timeNext;
    }
}
