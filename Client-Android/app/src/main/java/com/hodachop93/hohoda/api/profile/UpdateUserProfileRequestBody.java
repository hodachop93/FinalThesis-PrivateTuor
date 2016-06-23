package com.hodachop93.hohoda.api.profile;

import android.graphics.Bitmap;
import android.util.Base64;

import com.google.gson.annotations.SerializedName;
import com.hodachop93.hohoda.model.HashTag;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hopho on 27/04/2016.
 */
public class UpdateUserProfileRequestBody {
    private String name;
    private String phone;
    private String address;
    private String infor;
    private String age;
    private int gender;
    private String lat, lng;
    @SerializedName("interested_subject")
    private List<String> interestedSubjects;
    @SerializedName("image_string")
    private String imageString;

    public UpdateUserProfileRequestBody() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setInfor(String infor) {
        this.infor = infor;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public void setInterestedSubjects(List<HashTag> interestedSubjects) {
        List<String> hashtags = new ArrayList<>();
        for (HashTag item : interestedSubjects) {
            hashtags.add(item.getName());
        }
        this.interestedSubjects = hashtags;
    }

    public void setImageString(Bitmap avatar) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        avatar.compress(Bitmap.CompressFormat.PNG, 100, bos);

        this.imageString = Base64.encodeToString(bos.toByteArray(), Base64.NO_WRAP);
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
}

