package com.hodachop93.hohoda.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hopho on 04/05/2016.
 */
public class Message implements Parcelable{
    private String id;
    @SerializedName("conversation_id")
    private String conversationId;
    @SerializedName("user_id")
    private String userId;
    @SerializedName("created_at")
    private long createdAt;
    @SerializedName("updated_at")
    private long updated_at;
    private int status;
    private String content;

    public Message(String conversationId, String userId, String content, long createdAt) {
        this.conversationId = conversationId;
        this.userId = userId;
        this.content = content;
        this.createdAt = createdAt;
    }

    protected Message(Parcel in) {
        id = in.readString();
        conversationId = in.readString();
        userId = in.readString();
        createdAt = in.readLong();
        updated_at = in.readLong();
        status = in.readInt();
        content = in.readString();
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getConversationId() {
        return conversationId;
    }

    public String getUserId() {
        return userId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdated_at() {
        return updated_at;
    }

    public int getStatus() {
        return status;
    }

    public String getContent() {
        return content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(conversationId);
        dest.writeString(userId);
        dest.writeLong(createdAt);
        dest.writeLong(updated_at);
        dest.writeInt(status);
        dest.writeString(content);
    }
}
