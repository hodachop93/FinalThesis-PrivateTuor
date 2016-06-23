package com.hodachop93.hohoda.api.chat;

import com.hodachop93.hohoda.api.ApiClient;
import com.hodachop93.hohoda.model.Conversation;
import com.hodachop93.hohoda.model.HohodaResponse;
import com.hodachop93.hohoda.model.Message;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by hopho on 04/05/2016.
 */
public class ChatApi {
    private static ChatApiInterface apiInterface;

    public static ChatApiInterface getInstance(){
        if (apiInterface == null){
            apiInterface = ApiClient.getRetrofit().create(ChatApiInterface.class);
        }
        return apiInterface;
    }

    public interface ChatApiInterface{
        @FormUrlEncoded
        @POST("secure/message/startChat")
        Call<HohodaResponse<Conversation>> startChat(@Field("to_id") String toUserId);

        @FormUrlEncoded
        @POST("secure/message/getAllMessages")
        Call<HohodaResponse<List<Message>>> getAllMessages(@Field("conversation_id") String conversationId,
                                                           @Field("time_next") long timeNext);

        @FormUrlEncoded
        @POST("secure/message/addNew")
        Call<HohodaResponse<Message>> addNewMessage(@Field("conversation_id") String conversationId,
                                                   @Field("content") String content,
                                                   @Field("created_at") long createdAt);

        @FormUrlEncoded
        @POST("secure/message/getAllConversations")
        Call<HohodaResponse<List<Conversation>>> getAllConversations(@Field("time_next") long timeNext);
    }
}
