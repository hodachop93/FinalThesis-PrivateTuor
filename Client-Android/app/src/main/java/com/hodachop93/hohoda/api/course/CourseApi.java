package com.hodachop93.hohoda.api.course;

import com.hodachop93.hohoda.api.ApiClient;
import com.hodachop93.hohoda.model.Candidate;
import com.hodachop93.hohoda.model.Course;
import com.hodachop93.hohoda.model.HohodaResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Hop on 27/03/2016.
 */
public class CourseApi {
    private static CourseApiInterface apiInterface;

    public static CourseApiInterface getInstance() {
        if (apiInterface == null) {
            apiInterface = ApiClient.getRetrofit().create(CourseApiInterface.class);
        }
        return apiInterface;
    }

    public interface CourseApiInterface {
        @POST("secure/course/post")
        Call<HohodaResponse<Object>> postCourse(@Body PostCourseRequestBody body);

        @POST("secure/course/join")
        Call<HohodaResponse<Object>> joinCourse(@Body JoinCourseRequestBody body);

        @FormUrlEncoded
        @POST("secure/course/unjoin")
        Call<HohodaResponse<Object>> unJoinCourse(@Field("course_id") String courseId);

        @FormUrlEncoded
        @POST("secure/course/close")
        Call<HohodaResponse<Object>> closeCourse(@Field("course_id") String courseId);

        @POST("secure/course/recommend")
        Call<HohodaResponse<List<Course>>> getRecommendedCourse(@Body CourseRequestBody body);

        @POST("secure/mycourse/tutor")
        Call<HohodaResponse<List<Course>>> getMyCoursePosted(@Body CourseRequestBody body);

        @POST("secure/mycourse/student")
        Call<HohodaResponse<List<Course>>> getMyCourseJoined(@Body CourseRequestBody body);

        @POST("secure/course/latest")
        Call<HohodaResponse<List<Course>>> getLatestCourse(@Body CourseRequestBody body);

        @POST("secure/course/local")
        Call<HohodaResponse<List<Course>>> getLocalCourse(@Body LocalCourseRequestBody body);

        @POST("secure/course/detail")
        Call<HohodaResponse<Course>> getCourseDetail(@Body CourseDetailRequestBody body);

        @FormUrlEncoded
        @POST("course/searchByHashtag")
        Call<HohodaResponse<List<Course>>> searchCoursesByHashtags(@Field("hashtags") List<String> hashtags,
                                                                   @Field("time_next") long timeNext);

        @FormUrlEncoded
        @POST("secure/course/getCandidateList")
        Call<HohodaResponse<List<Candidate>>> getCandidateList(@Field("course_id") String courseId);

    }
}
