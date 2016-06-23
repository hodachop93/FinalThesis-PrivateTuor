package com.hodachop93.hohoda.api.course;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Hop on 27/03/2016.
 */
public class JoinCourseRequestBody {
    @SerializedName("course_id")
    private String courseId;

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
}
