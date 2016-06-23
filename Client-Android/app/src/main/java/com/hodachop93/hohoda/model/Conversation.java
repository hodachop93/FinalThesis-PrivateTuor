package com.hodachop93.hohoda.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hopho on 04/05/2016.
 */
public class Conversation {
    private String id;
    @SerializedName("member_one")
    private String memberOne;
    @SerializedName("member_two")
    private String memberTwo;
    @SerializedName("person_one")
    private Profile personOne;
    @SerializedName("person_two")
    private Profile personTwo;
    @SerializedName("created_at")
    private long createdAt;
    @SerializedName("updated_at")
    private long updatedAt;
    @SerializedName("last_message")
    private String lastMessage;

    public String getId() {
        return id;
    }

    public String getMemberOne() {
        return memberOne;
    }

    public String getMemberTwo() {
        return memberTwo;
    }

    public Profile getPersonOne() {
        return personOne;
    }

    public Profile getPersonTwo() {
        return personTwo;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public String getLastMessage() {
        return lastMessage;
    }
}
