package com.hodachop93.hohoda.api.course;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hopho on 28/04/2016.
 */
public class CourseDetailRequestBody {
    @SerializedName("course_id")
    private String courseId;

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
}
