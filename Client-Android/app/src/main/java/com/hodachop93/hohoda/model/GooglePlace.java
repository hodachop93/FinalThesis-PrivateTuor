package com.hodachop93.hohoda.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;
import com.google.maps.android.clustering.ClusterItem;


public class GooglePlace implements ClusterItem, Parcelable {

    public static final Creator<GooglePlace> CREATOR = new Creator<GooglePlace>() {
        @Override
        public GooglePlace createFromParcel(Parcel in) {
            return new GooglePlace(in);
        }

        @Override
        public GooglePlace[] newArray(int size) {
            return new GooglePlace[size];
        }
    };
    @SerializedName("place_id")
    private String placeId;
    @SerializedName("description")
    private String placeName;
    private LatLng position;

    public GooglePlace(String placeName) {
        this.placeName = placeName;
    }
    protected GooglePlace(Parcel in) {
        placeName = in.readString();
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    @Override
    public String toString() {
        return placeName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(placeName);
    }
}
