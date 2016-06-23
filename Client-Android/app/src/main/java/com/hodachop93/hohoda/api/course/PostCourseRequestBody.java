package com.hodachop93.hohoda.api.course;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Hop on 27/03/2016.
 */
public class PostCourseRequestBody {
    @SerializedName("draft_id")
    private String draftId;
    private String title, price, description, address, city, country;
    private double lat, lng;
    private String duration;
    @SerializedName("course_type")
    private int courseType;
    @SerializedName("start_date")
    private long startDate;
    private List<String> hashtags;
    private String schedule;

    public void setDraftId(String draftId) {
        this.draftId = draftId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public void setHashtags(List<String> hashtags) {
        this.hashtags = hashtags;
    }

    public void setCourseType(int courseType) {
        this.courseType = courseType;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }
}
